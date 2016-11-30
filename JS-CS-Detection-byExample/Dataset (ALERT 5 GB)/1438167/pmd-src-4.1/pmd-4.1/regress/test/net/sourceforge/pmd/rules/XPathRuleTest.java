package test.net.sourceforge.pmd.rules;
 
import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.rules.XPathRule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.RuleTst;

import java.io.StringReader;
 /**
  * @author daniels
  */
 public class XPathRuleTest extends RuleTst {
 
     XPathRule rule;
 
     @Before
     public void setUp() {
         rule = new XPathRule();
         rule.setMessage("XPath Rule Failed");
     }
 
     @Test
     public void testPluginname() throws Throwable {
         Rule rule = new XPathRule();
         rule.addProperty("xpath", "//VariableDeclaratorId[string-length(@Image) < 3]");
         rule.setMessage("{0}");
         rule.addProperty("pluginname", "true");
         PMD p = new PMD();
         RuleContext ctx = new RuleContext();
         Report report = new Report();
         ctx.setReport(report);
         ctx.setSourceCodeFilename("n/a");
         RuleSet rules = new RuleSet();
         rules.addRule(rule);
         p.processFile(new StringReader(TEST1), rules, ctx);
         RuleViolation rv = (RuleViolation) report.iterator().next();
         assertEquals("a", rv.getDescription());
     }
 
     @Test
     public void testVariables() throws Throwable {
         Rule rule = new XPathRule();
         rule.addProperty("xpath", "//VariableDeclaratorId[@Image=$var]");
         rule.setMessage("Avoid vars");
         rule.addProperty("var", "fiddle");
         PMD p = new PMD();
         RuleContext ctx = new RuleContext();
         Report report = new Report();
         ctx.setReport(report);
         ctx.setSourceCodeFilename("n/a");
         RuleSet rules = new RuleSet();
         rules.addRule(rule);
         p.processFile(new StringReader(TEST2), rules, ctx);
         RuleViolation rv = (RuleViolation) report.iterator().next();
         assertEquals(3, rv.getBeginLine());
     }
 
     private static final String TEST1 =
             "public class Foo {" + PMD.EOL +
             " int a;" + PMD.EOL +
             "}";
 
     private static final String TEST2 =
             "public class Foo {" + PMD.EOL +
             " int faddle;" + PMD.EOL +
             " int fiddle;" + PMD.EOL +
             "}";

     public static junit.framework.Test suite() {
         return new junit.framework.JUnit4TestAdapter(XPathRuleTest.class);
     }
 }
