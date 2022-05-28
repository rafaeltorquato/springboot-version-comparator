package com.torquato.springboot.vc.application;

import com.torquato.springboot.vc.application.out.OutputWriter;
import com.torquato.springboot.vc.application.report.InMemoryReport;
import com.torquato.springboot.vc.application.report.ReportWriter;
import com.torquato.springboot.vc.domain.model.dependency.ComparedDependencies;
import com.torquato.springboot.vc.domain.model.dependency.ComparedDependenciesFactory;
import com.torquato.springboot.vc.domain.model.dependency.ComparedDependency;
import com.torquato.springboot.vc.domain.model.dependency.Dependencies;
import com.torquato.springboot.vc.domain.model.dependency.DependenciesPair;
import com.torquato.springboot.vc.domain.model.dependency.DependenciesPairFactory;
import com.torquato.springboot.vc.domain.service.DependenciesService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ComparatorService {

    private final DependenciesService dependenciesService;
    private final ComparedDependenciesFactory comparedDependenciesFactory;
    private final DependenciesPairFactory dependenciesPairFactory;
    private final ReportWriter reportWriter;
    private final OutputWriter outputWriter;
    private final Predicate<ComparedDependency> comparedDependencyFilter;

    public void compare(final Set<String> versions) {
        final var start = Instant.now();

        Observable.fromIterable(versions)
                .flatMap(this::fetchVersion)
                .toList()
                .flattenAsObservable(this::createDependenciesPairs)
                .flatMap(this::compare)
                .map(this::applyFilters)
                .flatMap(this::makeReport)
                .flatMap(this::writeOnOutput)
                .blockingSubscribe();

        log.info("Duration: {}ms", Duration.between(start, Instant.now()).toMillis());
    }

    private ComparedDependencies applyFilters(final ComparedDependencies comparedDependencies) {
        final List<ComparedDependency> filteredDependencies = comparedDependencies.dependencies()
                .stream()
                .filter(this.comparedDependencyFilter)
                .collect(Collectors.toList());
        return new ComparedDependencies(
                comparedDependencies.leftVersion(),
                comparedDependencies.rightVersion(),
                filteredDependencies
        );
    }

    private Observable<Dependencies> fetchVersion(final String version) {
        return Observable.just(version)
                .subscribeOn(Schedulers.io())
                .map(this.dependenciesService::fetch);
    }

    private List<DependenciesPair> createDependenciesPairs(final List<Dependencies> dependencies) {
        return this.dependenciesPairFactory.createAscPairs(dependencies);
    }

    private Observable<ComparedDependencies> compare(final DependenciesPair pair) {
        return Observable.just(pair)
                .subscribeOn(Schedulers.computation())
                .map(this.comparedDependenciesFactory::create);
    }

    private Observable<InMemoryReport> makeReport(final ComparedDependencies dependencies) {
        return Observable.just(dependencies)
                .subscribeOn(Schedulers.computation())
                .map(this.reportWriter::write);
    }

    private Observable<Void> writeOnOutput(final InMemoryReport inMemoryReport) {
        return Observable.<Void>fromAction(() -> this.outputWriter.write(inMemoryReport))
                .subscribeOn(Schedulers.io());
    }

}
