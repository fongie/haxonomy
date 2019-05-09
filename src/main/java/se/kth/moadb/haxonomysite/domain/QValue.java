package se.kth.moadb.haxonomysite.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "q_value")
public class QValue {
    //TODO OBS! This class is not used ritht now, the Q Value is just a varible in MarkovState instead

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double value;

    public QValue(double value) {
        this.value = value;
    }

}
