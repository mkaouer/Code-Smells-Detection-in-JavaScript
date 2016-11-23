package org.trifort.rootbeer.generate.bytecode;

import java.util.HashSet;
import java.util.Set;

public class JavaNumberTypes {

  private Set<String> m_types;
  
  public JavaNumberTypes(){
    m_types = new HashSet<String>();
    m_types.add("java.lang.Byte");
    m_types.add("java.lang.Boolean");
    m_types.add("java.lang.Character");
    m_types.add("java.lang.Short");
    m_types.add("java.lang.Integer");
    m_types.add("java.lang.Long");
    m_types.add("java.lang.Float");
    m_types.add("java.lang.Double");
  }
  
  public Set<String> get(){
    return m_types;
  }
}
