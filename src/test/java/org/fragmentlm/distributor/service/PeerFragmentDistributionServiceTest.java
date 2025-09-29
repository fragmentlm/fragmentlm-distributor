package org.fragmentlm.distributor.service;

import org.fragmentlm.distributor.controller.DistributorController;
import org.fragmentlm.distributor.dto.ProcessedFragments;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DistributorController.class)
class PeerFragmentDistributionServiceTest
{
    @Autowired
    MockMvc mockMvc;

    @MockBean
    IPeerConnectionService peerConnectionService;

    @Test
    void getFragmentsResult() throws Exception
    {
        when(peerConnectionService.distributeRequest(any()))
            .thenReturn(CompletableFuture.completedFuture(new ProcessedFragments(new ConcurrentHashMap<>())));

        mockMvc.perform(post("/fragmentlm/distribute-fragments")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"fragment\": []}"))
            .andExpect(status().is2xxSuccessful());
    }
}
