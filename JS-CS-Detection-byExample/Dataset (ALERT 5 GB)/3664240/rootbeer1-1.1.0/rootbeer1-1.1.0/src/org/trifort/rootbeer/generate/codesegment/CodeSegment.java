/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.codesegment;

import java.util.List;
import soot.Local;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Value;

public interface CodeSegment {
  public List<Local> getInputArguments();
  public List<Local> getOutputArguments();
  public SootClass getSootClass();
  public List<Value> getInputValues();
  public List<Type> getParameterTypes();
  public void makeCpuBodyForRuntimeBasicBlock(SootClass mClass);
  public SootMethod getRootMethod();
  public SootClass getRootSootClass();
}
