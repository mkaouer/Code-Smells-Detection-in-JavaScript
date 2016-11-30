/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.test.serialization;

import edu.syr.pcpratts.rootbeer.JarClassLoader;
import edu.syr.pcpratts.rootbeer.RootbeerCompiler;
import edu.syr.pcpratts.rootbeer.test.LoadTestSerialization;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import java.util.List;

public class GenerateSerializersAndTest {

  public boolean run(String jar_file, String out_jar_file){
    try {
      RootbeerCompiler compiler = new RootbeerCompiler();
      compiler.compile(jar_file, out_jar_file);
      return test(out_jar_file);
    } catch(Exception ex){
      ex.printStackTrace();
      return false;
    }
  }

  private boolean test(String dest_jar) throws Exception {
    JarClassLoader loader_factory = new JarClassLoader(dest_jar);
    ClassLoader cls_loader = loader_factory.getLoader();
    Thread.currentThread().setContextClassLoader(cls_loader);
    
    LoadTestSerialization loader = new LoadTestSerialization();
    List<TestSerialization> creators = loader.load(cls_loader, "rootbeertest.serialization.Main");
    for(TestSerialization creator : creators){
      System.out.println("Testing: "+creator.toString()+"...");
      SerializationTester tester = new SerializationTester(creator);
      boolean ret = tester.test();
      if(ret){
        System.out.println(" PASSED");
      } else {
        System.out.println();
        return false;
      }
    }
    return true;
  }

}
