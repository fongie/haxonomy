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
 * Finds all States that we can go to and returns the State with the highest Q-value
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
            System.out.println("Max Q-value State Id: " + maxQValueState.getId());
            System.out.println("Max Q-value State Q-value: " + maxQValueState.getQValue());

            MarkovAction nextAction = findActionsThatDiffer(currentState.getMarkovActions(), maxQValueState.getMarkovActions()).stream().findFirst().get();
            System.out.println("Next Action: " + nextAction.getId());
            return maxQValueState.getMarkovActions().stream()
                    .filter(action -> action.getTerm().getId() == nextAction.getTerm().getId()).findFirst().get(); // it is the maxQValueStates maxAction that we want to return
        }
    }
}
