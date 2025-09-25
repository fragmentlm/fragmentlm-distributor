package org.fragmentlm.distributor.controller;

import org.fragmentlm.distributor.dto.DistributorRequest;
import org.fragmentlm.distributor.dto.FragmentFetcherServiceReply;
import org.fragmentlm.distributor.service.FragmentFetcherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fragmentlm")
public class DistributorController
{
    private final FragmentFetcherService service;

    public DistributorController (FragmentFetcherService service)
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
    public ResponseEntity<FragmentFetcherServiceReply> distributeFragments (@RequestBody DistributorRequest request)
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
    public ResponseEntity<FragmentFetcherServiceReply> getFragments ()
    {
        return new ResponseEntity<>(service.getResponses(), HttpStatus.OK);
    }
}
