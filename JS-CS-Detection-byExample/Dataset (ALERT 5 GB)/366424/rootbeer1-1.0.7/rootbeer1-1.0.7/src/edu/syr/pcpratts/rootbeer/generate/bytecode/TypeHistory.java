/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.bytecode;

import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import soot.ArrayType;
import soot.RefType;
import soot.SootClass;
import soot.Type;

public class TypeHistory {
  private Set<Type> mHistory;

  public TypeHistory(SootClass runtime_basic_block){
    mHistory = new HashSet<Type>();
    addType(runtime_basic_block.getType());
  }

  public void addType(Type type){
    mHistory.add(type);
  }
  
  public List<Type> getHistory() {
    List<Type> ret = new ArrayList<Type>();
    Iterator<Type> iter = mHistory.iterator();
    while(iter.hasNext()){
      ret.add(iter.next());
    }
    return ret;
  }
}
