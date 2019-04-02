package se.kth.moadb.haxonomysite.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data
public class Term {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;

    @ManyToOne
    private Term broader_term;


    @ManyToMany(mappedBy = "term")
    Collection<Report> reports;

}
