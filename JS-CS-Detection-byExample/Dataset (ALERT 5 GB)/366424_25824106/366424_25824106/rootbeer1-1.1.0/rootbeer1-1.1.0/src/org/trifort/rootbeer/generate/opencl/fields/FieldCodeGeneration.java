/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl.fields;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.trifort.rootbeer.generate.opencl.FieldPackingSorter;
import org.trifort.rootbeer.generate.opencl.OpenCLClass;
import org.trifort.rootbeer.generate.opencl.OpenCLScene;

import soot.SootClass;

public class FieldCodeGeneration {
  
  private FieldTypeSwitch m_TypeSwitch;
 
  public String prototypes(Map<String, OpenCLClass> classes) {
    Set<String> set = new HashSet<String>();
    List<CompositeField> fields = OpenCLScene.v().getCompositeFields();
    for(CompositeField field : fields){
      set.addAll(getFieldPrototypes(field));
    }
    return setToString(set);
  }
  
  public String bodies(Map<String, OpenCLClass> classes, FieldTypeSwitch type_switch) {
    m_TypeSwitch = type_switch;
    Set<String> set = new HashSet<String>();
    List<CompositeField> fields = OpenCLScene.v().getCompositeFields();
    for(CompositeField field : fields){
      set.addAll(getFieldBodies(field));
    }
    return setToString(set);
  }
  
  private Set<String> getFieldBodies(CompositeField composite){
    Set<String> ret = new HashSet<String>();
    FieldPackingSorter sorter = new FieldPackingSorter();
    List<OpenCLField> ref_sorted = sorter.sort(composite.getRefFields());
    List<OpenCLField> nonref_sorted = sorter.sort(composite.getNonRefFields());
    for(OpenCLField field : ref_sorted){
      boolean writable = true;
      ret.add(field.getGetterSetterBodies(composite, writable, m_TypeSwitch));
    }
    for(OpenCLField field : nonref_sorted){
      boolean writable = true;
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
}
