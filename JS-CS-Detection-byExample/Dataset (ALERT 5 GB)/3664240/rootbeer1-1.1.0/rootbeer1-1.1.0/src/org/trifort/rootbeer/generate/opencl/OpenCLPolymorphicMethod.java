/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.trifort.rootbeer.generate.opencl.tweaks.Tweaks;

import soot.*;
import soot.rbclassload.ClassHierarchy;
import soot.rbclassload.HierarchyGraph;
import soot.rbclassload.MethodSignatureUtil;
import soot.rbclassload.RootbeerClassLoader;

/**
 * Represents an OpenCL function that dispatches to the real OpenCL function
 * implementing the behavior of a certain classes version of a virtual method.
 * @author pcpratts
 */
public class OpenCLPolymorphicMethod {

  private final SootMethod m_sootMethod;
  private MethodSignatureUtil m_util;

  public OpenCLPolymorphicMethod(SootMethod soot_method){
    m_sootMethod = soot_method;
    m_util = new MethodSignatureUtil();
  }

  public String getMethodPrototypes(){
    if(m_sootMethod.getName().equals("<init>"))
      return "";
    List<String> decls = getMethodDecls();
    StringBuilder ret = new StringBuilder();
    for(String decl : decls){
      decl += ";\n";
      ret.append(decl);
    }
    return ret.toString();
  }

  private List<String> getMethodDecls(){
    List<SootMethod> virtual_methods = getVirtualMethods();
    
    List<String> ret = new ArrayList<String>();
    for(SootMethod virtual_method : virtual_methods){
      SootClass soot_class = virtual_method.getDeclaringClass();
      OpenCLMethod ocl_method = new OpenCLMethod(m_sootMethod, soot_class);

      StringBuilder builder = new StringBuilder();
      builder.append(Tweaks.v().getDeviceFunctionQualifier()+" ");
      builder.append(ocl_method.getReturnString());
      builder.append(" invoke_"+ocl_method.getPolymorphicName());
      builder.append(ocl_method.getArgumentListStringPolymorphic());
      ret.add(builder.toString());
    }
    return ret;
  }

  public Set<String> getMethodBodies(){
    if(m_sootMethod.getName().equals("<init>"))
      return new HashSet<String>();
    List<String> decls = getMethodDecls();
    Set<String> ret = new HashSet<String>();
    for(String decl : decls){
      String method = getMethodBody(decl);
      ret.add(method);
    }
    return ret;
  }
  
  private List<SootMethod> getVirtualMethods(){
    ClassHierarchy class_hierarchy = RootbeerClassLoader.v().getClassHierarchy();
    List<String> virtual_methods = class_hierarchy.getVirtualMethods(m_sootMethod.getSignature());
    List<SootMethod> ret = new ArrayList<SootMethod>();
    for(String virtual_method : virtual_methods){
      m_util.parse(virtual_method);
      SootMethod soot_method = m_util.getSootMethod();
      if(soot_method.isConcrete()){
        ret.add(soot_method);
      }
    }
    if(ret.contains(m_sootMethod) == false){
      ret.add(m_sootMethod);
    }
    return ret;
  }
  
  public String getMethodBody(String decl){
    StringBuilder ret = new StringBuilder();
    String address_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    //write function signature
    ret.append(decl);
    ret.append("{\n");
    
    List<SootMethod> virtual_methods = getVirtualMethods();
    if(m_sootMethod.isStatic()){
      if(m_sootMethod.getReturnType() instanceof VoidType == false){
        ret.append("return ");
      }
      Type first_type = m_sootMethod.getDeclaringClass().getType();
      RefType ref_type = (RefType) first_type;
      SootClass first_class = ref_type.getSootClass();
      String invoke_string = getStaticInvokeString(first_class);
      ret.append(invoke_string+"\n");
    } else {
      ret.append(address_qual+" char * thisref_deref;\n");
      ret.append("GC_OBJ_TYPE_TYPE derived_type;\n");
      ret.append("if(thisref == -1){\n");
      ret.append("  *exception = -2;\n");
      ret.append("return ");
      if(m_sootMethod.getReturnType() instanceof VoidType == false)
        ret.append("-1");
      ret.append(";\n");
      ret.append("}\n");
      ret.append("thisref_deref = org_trifort_gc_deref(gc_info, thisref);\n");
      if(virtual_methods.size() == 1){
        SootClass sclass = virtual_methods.get(0).getDeclaringClass();
        String invoke_string = getInvokeString(sclass);
        if(m_sootMethod.getReturnType() instanceof VoidType == false){
          ret.append("return ");
        }
        ret.append(invoke_string+"\n");
      } else {
        ret.append("derived_type = org_trifort_gc_get_type(thisref_deref);\n");
        ret.append("if(0){}\n");
        int count = 0;
        List<SootMethod> used_methods = new ArrayList<SootMethod>();
        for(SootMethod method : virtual_methods){
          SootClass sclass = method.getDeclaringClass();
          if(sclass.isInterface()){
            continue;
          }
          String invoke_string = getInvokeString(sclass);
          if(invoke_string == ""){
            continue;
          }
          used_methods.add(method);
        }
        Collections.sort(used_methods, new VirtualMethodComparator());
        for(SootMethod method : used_methods){
          SootClass sclass = method.getDeclaringClass();
          String invoke_string = getInvokeString(sclass);
        
          ret.append("else if(derived_type == "+RootbeerClassLoader.v().getClassNumber(sclass)+"){\n");
          if(m_sootMethod.getReturnType() instanceof VoidType == false){
            ret.append("return ");
          }
          ret.append(invoke_string+"\n");
          ret.append("}\n");
          count++;
        }
      }
    }
    ret.append("return ");
    if(m_sootMethod.getReturnType() instanceof VoidType == false)
      ret.append("-1");
    ret.append(";\n");
    ret.append("}\n");
    return ret.toString();
  }

  //used to invoke polymorphic method inside this function
  private String getInvokeString(SootClass start_class){
    if(m_sootMethod.getName().equals("<init>"))
      return "";
        
    SootClass soot_class = start_class;
    OpenCLMethod ocl_method = new OpenCLMethod(m_sootMethod, soot_class);
    String ret = ocl_method.getPolymorphicName() + "(";

    //write the gc_info and thisref
    ret += "gc_info, thisref";
    List args = m_sootMethod.getParameterTypes();
    if(args.size() != 0)
      ret += ", ";

    for(int i = 0; i < args.size(); ++i){
      ret += "parameter" + Integer.toString(i);
      if(i < args.size() - 1)
        ret += ", ";
    }
    ret += ", exception);";
    return ret;
  }

  private String getStaticInvokeString(SootClass soot_class) {
    if(m_sootMethod.getName().equals("<init>"))
      return "";
    OpenCLMethod ocl_method = new OpenCLMethod(m_sootMethod, soot_class);
    String ret = ocl_method.getPolymorphicName() + "(";

    //write the gc_info and thisref
    ret += "gc_info";
    List args = m_sootMethod.getParameterTypes();
    if(args.size() != 0)
      ret += ", ";

    for(int i = 0; i < args.size(); ++i){
      ret += "parameter" + Integer.toString(i);
      if(i < args.size() - 1)
        ret += ", ";
    }
    ret += ", exception);";
    return ret;
  }

  public class VirtualMethodComparator implements
      Comparator<SootMethod> {

    @Override
    public int compare(SootMethod lhs, SootMethod rhs) {
      SootClass lhs_class = lhs.getDeclaringClass();
      SootClass rhs_class = rhs.getDeclaringClass();
      
      Integer lhs_number = RootbeerClassLoader.v().getClassNumber(lhs_class);
      Integer rhs_number = RootbeerClassLoader.v().getClassNumber(rhs_class);
      return lhs_number.compareTo(rhs_number);
    }
  }
}
