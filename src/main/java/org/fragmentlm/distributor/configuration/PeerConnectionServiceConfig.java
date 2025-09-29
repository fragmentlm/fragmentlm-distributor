package org.fragmentlm.distributor.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
public class PeerConnectionServiceConfig
{
    @Bean
    public @NotNull WebClient webClient ()
    {
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
            .build();
        return WebClient.builder()
            .exchangeStrategies(strategies)
            .build();
    }

    @Bean
    public ObjectMapper mapper ()
    {
        return new ObjectMapper();
    }

    @Bean
    public Duration retryDuration()
    {
        return Duration.ofSeconds(30);
    }

    @Bean
    public Retry retrySpec()
    {
        return Retry.backoff(2, Duration.ofMillis(200))
            .maxBackoff(Duration.ofSeconds(1))
            .filter(throwable -> {
                if (throwable instanceof WebClientResponseException responseException) {
                    var status = responseException.getStatusCode();
                    return status.is5xxServerError();
                }
                return true;
            });
    }
}
