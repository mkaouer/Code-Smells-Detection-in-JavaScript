/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl.fields;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.trifort.rootbeer.generate.opencl.OpenCLClass;

import soot.SootClass;
import soot.SootField;

public class CompositeFieldFactory {

  private List<TreeNode> m_hierarchy;
  private List<CompositeField> m_fields;
  private Set<String> m_processNodeVisited;
  
  public void setup(Map<String, OpenCLClass> classes) {
    m_processNodeVisited = new HashSet<String>();
    createHierarchy(classes);
    m_fields = new ArrayList<CompositeField>();
    for(TreeNode node : m_hierarchy){
      CompositeField composite = new CompositeField();
      processNode(node, composite);
      if(composite.getClasses().isEmpty()){
        composite.getClasses().add(node.getSootClass());          
      }
      composite.sort();
      m_fields.add(composite);
    }
  }
  
  public List<CompositeField> getCompositeFields(){
    return m_fields; 
  }
        
  private void createHierarchy(Map<String, OpenCLClass> classes){
    ReverseClassHierarchy creator = new ReverseClassHierarchy(classes);
    m_hierarchy = creator.get();
  }
  
  private void processNode(TreeNode node, CompositeField composite) {
    OpenCLClass ocl_class = node.getOpenCLClass();
    List<OpenCLField> ref_fields = ocl_class.getInstanceRefFields();
    for(OpenCLField field : ref_fields){ 
      processNodeField(node, field, true, composite);
    }    
    List<OpenCLField> static_ref_fields = ocl_class.getStaticRefFields();
    for(OpenCLField field : static_ref_fields){ 
      processNodeField(node, field, true, composite);
    }
    List<OpenCLField> non_ref_fields = ocl_class.getInstanceNonRefFields();
    for(OpenCLField field : non_ref_fields){ 
      processNodeField(node, field, false, composite);
    }
    List<OpenCLField> static_non_ref_fields = ocl_class.getStaticNonRefFields();
    for(OpenCLField field : static_non_ref_fields){ 
      processNodeField(node, field, false, composite);
    }
    for(TreeNode child : node.getChildren()){
      processNode(child, composite);
    }
  }
  
  private void processNodeField(TreeNode node, OpenCLField field, boolean ref_field, CompositeField composite){
    
    SootClass soot_class = node.getSootClass();    
    SootField soot_field = field.getSootField();
    
    OpenCLField new_field = new OpenCLField(soot_field, soot_class);
    
    if(isCloned(soot_class, soot_field)){
      new_field.setClone(field);
    } else {
      soot_field = soot_class.getFieldByName(soot_field.getName());
      new_field = new OpenCLField(soot_field, soot_class);
      field = new_field;
    }
    
    String hash = soot_field.toString();
    if(m_processNodeVisited.contains(hash) == false){
      m_processNodeVisited.add(hash);
      if(ref_field){
        composite.addRefField(new_field, soot_class);
      } else {
        composite.addNonRefField(new_field, soot_class);
      }
    }
    
    if(soot_field.isPrivate() == false){
      for(TreeNode child : node.getChildren()){
        processNodeField(child, field, ref_field, composite);
      }
    }
  }

  /**
   * if the class has a field by the name, it is not cloned
   * @param soot_class
   * @param soot_field
   * @return 
   */
  private boolean isCloned(SootClass soot_class, SootField soot_field) {
    try {
      soot_class.getFieldByName(soot_field.getName());
      return false;
    } catch(Exception ex){
      return true;
    }
  }
  
}