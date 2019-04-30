package se.kth.moadb.haxonomysite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import se.kth.moadb.haxonomysite.domain.MarkovAction;
import se.kth.moadb.haxonomysite.domain.Reply;
import se.kth.moadb.haxonomysite.domain.Term;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface MarkovActionRepository extends JpaRepository<MarkovAction, Long> {
   Collection<MarkovAction> findAllByMarkovState_Id(long stateId);
   Collection<MarkovAction> findAllByReply(Reply replyName);
   List<MarkovAction> findAllByTerm(Term term);
   Optional<MarkovAction> findByTermAndReply(Term term, Reply reply);
}
