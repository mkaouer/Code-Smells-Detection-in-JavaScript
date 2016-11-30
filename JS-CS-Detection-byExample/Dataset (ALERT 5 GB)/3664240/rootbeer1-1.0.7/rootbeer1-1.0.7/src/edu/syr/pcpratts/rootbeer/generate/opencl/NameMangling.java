/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import soot.SootMethod;
import soot.Type;

public class NameMangling {

  private static NameMangling mInstance = null;
  private Map<String, Integer> mangleMap;
  private int lastInt;
  
  public static NameMangling v(){
    if(mInstance == null)
      mInstance = new NameMangling();
    return mInstance;
  }
  
  private NameMangling(){
    mangleMap = new HashMap<String, Integer>();
    lastInt = 0;
  }

  public String mangleArgs(SootMethod method){
    String ret = "";

    Type return_type = method.getReturnType();
    ret += mangle(return_type);
    
    List parameter_types = method.getParameterTypes();
    for(int i = 0; i < parameter_types.size(); ++i){
      Type type = (Type) parameter_types.get(i);
      ret += mangle(type);
    }
    return ret;
  }

  public String mangle(Type type){
    String name_without_arrays = type.toString();
    name_without_arrays = name_without_arrays.replace("\\[", "a");

    int number;
    if(mangleMap.containsKey(name_without_arrays)){
      number = mangleMap.get(name_without_arrays);
    } else {
      number = lastInt;
      lastInt++;
      mangleMap.put(name_without_arrays, number);
    }

    int dims = arrayDimensions(type);
    String ret = "";
    for(int i = 0; i < dims; ++i)
      ret += "a";
    ret += Integer.toString(number);
    return ret+"_";
  }

  private int arrayDimensions(Type type){
    int ret = 0;
    String str = type.toString();
    for(int i = 0; i < str.length(); ++i){
      char c = str.charAt(i);
      if(c == '[')
        ret++;
    }
    return ret;
  }
}
