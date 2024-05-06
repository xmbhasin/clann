package com.clann.test.util;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import java.util.stream.Stream;
import javax.tools.*;
import org.jetbrains.annotations.NotNull;

/**
 * Test helper class providing static methods to create a Java JAR file in memory from a Java source files directory.
 * Based on examples in https://docs.oracle.com/en/java/javase/21/docs/api/java.compiler/javax/tools/JavaCompiler.html.
 */
public final class InMemoryJarCompiler {
    /**
     * Prevents instantiation of utility class with a private constructor.
     */
    private InMemoryJarCompiler() {}

    /**
     * Create a Java JAR file in memory from a Java source files directory.
     *
     * @param sourceDirPath The path to the source directory to be compiled and packaged into the JAR file.
     * @return An in-memory Java JAR file.
     * @throws IOException On failure to read from the source directory or on writing to the in-memory JAR file.
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public static byte[] createJar(@NotNull final String sourceDirPath) throws IOException {
        // Find the .java files in the source directory
        final Path sourceDir = Paths.get(sourceDirPath);
        final List<Path> javaFiles;
        try (final Stream<Path> walk = Files.walk(sourceDir)) {
            javaFiles = walk.filter(p -> p.toString().endsWith(".java")).toList();
        }

        // Compile the java files
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final StandardJavaFileManager stdFileManager =
                compiler.getStandardFileManager(null, null, null);
        final InMemoryFileManager fileManager = new InMemoryFileManager(stdFileManager);

        final ByteArrayOutputStream jarOutBytes = new ByteArrayOutputStream();
        try (stdFileManager;
                fileManager) {
            final Iterable<? extends JavaFileObject> compilationUnits =
                    stdFileManager.getJavaFileObjectsFromPaths(javaFiles);
            final boolean success =
                    compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();
            if (!success) {
                throw new AssertionError("Compilation failed: sourceDirPath=" + sourceDirPath);
            }

            // Write compiled class files to an in-memory JAR
            try (final JarOutputStream jarOut = new JarOutputStream(jarOutBytes)) {
                for (final Map.Entry<String, byte[]> entry :
                        fileManager.getCompiledClasses().entrySet()) {
                    final String className = entry.getKey().replace('.', '/') + ".class";
                    final JarEntry jarEntry = new JarEntry(className);
                    jarOut.putNextEntry(jarEntry);
                    jarOut.write(entry.getValue());
                    jarOut.closeEntry();
                }
            }
        }

        return jarOutBytes.toByteArray();
    }

    private static class InMemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        private final Map<String, ByteArrayOutputStream> classNameToByteStream = new HashMap<>();

        InMemoryFileManager(@NotNull final JavaFileManager fileManager) {
            super(fileManager);
        }

        /*
         * Create an in-memory JavaFileObject for output.
         */
        @NotNull
        @Override
        public JavaFileObject getJavaFileForOutput(
                final Location location,
                @NotNull final String className,
                @NotNull final JavaFileObject.Kind kind,
                final FileObject sibling) {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            classNameToByteStream.put(className, outputStream);

            final URI memURI = URI.create("out:///" + className.replace('.', '/') + kind.extension);
            return new SimpleJavaFileObject(memURI, kind) {
                @NotNull
                @Override
                public OutputStream openOutputStream() {
                    return outputStream;
                }
            };
        }

        @NotNull
        public Map<String, byte[]> getCompiledClasses() {
            final Map<String, byte[]> result = new HashMap<>();
            classNameToByteStream.forEach((k, v) -> result.put(k, v.toByteArray()));
            return result;
        }
    }
}
