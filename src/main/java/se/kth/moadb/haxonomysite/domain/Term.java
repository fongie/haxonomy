package se.kth.moadb.haxonomysite.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Collection;
import java.util.Optional;

@Entity
@Table(
      uniqueConstraints = {
            @UniqueConstraint(columnNames = {"name", "broader_term_id"})
      }
)
@Data
public class Term {

    public static final String ROOT_VULNERABILITY = "Vulnerabilities";
    public static final String ROOT_TERM = "attack surface";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    @ManyToOne
    private Time time;

    @ManyToOne
    private Term broaderTerm;

    @ManyToMany(mappedBy = "terms")
    @JsonIgnore
    Collection<Report> reports;

    @JsonIgnore
    public boolean hasBroaderTerm() {
        Optional<Term> t = Optional.ofNullable(broaderTerm);
        return t.isPresent();
    }
}
