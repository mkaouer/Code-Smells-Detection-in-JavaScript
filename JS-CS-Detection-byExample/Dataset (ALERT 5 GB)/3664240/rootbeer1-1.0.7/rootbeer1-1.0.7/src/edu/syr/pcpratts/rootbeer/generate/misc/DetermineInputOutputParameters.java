/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.misc;

import java.util.ArrayList;
import java.util.List;
import soot.Local;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;

public class DetermineInputOutputParameters {

  List<Local> mInputArguments;
  List<Local> mOutputArguments;
  List<Unit> mInnerBody;
  List<Value> mValuesNotSet;
  List<Value> mValuesSet;
  private BasicBlock mBlock;

  public void run(BasicBlock block) throws Exception{
    mBlock = block;
    mInnerBody = block.getUnits();
    determineValuesSet();
    determineValuesNotSet();
    createArguments();
  }

  public List<Local> getInputArguments(){ return mInputArguments; }
  public List<Local> getOutputArguments(){ return mOutputArguments; }

  public List<Local> getAllArguments(){
    List<Local> ret = new ArrayList<Local>();
    ret.addAll(mInputArguments);
    ret.addAll(mOutputArguments);
    return ret;
  }

  private void createArguments() {
    mInputArguments = new ArrayList<Local>();
    mOutputArguments = new ArrayList<Local>();
    //create input parameters
    for(int i = 0; i < mValuesNotSet.size(); ++i){
      Value curr = mValuesNotSet.get(i);
      createArgument(curr, mInputArguments);
    }
    //create output parameters
    List<Value> output_args = mBlock.getOutputValues();
    for(Value value : output_args){
      createArgument(value, mOutputArguments);
    }
  }

  private void createArgument(Value curr, List<Local> list_to_add_to){
    if(curr instanceof Local){
      Local local = (Local) curr;
      list_to_add_to.add(local);
    }
  }

  private boolean isOutputArg(Value value){
    for(Local arg : mOutputArguments){
      if(arg.equals(value))
        return true;
    }
    return false;
  }

  private void determineValuesNotSet() {
    mValuesNotSet = new ArrayList<Value>();
    for(int i = 0; i < mInnerBody.size(); ++i){
      Unit curr = mInnerBody.get(i);
      List<ValueBox> use_boxes = curr.getUseBoxes();
      for(ValueBox box : use_boxes){
        Value value = box.getValue();
        if(value instanceof Local == false)
          continue;
        if(isValueNotSet(value) && mValuesNotSet.contains(value) == false)
          mValuesNotSet.add(value);
      }
    }
  }

  boolean isValueNotSet(Value v){
    for(int i = 0; i < mInnerBody.size(); ++i){
      Unit curr = mInnerBody.get(i);
      List<ValueBox> def_boxes = curr.getDefBoxes();
      for(ValueBox box : def_boxes){
        if(v.equals(box.getValue()))
          return false;
      }
    }
    return true;
  }

  private void determineValuesSet() {
    mValuesSet = new ArrayList<Value>();
    for(int i = 0; i < mInnerBody.size(); ++i){
      Unit curr = mInnerBody.get(i);
      if(curr instanceof AssignStmt == false)
        continue;
      AssignStmt assign = (AssignStmt) curr;
      mValuesSet.add(assign.getLeftOp());
    }
  }

  public List getParameterTypes() {
    List<Type> ret = new ArrayList<Type>();
    List<Local> args = getInputArguments();
    for(Local arg : args){
      ret.add(arg.getType());
    }
    return ret;
  }

  public List<Value> getInputValues() {
    return mValuesNotSet;
  }

}
