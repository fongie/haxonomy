package se.kth.moadb.haxonomysite.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "q_value")
public class QValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "value")
    private double value;

    public double getValue() {
        return value;
    }

    public QValue(double value) {
        this.value = value;
    }

    public QValue() {
    }
}
