package se.kth.moadb.haxonomysite.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Collection;
import java.util.Optional;

@Entity
@Data
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String url;
    private String title;
    private int bounty;

    @ManyToMany
    Collection<Term> terms;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Term getVulnerability() {
        Optional<Term> vuln = terms.stream()
              .filter(
                    term -> term.hasBroaderTerm() && term.getBroaderTerm().getName().equals(Term.ROOT_VULNERABILITY)
              )
              .findFirst();
        if (vuln.isPresent())
            return vuln.get();
        else
            return null;
    }

}