package com.torquato.springboot.vc.domain.model.dependency;

import java.util.List;

public record ComparedDependency(Dependency dependency,
                                 List<String> versions,
                                 String diff) {
}
