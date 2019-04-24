package se.kth.moadb.haxonomysite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.kth.moadb.haxonomysite.domain.MarkovState;

@Repository
public interface MarkovStateRepository extends JpaRepository<MarkovState, Long> {
}
