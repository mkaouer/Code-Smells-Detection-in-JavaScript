/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization;

import edu.syr.pcpratts.rootbeer.runtime.Rootbeer;
import edu.syr.pcpratts.rootbeer.runtime.RootbeerFactory;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import edu.syr.pcpratts.rootbeer.test.TestSerializationFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.SuperClass;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.baseconversion.BaseConversionTest;
import edu.syr.pcpratts.rootbeer.testcases.rootbeertest.ofcoarse.OfCoarse;

public class Main implements TestSerializationFactory {

  public List<TestSerialization> getProviders() {
    List<TestSerialization> ret = new ArrayList<TestSerialization>();
    ret.add(new SimpleTest());
    ret.add(new LongArrays());
    ret.add(new ByteArrays());
    ret.add(new ByteArrays());
    ret.add(new CharArrays());
    ret.add(new ShortArrays());
    ret.add(new IntArrays());
    ret.add(new FloatArrays());
    ret.add(new DoubleArrays());
    ret.add(new RefTypeArrays());
    ret.add(new StrictMathTest());    
    ret.add(new OuterClassTest());
    ret.add(new OuterClassTest2());
    ret.add(new OuterClassTest3());
    ret.add(new SameClassUsedTwiceTest1());
    ret.add(new SameClassUsedTwiceTest2());
    ret.add(new NativeStrictMathTest());
    ret.add(new MMult());
    return ret;
  }

  public static void main(String[] args){
    Main m = new Main();
    List<TestSerialization> providers = m.getProviders();
    for(TestSerialization provider : providers){
      m.test(provider);
    }
  }

  private void test(TestSerialization provider) {
    System.out.print("Testing provider: "+provider.toString()+"...");
    List<Kernel> objects = provider.create();
    List<Kernel> known_goods = provider.create();

    Rootbeer rootbeer = new Rootbeer();
    int i = 0;
    Iterator<Kernel> iter = rootbeer.run(objects.iterator());
    while(iter.hasNext()){
      Kernel result = iter.next();
      Kernel known_good = known_goods.get(i);
      known_good.gpuMethod();

      if(!provider.compare(result, known_good)){
        System.out.println("Failed at: "+i);
        System.exit(-1);
      }

      ++i;
    }
    System.out.println("PASSED");
  }
}
