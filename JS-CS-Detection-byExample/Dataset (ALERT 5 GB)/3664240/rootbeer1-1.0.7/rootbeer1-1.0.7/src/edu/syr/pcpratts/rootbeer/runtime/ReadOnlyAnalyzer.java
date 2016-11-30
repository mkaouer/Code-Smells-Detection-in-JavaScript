/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime;

import java.util.IdentityHashMap;
import java.util.Map;

public abstract class ReadOnlyAnalyzer {

  protected Map<Object, Boolean> m_Map;
  
  public ReadOnlyAnalyzer(){
    m_Map = new IdentityHashMap<Object, Boolean>();
  }
  
  public abstract void analyze(Kernel root);
  
  public boolean isReadOnly(Object o){
    return m_Map.get(o);
  }
}
