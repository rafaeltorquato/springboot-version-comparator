package com.torquato.springboot.vc.domain.model.dependency;

public record VersionedDependency(Dependency dependency, String version) {
}
