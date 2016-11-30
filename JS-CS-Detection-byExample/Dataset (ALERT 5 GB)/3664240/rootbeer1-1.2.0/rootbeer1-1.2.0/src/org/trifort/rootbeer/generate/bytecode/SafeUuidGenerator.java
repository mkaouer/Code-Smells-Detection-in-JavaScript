/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.bytecode;

import java.util.UUID;

public class SafeUuidGenerator {

  public static String get(){
    String ret = UUID.randomUUID().toString();
    ret = ret.replaceAll("-", "");
    return ret;
  }
}
