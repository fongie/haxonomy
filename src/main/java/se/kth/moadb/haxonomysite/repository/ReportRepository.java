package se.kth.moadb.haxonomysite.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import se.kth.moadb.haxonomysite.domain.Report;

import java.util.List;

@RepositoryRestResource(path = "reports")
public interface ReportRepository extends PagingAndSortingRepository<Report, Long> {
    List<Report> findAll();
}
