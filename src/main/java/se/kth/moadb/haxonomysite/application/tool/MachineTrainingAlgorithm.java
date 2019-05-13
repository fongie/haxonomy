package se.kth.moadb.haxonomysite.application.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.kth.moadb.haxonomysite.domain.*;
import se.kth.moadb.haxonomysite.repository.MarkovActionRepository;
import se.kth.moadb.haxonomysite.repository.MarkovStateRepository;
import se.kth.moadb.haxonomysite.repository.ReportRepository;
import se.kth.moadb.haxonomysite.repository.TermRepository;


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
public class MachineTrainingAlgorithm {

    @Autowired
    MarkovActionRepository markovActionRepository;
    @Autowired
    MarkovStateRepository markovStateRepository;
    @Autowired
    MarkovStateService markovStateService;
    @Autowired
    ReportRepository reportRepository;
    @Autowired
    TermRepository termRepository;


    /**
     * Finds all available reports and starts training
     */
    public void trainingAlgorithm(){

        markovStateService.init();

        System.out.println("Beginning training...");

        // List of all Reports that we want to train with
        List<Report> listOfReports; listOfReports = reportRepository.findAll();
        // Gamma value decides how big a reward should be in regard to the reward in the next State
        double gamma = 0.8;
        // Epsilon is the threshold value where we want to make a random choice instead of choosing the State with the highest Q value
        double epsilon = 20;
        // Initial MarkovStateId
        long markovStateId = 1;


        // Loop through all Reports
        for (Report report : listOfReports){
            System.out.println("Number of reports: " + listOfReports.size());

            // Get current State and create a list of all Actions/Terms in that State
            MarkovState currentState = markovStateRepository.findById(markovStateId); //TODO markovStateId must be updated each loop
            System.out.println("Current MarkovState: " + currentState.getId());
            Collection<MarkovAction> markovActions = markovActionRepository.findAllByMarkovState_Id(markovStateId);
            List<MarkovAction> listOfCurrentActions = new ArrayList<>();
            listOfCurrentActions.addAll(markovActions);
            System.out.println("Number of MarkovActions: " + listOfCurrentActions.size());

            // Get all currently saved MarkovStates
            Collection<MarkovState> stateCollection = markovStateRepository.findAll();
            List<MarkovState> allMarkovStates = new ArrayList<>();
            allMarkovStates.addAll(stateCollection);
            System.out.println("Number of saved MarkovStates: " + allMarkovStates.size());


            // This is the list that will contain all States that are possible to go to from current State
            List<MarkovState> listOfPossibleStatesToGoTo = new ArrayList<>();
            // Term ID for the term we want to ask about, the Term Reply should be UNKNOWN before answering
            HashMap<MarkovState, Term> termStateMap = new HashMap<>();

            // Checks if there are any states that we can go to that we been to before and therefore may have a Q value
            // Compare the states in this list to our current State
            for(MarkovState state : allMarkovStates){
                System.out.println("Markov State Id: " + state.getId());
                // Get all actions from this State
                List<MarkovAction> actions = new ArrayList<>();
                actions.addAll(state.getMarkovActions());

                int numberOfTermsWidthDifferentStatus = 0;
                for (int i=0; i<actions.size(); i++){
                    if (!actions.get(i).getReply().equals(listOfCurrentActions.get(i).getReply())){
                        numberOfTermsWidthDifferentStatus++;
                        termStateMap.put(state, actions.get(i).getTerm()); // update HashTable with relevant State and Term info
                    }
                    if (numberOfTermsWidthDifferentStatus > 1){
                        termStateMap.clear(); // clear if more than one difference
                        break;
                    }
                }
                if (numberOfTermsWidthDifferentStatus == 1){
                    listOfPossibleStatesToGoTo.add(state);
                }
            }

            // Make different choices depending if there are states to go to that has Q values or not
            if (listOfPossibleStatesToGoTo.isEmpty()){
                //TODO chose a random one
                MarkovState nextState = currentState.copy();
                Collection<MarkovAction> actions = nextState.getMarkovActions();
                List<MarkovAction> listOfActions = new ArrayList<>(actions);

                List<MarkovAction> listOfUnknownActions = new ArrayList<>();
                for (MarkovAction action : listOfActions){
                    if (action.getReply().getName().equals(Reply.UNKNOWN)){
                        listOfUnknownActions.add(action);
                    }
                }

                Random rand = new Random();
                int actionId = rand.nextInt(listOfUnknownActions.size());
                listOfUnknownActions.get(actionId).setReply(new Reply(Reply.YES));


            } else { // search for the State with maximal Q value
                MarkovState maxQValueState = Collections.max(listOfPossibleStatesToGoTo);

                if (maxQValueState.getQValue() < epsilon){
                    //TODO random choice
                }
                else {
                    // YES if match
                    if (report.getTerms().contains(termStateMap.get(maxQValueState))){
                        currentState = maxQValueState;
                    }
                }
            }
        }
    }


    public void updateQValues(List<MarkovState> states, Collection<MarkovState> stateCollection, MarkovAction currentAction, List<MarkovAction> actions){

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
