/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.test;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.test.TestException;
import org.trifort.rootbeer.test.TestExceptionFactory;
import org.trifort.rootbeer.testcases.rootbeertest.exception.NullPointer1Test;
import org.trifort.rootbeer.testcases.rootbeertest.exception.NullPointer2Test;
import org.trifort.rootbeer.testcases.rootbeertest.gpurequired.ExceptionBasicTest;


public class ExMain implements TestExceptionFactory {

  public List<TestException> getProviders() {
    List<TestException> ret = new ArrayList<TestException>();
    ret.add(new ExceptionBasicTest());
    ret.add(new NullPointer1Test());
    ret.add(new NullPointer2Test());
    return ret;
  }

  
}
