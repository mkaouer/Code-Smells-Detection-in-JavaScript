/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.util;

import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import soot.*;

public class ClassConstantReader {

  public Type stringToType(String value) {
    int dims = 0;
    while(value.charAt(0) == '['){
      dims++;
      value = value.substring(1);
    }
    if(dims != 0 && value.charAt(0) == 'L'){
      value = value.substring(1, value.length()-2);       
    }
    value = value.replace("/", ".");
    Type base_type = getType(value);
    if(dims != 0){
      return ArrayType.v(base_type, dims);
    } else {
      return base_type;
    }
  }
  
  private Type getType(String constant) {
    if(constant.equals("Z")){
      return BooleanType.v();
    } else if(constant.equals("B")){
      return ByteType.v();
    } else if(constant.equals("S")){
      return ShortType.v();
    } else if(constant.equals("C")){
      return CharType.v();
    } else if(constant.equals("I")){
      return IntType.v();
    } else if(constant.equals("J")){
      return LongType.v();
    } else if(constant.equals("F")){
      return FloatType.v();
    } else if(constant.equals("D")){
      return DoubleType.v();
    } else {
      return RefType.v(constant);
    }
  }
}
