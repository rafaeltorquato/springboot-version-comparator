package com.torquato.springboot.vc.infrastructure;

import com.torquato.springboot.vc.domain.model.dependency.Dependencies;
import com.torquato.springboot.vc.domain.model.dependency.Dependency;
import com.torquato.springboot.vc.domain.model.dependency.VersionedDependency;
import com.torquato.springboot.vc.domain.service.DependenciesService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class ExternalDependenciesService implements DependenciesService {

    private static final String VERSION_TEMPLATE_URL = "https://docs.spring.io/spring-boot/docs/%s/reference/" +
            "html/dependency-versions.html";

    @Override
    public Dependencies fetch(final String version) {
        return Observable.just(version)
                .subscribeOn(Schedulers.io())
                .doOnNext(v -> log.info("Fetching dependencies of Spring Boot version {}...", v))
                .map(v -> new HtmlOfVersion(v, doRequest(v)))
                .doOnNext(v -> log.info("Fetched dependencies of Spring Boot version {}.", v.version()))
                .flatMap(this::toDependencies)
                .blockingFirst();
    }

    @SneakyThrows
    private static String doRequest(final String version) {
        final Connection connect = Jsoup.connect(String.format(VERSION_TEMPLATE_URL, version));
        return connect.execute().body();
    }

    private Observable<Dependencies> toDependencies(final HtmlOfVersion htmlOfVersion) {
        return Observable.just(htmlOfVersion)
                .observeOn(Schedulers.computation())
                .doOnNext((v) -> log.info("Mapping dependencies of version {}...", htmlOfVersion.version))
                .map(hwv -> new Dependencies(hwv.version(), getDependencies(hwv)))
                .doOnNext((v) -> log.info("Mapping dependencies of version {} COMPLETED!", htmlOfVersion.version));
    }

    private List<VersionedDependency> getDependencies(HtmlOfVersion hwv) {
        final Document document = Jsoup.parse(hwv.html());
        final Element body = document.body();
        final Elements tbody = body.getElementsByTag("tbody");
        final Optional<Element> firstTbody = Optional.ofNullable(tbody.first());
        if (firstTbody.isEmpty()) return Collections.emptyList();

        final Elements trs = firstTbody.get().getElementsByTag("tr");
        return trs
                .stream()
                .map(this::getVersionedDependency)
                .collect(Collectors.toList());
    }

    private VersionedDependency getVersionedDependency(Element tr) {
        final var codes = tr.getElementsByTag("code");
        final String groupId = codes.get(0).html().trim();
        final String artifactId = codes.get(1).html().trim();
        final String artifactVersion = codes.get(2).html().trim();

        final var dependency = new Dependency(groupId, artifactId);
        return new VersionedDependency(dependency, artifactVersion);
    }

    private record HtmlOfVersion(String version, String html) {}

}
