package se.kth.moadb.haxonomysite.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import se.kth.moadb.haxonomysite.domain.Term;

import java.util.Collection;

@RepositoryRestResource(path = "terms")
public interface TermRepository extends PagingAndSortingRepository<Term, Long> {

    //TODO dont expose delete, put, etc.

    Term findByName(String name);
    Collection<Term> findByBroaderTerm(Term term);
    Term findById(long id);
}
