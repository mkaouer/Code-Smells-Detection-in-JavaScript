/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.bytecode.permissiongraph;

import java.util.ArrayList;
import java.util.List;
import soot.SootClass;

public class PermissionGraphNode {

  private SootClass m_class;
  private List<SootClass> m_children;
  
  public PermissionGraphNode(SootClass soot_class){
    m_class = soot_class;
    m_children = new ArrayList<SootClass>();
  }

  public void addChild(SootClass soot_class) {
    m_children.add(soot_class);
  }
  
  public List<SootClass> getChildren(){
    return m_children;
  }
  
  public SootClass getSootClass(){
    return m_class;
  }
  
  @Override
  public String toString(){
    String ret = "";
    ret += "root: "+m_class.getName()+"\n";
    for(SootClass child : m_children){
      ret += "child: "+child.getName();
    }
    return ret;
  }
}
