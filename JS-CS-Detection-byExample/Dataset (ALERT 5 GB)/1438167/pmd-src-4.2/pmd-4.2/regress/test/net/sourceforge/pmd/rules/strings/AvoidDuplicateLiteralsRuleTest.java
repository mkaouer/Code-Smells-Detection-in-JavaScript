/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.strings;
 
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.strings.AvoidDuplicateLiteralsRule;

import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import java.util.Set;
 
 public class AvoidDuplicateLiteralsRuleTest extends SimpleAggregatorTst {
     
     @Test
     public void testAll() {
         Rule rule = findRule("strings", "AvoidDuplicateLiterals");
         rule.addProperty("threshold", "2");
         runTests(rule);
     }
 
     @Test
     public void testStringParserEmptyString() {
         AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
         Set res = p.parse("");
         assertTrue(res.isEmpty());
     }
 
     @Test
     public void testStringParserSimple() {
         AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
         Set res = p.parse("a,b,c");
         assertEquals(3, res.size());
         assertTrue(res.contains("a"));
         assertTrue(res.contains("b"));
         assertTrue(res.contains("c"));
     }
 
     @Test
     public void testStringParserEscapedChar() {
         AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
         Set res = p.parse("a,b,\\,");
         assertEquals(3, res.size());
         assertTrue(res.contains("a"));
         assertTrue(res.contains("b"));
         assertTrue(res.contains(","));
     }
 
     @Test
     public void testStringParserEscapedEscapedChar() {
         AvoidDuplicateLiteralsRule.ExceptionParser p = new AvoidDuplicateLiteralsRule.ExceptionParser(',');
         Set res = p.parse("a,b,\\\\");
         assertEquals(3, res.size());
         assertTrue(res.contains("a"));
         assertTrue(res.contains("b"));
         assertTrue(res.contains("\\"));
     }

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(AvoidDuplicateLiteralsRuleTest.class);
     }
 }
