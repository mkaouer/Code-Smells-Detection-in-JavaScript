/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl;

import edu.syr.pcpratts.rootbeer.classloader.NumberedType;
import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.Tweaks;
import java.util.List;
import soot.RefType;
import soot.Type;
import soot.jimple.InstanceOfExpr;

public class OpenCLInstanceof {

  private Type m_type;
  private OpenCLType m_oclType;
  
  public OpenCLInstanceof(Type type) {
    m_type = type;
    m_oclType = new OpenCLType(m_type);
  }

  public String getPrototype() {
    return getDecl()+";\n";
  }
  
  private String getDecl(){
    String device = Tweaks.v().getDeviceFunctionQualifier();
    String global = Tweaks.v().getGlobalAddressSpaceQualifier();
    
    String ret = device+" boolean "+getMethodName();
    ret += "("+global+" char * gc_info, int thisref, int * exception)";
    return ret;
  }
  
  private String getMethodName(){
    return "edu_syr_pcpratts_rootbeer_instanceof_"+m_oclType.getDerefString();
  }

  public String getBody() {
    if(m_type instanceof RefType == false){
      throw new RuntimeException("not supported yet");
    }
    RefType ref_type = (RefType) m_type;
    List<NumberedType> type_list = RootbeerScene.v().getDfsInfo().getNumberedHierarchyDown(ref_type.getSootClass());
    
    String ret = getDecl();
    ret += "{\n";
    ret += "  if(thisref == -1){\n";
    ret += "    return 0;\n";
    ret += "  }\n";
    ret += "  char * thisref_deref = edu_syr_pcpratts_gc_deref(gc_info, thisref);\n";
    ret += "  GC_OBJ_TYPE_TYPE type = edu_syr_pcpratts_gc_get_type(thisref_deref);\n";
    ret += "  switch(type){\n";
    for(NumberedType ntype : type_list){
      ret += "    case "+ntype.getNumber()+":\n";
    }
    ret += "      return 1;\n";
    ret += "  }\n";
    ret += "  return 0;\n";
    ret += "}\n";
    return ret;
  }
  
  public String invokeExpr(InstanceOfExpr arg0){
    String ret = getMethodName();
    ret += "(gc_info, "+arg0.getOp().toString()+", exception)";
    return ret;
  }
  
  @Override
  public boolean equals(Object other){
    if(other == null){
      return false;
    }
    if(other instanceof OpenCLInstanceof){
      OpenCLInstanceof rhs = (OpenCLInstanceof) other;
      return m_type.equals(rhs.m_type);
    } else {
      return false;
    }
    }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 29 * hash + (this.m_type != null ? this.m_type.hashCode() : 0);
    return hash;
  }
}