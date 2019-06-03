package se.kth.moadb.haxonomysite.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.kth.moadb.haxonomysite.repository.MarkovActionRepository;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Entity
public class MarkovState implements Comparable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "markovState")
    @LazyCollection(LazyCollectionOption.FALSE)
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
        else if (this.qValue == compare.qValue)
            return 0;
        else
            return -1;
    }

    public MarkovState copy() {
        MarkovState markovCopy = new MarkovState();
        Collection<MarkovAction> newActions = markovActions.stream()
              .map(action -> action.copy())
              .collect(Collectors.toList()
              );
        newActions.stream().forEach(a -> a.setMarkovState(markovCopy));
        markovCopy.setMarkovActions(newActions);
        return markovCopy;
    }


    @JsonIgnore
    public Collection<MarkovAction> getMarkovActions() {
        return markovActions;
    }

    public void setMarkovActions(Collection<MarkovAction> markovActions) {
        this.markovActions = markovActions;
    }

    public double getQValue() {
        return qValue;
    }

    public void setQValue(double qValue) {
        this.qValue = qValue;
    }

    public void setId(long id) {
        this.id = id;
    }
}