/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl.fields;

import edu.syr.pcpratts.rootbeer.generate.bytecode.FieldReadWriteInspector;
import edu.syr.pcpratts.rootbeer.generate.opencl.FieldPackingSorter;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLClass;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FieldCodeGeneration {
  
  private FieldCloner m_FieldCloner;
  private FieldReadWriteInspector m_Inspector;
  private FieldTypeSwitch m_TypeSwitch;
 
  public String prototypes(Map<String, OpenCLClass> classes, FieldReadWriteInspector inspector) {
    setup(classes, inspector);    
    Set<String> set = new HashSet<String>();
    for(CompositeField composite : m_FieldCloner.getCompositeFields()){
      set.addAll(getFieldPrototypes(composite));
    }
    return setToString(set);
  }
  
  public String bodies(Map<String, OpenCLClass> classes, FieldReadWriteInspector inspector, FieldTypeSwitch type_switch) {
    m_TypeSwitch = type_switch;
    setup(classes, inspector);   
    Set<String> set = new HashSet<String>();
    for(CompositeField composite : m_FieldCloner.getCompositeFields()){
      set.addAll(getFieldBodies(composite));
    }
    return setToString(set);
  }
  
  private Set<String> getFieldBodies(CompositeField composite){
    Set<String> ret = new HashSet<String>();
    FieldPackingSorter sorter = new FieldPackingSorter();
    List<OpenCLField> ref_sorted = sorter.sort(composite.getRefFields());
    List<OpenCLField> nonref_sorted = sorter.sort(composite.getNonRefFields());
    for(OpenCLField field : ref_sorted){
      boolean writable = m_Inspector.fieldIsWrittenOnGpu(field);
      ret.add(field.getGetterSetterBodies(composite, writable, m_TypeSwitch));
    }
    for(OpenCLField field : nonref_sorted){
      boolean writable = m_Inspector.fieldIsWrittenOnGpu(field);
      ret.add(field.getGetterSetterBodies(composite, writable, m_TypeSwitch));
    }
    return ret;
  }

  private Set<String> getFieldPrototypes(CompositeField composite){
    Set<String> ret = new HashSet<String>();
    for(OpenCLField field : composite.getRefFields()){
      ret.add(field.getGetterSetterPrototypes());
    }
    for(OpenCLField field : composite.getNonRefFields()){
      ret.add(field.getGetterSetterPrototypes());
    }
    return ret;
  }
  
  private String setToString(Set<String> set){
    String ret = "";
    Iterator<String> iter = set.iterator();
    while(iter.hasNext()){
      ret += iter.next()+"\n";
    }
    return ret;
  }

  private void setup(Map<String, OpenCLClass> classes, FieldReadWriteInspector inspector) {
    m_FieldCloner = new FieldCloner();
    m_FieldCloner.setup(classes);
    m_Inspector = inspector;
  }
    
}
