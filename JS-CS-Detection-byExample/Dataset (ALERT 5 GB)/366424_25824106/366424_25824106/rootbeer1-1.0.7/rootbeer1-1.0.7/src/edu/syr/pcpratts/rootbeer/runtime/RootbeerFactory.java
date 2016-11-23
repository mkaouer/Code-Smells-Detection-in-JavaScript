/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime;

import java.lang.reflect.Constructor;

public class RootbeerFactory {

  //http://java.sun.com/developer/technicalArticles/ALT/Reflection/
  public IRootbeer create() {
    try {
      Class c = Class.forName("edu.syr.pcpratts.rootbeer.runtime.ConcreteRootbeer");
      Constructor<Rootbeer> ctor = c.getConstructor();
      return ctor.newInstance();
    } catch(Exception ex){
      ex.printStackTrace();
      return null;
    }
  }
}
