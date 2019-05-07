package se.kth.moadb.haxonomysite.application.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import se.kth.moadb.haxonomysite.domain.MarkovAction;
import se.kth.moadb.haxonomysite.domain.QValue;
import se.kth.moadb.haxonomysite.domain.Reply;
import se.kth.moadb.haxonomysite.repository.MarkovActionRepository;

import java.util.*;

@Component
@Primary
public class MachineLearningAlgorithm implements ActionChoosingAlgorithm {

    private double gamma = 0.8;

    /* Add values to some terms to see if it works
    * In mysql terminal:
    * UPDATE q_value SET value=100 WHERE value=0 LIMIT 20;
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Autowired
    MarkovActionRepository markovActionRepository;

    @Override
    public MarkovAction chooseNextAction(long markovStateId) {
        Collection<MarkovAction> actionCollection = markovActionRepository.findAllByMarkovState_IdAndReply_Name(markovStateId, "UNKNOWN");
        List<MarkovAction> actions = new ArrayList<>();
        actions.addAll(actionCollection);

        Random rand = new Random();
        int numActions = actions.size();
        int actionId = rand.nextInt(numActions);

        updateQValues(actions.get(actionId), actions); // instead of updating Q matrix

        return actions.get(actionId);
    }

    private void updateQValues(MarkovAction currentAction, List<MarkovAction> actions){
        MarkovAction maxQValueAction = Collections.max(actions);
//        System.out.println("Max Q Value Action " + maxQValueAction);
        currentAction.setQValue(new QValue(gamma * maxQValueAction.getQValue().getValue()));
        markovActionRepository.save(currentAction);
    }
}
