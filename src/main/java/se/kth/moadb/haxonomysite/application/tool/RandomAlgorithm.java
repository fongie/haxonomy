package se.kth.moadb.haxonomysite.application.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import se.kth.moadb.haxonomysite.domain.MarkovAction;
import se.kth.moadb.haxonomysite.repository.MarkovActionRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Just send actions to try randomly
 */
@Component
public class RandomAlgorithm implements ActionChoosingAlgorithm {
   @Autowired
   private MarkovActionRepository markovActionRepository;

   @Override
   public MarkovAction chooseNextAction(long markovStateId) {
      Collection<MarkovAction> actionCollection = markovActionRepository.findAllByMarkovState_Id(markovStateId);
      List<MarkovAction> actions = new ArrayList<>();
      actions.addAll(actionCollection);

      Random rand = new Random();
      int numActions = actions.size();
      int actionId = rand.nextInt(numActions);
      return actions.get(actionId);
   }

   @Override
   public MarkovAction chooseActionFromNewPath(long stateId, String actionStatus, long termName) {
      return null;
   }
}
