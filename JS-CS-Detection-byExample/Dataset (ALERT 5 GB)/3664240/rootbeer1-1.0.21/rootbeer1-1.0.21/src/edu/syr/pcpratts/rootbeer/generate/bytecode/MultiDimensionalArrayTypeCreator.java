/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.bytecode;

import java.util.*;
import soot.ArrayType;
import soot.Type;

public class MultiDimensionalArrayTypeCreator {


  public MultiDimensionalArrayTypeCreator(){
  }

  public Set<ArrayType> create(Set<ArrayType> types){
    Set<ArrayType> ret = new HashSet<ArrayType>();
    for(ArrayType type : types){
      Type base_type = type.baseType;
      int dim = type.numDimensions;
      for(int i = dim - 1; i > 0; --i){
        ArrayType curr = ArrayType.v(base_type, i);
        if(types.contains(curr) == false){
          ret.add(curr);
        }
      }
    }
    return ret;
  }
}
