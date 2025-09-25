package org.fragmentlm.distributor.dto;

import java.util.Map;

public record FragmentFetcherServiceReply(Map<String, PeerReply> mappedReplies)
{
}
