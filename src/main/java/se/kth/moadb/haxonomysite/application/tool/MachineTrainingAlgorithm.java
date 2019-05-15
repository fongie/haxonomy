package se.kth.moadb.haxonomysite.application.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.kth.moadb.haxonomysite.domain.*;
import se.kth.moadb.haxonomysite.repository.MarkovActionRepository;
import se.kth.moadb.haxonomysite.repository.MarkovStateRepository;
import se.kth.moadb.haxonomysite.repository.ReportRepository;
import se.kth.moadb.haxonomysite.repository.TermRepository;


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
    private static final double LEARNING_RATE = 0.1;
    private boolean win;

    private void updateQValues(Deque<MarkovState> path) {
        MarkovState state = path.removeFirst();
        double currentQ = WINVALUE;
        state.setQValue(currentQ);
        markovStateRepository.save(state);

        while (!path.isEmpty()) {
            state = path.removeFirst();
            double previousQ = state.getQValue();
            currentQ = computeQ(previousQ,currentQ);


            state.setQValue(currentQ);
            markovStateRepository.save(state);

        }
    }

    private double computeQ(double previousQ, double currentQ) {
        double q = (1-LEARNING_RATE) * previousQ + LEARNING_RATE * GAMMA * currentQ;
        return q;
    }

    private boolean hasSameTermAndReply(MarkovAction comparing, Collection<MarkovAction> collection) {

         MarkovAction selected = collection.stream()
                .filter(markovAction -> markovAction.getTerm().equals(comparing.getTerm()))
                .findFirst()
                .get();

                if (comparing.getReply().equals(selected.getReply())) {
                    return true;
                } else {
                    if (comparing.getReply().equals(new Reply(Reply.UNKNOWN)))
                        return false;
                    else
                        return true;
                }

    }

    private Collection<MarkovAction> findActionsThatDiffer(Collection<MarkovAction> one, Collection<MarkovAction> other) {
        return one.stream()
                .filter(markovAction -> !hasSameTermAndReply(markovAction, other))
                .collect(Collectors.toList());
    }

    private List<MarkovState> findPossibleStatesToGoTo(MarkovState currentState, Collection<MarkovState> allStates) {
        return allStates.stream()
                .filter(state -> findActionsThatDiffer(currentState.getMarkovActions(), state.getMarkovActions()).size() == 1)
                .collect(Collectors.toList());
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
        double epsilon = 2;

        int numberOfRound = 100;
        for (int round = 0; round<numberOfRound; round++){
            // Loop through all Reports
            for (Report report : listOfReports){
//            System.out.println("Number of reports: " + listOfReports.size());

                Deque<MarkovState> path = new LinkedList<>();
                long currentStateId = firstState.getId();

                win = false;
                while (!win) {
                    // Get current State and create a list of all Actions/Terms in that State

                    System.out.println("Current state is: " + currentStateId);
                    MarkovState currentState = markovStateRepository.findById(currentStateId);
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
                    //List<MarkovState> listOfPossibleStatesToGoTo = new ArrayList<>();
                    List<MarkovState> listOfPossibleStatesToGoTo = findPossibleStatesToGoTo(currentState, allMarkovStates);
                    // Term ID for the term we want to ask about, the Term Reply should be UNKNOWN before answering
                    /*
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
                    */

                    System.out.println("List of possible states to go to is: " + listOfPossibleStatesToGoTo.size());
                    for (MarkovState s : listOfPossibleStatesToGoTo)
                        System.out.println("State is: " + s.getId() + " q value " + s.getQValue());

//                System.out.println("Before checking if there are any saved states that we can go to");
                    // Make different choices depending if there are states to go to that has Q values or not
                    if (listOfPossibleStatesToGoTo.isEmpty()) {
                        System.out.println("Possible states was empty");
                        currentStateId = goToRandomState(report, currentState, path);

                    } else { // search for the State with maximal Q value
                        MarkovState maxQValueState = Collections.max(listOfPossibleStatesToGoTo);
                        System.out.println("Max Q value is state with id: " + maxQValueState.getId());

                        if (maxQValueState.getQValue() < epsilon) {
                            System.out.println("Under epsilon!! Qvalue was " + maxQValueState.getQValue());
                            currentStateId = goToRandomState(report, currentState, path);
                        } else {
                            MarkovAction maxQAction = findActionsThatDiffer(currentState.getMarkovActions(), maxQValueState.getMarkovActions()).stream().findFirst().get();
                            //MarkovAction maxQAction = possibleStateActions.get(maxQValueState);
                            System.out.println("Term in maxQAction is" + maxQAction.getTerm().getName());
                            //if our reports reply matches the one in qmaxvaluestate, go there
                            if (report.getTerms().contains(maxQAction.getTerm())) {
                                //MarkovAction maxQAction = possibleStateActions.get(maxQValueState);

                                if (markovStateService.isVulnerability(maxQAction.getTerm()) && maxQAction.getReply().equals(new Reply(Reply.YES))){
                                    path.addFirst(maxQValueState);
                                    System.out.println("epsilon WIN" + " from Report: " + report.getTitle());
                                    updateQValues(path);
                                    win = true;
                                } else if (maxQAction.getReply().equals(new Reply(Reply.YES))) {
                                    System.out.println("I matched the one in qmaxvaluestate");
                                    currentStateId = maxQValueState.getId();
                                } else { //our report was different than qmax, find next state
                                    //if state already exists, use that, otherwise create new
                                    System.out.println("I didnt match the one in qmaxvaluestate");
                                    currentStateId = goToNextState(currentState, maxQAction, listOfPossibleStatesToGoTo);
                                }
                            } else {
                                //System.out.println("NU LOOPAR VI FOREVER");
                                currentStateId = goToNextState(currentState, maxQAction, listOfPossibleStatesToGoTo);
                            }
                        }
                    }
                }
            }
        }
    }
    private long goToNextState(MarkovState currentState, MarkovAction selectedAction, Collection<MarkovState> possibleStatesToGoTo) {
        long stateId = 0;
        boolean found = false;
        Term t = selectedAction.getTerm();
        Reply r = new Reply(Reply.NO);
        for (MarkovState s : possibleStatesToGoTo) {
            MarkovAction a = findActionsThatDiffer(currentState.getMarkovActions(), s.getMarkovActions()).stream().findFirst().get();
            if (a.getReply().equals(r) && a.getTerm().equals(t)) {
                stateId = s.getId();
                found = true;
                System.out.println("Found");
            }
        }
        /*
        for (Map.Entry<MarkovState, MarkovAction> entry : possibleStateActions.entrySet()) {
            MarkovAction a = entry.getValue();
            if (a.getReply().equals(r) && a.getTerm().equals(t)) {
                stateId = entry.getKey().getId();
                found = true;
                System.out.println("Found");
            }
        }
        */
        if (!found) {
            System.out.println("Not Found");
            MarkovState copy = currentState.copy();
            copy = markovStateService.saveMarkovState(copy);

            Collection<MarkovAction> actions = copy.getMarkovActions();

            for (MarkovAction a : actions) {
                if (a.getTerm().equals(selectedAction.getTerm())) {
                    a.setReply(new Reply(Reply.NO));
                }
            }
            copy.setMarkovActions(actions);

            /*
            actions.stream()
                    .filter(action -> action.getTerm().equals(selectedAction.getTerm()))
                    .findFirst()
                    .get()
                    .setReply(new Reply(Reply.NO));
             */
            copy = markovStateRepository.save(copy);
            stateId = copy.getId();
        }
        return stateId;
    }

    private long goToRandomState(Report report, MarkovState currentState, Deque<MarkovState> path) {
        long currentStateId;
//        System.out.println("Should go in here... No matching States");
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

        MarkovAction randomAction = listOfNextStateActions.stream()
                .filter(markovAction -> markovAction.getId() == id)
                .findFirst()
                .get();

        if (report.getTerms().contains(randomAction.getTerm())) {
            randomAction.setReply(new Reply(Reply.YES));

            if (markovStateService.isVulnerability(randomAction.getTerm())) {
                System.out.println("WIN" + " from Report: " + report.getTitle());
                path.addFirst(nextState);
                updateQValues(path);
                win = true;
            }
        } else {
            randomAction.setReply(new Reply(Reply.NO));
        }

        nextState.setMarkovActions(listOfNextStateActions);
        markovStateRepository.save(nextState); // save next State with actions
        currentStateId = nextState.getId();
        return currentStateId;

    }



}
