package se.kth.moadb.haxonomysite.application.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import se.kth.moadb.haxonomysite.domain.MarkovAction;
import se.kth.moadb.haxonomysite.domain.MarkovState;
import se.kth.moadb.haxonomysite.domain.Reply;
import se.kth.moadb.haxonomysite.repository.MarkovActionRepository;
import se.kth.moadb.haxonomysite.repository.MarkovStateRepository;
import se.kth.moadb.haxonomysite.repository.ReplyRepository;
import se.kth.moadb.haxonomysite.repository.TermRepository;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class MarkovStateService {
    @Autowired
    MarkovStateRepository markovStateRepository;
    @Autowired
    TermRepository termRepository;
    @Autowired
    ReplyRepository replyRepository;
    @Autowired
    MarkovActionRepository markovActionRepository;
    @Autowired
    @Qualifier("qLearning")
    ActionChoosingAlgorithm actionChoosingAlgorithm;

    public MarkovState init() {

        /*
        MarkovState state = new MarkovState();
        state = markovStateRepository.save(state);


        MarkovState finalState = state;
        termRepository.findAll().forEach(
                term -> {
                    MarkovAction action = new MarkovAction();
                    action.setReply(replyRepository.findById(Reply.UNKNOWN).get());
                    action.setTerm(term);
                    //action.setMarkovState(finalState);
                    markovActionRepository.save(action);
                }
        );

        state = markovStateRepository.save(state);
        return state;
         */


        //STRATEGY:

        //1. INIT ALL POSSIBLE ACTIONS
        //for each term:
        //generate an MarkovAction that has reply YES, NO, and UNKNOWN

        //2. INIT ALL POSSIBLE STATES
        //create a state
        //loop over #actions ^ #terms (3^100 f.e) we need that many states
        //generate all states
        //

        //3. INIT Q MATRIX
        //for all states
        //and for all transitions to another possible state
        //generate a data point init to 0
        termRepository.findAll().forEach(
                term -> {
                    MarkovAction action = new MarkovAction();
                    action.setReply(replyRepository.findById(Reply.UNKNOWN).get());
                    action.setTerm(term);
                    markovActionRepository.save(action);
                }
        );
        MarkovState state = new MarkovState();
        state.setMarkovActions(markovActionRepository.findAll());

        return markovStateRepository.save(state);
    }

    public MarkovAction getNextActionForState(long stateId) {
       return actionChoosingAlgorithm.chooseNextAction(stateId);
    }
}
