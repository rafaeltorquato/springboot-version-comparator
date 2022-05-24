package com.torquato.springboot.vc.application.report;

import com.torquato.springboot.vc.domain.model.dependency.ComparedDependencies;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class HtmlReportWriter implements ReportWriter {

    private static final AbstractConfigurableTemplateResolver templateResolver;

    static {
        templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML");
    }

    private final String outputDirectory;

    @Override
    @SneakyThrows
    public void write(final ComparedDependencies cpd) {
        //TODO separate template process from file writing

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context();
        context.setVariable("comparison", cpd);
        String html = templateEngine.process("/reports/report.html", context);
        
        new File(this.outputDirectory).mkdirs();
        final var fileName = "/comp-" + cpd.leftVersion() + "-with-" + cpd.rightVersion() + ".html";
        final File file = new File(this.outputDirectory + fileName);
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(html.getBytes(StandardCharsets.UTF_8));
        outputStream.close();
        log.info("Report created at '{}'.", file.getAbsolutePath());

    }

}
