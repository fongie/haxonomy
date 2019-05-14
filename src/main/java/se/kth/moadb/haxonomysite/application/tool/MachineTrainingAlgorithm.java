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

    private static final int WINVALUE = 100;
    private static final double GAMMA = 0.8;

    private void updateQValues(Deque<MarkovState> path) {
        MarkovState state = path.removeFirst();
        double currentQ = WINVALUE;
        state.setQValue(currentQ);
        markovStateRepository.save(state);

        while (!path.isEmpty()) {
            state = path.removeFirst();
            currentQ = currentQ * GAMMA;
            state.setQValue(currentQ);
            markovStateRepository.save(state);
        }
    }

    /**
     * Finds all available reports and starts training
     */
    public void trainingAlgorithm(){

        MarkovState firstState = markovStateService.init();

        System.out.println("Beginning training...");

        // List of all Reports that we want to train with
        List<Report> listOfReports; listOfReports = reportRepository.findAll();

        // Epsilon is the threshold value where we want to make a random choice instead of choosing the State with the highest Q value
        double epsilon = 20;

        // Loop through all Reports
        for (Report report : listOfReports){
//            System.out.println("Number of reports: " + listOfReports.size());

            Deque<MarkovState> path = new LinkedList<>();
            long currentStateId = firstState.getId();


            while (true) {
                // Get current State and create a list of all Actions/Terms in that State
                MarkovState currentState = markovStateRepository.findById(currentStateId); //TODO markovStateId must be updated each loop
                path.addFirst(currentState);

//                System.out.println("Current MarkovState: " + currentState.getId());
                Collection<MarkovAction> markovActions = currentState.getMarkovActions();
                List<MarkovAction> listOfCurrentActions = new ArrayList<>(markovActions);
//                System.out.println("Number of MarkovActions: " + listOfCurrentActions.size());

                // Get all saved MarkovStates
                Collection<MarkovState> stateCollection = markovStateRepository.findAll();
                List<MarkovState> allMarkovStates = new ArrayList<>(stateCollection);
//                System.out.println("Number of saved MarkovStates: " + allMarkovStates.size());


                // This is the list that will contain all States that are possible to go to from current State
                List<MarkovState> listOfPossibleStatesToGoTo = new ArrayList<>();
                // Term ID for the term we want to ask about, the Term Reply should be UNKNOWN before answering
                HashMap<MarkovState, MarkovAction> stateActionMap = new HashMap<>();
                HashMap<MarkovState, MarkovAction> possibleStateActions = new HashMap<>();

                // Checks if there are any states that we can go to that we been to before and therefore may have a Q value
                // Compare the states in this list to our current State
                for (MarkovState state : allMarkovStates) {
//                    System.out.println("Markov State Id: " + state.getId());
                    // Get all actions from this State
                    List<MarkovAction> actions = new ArrayList<>(state.getMarkovActions());

                    int numberOfTermsWidthDifferentStatus = 0;
                    for (int i = 0; i < actions.size(); i++) {
                        if (!actions.get(i).getReply().equals(listOfCurrentActions.get(i).getReply())) {
                            numberOfTermsWidthDifferentStatus++;
                            stateActionMap.put(state, actions.get(i)); // update HashTable with relevant State and Term info
                        }
                        if (numberOfTermsWidthDifferentStatus > 1) {
                            stateActionMap.clear(); // clear if more than one difference
                            break;
                        }
                    }
                    if (numberOfTermsWidthDifferentStatus == 1) {
//                        System.out.println("Chosen term: " + stateActionMap.get(state).getTerm().getName());
                        listOfPossibleStatesToGoTo.add(state);
                        possibleStateActions.put(state, stateActionMap.get(state));
//                        System.out.println("Number of different States that we can go to: " + listOfPossibleStatesToGoTo.size() +
//                                "State Id of item 0: " + listOfPossibleStatesToGoTo.get(0).getId());

                    }
                }

//                System.out.println("Before checking if there are any saved states that we can go to");
                // Make different choices depending if there are states to go to that has Q values or not
                if (listOfPossibleStatesToGoTo.isEmpty()) {
                    currentStateId = goToRandomState(report, currentState, path);

                } else { // search for the State with maximal Q value
                    MarkovState maxQValueState = Collections.max(listOfPossibleStatesToGoTo);

                    if (maxQValueState.getQValue() < epsilon) {
                        currentStateId = goToRandomState(report, currentState, path);
                    } else {
                        //if our reports reply matches the one in qmaxvaluestate, go there
                        if (report.getTerms().contains(possibleStateActions.get(maxQValueState).getTerm())) {
                            if (markovStateService.isVulnerability(possibleStateActions.get(maxQValueState).getTerm())){
                                System.out.println("WIN"); //TODO update Q values and so on
                                updateQValues(path);
                                break;
                            }
                            if (possibleStateActions.get(maxQValueState).getReply().equals(new Reply(Reply.YES))) {
                                currentStateId = maxQValueState.getId();
                            } else { //our report was different than qmax, find next state
                                //if state already exists, use that, otherwise create new
                                boolean found = false;
                                Term t = possibleStateActions.get(maxQValueState).getTerm();
                                Reply r = new Reply(Reply.YES);
                                for (Map.Entry<MarkovState, MarkovAction> entry : possibleStateActions.entrySet()) {
                                    MarkovAction a = entry.getValue();
                                    if (a.getReply().equals(r) && a.getTerm().equals(t)) {
                                        currentStateId = entry.getKey().getId();
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    MarkovState copy = currentState.copy();
                                    copy.getMarkovActions().stream()
                                            .filter(action -> action.getTerm().equals(possibleStateActions.get(maxQValueState).getTerm()))
                                            .findFirst()
                                            .get()
                                            .setReply(new Reply(Reply.YES));
                                    copy = markovStateService.saveMarkovState(copy);
                                    currentStateId = copy.getId();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private long goToRandomState(Report report, MarkovState currentState, Deque<MarkovState> path) {
        long currentStateId;
//        System.out.println("Should go in here... No matching States");
        //TODO chose a random one
        MarkovState nextState = currentState.copy();
        nextState = markovStateService.saveMarkovState(nextState);
//        System.out.println("Next state Id:" + nextState.getId());


        Collection<MarkovAction> nextStateActions = nextState.getMarkovActions();
        List<MarkovAction> listOfNextStateActions = new ArrayList<>(nextStateActions);

        List<MarkovAction> onlyUnknownActions = new ArrayList<>();
        for (MarkovAction action : listOfNextStateActions){
            if (action.getReply().getName().equals(Reply.UNKNOWN)){
                onlyUnknownActions.add(action);
            }
        }

        Random rand = new Random();
        int randomIndex = rand.nextInt(onlyUnknownActions.size());
//        System.out.println("Random index:" + randomIndex);

        Long id = onlyUnknownActions.get(randomIndex).getId();
//        System.out.println("Action chosen from UNKNOWN actions: " + onlyUnknownActions.get(randomIndex).getTerm().getName());


        for (MarkovAction action : listOfNextStateActions){
            if (action.getId() == id){
                if (report.getTerms().contains(action.getTerm())) {
                    // we found term
                    action.setReply(new Reply(Reply.YES));
                    //if term is vulnerability

                    if (markovStateService.isVulnerability(action.getTerm())){
                        System.out.println("WIN"); //TODO update Q values and so on
                        updateQValues(path);
                        break;
                    }
                } else {
                    action.setReply(new Reply(Reply.NO));
                }
//                System.out.println("Should be the same action chosen from the list containing all actions: " + action.getTerm().getName());
            }
        }
        nextState.setMarkovActions(listOfNextStateActions);
        markovStateRepository.save(nextState); // save next State
        currentStateId = nextState.getId();
        return currentStateId;
    }



}
