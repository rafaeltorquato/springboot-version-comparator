package com.torquato.springboot.vc.domain.service;

import com.torquato.springboot.vc.domain.model.dependency.Dependencies;
import io.reactivex.rxjava3.core.Observable;

public interface DependenciesService {

    Observable<Dependencies> fetch(final String version);

}
