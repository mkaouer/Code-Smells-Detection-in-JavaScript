/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.bytecode;

import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.generate.bytecode.TypeHistory;
import edu.syr.pcpratts.rootbeer.generate.codesegment.CodeSegment;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import soot.*;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;

public class GcHeapReadWriteAdder {

  private Set<SootMethod> mMethodsInspected;
  private TypeHistory mTypeHistory;
  private String gcObjectVisitorClassName;

  public GcHeapReadWriteAdder(){
  }

  public void add(CodeSegment block){
    System.out.println("generating serialization bytecode...");
    mTypeHistory = OpenCLScene.v().getTypeHistory();;
    SootClass block_class = block.getRootSootClass();
    mMethodsInspected = new HashSet<SootMethod>();

    SootMethod root_method = block.getRootMethod();
    addToMethod(root_method);

    VisitorGen generate_visitor = new VisitorGen(block.getReadWriteFieldInspector(), block_class);
    generate_visitor.generate();
    gcObjectVisitorClassName = generate_visitor.getClassName();
  }

  private void addToMethod(SootMethod method) {
    if(mMethodsInspected.contains(method))
      return;
    mMethodsInspected.add(method);
    
    SootClass soot_class = method.getDeclaringClass();
    soot_class = Scene.v().getSootClass(soot_class.getName());
    
    while(true){
      mTypeHistory.addType(soot_class.getType());
      if(soot_class.hasSuperclass() == false)
        break;
      soot_class = soot_class.getSuperclass();
      soot_class = Scene.v().getSootClass(soot_class.getName());
      if(soot_class.getName().equals("java.lang.Object"))
        break;
    }

    Body body;
    try {
      body = method.getActiveBody();
    } catch(RuntimeException ex){
      //no body for method...
      return;
    }

    List<ValueBox> boxes = body.getUseAndDefBoxes();
    //foreach statement reachable in the block
    for(ValueBox box : boxes){
      Value v = box.getValue();
      //if it accesses a class in some way
      if(v instanceof InvokeExpr){
        InvokeExpr invoke_expr = (InvokeExpr) v;
        //make if adhere to the GcObject interface if it allready doesn't
        addToMethod(invoke_expr.getMethod());
      } else if(v instanceof FieldRef){
        FieldRef field_ref = (FieldRef) v;
        Type type = field_ref.getField().getType();
        if(type instanceof RefType){
          mTypeHistory.addType(type);
        } else if(type instanceof ArrayType){
          mTypeHistory.addType(type);
        }
      }
    }
  }

  String getGcObjectVisitorClassName() {
    return gcObjectVisitorClassName;
  }
}
