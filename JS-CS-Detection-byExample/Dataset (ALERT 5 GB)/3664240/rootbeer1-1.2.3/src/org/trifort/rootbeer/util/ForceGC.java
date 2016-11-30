/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */
package org.trifort.rootbeer.util;

import java.lang.ref.WeakReference;

public class ForceGC {

  //http://stackoverflow.com/questions/1481178/forcing-garbage-collection-in-java
  public static void gc() {  
    Object obj = new Object();
    WeakReference ref = new WeakReference<Object>(obj);
    obj = null;
    for(int i = 0; i < 3; ++i){
      if(ref.get() == null){
        break;
      }
      System.gc();
    }
  }
}
