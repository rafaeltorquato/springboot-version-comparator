package com.torquato.springboot.vc.application.out;

import com.torquato.springboot.vc.application.report.InMemoryReport;

public interface OutputWriter {

    void write(final InMemoryReport inMemoryReport);

    static OutputWriter create(final String output, final String format, final String outputDir) {
        return switch (output) {
            case "console" -> new ConsoleOutputWriter();
            case "file" -> new FileOutputWriter(outputDir, format);
            default -> throw new IllegalArgumentException("Invalid output option " + output);
        };
    }

}
