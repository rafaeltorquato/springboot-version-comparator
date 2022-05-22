package com.torquato.springboot.vc.domain.model.dependency;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComparedDependenciesFactory {

    public ComparedDependencies create(DependenciesPair pair) {
        final Set<Dependency> allDependencies = Stream.of(pair.left(), pair.right())
                .map(Dependencies::dependencies)
                .flatMap(Collection::stream)
                .map(VersionedDependency::dependency)
                .collect(Collectors.toSet());
        final Map<Dependency, List<VersionedDependency>> leftVersions = pair.left()
                .dependencies()
                .stream()
                .collect(Collectors.groupingBy(VersionedDependency::dependency));
        final Map<Dependency, List<VersionedDependency>> rightVersions = pair.right()
                .dependencies()
                .stream()
                .collect(Collectors.groupingBy(VersionedDependency::dependency));

        final Comparator<ComparedDependency> dependencyComparator =
                Comparator.comparing((Function<ComparedDependency, String>) cd -> cd.dependency().groupId())
                        .thenComparing(cd -> cd.dependency().artifactId());
        final List<ComparedDependency> dependencies = allDependencies
                .stream()
                .map(dependency -> {
                    final List<VersionedDependency> leftDependencies = leftVersions.get(dependency);
                    final List<VersionedDependency> rightDependencies = rightVersions.get(dependency);
                    final String leftVersion = leftDependencies != null ? leftDependencies.get(0).version() : "none";
                    final String rightVersion = rightDependencies != null ? rightDependencies.get(0).version() : "none";
                    final String diff = getDiff(leftVersion, rightVersion);
                    return new ComparedDependency(
                            dependency,
                            List.of(leftVersion, rightVersion),
                            diff
                    );
                })
                .sorted(dependencyComparator)
                .collect(Collectors.toList());
        return new ComparedDependencies(pair.left().version(), pair.right().version(), dependencies);
    }

    private String getDiff(final String leftVersion, final String rightVersion) {
        if ("none".equals(leftVersion)) return "added";
        if ("none".equals(rightVersion)) return "removed";

        final String[] leftNumbers = leftVersion.split("\\.");
        final String[] rightNumbers = rightVersion.split("\\.");
        try {
            int numberDiff = Math.abs(Integer.parseInt(rightNumbers[0]) - Integer.parseInt(leftNumbers[0]));
            if (numberDiff > 0) {
                return "major +" + numberDiff;
            }
            numberDiff = Math.abs(Integer.parseInt(rightNumbers[1]) - Integer.parseInt(leftNumbers[1]));
            if (numberDiff > 0) {
                return "minor +" + numberDiff;
            }
            numberDiff = Math.abs(Integer.parseInt(rightNumbers[2]) - Integer.parseInt(leftNumbers[2]));
            if (numberDiff > 0) {
                return "patch +" + numberDiff;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
        }
        return "none";
    }

}
