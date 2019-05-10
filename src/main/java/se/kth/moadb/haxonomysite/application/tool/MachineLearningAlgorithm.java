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

    private double gamma = 0.8;

    /* Add values to some terms to see if it works
    * In mysql terminal:
    * UPDATE q_value SET value=100 WHERE value=0 LIMIT 20;
    * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Autowired
    MarkovActionRepository markovActionRepository;

    @Autowired
    MarkovStateRepository markovStateRepository;

    // This is the list that will contain all States that are possible to go to from current State
    private List<MarkovState> listOfPossibleStatesToGoTo = new ArrayList<>();
    // Term ID for the term we want to ask about, the Term Reply should be UNKNOWN before answering
    private HashMap<MarkovState, Long> termInState = new HashMap<>();


    @Override
    public MarkovAction chooseNextAction(long markovStateId) {

        // We have the current MarkovState ID in the markovStateId variable
        // Current MarkovState is saved in currentState
        // A list of all Actions/Terms in the state are saved in listOfCurrentActions
        MarkovState currentState = markovStateRepository.findById(markovStateId);
        Collection<MarkovAction> markovActions = currentState.getMarkovActions();
        List<MarkovAction> listOfCurrentActions = new ArrayList<>();
        listOfCurrentActions.addAll(markovActions);

        // Get all currently saved MarkovStates
        Collection<MarkovState> stateCollection = markovStateRepository.findAll();
        List<MarkovState> allMarkovStates = new ArrayList<>();
        allMarkovStates.addAll(stateCollection);
        System.out.println(allMarkovStates.size());


        // All terms in this State that are still unknown (Reply = "UNKNOWN")
        Collection<MarkovAction> actionCollection = markovActionRepository.findAllByMarkovState_IdAndReply_Name(markovStateId, "UNKNOWN");
        List<MarkovAction> allUnknownActions = new ArrayList<>();
        allUnknownActions.addAll(actionCollection);


//        // This should give the same result as we got in listOfCurrentActions
//        // Also get all action, just based on MarkovState ID, disregarding Replies
//        Collection<MarkovAction> collectionOfAllActionsInCurrentState = markovActionRepository.findAllByMarkovState_Id(markovStateId);
//        List<MarkovAction> allActionsInCurrentState = new ArrayList<>();
//        allActionsInCurrentState.addAll(collectionOfAllActionsInCurrentState);


        // Checks if there are any states that we can go to that we been to before and therefore may have a Q value
        listOfPossibleStatesToGoTo.clear(); // remove the stats that we could go to last time
        updateListOfPossibleStatesToGoTo(listOfCurrentActions, allMarkovStates);

        // Make different choices depending if there are states to go to that has Q values or not
        if (listOfPossibleStatesToGoTo.isEmpty()){
            //TODO chose a random one
        } else { // search for the State with maximal Q value
            MarkovState maxQValueState = Collections.max(listOfPossibleStatesToGoTo);
            maxQValueState.getMarkovActions().stream().filter(termInState.);

        }

        //


        Random rand = new Random();
        int numActions = allUnknownActions.size();
        int actionId = rand.nextInt(numActions);

        updateQValues(allMarkovStates, stateCollection, allUnknownActions.get(actionId), allUnknownActions); // instead of updating Q matrix

        return allUnknownActions.get(actionId);
    }

    private void updateListOfPossibleStatesToGoTo(List<MarkovAction> listOfCurrentActions, List<MarkovState> allMarkovStates) {
        // Compare the states in this list to our current State
        for(MarkovState state : allMarkovStates){
            // Get all actions from this State
            List<MarkovAction> actions = new ArrayList<>();
            actions.addAll(state.getMarkovActions());

            int numberOfTermsWidthDifferentStatus = 0;
            for (int i=0; i<actions.size(); i++){
                if (!actions.get(i).getReply().equals(listOfCurrentActions.get(i).getReply())){
                    numberOfTermsWidthDifferentStatus++;
                    termInState.put(state, actions.get(i).getId()); // update HashTable with relevant State and Term info
                }
                if (numberOfTermsWidthDifferentStatus > 1){
                    termInState.clear(); // clear if more than one difference
                    break;
                }

            }
            if (numberOfTermsWidthDifferentStatus == 1){
                listOfPossibleStatesToGoTo.add(state);
            }
        }
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
