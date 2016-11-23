/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl.body;

import java.util.ArrayList;
import java.util.List;
import soot.Unit;

public class MonitorGroupItem {
  
  private List<Unit> m_PrefixUnits;
  private Unit m_EnterMonitorUnit;
  private List<MonitorGroupItem> m_MonitorGroups;
  
  public MonitorGroupItem(){
    m_PrefixUnits = new ArrayList<Unit>();
    m_MonitorGroups = new ArrayList<MonitorGroupItem>();
  }

  public List<Unit> getPrefixUnits() {
    return m_PrefixUnits;
  }
    
  public List<MonitorGroupItem> getGroups(){
    return m_MonitorGroups;
  }
  
  public Unit getEnterMonitor(){
    return m_EnterMonitorUnit;
  }

  public void addUnit(Unit curr) {
    m_PrefixUnits.add(curr);
  }
  
  public void addGroup(MonitorGroupItem item) {
    m_MonitorGroups.add(item);
  }

  public void addEnterMonitor(Unit curr) {
    m_EnterMonitorUnit = curr;
  }
}
