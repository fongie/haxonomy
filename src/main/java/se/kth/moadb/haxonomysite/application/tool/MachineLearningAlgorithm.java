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
     * Should remove the states that goes backwards (from NO or YES to UNKNOWN)
     */
    private Collection<MarkovAction> dontGoTheWrongWay(MarkovState currentState, MarkovState otherState){

        return currentState.getMarkovActions().stream()
                .filter(markovAction -> !hasSameTermAndReply(markovAction, otherState.getMarkovActions()) && markovAction.getReply().equals(new Reply(Reply.UNKNOWN)))
//                .filter(markovAction -> markovAction.getReply().equals(new Reply(Reply.UNKNOWN)))
                .collect(Collectors.toList());
    }

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
                .filter(markovAction -> !hasSameTermAndReply(markovAction, other)) // cannot add && markovAction.getReply().equals(new Reply(Reply.UNKNOWN))
//                .filter(markovAction -> markovAction.getReply().equals(new Reply(Reply.UNKNOWN))) //only keep the UNKNOWN TODO this can't be filtered here, results in wrong states beeing saved in possibleStatesToGoTo
//                .peek(e -> System.out.println("Should be unknown: " + e.getReply().getName()))
                .collect(Collectors.toList());
    }

    /*
     * Takes the currentState and a collection of all saved States
     * Streams through all saved States and runs #findActionsThatDiffer(currentStateMarkovActions, theComparingStatesMarkovActions)
     */
    private List<MarkovState> findPossibleStatesToGoTo(MarkovState currentState, Collection<MarkovState> allStates) {

        System.out.println("IN FUNCTION findPossibleStatesToGoTo");
        System.out.println("This is the actions in current State that was sen here");
        currentState.getMarkovActions().stream()
                .map(MarkovAction::getReply)
                .forEach(System.out::println);

        List<MarkovState> statesWithOneTermThatDiffer = allStates.stream()
                .filter(state -> findActionsThatDiffer(currentState.getMarkovActions(), state.getMarkovActions()).size() == 1)
//                .peek(e -> System.out.println("findPossibleStatesToGoTo State Id: " + e.getId()))
                .collect(Collectors.toList());


        // remove the ones that goes from YES to UNKNOWN or NO to UNKNOWN
        return statesWithOneTermThatDiffer.stream()
                .filter(state -> dontGoTheWrongWay(currentState, state).size() == 1)
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

        if (listOfPossibleStatesToGoTo.isEmpty()){
            MarkovAction noMoreSuggestions = currentState.getMarkovActions().stream().findFirst().get().copy();
            noMoreSuggestions.setId(999999999);
            return noMoreSuggestions;
        } else {
            MarkovState maxQValueState = Collections.max(listOfPossibleStatesToGoTo);
            System.out.println(maxQValueState.getQValue());

            MarkovAction nextAction = findActionsThatDiffer(currentState.getMarkovActions(), maxQValueState.getMarkovActions()).stream().findFirst().get();
            return nextAction;
        }
    }
}
