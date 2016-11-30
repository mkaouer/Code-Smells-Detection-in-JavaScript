/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl.fields;

import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLClass;
import java.util.ArrayList;
import java.util.List;
import soot.SootClass;

public class TreeNode {

  private SootClass m_Curr;
  private OpenCLClass m_CurrOcl;
  private List<TreeNode> m_Children;

  public TreeNode(SootClass root, OpenCLClass ocl_class){
    m_Curr = root;
    m_CurrOcl = ocl_class;
    m_Children = new ArrayList<TreeNode>();
  }

  public void addChild(SootClass child, OpenCLClass ocl_class){
    TreeNode new_node = new TreeNode(child, ocl_class);
    m_Children.add(new_node);
  }
  
  public SootClass getSootClass(){
    return m_Curr;
  }
  
  public OpenCLClass getOpenCLClass(){
    return m_CurrOcl;
  }
  
  public List<TreeNode> getChildren(){
    return m_Children;
  }

  public TreeNode find(SootClass node){
    if(m_Curr.getName().equals(node.getName()))
      return this;
    for(TreeNode child : m_Children){
      TreeNode ret = child.find(node);
      if(ret != null)
        return ret;
    }
    return null;
  }

  public void print(){
    System.out.println("curr: "+m_Curr.getName());
    for(TreeNode child : m_Children){
      System.out.println("child: "+child.m_Curr.getName());
    }
    for(TreeNode child : m_Children){
      child.print();
    }
  }
}