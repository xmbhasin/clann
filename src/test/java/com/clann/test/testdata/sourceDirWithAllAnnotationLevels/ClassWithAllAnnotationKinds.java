package com.clann.test.testdata.sourceDirWithAllAnnotationLevels;

import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

// Class-Level Annotation
@TestAnnotationSourceOnly
@Deprecated
class ClassWithAllAnnotationKinds<@TestAnnotationCritical T extends @TestAnnotationCritical List>
        implements @TestAnnotationCritical Runnable {
    // Field-Level Annotation
    @Deprecated
    public static final /* Field-Level Type Annotation */ @TestAnnotationCritical int MIN_VALUE =
            0x80000000;

    @Deprecated
    public static class Inner {
        @TestAnnotationNonCritical int MAX_VALUE = 0x80000000;
    }

    // Method-Level Annotation
    @SafeVarargs
    public final <@TestAnnotationCritical K extends @TestAnnotationCritical Integer>
            @TestAnnotationCritical int foo(
                    @TestAnnotationNonCritical ClassWithAllAnnotationKinds<T> this,
                    /* Method Parameter-Level Annotation */ @Deprecated K... toAdd)
                    throws /* Method Type-Level Annotation in thrown exception declaration */
                            @TestAnnotationCritical Exception {

        // Method Local-Variable-Level Type Annotation on declared type
        @TestAnnotationCritical String i = "1";
        // Method Local-Variable-Level Type Annotation on
        String j = new @TestAnnotationCritical String("2");

        // Method Try-Catch annotation
        // javac strips annotations in try-catch exception handlers regardless of retention policy
        // TODO: to test that we can report annotations used in these places we could use a
        // different or modified java compiler
        // such as the Eclipse Java Compiler
        try (@TestAnnotationNonCritical
                ReadableByteChannel byteChannel = Channels.newChannel(System.in); ) {
        } catch (@TestAnnotationCritical Exception e) {
        }

        return 0;
    }

    @Override
    public void run() {}
}
