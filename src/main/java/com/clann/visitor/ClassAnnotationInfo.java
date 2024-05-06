package com.clann.visitor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

/**
 * Groups information about the annotations used in a class.
 */
public class ClassAnnotationInfo {
    private String className;

    /**
     * Mapping from annotation names to annotation details.
     */
    @NotNull private final Map<String, AnnotationDetails> annotations = new ConcurrentHashMap<>();

    /**
     * Sets the class name.
     *
     * @param className The class name.
     */
    public void setClassName(final String className) {
        this.className = className;
    }

    /**
     * Returns the class name.
     *
     * @return the class name.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Returns a mapping from annotation names to annotation details.
     *
     * @return Mapping from annotation names to annotation details.
     */
    @NotNull
    public Map<String, AnnotationDetails> getAnnotations() {
        return Collections.unmodifiableMap(annotations);
    }

    /**
     * Add an annotation to the annotations used by this class.
     *
     * @param annotationDescriptor The annotation descriptor. The descriptor must start with 'L' and end with ';'.
     * @param annotationLevel      The usage levels that this annotation has been seen in.
     */
    public void addAnnotation(
            @NotNull final String annotationDescriptor, final AnnotationLevel annotationLevel) {
        final String annotationName =
                ClassAnnotationInfo.annotationDescriptorToClassName(annotationDescriptor);
        this.annotations.putIfAbsent(annotationName, new AnnotationDetails());
        this.annotations.get(annotationName).addAnnotationLevel(annotationLevel);
    }

    /**
     * Convert a raw annotation descriptor string into a friendlier dot-separated class name.
     *
     * @param descriptor The raw annotation descriptor.
     * @return The friendly class name of the annotation.
     * @throws IllegalArgumentException If the annotation descriptor is not valid.
     *                                  That is, the descriptor does not start with 'L' and end with ';'.
     */
    @NotNull
    private static String annotationDescriptorToClassName(@NotNull final String descriptor) {
        if (descriptor.startsWith("L") && descriptor.endsWith(";")) {
            return "@" + descriptor.substring(1, descriptor.length() - 1).replace('/', '.');
        }
        throw new IllegalArgumentException("Not a valid annotation descriptor: " + descriptor);
    }

    /**
     * Container for details about the use of Java annotations in a class file.
     */
    public static class AnnotationDetails {
        /**
         * The usage levels that this annotation has been seen in.
         * For example, annotations can be seen at the class level, method level, or field level.
         * <p>
         * We use a TreeSet to ensure the annotation levels are stored in sorted order for consistency
         * in when serializing.
         */
        @NotNull private final Set<AnnotationLevel> annotationLevels = new TreeSet<>();

        /**
         * Add a usage level to the usage levels that this annotation has been seen in.
         *
         * @param level The usage level.
         */
        void addAnnotationLevel(final AnnotationLevel level) {
            annotationLevels.add(level);
        }

        @Override
        public String toString() {
            return annotationLevels.toString();
        }
    }
}
