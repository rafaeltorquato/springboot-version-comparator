package com.torquato.springboot.vc.delivery.cli;

import picocli.CommandLine;

public class Main {

    public static void main(final String[] args) {
        final int exitCode = new CommandLine(new CompareCommand()).execute(args);
        System.exit(exitCode);
    }

}
