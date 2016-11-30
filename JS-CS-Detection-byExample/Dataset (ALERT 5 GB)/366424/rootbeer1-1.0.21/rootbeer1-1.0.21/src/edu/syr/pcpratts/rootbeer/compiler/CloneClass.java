/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.compiler;

import java.util.Iterator;
import java.util.List;
import soot.Body;
import soot.Modifier;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;

public class CloneClass {
  
  public SootClass execute(SootClass soot_class, String new_class_name) {
    SootClass ret = new SootClass(new_class_name, Modifier.PUBLIC);
    List<SootMethod> methods = soot_class.getMethods();
    for(SootMethod method : methods){
      SootMethod new_method = new SootMethod(method.getName(), method.getParameterTypes(), method.getReturnType(), method.getModifiers(), method.getExceptions());
      if(method.isConcrete()){
        Body body = method.retrieveActiveBody();
        new_method.setActiveBody((Body) body.clone());
      }
      ret.addMethod(new_method);
    }
    Iterator<SootField> iter = soot_class.getFields().iterator();
    while(iter.hasNext()){
      SootField next = iter.next();
      SootField cloned = new SootField(next.getName(), next.getType(), next.getModifiers());
      ret.addField(cloned);
    }
    Iterator<SootClass> iter2 = soot_class.getInterfaces().iterator();
    while(iter2.hasNext()){
      SootClass next = iter2.next();
      ret.addInterface(next);
    }
    if(soot_class.hasSuperclass()){
      ret.setSuperclass(soot_class.getSuperclass());
    }
    if(soot_class.hasOuterClass()){
      ret.setOuterClass(soot_class.getOuterClass());
    }
    return ret;
  }
}
