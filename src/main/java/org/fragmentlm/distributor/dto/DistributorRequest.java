package org.fragmentlm.distributor.dto;

import java.util.List;

public record DistributorRequest(
    List<Peer> peers,
    List<String> fragments
    )
{
}
