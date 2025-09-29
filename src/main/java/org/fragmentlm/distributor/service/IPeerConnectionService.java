package org.fragmentlm.distributor.service;

import jakarta.validation.constraints.NotNull;
import org.fragmentlm.distributor.dto.ProcessedFragments;
import org.fragmentlm.distributor.dto.WeightedFragment;

import java.util.List;

public interface IPeerConnectionService
{
    ProcessedFragments sendRequests (@NotNull List<WeightedFragment> requests);

    ProcessedFragments getResponses ();
}
