package se.kth.moadb.haxonomysite.application.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.kth.moadb.haxonomysite.domain.MarkovAction;
import se.kth.moadb.haxonomysite.domain.MarkovState;
import se.kth.moadb.haxonomysite.repository.TermRepository;

@Component("qLearning")
public class QLearning implements ActionChoosingAlgorithm {
    @Autowired
    private TermRepository termRepository; //maybe load all terms at start to avoid a million repository calls

    //we need no R matrix because we can always fetch next R directly from db (reward is the same for a question no matter when it is asked)

    private int getReward(long termId) {
        int time = termRepository.findById(termId).get().getTime().getTime();
        return -time;
    }

    public void firstTimeMakeQMatrix() {
    }

    @Override
    public MarkovAction chooseNextAction(long markovStateId) {
        return null;
    }
}
