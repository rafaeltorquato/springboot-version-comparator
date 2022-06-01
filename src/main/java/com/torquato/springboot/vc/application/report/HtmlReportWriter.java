package com.torquato.springboot.vc.application.report;

import com.torquato.springboot.vc.domain.model.dependency.ComparedDependencies;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class HtmlReportWriter implements ReportWriter {

    //TODO make unit testable, remove static
    private static final AbstractConfigurableTemplateResolver templateResolver;

    static {
        templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML");
    }

    @Override
    @SneakyThrows
    public InMemoryReport write(final ComparedDependencies cpd) {
        log.info("Computing html report {}-with-{}...", cpd.leftVersion(), cpd.rightVersion());
        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        final Context context = new Context();
        context.setVariable("comparison", cpd);
        final String html = templateEngine.process("/reports/report.html", context);
        log.info("Computing html report {}-with-{} DONE.", cpd.leftVersion(), cpd.rightVersion());
        return new InMemoryReport(html.getBytes(StandardCharsets.UTF_8), cpd);
    }

}
