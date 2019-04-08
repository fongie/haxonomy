package se.kth.moadb.haxonomysite.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;
import se.kth.moadb.haxonomysite.domain.Report;
import se.kth.moadb.haxonomysite.domain.Term;
import se.kth.moadb.haxonomysite.repository.ReportRepository;
import se.kth.moadb.haxonomysite.repository.TermRepository;

import java.util.Optional;

@Component
public class OnSaveTermEntityListener extends AbstractRepositoryEventListener<Report> {
    @Autowired
    TermRepository termRepository;
    @Autowired
    ReportRepository reportRepository;

    @Override
    protected void onAfterCreate(Report report) {
        System.err.println("OMG SAVED REPORT " + report.getId());
        Optional<Term> term = termRepository.findById((long) 100);

        report.getTerms().add(term.get());

        reportRepository.save(report);
    }
}
