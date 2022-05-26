package com.torquato.springboot.vc.application;

import com.torquato.springboot.vc.application.out.OutputWriter;
import com.torquato.springboot.vc.application.report.InMemoryReport;
import com.torquato.springboot.vc.application.report.ReportWriter;
import com.torquato.springboot.vc.domain.model.dependency.ComparedDependencies;
import com.torquato.springboot.vc.domain.model.dependency.ComparedDependenciesFactory;
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
        final List<DependenciesPair> pairs = Observable.fromIterable(versions)
                .flatMap(this.dependenciesService::fetch)
                .toList()
                .map(this.dependenciesPairFactory::createAscPairs)
                .blockingGet();
        Observable.fromIterable(pairs)
                .subscribeOn(Schedulers.computation())
                .map(this.comparedDependenciesFactory::create)
                .flatMap(this::computeReport)
                .flatMap(this::writeFile)
                .blockingSubscribe();

        log.info("Duration: {}ms", Duration.between(start, Instant.now()).toMillis());
    }

    private Observable<InMemoryReport> computeReport(final ComparedDependencies comparedDependencies) {
        return Observable.just(comparedDependencies)
                .subscribeOn(Schedulers.computation())
                .map(this.reportWriter::write);
    }

    private Observable<Void> writeFile(final InMemoryReport inMemoryReport) {
        return Observable.<Void>fromAction(() -> this.outputWriter.write(inMemoryReport))
                .subscribeOn(Schedulers.io());
    }

}
