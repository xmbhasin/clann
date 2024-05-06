package com.clann;

import com.clann.visitor.AnnotationCollector;
import com.clann.visitor.ClassAnnotationInfo;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides static methods that implement Java JAR file analysis and reporting features.
 * Currently, the focus is on analysis of Java class file annotations.
 */
public final class JarAnalyzer {
    private static final Logger LOGGER = LoggerFactory.getLogger(JarAnalyzer.class.getSimpleName());

    /**
     * Prevents instantiation with a private constructor.
     */
    private JarAnalyzer() {}

    /**
     * Analyze a Java JAR file and produce information about the annotations in each valid Java class file.
     * <p>
     * Java class files are identified by their extension: {@code class}.
     * This is a reasonable heuristic to avoid trying to parse files that are unlikely to be class files.
     *
     * @param jarPath The path to the JAR file to parse and analyze. The file is assumed to exist.
     * @return Information about the annotations for each class in the JAR file that was successfully parsed.
     * @throws IOException If there is an I/O or ZIP file error when reading the JAR file.
     */
    @NotNull
    public static List<ClassAnnotationInfo> analyzeJar(@NotNull final String jarPath)
            throws IOException {
        final File jarFile = new File(jarPath);
        return JarAnalyzer.analyzeJar(jarFile);
    }

    /**
     * Analyze a Java JAR file and produce information about the annotations in each valid Java class file.
     * <p>
     * Java class files are identified by their extension: {@code class}.
     * This is a reasonable heuristic to avoid trying to parse files that are unlikely to be class files.
     *
     * @param jarFile The JAR file to parse and analyze. The file is assumed to exist.
     * @return Information about the annotations for each class in the JAR file that was successfully parsed.
     * @throws IOException If there is an I/O or ZIP file error when reading the JAR file.
     */
    @NotNull
    public static List<ClassAnnotationInfo> analyzeJar(@NotNull final File jarFile)
            throws IOException {
        try (final JarInputStream jarInputStream =
                new JarInputStream(Files.newInputStream(jarFile.toPath()))) {
            return JarAnalyzer.analyzeJar(jarInputStream);
        }
    }

    /**
     * Analyze a Java JAR file and produce information about the annotations in each valid Java class file.
     * <p>
     * Java class files are identified by their extension: {@code class}.
     * This is a reasonable heuristic to avoid trying to parse files that are unlikely to be class files.
     *
     * @param jarBytes The JAR file bytes to parse and analyze.
     * @return Information about the annotations for each class in the JAR file that was successfully parsed.
     * @throws IOException If there is an I/O or ZIP file error when reading the JAR file.
     */
    @NotNull
    public static List<ClassAnnotationInfo> analyzeJar(@NotNull final byte[] jarBytes)
            throws IOException {
        try (final JarInputStream jarIn = new JarInputStream(new ByteArrayInputStream(jarBytes))) {
            return JarAnalyzer.analyzeJar(jarIn);
        }
    }

    /**
     * Analyze a Java JAR file and produce information about the annotations in each valid Java class file.
     * <p>
     * This method is private to ensure public alternatives guarantee the given {@code JarInputStream} is properly
     * constructed and closed.
     * <p>
     * Java class files are identified by their extension: {@code class}.
     * This is a reasonable heuristic to avoid trying to parse files that are unlikely to be class files.
     * <p>
     * We work with a {@code JarInputStream} instead of a {@code JarFile} because a {@code JarFile}
     * cannot be constructed with a byte array directly (that is without having to create a
     * temporary file) which is useful in tests.
     *
     * @param jarIn The JAR file input stream to parse and analyze.
     * @return Information about the annotations for each class in the JAR file that was successfully parsed.
     * @throws IOException If there is an I/O or ZIP file error when reading the JAR file.
     */
    @NotNull
    private static List<ClassAnnotationInfo> analyzeJar(@NotNull final JarInputStream jarIn)
            throws IOException {
        final List<ClassAnnotationInfo> classAnnotationInfos = new ArrayList<>();
        final List<String> failedClassFiles = new ArrayList<>();
        int entryCount = 0;

        // May throw IOException
        JarEntry entry = jarIn.getNextJarEntry();

        // JarInputStream only supports an iterator-style interface so we use a while loop here.
        // For each entry in the JarInputStream, if the entry is a class file, we read the file and
        // collect its annotations.
        while (entry != null) {
            entryCount++;

            final String entryName = entry.getName();
            JarAnalyzer.LOGGER.trace("got jar entry {}", entryName);

            if (entryName.endsWith(".class")) {
                // May throw IOException
                final byte[] buffer = jarIn.readAllBytes();

                try {
                    final ClassAnnotationInfo classAnnotationInfo =
                            AnnotationCollector.collectAnnotations(buffer);
                    classAnnotationInfos.add(classAnnotationInfo);
                } catch (final AnnotationCollector.ClannClassReaderException e) {
                    failedClassFiles.add(entryName + ": " + e.getMessage());
                }
            }

            // May throw IOException
            jarIn.closeEntry();
            // May throw IOException
            entry = jarIn.getNextJarEntry();
        }

        if (entryCount == 0) {
            JarAnalyzer.LOGGER.error(
                    "Failed to parse jar file. Found zero entries in jar file. Please check that"
                            + " the provided jar file is valid and has one or more class files.");
        } else {
            if (!failedClassFiles.isEmpty()) {
                JarAnalyzer.LOGGER.warn(
                        "Failed to parse annotations from 1 or more class files: {}",
                        failedClassFiles);
            }

            JarAnalyzer.LOGGER.info(
                    "Successfully parsed annotations from class files"
                            + " numberOfClassFilesSuccessfullyParsed={}",
                    classAnnotationInfos.size());
        }

        return classAnnotationInfos;
    }

    /**
     * Produce a report detailing the usage of annotations by Java classes.
     * <p>
     * Currently, a simple YAML-like format is manually constructed.
     * TODO: Consider using a serialization library like Jackson to serialize formally to JSON/YAML
     *
     * @param classAnnotationInfos The annotation usage information for some Java classes.
     * @return The report.
     */
    @NotNull
    public static String getClassAnnotationUsageReport(
            @NotNull final List<? extends ClassAnnotationInfo> classAnnotationInfos) {
        final StringBuilder stringBuilder = new StringBuilder();

        for (final ClassAnnotationInfo classAnnotationInfo : classAnnotationInfos) {
            if (classAnnotationInfo.getAnnotations().isEmpty()) {
                continue;
            }
            stringBuilder.append("\n- class: ").append(classAnnotationInfo.getClassName());
            classAnnotationInfo
                    .getAnnotations()
                    .forEach(
                            (annotationName, myAnnotation) -> {
                                stringBuilder.append("\n\t - ").append(annotationName);
                                stringBuilder.append("\n\t\t - ").append(myAnnotation.toString());
                            });
        }
        return stringBuilder.toString();
    }
}
