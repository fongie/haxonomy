package se.kth.moadb.haxonomysite.domain.nonpersistent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.kth.moadb.haxonomysite.domain.Term;

import java.util.Collection;

@JsonPropertyOrder({"termId", "name", "children"})
@Data
@NoArgsConstructor
public class TreeNode {

    @JsonIgnore
    private Term term;

    public String getName() {
        return term.getName();
    }

    public long getTermId() {
        return term.getId();
    }

    private Collection<TreeNode> children;
}
