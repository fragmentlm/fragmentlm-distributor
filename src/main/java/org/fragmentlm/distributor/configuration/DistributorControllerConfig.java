package org.fragmentlm.distributor.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import org.fragmentlm.distributor.service.IPeerConnectionService;
import org.fragmentlm.distributor.service.PeerFragmentDistributionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
public class DistributorControllerConfig
{
    @Bean
    public IPeerConnectionService service (@NotNull WebClient webClient, @NotNull ObjectMapper objectMapper, @NotNull Duration duration, @NotNull Retry retrySpec)
    {
        return new PeerFragmentDistributionService(webClient, objectMapper, duration, retrySpec);
    }
}
