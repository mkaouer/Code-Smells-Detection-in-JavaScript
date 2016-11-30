/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.codesegment;

import edu.syr.pcpratts.rootbeer.generate.misc.BasicBlock;
import edu.syr.pcpratts.rootbeer.generate.bytecode.BytecodeLanguage;
import edu.syr.pcpratts.rootbeer.generate.bytecode.FieldReadWriteInspector;
import java.util.HashMap;
import java.util.List;
import soot.Local;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.jimple.AssignStmt;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;

public class LoopCodeSegment implements CodeSegment {
  private BasicBlock block;

  public LoopCodeSegment(BasicBlock block){
    this.block = block;
  }

  public List<Local> getInputArguments() {
    return block.getInputArguments();
  }

  public List<Local> getOutputArguments() {
    return block.getOutputArguments();
  }

  public FieldReadWriteInspector getReadWriteFieldInspector() {
    return block.getReadWriteFieldInspector();
  }

  public SootClass getSootClass() {
    return block.getSootClass();
  }

  public List<Value> getInputValues() {
    return block.getInputValues();
  }

  public List<Type> getParameterTypes() {
    return block.getParameterTypes();
  }

  public void makeCpuBodyForRuntimeBasicBlock(SootClass mClass) {
    BytecodeLanguage bcl = new BytecodeLanguage();
    bcl.openClass(mClass);
    bcl.startMethod("run", VoidType.v());

    //create an instance of self
    Local thislocal = bcl.refThis();

    //assign from shared state
    HashMap<String, Local> locals_made = new HashMap<String, Local>();
    //assign to locals
    List<Value> values_not_set = block.getInputValues();
    for(Value v : values_not_set){
      if(v instanceof Local == false)
        continue;
      Local local = (Local) v.clone();
      locals_made.put(local.toString(), local);

      bcl.refInstanceFieldToInput(thislocal, local.getName(), local);
    }

    //copy the body of the block
    List<Unit> whole_body = block.getLoop().getWholeBodyOfMethod();
    Unit unit_after_loop = block.getLoop().getUnitAfterWholeBodyOfMethod();
    Unit new_unit_after_loop = Jimple.v().newReturnVoidStmt();
    for(int i = block.getStart(); i <= block.getStop(); ++i){
      //replace references to locals to references to fields
      Unit src = whole_body.get(i);

      //if the statement assigns to a local set, ignore the statement
      if(src instanceof AssignStmt){
        AssignStmt assign = (AssignStmt) src;
        Value v = assign.getLeftOp();
        if(values_not_set.contains(v))
          continue;
      }

      //if the target of an if points to right after the loop, then make it point to the new return void
      if(src instanceof IfStmt){
        IfStmt if_stmt = (IfStmt) src;
        UnitBox target_box = if_stmt.getTargetBox();
        if(target_box.getUnit().equals(unit_after_loop))
          target_box.setUnit(new_unit_after_loop);
      }

      List boxes = src.getUseAndDefBoxes();
      for(int k = 0; k < boxes.size(); ++k){
        ValueBox curr_box = (ValueBox) boxes.get(k);
        Value curr_value = curr_box.getValue();
        if(curr_value instanceof Local == false)
          continue;
        Local curr_local = (Local) curr_value;
        if(locals_made.containsKey(curr_local.toString()))
          curr_box.setValue(locals_made.get(curr_local.toString()));
      }
      bcl.getAssembler().add(src);
    }

    //write output args to fields
    List<Value> output_args = block.getOutputValues();
    for(Value value : output_args){
      if(value instanceof Local == false)
        continue;
      Local local = (Local) value;
      SootField field = mClass.getField(local.getName(), local.getType());
      Value lhs_assign = Jimple.v().newInstanceFieldRef(thislocal, field.makeRef());
      Unit assign = Jimple.v().newAssignStmt(lhs_assign, local);
      bcl.getAssembler().add(assign);
    }

    bcl.getAssembler().add(new_unit_after_loop);
    bcl.endMethod();
  }

  public SootMethod getRootMethod() {
    return block.getSootClass().getMethodByName("run");
  }

  public SootClass getRootSootClass() {
    return block.getSootClass();
  }
}
