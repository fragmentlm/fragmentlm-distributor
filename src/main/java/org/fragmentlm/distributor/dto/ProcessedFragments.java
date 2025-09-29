package org.fragmentlm.distributor.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record ProcessedFragments(@NotNull Map<String, PeerReply> mappedReplies)
{
}
