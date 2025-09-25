package org.fragmentlm.distributor.controller;

import org.fragmentlm.distributor.dto.DistributorRequest;
import org.fragmentlm.distributor.dto.DistributorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DistributorController
{
    @PostMapping("/distributor")
    public ResponseEntity<DistributorResponse> handleRequest(@RequestBody DistributorRequest request)
    {
        final DistributorResponse response = new DistributorResponse("Test info");
        System.out.println(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
