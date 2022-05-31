package com.torquato.springboot.vc.application.out;

import com.torquato.springboot.vc.application.report.InMemoryReport;

public class ConsoleOutputWriter implements OutputWriter {

    @Override
    public synchronized void write(InMemoryReport inMemoryReport) {
        System.out.println(new String(inMemoryReport.report()));
    }

}
