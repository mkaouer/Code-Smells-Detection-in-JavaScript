/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.compiler;

import edu.syr.pcpratts.rootbeer.classloader.DfsInfo;
import java.util.ArrayList;
import java.util.List;
import soot.*;
import soot.util.NumberedString;

public class RootbeerScene {

  private static RootbeerScene m_instance;
  
  public static RootbeerScene v(){
    if(m_instance == null){
      m_instance = new RootbeerScene();
    }
    return m_instance;
  }

  private List<String> m_addedClasses;
  private List<String> m_reachables;
  private List<SootClass> m_allClasses;
  private List<String> m_forwardReachables;
  private DfsInfo m_dfsInfo;
  
  public RootbeerScene(){
    m_addedClasses = new ArrayList<String>();
    m_allClasses = new ArrayList<SootClass>();
  }
  
  public void addClass(SootClass new_class) {
    if(m_addedClasses.contains(new_class.getName()) == false){
      m_addedClasses.add(new_class.getName());
      Scene.v().addClass(new_class);
    }
  }

  public List<String> getAddedClasses() {
    return m_addedClasses;
  }

  public void setReachableMethods(List<String> reachables) {
    m_reachables = reachables;
  }
  
  public void setForwardReachables(List<String> reachables) {
    m_forwardReachables = reachables;
  }

  public List<String> getReachableMethods() {
    return m_reachables;
  }

  public List<SootClass> getAllClasses() {
    return m_allClasses;
  }
  
  public void addAllClass(SootClass soot_class){
    if(m_allClasses.contains(soot_class) == false){
      m_allClasses.add(soot_class);
    }
  }

  public List<String> getForwardReachableMethods() {
    return m_forwardReachables;
  }

  public void setDfsInfo(DfsInfo info) {
    m_dfsInfo = info;
  }
  
  public DfsInfo getDfsInfo(){
    return m_dfsInfo;
  }

  public SootMethod getMethod(SootClass soot_class, String subSignature) {
    SootClass curr_class = soot_class;
    while(true){
      try { 
        SootMethod ret = curr_class.getMethod(subSignature);
        return ret;
      } catch(Exception ex){
        if(curr_class.hasSuperclass() == false){
          break;
        }
        curr_class = curr_class.getSuperclass();
      }
    }
    throw new RuntimeException("cannot find method: "+subSignature+" in class: "+soot_class);
  }
}
