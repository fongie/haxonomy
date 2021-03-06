package se.kth.moadb.haxonomysite.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import se.kth.moadb.haxonomysite.application.tool.MarkovStateService;
import se.kth.moadb.haxonomysite.domain.MarkovAction;
import se.kth.moadb.haxonomysite.domain.MarkovState;

@RestController
public class MarkovController {

    @Autowired
    private MarkovStateService markovStateService;

    //returns the new state id (that user can later use to log in to where they were?)
    @PostMapping(value = "/tool/init")
    public MarkovState initState() {
        return markovStateService.init();
    }

    //returns the next action (question) to be answered by user
    @GetMapping(value = "/tool/{stateId}/next")
    public MarkovAction getNextAction(@PathVariable long stateId) {
        //TODO check if state exists, if not, throw exception

        //TODO maybe format the reports better (they are recursively returned)
       return markovStateService.getNextActionForState(stateId);
    }

    @PostMapping(value = "tool/learn")
    public String  runMachineTrainingAlgorithm(){
        String returnMessage = "I'm feeling smarter by the millisecond. More. Mooooore!";
        markovStateService.startTraining();
        return returnMessage;
    }

    @GetMapping(value = "/tool/mismatch/{stateId}/{actionStatus}/{termId}")
    public MarkovAction getActionFromNewPath(@PathVariable long stateId, @PathVariable String actionStatus, @PathVariable long termId){
        System.out.println("RECEIVED MISMATCH: " + stateId + " " + actionStatus + " " + termId);
        return markovStateService.getActionFromNewPath(stateId, actionStatus, termId);
    }
}
