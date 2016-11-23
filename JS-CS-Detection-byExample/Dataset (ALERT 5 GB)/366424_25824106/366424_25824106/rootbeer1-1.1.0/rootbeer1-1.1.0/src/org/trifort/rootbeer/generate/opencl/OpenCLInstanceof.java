/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.trifort.rootbeer.generate.opencl.tweaks.Tweaks;

import soot.RefType;
import soot.SootClass;
import soot.Type;
import soot.jimple.InstanceOfExpr;
import soot.rbclassload.ClassHierarchy;
import soot.rbclassload.HierarchyGraph;
import soot.rbclassload.NumberedType;
import soot.rbclassload.RootbeerClassLoader;
import soot.rbclassload.StringNumbers;

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
    
    String ret = device+" char "+getMethodName();
    ret += "("+global+" char * gc_info, int thisref, int * exception)";
    return ret;
  }
  
  private String getMethodName(){
    return "org_trifort_rootbeer_instanceof_"+m_oclType.getDerefString();
  }

  public String getBody() {
    if(m_type instanceof RefType == false){
      throw new RuntimeException("not supported yet");
    }
    RefType ref_type = (RefType) m_type;    
    List<NumberedType> type_list = getTypeList(ref_type.getSootClass());
    
    String ret = getDecl();
    ret += "{\n";
    ret += "  char * thisref_deref;\n";
    ret += "  GC_OBJ_TYPE_TYPE type;\n";
    ret += "  if(thisref == -1){\n";
    ret += "    return 0;\n";
    ret += "  }\n";
    ret += "  thisref_deref = org_trifort_gc_deref(gc_info, thisref);\n";
    ret += "  type = org_trifort_gc_get_type(thisref_deref);\n";
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

  private List<NumberedType> getTypeList(SootClass soot_class) {
    
    ClassHierarchy class_hierarchy = RootbeerClassLoader.v().getClassHierarchy();
    HierarchyGraph hgraph = class_hierarchy.getHierarchyGraph();

    Set<Integer> visited = new TreeSet<Integer>();
    visited.add(StringNumbers.v().addString(soot_class.getName()));
    LinkedList<String> queue = new LinkedList<String>();
    queue.add(soot_class.getName());
    
    Set<Integer> new_invokes = RootbeerClassLoader.v().getNewInvokes();
    List<NumberedType> ret = new ArrayList<NumberedType>();
    
    while(queue.isEmpty() == false){
      String entry = queue.removeFirst();
      Integer num = StringNumbers.v().addString(entry);
      if(new_invokes.contains(num)){
        NumberedType ntype = class_hierarchy.getNumberedType(entry);
        ret.add(ntype);
      }
      
      Set<Integer> children = hgraph.getChildren(num);
      for(Integer child : children){
        if(visited.contains(child)){
          continue;
        }
        visited.add(child);
        queue.add(StringNumbers.v().getString(child));
      }
    }
    
    return ret;
  }
}