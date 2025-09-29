package org.fragmentlm.distributor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.constraints.NotNull;
import org.fragmentlm.distributor.dto.PeerReply;
import org.fragmentlm.distributor.dto.ProcessedFragments;
import org.fragmentlm.distributor.dto.WeightedFragment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Service to manage connection with the peers
 */
@Service
public class PeerFragmentDistributionService implements IPeerConnectionService
{
    private final ProcessedFragments replyObject;
    private final RestTemplate restTemplate;

    public PeerFragmentDistributionService (ProcessedFragments replyObject, RestTemplate restTemplate)
    {
        this.replyObject = replyObject;
        this.restTemplate = restTemplate;
    }

    /**
     * Sends the fragments to the peers
     *
     * @param requests list of fragments to be sent
     * @return map of peer replies to their addresses
     */
    public @NotNull ProcessedFragments sendRequests (@NotNull List<WeightedFragment> requests)
    {
        CountDownLatch latch = new CountDownLatch(requests.size());
        requests.forEach((weightedFragment) ->
        {
            final String ip = weightedFragment.address();
            final String fragment = weightedFragment.fragment();
            Thread.ofVirtual().start(() ->
            {
                final String url = buildUrlString(ip, "/fragmentlm/worker");
                final ObjectMapper mapper = new ObjectMapper();
                final ObjectNode root = mapper.createObjectNode();
                root.put("fragment", fragment);
                final String jsonRequest = root.toString();
                try
                {
                    final ResponseEntity<PeerReply> reply = restTemplate.postForEntity(url, jsonRequest, PeerReply.class);
                    if (!reply.getStatusCode().is2xxSuccessful())
                    {
                        replyObject.mappedReplies().put(ip, new PeerReply(reply.getStatusCode().value(), ""));
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

    private static @NotNull String buildUrlString (@NotNull String address, @NotNull String path)
    {
        try
        {
            final URI uri = new URI("http", address, path, null);
            return uri.toString();
        } catch (URISyntaxException e)
        {
            return "";
        }
    }

    /**
     * Gets currently present reply object
     *
     * @return partially filled reply
     */
    public @NotNull ProcessedFragments getResponses ()
    {
        return replyObject;
    }
}
