/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.testframework;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.Rule;

import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * Standard methods for (simple) testcases.
 */
@RunWith(SimpleAggregatorTst.CustomXmlTestClassMethodsRunner.class)
/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
public abstract class SimpleAggregatorTst extends RuleTst {
    /**
     * Run a set of tests defined in an XML test-data file for a rule. The file
     * should be ./xml/RuleName.xml relative to the test-class. The format is
     * defined in test-data.xsd.
     */
    public void runTests(Rule rule) {
        runTests(extractTestsFromXml(rule));
    }

    /**
     * Run a set of tests defined in a XML test-data file. The file should be
     * ./xml/[testsFileName].xml relative to the test-class. The format is
     * defined in test-data.xsd.
     */
    public void runTests(Rule rule, String testsFileName) {
        runTests(extractTestsFromXml(rule, testsFileName));
    }

    /**
     * Run a set of tests of a certain sourceType.
     */
    public void runTests(TestDescriptor[] tests) {
        for (int i = 0; i < tests.length; i++) {
            runTest(tests[i]);
        }
    }

    private List<Rule> rules = new ArrayList<Rule>();

    /**
     * Add new XML tests associated with the rule to the test suite. This should
     * be called from the setup method.
     */
    protected void addRule(String ruleSet, String ruleName) {
        rules.add(findRule(ruleSet, ruleName));
    }

    /**
     * Run a set of tests for all rules added in the setup method.
     */
    @Test
    public void testAll() {
        boolean regressionTest = TestDescriptor.inRegressionTestMode();
        ArrayList<Failure> l = new ArrayList<Failure>();
        for (Rule r : rules) {
            TestDescriptor[] tests = extractTestsFromXml(r);
            for (TestDescriptor test: tests) {
                try {
                    if (!regressionTest || test.isRegressionTest()) {
                        runTest(test);
                    }
                } catch (Throwable t) {
                    Failure f = CustomXmlTestClassMethodsRunner.createFailure(r, t);
                    l.add(f);
                }
            }
        }
        for(Failure f: l) {
            CustomXmlTestClassMethodsRunner.addFailure(f);
        }
    }

    public static class CustomXmlTestClassMethodsRunner extends JUnit4ClassRunner {
        public CustomXmlTestClassMethodsRunner(Class<?> klass) throws InitializationError {
            super(klass);
        }

        public static Failure createFailure(Rule rule, Throwable targetException) {
            return new Failure(Description.createTestDescription(
                    SimpleAggregatorTst.class, "xml." + rule.getRuleSetName() + '.' + rule.getName()),
                    targetException);
        }

        public static void addFailure(Failure failure) {
            synchronized(CustomXmlTestClassMethodsRunner.class) {
                NOTIFIER.fireTestFailure(failure);
            }
        }

        @Override
        public void run(RunNotifier n) {
            synchronized(CustomXmlTestClassMethodsRunner.class) {
                // synchronized so that access to NOTIFIER is safe: only
                // one runner at a time is active
                NOTIFIER = n;
                super.run(n);
            }
        }

        private static RunNotifier NOTIFIER;
    }

}
