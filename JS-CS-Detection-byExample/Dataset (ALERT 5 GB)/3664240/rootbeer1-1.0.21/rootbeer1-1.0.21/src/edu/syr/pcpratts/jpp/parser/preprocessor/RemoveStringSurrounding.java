/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.parser.preprocessor;

public class RemoveStringSurrounding {

  public String remove(String str){
    if(str.startsWith("\"") || str.startsWith("\'")){
      return str.substring(1, str.length()-1);
    } else {
      return str;
    }
  }
}
