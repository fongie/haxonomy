package se.kth.moadb.haxonomysite.presentation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@RestController
public class ReportController {

    @PostMapping("/reportData")
    public ReportMetadataPostRequest successLogin(@RequestBody ReportMetadataPostRequest reportMetadataPostRequest){
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl
                = reportMetadataPostRequest.getUrl() + ".json";
        ResponseEntity<String> response
                = restTemplate.getForEntity(fooResourceUrl , String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(response.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String title = root.path("title").textValue();
        Double bounty = root.path("bounty_amount").asDouble();
        String vulnerability = root.path("weakness").path("name").textValue();
        reportMetadataPostRequest.setTitle(title);
        reportMetadataPostRequest.setBounty(bounty);
        reportMetadataPostRequest.setVulnerability(vulnerability);
        return reportMetadataPostRequest;
    }

}
