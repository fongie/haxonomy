package se.kth.moadb.haxonomysite.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
@Data
public class Reply {
    public final static String UNKNOWN = "UNKNOWN";
    public final static String YES = "YES";
    public final static String NO = "NO";

    @Id
    String name;

    public Reply(String name) {
        this.name = name;
    }

    public Reply() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Reply reply = (Reply) o;
        return Objects.equals(name, reply.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), name);
    }
}
