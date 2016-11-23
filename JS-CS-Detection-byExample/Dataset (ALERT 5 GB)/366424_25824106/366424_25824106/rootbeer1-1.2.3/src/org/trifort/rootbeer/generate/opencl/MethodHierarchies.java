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

import soot.Hierarchy;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.rbclassload.ClassHierarchy;
import soot.rbclassload.HierarchyGraph;
import soot.rbclassload.MethodSignatureUtil;
import soot.rbclassload.RootbeerClassLoader;

/**
 * Represents all the versions of methods in a class Hierarchy
 * @author pcpratts
 */
public class MethodHierarchies {

  private Set<MethodHierarchy> m_hierarchies;
  private MethodSignatureUtil m_util;
  
  public MethodHierarchies(){
    m_hierarchies = new LinkedHashSet<MethodHierarchy>();
    m_util = new MethodSignatureUtil();
  }
  
  public void addMethod(SootMethod method){
    MethodHierarchy new_hierarchy = new MethodHierarchy(method);
    
    for(MethodHierarchy curr : m_hierarchies){
      if(curr.contains(new_hierarchy)){
        return;
      }
    }
    
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
    for(MethodHierarchy method_hierarchy : m_hierarchies){
      if(method_hierarchy.isPolyMorphic()){
        ret.add(method_hierarchy.getOpenCLPolyMorphicMethod());
      }
    }   
    return ret;
  }
  
  private class MethodHierarchy {
    
    private SootMethod m_method;
    
    public MethodHierarchy(SootMethod method){
      m_method = method;
    }
    
    public List<OpenCLMethod> getMethods(){
      List<OpenCLMethod> ret = new ArrayList<OpenCLMethod>();
      if(m_method.isConstructor()){
        OpenCLMethod method = new OpenCLMethod(m_method, m_method.getDeclaringClass());
        ret.add(method);
        return ret;
      }
      
      List<String> methods = RootbeerClassLoader.v().getClassHierarchy().getVirtualMethods(m_method.getSignature());
      for(String virt_method : methods){
        m_util.parse(virt_method);
        SootMethod soot_method = m_util.getSootMethod();
        OpenCLMethod method = new OpenCLMethod(soot_method, soot_method.getDeclaringClass());
        ret.add(method);
      }
      return ret;
    }
        
    public boolean isPolyMorphic(){
      IsPolymorphic poly_checker = new IsPolymorphic();
      if(poly_checker.test(m_method)){
        return true;
      }
      return false;
    }
    
    public OpenCLPolymorphicMethod getOpenCLPolyMorphicMethod(){
      SootClass soot_class = m_method.getDeclaringClass();
      List<SootClass> interfaces = new ArrayList<SootClass>();
      interfaces.addAll(soot_class.getInterfaces());
      String sub_sig = m_method.getSubSignature();
      for(SootClass iface : interfaces){
        if(iface.declaresMethod(sub_sig)){
          SootMethod iface_method = iface.getMethod(sub_sig);
          return new OpenCLPolymorphicMethod(iface_method);
        }
      }
      return new OpenCLPolymorphicMethod(m_method);
    }
    
    public boolean contains(MethodHierarchy other){
      if(m_method.getSubSignature().equals(other.m_method.getSubSignature()) == false)
        return false;
      
      SootClass lhs_class = m_method.getDeclaringClass();
      SootClass rhs_class = other.m_method.getDeclaringClass();
      Integer lhs_number = RootbeerClassLoader.v().getClassNumber(lhs_class);
      Integer rhs_number = RootbeerClassLoader.v().getClassNumber(rhs_class);
      HierarchyGraph hgraph = RootbeerClassLoader.v().getClassHierarchy().getHierarchyGraph();
      if(hgraph.sameHierarchy(lhs_number, rhs_number)){
        return true;
      }
      return false;
    }
  }
}
