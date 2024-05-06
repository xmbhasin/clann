package com.clann.visitor;

import com.clann.ExcludeFromGeneratedCoverageReport;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ClannMethodVisitor extends MethodVisitor {
    private final String name;
    private final ClassAnnotationInfo annotations;
    private static final Logger LOGGER =
            LoggerFactory.getLogger(AnnotationCollector.class.getSimpleName());

    ClannMethodVisitor(final ClassAnnotationInfo annotations, final String name) {
        super(AnnotationCollector.ASM_API_VERSION);
        this.annotations = annotations;
        this.name = name;
    }

    @Override
    public AnnotationVisitor visitAnnotation(
            @NotNull final String descriptor, final boolean visible) {
        annotations.addAnnotation(descriptor, AnnotationLevel.METHOD);

        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(
            final int typeRef,
            final TypePath typePath,
            @NotNull final String descriptor,
            final boolean visible) {
        final TypeReference ref = new TypeReference(typeRef);
        //  TypeReference.METHOD_FORMAL_PARAMETER is covered by visitParameterAnnotation
        switch (ref.getSort()) {
            case TypeReference.METHOD_TYPE_PARAMETER:
                annotations.addAnnotation(
                        descriptor, AnnotationLevel.METHOD_TYPE_USE_TYPE_PARAMETER);
                break;
            case TypeReference.METHOD_TYPE_PARAMETER_BOUND:
                annotations.addAnnotation(
                        descriptor, AnnotationLevel.METHOD_TYPE_USE_TYPE_PARAMETER_BOUND);
                break;
            case TypeReference.METHOD_RETURN:
                annotations.addAnnotation(descriptor, AnnotationLevel.METHOD_TYPE_USE_RETURN);
                break;
            case TypeReference.METHOD_RECEIVER:
                annotations.addAnnotation(descriptor, AnnotationLevel.METHOD_TYPE_USE_RECEIVER);
                break;
            case TypeReference.THROWS:
                annotations.addAnnotation(descriptor, AnnotationLevel.METHOD_TYPE_USE_THROWS);
                break;
            default:
                annotations.addAnnotation(descriptor, AnnotationLevel.METHOD_TYPE_USE);
                break;
        }
        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(
            final int parameter, @NotNull final String descriptor, final boolean visible) {
        annotations.addAnnotation(descriptor, AnnotationLevel.METHOD_PARAMETER);

        return super.visitParameterAnnotation(parameter, descriptor, visible);
    }

    /**
     * Currently, we don't distinguish between {@link TypeReference#LOCAL_VARIABLE} or
     * {@link TypeReference#RESOURCE_VARIABLE}.
     */
    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(
            final int typeRef,
            final TypePath typePath,
            final Label[] start,
            final Label[] end,
            final int[] index,
            @NotNull final String descriptor,
            final boolean visible) {
        annotations.addAnnotation(descriptor, AnnotationLevel.METHOD_LOCAL_VARIABLE);

        return super.visitLocalVariableAnnotation(
                typeRef, typePath, start, end, index, descriptor, visible);
    }

    @ExcludeFromGeneratedCoverageReport
    @Override
    public AnnotationVisitor visitTryCatchAnnotation(
            final int typeRef,
            final TypePath typePath,
            final String descriptor,
            final boolean visible) {
        ClannMethodVisitor.LOGGER.trace(
                "visiting TryCatchAnnotation in method '{}': {}", name, descriptor);
        annotations.addAnnotation(descriptor, AnnotationLevel.METHOD_TRYCATCH);

        return super.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible);
    }
}
