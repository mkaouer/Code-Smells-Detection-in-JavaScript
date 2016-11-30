/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.bytecode;

import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import java.util.ArrayList;
import java.util.List;
import soot.Local;
import soot.Modifier;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.jimple.Expr;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;

public class BclQueueManager {
  private Jimple jimple;
  private Local mQueueManagerLocal;
  private SootClass mQueueManagerClass;
  private String mUuid;
  private UnitAssembler mAssembler;
  private BytecodeLanguage mBcl;
  
  public BclQueueManager(){
    commonCtor();
  }

  private void commonCtor(){
    jimple = Jimple.v();
    mQueueManagerClass = Scene.v().getSootClass("edu.syr.pcpratts.javaautogpu.runtime.QueueManager");
  }

  //Facade functions to support new style of code
  public BclQueueManager(UnitAssembler assembler){ commonCtor(); mAssembler = assembler; }
  public void callV(String uuid){ callV(mAssembler, uuid); }
  public void enqueueRuntimeBasicBlock(Local runtime_basic_block){ enqueueRuntimeBasicBlock(mAssembler, runtime_basic_block); }
  public Unit runBasicBlocks() { return runBasicBlocks(mAssembler); }
  public Local createIterator(){ return createIterator(mAssembler); }

  public void callV(UnitAssembler assembler, String uuid) {
    mUuid = uuid;
    String reg1 = RegisterNamer.v().getName();
    Type type1 = RefType.v("edu.syr.pcpratts.javaautogpu.runtime.QueueManager");
    mQueueManagerLocal = jimple.newLocal(reg1, type1);

    List parameter_types = new ArrayList();
    parameter_types.add(RefType.v("java.lang.String"));
    SootMethod QueueManagerV =  new SootMethod("v", parameter_types, type1, Modifier.STATIC);
    QueueManagerV.setDeclaringClass(mQueueManagerClass);
    Value rvalue = jimple.newStaticInvokeExpr(QueueManagerV.makeRef(), StringConstant.v(uuid));
    Unit u1 = jimple.newAssignStmt(mQueueManagerLocal, rvalue);
    assembler.add(u1);
  }

  public void enqueueRuntimeBasicBlock(UnitAssembler assembler, Local runtime_basic_block){
    List parameter_types = new ArrayList();
    parameter_types.add(RefType.v("edu.syr.pcpratts.javaautogpu.runtime.RuntimeBasicBlock"));
    SootMethod enqueue = new SootMethod("enqueue", parameter_types, VoidType.v());
    enqueue.setDeclaringClass(mQueueManagerClass);
    Expr expr = jimple.newVirtualInvokeExpr(mQueueManagerLocal, enqueue.makeRef(), runtime_basic_block);
    Unit unit = jimple.newInvokeStmt(expr);
    assembler.add(unit);
  }

  public Unit runBasicBlocks(UnitAssembler assembler) {
    List parameter_types = new ArrayList();
    SootMethod run = new SootMethod("run", parameter_types, VoidType.v());
    run.setDeclaringClass(mQueueManagerClass);
    Expr expr = jimple.newVirtualInvokeExpr(mQueueManagerLocal, run.makeRef(), new ArrayList());
    Unit unit = jimple.newInvokeStmt(expr);
    assembler.add(unit);
    return unit;
  }

  public Local createIterator(UnitAssembler assembler){
    mBcl = new BytecodeLanguage();
    mBcl.continueMethod(assembler);

    SootClass peek_iterator_soot_class = Scene.v().getSootClass("edu.syr.pcpratts.util.PeekIterator");
    mBcl.pushMethod(mQueueManagerLocal, "iterator", peek_iterator_soot_class.getType());
    Local ret = mBcl.invokeMethodRet(mQueueManagerLocal);
    return ret;
  }

}
