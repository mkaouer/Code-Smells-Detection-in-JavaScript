/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.cfile.internal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NamespaceStack {

  private List<String> m_Type;
  private List<String> m_Identifiers;

  public NamespaceStack(){
    m_Type = new LinkedList<String>();
    m_Identifiers = new LinkedList<String>();
  }

  public boolean isEmpty(){
    return m_Type.isEmpty();
  }

  public void push(String scope, String identifier){
    m_Type.add(scope);
    m_Identifiers.add(identifier);
  }

  public String getNamespace(){
    List<String> namespaces = new ArrayList<String>();
    for(int i = 0; i < m_Type.size(); ++i){
      String type = m_Type.get(i);
      String id = m_Identifiers.get(i);
      if(type.equals("namespace"))
        namespaces.add(id);
    }
    String ret = "";
    for(int i = 0; i < namespaces.size(); ++i){
      ret += namespaces.get(i);
      if(i < namespaces.size()-1)
        ret += "::";
    }
    return ret;
  }

  public void pop(){
    m_Type.remove(m_Type.size()-1);
    m_Identifiers.remove(m_Identifiers.size()-1);
  }

  boolean nameSpaceOnTop() {
    String top = m_Type.get(m_Type.size()-1);
    if(top.equals("namespace"))
      return true;
    return false;
  }
}
