/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.trifort.rootbeer.configuration.RootbeerPaths;

import soot.SootMethod;
import soot.Type;

public class NameMangling {

  private static NameMangling m_instance = null;
  private Map<String, Integer> m_mangleMap;
  private int m_lastInt;
  
  public static NameMangling v(){
    if(m_instance == null)
      m_instance = new NameMangling();
    return m_instance;
  }
  
  private NameMangling(){
    m_mangleMap = new HashMap<String, Integer>();
    m_lastInt = 0;
    addBuiltIns();
  }
  
  private void addBuiltIns(){
    addBuiltIn("void");                         //0
    addBuiltIn("boolean");                      //1
    addBuiltIn("byte");                         //2
    addBuiltIn("char");                         //3
    addBuiltIn("short");                        //4
    addBuiltIn("int");                          //5
    addBuiltIn("long");                         //6
    addBuiltIn("float");                        //7
    addBuiltIn("double");                       //8
    addBuiltIn("java.lang.String");             //9
    addBuiltIn("java.lang.StringBuilder");      //10
  }
  
  private void addBuiltIn(String type){
    m_mangleMap.put(type, m_lastInt);
    ++m_lastInt;
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
    if(m_mangleMap.containsKey(name_without_arrays)){
      number = m_mangleMap.get(name_without_arrays);
    } else {
      number = m_lastInt;
      m_lastInt++;
      m_mangleMap.put(name_without_arrays, number);
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
  
  public void writeTypesToFile(){
    try {
      PrintWriter writer = new PrintWriter(RootbeerPaths.v().getRootbeerHome()+"mangling");
      for(String name : m_mangleMap.keySet()){
        int number = m_mangleMap.get(name);
        writer.println(number+" "+name);
      }
      writer.flush();
      writer.close();
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
}
