package se.kth.moadb.haxonomysite.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String url;
    private String title;
    private int bounty;
    private String vulnerability;

    @ManyToMany
    Collection<Term> terms;

}