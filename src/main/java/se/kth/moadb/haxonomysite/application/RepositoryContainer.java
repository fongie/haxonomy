package se.kth.moadb.haxonomysite.application;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import se.kth.moadb.haxonomysite.repository.ReportRepository;
import se.kth.moadb.haxonomysite.repository.TermRepository;

import javax.annotation.PostConstruct;

@Data
@Configuration
public class RepositoryContainer {
    private static RepositoryContainer instance;
    @Autowired
    private TermRepository termRepository;
    @Autowired
    private ReportRepository reportRepository;

    @PostConstruct
    public void init() {
        instance = this;
    }

    public static RepositoryContainer getInstance() {
        return instance;
    }
}
