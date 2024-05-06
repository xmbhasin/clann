
//
//    static JavaClass parse(String file) throws IOException {
//        org.apache.bcel.classfile.ClassParser parser =
//                new org.apache.bcel.classfile.ClassParser(file);
//        return parser.parse();
//    }
//
//    static AnnotationEntry[] getAnnotations(String file) throws IOException {
//        JavaClass javaClass = parse(file);
//        return javaClass.getAnnotationEntries();
//    }
//
//    public static List<JavaClass> parseJar(String file) throws IOException {
//        JarFile jarFile = new JarFile(file);
//
//        try (jarFile) {
//            List<JavaClass> clazzes =
//                    jarFile.stream()
//                            .filter(entry -> entry.getName().endsWith(".class"))
//                            .map(
//                                    entry -> {
//                                        try (InputStream input = jarFile.getInputStream(entry)) {
//                                            org.apache.bcel.classfile.ClassParser parser =
//                                                    new org.apache.bcel.classfile.ClassParser(
//                                                            input, entry.getName());
//                                            return parser.parse();
//                                        } catch (Exception e) {
//                                            System.err.println(
//                                                    "Error processing "
//                                                            + entry.getName()
//                                                            + ": "
//                                                            + e.getMessage());
//                                        }
//                                        return null;
//                                    })
//                            .filter(Objects::nonNull)
//                            .collect(Collectors.toList());
//            return clazzes;
//        }
//    }
