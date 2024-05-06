package com.clann.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

import com.clann.App;
import com.clann.JarAnalyzer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/**
 * Tests end-2-end usage of the clann application's CLI.
 */
@SuppressWarnings("PMD.MethodNamingConventions")
class AppTest {
    private static final Path TEST_DATA_PATH = Path.of("src/test/java/com/clann/test/testdata");

    private static final ByteArrayOutputStream OUT_CONTENT = new ByteArrayOutputStream();
    private static final PrintStream ORIGINAL_STD_OUT = System.out;
    private static final ByteArrayOutputStream ERR_CONTENT = new ByteArrayOutputStream();
    private static final PrintStream ORIGINAL_STD_ERR = System.err;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(AppTest.OUT_CONTENT));
        System.setErr(new PrintStream(AppTest.ERR_CONTENT));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(AppTest.ORIGINAL_STD_OUT);
        System.setErr(AppTest.ORIGINAL_STD_ERR);
    }

    /**
     * Check that running the app succeeds when provided a valid JAR file path on the CLI within
     * a reasonable time limit.
     */
    @Test
    void runSucceeds_WithValidJarFile() {
        final String testJarFilePath =
                AppTest.TEST_DATA_PATH
                        .resolve("realJarFiles")
                        .resolve("netty-common-4.2.0.Final.jar")
                        .toString();

        assertTimeout(
                Duration.ofMillis(1000),
                () -> {
                    final int exitCode = App.run(testJarFilePath);
                    assertEquals(0, exitCode);

                    final String err = AppTest.ERR_CONTENT.toString();
                    assertThat(err)
                            .contains(
                                    "parsed annotations from class files"
                                            + " numberOfClassFilesSuccessfullyParsed=545");
                });
    }

    /**
     * Check that running the app fails with a non-zero exit code and the expected error message when a JAR file is
     * not provided in the first positional argument.
     */
    @Test
    void runFails_IfJarNotProvided() {
        final int exitCode = App.run();
        assertNotEquals(0, exitCode);

        final String err = AppTest.ERR_CONTENT.toString().toLowerCase(Locale.ROOT);
        assertThat(err).contains("missing required parameter");
    }

    /**
     * Check that running the app fails with a non-zero exit code and the expected error message when the provided JAR
     * file path is not an existing file.
     */
    @Test
    void runFails_IfJarIsNotFound() {
        final int exitCode = App.run("non-existent.jar");
        assertNotEquals(0, exitCode);
        final String err = AppTest.ERR_CONTENT.toString().toLowerCase(Locale.ROOT);
        assertThat(err).contains("jar file not found");
    }

    /**
     * Check that running the app fails with a non-zero exit code and the expected error message when too many
     * positional arguments are given.
     */
    @Test
    void runFails_IfTooManyArguments() {
        final int exitCode = App.run("non-existent.jar", "non-existent-2.jar");
        assertNotEquals(0, exitCode);

        final String err = AppTest.ERR_CONTENT.toString().toLowerCase(Locale.ROOT);
        assertThat(err).contains("unmatched argument");
    }

    /**
     * Check that running the app fails with a non-zero exit code with the expected error message
     * and without an uncaught exception when analyzing the JAR file throws an IOException.
     */
    @Test
    void runFails_IfJarCannotBeReadWithIOException() {
        try (final MockedStatic<JarAnalyzer> utilities = mockStatic(JarAnalyzer.class)) {
            final String testJarFilePath =
                    AppTest.TEST_DATA_PATH
                            .resolve("realJarFiles")
                            .resolve("netty-common-4.2.0.Final.jar")
                            .toString();
            final File testJarFile = new File(testJarFilePath);
            utilities
                    .when(() -> JarAnalyzer.analyzeJar(testJarFile))
                    .thenThrow(new IOException("Caught a mocked IOException"));

            final int exitCode = App.run(testJarFilePath);
            assertNotEquals(0, exitCode);

            final String err = AppTest.ERR_CONTENT.toString().toLowerCase(Locale.ROOT);
            assertThat(err).contains("error analyzing jar file");
            assertThat(err).contains("caught a mocked ioexception");
        }
    }
}
