package se.kth.moadb.haxonomysite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import se.kth.moadb.haxonomysite.domain.Time;

@RepositoryRestResource
public interface TimeRepository extends JpaRepository<Time, Long> {
}
