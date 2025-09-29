package org.fragmentlm.distributor.configuration;

import jakarta.validation.constraints.NotNull;
import org.fragmentlm.distributor.dto.ProcessedFragments;
import org.fragmentlm.distributor.service.FragmentFetcherService;
import org.fragmentlm.distributor.service.IPeerConnectionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class PeerConnectionServiceConfig
{
    @Bean
    public IPeerConnectionService service (@NotNull ProcessedFragments replyObject, @NotNull RestTemplate restTemplate)
    {
        return new FragmentFetcherService(replyObject, restTemplate);
    }

    @Bean
    public @NotNull RestTemplate restTemplate ()
    {
        return new RestTemplate();
    }

    @Bean
    public @NotNull ProcessedFragments reply ()
    {
        return new ProcessedFragments(new ConcurrentHashMap<>());
    }
}
