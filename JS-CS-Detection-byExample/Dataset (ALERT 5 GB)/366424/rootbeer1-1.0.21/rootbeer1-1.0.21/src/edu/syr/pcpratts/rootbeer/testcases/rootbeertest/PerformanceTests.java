/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest;

import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import edu.syr.pcpratts.rootbeer.test.TestSerializationFactory;
import java.util.ArrayList;
import java.util.List;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.baseconversion.BaseConversionTest;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired.BruteForceFFTTest;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.ofcoarse.OfCoarse;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.MMult;

public class PerformanceTests implements TestSerializationFactory {
  
  public List<TestSerialization> getProviders() {
    List<TestSerialization> ret = new ArrayList<TestSerialization>();
    ret.add(new MMult());
    ret.add(new OfCoarse());
    ret.add(new BaseConversionTest());
    ret.add(new BruteForceFFTTest());
    return ret;
  }
}
