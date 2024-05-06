package com.clann.visitor;

/**
 * Indicates where (at what level) in the Java class structure an annotation appears and is applied.
 */
public enum AnnotationLevel {
    /**
     * Annotations that apply to the class itself.
     */
    CLASS,
    /**
     * Catch-all for type-use annotations at the class-level.
     */
    CLASS_TYPE_USE,
    /**
     * Annotations that apply to the type of a type parameter used in a class.
     */
    CLASS_TYPE_USE_TYPE_PARAMETER,
    /**
     * Annotations that apply to the type of a type parameter bound used in a class.
     */
    CLASS_TYPE_USE_TYPE_PARAMETER_BOUND,
    /**
     * Annotations that apply to the type of a class in an extends clause or an interface in an implements clause
     * at the class-level.
     */
    CLASS_TYPE_USE_EXTENDS,

    /**
     * Annotations that apply to fields of a class.
     */
    FIELD,
    /**
     * Annotations that apply to the types of fields of a class.
     * This may be the type of the field itself or to a type inside initialization code for the field.
     */
    FIELD_TYPE_USE,

    /**
     * Annotations that apply to methods of a class as a whole.
     */
    METHOD,
    /**
     * Annotations that apply to parameters of methods of a class as a whole.
     */
    METHOD_PARAMETER,
    /**
     * Annotations that apply to local variable declarations inside a method.
     */
    METHOD_LOCAL_VARIABLE,
    /**
     * Annotations that apply to types inside try-catch blocks inside methods.
     */
    METHOD_TRYCATCH,
    /**
     * Catch-all for type-use annotations at the method level.
     */
    METHOD_TYPE_USE,
    /**
     * Annotations that apply to types of type parameters at the method level.
     */
    METHOD_TYPE_USE_TYPE_PARAMETER,
    /**
     * Annotations that apply to types of type parameter bounds at the method level.
     */
    METHOD_TYPE_USE_TYPE_PARAMETER_BOUND,
    /**
     * Annotations that apply to types of method parameters.
     */
    METHOD_TYPE_USE_PARAMETER,
    /**
     * Annotations that apply to the types of the return value of methods.
     */
    METHOD_TYPE_USE_RETURN,
    /**
     * Annotations that apply to the types of exceptions in throws clauses of methods.
     */
    METHOD_TYPE_USE_THROWS,
    /**
     * Annotations that apply to explicit receiver parameters in non-static methods of classes.
     */
    METHOD_TYPE_USE_RECEIVER,
}
