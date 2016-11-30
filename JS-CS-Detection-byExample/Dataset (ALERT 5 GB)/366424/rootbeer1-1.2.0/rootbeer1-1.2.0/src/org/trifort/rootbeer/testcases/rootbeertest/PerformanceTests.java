/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.test.TestSerialization;
import org.trifort.rootbeer.test.TestSerializationFactory;
import org.trifort.rootbeer.testcases.rootbeertest.baseconversion.BaseConversionTest;
import org.trifort.rootbeer.testcases.rootbeertest.gpurequired.BruteForceFFTTest;
import org.trifort.rootbeer.testcases.rootbeertest.ofcoarse.OfCoarse;
import org.trifort.rootbeer.testcases.rootbeertest.serialization.MMult;


public class PerformanceTests implements TestSerializationFactory {
  
  public List<TestSerialization> getProviders() {
    List<TestSerialization> ret = new ArrayList<TestSerialization>();
    ret.add(new MMult());
    ret.add(new OfCoarse());
    ret.add(new BaseConversionTest());
    ret.add(new BruteForceFFTTest());
    return ret;
  }

  public void makeHarder() {
    //ignore
  }
}
