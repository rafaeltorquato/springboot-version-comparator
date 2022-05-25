package com.torquato.springboot.vc.delivery.cli;

import com.torquato.springboot.vc.application.ComparatorService;
import com.torquato.springboot.vc.application.out.FileOutputWriter;
import com.torquato.springboot.vc.application.report.HtmlReportWriter;
import com.torquato.springboot.vc.application.report.PdfReportWriter;
import com.torquato.springboot.vc.domain.model.dependency.ComparedDependenciesFactory;
import com.torquato.springboot.vc.domain.model.dependency.DependenciesPairFactory;
import com.torquato.springboot.vc.infrastructure.ExternalHtmlVersionService;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@CommandLine.Command(name = "compare", description = "Compare two or more SpringBoot versions.")
public class CompareCommand implements Callable<Integer> {

    @CommandLine.Option(names = {"-v", "--versions"}, description = "SpringBoot versions. Ex: 2.4.3, 2.6.7, ...")
    private String versions;
    @CommandLine.Option(names = {"-o", "--outputDir"}, description = "Output directory")
    private String outputDir;

    @Override
    public Integer call() throws Exception {
        try {
            final ComparatorService comparatorService = new ComparatorService(
                    new ExternalHtmlVersionService(),
                    new ComparedDependenciesFactory(),
                    new DependenciesPairFactory(),
                    new PdfReportWriter(new HtmlReportWriter()),
                    new FileOutputWriter(this.outputDir, "pdf")
            );
            final Set<String> versionsSet = Stream.of(this.versions.split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());
            comparatorService.compare(versionsSet);
            return 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 1;
        }
    }

}
