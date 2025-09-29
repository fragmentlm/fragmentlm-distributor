package org.fragmentlm.distributor.dto;

import jakarta.validation.constraints.NotNull;

public record WeightedFragment(@NotNull String address, @NotNull String fragment)
{
}
