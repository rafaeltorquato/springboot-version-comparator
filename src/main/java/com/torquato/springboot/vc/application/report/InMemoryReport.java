package com.torquato.springboot.vc.application.report;

import com.torquato.springboot.vc.domain.model.dependency.ComparedDependencies;

public record InMemoryReport(byte[] report, ComparedDependencies comparedDependencies) {}
