/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl;

import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.Tweaks;
import java.util.List;
import soot.*;

/**
 * Represents an OpenCL function that dispatches to the real OpenCL function
 * implementing the behavior of a certain classes version of a virtual method.
 * @author pcpratts
 */
public class OpenCLPolymorphicMethod {
  private final SootMethod m_sootMethod;

  //for hashcode
  private List<Type> m_hierarchy;

  public OpenCLPolymorphicMethod(SootMethod soot_method){
    m_sootMethod = soot_method;
  }

  public String getMethodPrototype(){
    if(m_sootMethod.getName().equals("<init>"))
      return "";
    return getMethodDecl()+";\n";
  }

  private String getMethodDecl(){
    List<Type> hierarchy = getHierarchy();
    Type type = hierarchy.get(0);
    if(type instanceof RefType == false){
      throw new RuntimeException("please report bug in OpenCLPolymorphicMethod.getMethodDecl"); 
    }
    RefType ref_type = (RefType) type;
    SootClass soot_class = ref_type.getSootClass();
    OpenCLMethod ocl_method = new OpenCLMethod(m_sootMethod, soot_class);

    StringBuilder ret = new StringBuilder();
    ret.append(Tweaks.v().getDeviceFunctionQualifier()+" ");
    ret.append(ocl_method.getReturnString());
    ret.append(" invoke_"+ocl_method.getPolymorphicName());
    ret.append(ocl_method.getArgumentListStringPolymorphic());
    return ret.toString();
  }

  public String getMethodBody(){
    if(m_sootMethod.getName().equals("<init>"))
      return "";
    List<Type> hierarchy = getHierarchy();
    Type first_type = hierarchy.get(0);
    if(first_type instanceof RefType == false){
      throw new RuntimeException("please report bug in OpenCLPolymorphicMethod.getMethodBody"); 
    }
    RefType ref_type = (RefType) first_type;
    SootClass first_soot_class = ref_type.getSootClass();

    StringBuilder ret = new StringBuilder();
    String address_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    //write function signature
    ret.append(getMethodDecl());
    ret.append("{\n");

    if(m_sootMethod.isStatic()){
      if(m_sootMethod.getReturnType() instanceof VoidType == false){
        ret.append("return ");
      }
      String invoke_string = getStaticInvokeString(first_soot_class);
      ret.append(invoke_string+"\n");
    } else {
      ret.append("if(thisref == -1){\n");
      ret.append("  *exception = -2;\n");
      ret.append("return ");
      if(m_sootMethod.getReturnType() instanceof VoidType == false)
        ret.append("-1");
      ret.append(";\n");
      ret.append("}\n");
      ret.append(address_qual+" char * thisref_deref = edu_syr_pcpratts_gc_deref(gc_info, thisref);\n");
      if(sizeHierarchy(hierarchy) == 1){
        SootClass sclass = getSingleMethodInHierarchy(hierarchy);
        String invoke_string = getInvokeString(sclass);
        if(m_sootMethod.getReturnType() instanceof VoidType == false){
          ret.append("return ");
        }
        ret.append(invoke_string+"\n");
      } else {
        ret.append("GC_OBJ_TYPE_TYPE derived_type = edu_syr_pcpratts_gc_get_type(thisref_deref);\n");
        ret.append("if(0){}\n");
        int count = 0;
        for(Type type : hierarchy){
          if(type instanceof RefType == false){
            continue;
          }
          RefType curr_ref_type = (RefType) type;
          SootClass sclass = curr_ref_type.getSootClass();
          if(sootClassHasMethod(sclass) == false)
            continue;
          ret.append("else if(derived_type == "+RootbeerScene.v().getDfsInfo().getClassNumber(sclass)+"){\n");
          if(m_sootMethod.getReturnType() instanceof VoidType == false){
            ret.append("return ");
          }
          String invoke_string = getInvokeString(sclass);
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
  private String getInvokeString(SootClass soot_class){
    if(m_sootMethod.getName().equals("<init>"))
      return "";
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

  private List<Type> getHierarchy(){
    SootClass soot_class = m_sootMethod.getDeclaringClass();
    return RootbeerScene.v().getDfsInfo().getHierarchy(soot_class);
  }

  @Override
  public boolean equals(Object o){
    if(o instanceof OpenCLPolymorphicMethod == false)
      return false;
    OpenCLPolymorphicMethod other = (OpenCLPolymorphicMethod) o;
    if(m_sootMethod.getName().equals(other.m_sootMethod.getName()) == false)
      return false;    
    if(getHierarchy().equals(other.getHierarchy()))
      return true;
    return false;
  }

  @Override
  public int hashCode() {
    m_hierarchy = getHierarchy();
    int hash = 5;
    hash = 53 * hash + (this.m_sootMethod != null ? this.m_sootMethod.hashCode() : 0);
    hash = 53 * hash + (this.m_hierarchy != null ? this.m_hierarchy.hashCode() : 0);
    return hash;
  }

  private boolean sootClassHasMethod(SootClass sclass) {
    try {
      SootMethod soot_method = sclass.getMethod(m_sootMethod.getSubSignature());
      return true;
    } catch(Exception ex){
      return false;
    }
  }

  private int sizeHierarchy(List<Type> hierarchy) {
    int ret = 0;
    for(Type type : hierarchy){
      if(type instanceof RefType == false){
        continue;
      }
      RefType ref_type = (RefType) type;
      SootClass sclass = ref_type.getSootClass();
      if(sootClassHasMethod(sclass) == false)
        continue;
      ret++;
    }
    return ret;
  }

  private SootClass getSingleMethodInHierarchy(List<Type> hierarchy) {
    for(Type type : hierarchy){
      if(type instanceof RefType == false){
        continue;
      }
      RefType ref_type = (RefType) type;
      SootClass soot_class = ref_type.getSootClass();
      if(sootClassHasMethod(soot_class) == false)
        continue;
      return soot_class;
    }
    return null;
  }

}
