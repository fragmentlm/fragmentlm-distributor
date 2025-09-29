package org.fragmentlm.distributor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotNull;
import org.fragmentlm.distributor.dto.PeerReply;
import org.fragmentlm.distributor.dto.ProcessedFragments;
import org.fragmentlm.distributor.dto.WeightedFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PeerFragmentDistributionService implements IPeerConnectionService
{
    private static final Logger logger = LoggerFactory.getLogger(PeerFragmentDistributionService.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final Duration requestTimeout;
    private final Retry retrySpec;

    public PeerFragmentDistributionService(@NotNull WebClient webClient, @NotNull ObjectMapper objectMapper, @NotNull Duration requestTimeout, @NotNull Retry retrySpec) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.requestTimeout = requestTimeout;
        this.retrySpec = retrySpec;
    }

    @Override
    public CompletableFuture<PeerReply> sendFragment(@NotNull URI url, @NotNull String requestJsonString) {
        return webClient.post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestJsonString)
            .retrieve()
            .bodyToMono(PeerReply.class)
            .timeout(requestTimeout)
            .retryWhen(retrySpec)
            .onErrorResume(throwable -> {
                int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
                if (throwable instanceof WebClientResponseException) {
                    status = ((WebClientResponseException) throwable).getStatusCode().value();
                }
                logger.warn("request to {} failed: {}", url, throwable.getMessage());
                return Mono.just(new PeerReply(status, ""));
            })
            .toFuture();
    }

    @Override
    public @NotNull CompletableFuture<ProcessedFragments> distributeRequest(@NotNull List<WeightedFragment> requests) {
        Flux<Map.Entry<String, Mono<PeerReply>>> entriesFlux = Flux.fromIterable(requests)
            .map(wf -> {
                URI url = buildUrl(wf.address(), "/fragmentlm/worker");
                if (url == null) {
                    return Map.entry("INVALID_URI:" + wf.address(), Mono.just(new PeerReply(HttpStatus.BAD_REQUEST.value(), "")));
                }
                ObjectNode jsonObj = objectMapper.createObjectNode().put("fragment", wf.fragment());
                String json = jsonObj.toString();
                return Map.entry(url.toString(), Mono.fromFuture(sendFragment(url, json)));
            });

        return entriesFlux
            .collectMap(Map.Entry::getKey, Map.Entry::getValue)
            .flatMap(map -> {
                List<Mono<Pair>> monos = map.entrySet().stream()
                    .map(e -> e.getValue()
                        .map(reply -> new Pair(e.getKey(), reply))
                        .onErrorResume(ex -> {
                            logger.warn("error collecting reply for {}: {}", e.getKey(), ex.getMessage());
                            return Mono.just(new Pair(e.getKey(), new PeerReply(HttpStatus.INTERNAL_SERVER_ERROR.value(), "")));
                        }))
                    .toList();

                return Flux.mergeSequential(monos)
                    .collectMap(pair -> pair.key, pair -> pair.reply);
            })
            .map(resultMap -> {
                Map<String, PeerReply> clean = new ConcurrentHashMap<>();
                resultMap.forEach((k, v) -> {
                    if (!k.startsWith("INVALID_URI:")) {
                        clean.put(k, v);
                    }
                });
                return new ProcessedFragments(clean);
            })
            .toFuture();
    }

    private static @NotNull URI buildUrl(@NotNull String address, @NotNull String path) {
        try {
            return new URI("http", address, path, null);
        } catch (URISyntaxException e) {
            logger.warn("Invalid address for URL construction: {}", address);
            return null;
        }
    }

    private record Pair(String key, PeerReply reply)
    {
    }
}
