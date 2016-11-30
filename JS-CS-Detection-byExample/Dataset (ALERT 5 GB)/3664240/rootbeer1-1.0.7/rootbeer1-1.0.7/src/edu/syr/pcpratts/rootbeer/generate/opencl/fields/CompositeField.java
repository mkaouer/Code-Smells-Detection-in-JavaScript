/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl.fields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import soot.SootClass;

public class CompositeField {
  private List<OpenCLField> m_RefFields;
  private List<OpenCLField> m_NonRefFields;
  private Map<SootClass, List<OpenCLField>> m_RefFieldsByClass;
  private Map<SootClass, List<OpenCLField>> m_NonRefFieldsByClass;
  private List<SootClass> m_Classes;

  public CompositeField(){
    m_RefFields = new ArrayList<OpenCLField>();
    m_NonRefFields = new ArrayList<OpenCLField>();
    m_RefFieldsByClass = new HashMap<SootClass, List<OpenCLField>>();
    m_NonRefFieldsByClass = new HashMap<SootClass, List<OpenCLField>>();
    m_Classes = new ArrayList<SootClass>();
  }
  
  public void addRefField(OpenCLField field, SootClass soot_class){
    addField(m_RefFieldsByClass, soot_class, field);
    m_RefFields.add(field);
  }
  
  public void addNonRefField(OpenCLField field, SootClass soot_class) {
    addField(m_NonRefFieldsByClass, soot_class, field);
    m_NonRefFields.add(field);
  }
  
  public List<OpenCLField> getRefFields(){
    return m_RefFields;
  }
  
  public List<OpenCLField> getNonRefFields(){
    return m_NonRefFields;
  }
  
  public List<OpenCLField> getRefFieldsByClass(SootClass soot_class){
    List<OpenCLField> ret = m_RefFieldsByClass.get(soot_class);
    if(ret == null)
      ret = new ArrayList<OpenCLField>();
    return ret;
  }
  
  public List<OpenCLField> getNonRefFieldsByClass(SootClass soot_class){
    List<OpenCLField> ret = m_NonRefFieldsByClass.get(soot_class);
    if(ret == null)
      ret = new ArrayList<OpenCLField>();
    return ret;
  }
  
  public List<SootClass> getClasses(){
    return m_Classes;
  }

  private void addField(Map<SootClass, List<OpenCLField>> fields_by_class, SootClass soot_class, OpenCLField field) {
    if(fields_by_class.containsKey(soot_class)){
      List<OpenCLField> lst = fields_by_class.get(soot_class);
      lst.add(field);
    } else {
      List<OpenCLField> lst = new ArrayList<OpenCLField>();
      lst.add(field);
      fields_by_class.put(soot_class, lst);
    }
    if(m_Classes.contains(soot_class) == false)
      m_Classes.add(soot_class);
  }

}

