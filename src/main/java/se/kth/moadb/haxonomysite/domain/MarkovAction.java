package se.kth.moadb.haxonomysite.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;

@Data
@NoArgsConstructor
@Entity
public class MarkovAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    Reply reply;
    @ManyToOne
    Term term;

    @ManyToMany
    Collection<MarkovState> markovState;
}