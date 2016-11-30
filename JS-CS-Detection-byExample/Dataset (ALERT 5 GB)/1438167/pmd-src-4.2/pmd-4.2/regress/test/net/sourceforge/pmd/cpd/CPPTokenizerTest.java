package test.net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.CPPTokenizer;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.Tokens;

import org.junit.Test;

public class CPPTokenizerTest {

    @Test
    public void testMultiLineMacros() throws Throwable {
        CPPTokenizer tokenizer = new CPPTokenizer();
        SourceCode code = new SourceCode(new SourceCode.StringCodeLoader(TEST1));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(code, tokens);
        assertEquals(7, tokens.size());
    }

    @Test
    public void testDollarSignInIdentifier() {
        parse(TEST2);
    }

    @Test
    public void testDollarSignStartingIdentifier() {
        parse(TEST3);
    }

    @Test
    public void testWideCharacters() {
        parse(TEST4);
    }

    private void parse(String snippet) {
        CPPTokenizer tokenizer = new CPPTokenizer();
        SourceCode code = new SourceCode(new SourceCode.StringCodeLoader(snippet));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(code, tokens);
    }

    private static final String TEST1 =
            "#define FOO a +\\" + PMD.EOL +
            "            b +\\" + PMD.EOL +
            "            c +\\" + PMD.EOL +
            "            d +\\" + PMD.EOL +
            "            e +\\" + PMD.EOL +
            "            f +\\" + PMD.EOL +
            "            g" + PMD.EOL +
            " void main() {}";

    private static final String TEST2 =
            " void main() { int x$y = 42; }";

    private static final String TEST3 =
            " void main() { int $x = 42; }";

    private static final String TEST4 =
            " void main() { char x = L'a'; }";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CPPTokenizerTest.class);
    }
}
