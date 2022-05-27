package com.torquato.springboot.vc.domain.service;

import com.torquato.springboot.vc.domain.model.dependency.Dependencies;

public interface DependenciesService {

    Dependencies fetch(final String version);

}
