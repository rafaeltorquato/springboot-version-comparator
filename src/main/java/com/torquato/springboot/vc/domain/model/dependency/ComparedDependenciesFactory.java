package com.torquato.springboot.vc.domain.model.dependency;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ComparedDependenciesFactory {

    private static final Comparator<ComparedDependency> DEPENDENCY_COMPARATOR =
            Comparator.comparing((Function<ComparedDependency, String>) cd -> cd.dependency().groupId())
                    .thenComparing(cd -> cd.dependency().artifactId());
    private static final String NONE_DIFF = "none";
    private static final String ADDED_DIFF = "added";
    private static final String REMOVED_DIFF = "removed";
    private static final String MAJOR_DIFF = "major";
    private static final String MINOR_DIFF = "minor";
    private static final String PATCH_DIFF = "patch";
    private static final String DOWNGRADED_DIFF = "downgraded";

    public ComparedDependencies create(final DependenciesPair pair) {
        log.info("Comparing {} with {}...", pair.left().version(), pair.right().version());
        final Map<Dependency, List<VersionedDependency>> leftVersionsMap = pair.left()
                .dependencies()
                .stream()
                .collect(Collectors.groupingBy(VersionedDependency::dependency));
        final Map<Dependency, List<VersionedDependency>> rightVersionsMap = pair.right()
                .dependencies()
                .stream()
                .collect(Collectors.groupingBy(VersionedDependency::dependency));

        final List<ComparedDependency> dependencies = Stream.of(pair.left(), pair.right())
                .map(Dependencies::dependencies)
                .flatMap(Collection::stream)
                .map(VersionedDependency::dependency)
                .collect(Collectors.toSet())
                .stream()
                .map(dependency -> {
                    final String leftVersion = Optional.ofNullable(leftVersionsMap.get(dependency))
                            .map(l -> l.get(0))
                            .map(VersionedDependency::version)
                            .orElse(NONE_DIFF);
                    final String rightVersion = Optional.ofNullable(rightVersionsMap.get(dependency))
                            .map(l -> l.get(0))
                            .map(VersionedDependency::version)
                            .orElse(NONE_DIFF);
                    final String diff = getDiff(leftVersion, rightVersion);
                    return new ComparedDependency(
                            dependency,
                            leftVersion,
                            rightVersion,
                            diff
                    );
                })
                .sorted(DEPENDENCY_COMPARATOR)
                .collect(Collectors.toList());
        log.info("Comparing {} with {} DONE.", pair.left().version(), pair.right().version());
        return new ComparedDependencies(pair.left().version(), pair.right().version(), dependencies);
    }

    private static String getDiff(final String leftVersion, final String rightVersion) {
        if (NONE_DIFF.equals(leftVersion)) return ADDED_DIFF;
        if (NONE_DIFF.equals(rightVersion)) return REMOVED_DIFF;

        final String[] leftNumbers = leftVersion.split("\\.");
        final String[] rightNumbers = rightVersion.split("\\.");
        try {
            final int majorDiff = Integer.parseInt(rightNumbers[0]) - Integer.parseInt(leftNumbers[0]);
            if (majorDiff > 0) {
                return MAJOR_DIFF + " +" + majorDiff;
            }
            final int minorDiff = Integer.parseInt(rightNumbers[1]) - Integer.parseInt(leftNumbers[1]);
            if (minorDiff > 0) {
                return MINOR_DIFF + " +" + minorDiff;
            }
            final int pathDiff = Integer.parseInt(rightNumbers[2]) - Integer.parseInt(leftNumbers[2]);
            if (pathDiff > 0) {
                return PATCH_DIFF + " +" + pathDiff;
            }

            if (majorDiff < 0 || minorDiff < 0 || pathDiff < 0) {
                return DOWNGRADED_DIFF;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
        }
        return NONE_DIFF;
    }

}
