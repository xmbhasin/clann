package com.clann.visitor;

import com.clann.ClannException;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.*;

/**
 * Collects information about the annotations used in a compiled Java class.
 * <p>
 * Uses the {@link org.objectweb.asm} package to traverse elements of the compiled Java class and visit
 * annotations applied to these elements.
 * <p>
 * Uses {@link ClannClassVisitor}, {@link ClannFieldVisitor}, and {@link ClannMethodVisitor} to implement
 * the specific logic of visiting annotations across different class file elements.
 */
public final class AnnotationCollector {
    static final int ASM_API_VERSION = Opcodes.ASM9;

    /**
     * Prevents instantiation of utility class with a private constructor.
     */
    private AnnotationCollector() {}

    /**
     * Collect annotation information for the given compiled Java class.
     *
     * @param classBytes The given bytes representing the compiled Java class.
     * @return annotation information for the given compiled Java class.
     * @throws ClannClassReaderException If the class file bytes cannot be read and parsed.
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    @NotNull
    public static ClassAnnotationInfo collectAnnotations(@NotNull final byte[] classBytes)
            throws ClannClassReaderException {
        final ClassAnnotationInfo annotations = new ClassAnnotationInfo();

        final ClassReader reader;
        try {
            reader = new ClassReader(classBytes);
            // We catch a broad Exception here to protect against exceptions raised in the asm
            // library which we do
            // not control
        } catch (final Exception e) {
            throw new ClannClassReaderException("Failed to parse class file content", e);
        }

        reader.accept(new ClannClassVisitor(annotations), ClassReader.EXPAND_FRAMES);

        return annotations;
    }

    /**
     * Custom exception thrown when class file bytes cannot be read and parsed.
     */
    public static class ClannClassReaderException extends ClannException {

        /**
         * See {@link ClannException#ClannException(String, Throwable)}.
         */
        public ClannClassReaderException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
