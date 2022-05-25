package com.torquato.springboot.vc.application.out;

import com.torquato.springboot.vc.application.report.InMemoryReport;
import com.torquato.springboot.vc.domain.model.dependency.ComparedDependencies;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;

@Slf4j
@RequiredArgsConstructor
public class FileOutputWriter implements OutputWriter {

    private final String outputDirectory;
    private final String fileExtension;

    @Override
    @SneakyThrows
    public void write(final InMemoryReport inMemoryReport) {
        final ComparedDependencies cpd = inMemoryReport.comparedDependencies();
        log.info("Writing report {}-with-{}...", cpd.leftVersion(), cpd.rightVersion());

        new File(this.outputDirectory).mkdirs();
        final var fileName = "/comp-" + cpd.leftVersion() + "-with-" + cpd.rightVersion() + "." + fileExtension;
        final File file = new File(this.outputDirectory + fileName);
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(inMemoryReport.report());
            log.info("Writing report {}-with-{} DONE. File '{}'.",
                    cpd.leftVersion(),
                    cpd.rightVersion(),
                    file.getAbsolutePath());
        }
    }

}
