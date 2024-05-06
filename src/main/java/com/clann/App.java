package com.clann;

import com.clann.visitor.ClassAnnotationInfo;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

/**
 * Implements the command-line interface of the clann application for parsing Java JAR files,
 * analyzing Java class files, and producing a report that details which classes use which annotations.
 */
@CommandLine.Command(
        name = "clann",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        description = "Analyze class files in a JAR file and print a report on annotation usage.")
public class App implements Callable<Integer> {
    private static final org.slf4j.Logger LOGGER =
            LoggerFactory.getLogger(App.class.getSimpleName());

    @CommandLine.Parameters(
            arity = "1",
            description = "Path to jar file to analyze",
            paramLabel = "<path-to-jar-file>")
    private File jarFile;

    /**
     * Implements this command line app conforming to the Callable interface.
     *
     * @return Exit code produced by the app.
     */
    @NotNull
    @Override
    @SuppressWarnings({"PMD.OnlyOneReturn", "PMD.SystemPrintln"})
    public Integer call() {
        if (!jarFile.isFile()) {
            App.LOGGER.error("JAR file not found: {}", jarFile);
            return 1;
        }

        final List<ClassAnnotationInfo> annotations;
        try {
            annotations = JarAnalyzer.analyzeJar(jarFile);
        } catch (final IOException e) {
            App.LOGGER.error("Error analyzing JAR file: {}", jarFile, e);
            return 2;
        }

        final String result = JarAnalyzer.getClassAnnotationUsageReport(annotations);
        System.out.println(result);

        return 0;
    }

    /**
     * Run this command line app with the given arguments.
     *
     * @param args Command line arguments for configuring the behaviour of the app.
     * @return Exit code produced by the app.
     */
    public static int run(final String... args) {
        return new CommandLine(new App()).execute(args);
    }
}
