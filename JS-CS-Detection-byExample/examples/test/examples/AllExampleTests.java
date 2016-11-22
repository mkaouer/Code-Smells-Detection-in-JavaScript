/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licensing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package examples;

//import examples.functionFinder.test.*;
import junit.framework.*;

/**
 * Test suite for all tests of package examples
 *
 * @author Klaus Meffert
 * @since 1.1
 */
public class AllExampleTests
    extends TestSuite {
  /** String containing the CVS revision. Read out via reflection!*/
  private final static String CVS_REVISION = "$Revision: 1.3 $";

  public static junit.framework.Test suite() {
    TestSuite suite = new TestSuite();
//    suite.addTest(AllFormulaFinderTests.suite());
    return suite;
  }
}
