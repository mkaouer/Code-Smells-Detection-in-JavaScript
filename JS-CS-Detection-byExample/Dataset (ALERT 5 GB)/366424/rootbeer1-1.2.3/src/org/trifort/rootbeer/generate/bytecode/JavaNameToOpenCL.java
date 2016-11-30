/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.bytecode;

public class JavaNameToOpenCL {

  public static String convert(String classname){
    String ret = "_";
    ret += classname.replace(".", "_");
    return ret;
  }
}
