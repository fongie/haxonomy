package se.kth.moadb.haxonomysite.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

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

    @JoinColumn
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    MarkovState markovState;

    public MarkovAction copy() {
        MarkovAction copy = new MarkovAction();
        copy.setTerm(term);
        copy.setReply(reply);
//        copy.setMarkovState(markovState);
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        MarkovAction other = (MarkovAction) o;
        return this.id == other.id;
    }
}
