/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl.body;

import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import soot.SootClass;
import soot.Trap;
import soot.Unit;

public class TrapItem {

  private int m_TrapNum;
  private List<Unit> m_TryUnits;
  private Unit m_Handler;
  private SootClass m_Exception;

  TrapItem(Trap t, Iterator<Unit> iter, int trap_num, SootClass exception) {
    m_TrapNum = trap_num;
    m_TryUnits = new ArrayList<Unit>();
    boolean found_start = false;
    boolean found_end = false;
    while(iter.hasNext()){
      Unit next = iter.next();
      if(t.getBeginUnit().equals(next)){
        found_start = true;
      }
      if(t.getEndUnit().equals(next)){
        found_end = true; 
      }
      if(found_start && !found_end){
        m_TryUnits.add(next);
      }
    }
    m_Handler = t.getHandlerUnit();
    m_Exception = exception;
  }

  List<Unit> getUnits() {
    return m_TryUnits;
  }

  int getTrapNum() {
    return m_TrapNum;
  }

  boolean unitIsHandler(Unit next) {
    return m_Handler.equals(next);
  }
  
  SootClass getException(){
    return m_Exception;
  }

  List<Integer> getTypeList() {
    List<SootClass> classes = getTrimmedClassHierarchy();
    List<Integer> ret = new ArrayList<Integer>();
    for(SootClass soot_class : classes){
      int num = OpenCLScene.v().getClassType(soot_class);
      ret.add(num);
    }
    return ret;
  }
  
  List<SootClass> getTrimmedClassHierarchy(){
    List<SootClass> all = OpenCLScene.v().getClassHierarchy(m_Exception);
    List<SootClass> ret = new ArrayList<SootClass>();
    for(int i = 0; i < all.size(); ++i){
      SootClass curr = all.get(i);
      ret.add(curr);
      if(curr.equals(m_Exception))
        break;
    }
    return ret;
  }
}
