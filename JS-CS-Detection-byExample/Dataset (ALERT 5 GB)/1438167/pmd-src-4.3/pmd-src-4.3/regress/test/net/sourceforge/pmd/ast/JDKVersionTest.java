package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.TargetJDK1_3;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.TargetJDK1_5;
import net.sourceforge.pmd.TargetJDK1_7;
import net.sourceforge.pmd.TargetJDKVersion;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.sourcetypehandlers.SourceTypeHandler;
import net.sourceforge.pmd.sourcetypehandlers.SourceTypeHandlerBroker;

import org.junit.Test;

import java.io.StringReader;

public class JDKVersionTest {

    // enum keyword/identifier
    @Test(expected = ParseException.class)
    public void testEnumAsKeywordShouldFailWith14() throws Throwable {
        JavaParser p = new TargetJDK1_4().createParser(new StringReader(JDK15_ENUM));
        p.CompilationUnit();
    }

    @Test
    public void testEnumAsIdentifierShouldPassWith14() throws Throwable {
        JavaParser p = new TargetJDK1_4().createParser(new StringReader(JDK14_ENUM));
        p.CompilationUnit();
    }

    @Test
    public void testEnumAsKeywordShouldPassWith15() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(JDK15_ENUM));
        p.CompilationUnit();
    }

    @Test(expected = ParseException.class)
    public void testEnumAsIdentifierShouldFailWith15() throws Throwable {
        TargetJDKVersion jdk = new TargetJDK1_5();
        JavaParser p = jdk.createParser(new StringReader(JDK14_ENUM));
        p.CompilationUnit();
    }
    // enum keyword/identifier

    // assert keyword/identifier
    @Test
    public void testAssertAsKeywordVariantsSucceedWith1_4() {
        (new TargetJDK1_4()).createParser(new StringReader(ASSERT_TEST1)).CompilationUnit();
        (new TargetJDK1_4()).createParser(new StringReader(ASSERT_TEST2)).CompilationUnit();
        (new TargetJDK1_4()).createParser(new StringReader(ASSERT_TEST3)).CompilationUnit();
        (new TargetJDK1_4()).createParser(new StringReader(ASSERT_TEST4)).CompilationUnit();
    }

    @Test(expected = ParseException.class)
    public void testAssertAsVariableDeclIdentifierFailsWith1_4() {
        (new TargetJDK1_4()).createParser(new StringReader(ASSERT_TEST5)).CompilationUnit();
    }

    @Test(expected = ParseException.class)
    public void testAssertAsMethodNameIdentifierFailsWith1_4() {
        (new TargetJDK1_4()).createParser(new StringReader(ASSERT_TEST7)).CompilationUnit();
    }

    @Test
    public void testAssertAsIdentifierSucceedsWith1_3() {
        JavaParser jp = (new TargetJDK1_3()).createParser(new StringReader(ASSERT_TEST5));
        jp.CompilationUnit();
    }

    @Test(expected = ParseException.class)
    public void testAssertAsKeywordFailsWith1_3() {
        JavaParser jp = (new TargetJDK1_3()).createParser(new StringReader(ASSERT_TEST6));
        jp.CompilationUnit();
    }
    // assert keyword/identifier

    @Test
    public void testVarargsShouldPassWith15() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(JDK15_VARARGS));
        p.CompilationUnit();
    }

    @Test(expected = ParseException.class)
    public void testVarargsShouldFailWith14() throws Throwable {
        JavaParser p = new TargetJDK1_4().createParser(new StringReader(JDK15_VARARGS));
        p.CompilationUnit();
    }

    @Test
    public void testJDK15ForLoopSyntaxShouldPassWith15() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(JDK15_FORLOOP));
        p.CompilationUnit();
    }

    @Test
    public void testJDK15ForLoopSyntaxWithModifiers() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(JDK15_FORLOOP_WITH_MODIFIER));
        p.CompilationUnit();
    }

    @Test(expected = ParseException.class)
    public void testJDK15ForLoopShouldFailWith14() throws Throwable {
        JavaParser p = new TargetJDK1_4().createParser(new StringReader(JDK15_FORLOOP));
        p.CompilationUnit();
    }

    @Test
    public void testJDK15GenericsSyntaxShouldPassWith15() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(JDK15_GENERICS));
        p.CompilationUnit();
    }

    @Test
    public void testVariousParserBugs() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(FIELDS_BUG));
        p.CompilationUnit();
        p = new TargetJDK1_5().createParser(new StringReader(GT_BUG));
        p.CompilationUnit();
        p = new TargetJDK1_5().createParser(new StringReader(ANNOTATIONS_BUG));
        p.CompilationUnit();
        p = new TargetJDK1_5().createParser(new StringReader(CONSTANT_FIELD_IN_ANNOTATION_BUG));
        p.CompilationUnit();
        p = new TargetJDK1_5().createParser(new StringReader(GENERIC_IN_FIELD));
        p.CompilationUnit();
    }

    @Test
    public void testNestedClassInMethodBug() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(INNER_BUG));
        p.CompilationUnit();
        p = new TargetJDK1_5().createParser(new StringReader(INNER_BUG2));
        p.CompilationUnit();
    }

    @Test
    public void testGenericsInMethodCall() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(GENERIC_IN_METHOD_CALL));
        p.CompilationUnit();
    }

    @Test
    public void testGenericINAnnotation() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(GENERIC_IN_ANNOTATION));
        p.CompilationUnit();
    }

    @Test
    public void testGenericReturnType() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(GENERIC_RETURN_TYPE));
        p.CompilationUnit();
    }

    @Test
    public void testMultipleGenerics() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(FUNKY_GENERICS));
        p.CompilationUnit();
        p = new TargetJDK1_5().createParser(new StringReader(MULTIPLE_GENERICS));
        p.CompilationUnit();
    }

    @Test
    public void testAnnotatedParams() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(ANNOTATED_PARAMS));
        p.CompilationUnit();
    }

    @Test
    public void testAnnotatedLocals() throws Throwable {
        JavaParser p = new TargetJDK1_5().createParser(new StringReader(ANNOTATED_LOCALS));
        p.CompilationUnit();
    }

    @Test
    public void testAssertAsIdentifierSucceedsWith1_3_test2() {
        JavaParser jp = (new TargetJDK1_3()).createParser(new StringReader(ASSERT_TEST5_a));
        jp.CompilationUnit();
    }

    @Test
    public final void testBinaryAndUnderscoresInNumericalLiterals() throws Throwable {
        new TargetJDK1_7().createParser(new StringReader(JDK17_NUMERICAL_LITERALS)).CompilationUnit();
    }
    
    @Test
    public final void testStringInSwitch() throws Throwable {
        new TargetJDK1_7().createParser(new StringReader(JDK17_STRING_IN_SWITCH)).CompilationUnit();
    }
    
    @Test
    public final void testGenericDiamond() throws Throwable {
        new TargetJDK1_7().createParser(new StringReader(JDK17_GENERIC_DIAMOND)).CompilationUnit();
    }

    @Test
    public final void testTryWithResources() throws Throwable {
        new TargetJDK1_7().createParser(new StringReader(JDK17_TRY_WITH_RESOURCES)).CompilationUnit();
    }
    
    @Test
    public final void testTryWithResourcesSemi() throws Throwable {
        new TargetJDK1_7().createParser(new StringReader(JDK17_TRY_WITH_RESOURCES_SEMI)).CompilationUnit();
    }
    
    @Test
    public final void testTryWithResourcesMulti() throws Throwable {
        new TargetJDK1_7().createParser(new StringReader(JDK17_TRY_WITH_RESOURCES_MULTI)).CompilationUnit();
    }
    
    @Test
    public final void testTryWithResourcesWithAnnotations() throws Throwable {
        new TargetJDK1_7().createParser(new StringReader(JDK17_TRY_WITH_RESOURCES_WITH_ANNOTATIONS)).CompilationUnit();
    }
    
    @Test
    public final void testTryWithResourcesWithVisitors() throws Throwable {
    	SourceTypeHandler sourceTypeHandler = SourceTypeHandlerBroker.getVisitorsFactoryForSourceType(SourceType.JAVA_17);
    	ASTCompilationUnit rootNode = (ASTCompilationUnit)sourceTypeHandler.getParser().parse(new StringReader(JDK17_TRY_WITH_RESOURCES));
    	sourceTypeHandler.getSymbolFacade().start(rootNode);
    	sourceTypeHandler.getDataFlowFacade().start(rootNode);
    	sourceTypeHandler.getTypeResolutionFacade(getClass().getClassLoader()).start(rootNode);
    }
    
    @Test
    public final void testMulticatch() throws Throwable {
        new TargetJDK1_7().createParser(new StringReader(JDK17_MULTICATCH)).CompilationUnit();
    }
    
    @Test
    public final void testMulticatchWithAnnotations() throws Throwable {
        new TargetJDK1_7().createParser(new StringReader(JDK17_MULTICATCH_WITH_ANNOTATIONS)).CompilationUnit();
    }

    private static final String ANNOTATED_LOCALS =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  @SuppressWarnings(\"foo\") int y = 5;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String ANNOTATED_PARAMS =
            "public class Foo {" + PMD.EOL +
            " void bar(@SuppressWarnings(\"foo\") int x) {}" + PMD.EOL +
            "}";

    private static final String ASSERT_TEST1 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  assert x == 2;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String ASSERT_TEST2 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  assert (x == 2);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String ASSERT_TEST3 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  assert (x==2) : \"hi!\";" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String ASSERT_TEST4 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  assert (x==2) : \"hi!\";" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String ASSERT_TEST5 =
            "public class Foo {" + PMD.EOL +
            "  int assert = 2;" + PMD.EOL +
            "}";


    private static final String ASSERT_TEST5_a =
            "public class Foo {" + PMD.EOL +
            "  void bar() { assert(); }" + PMD.EOL +
            "}";

    private static final String ASSERT_TEST6 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  assert (x == 2) : \"hi!\";" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String ASSERT_TEST7 =
            "public class Foo {" + PMD.EOL +
            " void assert() {}" + PMD.EOL +
            "}";

    private static final String JDK15_ENUM =
            "public class Test {" + PMD.EOL +
            " enum Season { winter, spring, summer, fall };" + PMD.EOL +
            "}";

    private static final String JDK14_ENUM =
            "public class Test {" + PMD.EOL +
            " int enum;" + PMD.EOL +
            "}";

    private static final String JDK15_VARARGS =
            "public class Test {" + PMD.EOL +
            " void bar(Object ... args) {}" + PMD.EOL +
            "}";

    private static final String JDK15_FORLOOP =
            "public class Test {" + PMD.EOL +
            " void foo(List list) {" + PMD.EOL +
            "  for (Integer i : list) {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String JDK15_FORLOOP_WITH_MODIFIER =
            "public class Test {" + PMD.EOL +
            " void foo(List list) {" + PMD.EOL +
            "  for (final Integer i : list) {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String JDK15_GENERICS =
            "public class Test {" + PMD.EOL +
            "  ArrayList<Integer> list =  new ArrayList<Integer>();" + PMD.EOL +
            "}";

    private static final String FIELDS_BUG =
            "public class Test {" + PMD.EOL +
            "  private Foo bar;" + PMD.EOL +
            "}";

    private static final String GT_BUG =
            "public class Test {" + PMD.EOL +
            "  int y = x > 32;" + PMD.EOL +
            "}";

    private static final String ANNOTATIONS_BUG =
            "@Target(ElementType.METHOD)" + PMD.EOL +
            "public @interface Foo {" + PMD.EOL +
            "}";

    private static final String CONSTANT_FIELD_IN_ANNOTATION_BUG =
            "public @interface Foo {" + PMD.EOL +
            "  String CONST = \"foo\";" + PMD.EOL +
            "}";

    private static final String GENERIC_IN_FIELD =
            "public class Foo {" + PMD.EOL +
            " Class<Double> foo = (Class<Double>)clazz;" + PMD.EOL +
            "}";

    private static final String GENERIC_IN_ANNOTATION =
            "public class Foo {" + PMD.EOL +
            " public <A extends Annotation> A foo(Class<A> c) {" + PMD.EOL +
            "  return null;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String INNER_BUG =
            "public class Test {" + PMD.EOL +
            "  void bar() {" + PMD.EOL +
            "   final class Inner {};" + PMD.EOL +
            "   Inner i = new Inner();" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";

    private static final String INNER_BUG2 =
            "public class Test {" + PMD.EOL +
            "  void bar() {" + PMD.EOL +
            "   class Inner {};" + PMD.EOL +
            "   Inner i = new Inner();" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";

    private static final String GENERIC_IN_METHOD_CALL =
            "public class Test {" + PMD.EOL +
            "  List<String> test() {" + PMD.EOL +
            "   return Collections.<String>emptyList();" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";

    private static final String GENERIC_RETURN_TYPE =
            "public class Test {" + PMD.EOL +
            "  public static <String> String test(String x) {" + PMD.EOL +
            "   return x;" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";

    // See java/lang/concurrent/ConcurrentHashMap
    private static final String MULTIPLE_GENERICS =
            "public class Foo<K,V> {" + PMD.EOL +
            "  public <A extends K, B extends V> Foo(Bar<A,B> t) {}" + PMD.EOL +
            "}";

    // See java/lang/concurrent/CopyOnWriteArraySet
    private static final String FUNKY_GENERICS =
            "public class Foo {" + PMD.EOL +
            "  public <T extends E> Foo() {}" + PMD.EOL +
            "}";
    
    private static final String JDK17_NUMERICAL_LITERALS =
      "public class Test {" + PMD.EOL +
      "  int i1 = 0b00011110;" + PMD.EOL +
      "  int i2 = 0B00011110;" + PMD.EOL +
      "  int i3 = 0xA;" + PMD.EOL +
      "  int i4 = 0x1___A_F;" + PMD.EOL +
      "  int i5 = 0b1;" + PMD.EOL +
      "  int i6 = 0b1___1_0;" + PMD.EOL +
      "  int i7 = 0;" + PMD.EOL +
      "  int i8 = 02;" + PMD.EOL +
      "  int i9 = 0_123;" + PMD.EOL +
      "  int i10 = 1;" + PMD.EOL +
      "  int i11 = 1___3;" + PMD.EOL +
      "  int i12 = 1_43_43598_7;" + PMD.EOL +
      "  " + PMD.EOL +
      "  long l1 = 0b00011110L;" + PMD.EOL +
      "  long l2 = 0B00011110l;" + PMD.EOL +
      "  long l3 = 0xAL;" + PMD.EOL +
      "  long l4 = 0x1___A_FL;" + PMD.EOL +
      "  long l5 = 0b1L;" + PMD.EOL +
      "  long l6 = 0b1___1_0L;" + PMD.EOL +
      "  long l7 = 0l;" + PMD.EOL +
      "  long l8 = 02L;" + PMD.EOL +
      "  long l9 = 0_123l;" + PMD.EOL +
      "  long l10 = 1l;" + PMD.EOL +
      "  long l11 = 1___3l;" + PMD.EOL +
      "  long l12 = 1_43_43598_7L;" + PMD.EOL +
      "  long l13 = 1_43_43598_7;" + PMD.EOL +
      "  " + PMD.EOL +
      "  float f1 = .1f;" + PMD.EOL +
      "  float f2 = 1.f;" + PMD.EOL +
      "  float f3 = 0f;" + PMD.EOL +
      "  float f4 = 1e0F;" + PMD.EOL +
      "  float f5 = 1e0f;" + PMD.EOL +
      "  float f6 = 12.345F;" + PMD.EOL +
      "  float f7 = .5____2_1f;" + PMD.EOL +
      "  float f8 = 1__42__3.f;" + PMD.EOL +
      "  float f9 = 0__2_4__324f;" + PMD.EOL +
      "  float f10 = 1_34e0F;" + PMD.EOL +
      "  float f11 = 1__1_2e0f;" + PMD.EOL +
      "  float f12 = 2_1___2.3__4_5F;" + PMD.EOL +
      "  float f13 = 1_34e0__4__3f;" + PMD.EOL +
      "  float f14 = 1__1_2e00__000_4f;" + PMD.EOL +
      "  float f15 = 2_1___2.3__4_5e00______0_5F;" + PMD.EOL +
      "  " + PMD.EOL +
      "  double d1 = .1d;" + PMD.EOL +
      "  double d2 = 1.D;" + PMD.EOL +
      "  double d3 = 0d;" + PMD.EOL +
      "  double d4 = 1e0D;" + PMD.EOL +
      "  double d5 = 1e0d;" + PMD.EOL +
      "  double d6 = 12.345D;" + PMD.EOL +
      "  double d7 = .5____2_1d;" + PMD.EOL +
      "  double d8 = 1__42__3.D;" + PMD.EOL +
      "  double d9 = 0__2_4__324d;" + PMD.EOL +
      "  double d10 = 1_34e0d;" + PMD.EOL +
      "  double d11 = 1__1_2e0d;" + PMD.EOL +
      "  double d12 = 2_1___2.3__4_5D;" + PMD.EOL +
      "  double d13 = 1_34e0__4__3d;" + PMD.EOL +
      "  double d14 = 1__1_2e00__000_4d;" + PMD.EOL +
      "  double d15 = 2_1___2.3__4_5e00______0_5D;" + PMD.EOL +
      "  double d16 = 0.12___34;" + PMD.EOL +
      "  " + PMD.EOL +
      "  float hf1 = 0x.1___AFp1;" + PMD.EOL +
      "  float hf2 = 0x.1___AFp0__0__0f;" + PMD.EOL +
      "  float hf3 = 0x2__3_34.4___AFP00_00f;" + PMD.EOL +
      "  " + PMD.EOL +
      "  double hd1 = 0x.1___AFp1;" + PMD.EOL +
      "  double hd2 = 0x.1___AFp0__0__0d;" + PMD.EOL +
      "  double hd3 = 0x2__3_34.4___AFP00_00d;" + PMD.EOL +
      "  " + PMD.EOL +
      "  int doc1 = 1234_5678;" + PMD.EOL +
      "  long doc2 = 1_2_3_4__5_6_7_8L;" + PMD.EOL +
      "  int doc3 = 0b0001_0010_0100_1000;" + PMD.EOL +
      "  double doc4 = 3.141_592_653_589_793d;" + PMD.EOL +
      "  double doc5 = 0x1.ffff_ffff_ffff_fP1_023;" + PMD.EOL +
      "}" + PMD.EOL
      ;
    
    private static final String JDK17_STRING_IN_SWITCH =
      "public class Test {" + PMD.EOL +
      "	public static void main(String[] args) {" + PMD.EOL +
      "		String mystr = \"value\" + \"2\";" + PMD.EOL +
      "		switch (mystr) {" + PMD.EOL +
      "			case \"value1\":" + PMD.EOL +
      "				break;" + PMD.EOL +
      "			case \"value2\":" + PMD.EOL +
      "				break;" + PMD.EOL +
      "			default:" + PMD.EOL +
      "				break;" + PMD.EOL +
      "		}" + PMD.EOL +
      "	}" + PMD.EOL +
      "}" + PMD.EOL
      ;
    
    private static final String JDK17_GENERIC_DIAMOND =
    	"public class InputJava7Diamond {" + PMD.EOL +
    	" HashMap<String> map = new HashMap<>();" + PMD.EOL +
    	"}";
    
    private static final String JDK17_TRY_WITH_RESOURCES =
    	"public class InputJava7TryWithResources {" + PMD.EOL +
    	" public static void main() {" + PMD.EOL +
    	"  try (MyResource resource = new MyResource()) { }" + PMD.EOL +
    	" }" + PMD.EOL +
    	"}";
    
    private static final String JDK17_TRY_WITH_RESOURCES_SEMI =
    	"public class InputJava7TryWithResources {" + PMD.EOL +
    	" public static void main() {" + PMD.EOL +
    	"  try (MyResource resource = new MyResource();) { }" + PMD.EOL +
    	" }" + PMD.EOL +
    	"}";
    
    private static final String JDK17_TRY_WITH_RESOURCES_MULTI =
    	"public class InputJava7TryWithResources {" + PMD.EOL +
    	" public static void main() {" + PMD.EOL +
    	"  try (MyResource resource = new MyResource(); MyResource2 resource2 = new MyResource2()) { }" + PMD.EOL +
    	" }" + PMD.EOL +
    	"}";
    
    private static final String JDK17_TRY_WITH_RESOURCES_WITH_ANNOTATIONS =
    	"public class InputJava7TryWithResources {" + PMD.EOL +
    	" public static void main() {" + PMD.EOL +
    	"  try (@SuppressWarnings(\"all\") final MyResource resource = new MyResource()) { }" + PMD.EOL +
    	" }" + PMD.EOL +
    	"}";
    
    private static final String JDK17_MULTICATCH =
    	"public class InputJava7Multicatch {" + PMD.EOL +
    	" public static void main() {" + PMD.EOL +
    	"  try { }" + PMD.EOL +
    	"  catch (FileNotFoundException | CustomException e) { }" + PMD.EOL +
    	" }" + PMD.EOL +
    	"}";
    
    private static final String JDK17_MULTICATCH_WITH_ANNOTATIONS =
    	"public class InputJava7Multicatch {" + PMD.EOL +
    	" public static void main() {" + PMD.EOL +
    	"  try { }" + PMD.EOL +
    	"  catch (final @SuppressWarnings(\"all\") FileNotFoundException | CustomException e) { }" + PMD.EOL +
    	" }" + PMD.EOL +
    	"}";
    
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(JDKVersionTest.class);
    }
}
