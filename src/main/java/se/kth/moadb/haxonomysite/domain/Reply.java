package se.kth.moadb.haxonomysite.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Reply {
    public final static String UNKNOWN = "UNKNOWN";
    public final static String YES = "YES";
    public final static String NO = "NO";

    @Id
    String name;
}
