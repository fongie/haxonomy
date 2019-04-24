package se.kth.moadb.haxonomysite.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import se.kth.moadb.haxonomysite.application.MarkovStateService;
import se.kth.moadb.haxonomysite.domain.MarkovState;

@RestController
public class MarkovController {

    @Autowired
    private MarkovStateService markovStateService;

    @GetMapping(value = "/markov/init")
    public MarkovState initState() {
        return markovStateService.init();
    }
}
