/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer;

import edu.syr.pcpratts.rootbeer.util.CurrJarName;
import java.lang.reflect.Method;

public class RootbeerTest {
  
  public RootbeerTest(){
  }
  
  public void runTests(String test_case) {
    RootbeerCompiler compiler = new RootbeerCompiler();
    String dest_jar = "output.jar";   
    CurrJarName jar_name = new CurrJarName();
    String rootbeer_jar = jar_name.get();
    try {
      if(test_case == null){
        compiler.compile(rootbeer_jar, dest_jar, true);
      } else {
        compiler.compile(rootbeer_jar, dest_jar, test_case);
      }
      
      test_case = compiler.getProvider();
      
      JarClassLoader loader_factory = new JarClassLoader(dest_jar);
      ClassLoader cls_loader = loader_factory.getLoader();
      Thread.currentThread().setContextClassLoader(cls_loader);
      
      Class agent_class = cls_loader.loadClass("edu.syr.pcpratts.rootbeer.RootbeerTestAgent");
      Object agent_obj = agent_class.newInstance();
      Method[] methods = agent_class.getMethods();
      if(test_case == null){
        Method test_method = findMethodByName("test", methods);
        test_method.invoke(agent_obj, cls_loader);
      } else {
        Method test_method = findMethodByName("testOne", methods);
        test_method.invoke(agent_obj, cls_loader, test_case);
      }
      
    } catch(Exception ex){
      ex.printStackTrace();
      System.exit(-1);
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
