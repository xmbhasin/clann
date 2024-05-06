package com.clann.test.visitor;

import static com.diffplug.selfie.Selfie.expectSelfie;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.clann.visitor.AnnotationLevel;
import com.clann.visitor.ClassAnnotationInfo;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link ClassAnnotationInfo} class, covering edge cases not covered by more comprehensive test suites.
 */
@SuppressWarnings("PMD.MethodNamingConventions")
class ClassAnnotationInfoTest {

    /**
     * Check that adding an annotation succeeds for valid annotation descriptors.
     */
    // We suppress PMD.UnitTestShouldIncludeAssert because it raises a false positive when used with
    // Selfie snapshot tests
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    @Test
    void addAnnotation_Succeeds_WhenAnnotationDescriptorIsValid() {
        final ClassAnnotationInfo classAnnotationInfo = new ClassAnnotationInfo();

        classAnnotationInfo.addAnnotation("Lfoo;", AnnotationLevel.CLASS);
        classAnnotationInfo.addAnnotation("L;", AnnotationLevel.CLASS);

        expectSelfie(classAnnotationInfo.getAnnotations().toString()).toMatchDisk();
    }

    /**
     * Check that adding an annotation fails for invalid annotation descriptors.
     */
    @Test
    void addAnnotation_ThrowsException_WhenAnnotationDescriptorIsInvalid() {
        final ClassAnnotationInfo classAnnotationInfo = new ClassAnnotationInfo();

        assertThatThrownBy(
                        () -> {
                            classAnnotationInfo.addAnnotation("", AnnotationLevel.CLASS);
                        })
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(
                        () -> {
                            classAnnotationInfo.addAnnotation("foo", AnnotationLevel.CLASS);
                        })
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(
                        () -> {
                            classAnnotationInfo.addAnnotation("Lfoo", AnnotationLevel.CLASS);
                        })
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(
                        () -> {
                            classAnnotationInfo.addAnnotation("foo;", AnnotationLevel.CLASS);
                        })
                .isInstanceOf(IllegalArgumentException.class);
    }
}
