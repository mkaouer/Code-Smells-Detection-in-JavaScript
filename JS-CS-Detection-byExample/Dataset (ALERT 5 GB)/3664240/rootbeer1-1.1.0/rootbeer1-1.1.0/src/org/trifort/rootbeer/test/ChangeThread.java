/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.test;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.testcases.rootbeertest.gpurequired.ChangeThreadTest;

public class ChangeThread implements TestSerializationFactory {

  public List<TestSerialization> getProviders() {
    List<TestSerialization> ret = new ArrayList<TestSerialization>();
    ret.add(new ChangeThreadTest());
    return ret;
  }

  public void makeHarder() {
    //ignore
  }

}
