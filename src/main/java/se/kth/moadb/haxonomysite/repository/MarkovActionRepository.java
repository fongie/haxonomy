package se.kth.moadb.haxonomysite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.kth.moadb.haxonomysite.domain.MarkovAction;

@Repository
public interface MarkovActionRepository extends JpaRepository<MarkovAction, Long> {
}
