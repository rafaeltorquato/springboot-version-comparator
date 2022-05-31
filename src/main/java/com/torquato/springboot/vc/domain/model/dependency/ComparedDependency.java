package com.torquato.springboot.vc.domain.model.dependency;

public record ComparedDependency(Dependency dependency,
                                 String leftVersion,
                                 String rightVersion,
                                 String diff) {
}
