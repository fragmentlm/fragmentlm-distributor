package org.fragmentlm.distributor.controller;

import org.fragmentlm.distributor.dto.DistributorRequest;
import org.fragmentlm.distributor.dto.PeerReply;
import org.fragmentlm.distributor.service.FragmentFetcherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/fragmentlm")
public class DistributorController
{
    private final FragmentFetcherService service;

    public DistributorController (FragmentFetcherService service)
    {
        this.service = service;
    }

    @PostMapping("/distribute-fragments")
    public ResponseEntity<Map<String, PeerReply>> distributeFragments (@RequestBody DistributorRequest request)
    {
        return new ResponseEntity<>(service.sendRequests(request.fragments()), HttpStatus.OK);
    }

    @GetMapping("/get-fragments")
    public ResponseEntity<Map<String, PeerReply>> getFragments ()
    {
        return new ResponseEntity<>(service.getResponses(), HttpStatus.OK);
    }
}
