package com.torquato.springboot.vc.application.out;

import com.torquato.springboot.vc.application.report.InMemoryReport;

public interface OutputWriter {

    void write(final InMemoryReport inMemoryReport);

}
