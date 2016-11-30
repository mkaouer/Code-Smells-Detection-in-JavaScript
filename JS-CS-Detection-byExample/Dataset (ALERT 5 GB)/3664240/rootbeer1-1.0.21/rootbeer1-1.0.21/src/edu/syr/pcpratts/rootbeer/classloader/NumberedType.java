/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.classloader;

import soot.Type;

public class NumberedType implements Comparable<NumberedType> {

  private long m_number;
  private Type m_type;
  
  public NumberedType(Type type, long number){
    m_type = type;
    m_number = number;
  }
  
  public Type getType(){
    return m_type;
  }
  
  public long getNumber(){
    return m_number;
  }
  
  public int compareTo(NumberedType o) {
    return Long.valueOf(o.m_number).compareTo(Long.valueOf(m_number));
  }

}
