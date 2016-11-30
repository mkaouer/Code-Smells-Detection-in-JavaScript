/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.bytecode;

import edu.syr.pcpratts.rootbeer.Configuration;
import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.generate.codesegment.CodeSegment;
import edu.syr.pcpratts.rootbeer.generate.codesegment.LoopCodeSegment;
import edu.syr.pcpratts.rootbeer.generate.codesegment.MethodCodeSegment;
import edu.syr.pcpratts.rootbeer.generate.opencl.OpenCLScene;
import edu.syr.pcpratts.rootbeer.generate.misc.BasicBlock;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.CompileResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import soot.*;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.StringConstant;

public class GenerateRuntimeBasicBlock {
  private CodeSegment codeSegment;
  private SootClass mSootClass;
  private List<Local> mFirstIterationLocals;
  private Jimple jimple;
  private String runtimeBasicBlockClassName;
  private String gcObjectVisitorClassName;

  public GenerateRuntimeBasicBlock(BasicBlock block, String uuid) {
    codeSegment = new LoopCodeSegment(block);
    jimple = Jimple.v();
    mFirstIterationLocals = new ArrayList<Local>();
  }

  public GenerateRuntimeBasicBlock(SootMethod method, String uuid){
    jimple = Jimple.v();
    mFirstIterationLocals = new ArrayList<Local>();
    mSootClass = method.getDeclaringClass();
    codeSegment = new MethodCodeSegment(method);
  }

  public Type getType(){
    return mSootClass.getType();
  }

  public void makeClass() throws Exception {
    gcObjectVisitorClassName = codeSegment.getSootClass().getName()+"GcObjectVisitor";
    
    makeCpuBody();
    makeGpuBody();
    makeIsUsingGarbageCollectorBody();
    makeIsReadOnly();    
                            
    GcHeapReadWriteAdder adder = new GcHeapReadWriteAdder();
    adder.add(codeSegment);
  }

  private void makeCpuBody() {
    codeSegment.makeCpuBodyForRuntimeBasicBlock(mSootClass);
  }  
  
  private void makeGetCodeMethodThatReturnsBytes(String filename) {
    BytecodeLanguage bcl = new BytecodeLanguage();
    bcl.openClass(mSootClass);
    SootClass string = Scene.v().getSootClass("java.lang.String");
    bcl.startMethod("getCubin", string.getType());
    Local thisref = bcl.refThis();
    bcl.returnValue(StringConstant.v(filename));
    bcl.endMethod();
  }
  
  private void makeGetCodeMethodThatReturnsString(String gpu_code){    
    //make the getCode method with the results of the opencl code generation
    SootMethod getCode = new SootMethod("getCode", new ArrayList(), RefType.v("java.lang.String"), Modifier.PUBLIC);
    getCode.setDeclaringClass(mSootClass);
    mSootClass.addMethod(getCode);

    JimpleBody body = jimple.newBody(getCode);
    UnitAssembler assembler = new UnitAssembler();

    //create an instance of self
    Local thislocal = jimple.newLocal("this0", mSootClass.getType());
    Unit thisid = jimple.newIdentityStmt(thislocal, jimple.newThisRef(mSootClass.getType()));
    assembler.add(thisid);

    //java string constants encoded in a class file have a maximum size of 65535...
    //$r1 = new java.lang.StringBuilder;
    SootClass string_builder_soot_class = Scene.v().getSootClass("java.lang.StringBuilder");
    Local r1 = jimple.newLocal("r1", string_builder_soot_class.getType());
    Value r1_assign_rhs = jimple.newNewExpr(string_builder_soot_class.getType());
    Unit r1_assign = jimple.newAssignStmt(r1, r1_assign_rhs);
    assembler.add(r1_assign);

    //specialinvoke $r1.<java.lang.StringBuilder: void <init>()>();
    SootMethod string_builder_ctor = string_builder_soot_class.getMethod("<init>", new ArrayList(), VoidType.v());
    Value r1_ctor = jimple.newSpecialInvokeExpr(r1, string_builder_ctor.makeRef(), new ArrayList());
    Unit r1_ctor_unit = jimple.newInvokeStmt(r1_ctor);
    assembler.add(r1_ctor_unit);
    
    //r2 = $r1;
    Local r2 = jimple.newLocal("r2", string_builder_soot_class.getType());
    Unit r2_assign_r1 = jimple.newAssignStmt(r2, r1);
    assembler.add(r2_assign_r1);
    
    SootClass string_class = Scene.v().getSootClass("java.lang.String");
    List parameter_types = new ArrayList();
    parameter_types.add(string_class.getType());
    SootMethod string_builder_append = string_builder_soot_class.getMethod("append", parameter_types, string_builder_soot_class.getType());

    GpuCodeSplitter splitter = new GpuCodeSplitter();
    List<String> blocks = splitter.split(gpu_code);

    for(String block : blocks){
      Value curr_string_constant = StringConstant.v(block);
        
      //virtualinvoke r2.<java.lang.StringBuilder: java.lang.StringBuilder append(java.lang.String)>("gpu code");
      List args = new ArrayList();
      args.add(curr_string_constant);
      Value invoke_expr = jimple.newVirtualInvokeExpr(r2, string_builder_append.makeRef(), args);
      Unit invoke_stmt = jimple.newInvokeStmt(invoke_expr);
      assembler.add(invoke_stmt);
    }

    //$r5 = virtualinvoke r2.<java.lang.StringBuilder: java.lang.String toString()>();
    Local r5 = jimple.newLocal("r5", string_class.getType());
    SootMethod to_string = string_builder_soot_class.getMethod("java.lang.String toString()");
    Value r5_rhs = jimple.newVirtualInvokeExpr(r2, to_string.makeRef());
    Unit r5_assign = jimple.newAssignStmt(r5, r5_rhs);
    assembler.add(r5_assign);

    assembler.add(jimple.newReturnStmt(r5));

    assembler.assemble(body);
    getCode.setActiveBody(body);
  }

  private void makeGpuBody() throws Exception {
    OpenCLScene.v().addCodeSegment(codeSegment);
    if(Configuration.compilerInstance().getMode() == Configuration.MODE_GPU){
      CompileResult result = OpenCLScene.v().getCudaCode();
      if(result.getBinary() == null){
        makeGetCodeMethodThatReturnsBytes(cubinFilename(false)+".error");
        makeGetCodeMethodThatReturnsString("");
      } else {
        List<byte[]> bytes = result.getBinary();
        writeBytesToFile(bytes, cubinFilename(true));
        makeGetCodeMethodThatReturnsBytes(cubinFilename(false));
        makeGetCodeMethodThatReturnsString("");
      }
    } else {
      String code = OpenCLScene.v().getOpenCLCode();
      makeGetCodeMethodThatReturnsString(code);
      makeGetCodeMethodThatReturnsBytes("");
    }
  }
  
  private String cubinFilename(boolean use_class_folder){
    String class_name = File.separator + gcObjectVisitorClassName.replace(".", File.separator) + ".cubin";
    if(use_class_folder)
      return edu.syr.pcpratts.rootbeer.Constants.OUTPUT_CLASS_FOLDER + class_name;
    else
      return class_name;
  }
  
  private void writeBytesToFile(List<byte[]> bytes, String filename) {
    try {
      File file = new File(filename);
      File parent = file.getParentFile();
      parent.mkdirs();
      OutputStream os = new FileOutputStream(filename);
      for(byte[] buffer : bytes){
        os.write(buffer);
      }
      os.flush();
      os.close();
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  public SootField getField(String name, Type type){
    return mSootClass.getField(name, type);
  }

  public void addFirstIterationLocal(Local local) {
    mFirstIterationLocals.add(local);
  }

  private void makeIsUsingGarbageCollectorBody() {
    BytecodeLanguage bcl = new BytecodeLanguage();
    bcl.openClass(mSootClass);
    bcl.startMethod("isUsingGarbageCollector", BooleanType.v());
    bcl.refThis();
    if(OpenCLScene.v().getUsingGarbageCollector())
      bcl.returnValue(IntConstant.v(1));
    else
      bcl.returnValue(IntConstant.v(0));
    bcl.endMethod();
  }

  public String getRuntimeBasicBlockName() {
    return runtimeBasicBlockClassName;
  }

  public String getGcObjectVisitorName() {
    return gcObjectVisitorClassName;
  }

  private void makeIsReadOnly() {
    BytecodeLanguage bcl = new BytecodeLanguage();
    bcl.openClass(mSootClass);
    bcl.startMethod("isReadOnly", BooleanType.v());
    bcl.refThis();
    if(OpenCLScene.v().getReadOnlyTypes().isRootReadOnly())
      bcl.returnValue(IntConstant.v(1));
    else
      bcl.returnValue(IntConstant.v(0));
    bcl.endMethod();
  }

}
