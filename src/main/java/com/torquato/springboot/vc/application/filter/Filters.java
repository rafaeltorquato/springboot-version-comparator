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

    public static Predicate<ComparedDependency> dependencyDiff(final Set<String> value) {
        return create(value, Filters::diff);
    }

    public static Predicate<ComparedDependency> dependencyArtifactId(final Set<String> values) {
        return create(values, Filters::artifactId);
    }

    public static Predicate<ComparedDependency> dependencyGroupId(final Set<String> values) {
        return create(values, Filters::groupId);
    }

    public static Predicate<ComparedDependency> dependency(final Set<String> diffValues,
                                                           final Set<String> groupIdValues,
                                                           final Set<String> artifactIdValues) {

        final Predicate<ComparedDependency> diffFilter = dependencyDiff(diffValues);
        final Predicate<ComparedDependency> groupIdFilter = dependencyGroupId(groupIdValues);
        final Predicate<ComparedDependency> artifactIdFilter = dependencyArtifactId(artifactIdValues);

        Predicate<ComparedDependency> filter = ALL;
        filter = diffValues.isEmpty() ? filter.or(diffFilter) : filter.and(diffFilter);
        filter = groupIdValues.isEmpty() ? filter.or(groupIdFilter) : filter.and(groupIdFilter);
        filter = artifactIdValues.isEmpty() ? filter.or(artifactIdFilter) : filter.and(artifactIdFilter);

        return filter;
    }

    private static Predicate<ComparedDependency> create(final Set<String> values,
                                                        final Function<String, Predicate<ComparedDependency>> filter) {
        return values
                .stream()
                .map(String::trim)
                .filter(Predicate.not(String::isBlank))
                .map(filter)
                .reduce(Filters.NONE, Predicate::or);
    }

}
