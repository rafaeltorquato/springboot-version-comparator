package com.torquato.springboot.vc.application.out;

import com.torquato.springboot.vc.application.report.InMemoryReport;

import static java.lang.System.out;

public class ConsoleOutputWriter implements OutputWriter {

    @Override
    public synchronized void write(InMemoryReport inMemoryReport) {
        out.println(new String(inMemoryReport.report()));
    }

}
