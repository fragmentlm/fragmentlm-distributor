package org.fragmentlm.distributor.service;

import jakarta.validation.constraints.NotNull;
import org.fragmentlm.distributor.dto.PeerReply;
import org.fragmentlm.distributor.dto.ProcessedFragments;
import org.fragmentlm.distributor.dto.WeightedFragment;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IPeerConnectionService
{
    CompletableFuture<ProcessedFragments> distributeRequest (final @NotNull List<WeightedFragment> requests);

    CompletableFuture<PeerReply> sendFragment (final @NotNull URI url, final @NotNull String requestJsonString);
}
