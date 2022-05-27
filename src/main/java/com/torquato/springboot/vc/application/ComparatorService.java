package com.torquato.springboot.vc.application;

import com.torquato.springboot.vc.application.out.OutputWriter;
import com.torquato.springboot.vc.application.report.InMemoryReport;
import com.torquato.springboot.vc.application.report.ReportWriter;
import com.torquato.springboot.vc.domain.model.dependency.ComparedDependencies;
import com.torquato.springboot.vc.domain.model.dependency.ComparedDependenciesFactory;
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

@Slf4j
@RequiredArgsConstructor
public class ComparatorService {

    private final DependenciesService dependenciesService;
    private final ComparedDependenciesFactory comparedDependenciesFactory;
    private final DependenciesPairFactory dependenciesPairFactory;
    private final ReportWriter reportWriter;
    private final OutputWriter outputWriter;

    public void compare(final Set<String> versions) {
        final var start = Instant.now();

        Observable.fromIterable(versions)
                .flatMap(this::fetchVersion)
                .toList()
                .flattenAsObservable(this::createDependenciesPairs)
                .flatMap(this::compare)
                .flatMap(this::makeReport)
                .flatMap(this::writeOnOutput)
                .blockingSubscribe();

        log.info("Duration: {}ms", Duration.between(start, Instant.now()).toMillis());
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
