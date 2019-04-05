package se.kth.moadb.haxonomysite.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import se.kth.moadb.haxonomysite.domain.Term;

@RepositoryRestResource(path = "terms")
public interface TermRepository extends PagingAndSortingRepository<Term, Long> {
}
