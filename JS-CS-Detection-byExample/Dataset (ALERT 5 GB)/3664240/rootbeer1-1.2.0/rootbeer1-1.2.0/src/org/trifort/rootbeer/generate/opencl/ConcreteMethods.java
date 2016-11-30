package org.trifort.rootbeer.generate.opencl;

import java.util.ArrayList;
import java.util.List;

import soot.SootMethod;
import soot.rbclassload.ClassHierarchy;
import soot.rbclassload.MethodSignatureUtil;
import soot.rbclassload.RootbeerClassLoader;

public class ConcreteMethods {

  public List<String> get(String signature){
    MethodSignatureUtil util = new MethodSignatureUtil();
    
    ClassHierarchy class_hierarchy = RootbeerClassLoader.v().getClassHierarchy();
    List<String> virtual_methods = class_hierarchy.getVirtualMethods(signature);
    
    List<String> ret = new ArrayList<String>();
    for(String virt_method : virtual_methods){
      util.parse(virt_method);
      SootMethod method = util.getSootMethod();
      if(method.isAbstract() == false){
        ret.add(virt_method);
      }
    }
    
    return ret;
  }
}
