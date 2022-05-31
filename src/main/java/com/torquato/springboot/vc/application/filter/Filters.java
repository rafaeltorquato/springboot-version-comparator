package com.torquato.springboot.vc.application.filter;

import com.torquato.springboot.vc.domain.model.dependency.ComparedDependency;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

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

    public static Predicate<ComparedDependency> comparedDependencyDiff(final Set<String> value) {
        return createFilters(value, Filters::diff);
    }

    public static Predicate<ComparedDependency> comparedDependencyArtifactId(final Set<String> values) {
        return createFilters(values, Filters::artifactId);
    }

    public static Predicate<ComparedDependency> comparedDependencyGroupId(final Set<String> values) {
        return createFilters(values, Filters::groupId);
    }

    public static Predicate<ComparedDependency> comparedDependency(final Set<String> diffValues,
                                                                   final Set<String> groupIdValues,
                                                                   final Set<String> artifactIdValues) {

        final Predicate<ComparedDependency> diffFilter = comparedDependencyDiff(diffValues);
        final Predicate<ComparedDependency> groupIdFilter = comparedDependencyGroupId(groupIdValues);
        final Predicate<ComparedDependency> artifactIdFilter = comparedDependencyArtifactId(artifactIdValues);

        Predicate<ComparedDependency> filter = ALL;
        filter = diffValues.isEmpty() ? filter.or(diffFilter) : filter.and(diffFilter);
        filter = groupIdValues.isEmpty() ? filter.or(groupIdFilter) : filter.and(groupIdFilter);
        filter = artifactIdValues.isEmpty() ? filter.or(artifactIdFilter) : filter.and(artifactIdFilter);

        return filter;
    }

    private static Predicate<ComparedDependency> createFilters(Set<String> values,
                                                               Function<String, Predicate<ComparedDependency>> filter) {
        return values
                .stream()
                .map(String::trim)
                .filter(Predicate.not(String::isBlank))
                .map(filter)
                .reduce(Filters.NONE, Predicate::or);
    }

}
