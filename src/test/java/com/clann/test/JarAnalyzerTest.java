package com.clann.test;

import static com.diffplug.selfie.Selfie.expectSelfie;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.clann.JarAnalyzer;
import com.clann.test.util.InMemoryJarCompiler;
import com.clann.visitor.ClassAnnotationInfo;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link JarAnalyzer} utility class's public methods.
 */
@SuppressWarnings("PMD.MethodNamingConventions")
class JarAnalyzerTest {
    Path testDataPath = Path.of("src/test/java/com/clann/test/testdata");

    private static final ByteArrayOutputStream OUT_CONTENT = new ByteArrayOutputStream();
    private static final PrintStream ORIGINAL_STD_OUT = System.out;
    private static final ByteArrayOutputStream ERR_CONTENT = new ByteArrayOutputStream();
    private static final PrintStream ORIGINAL_STD_ERR = System.err;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(JarAnalyzerTest.OUT_CONTENT));
        System.setErr(new PrintStream(JarAnalyzerTest.ERR_CONTENT));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(JarAnalyzerTest.ORIGINAL_STD_OUT);
        System.setErr(JarAnalyzerTest.ORIGINAL_STD_ERR);
    }

    /**
     * Tests that compiling and analyzing a java source directory with annotations that cover all known
     * and supported annotation levels succeeds and produces the expected output report snapshot.
     */
    @Test
    void collectAnnotations_Succeeds_GivenSourceDirWithAllAnnotationLevels()
            throws java.io.IOException {
        final String srcFiles = testDataPath.resolve("sourceDirWithAllAnnotationLevels").toString();

        final byte[] jarBytes = InMemoryJarCompiler.createJar(srcFiles);
        final List<ClassAnnotationInfo> annotations = JarAnalyzer.analyzeJar(jarBytes);

        final String classAnnotationUsageReport =
                JarAnalyzer.getClassAnnotationUsageReport(annotations);

        // Annotations that are not visible in the compiled class file cannot be collected from the
        // class file
        assertThat(classAnnotationUsageReport).doesNotContain("@TestAnnotationSourceOnly");

        expectSelfie(classAnnotationUsageReport).toMatchDisk();
    }

    /**
     * Tests that compiling and analyzing an empty JAR file succeeds, producing empty annotation info.
     */
    @Test
    void collectAnnotations_Succeeds_GivenEmptyJarFile() throws java.io.IOException {
        final String emptyJarFilePath = testDataPath.resolve("testJarFiles/empty.jar").toString();

        final List<ClassAnnotationInfo> classAnnotationInfos =
                JarAnalyzer.analyzeJar(emptyJarFilePath);
        assertThat(classAnnotationInfos).isEmpty();
    }

    /**
     * Tests that compiling and analyzing an invalid JAR file partially succeeds, producing empty annotation info
     * and logging an error.
     */
    @Test
    void collectAnnotations_Fails_GivenInvalidJarFile() throws java.io.IOException {
        final String jarFilePath = testDataPath.resolve("testJarFiles/invalid.jar").toString();
        final List<ClassAnnotationInfo> annotations = JarAnalyzer.analyzeJar(jarFilePath);
        assertThat(annotations).isEmpty();

        final String err = JarAnalyzerTest.ERR_CONTENT.toString().toLowerCase(Locale.ROOT);
        Assertions.assertThat(err).contains("failed to parse jar file");
    }

    /**
     * Tests that compiling and analyzing an JAR file with both valid and invalid class files partially succeeds
     */
    @Test
    void collectAnnotations_Fails_GivenJarFileWithInvalidClassFile() throws java.io.IOException {
        final String jarFilePath =
                testDataPath.resolve("testJarFiles/with-invalid-class-file.jar").toString();

        final List<ClassAnnotationInfo> annotations = JarAnalyzer.analyzeJar(jarFilePath);
        assertThat(annotations).isEmpty();
    }
}
