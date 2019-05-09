package se.kth.moadb.haxonomysite.application.taxonomy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;
import se.kth.moadb.haxonomysite.domain.Report;
import se.kth.moadb.haxonomysite.domain.Term;
import se.kth.moadb.haxonomysite.repository.ReportRepository;
import se.kth.moadb.haxonomysite.repository.TermRepository;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class OnSaveTermEntityListener extends AbstractRepositoryEventListener<Report> {
    @Autowired
    TermRepository termRepository;
    @Autowired
    ReportRepository reportRepository;

    /** Add all broader terms to the report as well
     *
     * @param report
     */
    @Override
    protected void onAfterCreate(Report report) {
        Collection<Term> broaderTerms = new ArrayList<>();
        report.getTerms().forEach(
                term -> addBroaderTerms(broaderTerms, term)
        );
        broaderTerms.stream()
                .filter(term -> !report.getTerms().contains(term))
                .forEach(report.getTerms()::add);

        reportRepository.save(report);
    }

    private void addBroaderTerms(Collection<Term> collection, Term term) {
        if (term.getBroaderTerm() == null) {
            return;
        }
        collection.add(term.getBroaderTerm());
        addBroaderTerms(collection, term.getBroaderTerm());
    }
}
