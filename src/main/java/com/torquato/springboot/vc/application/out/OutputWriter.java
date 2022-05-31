package com.torquato.springboot.vc.application.out;

import com.torquato.springboot.vc.application.report.InMemoryReport;

public interface OutputWriter {

    void write(final InMemoryReport inMemoryReport);

    static OutputWriter create(final String output, final String format, final String outputDir) {
        if ("console".equals(output)) {
            return new ConsoleOutputWriter();
        } else if ("file".equals(output)) {
            return new FileOutputWriter(outputDir, format);
        }
        throw new IllegalArgumentException("Invalid output option " + output);

    }

}
