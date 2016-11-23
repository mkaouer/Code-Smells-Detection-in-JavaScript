/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.configuration.Configuration;
import org.trifort.rootbeer.generate.bytecode.Constants;
import org.trifort.rootbeer.generate.opencl.body.MethodJimpleValueSwitch;
import org.trifort.rootbeer.generate.opencl.tweaks.Tweaks;

import soot.*;
import soot.jimple.ArrayRef;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.rbclassload.RootbeerClassLoader;

public class OpenCLArrayType {

  private ArrayType m_arrayType;
  
  public OpenCLArrayType(ArrayType type){
    m_arrayType = (ArrayType) type;
  }
  
  public ArrayType getArrayType(){
    return m_arrayType;  
  }
  
  public String getArrayRefGetter(ArrayRef arg0){
    Value index = arg0.getIndex();
    Value base = arg0.getBase();
    if(base instanceof Local == false)
      throw new UnsupportedOperationException("what do I do if base is not a local?");
    Local local = (Local) base;
    return getDerefTypeString()+"_get(gc_info, "+local.getName()+", "+index.toString()+", exception)";
  }
  
  
  public String getArrayRefSetter(ArrayRef arg0){
    Value index = arg0.getIndex();
    Value base = arg0.getBase();
    if(base instanceof Local == false)
      throw new UnsupportedOperationException("what do I do if base is not a local?");
    Local local = (Local) base;

    return getDerefTypeString()+"_set(gc_info, "+local.getName()+", "+index.toString();
  }
  
  public String getDerefTypeString(){
    OpenCLType type = new OpenCLType(m_arrayType);
    String ret = type.getDerefString();
    return ret;
  }
  
  private String getMultiDeref(int dimensions){
    Type base_type = m_arrayType.baseType;  
    ArrayType array_type = ArrayType.v(base_type,  dimensions);
    OpenCLType type = new OpenCLType(array_type);
    String ret = type.getDerefString();
    return ret;    
  }

  private List<String> getDecls(){
    List<String> ret = new ArrayList<String>();
    String address_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    String function_qual = Tweaks.v().getDeviceFunctionQualifier();
    ret.add(function_qual+" "+getAssignType()+" "+getDerefTypeString()+"_get("+address_qual+" char * gc_info, int thisref, int parameter0, int * exception)");
    ret.add(function_qual+" void "+getDerefTypeString()+"_set("+address_qual+" char * gc_info, int thisref, int parameter0, "+getAssignType()+" parameter1, int * exception)");
    ret.add(function_qual+" int "+getDerefTypeString()+"_new("+address_qual+" char * gc_info, int size, int * exception)");
    
    String multi_dim_decl = "";
    multi_dim_decl += function_qual+" int "+getDerefTypeString()+"_new_multi_array";
    multi_dim_decl += "("+address_qual+" char * gc_info, ";
    for(int i = 0; i < m_arrayType.numDimensions; ++i){
      multi_dim_decl += "int dim"+i+", ";
    }
    multi_dim_decl += "int * exception)";
    ret.add(multi_dim_decl);
    
    return ret;
  }
  
  public String getPrototypes(){
    StringBuilder ret = new StringBuilder();
    List<String> decls = getDecls();
    for(String decl : decls)
      ret.append(decl+";\n");
    return ret.toString();
  }

  public String invokeNewArrayExpr(NewArrayExpr arg0){
    StringBuilder ret = new StringBuilder();
    ret.append(getDerefTypeString()+"_new(gc_info, ");
    MethodJimpleValueSwitch quick_value_switch = new MethodJimpleValueSwitch(ret);
    arg0.getSize().apply(quick_value_switch);
    ret.append(", exception)");
    return ret.toString();
  }
  
  public String invokeNewMultiArrayExpr(NewMultiArrayExpr arg0){
    StringBuilder ret = new StringBuilder();
    ret.append(getDerefTypeString()+"_new_multi_array(gc_info, ");
    MethodJimpleValueSwitch quick_value_switch = new MethodJimpleValueSwitch(ret);
    for(int i = 0; i < arg0.getSizeCount(); ++i){
      arg0.getSize(i).apply(quick_value_switch);
      ret.append(", ");
    }
    ret.append("exception)");
    return ret.toString();
  }

  private String getAssignType(){
    ArrayType array_type = m_arrayType;
    if(array_type.numDimensions != 1)
      return "int";
    OpenCLType type = new OpenCLType(array_type.baseType);
    return type.getCudaTypeString();
  }
  
  private String initValue(){     
    String ret = "";
    if(isBaseRefType())
      ret = "-1";
    else
      ret = "0";
    return ret;
  }
  
  public String getBodies(){
    StringBuilder ret = new StringBuilder();
    List<String> decls = getDecls();
    int element_size = getElementSize();
    int offset_size = Constants.ArrayOffsetSize;
    
    String address_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    SootClass null_ptr = Scene.v().getSootClass("java.lang.NullPointerException");
    //get
    ret.append(decls.get(0)+"{\n");
    ret.append("int offset;\n");
    ret.append("int length;\n");
    ret.append(address_qual+" char * thisref_deref;\n");
    ret.append("offset = "+offset_size+"+(parameter0*"+element_size+");\n");
    
    if(Configuration.compilerInstance().getExceptions()){
      ret.append("if(thisref == -1){\n");
      ret.append("  *exception = "+RootbeerClassLoader.v().getClassNumber(null_ptr) +";\n");
      ret.append("  return 0;\n");
      ret.append("}\n");
    }
    ret.append("thisref_deref = org_trifort_gc_deref(gc_info, thisref);\n");
    if(Configuration.compilerInstance().getArrayChecks() && 
       Configuration.compilerInstance().getExceptions()){
      ret.append("length = org_trifort_getint(thisref_deref, 12);\n");
      ret.append("if(parameter0 < 0 || parameter0 >= length){\n");
      ret.append("  *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(gc_info, parameter0, thisref, length, exception);");
      ret.append("  return 0;\n");
      ret.append("}\n");
    }
    ret.append("return *(("+address_qual+" "+getAssignType()+" *) &thisref_deref[offset]);\n");
    ret.append("}\n");
    
    //set
    ret.append(decls.get(1)+"{\n");
    ret.append("int length;\n");
    ret.append(address_qual+" char * thisref_deref;\n");
    if(Configuration.compilerInstance().getExceptions()){
      ret.append("  if(thisref == -1){\n");
      ret.append("    *exception = "+RootbeerClassLoader.v().getClassNumber(null_ptr) +";\n");
      ret.append("    return;\n");
      ret.append("  }\n");
    }
    ret.append("thisref_deref = org_trifort_gc_deref(gc_info, thisref);\n");
     if(Configuration.compilerInstance().getArrayChecks() && 
       Configuration.compilerInstance().getExceptions()){
      ret.append("length = org_trifort_getint(thisref_deref, 12);\n");
      ret.append("if(parameter0 < 0 || parameter0 >= length){\n");
      ret.append("  *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(gc_info, parameter0, thisref, length, exception);");
      ret.append("  return;\n");
      ret.append("}\n");
    }
    if(isCharArray()){
      ret.append("*(("+address_qual+" int *) &thisref_deref["+offset_size+"+(parameter0*"+element_size+")]) = 0;\n");
    }
    ret.append("*(("+address_qual+" "+getAssignType()+" *) &thisref_deref["+offset_size+"+(parameter0*"+element_size+")]) = parameter1;\n");
    ret.append("}\n");
    
    //new
    int derived_type = RootbeerClassLoader.v().getClassNumber(m_arrayType.toString());
    ret.append(decls.get(2)+"{\n");
    ret.append("int i;\n");
    ret.append("int total_size;\n");
    ret.append("int mod;\n");
    ret.append("int thisref;\n");
    ret.append(address_qual+" char * thisref_deref;\n");
    ret.append("total_size = (size * "+element_size+")+ "+offset_size+";\n");
    ret.append("mod = total_size % 8;\n");
    ret.append("if(mod != 0)\n");
    ret.append("  total_size += (8 - mod);\n");
    ret.append("thisref = org_trifort_gc_malloc(gc_info, total_size);\n");
    if(Configuration.compilerInstance().getExceptions()){
      ret.append("if(thisref == -1){\n");
      ret.append("  *exception = "+RootbeerClassLoader.v().getClassNumber(null_ptr) +";\n");
      ret.append("  return -1;\n");
      ret.append("}\n");
    }
    ret.append("thisref_deref = org_trifort_gc_deref(gc_info, thisref);\n");
    ret.append("\n//class info\n");
    ret.append("org_trifort_gc_set_count(thisref_deref, 0);\n");
    ret.append("org_trifort_gc_set_color(thisref_deref, COLOR_GREY);\n");
    ret.append("org_trifort_gc_set_type(thisref_deref, "+Integer.toString(derived_type)+");\n");
    ret.append("org_trifort_gc_set_ctor_used(thisref_deref, 1);\n");
    ret.append("org_trifort_gc_set_size(thisref_deref, total_size);\n");
    ret.append("org_trifort_setint(thisref_deref, 12, size);\n");
    ret.append("for(i = 0; i < size; ++i){\n");
    ret.append("  "+getDerefTypeString()+"_set(gc_info, thisref, i, "+initValue()+", exception);\n");
    ret.append("}\n");
    ret.append("return thisref;\n");
    ret.append("}\n");   
    
    String multi_decl = decls.get(3);
    int dim = m_arrayType.numDimensions; 
    //new multi-dimensional
    ret.append(multi_decl+"{\n");
    ret.append("int total_size = (dim0 * 8) + "+offset_size+";\n"); 
    for(int i = 0; i < dim; ++i){
      ret.append("int index"+i+";\n");
      ret.append("int aref"+i+";\n");
    }
    ret.append("int mod;\n");
    ret.append("int thisref;\n");
    ret.append(address_qual+" char * thisref_deref;\n");
    ret.append("mod = total_size % 8;\n");
    ret.append("if(mod != 0)\n");
    ret.append("  total_size += (8 - mod);\n");
    ret.append("thisref = org_trifort_gc_malloc(gc_info, total_size);\n");
    if(Configuration.compilerInstance().getExceptions()){
      ret.append("if(thisref == -1){\n");
      ret.append("  *exception = "+RootbeerClassLoader.v().getClassNumber(null_ptr) +";\n");
      ret.append("  return -1;\n");
      ret.append("}\n");
    }
    ret.append("thisref_deref = org_trifort_gc_deref(gc_info, thisref);\n");
    ret.append("\n//class info\n");
    ret.append("org_trifort_gc_set_count(thisref_deref, 0);\n");
    ret.append("org_trifort_gc_set_color(thisref_deref, COLOR_GREY);\n");
    ret.append("org_trifort_gc_set_type(thisref_deref, "+Integer.toString(derived_type)+");\n");
    ret.append("org_trifort_gc_set_ctor_used(thisref_deref, 1);\n");
    ret.append("org_trifort_gc_set_size(thisref_deref, total_size);\n");
    ret.append("org_trifort_setint(thisref_deref, 12, dim0);\n");
    ret.append(multiInitString(dim));
    ret.append("return thisref;\n");
    ret.append("}\n");
    
    return ret.toString();
  }

  private String multiInitString(int dim){
    String ret = "";
    String address_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    
    for(int i = 0; i < dim; ++i){      
      ret += "for(index"+i+" = 0; index"+i+" < dim"+i+"; ++index"+i+"){\n";
      String thisref = "thisref";
      if(i > 0){
        thisref = "aref"+(i-1);
      }
      String set_str = getMultiDeref(dim-i)+"_set("+address_qual+"gc_info, "+thisref;
      if(i < dim - 1){
        String new_str = getMultiDeref(dim-1-i)+"_new("+address_qual+"gc_info, dim"+(i+1)+", exception)";
        ret += "  aref"+i+" = "+new_str+";\n";
        ret += "  "+set_str+", index"+i+", aref"+i+", exception);\n";
      } else {
        ret += "  "+set_str+", index"+i+", "+initValue()+", exception);\n";
      }
    }
    for(int i = 0; i < dim; ++i){
      ret += "}\n"; 
    }
    return ret;
  }
  
  
  private boolean isBaseRefType(){
    Type base_type = getBaseType();

    if(base_type instanceof RefType)
      return true;
    else
      return false;
  }
  
  private Type getBaseType(){
    Type base_type = null;
    ArrayType array_type = (ArrayType) m_arrayType;
    base_type = array_type.baseType;
    return base_type;
  }

  @Override
  public boolean equals(Object o){
    if(o instanceof OpenCLArrayType == false)
      return false;
    OpenCLArrayType other = (OpenCLArrayType) o;
    if(this.m_arrayType.equals(other.m_arrayType))
      return true;
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 79 * hash + (this.m_arrayType != null ? this.m_arrayType.hashCode() : 0);
    return hash;
  }

  public int getElementSize() {
    if(m_arrayType.numDimensions != 1)
      return 4;
    Type base_type = m_arrayType.baseType;
    OpenCLType ocl_type = new OpenCLType(base_type);
    return ocl_type.getSize();
  }

  int getTypeInteger() {
    return RootbeerClassLoader.v().getClassNumber(m_arrayType.toString());
  }

  private boolean isCharArray() {
    Type base_type = getBaseType();
    String str = base_type.toString();
    if(str.equals("char"))
      return true;
    return false;
  }

  private String getCacheName() {
    String ret = getAssignType();
    if(ret.equals("long long")){
      return "long";
    } 
    return ret;
  }
}
