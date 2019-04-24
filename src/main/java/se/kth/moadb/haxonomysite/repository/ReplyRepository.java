package se.kth.moadb.haxonomysite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import se.kth.moadb.haxonomysite.domain.Reply;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, String> {
}
