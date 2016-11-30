/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.bytecode;

import java.util.ArrayList;
import java.util.List;

public class AcceptableGpuTypes {
  
  private List<String> m_ExcludedPackages;
  
  public AcceptableGpuTypes(){    
    m_ExcludedPackages = new ArrayList<String>();
    m_ExcludedPackages.add("java.");
    m_ExcludedPackages.add("sun.");
    m_ExcludedPackages.add("javax.");
    m_ExcludedPackages.add("com.sun.");
    m_ExcludedPackages.add("com.ibm.");
    m_ExcludedPackages.add("org.xml.");
    m_ExcludedPackages.add("org.w3c.");
    m_ExcludedPackages.add("apple.awt.");
    m_ExcludedPackages.add("com.apple.");
  }
  
  boolean shouldGenerateCtor(String type) {
    if(type.equals("java.lang.StringBuilder"))
      return false;
    if(type.equals("java.lang.AbstractStringBuilder"))
      return false;
    return true;
  }
  
}
