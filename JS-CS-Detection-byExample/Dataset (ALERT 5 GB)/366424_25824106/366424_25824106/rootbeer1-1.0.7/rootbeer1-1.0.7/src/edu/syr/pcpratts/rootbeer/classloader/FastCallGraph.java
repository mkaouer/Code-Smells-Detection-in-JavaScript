/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.classloader;

import java.util.*;
import soot.MethodOrMethodContext;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.Edge;

public class FastCallGraph {

  private Map<String, List<String>> m_methodCalls;
  private List<Edge> m_edges;
  private MethodFinder m_finder;
  
  public FastCallGraph(){
    m_methodCalls = new HashMap<String, List<String>>();
    m_edges = new ArrayList<Edge>();
    m_finder = new MethodFinder();
  }
  
  public void addEdge(String src, String dest, Stmt stmt){
    SootMethod src_method = m_finder.find(src);
    if(src_method == null){
      return;
    }
    SootMethod dest_method = m_finder.find(dest);
    if(dest_method == null){
      return;
    }
    if(m_methodCalls.containsKey(src)){
      List<String> methods = m_methodCalls.get(src);
      if(methods.contains(dest)){
        return;
      }
      methods.add(dest);
      Edge edge = new Edge(src_method, stmt, dest_method);
      m_edges.add(edge);
    } else {
      List<String> methods = new ArrayList<String>();
      methods.add(dest);
      Edge edge = new Edge(src_method, stmt, dest_method);
      m_edges.add(edge);
      m_methodCalls.put(src, methods);
    }
  }
  
  public List<String> getMethodCalls(String method){
    List<String> ret = m_methodCalls.get(method);
    if(ret == null){
      return new ArrayList<String>();
    } else {
      return ret;
    }
  }
  
  public Collection<String> getReachableMethods(){
    return m_methodCalls.keySet();
  }

  public List<Edge> getEdges(){
    return m_edges;
  }
}
