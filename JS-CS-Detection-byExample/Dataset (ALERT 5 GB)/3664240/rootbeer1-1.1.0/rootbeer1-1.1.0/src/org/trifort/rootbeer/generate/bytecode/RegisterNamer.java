/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.bytecode;

public class RegisterNamer {
  static RegisterNamer mInstance = null;
  int count;

  public RegisterNamer(){
    count = 0;
  }

  public static RegisterNamer v(){
    if(mInstance == null)
      mInstance = new RegisterNamer();
    return mInstance;
  }

  public String getName(){
    String ret = "philreg" + Integer.toString(count);
    count++;
    return ret;
  }
}
