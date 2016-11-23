/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest;

import edu.syr.pcpratts.rootbeer.test.TestException;
import edu.syr.pcpratts.rootbeer.test.TestExceptionFactory;
import java.util.ArrayList;
import java.util.List;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.exception.NullPointer1Test;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.exception.NullPointer2Test;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired.ExceptionBasicTest;

public class ExMain implements TestExceptionFactory {

  public List<TestException> getProviders() {
    List<TestException> ret = new ArrayList<TestException>();
    ret.add(new ExceptionBasicTest());
    ret.add(new NullPointer1Test());
    ret.add(new NullPointer2Test());
    return ret;
  }

  
}
