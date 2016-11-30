/*
 * Copyright 2014 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm.processor;

import com.squareup.javawriter.JavaWriter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.String;
import java.util.*;

public class RealmProxyClassGenerator {
    private ProcessingEnvironment processingEnvironment;
    private String className;
    private String packageName;
    private List<VariableElement> fields = new ArrayList<VariableElement>();
    private Map<String, String> getters = new HashMap<String, String>();
    private Map<String, String> setters = new HashMap<String, String>();
    private List<VariableElement> fieldsToIndex;
    private static final String REALM_PACKAGE_NAME = "io.realm";
    private static final String TABLE_PREFIX = "class_";
    private static final String PROXY_SUFFIX = "RealmProxy";

    public RealmProxyClassGenerator(ProcessingEnvironment processingEnvironment,
                                    String className, String packageName,
                                    List<VariableElement> fields,
                                    Map<String, String> getters, Map<String, String> setters,
                                    List<VariableElement> fieldsToIndex) {
        this.processingEnvironment = processingEnvironment;
        this.className = className;
        this.packageName = packageName;
        this.fields = fields;
        this.getters = getters;
        this.setters = setters;
        this.fieldsToIndex = fieldsToIndex;
    }

    private static final Map<String, String> JAVA_TO_REALM_TYPES;

    static {
        JAVA_TO_REALM_TYPES = new HashMap<String, String>();
        JAVA_TO_REALM_TYPES.put("byte", "Long");
        JAVA_TO_REALM_TYPES.put("short", "Long");
        JAVA_TO_REALM_TYPES.put("int", "Long");
        JAVA_TO_REALM_TYPES.put("long", "Long");
        JAVA_TO_REALM_TYPES.put("float", "Float");
        JAVA_TO_REALM_TYPES.put("double", "Double");
        JAVA_TO_REALM_TYPES.put("boolean", "Boolean");
        JAVA_TO_REALM_TYPES.put("Byte", "Long");
        JAVA_TO_REALM_TYPES.put("Short", "Long");
        JAVA_TO_REALM_TYPES.put("Integer", "Long");
        JAVA_TO_REALM_TYPES.put("Long", "Long");
        JAVA_TO_REALM_TYPES.put("Float", "Float");
        JAVA_TO_REALM_TYPES.put("Double", "Double");
        JAVA_TO_REALM_TYPES.put("Boolean", "Boolean");
        JAVA_TO_REALM_TYPES.put("java.lang.String", "String");
        JAVA_TO_REALM_TYPES.put("java.util.Date", "Date");
        JAVA_TO_REALM_TYPES.put("byte[]", "BinaryByteArray");
        // TODO: add support for char and Char
    }

    private static final Map<String, String> JAVA_TO_COLUMN_TYPES;

    static {
        JAVA_TO_COLUMN_TYPES = new HashMap<String, String>();
        JAVA_TO_COLUMN_TYPES.put("byte", "ColumnType.INTEGER");
        JAVA_TO_COLUMN_TYPES.put("short", "ColumnType.INTEGER");
        JAVA_TO_COLUMN_TYPES.put("int", "ColumnType.INTEGER");
        JAVA_TO_COLUMN_TYPES.put("long", "ColumnType.INTEGER");
        JAVA_TO_COLUMN_TYPES.put("float", "ColumnType.FLOAT");
        JAVA_TO_COLUMN_TYPES.put("double", "ColumnType.DOUBLE");
        JAVA_TO_COLUMN_TYPES.put("boolean", "ColumnType.BOOLEAN");
        JAVA_TO_COLUMN_TYPES.put("Byte", "ColumnType.INTEGER");
        JAVA_TO_COLUMN_TYPES.put("Short", "ColumnType.INTEGER");
        JAVA_TO_COLUMN_TYPES.put("Integer", "ColumnType.INTEGER");
        JAVA_TO_COLUMN_TYPES.put("Long", "ColumnType.INTEGER");
        JAVA_TO_COLUMN_TYPES.put("Float", "ColumnType.FLOAT");
        JAVA_TO_COLUMN_TYPES.put("Double", "ColumnType.DOUBLE");
        JAVA_TO_COLUMN_TYPES.put("Boolean", "ColumnType.BOOLEAN");
        JAVA_TO_COLUMN_TYPES.put("java.lang.String", "ColumnType.STRING");
        JAVA_TO_COLUMN_TYPES.put("java.util.Date", "ColumnType.DATE");
        JAVA_TO_COLUMN_TYPES.put("byte[]", "ColumnType.BINARY");
    }

    private static final Map<String, String> CASTING_TYPES;

    static {
        CASTING_TYPES = new HashMap<String, String>();
        CASTING_TYPES.put("byte", "long");
        CASTING_TYPES.put("short", "long");
        CASTING_TYPES.put("int", "long");
        CASTING_TYPES.put("long", "long");
        CASTING_TYPES.put("float", "float");
        CASTING_TYPES.put("double", "double");
        CASTING_TYPES.put("boolean", "boolean");
        CASTING_TYPES.put("Byte", "long");
        CASTING_TYPES.put("Short", "long");
        CASTING_TYPES.put("Integer", "long");
        CASTING_TYPES.put("Long", "long");
        CASTING_TYPES.put("Float", "float");
        CASTING_TYPES.put("Double", "double");
        CASTING_TYPES.put("Boolean", "boolean");
        CASTING_TYPES.put("java.lang.String", "String");
        CASTING_TYPES.put("java.util.Date", "Date");
        CASTING_TYPES.put("byte[]", "byte[]");
    }

    private static final Map<String, String[]> HASHCODE;

    static {
        HASHCODE = new HashMap<String, String[]>();
        HASHCODE.put("boolean", new String[] {
                "result = 31 * result + (%s() ? 1 : 0)" });
        HASHCODE.put("byte", new String[] {
                "result = 31 * result + (int) %s()" });
        HASHCODE.put("short", new String[] {
                "result = 31 * result + (int) %s()" });
        HASHCODE.put("int", new String[] {
                "result = 31 * result + %s()" });
        HASHCODE.put("long", new String[] {
                "long aLong_%d = %s()",
                "result = 31 * result + (int) (aLong_%d ^ (aLong_%d >>> 32))" });
        HASHCODE.put("float", new String[] {
                "float aFloat_%d = %s()",
                "result = 31 * result + (aFloat_%d != +0.0f ? Float.floatToIntBits(aFloat_%d) : 0)" });
        HASHCODE.put("double", new String[] {
                "long temp_%d = Double.doubleToLongBits(%s())",
                "result = 31 * result + (int) (temp_%d ^ (temp_%d >>> 32))" });
        HASHCODE.put("Byte", new String[] {
                "result = 31 * result + (int) %s()" });
        HASHCODE.put("Short", new String[] {
                "result = 31 * result + (int) %s()" });
        HASHCODE.put("Integer", new String[] {
                "result = 31 * result + %s()" });
        HASHCODE.put("Long", new String[] {
                "long aLong_%d = %s()",
                "result = 31 * result + (int) (aLong_%d ^ (aLong_%d >>> 32))" });
        HASHCODE.put("Float", new String[] {
                "float aFloat_%d = %s()",
                "result = 31 * result + (aFloat_%d != +0.0f ? Float.floatToIntBits(aFloat_%d) : 0)" });
        HASHCODE.put("Double", new String[] {
                "long temp_%d = Double.doubleToLongBits(%s())",
                "result = 31 * result + (int) (temp_%d ^ (temp_%d >>> 32))" });
        HASHCODE.put("Boolean", new String[] {
                "result = 31 * result + (%s() ? 1 : 0)" });
        HASHCODE.put("java.lang.String", new String[] {
                "String aString_%d = %s()",
                "result = 31 * result + (aString_%d != null ? aString_%d.hashCode() : 0)" });
        HASHCODE.put("java.lang.Date", new String[] {
                "Date aDate_%d = %s()",
                "result = 31 * result + (aDate_%d != null ? aDate_%d.hashCode() : 0)" });
        HASHCODE.put("byte[]", new String[] {
                "byte[] aByteArray_%d = %s()",
                "result = 31 * result + (aByteArray_%d != null ? Arrays.hashCode(aByteArray_%d) : 0)" });
    }

    private static final Map<String, Integer> HOW_TO_EQUAL;
    private static final int EQUALS_DIRECT = 0;  // compare values directly
    private static final int EQUALS_NULL = 1;    // check for null and use .equals()
    private static final int EQUALS_ARRAY = 2;   // compare using array
    private static final int EQUALS_COMPARE = 3; // use the compare method

    static {
        HOW_TO_EQUAL = new HashMap<String, Integer>();
        HOW_TO_EQUAL.put("boolean", EQUALS_DIRECT);
        HOW_TO_EQUAL.put("byte", EQUALS_DIRECT);
        HOW_TO_EQUAL.put("short", EQUALS_DIRECT);
        HOW_TO_EQUAL.put("int", EQUALS_DIRECT);
        HOW_TO_EQUAL.put("long", EQUALS_DIRECT);
        HOW_TO_EQUAL.put("float", EQUALS_COMPARE);
        HOW_TO_EQUAL.put("double", EQUALS_COMPARE);
        HOW_TO_EQUAL.put("Byte", EQUALS_DIRECT);
        HOW_TO_EQUAL.put("Short", EQUALS_DIRECT);
        HOW_TO_EQUAL.put("Integer", EQUALS_DIRECT);
        HOW_TO_EQUAL.put("Long", EQUALS_DIRECT);
        HOW_TO_EQUAL.put("Float", EQUALS_DIRECT);
        HOW_TO_EQUAL.put("Double", EQUALS_DIRECT);
        HOW_TO_EQUAL.put("Boolean", EQUALS_DIRECT);
        HOW_TO_EQUAL.put("java.lang.String", EQUALS_NULL); // check for null
        HOW_TO_EQUAL.put("java.util.Date", EQUALS_NULL);
        HOW_TO_EQUAL.put("byte[]", EQUALS_ARRAY); // compare using array
    }

    public void generate() throws IOException, UnsupportedOperationException {
        String qualifiedGeneratedClassName = String.format("%s.%s%s", REALM_PACKAGE_NAME, className, PROXY_SUFFIX);
        JavaFileObject sourceFile = processingEnvironment.getFiler().createSourceFile(qualifiedGeneratedClassName);
        JavaWriter writer = new JavaWriter(new BufferedWriter(sourceFile.openWriter()));

        Elements elementUtils = processingEnvironment.getElementUtils();
        Types typeUtils = processingEnvironment.getTypeUtils();

        TypeMirror realmObject = elementUtils.getTypeElement("io.realm.RealmObject").asType();
        DeclaredType realmList = typeUtils.getDeclaredType(elementUtils.getTypeElement("io.realm.RealmList"), typeUtils.getWildcardType(null, null));

        // Set source code indent to 4 spaces
        writer.setIndent("    ");

        writer.emitPackage(REALM_PACKAGE_NAME)
                .emitEmptyLine();

        writer.emitImports(
                "io.realm.internal.ColumnType",
                "io.realm.internal.Table",
                "io.realm.internal.ImplicitTransaction",
                "io.realm.internal.Row",
                "io.realm.internal.LinkView",
                "io.realm.RealmList",
                "io.realm.RealmObject",
                "io.realm.Realm",
                "java.util.*",
                packageName + ".*")
                .emitEmptyLine();

        // Begin the class definition
        writer.beginType(
                qualifiedGeneratedClassName, // full qualified name of the item to generate
                "class",                     // the type of the item
                EnumSet.of(Modifier.PUBLIC), // modifiers to apply
                className)                   // class to extend
                .emitEmptyLine();

        // Accessors
        for (VariableElement field : fields) {
            String fieldName = field.getSimpleName().toString();
            String fieldTypeCanonicalName = field.asType().toString();

            if (JAVA_TO_REALM_TYPES.containsKey(fieldTypeCanonicalName)) {
                /**
                 * Primitives and boxed types
                 */
                String realmType = JAVA_TO_REALM_TYPES.get(fieldTypeCanonicalName);
                String castingType = CASTING_TYPES.get(fieldTypeCanonicalName);

                // Getter
                writer.emitAnnotation("Override");
                writer.beginMethod(fieldTypeCanonicalName, getters.get(fieldName), EnumSet.of(Modifier.PUBLIC));
                writer.emitStatement(
                        "realm.assertThread()"
                );
                writer.emitStatement(
                        "return (%s) row.get%s(Realm.columnIndices.get(\"%s\").get(\"%s\"))",
                        fieldTypeCanonicalName, realmType, className, fieldName);
                writer.endMethod();
                writer.emitEmptyLine();

                // Setter
                writer.emitAnnotation("Override");
                writer.beginMethod("void", setters.get(fieldName), EnumSet.of(Modifier.PUBLIC), fieldTypeCanonicalName, "value");
                writer.emitStatement(
                        "realm.assertThread()"
                );
                writer.emitStatement(
                        "row.set%s(Realm.columnIndices.get(\"%s\").get(\"%s\"), (%s) value)",
                        realmType, className, fieldName, castingType);
                writer.endMethod();
            } else if (typeUtils.isAssignable(field.asType(), realmObject)) {
                /**
                 * Links
                 */

                // Getter
                writer.emitAnnotation("Override");
                writer.beginMethod(fieldTypeCanonicalName, getters.get(fieldName), EnumSet.of(Modifier.PUBLIC));
                writer.beginControlFlow("if (row.isNullLink(Realm.columnIndices.get(\"%s\").get(\"%s\")))", className, fieldName);
                writer.emitStatement("return null");
                writer.endControlFlow();
                writer.emitStatement(
                        "return realm.get(%s.class, row.getLink(Realm.columnIndices.get(\"%s\").get(\"%s\")))",
                        fieldTypeCanonicalName, className, fieldName);
                writer.endMethod();
                writer.emitEmptyLine();

                // Setter
                writer.emitAnnotation("Override");
                writer.beginMethod("void", setters.get(fieldName), EnumSet.of(Modifier.PUBLIC), fieldTypeCanonicalName, "value");
                writer.beginControlFlow("if (value == null)");
                writer.emitStatement("row.nullifyLink(Realm.columnIndices.get(\"%s\").get(\"%s\"))", className, fieldName);
                writer.endControlFlow();
                writer.emitStatement("row.setLink(Realm.columnIndices.get(\"%s\").get(\"%s\"), value.row.getIndex())", className, fieldName);
                writer.endMethod();
            } else if (typeUtils.isAssignable(field.asType(), realmList)) {
                /**
                 * LinkLists
                 */
                String genericCanonicalType = ((DeclaredType) field.asType()).getTypeArguments().get(0).toString();
                String genericType;
                if (genericCanonicalType.contains(".")) {
                    genericType = genericCanonicalType.substring(genericCanonicalType.lastIndexOf('.') + 1);
                } else {
                    genericType = genericCanonicalType;
                }

                // Getter
                writer.emitAnnotation("Override");
                writer.beginMethod(fieldTypeCanonicalName, getters.get(fieldName), EnumSet.of(Modifier.PUBLIC));
                writer.emitStatement(
                        "return new RealmList<%s>(%s.class, row.getLinkList(Realm.columnIndices.get(\"%s\").get(\"%s\")), realm)",
                        genericType, genericType, className, fieldName);
                writer.endMethod();
                writer.emitEmptyLine();

                // Setter
                writer.emitAnnotation("Override");
                writer.beginMethod("void", setters.get(fieldName), EnumSet.of(Modifier.PUBLIC), fieldTypeCanonicalName, "value");
                writer.emitStatement("LinkView links = row.getLinkList(Realm.columnIndices.get(\"%s\").get(\"%s\"))", className, fieldName);
                writer.beginControlFlow("if (value == null)");
                writer.emitStatement("return"); // TODO: delete all the links instead
                writer.endControlFlow();
                writer.beginControlFlow("for (RealmObject linkedObject : (RealmList<? extends RealmObject>) value)");
                writer.emitStatement("links.add(linkedObject.row.getIndex())");
                writer.endControlFlow();
                writer.endMethod();
            } else {
                throw new UnsupportedOperationException(
                        String.format("Type %s of field %s is not supported", fieldTypeCanonicalName, fieldName));
            }
            writer.emitEmptyLine();
        }

        /**
         * initTable method
         */
        writer.beginMethod(
                "Table", // Return type
                "initTable", // Method name
                EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), // Modifiers
                "ImplicitTransaction", "transaction"); // Argument type & argument name

        writer.beginControlFlow("if(!transaction.hasTable(\"" + TABLE_PREFIX + this.className + "\"))");
        writer.emitStatement("Table table = transaction.getTable(\"%s%s\")", TABLE_PREFIX, this.className);

        // For each field generate corresponding table index constant
        for (VariableElement field : fields) {
            String fieldName = field.getSimpleName().toString();
            String fieldTypeCanonicalName = field.asType().toString();
            String fieldTypeName;
            if (fieldTypeCanonicalName.contains(".")) {
                fieldTypeName = fieldTypeCanonicalName.substring(fieldTypeCanonicalName.lastIndexOf('.') + 1);
            } else {
                fieldTypeName = fieldTypeCanonicalName;
            }

            if (JAVA_TO_REALM_TYPES.containsKey(fieldTypeCanonicalName)) {
                writer.emitStatement("table.addColumn(%s, \"%s\")",
                        JAVA_TO_COLUMN_TYPES.get(fieldTypeCanonicalName),
                        fieldName);
            } else if (typeUtils.isAssignable(field.asType(), realmObject)) {
                writer.beginControlFlow("if (!transaction.hasTable(\"%s%s\"))", TABLE_PREFIX, fieldTypeName);
                writer.emitStatement("%s%s.initTable(transaction)", fieldTypeName, PROXY_SUFFIX);
                writer.endControlFlow();
                writer.emitStatement("table.addColumnLink(ColumnType.LINK, \"%s\", transaction.getTable(\"%s%s\"))",
                        fieldName, TABLE_PREFIX, fieldTypeName);
            } else if (typeUtils.isAssignable(field.asType(), realmList)) {
                String genericCanonicalType = ((DeclaredType) field.asType()).getTypeArguments().get(0).toString();
                String genericType;
                if (genericCanonicalType.contains(".")) {
                    genericType = genericCanonicalType.substring(genericCanonicalType.lastIndexOf('.') + 1);
                } else {
                    genericType = genericCanonicalType;
                }
                writer.beginControlFlow("if (!transaction.hasTable(\"%s%s\"))", TABLE_PREFIX, genericType);
                writer.emitStatement("%s%s.initTable(transaction)", genericType, PROXY_SUFFIX);
                writer.endControlFlow();
                writer.emitStatement("table.addColumnLink(ColumnType.LINK_LIST, \"%s\", transaction.getTable(\"%s%s\"))",
                        fieldName, TABLE_PREFIX, genericType);
            }
        }

        for (VariableElement field : fieldsToIndex) {
            String fieldName = field.getSimpleName().toString();
            writer.emitStatement("table.setIndex(table.getColumnIndex(\"%s\"))", fieldName);
        }

        writer.emitStatement("return table");
        writer.endControlFlow();
        writer.emitStatement("return transaction.getTable(\"%s%s\")", TABLE_PREFIX, this.className);
        writer.endMethod();
        writer.emitEmptyLine();

        /**
         * validateTable method
         */
        writer.beginMethod(
                "void", // Return type
                "validateTable", // Method name
                EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), // Modifiers
                "ImplicitTransaction", "transaction"); // Argument type & argument name

        writer.beginControlFlow("if(transaction.hasTable(\"" + TABLE_PREFIX + this.className + "\"))");
        writer.emitStatement("Table table = transaction.getTable(\"%s%s\")", TABLE_PREFIX, this.className);

        // verify number of columns
        writer.beginControlFlow("if(table.getColumnCount() != " + fields.size() + ")");
        writer.emitStatement("throw new IllegalStateException(\"Column count does not match\")");
        writer.endControlFlow();

        // create type dictionary for lookup
        writer.emitStatement("Map<String, ColumnType> columnTypes = new HashMap<String, ColumnType>()");
        writer.beginControlFlow("for(long i = 0; i < " + fields.size() + "; i++)");
        writer.emitStatement("columnTypes.put(table.getColumnName(i), table.getColumnType(i))");
        writer.endControlFlow();

        // For each field verify there is a corresponding column
        for (VariableElement field : fields) {
            String fieldName = field.getSimpleName().toString();
            String fieldTypeCanonicalName = field.asType().toString();
            String fieldTypeName;
            if (fieldTypeCanonicalName.contains(".")) {
                fieldTypeName = fieldTypeCanonicalName.substring(fieldTypeCanonicalName.lastIndexOf('.') + 1);
            } else {
                fieldTypeName = fieldTypeCanonicalName;
            }

            if (JAVA_TO_REALM_TYPES.containsKey(fieldTypeCanonicalName)) {
                // make sure types align
                writer.beginControlFlow("if (!columnTypes.containsKey(\"%s\"))", fieldName);
                writer.emitStatement("throw new IllegalStateException(\"Missing column '%s'\")", fieldName);
                writer.endControlFlow();
                writer.beginControlFlow("if (columnTypes.get(\"%s\") != %s)", fieldName, JAVA_TO_COLUMN_TYPES.get(fieldTypeCanonicalName));
                writer.emitStatement("throw new IllegalStateException(\"Invalid type '%s' for column '%s'\")",
                        fieldTypeName, fieldName);
                writer.endControlFlow();
            } else if (typeUtils.isAssignable(field.asType(), realmObject)) { // Links
                writer.beginControlFlow("if (!columnTypes.containsKey(\"%s\"))", fieldName);
                writer.emitStatement("throw new IllegalStateException(\"Missing column '%s'\")", fieldName);
                writer.endControlFlow();
                writer.beginControlFlow("if (columnTypes.get(\"%s\") != ColumnType.LINK)", fieldName);
                writer.emitStatement("throw new IllegalStateException(\"Invalid type '%s' for column '%s'\")",
                        fieldTypeName, fieldName);
                writer.endControlFlow();
                writer.beginControlFlow("if (!transaction.hasTable(\"%s%s\"))", TABLE_PREFIX, fieldTypeName);
                writer.emitStatement("throw new IllegalStateException(\"Missing table '%s%s' for column '%s'\")",
                        TABLE_PREFIX, fieldTypeName, fieldName);
                writer.endControlFlow();
                // TODO: Replace with a proper comparison
//                writer.emitStatement("Table table_%d = transaction.getTable(\"%s%s\")", columnNumber, TABLE_PREFIX, fieldTypeName);
//                writer.beginControlFlow("if (table.getLinkTarget(%d).equals(table_%d))", columnNumber, columnNumber);
//                writer.emitStatement("throw new IllegalStateException(\"Mismatching link tables for column '%s'\")",
//                        fieldName);
//                writer.endControlFlow();
            } else if (typeUtils.isAssignable(field.asType(), realmList)) { // Link Lists
                String genericCanonicalType = ((DeclaredType) field.asType()).getTypeArguments().get(0).toString();
                String genericType;
                if (genericCanonicalType.contains(".")) {
                    genericType = genericCanonicalType.substring(genericCanonicalType.lastIndexOf('.') + 1);
                } else {
                    genericType = genericCanonicalType;
                }
                writer.beginControlFlow("if(!columnTypes.containsKey(\"%s\"))", fieldName);
                writer.emitStatement("throw new IllegalStateException(\"Missing column '%s'\")", fieldName);
                writer.endControlFlow();
                writer.beginControlFlow("if(columnTypes.get(\"%s\") != ColumnType.LINK_LIST)", fieldName);
                writer.emitStatement("throw new IllegalStateException(\"Invalid type '%s' for column '%s'\")",
                        genericType, fieldName);
                writer.endControlFlow();
                writer.beginControlFlow("if (!transaction.hasTable(\"%s%s\"))", TABLE_PREFIX, genericType);
                writer.emitStatement("throw new IllegalStateException(\"Missing table '%s%s' for column '%s'\")",
                        TABLE_PREFIX, genericType, fieldName);
                writer.endControlFlow();
                // TODO: Replace with a proper comparison
//                writer.emitStatement("Table table_%d = transaction.getTable(\"%s%s\")", columnNumber, TABLE_PREFIX, genericType);
//                writer.beginControlFlow("if (table.getLinkTarget(%d).equals(table_%d))", columnNumber, columnNumber);
//                writer.emitStatement("throw new IllegalStateException(\"Mismatching link list tables for column '%s'\")",
//                        fieldName);
//                writer.endControlFlow();
            }
        }
        writer.endControlFlow();
        writer.endMethod();
        writer.emitEmptyLine();

        /**
         * getFieldNames method
         */
        writer.beginMethod("List<String>", "getFieldNames", EnumSet.of(Modifier.PUBLIC, Modifier.STATIC));
        List<String> entries = new ArrayList<String>();
        for (VariableElement field : fields) {
            String fieldName = field.getSimpleName().toString();
            entries.add(String.format("\"%s\"", fieldName));
        }
        String statementSection = joinStringList(entries, ", ");
        writer.emitStatement("return Arrays.asList(%s)", statementSection);
        writer.endMethod();
        writer.emitEmptyLine();

        /**
         * toString method
         */
        writer.emitAnnotation("Override");
        writer.beginMethod("String", "toString", EnumSet.of(Modifier.PUBLIC));
        writer.emitStatement("StringBuilder stringBuilder = new StringBuilder(\"%s = [\")", className);
        for (VariableElement field : fields) {
            String fieldName = field.getSimpleName().toString();
            writer.emitStatement("stringBuilder.append(\"{%s:\")", fieldName);
            writer.emitStatement("stringBuilder.append(%s())", getters.get(fieldName));
            writer.emitStatement("stringBuilder.append(\"} \")", fieldName);
        }
        writer.emitStatement("stringBuilder.append(\"]\")");
        writer.emitStatement("return stringBuilder.toString()");
        writer.endMethod();
        writer.emitEmptyLine();

        /**
         * hashCode method
         */
        writer.emitAnnotation("Override");
        writer.beginMethod("int", "hashCode", EnumSet.of(Modifier.PUBLIC));
        writer.emitStatement("int result = 17");
        int counter = 0;
        for (VariableElement field : fields) {
            String fieldName = field.getSimpleName().toString();
            String fieldTypeCanonicalName = field.asType().toString();
            if (HASHCODE.containsKey(fieldTypeCanonicalName)) {
                for (String statement : HASHCODE.get(fieldTypeCanonicalName)) {
                    if (statement.contains("%d") && statement.contains("%s")) {
                        // This statement introduces a temporary variable
                        writer.emitStatement(statement, counter, getters.get(fieldName));
                    } else if(statement.contains("%d")) {
                        // This statement uses the temporary variable
                        writer.emitStatement(statement, counter, counter);
                    } else if (statement.contains("%s")) {
                        // This is a normal statement with only one assignment
                        writer.emitStatement(statement, getters.get(fieldName));
                    } else {
                        // This should never happen
                        throw new AssertionError();
                    }
                }
            } else {
                // Links and Link lists
                writer.emitStatement("%s temp_%d = %s()", fieldTypeCanonicalName, counter, getters.get(fieldName));
                writer.emitStatement("result = 31 * result + (temp_%d != null ? temp_%d.hashCode() : 0)", counter, counter);
            }
            counter++;
        }
        writer.emitStatement("return result");
        writer.endMethod();
        writer.emitEmptyLine();

        /**
         * equals method
         */
        String proxyClassName = className + PROXY_SUFFIX;
        writer.emitAnnotation("Override");
        writer.beginMethod("boolean", "equals", EnumSet.of(Modifier.PUBLIC), "Object", "o");
        writer.emitStatement("if (this == o) return true");
        writer.emitStatement("if (o == null || getClass() != o.getClass()) return false");
        writer.emitStatement("%s a%s = (%s)o", proxyClassName, className, proxyClassName);  // FooRealmProxy aFoo = (FooRealmProxy)o

        for (VariableElement field : fields) {
            String fieldName = field.getSimpleName().toString();
            String capFieldName = capitaliseFirstChar(fieldName);
            String fieldTypeCanonicalName = field.asType().toString();
            if (HOW_TO_EQUAL.containsKey(fieldTypeCanonicalName)) {
                switch (HOW_TO_EQUAL.get(fieldTypeCanonicalName)) {
                    case EQUALS_DIRECT: // if (getField() != aFoo.getField()) return false
                        writer.emitStatement("if (%s() != a%s.%s()) return false", getters.get(fieldName), className, getters.get(fieldName));
                        break;
                    case EQUALS_NULL: // if (getField() != null = !getField().equals(aFoo.getField()) : aFoo.getField() != null) return false
                        writer.emitStatement("if (%s() != null ? !%s().equals(a%s.%s()) : a%s.%s() != null) return false",
                                getters.get(fieldName),
                                getters.get(fieldName), className, getters.get(fieldName),
                                className, getters.get(fieldName));
                        break;
                    case EQUALS_ARRAY: // if (!Arrays.equals(getField(), aFoo.getField())) return false
                        writer.emitStatement("if (!Arrays.equals(%s(), a%s.%s())) return false",
                                getters.get(fieldName),
                                className, getters.get(fieldName));
                        break;
                    case EQUALS_COMPARE: // if (
                        writer.emitStatement("if (%s.compare(%s(), a%s.%s()) != 0) return false",
                                capitaliseFirstChar(fieldTypeCanonicalName), getters.get(fieldName), className,
                                getters.get(fieldName));
                        break;
                }
            }
            else if (typeUtils.isAssignable(field.asType(), realmObject) || typeUtils.isAssignable(field.asType(), realmList)) {
                writer.emitStatement("if (%s() != null ? !%s().equals(a%s.%s()) : a%s.%s() != null) return false",
                        getters.get(fieldName),
                        getters.get(fieldName), className, getters.get(fieldName),
                        className, getters.get(fieldName));
            }
        }
        writer.emitStatement("return true");
        writer.endMethod();
        writer.emitEmptyLine();

        // End the class definition
        writer.endType();
        writer.close();
    }

    private static String capitaliseFirstChar(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static String joinStringList(List<String> strings, String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        ListIterator<String> iterator = strings.listIterator();
        while (iterator.hasNext()) {
            int index = iterator.nextIndex();
            String item = iterator.next();

            if (index > 0) {
                stringBuilder.append(separator);
            }
            stringBuilder.append(item);
        }
        return stringBuilder.toString();
    }
}
