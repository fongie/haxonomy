package se.kth.moadb.haxonomysite.application;

import org.springframework.beans.factory.annotation.Autowired;
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

    public MarkovState init() {

        MarkovState state = new MarkovState();
        state = markovStateRepository.save(state);

        //Collection<MarkovAction> actions = new ArrayList<>();

        MarkovState finalState = state;
        termRepository.findAll().forEach(
                term -> {
                    MarkovAction action = new MarkovAction();
                    action.setReply(replyRepository.findById(Reply.UNKNOWN).get());
                    action.setTerm(term);
                    action.setMarkovState(finalState);
                    markovActionRepository.save(action);
                    //actions.add(action);
                }
        );
        //state.setMarkovActions(actions);

        state = markovStateRepository.save(state);
        return state;
    }
}
