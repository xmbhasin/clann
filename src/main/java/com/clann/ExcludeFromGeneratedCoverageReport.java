package com.clann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that should be used judiciously to exclude code from test coverage reports.
 * For example, one may want to exclude code that is generated, verified correct by means other than tests,
 * or because testing the code has been deferred until it is feasible.
 * <p>
 * This annotation includes the keyword {@code Generated} in its name because by default the JaCoCo
 * code coverage tool used in this repo excludes from coverage code that has been annotated with
 * an annotation including this keyword.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.TYPE})
public @interface ExcludeFromGeneratedCoverageReport {}
