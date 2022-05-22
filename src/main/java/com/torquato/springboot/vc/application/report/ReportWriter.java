package com.torquato.springboot.vc.application.report;

import com.torquato.springboot.vc.domain.model.dependency.ComparedDependencies;

import java.io.OutputStream;

public interface ReportWriter {

    void write(final ComparedDependencies comparedDependencies, final OutputStream outputStream);

}
