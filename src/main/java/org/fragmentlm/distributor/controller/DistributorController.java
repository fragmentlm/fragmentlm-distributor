package org.fragmentlm.distributor.controller;

import jakarta.validation.constraints.NotNull;
import org.fragmentlm.distributor.dto.DistributorRequest;
import org.fragmentlm.distributor.dto.ProcessedFragments;
import org.fragmentlm.distributor.service.IPeerConnectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fragmentlm")
public class DistributorController
{
    private final IPeerConnectionService service;

    public DistributorController (@NotNull IPeerConnectionService service)
    {
        this.service = service;
    }

    /**
     * Endpoint to start a new distributing operation
     *
     * @param request request containing list of weighted fragments
     * @return Response entity containing partially filled map of individual processed fragments;status 200 if full, 206 if some was lost
     */
    @PostMapping("/distribute-fragments")
    public @NotNull ResponseEntity<ProcessedFragments> distributeFragments (@NotNull @RequestBody DistributorRequest request)
    {
        var replies = service.sendRequests(request.fragments());
        if (replies.mappedReplies().size() != request.fragments().size())
        {
            return new ResponseEntity<>(replies, HttpStatus.PARTIAL_CONTENT);
        }
        return ResponseEntity.ok(replies);
    }

    /**
     * Endpoint to lookup partial result of current operation
     *
     * @return Response entity containing partially filled map of individual processed fragments; always status 200
     * @apiNote The returned body object may be partially filled or empty
     */
    @GetMapping("/get-fragments")
    public @NotNull ResponseEntity<ProcessedFragments> getFragments ()
    {
        return new ResponseEntity<>(service.getResponses(), HttpStatus.OK);
    }
}
