package org.fragmentlm.distributor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fragmentlm.distributor.dto.PeerReply;
import org.fragmentlm.distributor.dto.WeightedFragment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Service
public class FragmentFetcherService
{
    private final RestTemplate restTemplate;
    private final Map<String, PeerReply> responses = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FragmentFetcherService ()
    {
        this.restTemplate = new RestTemplate();
    }

    public Map<String, PeerReply> sendRequests (List<WeightedFragment> requests)
    {
        CountDownLatch latch = new CountDownLatch(requests.size());
        requests.forEach((weightedFragment) ->
        {
            final String ip = weightedFragment.address(), fragment = weightedFragment.fragment();
            Thread.ofVirtual().start(() ->
            {
                String url = "http://" + ip + "/fragmentlm/worker";
                try
                {
                    String jsonRequest = "{\"fragment\":\"" + fragment + "\"}";
                    String jsonResponse = restTemplate.postForObject(url, jsonRequest, String.class);
                    if (jsonResponse == null)
                    {
                        return;
                    }
                    JsonNode jsonNode = objectMapper.readTree(jsonResponse);
                    int status = jsonNode.path("status").asInt();
                    String processed = jsonNode.path("processed").asText();
                    responses.put(ip, new PeerReply(status, processed));
                    System.out.println("Response from " + ip + ": Status: " + status + ", Processed: " + processed);
                } catch (HttpClientErrorException e)
                {
                    System.err.println("Error calling " + url + ": " + e.getStatusCode());
                } catch (Exception e)
                {
                    System.err.println("Unexpected error: " + e.getMessage());
                }
            });
        });
        try
        {
        latch.await();
        } catch (InterruptedException e)
        {
            return Map.of();
        }
        return responses;
    }

    public Map<String, PeerReply> getResponses ()
    {
        return responses;
    }
}
