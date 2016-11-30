/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.entry;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import org.trifort.rootbeer.configuration.Configuration;
import org.trifort.rootbeer.runtime.Rootbeer;
import org.trifort.rootbeer.test.RootbeerTestAgent;
import org.trifort.rootbeer.util.CurrJarName;
import org.trifort.rootbeer.util.ForceGC;

import soot.G;
import soot.Modifier;

public class RootbeerTest {
  
  private String destJAR;
  
  public RootbeerTest(){
    destJAR = "output.jar";
  }
  
  public void runTests(String test_case, boolean run_hard_tests) {
    RootbeerCompiler compiler = new RootbeerCompiler();  
    CurrJarName jar_name = new CurrJarName();
    String rootbeer_jar = jar_name.get();
    try {
      if(test_case == null){
        compiler.compile(rootbeer_jar, destJAR, true);
      } else {
        compiler.compile(rootbeer_jar, destJAR, test_case);
      }
      
      test_case = compiler.getProvider();
      
      //clear out the memory used by soot and compiler
      compiler = null;
      G.reset();
      ForceGC.gc();
      
      runTestCases(test_case, run_hard_tests);
      
    } catch(Exception ex){
      ex.printStackTrace();
      System.exit(-1);
    } 
  }

  public void repeatTests() {
    try {
      runTestCases(null, false);
    } catch(Exception ex){
      ex.printStackTrace();
      System.exit(-1);
    } 
  }
  
  private void runTestCases(String test_case, boolean run_hard_tests) throws Exception {   
    JarClassLoader loader_factory = new JarClassLoader(destJAR);
    ClassLoader cls_loader = loader_factory.getLoader();
    Thread.currentThread().setContextClassLoader(cls_loader);
    
    Class agent_class = cls_loader.loadClass("org.trifort.rootbeer.test.RootbeerTestAgent");
    Object agent_obj = agent_class.newInstance();
    Method[] methods = agent_class.getMethods();
    if(test_case == null){
      Method test_method = findMethodByName("test", methods);
      test_method.invoke(agent_obj, cls_loader, run_hard_tests);
    } else {
      Method test_method = findMethodByName("testOne", methods);
      test_method.invoke(agent_obj, cls_loader, test_case);
    }
  }
  
  
  private Method findMethodByName(String name, Method[] methods){
    for(Method method : methods){
      if(method.getName().equals(name)){
        return method;
      }
    }
    return null;
  }
}
