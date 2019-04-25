package se.kth.moadb.haxonomysite.application.tool;

import se.kth.moadb.haxonomysite.domain.MarkovAction;

public interface ActionChoosingAlgorithm {
   MarkovAction chooseNextAction(long markovStateId);
}
