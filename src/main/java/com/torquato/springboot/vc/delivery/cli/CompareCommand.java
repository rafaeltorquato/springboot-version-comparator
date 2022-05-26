package com.torquato.springboot.vc.delivery.cli;

import com.torquato.springboot.vc.application.ComparatorService;
import com.torquato.springboot.vc.application.out.FileOutputWriter;
import com.torquato.springboot.vc.application.report.ReportWriter;
import com.torquato.springboot.vc.domain.model.dependency.ComparedDependenciesFactory;
import com.torquato.springboot.vc.domain.model.dependency.DependenciesPairFactory;
import com.torquato.springboot.vc.infrastructure.ExternalDependenciesService;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@CommandLine.Command(name = "compare", description = "Compare two or more SpringBoot versions.")
public class CompareCommand implements Callable<Integer> {

    @CommandLine.Option(names = {"-v", "--versions"}, description = "SpringBoot versions. Ex: 2.4.3,2.6.7,...")
    private String versions;
    @CommandLine.Option(names = {"-o", "--outputDir"}, description = "Output directory")
    private String outputDir;

    @CommandLine.Option(names = {"-f", "--format"}, description = "Output format. Available: html | pdf")
    private String format;

    @Override
    public Integer call() throws Exception {
        int returnCode = 0;
        try {
            execute();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            returnCode = 1;
        }
        return returnCode;
    }

    private void execute() {
        final String normalizedFormat = Optional.ofNullable(this.format).orElse("html");
        final ComparatorService comparatorService = new ComparatorService(
                new ExternalDependenciesService(),
                new ComparedDependenciesFactory(),
                new DependenciesPairFactory(),
                ReportWriter.create(normalizedFormat),
                new FileOutputWriter(this.outputDir, normalizedFormat)
        );
        final Set<String> versionsSet = Stream.of(this.versions.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
        comparatorService.compare(versionsSet);
    }

}
