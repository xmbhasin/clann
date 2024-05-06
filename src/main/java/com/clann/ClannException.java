package com.clann;

import java.io.Serial;
import org.jetbrains.annotations.NotNull;

/**
 * Ancestor of all custom exceptions used by the clann application.
 */
public class ClannException extends Exception {

    @Serial private static final long serialVersionUID = -1555085312443006616L;

    /**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public ClannException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Returns the detail message string including the messages from the causes of this exception.
     *
     * @return the detail message string of this {@code ClannException} instance
     * including the messages from the causes of this exception.
     */
    @Override
    public @NotNull String getMessage() {
        final StringBuilder stringBuilder = new StringBuilder(super.getMessage());
        Throwable cause = getCause();
        while (cause != null) {
            stringBuilder.append(": ").append(cause.getMessage());
            cause = cause.getCause();
        }
        return stringBuilder.toString();
    }
}
