package com.torquato.springboot.vc.application.report;

import com.torquato.springboot.vc.domain.model.dependency.ComparedDependencies;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class HtmlReportWriter implements ReportWriter {
    private static final AbstractConfigurableTemplateResolver templateResolver;
    static {
        templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML");
    }

    @Override
    @SneakyThrows
    public void write(final ComparedDependencies comparedDependencies, final OutputStream outputStream) {
        //TODO separate template process from file writing

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context();
        context.setVariable("comparison", comparedDependencies);
        String html = templateEngine.process("/reports/report.html", context);
        outputStream.write(html.getBytes(StandardCharsets.UTF_8));
        outputStream.close();
    }

}
