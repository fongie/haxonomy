package se.kth.moadb.haxonomysite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import se.kth.moadb.haxonomysite.domain.MarkovAction;

import java.util.Collection;

@RepositoryRestResource
public interface MarkovActionRepository extends JpaRepository<MarkovAction, Long> {
   Collection<MarkovAction> findAllByMarkovState_Id(long stateId);
}
