package test.net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.AnyTokenizer;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.Tokens;

import org.junit.Test;

public class AnyTokenizerTest {

    @Test
    public void testMultiLineMacros() throws Throwable {
        AnyTokenizer tokenizer = new AnyTokenizer();
        SourceCode code = new SourceCode(new SourceCode.StringCodeLoader(TEST1));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(code, tokens);
        assertEquals(30, tokens.size());
    }

    private static final String TEST1 =
            "using System;" + PMD.EOL +
            "namespace HelloNameSpace {" + PMD.EOL +
            "" + PMD.EOL +
            "    public class HelloWorld {" + PMD.EOL +
            "        static void Main(string[] args) {" + PMD.EOL +
            "            Console.WriteLine(\"Hello World!\");" + PMD.EOL +
            "        }" + PMD.EOL +
            "    }" + PMD.EOL +
            "}" + PMD.EOL;

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(AnyTokenizerTest.class);
    }
}
