/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import soot.SootClass;
import soot.SootMethod;

/**
 * Represents all the versions of methods in a class Hierarchy
 * @author pcpratts
 */
public class MethodHierarchies {

  private Set<MethodHierarchy> m_hierarchies;
  
  public MethodHierarchies(){
    m_hierarchies = new LinkedHashSet<MethodHierarchy>();
  }
  
  public void addMethod(SootMethod method){
    MethodHierarchy new_hierarchy = new MethodHierarchy(method);
    if(m_hierarchies.contains(new_hierarchy) == false)
      m_hierarchies.add(new_hierarchy);
  }
  
  public List<OpenCLMethod> getMethods(){
    List<OpenCLMethod> ret = new ArrayList<OpenCLMethod>();
    //for each method    
    for(MethodHierarchy method_hierarchy : m_hierarchies){
      //get the list of classes in the hierarchy
      List<OpenCLMethod> methods = method_hierarchy.getMethods();
      for(OpenCLMethod method : methods){ 
        ret.add(method);
      }
    }   
    return ret;
  }
  
  public List<OpenCLPolymorphicMethod> getPolyMorphicMethods(){
    List<OpenCLPolymorphicMethod> ret = new ArrayList<OpenCLPolymorphicMethod>();
    //for each method    
    for(MethodHierarchy method_hierarchy : m_hierarchies){
      if(method_hierarchy.isPolyMorphic()){
        ret.add(method_hierarchy.getOpenCLPolyMorphicMethod());
      }
    }   
    return ret;
  }
  
  private class MethodHierarchy {
    
    private String m_methodSubsignature;
    private SootMethod m_sootMethod;
    private List<SootClass> m_hierarchy;
    
    public MethodHierarchy(SootMethod method){
      m_methodSubsignature = method.getSubSignature();
      m_sootMethod = method;
    }
    
    public List<OpenCLMethod> getMethods(){
      List<OpenCLMethod> ret = new ArrayList<OpenCLMethod>();
      List<SootClass> class_hierarchy = OpenCLScene.v().getClassHierarchy(m_sootMethod.getDeclaringClass());
      for(SootClass soot_class : class_hierarchy){
        SootMethod soot_method = null;
        try {
          soot_method = soot_class.getMethod(m_methodSubsignature);
        } catch(Exception ex){
          continue;
        }
        OpenCLMethod method = new OpenCLMethod(soot_method, soot_class);
        ret.add(method);
      }      
      return ret;
    }
        
    public boolean isPolyMorphic(){
      List<SootClass> class_hierarchy = OpenCLScene.v().getClassHierarchy(m_sootMethod.getDeclaringClass());
      if(class_hierarchy.size() > 1)
        return true;
      return false;
    }
    
    public OpenCLPolymorphicMethod getOpenCLPolyMorphicMethod(){
      List<SootClass> class_hierarchy = OpenCLScene.v().getClassHierarchy(m_sootMethod.getDeclaringClass());
      for(SootClass soot_class : class_hierarchy){
        try {
          SootMethod soot_method = soot_class.getMethod(m_methodSubsignature);
          return new OpenCLPolymorphicMethod(soot_method);
        } catch(RuntimeException ex){
          continue;
        }
      }
      throw new RuntimeException("Cannot find class: "+m_methodSubsignature);
    }
    
    @Override
    public boolean equals(Object o){
      if(o instanceof MethodHierarchy == false)
        return false;
      MethodHierarchy other = (MethodHierarchy) o;
      if(m_methodSubsignature.equals(other.m_methodSubsignature) == false)
        return false;
      saveHierarchy();
      other.saveHierarchy();
      if(m_hierarchy == other.m_hierarchy == false){
        return false;
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 7;
      hash = 59 * hash + (this.m_methodSubsignature != null ? this.m_methodSubsignature.hashCode() : 0);
      hash = 59 * hash + (this.m_hierarchy != null ? this.m_hierarchy.hashCode() : 0);
      return hash;
    }

    private void saveHierarchy() {
      m_hierarchy = OpenCLScene.v().getClassHierarchy(m_sootMethod.getDeclaringClass());
    }
  }
}
