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
            currentQ = computeQ(previousQ, currentQ);

            state.setQValue(currentQ);
            markovStateRepository.save(state);
        }
    }

    /*
     * Calculates a the new Q-value
     */
    private double computeQ(double previousQ, double currentQ) {
        double q = (1 - LEARNING_RATE) * previousQ + LEARNING_RATE * GAMMA * currentQ;
        return q;
    }


    /*
     * Should remove the states that goes backwards (from NO or YES to UNKNOWN)
     */
    private Collection<MarkovAction> dontGoTheWrongWay(MarkovState currentState, MarkovState otherState) {

        return currentState.getMarkovActions().stream()
                .filter(markovAction -> !hasSameTermAndReply(markovAction, otherState.getMarkovActions()) && markovAction.getReply().equals(new Reply(Reply.UNKNOWN)))
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
            return true;
        }
        return false;
    }

    private Collection<MarkovAction> findActionsThatDiffer(Collection<MarkovAction> one, Collection<MarkovAction> other) {
        return one.stream()
                .filter(markovAction -> !hasSameTermAndReply(markovAction, other)) // cannot add && markovAction.getReply().equals(new Reply(Reply.UNKNOWN))
                .collect(Collectors.toList());
    }

    /*
     * Takes the currentState and a collection of all saved States
     * Streams through all saved States and runs #findActionsThatDiffer(currentStateMarkovActions, theComparingStatesMarkovActions)
     */
    private List<MarkovState> findPossibleStatesToGoTo(MarkovState currentState, Collection<MarkovState> allStates) {


        List<MarkovState> statesWithOneTermThatDiffer = allStates.stream()
                .filter(state -> findActionsThatDiffer(currentState.getMarkovActions(), state.getMarkovActions()).size() == 1)
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
    public void trainingAlgorithm() {

        MarkovState firstState = markovStateService.init();
        MarkovState secondState = firstState.copy();
        secondState = markovStateService.saveMarkovState(secondState);

        System.out.println("Beginning training...");

        // List of all Reports that we want to train with
        List<Report> listOfReports;
        listOfReports = reportRepository.findAll();

        // Epsilon is the threshold value where we want to make a random choice instead of choosing the State with the highest Q value
        double epsilon = 2;

        int numberOfRound = 1;
        for (int round = 0; round < numberOfRound; round++) {
            // Loop through all Reports
            for (Report report : listOfReports) {
                System.out.println("Number of reports: " + listOfReports.size());
                System.out.println("========>>>>>>> Current REPORT: " + report.getTitle());


                Deque<MarkovState> path = new LinkedList<>();
                long currentStateId = secondState.getId();

                win = false;
                while (!win) {
                    // Get current State
                    MarkovState currentState = markovStateRepository.findById(currentStateId);
                    path.addFirst(currentState);

                    // Get all saved MarkovStates
                    Collection<MarkovState> stateCollection = markovStateRepository.findAll();
                    List<MarkovState> allMarkovStates = new ArrayList<>(stateCollection);


                    // This is the list that will contain all States that are possible to go to from current State
                    List<MarkovState> listOfPossibleStatesToGoTo = findPossibleStatesToGoTo(currentState, allMarkovStates);
                    

                    // Make different choices depending if there are states to go to that has Q values or not
                    if (listOfPossibleStatesToGoTo.isEmpty()) {
                        currentStateId = goToRandomState(report, currentState, path);

                    } else { // search for the State with maximal Q value
                        MarkovState maxQValueState = Collections.max(listOfPossibleStatesToGoTo);


                        //must get maxQAction and maxQActionIsVulnerability to make a check in the epsilon if statement otherwise it might be a vulnerability and
                        //then set to NO even though it is the vulnerability in current report
                        MarkovAction maxQAction = findActionsThatDiffer(currentState.getMarkovActions(), maxQValueState.getMarkovActions()).stream().findFirst().get();
                        boolean maxQActionIsVulnerability = markovStateService.isVulnerability(maxQAction.getTerm());

                        if (maxQValueState.getQValue() < epsilon && !maxQActionIsVulnerability) {
                            currentStateId = goToRandomState(report, currentState, path);
                        } else {

                            MarkovAction maxQValueStateAction = maxQValueState.getMarkovActions().stream()
                                    .filter(action -> action.getTerm().equals(maxQAction.getTerm()))
                                    .findFirst()
                                    .get(); // this is the one that we should check if it is YES later

                            //if our reports reply matches the one in qmaxvaluestate, go there
                            if (report.getTerms().contains(maxQAction.getTerm())) {

                                // behöver kanske inte kolla maxQAction.getReply för den ska ju vara UNKNOWN nuinnan väl? JA, så verkar det vara
                                if (markovStateService.isVulnerability(maxQAction.getTerm()) && maxQValueStateAction.getReply().getName().equals("YES")) {
                                    System.out.println("WIN! could go to a State with a previously known vulnerability. In report: " + report.getTitle());
                                    path.addFirst(maxQValueState);
                                    updateQValues(path);
                                    win = true;
                                } else if (maxQValueStateAction.getReply().getName().equals("YES")) { //because a match happens on YES (if the term is present in the report it is YES)
                                    currentStateId = maxQValueState.getId();
                                } else { //our report was different than qmax, find next state
                                    //if state already exists, use that, otherwise create new
                                    currentStateId = goToNextState(currentState, maxQAction, listOfPossibleStatesToGoTo, "YES", path);
                                }
                            } else {
                                currentStateId = goToNextState(currentState, maxQAction, listOfPossibleStatesToGoTo, "NO", path);
                            }
                        }
                    }
                }
            }
        }
    }

    private long goToNextState(MarkovState currentState, MarkovAction selectedAction, Collection<MarkovState> possibleStatesToGoTo, String yesOrNo, Deque<MarkovState> path) {

        long stateId = currentState.getId();
        boolean found = false;
        Term t = selectedAction.getTerm();
        Reply r = new Reply(Reply.NO);
        for (MarkovState s : possibleStatesToGoTo) {
            MarkovAction a = findActionsThatDiffer(currentState.getMarkovActions(), s.getMarkovActions()).stream().findFirst().get();
            if (a.getReply().equals(r) && a.getTerm().equals(t)) {
                stateId = s.getId();
                found = true;
            }
        }


        if (!found) {

            MarkovState copy = currentState.copy();
            copy = markovStateService.saveMarkovState(copy);

            Collection<MarkovAction> actions = copy.getMarkovActions();

            // create the State that we want to go to
            if (yesOrNo.equalsIgnoreCase("YES")) {
                for (MarkovAction a : actions) {
                    if (a.getTerm().equals(selectedAction.getTerm())) {
                        a.setReply(new Reply(Reply.YES));
                    }
                }
                copy.setMarkovActions(actions);
                copy = markovStateRepository.save(copy);
                stateId = copy.getId();

                if (markovStateService.isVulnerability(selectedAction.getTerm())) {
                    System.out.println("WIN" + " (Go to next State) ");
                    path.addFirst(copy);
                    updateQValues(path);
                    win = true;
                }

            }
            if (yesOrNo.equalsIgnoreCase("NO")) {
                for (MarkovAction a : actions) {
                    if (a.getTerm().equals(selectedAction.getTerm())) {
                        a.setReply(new Reply(Reply.NO));
                    }
                }
                copy.setMarkovActions(actions);
                copy = markovStateRepository.save(copy);
                stateId = copy.getId();
            }

        }
        return stateId;
    }

    private long goToRandomState(Report report, MarkovState currentState, Deque<MarkovState> path) {


        long currentStateId = currentState.getId();
        MarkovState nextState = currentState.copy();
        nextState = markovStateService.saveMarkovState(nextState);

        Collection<MarkovAction> nextStateActions = nextState.getMarkovActions();


        List<MarkovAction> listOfNextStateActions = new ArrayList<>(nextStateActions);

        List<MarkovAction> onlyUnknownActions = new ArrayList<>();
        for (MarkovAction action : listOfNextStateActions) {
            if (action.getReply().getName().equals(Reply.UNKNOWN)) {
                onlyUnknownActions.add(action);
            }
        }

        Random rand = new Random();

        int bound = onlyUnknownActions.size();

        if (onlyUnknownActions.size() == 0) {
            win = true; //should have won if onlyUnknownActions.size() == 0
        }

        if (!win) {
            int randomIndex = rand.nextInt(bound);


            Long id = onlyUnknownActions.get(randomIndex).getId();

            MarkovAction randomAction = listOfNextStateActions.stream()
                    .filter(markovAction -> markovAction.getId() == id)
                    .findFirst()
                    .get();

            if (report.getTerms().contains(randomAction.getTerm())) {
                randomAction.setReply(new Reply(Reply.YES)); //TODO should this be set here or should it be in the if statement?

                if (markovStateService.isVulnerability(randomAction.getTerm())) {
                    System.out.println("WIN" + " (Got to random State) " + " from Report: " + report.getTitle());
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
        }
        return currentStateId;
    }

}
