/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.test;

import java.util.List;

public class LoadTestSerialization {

  public List<TestSerialization> load(ClassLoader loader, String class_name, boolean run_hard_tests) throws Exception {
    Object instance = doLoad(loader, class_name);
    TestSerializationFactory factory = (TestSerializationFactory) instance;
    if(run_hard_tests){
      factory.makeHarder();
    }
    return factory.getProviders();
  }
  
  public List<TestKernelTemplate> loadKernelTemplate(ClassLoader loader, String class_name) throws Exception {
    Object instance = doLoad(loader, class_name);
    TestKernelTemplateFactory factory = (TestKernelTemplateFactory) instance;
    return factory.getProviders();
  }

  public List<TestException> loadException(ClassLoader loader, String class_name) throws Exception {
    Object instance = doLoad(loader, class_name);
    TestExceptionFactory factory = (TestExceptionFactory) instance;
    return factory.getProviders();
  }
  
  private Object doLoad(ClassLoader loader, String class_name) throws Exception {
    Class classToLoad = Class.forName(class_name, true, loader);
    Object instance = classToLoad.newInstance();
    return instance;
  }

  List<TestApplication> loadApplication(ClassLoader loader, String class_name) throws Exception {
    Object instance = doLoad(loader, class_name);
    TestApplicationFactory factory = (TestApplicationFactory) instance;
    return factory.getProviders();
  }
}
