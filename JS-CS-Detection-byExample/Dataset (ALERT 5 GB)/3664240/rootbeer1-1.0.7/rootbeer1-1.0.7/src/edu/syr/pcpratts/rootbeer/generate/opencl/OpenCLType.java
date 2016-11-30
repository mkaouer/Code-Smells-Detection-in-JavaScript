/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl;

import soot.RefLikeType;
import soot.RefType;
import soot.Type;

public class OpenCLType {
  private final Type m_sootType;

  public OpenCLType(Type soot_type){
    m_sootType = soot_type;
  }

  public String getRefString(){
    if(m_sootType instanceof RefLikeType)
      return "int";
    String ret = getDerefString();
    if(ret.equals("long"))
      return "long long";
    return ret;
  }

  public String getDerefString(){
    String ret = m_sootType.toString();
    ret = ret.replaceAll("\\.", "_");
    ret = ret.replaceAll("\\[\\]", "__array");
    return ret;
  }

  public boolean isRefType(){
    if(m_sootType instanceof RefLikeType)
      return true;
    return false;
  }
  
  public int getSize(){
    if(m_sootType instanceof RefLikeType)
      return 4;
    String type = m_sootType.toString();
    if(type.equals("byte"))
      return 1;
    if(type.equals("boolean"))
      return 1;
    if(type.equals("char"))
      return 4;
    if(type.equals("short"))
      return 2;
    if(type.equals("int"))
      return 4;
    if(type.equals("long"))
      return 8;
    if(type.equals("float"))
      return 4;
    if(type.equals("double"))
      return 8;
    throw new RuntimeException("Unknown type");
  }

  public Type getSootType() {
    return m_sootType;
  }

  public Type getCapitalType() {
    if(m_sootType instanceof RefLikeType)
      return m_sootType;
    String type = m_sootType.toString();
    if(type.equals("byte"))
      return RefType.v("java.lang.Byte");
    if(type.equals("boolean"))
      return RefType.v("java.lang.Boolean");
    if(type.equals("char"))
      return RefType.v("java.lang.Character");
    if(type.equals("short"))
      return RefType.v("java.lang.Short");
    if(type.equals("int"))
      return RefType.v("java.lang.Integer");
    if(type.equals("long"))
      return RefType.v("java.lang.Long");
    if(type.equals("float"))
      return RefType.v("java.lang.Float");
    if(type.equals("double"))
      return RefType.v("java.lang.Double");
    throw new RuntimeException("Unknown type");
  }

  public String getName() {
    return m_sootType.toString();
  }

  public String getCapitalName() {
    String ret = getName();
    Character first = ret.charAt(0);
    String first_str = "" + first;
    first_str = first_str.toUpperCase();
    return first_str + ret.substring(1);
  }
}
