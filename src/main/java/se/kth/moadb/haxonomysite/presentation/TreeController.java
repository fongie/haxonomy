package se.kth.moadb.haxonomysite.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import se.kth.moadb.haxonomysite.application.taxonomy.TreeBuilder;
import se.kth.moadb.haxonomysite.domain.Term;
import se.kth.moadb.haxonomysite.domain.nonpersistent.TreeNode;
import se.kth.moadb.haxonomysite.repository.TermRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
public class TreeController {

    @Autowired
    private TermRepository termRepository;
    @Autowired
    private TreeBuilder treeBuilder;

    @GetMapping("/tree")
    public Collection<TreeNode> getTermTree() {
        TreeNode tree = treeBuilder.buildTermTree();
        Collection<TreeNode> list = new ArrayList<>();
        list.add(tree); //app wants it wrapped in a list
        return list;
    }

    @GetMapping("/vulnerabilities")
    public Collection<Term> getVulnerabilities() {
        return termRepository.findByBroaderTerm(termRepository.findByName("vulnerabilities"));
    }

}
