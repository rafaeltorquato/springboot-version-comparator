package com.torquato.springboot.vc.application.report;

import com.torquato.springboot.vc.domain.model.dependency.ComparedDependencies;

public interface ReportWriter {

    InMemoryReport write(final ComparedDependencies comparedDependencies);

    static ReportWriter create(final String format) {
        return switch (format) {
            case "pdf" -> new PdfReportWriter(new HtmlReportWriter());
            case "html" -> new HtmlReportWriter();
            case "txt" -> new TxtReportWriter();
            default -> throw new IllegalArgumentException("Unknown format " + format);
        };
    }

}
