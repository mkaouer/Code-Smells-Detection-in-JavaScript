/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.ofcoarse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.Rootbeer;
import org.trifort.rootbeer.test.TestSerialization;
import org.trifort.rootbeer.test.TestSerializationFactory;

public class Main implements TestSerializationFactory {

  public List<TestSerialization> getProviders() {
    List<TestSerialization> ret = new ArrayList<TestSerialization>();
    ret.add(new OfCoarse());
    return ret;
  }
  
  public void makeHarder() {
    //ignore
  }
}
