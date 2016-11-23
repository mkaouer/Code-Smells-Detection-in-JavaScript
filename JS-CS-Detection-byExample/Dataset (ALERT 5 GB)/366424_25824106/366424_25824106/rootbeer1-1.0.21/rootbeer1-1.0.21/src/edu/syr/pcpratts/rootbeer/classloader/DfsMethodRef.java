/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.classloader;

import soot.SootMethodRef;
import soot.jimple.Stmt;

public class DfsMethodRef {
  
  private final SootMethodRef m_ref;
  private final Stmt m_stmt;

  public DfsMethodRef(SootMethodRef ref, Stmt stmt){
    m_ref = ref;
    m_stmt = stmt;
  }
  
  public SootMethodRef getSootMethodRef(){
    return m_ref;
  }
  
  public Stmt getStmt(){
    return m_stmt;
  } 
  
  @Override
  public boolean equals(Object other){
    if(other instanceof DfsMethodRef == false){
      return false;
    }
    DfsMethodRef rhs = (DfsMethodRef) other;
    if(m_ref.equals(rhs.m_ref) && m_stmt.equals(rhs.m_stmt)){
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 23 * hash + (this.m_ref != null ? this.m_ref.hashCode() : 0);
    hash = 23 * hash + (this.m_stmt != null ? this.m_stmt.hashCode() : 0);
    return hash;
  }
}
