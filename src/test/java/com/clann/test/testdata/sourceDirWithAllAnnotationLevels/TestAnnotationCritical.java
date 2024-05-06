package com.clann.test.testdata.sourceDirWithAllAnnotationLevels;

import java.lang.annotation.*;

@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@interface TestAnnotationCritical {}
