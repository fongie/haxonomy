package se.kth.moadb.haxonomysite.presentation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import se.kth.moadb.haxonomysite.domain.Report;
import se.kth.moadb.haxonomysite.domain.Term;
import se.kth.moadb.haxonomysite.repository.ReportRepository;

import java.io.IOException;

@RestController
public class ReportController {
   @Autowired
   private ReportRepository reportRepository;

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

    @GetMapping("/reports/{id}/vulnerability")
   public Term getReportVulnerability(@PathVariable long id) {

       //TODO check if exists before get
        Report report = reportRepository.findById(id).get();
        Term vulnerability = report.getTerms().stream()
              .filter(
                    term -> term.hasBroaderTerm() && term.getBroaderTerm().getName().equals(Term.ROOT_VULNERABILITY)
              )
              .findFirst()
              .get();

        return vulnerability;
    }

}
