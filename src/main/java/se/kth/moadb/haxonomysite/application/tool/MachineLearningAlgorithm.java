package se.kth.moadb.haxonomysite.application.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import se.kth.moadb.haxonomysite.domain.MarkovAction;
import se.kth.moadb.haxonomysite.domain.MarkovState;
import se.kth.moadb.haxonomysite.domain.QValue;
import se.kth.moadb.haxonomysite.domain.Reply;
import se.kth.moadb.haxonomysite.repository.MarkovActionRepository;
import se.kth.moadb.haxonomysite.repository.MarkovStateRepository;
import sun.jvm.hotspot.oops.Mark;

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

    @Autowired
    MarkovStateRepository markovStateRepository;

    @Override
    public MarkovAction chooseNextAction(long markovStateId) {

        // All states
        Collection<MarkovState> stateCollection = markovStateRepository.findAll();
        List<MarkovState> states = new ArrayList<>();
        states.addAll(stateCollection);

        // All actions that has a reply status UNKNOWN
        Collection<MarkovAction> actionCollection = markovActionRepository.findAllByMarkovState_IdAndReply_Name(markovStateId, "UNKNOWN");
        List<MarkovAction> actions = new ArrayList<>();
        actions.addAll(actionCollection);

        Random rand = new Random();
        int numActions = actions.size();
        int actionId = rand.nextInt(numActions);

        updateQValues(states, stateCollection, actions.get(actionId), actions); // instead of updating Q matrix

        return actions.get(actionId);
    }

    private void updateQValues(List<MarkovState> states, Collection<MarkovState> stateCollection, MarkovAction currentAction, List<MarkovAction> actions){

        /**
         * Look through all actions connected to a state and check which of those actions
         * that still is UNKNOWN leeds to the next possible state with highest q value
         */
        for(MarkovState state : stateCollection){
            for (MarkovAction action : state.getMarkovActions()) {
                System.out.println(action.getReply().getName());
            }
        }

        MarkovState maxQValueState = Collections.max(states);


//        MarkovAction maxQValueAction = Collections.max(actions);
//        System.out.println("Max Q Value Action " + maxQValueAction);
//        currentAction.setQValue(new QValue(gamma * maxQValueAction.getQValue().getValue()));
//        markovActionRepository.save(currentAction);
    }
}
