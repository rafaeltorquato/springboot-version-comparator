package com.torquato.springboot.vc;

import com.torquato.springboot.vc.delivery.cli.CompareCommand;
import picocli.CommandLine;

public class Main {

    public static void main(final String[] args) {
        int exitCode = new CommandLine(new CompareCommand()).execute(args);
        System.exit(exitCode);
    }

}
