package com.torquato.springboot.vc;

import com.torquato.springboot.vc.application.ComparatorService;
import com.torquato.springboot.vc.application.report.HtmlReportWriter;
import com.torquato.springboot.vc.domain.model.dependency.ComparedDependenciesFactory;
import com.torquato.springboot.vc.domain.model.dependency.DependenciesPairFactory;
import com.torquato.springboot.vc.infrastructure.ExternalHtmlVersionService;

import java.util.Set;

public class Main {
    //TODO create cli flow
    public static void main(final String[] args) {
        if (!isValidArgs(args)) {
            printManual();
            return;
        }
        final ComparatorService comparatorService = new ComparatorService(
                new ExternalHtmlVersionService(),
                new ComparedDependenciesFactory(),
                new DependenciesPairFactory(),
                new HtmlReportWriter()
        );
        comparatorService.compare(Set.of("2.4.3", "2.6.7", "2.7.0"));
    }

    private static boolean isValidArgs(String[] args) {
        //TODO cli params validation
        return true;
    }

    private static void printManual() {
        //TODO cli Manual
    }

}
