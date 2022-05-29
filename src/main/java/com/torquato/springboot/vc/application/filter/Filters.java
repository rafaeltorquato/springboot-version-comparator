package com.torquato.springboot.vc.application.filter;

import com.torquato.springboot.vc.domain.model.dependency.ComparedDependency;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Filters {

    public static final Predicate<ComparedDependency> ALL = comparedDependencies -> true;

    public static final Predicate<ComparedDependency> NONE = comparedDependencies -> false;

    public static Predicate<ComparedDependency> diff(final String value) {
        return cd -> cd.diff().contains(value);
    }

    public static Predicate<ComparedDependency> artifactId(final String value) {
        return cd -> cd.dependency().artifactId().contains(value);
    }

    public static Predicate<ComparedDependency> groupId(final String value) {
        return cd -> cd.dependency().groupId().contains(value);
    }

    public static Predicate<ComparedDependency> comparedDependencyDiff(final String value) {
        return createFilters(value, Filters::diff);
    }

    public static Predicate<ComparedDependency> comparedDependencyArtifactId(final String value) {
        return createFilters(value, Filters::artifactId);
    }

    public static Predicate<ComparedDependency> comparedDependencyGroupId(final String value) {
        return createFilters(value, Filters::groupId);
    }

    public static Predicate<ComparedDependency> comparedDependency(final String diff,
                                                                   final String groupId,
                                                                   final String artifactId) {

        final Predicate<ComparedDependency> diffFilter = comparedDependencyDiff(diff);
        final Predicate<ComparedDependency> groupIdFilter = comparedDependencyGroupId(groupId);
        final Predicate<ComparedDependency> artifactIdFilter = comparedDependencyArtifactId(artifactId);

        Predicate<ComparedDependency> filter = ALL;
        filter = diff.isBlank() ? filter.or(diffFilter) : filter.and(diffFilter);
        filter = groupId.isBlank() ? filter.or(groupIdFilter) : filter.and(groupIdFilter);
        filter = artifactId.isBlank() ? filter.or(artifactIdFilter) : filter.and(artifactIdFilter);

        return filter;
    }

    private static Predicate<ComparedDependency> createFilters(String value,
                                                               Function<String, Predicate<ComparedDependency>> filter) {
        return Stream.of(value.split(","))
                .map(String::trim)
                .filter(Predicate.not(String::isBlank))
                .map(filter)
                .reduce(Filters.NONE, Predicate::or);
    }

}
