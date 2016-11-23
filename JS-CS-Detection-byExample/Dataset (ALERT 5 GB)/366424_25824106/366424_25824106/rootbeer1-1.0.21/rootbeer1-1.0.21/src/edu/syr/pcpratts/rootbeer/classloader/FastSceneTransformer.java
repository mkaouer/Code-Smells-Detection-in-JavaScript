/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.classloader;

import java.util.Collection;

import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;

public interface FastSceneTransformer {
  
  void internalTransform(String phaseName, Collection<SootMethod> entries, CallGraph cg);
}
