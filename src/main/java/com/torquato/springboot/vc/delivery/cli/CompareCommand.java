package com.torquato.springboot.vc.delivery.cli;

import com.torquato.springboot.vc.application.ComparatorService;
import com.torquato.springboot.vc.application.filter.Filters;
import com.torquato.springboot.vc.application.out.OutputWriter;
import com.torquato.springboot.vc.application.report.ReportWriter;
import com.torquato.springboot.vc.domain.model.dependency.ComparedDependenciesFactory;
import com.torquato.springboot.vc.domain.model.dependency.DependenciesPairFactory;
import com.torquato.springboot.vc.infrastructure.ExternalDependenciesService;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@CommandLine.Command(name = "compare", description = "Compare two or more SpringBoot versions.")
public class CompareCommand implements Callable<Integer> {

    @CommandLine.Option(
            required = true,
            names = {"-v", "--versions"},
            description = "Mandatory SpringBoot versions. Ex: 2.4.3,2.6.7,...")
    private String versions;
    @CommandLine.Option(
            required = true,
            names = {"-o", "--output"},
            description = "Output file or console. ")
    private String output;

    @CommandLine.Option(
            names = {"-od", "--outputDir"},
            description = "Output directory, mandatory if output is file. ")
    private String outputDir;

    @CommandLine.Option(
            names = {"-f", "--format"},
            description = "Output format. html | pdf | txt. Default is txt, html and pdf available only on file output",
            defaultValue = "txt")
    private String format;

    @CommandLine.Option(
            names = {"-df", "--diffFilter"},
            description = "Filter dependencies by diff. Works like contains. " +
                    "Available: added | removed | major | minor | patch | downgraded. Ex: added,major,removed,etc...",
            defaultValue = "")
    private String diffFilter;

    @CommandLine.Option(
            names = {"-gf", "--groupIdFilter"},
            description = "Filter compared dependencies by groupId. Works like contains. " +
                    "Ex: org.mockito,org.flywaydb,etc...",
            defaultValue = "")
    private String groupIdFilter;

    @CommandLine.Option(
            names = {"-af", "--artifactIdFilter"},
            description = "Filter dependencies by artifactId. Works like contains. " +
                    "Ex: kafka-shell,httpclient5,etc...",
            defaultValue = "")
    private String artifactIdFilter;

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
        if ("console".equals(this.output.trim()) && Set.of("html", "pdf").contains(this.format.trim())) {
            throw new IllegalArgumentException("Available only in file output.");
        }
        final ComparatorService comparatorService = new ComparatorService(
                new ExternalDependenciesService(),
                new ComparedDependenciesFactory(),
                new DependenciesPairFactory(),
                ReportWriter.create(this.format),
                OutputWriter.create(this.output, this.format, this.outputDir),
                Filters.comparedDependency(this.diffFilter, this.groupIdFilter, this.artifactIdFilter)
        );
        final Set<String> versionsSet = Stream.of(this.versions.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
        comparatorService.compare(versionsSet);
    }

}
