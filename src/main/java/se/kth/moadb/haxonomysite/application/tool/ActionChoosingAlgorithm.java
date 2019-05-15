package se.kth.moadb.haxonomysite.application.tool;

import se.kth.moadb.haxonomysite.domain.MarkovAction;

public interface ActionChoosingAlgorithm {
   MarkovAction chooseNextAction(long markovStateId); //TODO i frontned kontroller om svaret motsvarar det som skickades YES = YES eller NO = NO i så fall chooseNextAction med nästa det ID som står i actionen
}
