/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl;

import java.util.List;
import soot.*;

public class IsPolyMorphic {

  public boolean isPoly(SootMethod soot_method, List<Type> hierarchy){ 
    if(hierarchy.size() > 2){
      return true;
    }
    if(hierarchy.size() == 1){
      return false;
    }
    String sub_sig = soot_method.getSubSignature();
    int count = 0;
    for(Type type : hierarchy){
      if(type instanceof RefType == false){
        continue;
      }
      RefType ref_type = (RefType) type;
      SootClass curr = ref_type.getSootClass();
      if(curr.declaresMethod(sub_sig)){
        count++;
      }
    }
    if(count >= 2){
      return true;
    }
    return false;
  }
}
