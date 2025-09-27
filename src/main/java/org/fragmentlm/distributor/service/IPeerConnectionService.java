package org.fragmentlm.distributor.service;

import org.fragmentlm.distributor.dto.FragmentFetcherServiceReply;
import org.fragmentlm.distributor.dto.WeightedFragment;

import java.util.List;

public interface IPeerConnectionService
{
    FragmentFetcherServiceReply sendRequests (List<WeightedFragment> requests);

    FragmentFetcherServiceReply getResponses ();
}
