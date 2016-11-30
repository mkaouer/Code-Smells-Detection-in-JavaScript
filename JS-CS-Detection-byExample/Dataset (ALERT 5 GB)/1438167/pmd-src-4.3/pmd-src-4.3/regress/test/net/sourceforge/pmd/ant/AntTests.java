/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.ant;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * Tests for the net.sourceforge.pmd.ant package
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id: AntTests.java 5043 2007-02-09 01:38:14Z allancaplan $
 */
@RunWith(Suite.class)
@SuiteClasses({FormatterTest.class, PMDTaskTest.class})
public class AntTests {
}


/*
 * $Log$
 * Revision 1.6  2007/02/09 01:37:59  allancaplan
 * Moving to JUnit 4
 *
 * Revision 1.5  2006/02/10 14:26:25  tomcopeland
 * Huge reformatting checkin
 *
 * Revision 1.4  2006/02/10 14:15:19  tomcopeland
 * Latest source from Pieter, everything compiles and all the tests pass with the exception of a few missing rules in basic-jsp.xml
 *
 * Revision 1.3  2003/11/20 16:01:01  tomcopeland
 * Changing over license headers in the source code
 *
 * Revision 1.2  2003/10/07 18:49:19  tomcopeland
 * Added copyright headers
 *
 * Revision 1.1  2003/09/29 14:32:30  tomcopeland
 * Committed regression test suites, thanks to Boris Gruschko
 *
 */
