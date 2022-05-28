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
        if (!diff.isBlank() && !groupId.isBlank() && !artifactId.isBlank()) {
            return diffFilter.and(groupIdFilter).and(artifactIdFilter);
        } else if (!diff.isBlank() && !groupId.isBlank()) {
            return diffFilter.and(groupIdFilter).or(artifactIdFilter);
        } else if (!diff.isBlank() && !artifactId.isBlank()) {
            return diffFilter.and(artifactIdFilter).or(groupIdFilter);
        } else if (!artifactId.isBlank() && !groupId.isBlank()) {
            return artifactIdFilter.and(groupIdFilter).or(diffFilter);
        }
        return diffFilter.or(groupIdFilter).or(artifactIdFilter);
    }

    private static Predicate<ComparedDependency> createFilters(final String value,
                                                               final Function<String, Predicate<ComparedDependency>> filter) {
        final String[] split = value.split(",");
        if (split.length == 0) return Filters.ALL;

        return Stream.of(split)
                .map(String::trim)
                .filter(Predicate.not(String::isBlank))
                .map(filter)
                .reduce(Filters.NONE, Predicate::or);
    }

}
