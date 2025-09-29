package org.fragmentlm.distributor.controller;

import jakarta.validation.constraints.NotNull;
import org.fragmentlm.distributor.dto.DistributorRequest;
import org.fragmentlm.distributor.dto.ProcessedFragments;
import org.fragmentlm.distributor.service.IPeerConnectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/fragmentlm")
public class DistributorController
{
    private final IPeerConnectionService service;

    public DistributorController (@NotNull IPeerConnectionService service)
    {
        this.service = service;
    }

    /**
     * Endpoint to start a new distributing operation
     *
     * @param request request containing list of weighted fragments
     * @return Response entity containing partially filled map of individual processed fragments;status 200 if full, 206 if some was lost
     */
    @PostMapping("/distribute-fragments")
    public @NotNull CompletableFuture<ResponseEntity<ProcessedFragments>> distributeFragments (@NotNull @RequestBody DistributorRequest request)
    {
        var replies = service.distributeRequest(request.fragments());
        return replies.thenApply(ResponseEntity::ok)
            .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
    }
}
