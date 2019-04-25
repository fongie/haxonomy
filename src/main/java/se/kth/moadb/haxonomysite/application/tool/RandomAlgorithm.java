package se.kth.moadb.haxonomysite.application.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.kth.moadb.haxonomysite.domain.MarkovAction;
import se.kth.moadb.haxonomysite.repository.MarkovActionRepository;

import java.util.Collection;

@Component
public class RandomAlgorithm implements ActionChoosingAlgorithm {
   @Autowired
   private MarkovActionRepository markovActionRepository;

   @Override
   public MarkovAction chooseNextAction(long markovStateId) {
      Collection<MarkovAction> actions = markovActionRepository.findAllByMarkovState_Id(markovStateId);
      return actions.stream().findFirst().get();
   }
}
