/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.bytecode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;
import soot.util.Chain;

public class UnitAssembler {

  private List<Unit> m_outputUnits;
  private List<Local> m_outputLocals;
  private Map<String, Local> m_localMap;
  private Map<String, List<UnitBox>> m_labelToUnitBoxMap;

  private List<Unit> m_inputUnits;
  private List<List<String>> m_labels;
  private Jimple m_jimple;

  public UnitAssembler(){
    m_outputUnits = new ArrayList<Unit>();
    m_outputLocals = new ArrayList<Local>();
    m_inputUnits = new ArrayList<Unit>();
    m_labels = new ArrayList<List<String>>();
    m_localMap = new HashMap<String, Local>();
    m_labelToUnitBoxMap = new HashMap<String, List<UnitBox>>();
    m_jimple = Jimple.v();
  }

  public void add(Unit u){
    m_inputUnits.add(u);
  }

  public void addAll(Collection<Unit> units){
    for(Unit u : units){
      m_inputUnits.add(u);
    }
  }

  void copyLocals(){
    for(Unit u : m_outputUnits){
      List<ValueBox> boxes = u.getUseAndDefBoxes();
      for(ValueBox box : boxes){
        Value v = box.getValue();
        if(v instanceof Local == false)
          continue;
        Local local = (Local) v.clone();
        if(m_localMap.containsKey(local.toString()) == false){
          m_localMap.put(local.toString(), local);
          m_outputLocals.add(local);
        }
        local = m_localMap.get(local.toString());
        box.setValue(local);
      }
    }
  }

  UnitBox getTarget(Unit input){
    if(input instanceof IfStmt){
      IfStmt if_stmt = (IfStmt) input;
      return if_stmt.getTargetBox();
    } else if(input instanceof GotoStmt){
      GotoStmt goto_stmt = (GotoStmt) input;
      return goto_stmt.getTargetBox();
    }
    return null;
  }

  void copyTargets(){
    for(int i = 0; i < m_inputUnits.size(); ++i){
      Unit input = m_inputUnits.get(i);
      Unit output = m_outputUnits.get(i);
      List<UnitBox> input_boxes = input.getUnitBoxes();
      List<UnitBox> output_boxes = output.getUnitBoxes();
      for(int j = 0; j < input_boxes.size(); ++j){
        UnitBox input_box = input_boxes.get(j);
        UnitBox output_box = output_boxes.get(j);

        Unit input_target = input_box.getUnit();
        //using the addIf method makes targets null
        if(input_target == null)
          continue;
        
        int target_i = findTarget(input_target);
        output_box.setUnit(m_outputUnits.get(target_i));
      }
    }
  }

  public Unit unitClone(Unit input){
    Unit output = (Unit) input.clone();
    List<UnitBox> input_boxes = input.getUnitBoxes();
    List<UnitBox> output_boxes = output.getUnitBoxes();
    for(int i = 0; i < input_boxes.size(); ++i){
      UnitBox input_box = input_boxes.get(i);
      UnitBox output_box = output_boxes.get(i);
      try {
        int j = findTarget(input_box.getUnit());
        output_box.setUnit(m_inputUnits.get(j));
      } catch(Exception ex){
        ex.printStackTrace();
        continue;
      }
    }
    return output;
  }

  private boolean unitEquals(Unit lhs, Unit rhs){
    if(lhs.equals(rhs))
      return true;
    if(lhs instanceof GotoStmt && rhs instanceof GotoStmt){
      GotoStmt lhs_goto = (GotoStmt) lhs;
      GotoStmt rhs_goto = (GotoStmt) rhs;
      if(lhs_goto.getTarget().equals(rhs_goto.getTarget()))
        return true;
    }
    return false;
  }

  int findTarget(Unit target){
    for(int i = 0; i < m_inputUnits.size(); ++i){
      Unit curr = m_inputUnits.get(i);
      if(unitEquals(target, curr))
        return i;
    }
    throw new RuntimeException("Cannot find target while assembling units: " + target.toString());
  }

  public void assemble(Body body){
    assignLabels();
    cloneUnits();
    //debugPrintOutput();
    copyTargets();
    checkTargetBoxes();
    copyLocals();
    writeToBody(body);
  }

  void checkTargetBoxes(){
    Set<String> key_set = m_labelToUnitBoxMap.keySet();
    for(String key : key_set){
      List<UnitBox> boxes = m_labelToUnitBoxMap.get(key);
      for(UnitBox box : boxes){
        if(box.getUnit() == null)
          throw new RuntimeException("box unit is null: "+key);
      }
    }
  }

  void cloneUnits(){
    for(Unit u : m_inputUnits){
      m_outputUnits.add((Unit) u.clone());
    }
  }

  void debugPrintOutput(){
    for(Unit u : m_outputUnits){
      System.out.println(u.toString());
    }
    System.out.println("End of debugPrintOutput");
  }

  private void writeToBody(Body body) {
    PatchingChain<Unit> units = body.getUnits();
    Chain<Local> locals = body.getLocals();
    units.clear();
    locals.clear();

    for(Unit u : m_outputUnits){
      units.add(u);
    }

    for(Local l : m_outputLocals){
      locals.add(l);
    }
  }

  @Override
  public String toString(){
    String ret = "";
    for(int i = 0; i < m_outputUnits.size(); ++i){
      if(i < m_labels.size()){
        List<String> labels = m_labels.get(i);
        for(String label : labels)
          ret += label+":\n";
      }
      ret += m_outputUnits.get(i).toString() + "\n";
    }
    return ret;
  }

  private void addLabelToUnitBox(String label, UnitBox unit_box){
    List<UnitBox> boxes;
    if(m_labelToUnitBoxMap.containsKey(label))
      boxes = m_labelToUnitBoxMap.get(label);
    else
      boxes = new ArrayList<UnitBox>();
    boxes.add(unit_box);
    m_labelToUnitBoxMap.put(label, boxes);
  }

  public void addIf(Value condition, String target_label) {
    UnitBox target = m_jimple.newStmtBox(null);
    addLabelToUnitBox(target_label, target);
    Unit u = m_jimple.newIfStmt(condition, target);
    add(u);
  }

  public void addGoto(String target_label){
    UnitBox target = m_jimple.newStmtBox(null);
    addLabelToUnitBox(target_label, target);
    Unit u = m_jimple.newGotoStmt(target);
    add(u);
  }

  public void addLabel(String label){
    while(m_inputUnits.size() >= m_labels.size())
      m_labels.add(new ArrayList<String>());

    m_labels.get(m_labels.size()-1).add(label);
  }

  public Unit getUnitByLabel(String label) {
    for(int i = 0; i < m_labels.size(); ++i){
      List<String> labelset = m_labels.get(i);
      if(labelset.contains(label))
        return m_inputUnits.get(i);
    }
    throw new RuntimeException("Cannot find unit");
  }

  private void assignLabels() {
    for(int i = 0; i < m_labels.size(); ++i){
      List<String> labelset = m_labels.get(i);
      if(labelset.size() == 0)
        continue;

      Unit target = m_inputUnits.get(i);
      for(String label : labelset){
        List<UnitBox> boxes = m_labelToUnitBoxMap.get(label);
        if(boxes == null){
          System.out.println("Cannot find boxes for label.  This could be caused by classes other than the BytecodeLanguage using the assembler and is not a fatal error.");
          continue;
        }
        for(UnitBox box : boxes){
          box.setUnit(target);
        }
      }
    }
  }

  Unit getLastUnitCreated() {
    return m_inputUnits.get(m_inputUnits.size()-1);
  }
}
