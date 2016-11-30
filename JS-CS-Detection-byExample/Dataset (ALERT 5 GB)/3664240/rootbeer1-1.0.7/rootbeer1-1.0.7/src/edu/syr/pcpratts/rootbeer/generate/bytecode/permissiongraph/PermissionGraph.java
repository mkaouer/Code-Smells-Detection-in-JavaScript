/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.bytecode.permissiongraph;

import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import java.util.ArrayList;
import java.util.List;
import soot.RefType;
import soot.SootClass;

public class PermissionGraph {

  private List<PermissionGraphNode> m_Roots;
  
  public PermissionGraph(){
    m_Roots = new ArrayList<PermissionGraphNode>();
    build(OpenCLScene.v().getRefTypeOrderedHistory());
  }

  private void build(List<RefType> history) {
    for(RefType type : history){
      SootClass soot_class = type.getSootClass();
      if(soot_class.isPublic()){
        m_Roots.add(new PermissionGraphNode(soot_class));
      }
    }
    for(RefType type : history){
      SootClass soot_class = type.getSootClass();
      if(soot_class.isPublic() == false){
        PermissionGraphNode root = findRoot(soot_class);
        root.addChild(soot_class);
      }
    }
  }
  
  public List<PermissionGraphNode> getRoots(){
    return m_Roots;
  }

  private PermissionGraphNode findRoot(SootClass soot_class) {
    String pkg = soot_class.getJavaPackageName();
    for(PermissionGraphNode node : m_Roots){
      String root_pkg = node.getSootClass().getJavaPackageName();
      if(pkg.equals(root_pkg))
        return node;
    }
    throw new RuntimeException("can't find root");
  }
}
