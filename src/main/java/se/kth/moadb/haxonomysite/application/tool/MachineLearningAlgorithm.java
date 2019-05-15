package se.kth.moadb.haxonomysite.application.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import se.kth.moadb.haxonomysite.domain.MarkovAction;
import se.kth.moadb.haxonomysite.domain.MarkovState;
import se.kth.moadb.haxonomysite.repository.MarkovActionRepository;
import se.kth.moadb.haxonomysite.repository.MarkovStateRepository;

import java.util.*;

/**
 * 1) Get current MarkovState ID
 * 2) Check which Terms in that State that are still marked as UNKNOWN
 * 3a) Check if there are any MarkovStates that equals this current MarkovState but with one more Term answered with YES or NO
 * 3b) If there are no such states, chose a Term/Action at random
 * 4a) Check which of these MarkovStates that has the highest Q value
 * 4b) If there are several MarkovStates with the same Q value, chose one at random
 * 5a) Try to go to that state by asking that Terms question
 * 6) Take the Q value the state we chose to "go to" and update this state Q value as 0.8 * the Q value of State we chose to go to
 *
 * ...Then make all the steps again for the new state
 */
@Component
@Primary
public class MachineLearningAlgorithm implements ActionChoosingAlgorithm {


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

        System.out.println("MarkovState Id: " + markovStateId);

        MarkovState currentState = markovStateRepository.findById(markovStateId);
        List<MarkovAction> listOfCurrentActions = new ArrayList<>(currentState.getMarkovActions());

//        System.out.println("Current Markov State:" + currentState.getId());
//        System.out.println("Size of list of current Markov Actions:" + listOfCurrentActions.size());

        Collection<MarkovState> stateCollection = markovStateRepository.findAll();
        List<MarkovState> allMarkovStates = new ArrayList<>(stateCollection);

//        System.out.println("Number of MarkovStates in Database: " + allMarkovStates.size());

        List<MarkovState> listOfPossibleStatesToGoTo = new ArrayList<>();
        HashMap<MarkovState, MarkovAction> stateActionMap = new HashMap<>();
        HashMap<MarkovState, MarkovAction> possibleStateActions = new HashMap<>();


        for (MarkovState state : allMarkovStates) {

            List<MarkovAction> actions = new ArrayList<>(state.getMarkovActions());
//            System.out.println("Number of actions in this specific State:" + actions.size());
//            System.out.println("Reply in first action: " + actions.get(147).getReply());
            System.out.println("Current state in loop: " + state.getId());

            int numberOfTermsWidthDifferentStatus = 0;
            for (int i = 0; i < actions.size(); i++) {
                if (!actions.get(i).getReply().equals(listOfCurrentActions.get(i).getReply())) {
//                    System.out.println("listOfCurrentActions.get(i).getReply(): " + listOfCurrentActions.get(i).getReply());
//                    System.out.println("actions.get(i).getReply(): " + actions.get(i).getReply());
                    numberOfTermsWidthDifferentStatus++;
                    stateActionMap.put(state, actions.get(i)); // update HashTable with relevant State and Term info
                }
                if (numberOfTermsWidthDifferentStatus > 1) {
                    stateActionMap.clear(); // clear if more than one difference
                    break;
                }
            }
            if (numberOfTermsWidthDifferentStatus == 1) {
                System.out.println(state.getQValue());
                listOfPossibleStatesToGoTo.add(state);
                possibleStateActions.put(state, stateActionMap.get(state));
            }
        }

        MarkovState maxQValueState = Collections.max(listOfPossibleStatesToGoTo);
        System.out.println(maxQValueState.getQValue());

        MarkovAction nextAction = possibleStateActions.get(maxQValueState);
        return nextAction;
    }
}
