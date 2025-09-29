package org.fragmentlm.distributor.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DistributorRequest(
    @NotNull List<WeightedFragment> fragments
)
{
}
