/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.test;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.trifort.rootbeer.runtime.Rootbeer;
import org.trifort.rootbeer.test.TestSerialization;
import org.trifort.rootbeer.test.TestSerializationFactory;
import org.trifort.rootbeer.testcases.rootbeertest.SuperClass;
import org.trifort.rootbeer.testcases.rootbeertest.arraysum.ArraySumTest;
import org.trifort.rootbeer.testcases.rootbeertest.canonical.CanonicalTest;
import org.trifort.rootbeer.testcases.rootbeertest.exception.NullPointer4Test;
import org.trifort.rootbeer.testcases.rootbeertest.gpurequired.*;
import org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate.FastMatrixTest;
import org.trifort.rootbeer.testcases.rootbeertest.remaptest.RemapTest;
import org.trifort.rootbeer.testcases.rootbeertest.serialization.*;


public class Main implements TestSerializationFactory {

  private boolean m_hardTests;
  
  public Main(){
    m_hardTests = false;
  }
  
  public void makeHarder(){
    m_hardTests = true;
  }
  
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
    ret.add(new NullPointer4Test());
    ret.add(new MultiArrayTest());
    ret.add(new OuterClassTest());
    ret.add(new OuterClassTest2());
    ret.add(new OuterClassTest3());
    ret.add(new SameClassUsedTwiceTest1());
    ret.add(new SameClassUsedTwiceTest2());
    ret.add(new RefTypeArrays());
    ret.add(new StrictMathTest()); 
    ret.add(new AtomicLongTest());
    ret.add(new NativeStrictMathTest());  
    ret.add(new SuperClass());
    ret.add(new StringTest());
    ret.add(new String2Test());
    ret.add(new StepFilterTest());
    ret.add(new GpuListTest());
    ret.add(new WhileTrueTest());
    ret.add(new ArraySumTest());
    ret.add(new RemapTest());
    ret.add(new InstanceofTest());
    ret.add(new DotClassTest());
    ret.add(new PrintTest());
    ret.add(new BarrierTest());
    ret.add(new SharedMemSimpleTest());
    ret.add(new StringConstantTest());
    ret.add(new AbstractTest());
    ret.add(new PairHmmJimpleTest());
    ret.add(new AutoboxingTest());
    ret.add(new Autoboxing2Test());
    ret.add(new ChangeThreadTest());
    ret.add(new ShiftTest());
    ret.add(new ArrayListTest());
    ret.add(new ArrayLengthTest());
    ret.add(new ByteByteValueTest());
    ret.add(new StringArrayTest1());
    ret.add(new CmplInfTest());
    ret.add(new LinkedListTest());
    ret.add(new HashSetTest());
    ret.add(new TreeSetTest());
    ret.add(new LinkedHashSetTest());
    ret.add(new HashMapTest());
    ret.add(new TreeMapTest());
    ret.add(new LinkedHashMapTest());
    ret.add(new CovarientTest());
    ret.add(new GpuMethodTest());
    ret.add(new RegularExpressionTest());
    ret.add(new StringBuilderTest1());
    ret.add(new StringBuilderTest2());
    ret.add(new DoubleToStringTest());
    ret.add(new FloatToStringTest());
    ret.add(new ZeroLengthArrayTest());
    ret.add(new ArraysSortTest());
    ret.add(new ArrayCloneTest());
    ret.add(new StringSplitTest());
    ret.add(new StringIndexOfTest());
    ret.add(new SubstringTest());
    ret.add(new ArraysSortComparatorTest());
    ret.add(new IntegerToStringTest());
    ret.add(new LongToStringTest());
    ret.add(new StringToIntegerTest());
    ret.add(new StringToLongTest());
    ret.add(new StringToFloatTest());
    ret.add(new StringToDoubleTest());
    ret.add(new ByteToStringTest());
    ret.add(new CharToStringTest());
    ret.add(new BooleanToStringTest());
    ret.add(new ShortToStringTest());
    ret.add(new ObjectToStringTest());
    ret.add(new StringBuilderTest3());
    ret.add(new StaticInitTest());
    ret.add(new PolymorphicNewTest());
    ret.add(new NestedMonitorTest());
    ret.add(new SimpleSynchronizedTest());
    ret.add(new SynchronizedMethodTest());
    ret.add(new SynchronizedMethod2Test());
    ret.add(new SynchronizedMethod3Test());
    ret.add(new SynchronizedStaticMethodTest());
    ret.add(new SynchronizedObjectTest());
    ret.add(new ForceArrayNewTest());
    ret.add(new CanonicalTest());
    ret.add(new GpuVectorMapTest());
    ret.add(new StringBuilderTest4());
            
    if(org.trifort.rootbeer.entry.Main.largeMemTests()){
      ret.add(new LargeMemTest());
    }
    
    return ret;
  }
}
