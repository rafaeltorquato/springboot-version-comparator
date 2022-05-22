package com.torquato.springboot.vc.domain.model.dependency;

import java.util.List;
import java.util.Objects;

public record Dependencies(String version, List<VersionedDependency> dependencies)
        implements Comparable<Dependencies> {

    @Override
    public int compareTo(Dependencies dependencies) {
        return this.version.compareTo(dependencies.version);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dependencies that = (Dependencies) o;
        return version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version);
    }

}
