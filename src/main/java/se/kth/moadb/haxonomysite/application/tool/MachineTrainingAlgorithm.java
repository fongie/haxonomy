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
import java.util.stream.Stream;

/**
 * This class uses Q-learning to build a policy/map that can be used to guide the user
 * by choosing questions continuously leeds them to the highest Q-value state that they can enter
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


    /*
    * Updates Q-values for all states in a Path
     */
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

    /*
    * Calculates a the new Q-value
     */
    private double computeQ(double previousQ, double currentQ) {
        double q = (1-LEARNING_RATE) * previousQ + LEARNING_RATE * GAMMA * currentQ;
        return q;
    }



    /*
    * Should remove the states that goes backwards (from NO or YES to UNKNOWN)
     */
    private Collection<MarkovAction> dontGoTheWrongWay(MarkovState currentState, MarkovState otherState){

        return currentState.getMarkovActions().stream()
                .filter(markovAction -> !hasSameTermAndReply(markovAction, otherState.getMarkovActions()))
                .filter(markovAction -> markovAction.getReply().equals(new Reply(Reply.UNKNOWN)))
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
                .filter(markovAction -> !hasSameTermAndReply(markovAction, other))
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


    /**
     * Loops through all reports and finds new Paths through a series of States that leads to a vulnerability
     * These states are given a Q-value and are saved in the database
     */
    public void trainingAlgorithm(){

        MarkovState firstState = markovStateService.init();
        MarkovState secondState = firstState.copy();
        secondState = markovStateService.saveMarkovState(secondState);

        System.out.println("Beginning training...");

        // List of all Reports that we want to train with
        List<Report> listOfReports;
        listOfReports = reportRepository.findAll();

        // Epsilon is the threshold value where we want to make a random choice instead of choosing the State with the highest Q value
        double epsilon = 2;

        int numberOfRound = 20;
        for (int round = 0; round<numberOfRound; round++){
            // Loop through all Reports
            for (Report report : listOfReports){
                System.out.println("Number of reports: " + listOfReports.size());
                System.out.println("========>>>>>>> Current REPORT: " + report.getTitle());


                Deque<MarkovState> path = new LinkedList<>();
                long currentStateId = secondState.getId();

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
                    * Print list of possible states to see whats in it
                     */
//                    System.out.println("Possible States to go to: ");
//                    listOfPossibleStatesToGoTo.stream()
//                            .map(MarkovState::getId)
//                            .forEach(System.out::println);




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
                        System.out.println("State Id: " + s.getId() + ", Q-value: " + s.getQValue());

//                System.out.println("Before checking if there are any saved states that we can go to");
                    // Make different choices depending if there are states to go to that has Q values or not
                    if (listOfPossibleStatesToGoTo.isEmpty()) {
                        System.out.println("Possible states was empty");
                        currentStateId = goToRandomState(report, currentState, path);

                    } else { // search for the State with maximal Q value
                        MarkovState maxQValueState = Collections.max(listOfPossibleStatesToGoTo);
                        System.out.println("Max Q-value is found in State with Id: " + maxQValueState.getId());

                        System.out.println("The Actions in this max Q-value State contains the following replies/answers: ");
                        maxQValueState.getMarkovActions().stream()
                                .map(MarkovAction::getReply)
                                .forEach(System.out::println); // look whats in here

                        //must get maxQAction and maxQActionIsVulnerability to make a check in the epsilon if statement otherwise it might be a vulnerability and
                        //then set to NO even though it is the vulnerability in current report
                        MarkovAction maxQAction = findActionsThatDiffer(currentState.getMarkovActions(), maxQValueState.getMarkovActions()).stream().findFirst().get();
                        boolean maxQActionIsVulnerability = markovStateService.isVulnerability(maxQAction.getTerm());

                        if (maxQValueState.getQValue() < epsilon && !maxQActionIsVulnerability) {
                            System.out.println("The Q-value was smaller than Epsilon, choosing another path. Q-value was: " + maxQValueState.getQValue());
                            currentStateId = goToRandomState(report, currentState, path);
                        } else {
//                            MarkovAction maxQAction = findActionsThatDiffer(currentState.getMarkovActions(), maxQValueState.getMarkovActions()).stream().findFirst().get();
                            System.out.println("Term in maxQAction is: " + maxQAction.getTerm().getName());

                            long maxQActionId = maxQAction.getId();
//                            MarkovAction maxQValueStateAction_removeThisLastPart = maxQValueState.

//                            boolean isTrue = maxQValueState.getMarkovActions().stream()
//                                    .anyMatch(action -> action.getTerm().getName().equals("YES"));
                            MarkovAction maxQValueStateAction = maxQValueState.getMarkovActions().stream()
                                    .filter(action -> action.getTerm().equals(maxQAction.getTerm()))
                                    .findFirst()
                                    .get(); // this is the one that we should check if it is YES later

                            System.out.println("The reply/answer in this maxQActions is (YES or NO): " + maxQValueStateAction.getReply());


                            //if our reports reply matches the one in qmaxvaluestate, go there
                            if (report.getTerms().contains(maxQAction.getTerm())) {

//                                long reportId = maxQAction.getTerm().getId(); // save the id to be able to look in the report if it is YES later
//                                Optional<Term> sameTermInReport = report.getTerms().stream()
//                                        .filter(term -> term.equals(maxQAction.getTerm()))
//                                        .findFirst();

                                //MarkovAction maxQAction = possibleStateActions.get(maxQValueState);

//                                if (markovStateService.isVulnerability(maxQAction.getTerm()) && maxQAction.getReply().equals(new Reply(Reply.YES))){
                                //TODO behöver kanske inte kolla maxQAction.getReply för den ska ju vara UNKNOWN nuinnan väl? JA, så verkar det vara
//                                System.out.println("# # # # # # # # maxQAction.getReply: " + maxQAction.getReply());
                                if (markovStateService.isVulnerability(maxQAction.getTerm()) && maxQValueStateAction.getReply().getName().equals("YES")) {
                                    System.out.println("WIN! could go to a State with a previously known vulnerability. In report: )" + report.getTitle());
                                    path.addFirst(maxQValueState);
                                    updateQValues(path);
                                    win = true;
                                }
//                                } else if (maxQAction.getReply().equals(new Reply(Reply.YES))) { //TODO det här replyet kommer väl alltid vara UNKOWN också den är YES i rapporten för att vi är inne i loopen
                                else if (maxQValueStateAction.getReply().getName().equals("YES")) { //TODO because a match happens on YES (if the term is present in the report it is YES)
                                    System.out.println("I matched the one in maxvaluestate");
                                    currentStateId = maxQValueState.getId();
                                } else { //our report was different than qmax, find next state
                                    //if state already exists, use that, otherwise create new
                                    System.out.println("I didn't match the one in maxvaluestate");
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

        System.out.println("Going to next state, current state is: "  + currentState.getId());

        long stateId = 0;
        boolean found = false;
        Term t = selectedAction.getTerm();
        Reply r = new Reply(Reply.NO);
        for (MarkovState s : possibleStatesToGoTo) {
            MarkovAction a = findActionsThatDiffer(currentState.getMarkovActions(), s.getMarkovActions()).stream().findFirst().get();
            if (a.getReply().equals(r) && a.getTerm().equals(t)) {
                stateId = s.getId();
                found = true;
                System.out.println("Found (in possibleStatesToGoTo in goToNextState)");
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

        System.out.println("--> Going to a random State, current state: " + currentState.getId());
        currentState.getMarkovActions().stream()
                .map(MarkovAction::getReply)
                .forEach(e -> System.out.println("MarkovAction Reply (current state): " + e.getName()));

        long currentStateId;
//        System.out.println("Should go in here... No matching States");
        MarkovState nextState = currentState.copy();
        nextState = markovStateService.saveMarkovState(nextState);
//        System.out.println("Next state Id:" + nextState.getId());


        Collection<MarkovAction> nextStateActions = nextState.getMarkovActions();

//        nextStateActions.stream()
//                .map(MarkovAction::getId)
//                .forEach(e -> System.out.println("MarkovAction Id: " + e));
//        nextStateActions.stream()
//                .map(MarkovAction::getReply)
//                .forEach(e -> System.out.println("MarkovAction Reply: " + e.getName()));

        List<MarkovAction> listOfNextStateActions = new ArrayList<>(nextStateActions);

        List<MarkovAction> onlyUnknownActions = new ArrayList<>();
        for (MarkovAction action : listOfNextStateActions){
            if (action.getReply().getName().equals(Reply.UNKNOWN)){
                onlyUnknownActions.add(action);
            }
        }

        Random rand = new Random();

        int bound;
        if (onlyUnknownActions.size() == 0)
            bound = 1;
        else
            bound = onlyUnknownActions.size();

        int randomIndex = rand.nextInt(bound);
//        System.out.println("Random index:" + randomIndex);

        Long id = onlyUnknownActions.get(randomIndex).getId();
//        System.out.println("Action chosen from UNKNOWN actions: " + onlyUnknownActions.get(randomIndex).getTerm().getName());

        MarkovAction randomAction = listOfNextStateActions.stream()
                .filter(markovAction -> markovAction.getId() == id)
                .findFirst()
                .get();

        if (report.getTerms().contains(randomAction.getTerm())) {
            randomAction.setReply(new Reply(Reply.YES)); //TODO should this be set here or should it be in the if statement?

            if (markovStateService.isVulnerability(randomAction.getTerm())) {
                System.out.println("WIN" + " from Report: " + report.getTitle());
                path.addFirst(nextState);
                updateQValues(path);
                win = true;
            }
        } else {
            randomAction.setReply(new Reply(Reply.NO));
        }


        nextStateActions.stream()
                .map(MarkovAction::getTerm)
                .forEach(e -> System.out.println("MarkovAction Term: " + e));
        nextStateActions.stream()
                .map(MarkovAction::getReply)
                .forEach(e -> System.out.println("MarkovAction Reply: " + e.getName()));


        nextState.setMarkovActions(listOfNextStateActions);
        markovStateRepository.save(nextState); // save next State with actions
        currentStateId = nextState.getId();
        return currentStateId;

    }



}
