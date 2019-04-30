package se.kth.moadb.haxonomysite.application.tool;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import se.kth.moadb.haxonomysite.domain.MarkovAction;
import se.kth.moadb.haxonomysite.domain.MarkovState;
import se.kth.moadb.haxonomysite.domain.Reply;
import se.kth.moadb.haxonomysite.domain.Term;
import se.kth.moadb.haxonomysite.repository.MarkovActionRepository;
import se.kth.moadb.haxonomysite.repository.MarkovStateRepository;
import se.kth.moadb.haxonomysite.repository.ReplyRepository;
import se.kth.moadb.haxonomysite.repository.TermRepository;

import java.util.*;

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

    public void init() {

        //STRATEGY:

        //1. INIT ALL POSSIBLE ACTIONS
        //for each term:
        //generate an MarkovAction that has reply YES, NO, and UNKNOWN
       initMarkovActions();

        //2. INIT ALL POSSIBLE STATES
        //create a state
        //loop over #actions ^ #terms (3^100 f.e) we need that many states
        //generate all states
       initMarkovStates();


        //3. INIT Q MATRIX
        //for all states
        //and for all transitions to another possible state
        //generate a data point init to 0
    }
    private void initMarkovStates() {
       long numStates = (long) Math.pow(replyRepository.count(), termRepository.count());
       System.err.println(numStates);
       List<List<MarkovAction>> termActions = new ArrayList<>();
       termRepository.findAll().forEach(
             term -> {
                termActions.add(markovActionRepository.findAllByTerm(term));
             }
       );

       List<List<MarkovAction>> cartesianProduct = Lists.cartesianProduct(
             termActions
       );

       for (List<MarkovAction> actions : cartesianProduct) {
          MarkovState state = new MarkovState();
          state.setMarkovActions(actions);
          markovStateRepository.save(state);
       }
    }
   private void initMarkovActions() {
      termRepository.findAll().forEach(
            term -> {
               //UNKNOWN
               MarkovAction action = new MarkovAction();
               action.setReply(replyRepository.findById(Reply.UNKNOWN).get());
               action.setTerm(term);
               markovActionRepository.save(action);

               //YES
               action = new MarkovAction();
               action.setReply(replyRepository.findById(Reply.YES).get());
               action.setTerm(term);
               markovActionRepository.save(action);

               //NO
               action = new MarkovAction();
               action.setReply(replyRepository.findById(Reply.NO).get());
               action.setTerm(term);
               markovActionRepository.save(action);
            }
      );

   }

   public MarkovAction getNextActionForState(long stateId) {
      return actionChoosingAlgorithm.chooseNextAction(stateId);
   }
}
