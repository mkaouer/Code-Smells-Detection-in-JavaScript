/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.bytecode;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.generate.opencl.OpenCLMethod;
import org.trifort.rootbeer.generate.opencl.OpenCLScene;

import soot.Local;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.internal.JimpleLocal;

public class MethodCodeSegment {
  private SootMethod m_existingMethod;
  private SootMethod m_clonedMethod;
  private SootClass m_clonedClass;

  public MethodCodeSegment(SootMethod method){
    this.m_existingMethod = method;
  }

  public List<Local> getInputArguments() {
    List<Local> ret = new ArrayList<Local>();
    Type t = m_existingMethod.getDeclaringClass().getType();
    Local l = new JimpleLocal("r0", t);
    ret.add(l);
    return ret;
  }

  public List<Local> getOutputArguments() {
    return new ArrayList<Local>();
  }

  public SootClass getSootClass() {
    return m_existingMethod.getDeclaringClass();
  }

  public List<Value> getInputValues() {
    return new ArrayList<Value>();
  }

  public List<Type> getParameterTypes() {
    List<Type> ret = new ArrayList<Type>();
    Type t = m_existingMethod.getDeclaringClass().getType();
    ret.add(t);
    return ret;
  }

  public void makeCpuBody(SootClass soot_class) {
    m_clonedClass = soot_class;
  }

  public SootMethod getRootMethod() {
    return m_existingMethod;
  }

  public SootClass getRootSootClass() {
    return m_existingMethod.getDeclaringClass();
  }

}
