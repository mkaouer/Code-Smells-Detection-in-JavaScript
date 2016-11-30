/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.cpd.cppast.CPPParserTokenManager;
import net.sourceforge.pmd.cpd.cppast.SimpleCharStream;
import net.sourceforge.pmd.cpd.cppast.Token;
import net.sourceforge.pmd.cpd.cppast.TokenMgrError;

import java.io.StringReader;

public class CPPTokenizer implements Tokenizer {
    private static SimpleCharStream charStream;

    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        StringBuffer sb = sourceCode.getCodeBuffer();
        try {
            if (charStream == null) {
                charStream = new SimpleCharStream(new StringReader(sb.toString()));
            } else {
                charStream.ReInit(new StringReader(sb.toString()));
            }
            CPPParserTokenManager.ReInit(charStream);
            CPPParserTokenManager.setFileName(sourceCode.getFileName());
            Token currToken = CPPParserTokenManager.getNextToken();
            while (currToken.image.length() > 0) {
                tokenEntries.add(new TokenEntry(currToken.image, sourceCode.getFileName(), currToken.beginLine));
                currToken = CPPParserTokenManager.getNextToken();
            }
            tokenEntries.add(TokenEntry.getEOF());
            System.err.println("Added " + sourceCode.getFileName());
        } catch (TokenMgrError err) {
            err.printStackTrace();
            System.err.println("Skipping " + sourceCode.getFileName() + " due to parse error");
            tokenEntries.add(TokenEntry.getEOF());
        }
    }
}
