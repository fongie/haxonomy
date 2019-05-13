package se.kth.moadb.haxonomysite.application.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.kth.moadb.haxonomysite.domain.MarkovAction;
import se.kth.moadb.haxonomysite.domain.MarkovState;
import se.kth.moadb.haxonomysite.domain.Reply;
import se.kth.moadb.haxonomysite.repository.MarkovActionRepository;
import se.kth.moadb.haxonomysite.repository.MarkovStateRepository;
import se.kth.moadb.haxonomysite.repository.ReplyRepository;
import se.kth.moadb.haxonomysite.repository.TermRepository;

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
    ActionChoosingAlgorithm actionChoosingAlgorithm;
    @Autowired
    MachineTrainingAlgorithm machineTrainingAlgorithm;

    public MarkovState init() {

        MarkovState state = new MarkovState();
        state.setQValue(0);
        state = markovStateRepository.save(state);

        //Collection<MarkovAction> actions = new ArrayList<>();

        MarkovState finalState = state;
        termRepository.findAll().forEach(
                term -> {
                    MarkovAction action = new MarkovAction();
                    action.setReply(replyRepository.findById(Reply.UNKNOWN).get());
                    action.setTerm(term);
                    action.setMarkovState(finalState);
//                    action.setQValue(new QValue(0));
                    markovActionRepository.save(action);
//                    actions.add(action);
                }
        );
        state.setMarkovActions(markovActionRepository.findAllByMarkovState_Id(finalState.getId()));

        state = markovStateRepository.save(state);
        return state;
    }

    public MarkovAction getNextActionForState(long stateId) {
       return actionChoosingAlgorithm.chooseNextAction(stateId);
    }

    public void startTraining(){
        machineTrainingAlgorithm.trainingAlgorithm();
    }
}
