package com.torquato.springboot.vc.domain.model.dependency;

import java.util.List;

public record ComparedDependency(Dependency dependency,
                                 String leftVersion,
                                 String rightVersion,
                                 String diff) {
}
