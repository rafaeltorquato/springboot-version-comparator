package com.torquato.springboot.vc.delivery.cli;

import lombok.RequiredArgsConstructor;
import picocli.CommandLine;

@RequiredArgsConstructor
public class Main {

    private final String[] args;

    public static void main(final String[] args) {
        final Main main = new Main(args);
        main.execute();
    }

    public void execute() {
        final int exitCode = new CommandLine(new CompareCommand()).execute(this.args);
        System.exit(exitCode);
    }

}
