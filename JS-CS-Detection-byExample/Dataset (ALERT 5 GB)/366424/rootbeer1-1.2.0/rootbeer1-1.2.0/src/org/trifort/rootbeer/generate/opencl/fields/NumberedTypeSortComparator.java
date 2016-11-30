/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl.fields;

import java.util.Comparator;
import soot.SootClass;
import soot.rbclassload.RootbeerClassLoader;

public class NumberedTypeSortComparator implements Comparator<SootClass>{

  private boolean m_lowest;
  
  public NumberedTypeSortComparator(boolean lowest_type_num_first) {
    m_lowest = lowest_type_num_first;
  }

  public int compare(SootClass lhs, SootClass rhs) {
    Integer lhs_number = Integer.valueOf(RootbeerClassLoader.v().getClassNumber(lhs));
    Integer rhs_number = Integer.valueOf(RootbeerClassLoader.v().getClassNumber(rhs));
    
    if(m_lowest){
      return lhs_number.compareTo(rhs_number);
    } else {
      return rhs_number.compareTo(lhs_number);
    }  
  }
  
}