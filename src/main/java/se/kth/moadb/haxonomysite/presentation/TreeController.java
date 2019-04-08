package se.kth.moadb.haxonomysite.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import se.kth.moadb.haxonomysite.domain.Term;
import se.kth.moadb.haxonomysite.domain.nonpersistant.TreeNode;
import se.kth.moadb.haxonomysite.repository.TermRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
public class TreeController {

    @Autowired
    private TermRepository termRepository;

    @GetMapping("/tree")
    public TreeNode getTermTree() {
        Collection<Term> allTerms = new ArrayList<>();
        termRepository.findAll().forEach(allTerms::add);
        TreeNode tree = buildTree(allTerms);
        return tree;
    }
    private TreeNode buildTree(Collection<Term> terms) {
        Term rootTerm = terms.stream().filter(term -> term.getBroaderTerm() == null).findFirst().get();
        TreeNode root = termToNode(rootTerm);
        buildTree(root, terms);
        return root;
    }
    private void buildTree(TreeNode node, Collection<Term> terms) {
        node.setChildren(findTermChildren(node.getTerm(), terms));
        node.getChildren().forEach(treeNode -> buildTree(treeNode, terms));
    }
    private Collection<TreeNode> findTermChildren(Term parent, Collection<Term> terms) {
        return terms.stream()
                .filter(term -> term.getBroaderTerm() != null)
                .filter(term -> term.getBroaderTerm().getId() == parent.getId())
                .map(this::termToNode)
                .collect(Collectors.toList());
    }
    private TreeNode termToNode(Term term) {
        TreeNode node = new TreeNode();
        node.setTerm(term);
        node.setChildren(new ArrayList<>());
        return node;
    }
}
