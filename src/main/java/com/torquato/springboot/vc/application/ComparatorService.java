package com.torquato.springboot.vc.application;

import com.torquato.springboot.vc.application.report.ReportWriter;
import com.torquato.springboot.vc.domain.model.dependency.ComparedDependencies;
import com.torquato.springboot.vc.domain.model.dependency.ComparedDependenciesFactory;
import com.torquato.springboot.vc.domain.model.dependency.Dependencies;
import com.torquato.springboot.vc.domain.model.dependency.DependenciesPair;
import com.torquato.springboot.vc.domain.model.dependency.DependenciesPairFactory;
import com.torquato.springboot.vc.domain.service.HtmlWithVersionService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class ComparatorService {

    private final HtmlWithVersionService htmlWithVersionService;
    private final ComparedDependenciesFactory comparedDependenciesFactory;
    private final DependenciesPairFactory dependenciesPairFactory;
    private final ReportWriter reportWriter;

    public void compare(final Set<String> versions) {
        final var start = Instant.now();
        final List<Dependencies> htmlWithVersions = Observable.fromIterable(versions)
                .flatMap(this.htmlWithVersionService::fetch)
                .toList()
                .blockingGet();
        final List<DependenciesPair> pairs = this.dependenciesPairFactory.createAscPairs(htmlWithVersions);

        Observable.fromIterable(pairs)
                .subscribeOn(Schedulers.computation())
                .map(this.comparedDependenciesFactory::create)
                .flatMap(this::toReport)
                .blockingSubscribe();

        log.info("Duration: {}ms", Duration.between(start, Instant.now()).toMillis());
    }

    @SneakyThrows
    private Observable<Void> toReport(final ComparedDependencies cpd) {
        Observable<Void> voidObservable = Observable.create(source -> {
            try {
                final var fileName = "comp-" + cpd.leftVersion() + "-with-" + cpd.rightVersion() + "-";
                final File tempFile = File.createTempFile(fileName, ".html");
                this.reportWriter.write(cpd, new FileOutputStream(tempFile));
                log.info("Report created at '{}'.", tempFile.getAbsolutePath());
            } catch (IOException e) {
                source.onError(e);
            }
            source.onComplete();
        });
        return voidObservable.subscribeOn(Schedulers.io());
    }

}
