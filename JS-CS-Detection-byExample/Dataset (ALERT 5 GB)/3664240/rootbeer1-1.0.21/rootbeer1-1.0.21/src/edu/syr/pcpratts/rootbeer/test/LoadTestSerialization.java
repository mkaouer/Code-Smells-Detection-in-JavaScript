/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.test;

import java.util.List;

public class LoadTestSerialization {

  public List<TestSerialization> load(ClassLoader loader, String class_name) throws Exception {
    Object instance = doLoad(loader, class_name);
    TestSerializationFactory factory = (TestSerializationFactory) instance;
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
}
