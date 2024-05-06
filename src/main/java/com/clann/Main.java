package com.clann;

/**
 * Serves as the entry point for the clann application.
 */
@SuppressWarnings("PMD.ShortClassName")
public final class Main {

    /**
     * Prevents instantiation of utility class with a private constructor.
     */
    @ExcludeFromGeneratedCoverageReport
    private Main() {}

    /**
     * The main method is the entry point for the clann application.
     *
     * @param args The command-line arguments provided when running the application.
     */
    @ExcludeFromGeneratedCoverageReport
    public static void main(final String[] args) {
        final int exitCode = App.run(args);
        System.exit(exitCode);
    }
}
