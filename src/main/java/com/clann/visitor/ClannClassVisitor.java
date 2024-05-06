package com.clann.visitor;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ClannClassVisitor extends ClassVisitor {
    private final ClassAnnotationInfo annotations;
    private static final Logger LOGGER =
            LoggerFactory.getLogger(AnnotationCollector.class.getSimpleName());

    ClannClassVisitor(final ClassAnnotationInfo annotations) {
        super(AnnotationCollector.ASM_API_VERSION);
        this.annotations = annotations;
    }

    @Override
    public void visit(
            final int version,
            final int access,
            @NotNull final String name,
            final String signature,
            final String superName,
            final String[] interfaces) {
        final String className = name.replace('/', '.');
        annotations.setClassName(className);
    }

    @Override
    public AnnotationVisitor visitAnnotation(
            @NotNull final String descriptor, final boolean visible) {
        annotations.addAnnotation(descriptor, AnnotationLevel.CLASS);
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(
            final int typeRef,
            final TypePath typePath,
            @NotNull final String descriptor,
            final boolean visible) {
        final TypeReference ref = new TypeReference(typeRef);
        switch (ref.getSort()) {
            case TypeReference.CLASS_TYPE_PARAMETER:
                annotations.addAnnotation(
                        descriptor, AnnotationLevel.CLASS_TYPE_USE_TYPE_PARAMETER);
                break;
            case TypeReference.CLASS_TYPE_PARAMETER_BOUND:
                annotations.addAnnotation(
                        descriptor, AnnotationLevel.CLASS_TYPE_USE_TYPE_PARAMETER_BOUND);
                break;
            case TypeReference.CLASS_EXTENDS:
                annotations.addAnnotation(descriptor, AnnotationLevel.CLASS_TYPE_USE_EXTENDS);
                break;
            default:
                annotations.addAnnotation(descriptor, AnnotationLevel.CLASS_TYPE_USE);
                break;
        }

        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    @NotNull
    @Override
    public FieldVisitor visitField(
            final int access,
            final String name,
            final String descriptor,
            final String signature,
            final Object value) {
        ClannClassVisitor.LOGGER.trace("visiting field: {}", name);

        return new ClannFieldVisitor(this.annotations);
    }

    @NotNull
    @Override
    public MethodVisitor visitMethod(
            final int access,
            final String name,
            @NotNull final String descriptor,
            final String signature,
            final String[] exceptions) {
        ClannClassVisitor.LOGGER.trace("visiting method: {}", name);

        return new ClannMethodVisitor(this.annotations, name);
    }
}
