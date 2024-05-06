package com.clann.visitor;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;

final class ClannFieldVisitor extends FieldVisitor {
    private final ClassAnnotationInfo annotations;

    ClannFieldVisitor(final ClassAnnotationInfo annotations) {
        super(AnnotationCollector.ASM_API_VERSION);
        this.annotations = annotations;
    }

    @Override
    public AnnotationVisitor visitAnnotation(
            @NotNull final String descriptor, final boolean visible) {
        annotations.addAnnotation(descriptor, AnnotationLevel.FIELD);

        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(
            final int typeRef,
            final TypePath typePath,
            @NotNull final String descriptor,
            final boolean visible) {
        annotations.addAnnotation(descriptor, AnnotationLevel.FIELD_TYPE_USE);
        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }
}
