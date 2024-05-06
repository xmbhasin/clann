# Clann - Java Class Annotations Analyzer

## Overview

The `clann` application implements a command-line interface for parsing Java JAR files, analyzing Java class files,
and producing a report that details which classes use which annotations.

## Acknowledgements

This repo uses the Java repo starter template at <https://github.com/HiveMinds/Java-template>.

## Usage

### Prerequisites

Ensure Java 21 is installed and correctly configured to build and run the clann app.

To run pre-commit checks over the code base, install `pre-commit` and `Docker` is required.
For `pre-commit` installation instructions see <https://pre-commit.com/>.
For `Docker` installation instructions see <https://docs.docker.com/engine/install/>.

```sh
# Install pre-commit
pre-commit install
pre-commit autoupdate
```

### Running the clann CLI

To run the clann CLI with gradle use:

```sh
./gradlew run --args="--help"
```

To build and run a fat runnable Java JAR file of the clann app:

```sh
./gradlew build shadowJar

java -jar build/libs/clann-all.jar
```

To run the app on an included real-world test JAR file from the netty library:

```sh
java -jar build/libs/clann-all.jar "src/test/java/com/clann/test/testdata/realJarFiles/netty-common-4.2.0.Final.jar"
```

### Testing

To run all unit tests:

```sh
./gradlew test
```

To view a test results report, open `build/reports/tests/test/index.html` in a browser.

To run tests, formatting checks, and get a test coverage report run:

```sh
./gradlew check
```

To view the coverage report in browser, open `build/reports/jacoco/index.html`.

### Pre-commit checks

Run all pre-commit checks with:

```sh
pre-commit run --all
```

This includes the PMD static analyzer which enforces a consistent Java style across the codebase.

TODO: In the future, we want to ensure developer IDEs are configured to use the same rule sets and run these pre-commit checks in continuous integration.

### Documentation

To generate documentation, run:

```sh
./gradlew javadoc
```

That produces the documentation in `build/docs/javadoc/index.html`.

## Background

The Java class file format is important to understand the implementation of the clann application.
The format is described in detail here for Java 21: <https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html>.

To make implementation simpler, the battle-tested open-source library ASM is used. See <https://asm.ow2.io/>.
The ASM library was chosen over alternatives because it claims to be more performant than other libraries and because it supports type use annotations. Initial testing with one alternative library, BCEL, showed that indeed type annotations are not supported.

Type annotations are annotations that can appear before a type, for example in a variable declarion:

```java
@NonNull String str;
```

More information about type annotations can be found in the class file format docs and at <https://docs.oracle.com/javase/tutorial/java/annotations/type_annotations.html>.

Java annotations are allowed only on language elements defined by @Target in java.lang.annotation.ElementType.
Valid targets include: TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE, TYPE_PARAMETER, TYPE_USE, MODULE, and RECORD_COMPONENT.

Attributes in a .class file are not all stored in one place. Instead, they are distributed throughout the class file structure, attached to the parts they belong to (e.g., class, field, method, code blocks, etc.). Implementing annotation collection thus requires correctly parsing and collecting annotation information from multiple places in a class file, making extraction of this logic into a library valuable.

## Design

The `com.clann` package contains a light-weight `Main` class that defines the conventional entry point for the clann application.

The command-line interface of the clann application is implemented in the `App` class for modularity, future flexibility in supporting multiple interfaces, and testability.

The `App` class uses the `picoli` library to implement the command-line interface. This makes it easy to add CLI arguments and provides features like `--help` and `--version` flags.

The `App` class uses the `JarAnalyzer` utility class to analyze a JAR file provided as a CLI positional argument and then print a report detailing the annotations used by class files.

The `JarAnalyzer` utility class provides the `analyzeJar` method which reads each entry in a JAR file in order, and for each class file with the `.class` extension, tries to read and parse the class and collection information about which annotations it uses.

To collect annotation information, `JarAnalyzer` uses the ASM library via the `com.clann.visitor.AnnotationCollector` class.

The `AnnotationCollector` class tries to read and parse class file bytes using ASM and if successful traverses the class structure and collects annotation information.

To collect annotation information, `AnnotationCollector` uses custom implementations of `org.objectweb.asm.ClassVisitor`, `org.objectweb.asm.FieldVisitor` and `org.objectweb.asm.MethodVisitor` in `ClannClassVisitor`, `ClannFieldVisitor` and `ClannMethodVisitor` respectively.
These visitors are called by ASM in a somewhat well-defined order and the custom implementations add annotation information to an instance of `ClassAnnotationInfo`.

`ClassAnnotationInfo` groups information about the annotations used by a class. It uses the `AnnotationLevel` enum to detail where annotations are seen or used in the class file at a high level.

### Performance

Profiling using the IntelliJ Idea Java profiler shows that the Call Tree when running the application with the JAR file at
`src/test/java/com/clann/test/testdata/realJarFiles/netty-common-4.2.0.Final.jar` is fairly even for classes under `com.clann`.
Currently, the CPU time is dominated by initializing and obtaining (around 15%). It may be worthwhile to use a leaner logging library instead of logback.
Only about 5% of time is spent in collecting annotations and only about 1% in reading the JAR file.

### Tests

Tests are placed in the `com.clann.test` package so that tests focus on the public interfaces exposed by the codebase.

In some cases we may want to test package-private code for thoroughness, in which case placing tests in the same package as the source code may be warranted.

We use the selfie library (see <https://github.com/diffplug/selfie>) for snapshot testing. This makes it easier to create tests over intricate test outputs and facilitates easy review of test output updates.

Test coverage of the source code in this repository is 100% as reported by JaCoCo.

However, coverage of dependencies and coverage of more intricate test scenarios cannot be detected by JaCoCo.

Test results can be compared with manual analysis of the output of the `javap` program which produces analyses and produces a more general report about the elements inside a Java class file.

We use JUnit's `assertTimeout` for a simple way to ensure the e2e run of the clann app remains performant.

### Caveats

Although class files containing non-class elements like interfaces or enums are not explicitly supported or tested, we still parse and collect annotation information about these to limit scope and complexity of the clann application.

Although tests cover corrupted class files provided in a JAR file, intricate scenarios like duplicated class names are not currently covered.
Assuming the user has used a well-known Java compiler to produce the JAR file should eliminate some of these scenarios.

Tests also do not currently cover JAR files compiled with multiple different versions of the Java compiler.
The primary target currently is the Java compiler used in the codebase itself, Java 21.

### Evolution

To improve the clann application several areas may be explored. We list some of them here:

1. Configurability
   1. Add CLI argument to change log level.
   1. Add CLI argument to specify output report format.
1. Output report formats
   1. Support more formal report formats including JSON and YAML.
1. Output analysis flexibility
   1. To support more flexible analysis and reporting on class annotation usage, a database file format such as SQLite could be produced. This would enable users to analyze and produce their own reports over the annotation information collected.
