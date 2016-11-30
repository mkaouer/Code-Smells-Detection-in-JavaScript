/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.classloader;

import java.util.ArrayList;
import java.util.List;
import soot.*;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;

public class MethodRefFinder {

  private MethodFinder m_methodFinder;
  
  public MethodRefFinder(){
    m_methodFinder = new MethodFinder();
  }
          
  public List<String> find(String curr_signature) {
    SootMethod curr = m_methodFinder.find(curr_signature);
    List<String> ret = new ArrayList<String>();
    if(curr == null || curr.isConcrete() == false){
      return ret;
    }
    Body body = curr.retrieveActiveBody();
    List<ValueBox> boxes = body.getUseAndDefBoxes();
    for(ValueBox box : boxes){
      Value value = box.getValue();
      if(value instanceof InvokeExpr){
        InvokeExpr expr = (InvokeExpr) value;
        SootMethodRef ref = expr.getMethodRef();
        String class_name = ref.declaringClass().getName();
        ret.add(class_name);
      } else if(value instanceof RefType){
        RefType ref_type = (RefType) value;
        ret.add(ref_type.getClassName());
      } else if(value instanceof FieldRef){
        FieldRef field_ref = (FieldRef) value;
        String soot_class = field_ref.getFieldRef().declaringClass().getName();
        ret.add(soot_class);
      }
    }
    return ret;
  }
  
}
