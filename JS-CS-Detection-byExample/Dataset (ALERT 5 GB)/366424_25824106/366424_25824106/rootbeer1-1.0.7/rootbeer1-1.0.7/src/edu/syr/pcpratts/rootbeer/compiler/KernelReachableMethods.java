/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.compiler;

import edu.syr.pcpratts.rootbeer.classloader.FastWholeProgram;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.util.Chain;

public class KernelReachableMethods {

  private List<String> m_ret;
  private List<String> m_kernelClasses;
  private List<String> m_intoGpuMethod;
  private Set<String> m_visited;
  
  public List<String> get(List<String> kernel_classes){
    m_ret = new ArrayList<String>();
    m_kernelClasses = kernel_classes;
    for(String kernel_class : kernel_classes){
      find(kernel_class);
    }
    String[] array_ret = new String[m_ret.size()];
    array_ret = m_ret.toArray(array_ret);
    Arrays.sort(array_ret);
    m_ret.clear();
    for(String str : array_ret){
      m_ret.add(str);
    }
    return m_ret;
  }

  private void find(String kernel_class) {
    SootClass soot_class = Scene.v().getSootClass(kernel_class);
    SootMethod gpuMethod = soot_class.getMethodByName("gpuMethod");
    m_visited = new HashSet<String>();
    dfs(gpuMethod);
    
    Chain<SootClass> scene_classes = Scene.v().getApplicationClasses();
    m_intoGpuMethod = new ArrayList<String>();
    addClass(soot_class);
    int prev_size = -1;
    while(prev_size != m_intoGpuMethod.size()){
      prev_size = m_intoGpuMethod.size();
      for(SootClass cls : scene_classes){
        search(cls.getName());
      }
    }
    for(String method : m_intoGpuMethod){
      if(m_ret.contains(method) == false){
        m_ret.add(method);
      }
    }
  }

  private void dfs(SootMethod method) {
    String signature = method.getSignature();
    if(m_visited.contains(signature)){
      return;
    }
    m_visited.add(signature);
    if(method.isConcrete() == false){
      return;
    }
    if(m_ret.contains(signature) == false){
      m_ret.add(signature);
    }    
    Body body = method.retrieveActiveBody();
    List<ValueBox> boxes = body.getUseAndDefBoxes();
    for(ValueBox box : boxes){
      Value value = box.getValue();
      if(value instanceof InvokeExpr){
        InvokeExpr expr = (InvokeExpr) value;
        SootMethodRef ref = expr.getMethodRef();
        FastWholeProgram.v().loadToBodyLater(ref.getSignature());
        SootMethod method2 = expr.getMethod();
        dfs(method2);
      }
    }      
  }

  private void search(String cls) {
    SootClass soot_class = Scene.v().getSootClass(cls);
    List<SootMethod> methods = soot_class.getMethods();
    for(SootMethod method : methods){
      if(reachesIntoGpuMethod(method)){
        addClass(soot_class);
        break;
      }
    }
  }
  
  private boolean reachesIntoGpuMethod(SootMethod method) {
    if(method.isConcrete() == false){
      return false;
    }
    Body body = method.retrieveActiveBody();
    if(body == null){
      return false;
    }
    List<ValueBox> boxes = body.getUseAndDefBoxes();
    for(ValueBox box : boxes){
      Value value = box.getValue();
      if(value instanceof InvokeExpr){
        InvokeExpr expr = (InvokeExpr) value;
        SootClass soot_class = expr.getMethodRef().declaringClass();
        try {
          soot_class = Scene.v().getSootClass(soot_class.getName());
        } catch(RuntimeException ex){
          continue;
        }
        int level = soot_class.resolvingLevel();
        if(level < SootClass.SIGNATURES){
          continue;
        }
        String signature = expr.getMethodRef().getSignature();
        if(m_intoGpuMethod.contains(signature)){
          return true;
        }
        if(m_kernelClasses.contains(soot_class.getName())){
          return true;
        }
      }
    }   
    return false;
  }

  private void addClass(SootClass soot_class) {
    if(soot_class.getName().equals("java.lang.Object")){
      return;
    }
    List<SootMethod> methods = soot_class.getMethods();
    for(SootMethod method : methods){
      String signature = method.getSignature();
      if(m_intoGpuMethod.contains(signature) == false){
        m_intoGpuMethod.add(signature);
      } 
    }
    SootClass parent = soot_class.getSuperclass();
    addClass(parent);
  }
}
