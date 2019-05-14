package se.kth.moadb.haxonomysite.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(
      uniqueConstraints = {
            @UniqueConstraint(columnNames = {"name", "broader_term_id"})
      }
)
@Data
public class Term {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    @ManyToOne
    private Time time;

    @ManyToOne
    private Term broaderTerm;

    @ManyToMany(mappedBy = "terms")
    @JsonIgnore
    Collection<Report> reports;

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id, name, time, broaderTerm, reports);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term = (Term) o;
        return id == term.id &&
                Objects.equals(name, term.name);
    }

}
