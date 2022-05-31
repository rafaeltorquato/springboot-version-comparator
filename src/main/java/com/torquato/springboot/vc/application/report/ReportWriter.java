package com.torquato.springboot.vc.application.report;

import com.torquato.springboot.vc.domain.model.dependency.ComparedDependencies;

public interface ReportWriter {

    InMemoryReport write(final ComparedDependencies comparedDependencies);

    static ReportWriter create(String format) {
        if ("pdf".equalsIgnoreCase(format.trim())) {
            return new PdfReportWriter(new HtmlReportWriter());
        } else if ("html".equalsIgnoreCase(format.trim())) {
            return new HtmlReportWriter();
        } else if ("txt".equals(format.trim())) {
            return new TxtReportWriter();
        }
        throw new IllegalArgumentException("Unknown format " + format);
    }

}
