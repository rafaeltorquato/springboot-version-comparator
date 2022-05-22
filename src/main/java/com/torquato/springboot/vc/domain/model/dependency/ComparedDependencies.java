package com.torquato.springboot.vc.domain.model.dependency;

import java.util.List;

public record ComparedDependencies(String leftVersion,
                                   String rightVersion,
                                   List<ComparedDependency> dependencies) {
}
