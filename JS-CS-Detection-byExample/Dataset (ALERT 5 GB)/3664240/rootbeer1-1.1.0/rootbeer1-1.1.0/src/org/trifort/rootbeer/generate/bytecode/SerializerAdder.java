/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.bytecode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.trifort.rootbeer.generate.codesegment.CodeSegment;
import org.trifort.rootbeer.generate.opencl.OpenCLScene;

import soot.*;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;

public class SerializerAdder {

  private String m_serializerClassName;

  public SerializerAdder(){
  }

  public void add(CodeSegment block){
    System.out.println("generating serialization bytecode...");
    SootClass block_class = block.getRootSootClass();

    VisitorGen generate_visitor = new VisitorGen(block_class);
    generate_visitor.generate();
    m_serializerClassName = generate_visitor.getClassName();
  }

  String getGcObjectVisitorClassName() {
    return m_serializerClassName;
  }
}
