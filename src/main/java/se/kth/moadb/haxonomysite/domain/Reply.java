package se.kth.moadb.haxonomysite.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reply {
    public final static String UNKNOWN = "UNKNOWN";
    public final static String YES = "YES";
    public final static String NO = "NO";

    @Id
    String name;
}
