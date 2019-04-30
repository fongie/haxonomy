package se.kth.moadb.haxonomysite.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.LinkedList;

@Entity
@Data
public class Playthrough {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private MarkovState currentState;

    //TODO make markovstate alwaysasId
    private LinkedList<MarkovState> path;
}
