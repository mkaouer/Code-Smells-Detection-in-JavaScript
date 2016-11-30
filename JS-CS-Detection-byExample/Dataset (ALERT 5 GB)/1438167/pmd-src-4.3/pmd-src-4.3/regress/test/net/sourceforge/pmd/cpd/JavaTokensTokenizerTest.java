/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.JavaTokenizer;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokens;

import org.junit.Test;

public class JavaTokensTokenizerTest {

    @Test
    public void test1() throws Throwable {
        Tokenizer tokenizer = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader("public class Foo {}"));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(sourceCode, tokens);
        assertEquals(6, tokens.size());
        assertEquals("public class Foo {}", sourceCode.getSlice(1, 1));
    }

    @Test
    public void test2() throws Throwable {
        Tokenizer t = new JavaTokenizer();
        String data = "public class Foo {" + PMD.EOL + "public void bar() {}" + PMD.EOL + "public void buz() {}" + PMD.EOL + "}";
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(data));
        Tokens tokens = new Tokens();
        t.tokenize(sourceCode, tokens);
        assertEquals("public class Foo {" + PMD.EOL + "public void bar() {}", sourceCode.getSlice(1, 2));
    }

    @Test
    public void testDiscardSemicolons() throws Throwable {
        Tokenizer t = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader("public class Foo {private int x;}"));
        Tokens tokens = new Tokens();
        t.tokenize(sourceCode, tokens);
        assertEquals(9, tokens.size());
    }

    @Test
    public void testDiscardImports() throws Throwable {
        Tokenizer t = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader("import java.io.File;" + PMD.EOL + "public class Foo {}"));
        Tokens tokens = new Tokens();
        t.tokenize(sourceCode, tokens);
        assertEquals(6, tokens.size());
    }

    @Test
    public void testDiscardPkgStmts() throws Throwable {
        Tokenizer t = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader("package foo.bar.baz;" + PMD.EOL + "public class Foo {}"));
        Tokens tokens = new Tokens();
        t.tokenize(sourceCode, tokens);
        assertEquals(6, tokens.size());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(JavaTokensTokenizerTest.class);
    }
}


