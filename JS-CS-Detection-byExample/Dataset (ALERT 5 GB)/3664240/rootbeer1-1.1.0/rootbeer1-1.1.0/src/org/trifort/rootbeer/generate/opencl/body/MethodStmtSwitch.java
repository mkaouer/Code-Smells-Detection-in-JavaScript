/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl.body;

import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.configuration.Configuration;
import org.trifort.rootbeer.generate.opencl.OpenCLClass;
import org.trifort.rootbeer.generate.opencl.OpenCLMethod;
import org.trifort.rootbeer.generate.opencl.OpenCLScene;
import org.trifort.rootbeer.generate.opencl.OpenCLType;
import org.trifort.rootbeer.util.Stack;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BreakpointStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.FieldRef;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.NopStmt;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.StmtSwitch;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;
import soot.options.Options;
import soot.rbclassload.NumberedType;
import soot.rbclassload.RootbeerClassLoader;

public class MethodStmtSwitch implements StmtSwitch {
  protected StringBuilder m_output;
  protected MethodJimpleValueSwitch m_valueSwitch;
  protected final OpenCLBody m_parent;
  protected SootMethod m_sootMethod;
  private List<TrapItem> m_trapItems;
  private int m_variableNumber;
  private Stack<String> m_oldValueFromMonitorStack;

  public MethodStmtSwitch(OpenCLBody parent, SootMethod soot_method){
    m_sootMethod = soot_method;
    m_output = new StringBuilder();
    m_valueSwitch = new MethodJimpleValueSwitch(m_output);
    m_parent = parent;
    m_variableNumber = 1;
    m_oldValueFromMonitorStack = new Stack<String>();
  }
  
  private String getVarName(){
    String ret = "monitor_var"+m_variableNumber;
    m_variableNumber++;
    return ret;
  }
  
  public void popMonitor(){
    m_oldValueFromMonitorStack.pop();
  }

  protected boolean methodReturnsAValue(){
    OpenCLMethod ocl_method = new OpenCLMethod(m_sootMethod, m_sootMethod.getDeclaringClass());
    return ocl_method.returnsAValue();
  }

  public void caseBreakpointStmt(BreakpointStmt arg0) {
    //intentionally left blank
  }

  public void caseInvokeStmt(InvokeStmt arg0) {
    arg0.getInvokeExpr().apply(m_valueSwitch);
    SootMethod method = arg0.getInvokeExpr().getMethod();
    m_output.append(";\n");
    if(m_valueSwitch.getCheckException()){
      checkException();
    }
  }

  private void caseDefinitionStmt(DefinitionStmt arg0){
    Value left_op = arg0.getLeftOp();
    Value right_op = arg0.getRightOp();
    Type left_op_type = left_op.getType();
    OpenCLType ocl_left_op_type = new OpenCLType(left_op_type);

    //if the left op is a field ref or array ref we use the setter
    if(left_op instanceof FieldRef || left_op instanceof ArrayRef){
      m_valueSwitch.setLhs();
      left_op.apply(m_valueSwitch);
      m_output.append(", ");
      m_valueSwitch.setRhs();
      right_op.apply(m_valueSwitch);
      m_output.append(", exception);\n");
      if(m_valueSwitch.getCheckException()){
        checkException();
      }
      m_valueSwitch.clearLhsRhs();
    }
    //if the right op is a field ref or array ref we use the getter
    else if(right_op instanceof FieldRef || right_op instanceof ArrayRef){
      m_valueSwitch.setLhs();
      left_op.apply(m_valueSwitch);
      m_output.append(" = ");
      m_valueSwitch.setRhs();
      right_op.apply(m_valueSwitch);
      m_output.append(";\n");
      if(m_valueSwitch.getCheckException()){
        checkException();
      }
      m_valueSwitch.clearLhsRhs();
    }
    //otherwise just use normal assignment
    else {
      m_valueSwitch.setLhs();
      arg0.getLeftOp().apply(m_valueSwitch);
      m_output.append(" = ");
      m_valueSwitch.setRhs();
      arg0.getRightOp().apply(m_valueSwitch);
      m_output.append(";\n");
      if(m_valueSwitch.getCheckException()){
        checkException();
      }
      m_valueSwitch.clearLhsRhs();   
    }
  }

  public void caseAssignStmt(AssignStmt arg0) {
    caseDefinitionStmt(arg0);
  }

  public void caseIdentityStmt(IdentityStmt arg0) {
    caseDefinitionStmt(arg0);
  }

  public void caseEnterMonitorStmt(EnterMonitorStmt arg0) {
    String id = getVarName();
    String mem = getVarName();
    String synch = getVarName();
    String count = getVarName();
    String old = getVarName();
    m_oldValueFromMonitorStack.push(old);
    
    OpenCLClass ocl_class = OpenCLScene.v().getOpenCLClass(m_sootMethod.getDeclaringClass());
    OpenCLMethod ocl_method = ocl_class.getMethod(m_sootMethod.getSignature());
    
    m_output.append("int "+id+" = getThreadId();\n");
    m_output.append("char * "+mem+" = org_trifort_gc_deref(gc_info, ");
    arg0.getOp().apply(m_valueSwitch);
    m_output.append(");\n");
    m_output.append("char * "+synch+" = org_trifort_gc_deref(gc_info, ");
    arg0.getOp().apply(m_valueSwitch);
    m_output.append(");\n");
    m_output.append(mem+" += 16;\n");
    m_output.append("int "+count+" = 0;\n");
    m_output.append("int "+old+";\n");
    m_output.append("while("+count+" < 100){\n");
    m_output.append("  "+old+" = atomicCAS((int *) "+mem+", -1, "+id+");\n");
    m_output.append("  if("+old+" != -1 && "+old+" != "+id+"){\n");
    m_output.append("    "+count+"++;\n");
    m_output.append("    if("+count+" >= 99){\n");
    m_output.append("      "+count+" = 0;\n");
    m_output.append("    }\n");
    m_output.append("  } else {\n");
    
    //the first write gets messed up in synch test cases
    m_output.append("  * ( ( int * ) & "+synch+" [ 20 ] ) = 20 ;\n");
  }

  public void caseExitMonitorStmt(ExitMonitorStmt arg0) {
    m_output.append("org_trifort_exitMonitorRef(gc_info, ");
    arg0.getOp().apply(m_valueSwitch);
    m_output.append(", "+m_oldValueFromMonitorStack.top()+");\n");
  }

  public void caseGotoStmt(GotoStmt arg0) {
    Unit target = arg0.getTarget();
    int label_num = m_parent.labelNum(target);
    m_output.append("goto label" + Integer.toString(label_num) + ";\n");
  }

  public void caseIfStmt(IfStmt arg0) {
    m_output.append("if (");
    arg0.getCondition().apply(m_valueSwitch);
    m_output.append(" ) goto label");
    Unit target = arg0.getTarget();
    int label_num = m_parent.labelNum(target);
    m_output.append(Integer.toString(label_num)+";\n");
  }

  public void caseLookupSwitchStmt(LookupSwitchStmt arg0) {
    
    m_output.append("switch(");
    arg0.getKey().apply(m_valueSwitch);
    m_output.append("){\n");
    List<Value> values = arg0.getLookupValues();
    List<Unit> units = arg0.getTargets();
    Unit default_target = arg0.getDefaultTarget();
    
    int label_num;
    
    for(int i = 0; i < values.size(); ++i){
      m_output.append("case ");
      values.get(i).apply(m_valueSwitch);
      label_num = m_parent.labelNum(units.get(i));
      m_output.append(": goto label"+Integer.toString(label_num) + ";\n");
    }
    label_num = m_parent.labelNum(default_target);
    m_output.append("default: goto label"+Integer.toString(label_num) + ";\n");
    m_output.append("}\n"); 
  }

  public void caseNopStmt(NopStmt arg0) {
    //intentionally left blank
  }

  public void caseRetStmt(RetStmt arg0) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void caseReturnStmt(ReturnStmt arg0) {
    if(m_sootMethod.isSynchronized()){
      m_output.append("org_trifort_exitMonitorMem(gc_info, mem, old);\n");
    }
    m_output.append("return ");
    arg0.getOp().apply(m_valueSwitch);
    m_output.append(";\n");
  }
  
  public void caseReturnVoidStmt(ReturnVoidStmt arg0) {
    if(m_sootMethod.isSynchronized()){ 
      m_output.append("org_trifort_exitMonitorMem(gc_info, mem, old);\n");
    }
    m_output.append("return;\n");
  }

  public void caseTableSwitchStmt(TableSwitchStmt arg0) {
    
    m_output.append("switch(");
    arg0.getKey().apply(m_valueSwitch);
    m_output.append("){\n");
    List<Value> values = new ArrayList<Value>();
    for(int i = arg0.getLowIndex(); i < arg0.getHighIndex(); ++i){
      values.add(IntConstant.v(i));  
    }
    
    List<Unit> units = arg0.getTargets();
    Unit default_target = arg0.getDefaultTarget();
    
    int label_num;
    
    for(int i = 0; i < values.size(); ++i){
      m_output.append("case ");
      values.get(i).apply(m_valueSwitch);
      label_num = m_parent.labelNum(units.get(i));
      m_output.append(": goto label"+Integer.toString(label_num) + ";\n");
    }
    m_output.append("}\n"); 
    
  }

  public void caseThrowStmt(ThrowStmt arg0) {
    m_output.append(" *exception = ");
    Value op = arg0.getOp();
    op.apply(m_valueSwitch);
    m_output.append(";\n");
    if(m_sootMethod.isSynchronized()){
      m_output.append("org_trifort_exitMonitorMem(gc_info, mem, old);\n");
    }
    m_output.append("return");
    if(methodReturnsAValue())
      m_output.append(" 0;\n");
    else 
      m_output.append(";\n");
  }

  public void defaultCase(Object arg0) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void append(String string) {
    m_output.append(string);
  }

  @Override
  public String toString(){
    return m_output.toString();
  }

  void setTrapItems(List<TrapItem> trap_items) {
    m_trapItems = trap_items;
  }

  void reset() {
    m_valueSwitch.reset();
  }
  
  boolean hasCaughtExceptionRef(){
    return m_valueSwitch.hasCaughtExceptionRef();
  }

  public String getThisRef() {
    return m_valueSwitch.getThisRef();
  }

  private void checkException() {    
    //if exceptions are turned off, do not check them
    if(Configuration.compilerInstance().getExceptions() == false){
      return;
    }
    String prefix = Options.v().rbcl_remap_prefix();
    if(Options.v().rbcl_remap_all() == false){
      prefix = "";
    }
    SootClass oom_cls = Scene.v().getSootClass(prefix+"java.lang.OutOfMemoryError");
    SootClass null_cls = Scene.v().getSootClass(prefix+"java.lang.NullPointerException");
    int oom_num = RootbeerClassLoader.v().getClassNumber(oom_cls);
    int null_num = RootbeerClassLoader.v().getClassNumber(null_cls);
    m_output.append("if(*exception != 0) { \n");
    if(m_trapItems != null){    
      m_output.append("  GC_OBJ_TYPE_TYPE ex_type;\n");
      //if exception is negative, then we didn't allocate memory for it.
      m_output.append("  if(*exception == "+oom_num+" || *exception == "+null_num+"){\n");
      m_output.append("    ex_type = *exception;\n");
      m_output.append("  } else {\n");
      m_output.append("    char * ex_deref = org_trifort_gc_deref(gc_info, *exception);\n");
      m_output.append("    ex_type = org_trifort_gc_get_type(ex_deref);\n");
      m_output.append("  }\n");
      m_output.append("if(0){}\n");
      for(TrapItem item : m_trapItems){
        m_output.append("else if(");
        List<NumberedType> types = RootbeerClassLoader.v().getDfsInfo().getNumberedHierarchyUp(item.getException());
        for(int i = 0; i < types.size(); ++i){
          m_output.append("ex_type == "+types.get(i).getNumber());
          if(i < types.size() - 1){
            m_output.append(" || ");
          }
        }
        m_output.append("){\n");
        m_output.append("goto trap"+item.getTrapNum()+";\n");
        m_output.append("}\n");
      }
    }
    //mOutput.append("org_trifort_fillInStackTrace(gc_info, *exception, \""+class_name+"\", \""+method_name+"\");\n");
    if(m_sootMethod.isSynchronized()){
      m_output.append("org_trifort_exitMonitorMem(gc_info, mem, old);\n");
    }
    m_output.append("return ");
    if(methodReturnsAValue())
      m_output.append("0");
    m_output.append("; }\n");
  }
}
