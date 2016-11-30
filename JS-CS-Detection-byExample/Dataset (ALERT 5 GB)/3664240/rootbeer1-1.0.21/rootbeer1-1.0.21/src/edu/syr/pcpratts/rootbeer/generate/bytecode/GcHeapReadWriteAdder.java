/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.bytecode;

import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.generate.codesegment.CodeSegment;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import soot.*;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;

public class GcHeapReadWriteAdder {

  private String gcObjectVisitorClassName;

  public GcHeapReadWriteAdder(){
  }

  public void add(CodeSegment block){
    System.out.println("generating serialization bytecode...");
    SootClass block_class = block.getRootSootClass();

    VisitorGen generate_visitor = new VisitorGen(block.getReadWriteFieldInspector(), block_class);
    generate_visitor.generate();
    gcObjectVisitorClassName = generate_visitor.getClassName();
  }

  String getGcObjectVisitorClassName() {
    return gcObjectVisitorClassName;
  }
}
