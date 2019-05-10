package se.kth.moadb.haxonomysite.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.kth.moadb.haxonomysite.repository.MarkovActionRepository;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Data
@Entity
public class MarkovState implements Comparable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore // TODO should this realy be ignored
    @OneToMany(mappedBy = "markovState")
    private Collection<MarkovAction> markovActions;

    public long getId() {
        return id;
    }

    private double qValue;

    @Override
    public int compareTo(Object o) {
        MarkovState compare = (MarkovState)o;
        if (this.qValue > compare.qValue)
            return 1;
        else
            return 0;
    }
}