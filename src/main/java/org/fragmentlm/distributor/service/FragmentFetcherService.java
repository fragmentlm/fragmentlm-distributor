package org.fragmentlm.distributor.service;

import org.fragmentlm.distributor.dto.FragmentFetcherServiceReply;
import org.fragmentlm.distributor.dto.PeerReply;
import org.fragmentlm.distributor.dto.WeightedFragment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Service to manage connection with the peers
 */
@Service
public class FragmentFetcherService
{
    private final RestTemplate restTemplate;
    private final FragmentFetcherServiceReply replyObject = new FragmentFetcherServiceReply(new ConcurrentHashMap<>());

    public FragmentFetcherService ()
    {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Sends the fragments to the peers
     *
     * @param requests list of fragments to be sent
     * @return map of peer replies to their addresses
     */
    public FragmentFetcherServiceReply sendRequests (List<WeightedFragment> requests)
    {
        CountDownLatch latch = new CountDownLatch(requests.size());
        requests.forEach((weightedFragment) ->
        {
            final String ip = weightedFragment.address();
            final String fragment = weightedFragment.fragment();
            Thread.ofVirtual().start(() ->
            {
                final String url = "http://" + ip + "/fragmentlm/worker";
                final String jsonRequest = "{\"fragment\":\"" + fragment + "\"}";
                try
                {
                    final ResponseEntity<PeerReply> reply = restTemplate.postForEntity(url, jsonRequest, PeerReply.class);
                    if (!reply.getStatusCode().is2xxSuccessful())
                    {
                        replyObject.mappedReplies().put(ip, new PeerReply(255, ""));
                        return;
                    }
                    replyObject.mappedReplies().put(ip, reply.getBody());
                } catch (RestClientException e)
                {
                    System.err.println(e.getMessage());
                }
                latch.countDown();
            });
        });
        try
        {
            latch.await();
        } catch (InterruptedException e)
        {
            return replyObject;
        }
        return replyObject;
    }

    /**
     * Gets currently present reply object
     * @return partially filled reply
     */
    public FragmentFetcherServiceReply getResponses ()
    {
        return replyObject;
    }
}
