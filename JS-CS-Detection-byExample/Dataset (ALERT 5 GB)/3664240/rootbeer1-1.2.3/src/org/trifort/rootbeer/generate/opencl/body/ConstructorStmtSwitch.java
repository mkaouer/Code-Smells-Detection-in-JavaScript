/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl.body;

import org.trifort.rootbeer.generate.opencl.OpenCLMethod;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.SpecialInvokeExpr;

public class ConstructorStmtSwitch extends MethodStmtSwitch {

  private SootClass m_SootClass;
  private boolean m_EmitRetVoid;
  public ConstructorStmtSwitch(OpenCLBody parent, SootMethod soot_method,
    boolean emit_ret_void){
  
    super(parent, soot_method);
    
    m_EmitRetVoid = emit_ret_void;    
    m_SootClass = soot_method.getDeclaringClass();
    m_SootClass = Scene.v().getSootClass(m_SootClass.getName());
  }

  @Override
  protected boolean methodReturnsAValue(){
    if(m_EmitRetVoid){
      return false;
    } else {
      return super.methodReturnsAValue();
    }
  }
  
  @Override
  public void caseReturnStmt(ReturnStmt arg0) {
    //intentionally left blank
  }

  @Override
  public void caseReturnVoidStmt(ReturnVoidStmt arg0) {
    if(m_EmitRetVoid)
      m_output.append("return;\n");
  }
  
  @Override
  public void caseInvokeStmt(InvokeStmt arg0) {
    InvokeExpr expr = arg0.getInvokeExpr();
    if(expr instanceof SpecialInvokeExpr == false){
      super.caseInvokeStmt(arg0);
      return;
    }
    SpecialInvokeExpr sexpr = (SpecialInvokeExpr) expr;
    if(needsReWriting(sexpr) == false){
      super.caseInvokeStmt(arg0);
      return;
    }
    SootMethod soot_method = sexpr.getMethod();
    SootClass soot_class = soot_method.getDeclaringClass();
    if(soot_class.getName().equals("java.lang.Object"))
      return;
    OpenCLMethod ocl_method = new OpenCLMethod(soot_method, soot_class);
    m_output.append(ocl_method.getConstructorBodyInvokeString(sexpr));
    m_output.append(";\n");
  }

  private boolean needsReWriting(SpecialInvokeExpr sexpr) {
    if(m_SootClass.hasSuperclass() == false)
      return false;
    SootMethod method = sexpr.getMethod();
    SootClass soot_class = method.getDeclaringClass();
    String parent_name = m_SootClass.getSuperclass().getName();
    if(soot_class.getName().equals(parent_name) == false)
      return false;
    if(method.getName().equals("<init>") == false)
      return false;
    return true;
  }
}
