package se.kth.moadb.haxonomysite.application.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import se.kth.moadb.haxonomysite.domain.MarkovAction;
import se.kth.moadb.haxonomysite.domain.MarkovState;
import se.kth.moadb.haxonomysite.domain.Reply;
import se.kth.moadb.haxonomysite.repository.MarkovActionRepository;
import se.kth.moadb.haxonomysite.repository.MarkovStateRepository;

import java.util.*;
import java.util.stream.Collectors;

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


    /*
     * Returns true if two markovActions has the same Term and Reply
     */
    private boolean hasSameTermAndReply(MarkovAction comparing, Collection<MarkovAction> collection) {

        MarkovAction selected = collection.stream()
                .filter(markovAction -> markovAction.getTerm().equals(comparing.getTerm()))
                .findFirst()
                .get();

        if (comparing.getReply().equals(selected.getReply())) {
//            System.out.println("Comparing reply: " + comparing.getReply().getName() + " Selected reply: " + selected.getReply().getName());
            return true;
        }
//        System.out.println("Comparing != Selected" + " Comparing: " + comparing.getTerm().getName() + " " + comparing.getReply().getName() + " Selected: " + selected.getTerm().getName() + " " + selected.getReply().getName());
        return false;
    }

    private Collection<MarkovAction> findActionsThatDiffer(Collection<MarkovAction> one, Collection<MarkovAction> other) {
        return one.stream()
                .filter(markovAction -> markovAction.getReply().equals(new Reply(Reply.UNKNOWN))) //only keep the UNKNOWN
                .filter(markovAction -> !hasSameTermAndReply(markovAction, other))
//                .peek(e -> System.out.println("Should be unknown: " + e.getReply().getName()))
                .collect(Collectors.toList());
    }

    /*
     * Takes the currentState and a collection of all saved States
     * Streams through all saved States and runs #findActionsThatDiffer(currentStateMarkovActions, theComparingStatesMarkovActions)
     */
    private List<MarkovState> findPossibleStatesToGoTo(MarkovState currentState, Collection<MarkovState> allStates) {
        return allStates.stream()
                .filter(state -> findActionsThatDiffer(currentState.getMarkovActions(), state.getMarkovActions()).size() == 1)
//                .peek(e -> System.out.println("findPossibleStatesToGoTo State Id: " + e.getId()))
                .collect(Collectors.toList());
    }


    @Override
    public MarkovAction chooseNextAction(long markovStateId) {
        System.out.println("#################################");
        System.out.println("Choosing next action based on");
        System.out.println("MarkovState Id: " + markovStateId);

        MarkovState currentState = markovStateRepository.findById(markovStateId);
        System.out.println("Current State from repository: " + currentState.getId());
        Collection<MarkovState> allStates = markovStateRepository.findAll();
        System.out.println("All states from repository: " + allStates.size());
        List<MarkovState> listOfPossibleStatesToGoTo = findPossibleStatesToGoTo(currentState, allStates);
        System.out.println("Number of states that we can go to from here: " + listOfPossibleStatesToGoTo.size());

        MarkovState maxQValueState = Collections.max(listOfPossibleStatesToGoTo);
        System.out.println(maxQValueState.getQValue());

        MarkovAction nextAction = findActionsThatDiffer(currentState.getMarkovActions(), maxQValueState.getMarkovActions()).stream().findFirst().get();
        return nextAction;
    }
}


//        List<MarkovAction> listOfCurrentActions = new ArrayList<>(currentState.getMarkovActions());

//        System.out.println("Current Markov State:" + currentState.getId());
//        System.out.println("Size of list of current Markov Actions:" + listOfCurrentActions.size());

//        List<MarkovState> allMarkovStates = new ArrayList<>(stateCollection);

//        System.out.println("Number of MarkovStates in Database: " + allMarkovStates.size());

//        List<MarkovState> listOfPossibleStatesToGoTo = new ArrayList<>();
//        HashMap<MarkovState, MarkovAction> stateActionMap = new HashMap<>();
//        HashMap<MarkovState, MarkovAction> possibleStateActions = new HashMap<>();


//        for (MarkovState state : allMarkovStates) {
//
//            List<MarkovAction> actions = new ArrayList<>(state.getMarkovActions());
////            System.out.println("Number of actions in this specific State:" + actions.size());
////            System.out.println("Reply in first action: " + actions.get(147).getReply());
//            System.out.println("Current state in loop: " + state.getId());
//
//            int numberOfTermsWidthDifferentStatus = 0;
//            for (int i = 0; i < actions.size(); i++) {
//                if (!actions.get(i).getReply().equals(listOfCurrentActions.get(i).getReply())) {
////                    System.out.println("listOfCurrentActions.get(i).getReply(): " + listOfCurrentActions.get(i).getReply());
////                    System.out.println("actions.get(i).getReply(): " + actions.get(i).getReply());
//                    numberOfTermsWidthDifferentStatus++;
//                    stateActionMap.put(state, actions.get(i)); // update HashTable with relevant State and Term info
//                }
//                if (numberOfTermsWidthDifferentStatus > 1) {
//                    stateActionMap.clear(); // clear if more than one difference
//                    break;
//                }
//            }
//            if (numberOfTermsWidthDifferentStatus == 1) {
//                System.out.println(state.getQValue());
//                listOfPossibleStatesToGoTo.add(state);
//                possibleStateActions.put(state, stateActionMap.get(state));
//            }
//        }