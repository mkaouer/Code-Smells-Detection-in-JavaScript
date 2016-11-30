/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.classloader;

import edu.syr.pcpratts.rootbeer.util.MethodSignatureUtil;
import java.util.Iterator;
import java.util.logging.Logger;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

public class MethodFinder {

  private final Logger m_log = Logger.getLogger(this.getClass().getName()); 
  
  public SootMethod find(String method_sig){
    MethodSignatureUtil util = new MethodSignatureUtil(method_sig);
    String class_name = util.getClassName();
    String method_subsig = util.getMethodSubSignature();
    SootClass soot_class = Scene.v().getSootClass(class_name);
    if(soot_class.isPhantom()){
      return null;
    }
    SootClass curr_class = soot_class;
    while(true){
      SootMethod ret = find(curr_class, method_subsig);
      if(ret == null){
        Iterator<SootClass> iter = curr_class.getInterfaces().iterator();
        while(iter.hasNext()){
          SootClass curr = iter.next();
          ret = find(curr, method_subsig);
          if(ret != null){
            return ret;
          }
        }
        if(curr_class.hasSuperclass() == false){
          //m_log.warning("cannot find method: "+method_sig);
          return null;
        }
        curr_class = curr_class.getSuperclass();
      } else {
        return ret;
      }
    }
  }

  private SootMethod find(SootClass soot_class, String method_subsig) {
    try {
      return soot_class.getMethod(method_subsig);
    } catch(Exception ex){
      return null;
    }
  }
}
