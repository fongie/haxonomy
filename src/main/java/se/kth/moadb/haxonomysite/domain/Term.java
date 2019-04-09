package se.kth.moadb.haxonomysite.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(
      uniqueConstraints = {
            @UniqueConstraint(columnNames = {"name", "broader_term_id"})
      }
)
@Data
public class Term {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    @ManyToOne
    private Term broaderTerm;

    @ManyToMany(mappedBy = "terms")
    Collection<Report> reports;

}
