/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl.fields;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.trifort.rootbeer.configuration.Configuration;
import org.trifort.rootbeer.generate.bytecode.StaticOffsets;
import org.trifort.rootbeer.generate.opencl.OpenCLClass;
import org.trifort.rootbeer.generate.opencl.OpenCLScene;
import org.trifort.rootbeer.generate.opencl.OpenCLType;
import org.trifort.rootbeer.generate.opencl.fields.CompositeField;
import org.trifort.rootbeer.generate.opencl.fields.OffsetCalculator;
import org.trifort.rootbeer.generate.opencl.tweaks.Tweaks;

import soot.Local;
import soot.Modifier;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.Type;
import soot.Value;
import soot.jimple.toolkits.typing.fast.Integer127Type;
import soot.options.Options;
import soot.rbclassload.FieldSignatureUtil;
import soot.rbclassload.RootbeerClassLoader;

public class OpenCLField {
  private final SootField m_sootField;
  private final SootClass m_sootClass;
  private boolean m_cloned;
  private OpenCLField m_cloneSource;
  private Map<Integer, List<SootClass>> m_offsets;

  public OpenCLField(SootField soot_field, SootClass soot_class) {
    m_sootField = soot_field;
    m_sootClass = soot_class;
    m_cloned = false;    
  }
  
  public void setClone(OpenCLField source){
    m_cloned = true;
    m_cloneSource = source;
  }
  
  public boolean isCloned(){
    return m_cloned;
  }  
  
  public OpenCLField getCloneSource(){
    return m_cloneSource;
  }
  
  public String getName(){
    return m_sootField.getName();
  }

  public SootField getSootField(){
    return m_sootField;
  }

  private String getFullName(){
    FieldSignatureUtil util = new FieldSignatureUtil();
    util.parse(m_sootField.getSignature());
    SootField real_field = util.getSootField();
    OpenCLClass ocl_class = OpenCLScene.v().getOpenCLClass(real_field.getDeclaringClass());
    return ocl_class.getName()+"_"+getName();
  }
  
  public OpenCLType getClassType(){
    Type soot_type = m_sootClass.getType();
    return new OpenCLType(soot_type);
  }

  public OpenCLType getType(){
    Type soot_type = m_sootField.getType();
    return new OpenCLType(soot_type);
  }

  private List<String> getDecls(){
    List<String> ret = new ArrayList<String>();
    String type_string = getType().getCudaTypeString();
    String device_function_qual = Tweaks.v().getDeviceFunctionQualifier();
    String address_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    if(m_sootField.isStatic() == false){
      //instance getter
      ret.add(device_function_qual+" "+type_string+" instance_getter_"+getFullName()+"("+address_qual+" char * gc_info, int thisref, int * exception)");
      //instance setter
      ret.add(device_function_qual+" void instance_setter_"+getFullName()+"("+address_qual+" char * gc_info, int thisref, "+type_string+" parameter0, int * exception)");
    } else {
      //static getter
      ret.add(device_function_qual+" "+type_string+" static_getter_"+getFullName()+"("+address_qual+" char * gc_info, int * exception)");
      //static setter
      ret.add(device_function_qual+" void static_setter_"+getFullName()+"("+address_qual+" char * gc_info, "+type_string+" parameter0, int * expcetion)");
    }
    return ret;
  }

  public String getGetterSetterPrototypes(){
    StringBuilder ret = new StringBuilder();
    List<String> decls = getDecls();
    for(String decl : decls){
      ret.append(decl+";\n");
    }
    return ret.toString();
  }
  
  public int getSize(){
    return getType().getSize();
  }

  private void calculateOffsets(CompositeField composite){    
    OffsetCalculator calc = new OffsetCalculator(composite);
    m_offsets = new TreeMap<Integer, List<SootClass>>();
    for(SootClass sclass : composite.getClasses()){
      int field_offset = calc.getOffset(this, sclass);
      List<SootClass> classes;
      if(m_offsets.containsKey(field_offset)){
        classes = m_offsets.get(field_offset);
      } else {
        classes = new ArrayList<SootClass>();
        m_offsets.put(field_offset, classes);
      }
      classes.add(sclass);
    }
  }
  
  private int getOnlyOffset(){
    Iterator<Integer> iter = m_offsets.keySet().iterator();
    while(iter.hasNext()){
      int ret = iter.next();
      return ret;
    }
    return -1;
  }
  
  private String getGetterSetterBodiesInstance(CompositeField composite, boolean writable,
    FieldTypeSwitch type_switch){
    
    StringBuilder ret = new StringBuilder();
    List<String> decls = getDecls();
    String address_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    
    String cast_string = getCastString();
    
    calculateOffsets(composite);
    
    String prefix = Options.v().rbcl_remap_prefix();    
    if(Options.v().rbcl_remap_all() == false){
      prefix = "";
    }
    SootClass null_cls = Scene.v().getSootClass(prefix+"java.lang.NullPointerException");
    int null_num = RootbeerClassLoader.v().getClassNumber(null_cls);
    
    //instance getter
    ret.append(decls.get(0)+"{\n");
    int field_offset = getOnlyOffset();
    //ret.append("if("+thisref+" & 0x1000000000000000L){\n");
    //ret.append("  thisref &= 0x0fffffffffffffffL;\n");
    //ret.append("  thisref += "+field_offset+";\n");
    //ret.append("  return org_trifort_cache_get_"+type+"(thisref);\n");
    //ret.append("} else {\n");   
    if(composite.getClasses().size() != 1){
      ret.append("GC_OBJ_TYPE_TYPE derived_type;\n");
      ret.append("int offset;\n");
    }
    ret.append(address_qual+" char * thisref_deref;\n");
    if(Configuration.compilerInstance().getExceptions()){
      ret.append("if(thisref == -1){\n");
      ret.append("  *exception = "+null_num+";\n");
      ret.append("  return 0;\n");
      ret.append("}\n");
    }
    ret.append("thisref_deref = org_trifort_gc_deref(gc_info, thisref);\n");
    if(composite.getClasses().size() == 1){
      SootClass sclass = composite.getClasses().get(0);
      ret.append("return *(("+address_qual+" "+cast_string+" *) &thisref_deref["+Integer.toString(field_offset)+"]);\n");
    } else {
      ret.append("derived_type = org_trifort_gc_get_type(thisref_deref);\n");
      ret.append("offset = "+type_switch.typeSwitchName(m_offsets)+"(derived_type);\n");
      ret.append("return *(("+address_qual+" "+cast_string+" *) &thisref_deref[offset]);\n");
    }
    //ret.append("}\n");
    ret.append("}\n");
    //instance setter
    ret.append(decls.get(1)+"{\n");
    if(composite.getClasses().size() != 1){
      ret.append("GC_OBJ_TYPE_TYPE derived_type;\n");
      ret.append("int offset;\n");
    }
    ret.append(address_qual+" char * thisref_deref;\n");
    if(Configuration.compilerInstance().getExceptions()){
      ret.append("if(thisref == -1){\n");
      ret.append("  *exception = "+null_num+";\n");
      ret.append("  return;\n");
      ret.append("}\n");
    }
    ret.append("thisref_deref = org_trifort_gc_deref(gc_info, thisref);\n");    
    if(composite.getClasses().size() == 1){
      ret.append("*(("+address_qual+" "+cast_string+" *) &thisref_deref["+Integer.toString(field_offset)+"]) = parameter0;\n");
    } else {
      ret.append("derived_type = org_trifort_gc_get_type(thisref_deref);\n");
      ret.append("offset = "+type_switch.typeSwitchName(m_offsets)+"(derived_type);\n");     
      ret.append("*(("+address_qual+" "+cast_string+" *) &thisref_deref[offset]) = parameter0;\n");
    }
    ret.append("}\n");
    
    return ret.toString();
  }
  
  private String getGetterSetterBodiesStatic() {
    StringBuilder ret = new StringBuilder();
    List<String> decls = getDecls();
    String address_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    StaticOffsets static_offsets = new StaticOffsets();
    int offset = static_offsets.getIndex(this);
    
    String cast_string = getCastString();
    
    ret.append(decls.get(0)+"{\n");
    ret.append(address_qual+" char * thisref_deref = org_trifort_gc_deref(gc_info, 0);\n");
    ret.append("return *(("+address_qual+" "+cast_string+" *) &thisref_deref["+offset+"]);\n");
    ret.append("}\n");
    
    ret.append(decls.get(1)+"{\n");    
    ret.append(address_qual+" char * thisref_deref = org_trifort_gc_deref(gc_info, 0);\n");
    ret.append("*(("+address_qual+" "+cast_string+" *) &thisref_deref["+offset+"]) = parameter0;\n");
    ret.append("}\n");
    return ret.toString();
  }
  
  public String getGetterSetterBodies(CompositeField composite, boolean writable,
    FieldTypeSwitch type_switch){
    
    if(m_sootField.isStatic()){
      return getGetterSetterBodiesStatic();
    } else {
      return getGetterSetterBodiesInstance(composite, writable, type_switch);
    }    
  }
  
  private String checkAlignmentString(int field_offset){
    String ret = "";
    ret += "int addr = thisref_deref + "+field_offset+";\n";
    ret += "if(addr % "+getSize()+" != 0){\n";
    ret += "  printf(\"misaligned field: "+m_sootField.toString()+"\\n\");\n";
    ret += "}\n";
    return ret;
  }
  
  public String getStaticGetterInvoke(){
    return "static_getter_"+getFullName()+"(gc_info, exception)";
  }

  public String getStaticSetterInvoke(){
    return "static_setter_"+getFullName()+"(gc_info";
  }

  public String getInstanceGetterInvoke(Value base){
    if(base instanceof Local == false)
      throw new UnsupportedOperationException("how do we handle when a base is not a loca?");
    Local local = (Local) base;
    return "instance_getter_"+getFullName()+"(gc_info, "+local.getName()+", exception)";
  }

  public String getInstanceSetterInvoke(Value base){
    if(base instanceof Local == false)
      throw new UnsupportedOperationException("how do we handle when a base is not a loca?");
    Local local = (Local) base;
    return "instance_setter_"+getFullName()+"(gc_info, "+local.getName();
  }
  
  public String getInstanceSetterInvokeWithoutThisref(){
    return "instance_setter_"+getFullName()+"(gc_info, ";
  }

  @Override
  public String toString(){
    return getType().getDerefString()+" "+getName();
  }

  @Override
  public boolean equals(Object o){
    if(o instanceof OpenCLField == false)
      return false;
    OpenCLField other = (OpenCLField) o;
    if(m_sootField.equals(other.m_sootField) && m_sootClass.equals(other.m_sootClass))
      return true;
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 37 * hash + (this.m_sootField != null ? this.m_sootField.hashCode() : 0);
    hash = 37 * hash + (this.m_sootClass != null ? this.m_sootClass.hashCode() : 0);
    return hash;
  }

  public boolean isInstance() {
    if(m_sootField.isStatic())
      return false;
    return true;
  }

  private String getCastString() {
    String ret = getType().getCudaTypeString();
    return ret;
  }

  public boolean isFinal() {
    return m_sootField.isFinal();
  }
}
