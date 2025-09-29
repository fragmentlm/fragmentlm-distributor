package org.fragmentlm.distributor.dto;

import jakarta.validation.constraints.NotNull;

public record PeerReply(int errorCode, @NotNull String reply)
{
}
