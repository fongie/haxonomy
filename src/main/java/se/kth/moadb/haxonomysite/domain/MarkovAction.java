package se.kth.moadb.haxonomysite.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class MarkovAction implements Comparable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    Reply reply;
    @ManyToOne
    Term term;
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    MarkovState markovState;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    QValue qValue;


    @Override
    public int compareTo(Object o) {
        MarkovAction compare = (MarkovAction)o;
        if (this.qValue.getValue() > compare.qValue.getValue())
            return 1;
        else
            return 0;
    }
}
