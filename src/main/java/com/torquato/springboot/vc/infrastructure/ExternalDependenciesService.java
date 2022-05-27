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
                .doOnNext(ignored -> log.info("Fetching dependencies of Spring Boot version {}...", version))
                .map(this::doRequest)
                .map(html -> new VersionAndHtml(version, html))
                .doOnNext(ignored -> log.info("Fetched dependencies of Spring Boot version {}.", version))
                .flatMap(this::toDependencies)
                .blockingFirst();
    }

    @SneakyThrows
    private String doRequest(final String version) {
        final Connection connect = Jsoup.connect(String.format(VERSION_TEMPLATE_URL, version));
        return connect.execute().body();
    }

    private Observable<Dependencies> toDependencies(final VersionAndHtml vah) {
        return Observable.just(vah)
                .observeOn(Schedulers.computation())
                .doOnNext((v) -> log.info("Mapping dependencies of version {}...", v.version))
                .map(v -> new Dependencies(v.version(), deserialize(v)))
                .doOnNext((v) -> log.info("Mapping dependencies of version {} COMPLETED!", v.version()));
    }

    private List<VersionedDependency> deserialize(final VersionAndHtml vah) {
        final Document document = Jsoup.parse(vah.html());
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

    private VersionedDependency getVersionedDependency(final Element tr) {
        final var codes = tr.getElementsByTag("code");
        final String groupId = codes.get(0).html().trim();
        final String artifactId = codes.get(1).html().trim();
        final String artifactVersion = codes.get(2).html().trim();

        final var dependency = new Dependency(groupId, artifactId);
        return new VersionedDependency(dependency, artifactVersion);
    }

    private record VersionAndHtml(String version, String html) {}

}
