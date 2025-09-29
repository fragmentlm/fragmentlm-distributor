package org.fragmentlm.distributor.dto;

import java.util.Map;

public record ProcessedFragments(Map<String, PeerReply> mappedReplies)
{
}
