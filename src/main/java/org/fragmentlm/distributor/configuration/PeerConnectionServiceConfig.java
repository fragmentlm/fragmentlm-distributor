package org.fragmentlm.distributor.configuration;

import org.fragmentlm.distributor.service.FragmentFetcherService;
import org.fragmentlm.distributor.service.IPeerConnectionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PeerConnectionServiceConfig
{
    @Bean
    public IPeerConnectionService service()
    {
        return new FragmentFetcherService();
    }
}
