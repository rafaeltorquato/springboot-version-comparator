package com.torquato.springboot.vc.application.report;

import com.torquato.springboot.vc.domain.model.dependency.ComparedDependencies;
import com.torquato.springboot.vc.domain.model.dependency.ComparedDependency;
import com.torquato.springboot.vc.domain.model.dependency.Dependency;

import java.nio.charset.StandardCharsets;

public class TxtReportWriter implements ReportWriter {

    @Override
    public InMemoryReport write(final ComparedDependencies comparedDependencies) {
        final StringBuilder output = new StringBuilder();
        String template = "%1$-90s %2$-20s %3$-20s %4$-10s";
        output.append(String.format("COMPARISON BETWEEN %s AND %s VERSIONS\n",
                comparedDependencies.leftVersion(),
                comparedDependencies.rightVersion()));
        output.append(String.format(template + "\n", "DEPENDENCY", "LEFT", "RIGHT", "DIFF"));
        for (final ComparedDependency cd : comparedDependencies.dependencies()) {
            final Dependency dependency = cd.dependency();
            output.append(String.format(template + "\n",
                    dependency.groupId() + ":" + dependency.artifactId(),
                    cd.versions().get(0),
                    cd.versions().get(1),
                    cd.diff()));
        }
        return new InMemoryReport(output.toString().getBytes(StandardCharsets.UTF_8), comparedDependencies);
    }

}
