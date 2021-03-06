package se.kth.moadb.haxonomysite.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Time {
    @Id
    private long id;
    private int time;
}
