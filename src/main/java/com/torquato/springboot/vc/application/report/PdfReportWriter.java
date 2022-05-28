package com.torquato.springboot.vc.application.report;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.torquato.springboot.vc.domain.model.dependency.ComparedDependencies;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;

@RequiredArgsConstructor
public class PdfReportWriter implements ReportWriter {

    private final HtmlReportWriter htmlReportWriter;

    @Override
    @SneakyThrows
    public InMemoryReport write(ComparedDependencies comparedDependencies) {
        final InMemoryReport html = htmlReportWriter.write(comparedDependencies);

        InMemoryReport pdf;
//        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//            ITextRenderer renderer = new ITextRenderer();
//            renderer.setDocumentFromString(new String(html.report()));
//            renderer.layout();
//            renderer.createPDF(out);
//            pdf = new InMemoryReport(out.toByteArray(), comparedDependencies);
//        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(new String(html.report()), "/");
            builder.toStream(out);
            builder.run();
            pdf = new InMemoryReport(out.toByteArray(), comparedDependencies);
        }

        return pdf;
    }

}
