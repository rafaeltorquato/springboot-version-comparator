package com.torquato.springboot.vc.application.report;

import com.torquato.springboot.vc.domain.model.dependency.ComparedDependencies;

public interface ReportWriter {

    void write(final ComparedDependencies comparedDependencies);

}
