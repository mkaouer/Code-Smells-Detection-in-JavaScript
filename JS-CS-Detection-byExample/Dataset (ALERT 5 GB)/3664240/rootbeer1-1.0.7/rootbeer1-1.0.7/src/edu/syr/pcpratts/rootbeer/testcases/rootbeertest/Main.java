/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest;

import edu.syr.pcpratts.rootbeer.runtime.Rootbeer;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import edu.syr.pcpratts.rootbeer.test.TestSerializationFactory;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.arraysum.ArraySumTest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.exception.NullPointer4Test;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.gpurequired.*;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.remaptest.RemapTest;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.ByteArrays;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.CharArrays;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.DoubleArrays;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.FloatArrays;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.GpuListTest;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.IntArrays;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.LongArrays;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.NativeStrictMathTest;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.OuterClassTest;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.OuterClassTest2;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.OuterClassTest3;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.RefTypeArrays;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.SameClassUsedTwiceTest1;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.SameClassUsedTwiceTest2;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.ShortArrays;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.SimpleTest;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.StrictMathTest;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization.StringTest;

public class Main implements TestSerializationFactory {

  public List<TestSerialization> getProviders() {
    List<TestSerialization> ret = new ArrayList<TestSerialization>();
    ret.add(new NewOnGpu());
    ret.add(new SimpleTest());
    ret.add(new LongArrays());
    ret.add(new ByteArrays());
    ret.add(new ByteArrays());
    ret.add(new CharArrays());
    ret.add(new ShortArrays());
    ret.add(new IntArrays());
    ret.add(new FloatArrays());
    ret.add(new DoubleArrays());
    ret.add(new StaticsTest1());
    ret.add(new StaticsTest2());
    ret.add(new StaticsTest3());
    ret.add(new LargeMemTest());
    ret.add(new NullPointer4Test());
    ret.add(new MultiArrayTest());
    ret.add(new OuterClassTest());
    ret.add(new OuterClassTest2());
    ret.add(new OuterClassTest3());
    ret.add(new SameClassUsedTwiceTest1());
    ret.add(new SameClassUsedTwiceTest2());
    ret.add(new RefTypeArrays());
    ret.add(new StrictMathTest()); 
    //ret.add(new AtomicLongTest());
    ret.add(new NativeStrictMathTest());  
    ret.add(new SimpleSynchronizedTest());
    ret.add(new SynchronizedMethodTest());
    ret.add(new SynchronizedMethod2Test());
    ret.add(new SynchronizedMethod3Test());
    ret.add(new SynchronizedStaticMethodTest());
    ret.add(new SynchronizedObjectTest());
    ret.add(new SuperClass());
    ret.add(new StringTest());
    ret.add(new StepFilterTest());
    ret.add(new GpuListTest());
    //ret.add(new WhileTrueTest());
    ret.add(new ArraySumTest());
    ret.add(new RemapTest());
    return ret;
  }
}
