/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.trifort.rootbeer.generate.opencl.fields.OffsetCalculator;
import org.trifort.rootbeer.generate.opencl.fields.OpenCLField;

import soot.SootClass;
import soot.SootField;

public class OpenCLClass {
  private final Set<OpenCLMethod> m_methods;
  private final Set<OpenCLField> m_fields;
  private final SootClass m_sootClass;
  private List<OpenCLField> m_instanceRefFields;
  private List<OpenCLField> m_instanceNonRefFields;
  private List<OpenCLField> m_allUsedInstanceFields;
  
  private List<OpenCLField> m_staticRefFields;
  private List<OpenCLField> m_staticNonRefFields;
  private List<OpenCLField> m_allUsedStaticFields;
  
  private int m_staticFieldSize;

  public OpenCLClass(SootClass soot_class){
    m_methods = new LinkedHashSet<OpenCLMethod>();
    m_fields = new LinkedHashSet<OpenCLField>();
    m_sootClass = soot_class;
    m_instanceRefFields = null;
    m_instanceNonRefFields = null;
    m_allUsedInstanceFields = null;
    m_staticRefFields = null;
    m_staticNonRefFields = null;
    m_allUsedStaticFields = null;
  }
  
  public void addField(SootField soot_field){
    
  }
  
  public SootClass getSootClass(){
    return m_sootClass;
  }

  public void calculateStaticFieldSize(){
    m_staticFieldSize = 0;
    determineFieldTypes();
    for(OpenCLField field : m_allUsedInstanceFields){
      SootField soot_field = field.getSootField();
      if(soot_field.isStatic() == false)
        continue;
      OpenCLType type = new OpenCLType(soot_field.getType());
      m_staticFieldSize += type.getSize();
    }
  }

  public int getStaticFieldSize(){
    return m_staticFieldSize;
  }

  public int getSize(){
    int max = org.trifort.rootbeer.generate.bytecode.Constants.SizeGcInfo;
    try {
      SootClass soot_class = m_sootClass;
      
      //find the largest size from all super classes
      while(true){
        OffsetCalculator calc = OpenCLScene.v().getOffsetCalculator(soot_class);
        int size = calc.getSize(soot_class);
        if(size > max){
          max = size;
        }
        if(soot_class.hasSuperclass()){
          soot_class = soot_class.getSuperclass();
        } else {
          return max;
        }
      }
    } catch(RuntimeException ex){
      return max;
    }
  }
  
  public String getName(){
    OpenCLType ocl_type = new OpenCLType(m_sootClass.getType());
    return ocl_type.getDerefString();
  }
  
  public String getJavaName(){
    return m_sootClass.getName();
  }
  
  public void addMethod(OpenCLMethod method){
    m_methods.add(method);
  }

  void addField(OpenCLField ocl_field) {
    m_fields.add(ocl_field);
  }

  @Override
  public boolean equals(Object o){
    if(o instanceof OpenCLClass == false)
      return false;
    OpenCLClass other = (OpenCLClass) o;
    if(m_sootClass.equals(other.m_sootClass))
      return true;
    return false;
  }

  @Override
  public String toString(){
    return getName();
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 83 * hash + (this.m_sootClass != null ? this.m_sootClass.hashCode() : 0);
    return hash;
  }

  private void determineFieldTypes() {
    //caching
    if(m_instanceRefFields != null)
      return;

    m_instanceRefFields = new ArrayList<OpenCLField>();
    m_instanceNonRefFields = new ArrayList<OpenCLField>();
    m_allUsedInstanceFields = new ArrayList<OpenCLField>();

    m_staticRefFields = new ArrayList<OpenCLField>();
    m_staticNonRefFields = new ArrayList<OpenCLField>();
    m_allUsedStaticFields = new ArrayList<OpenCLField>();
    
    for(OpenCLField field : m_fields){
      OpenCLType type = field.getType();
      if(type.isRefType()){
        if(field.isInstance()){
          m_instanceRefFields.add(field);
        } else {
          m_staticRefFields.add(field);
        }
      } else {
        if(field.isInstance()){
          m_instanceNonRefFields.add(field);
        } else {
          m_staticNonRefFields.add(field);
        }
      }
    }

    m_allUsedInstanceFields.addAll(m_instanceRefFields);
    m_allUsedInstanceFields.addAll(m_instanceNonRefFields);
    
    m_allUsedStaticFields.addAll(m_staticRefFields);
    m_allUsedStaticFields.addAll(m_staticNonRefFields);
  }
  
  public int getRefFieldsSize() {
    determineFieldTypes();
    return m_instanceRefFields.size();
  }

  public List<OpenCLField> getAllUsedInstanceFields(){
    return m_allUsedInstanceFields;
  }

  boolean isLibraryClass() {
    return false;
  }

  public List<OpenCLField> getInstanceRefFields() {
    determineFieldTypes();
    FieldPackingSorter sorter = new FieldPackingSorter();
    return sorter.sort(m_instanceRefFields);
  }

  public List<OpenCLField> getInstanceNonRefFields() {
    determineFieldTypes();
    FieldPackingSorter sorter = new FieldPackingSorter();
    return sorter.sort(m_instanceNonRefFields);
  }
  
  public List<OpenCLField> getStaticRefFields() {
    determineFieldTypes();
    FieldPackingSorter sorter = new FieldPackingSorter();
    return sorter.sort(m_staticRefFields);
  }
  
  public List<OpenCLField> getStaticNonRefFields() {
    determineFieldTypes();
    FieldPackingSorter sorter = new FieldPackingSorter();
    return sorter.sort(m_staticNonRefFields);
  }
  
  public List<OpenCLField> getAllUsedStaticFields(){
    return m_allUsedStaticFields;
  }

  public Set<OpenCLMethod> getMethods() {
    return m_methods;
  }

  public OpenCLField getField(String name) {
    determineFieldTypes();
    for(OpenCLField field : m_allUsedInstanceFields){
      if(field.getName().equals(name))
        return field;
    }
    for(OpenCLField field : m_allUsedStaticFields){
      if(field.getName().equals(name))
        return field;
    }
    return null;
  }

  public OpenCLMethod getMethod(String signature) {
    for(OpenCLMethod ocl_method : m_methods){
      if(ocl_method.getSignature().equals(signature)){
        return ocl_method;
      }
    }
    return null;
  }
}
