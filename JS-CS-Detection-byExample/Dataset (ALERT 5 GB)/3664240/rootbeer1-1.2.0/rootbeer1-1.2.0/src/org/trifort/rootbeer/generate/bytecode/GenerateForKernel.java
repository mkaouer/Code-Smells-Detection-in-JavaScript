/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.bytecode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.configuration.Configuration;
import org.trifort.rootbeer.configuration.RootbeerPaths;
import org.trifort.rootbeer.deadmethods.DeadMethods;
import org.trifort.rootbeer.generate.opencl.OpenCLScene;
import org.trifort.rootbeer.generate.opencl.tweaks.CompileResult;

import soot.*;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.StringConstant;
import soot.options.Options;
import soot.rbclassload.RootbeerClassLoader;

public class GenerateForKernel {
  private MethodCodeSegment m_codeSegment;
  private SootClass m_sootClass;
  private List<Local> m_firstIterationLocals;
  private Jimple m_jimple;
  private String m_runtimeBasicBlockClassName;
  private String m_serializerClassName;

  public GenerateForKernel(SootMethod method, String uuid){
    m_jimple = Jimple.v();
    m_firstIterationLocals = new ArrayList<Local>();
    m_sootClass = method.getDeclaringClass();
    m_codeSegment = new MethodCodeSegment(method);
  }

  public Type getType(){
    return m_sootClass.getType();
  }

  public void makeClass() throws Exception {
    m_serializerClassName = m_codeSegment.getSootClass().getName()+"Serializer";
    
    makeCpuBody();
    makeGpuBody();
    makeIsUsingGarbageCollectorBody();
    makeIsReadOnly();    
    makeExceptionNumbers();
                            
    SerializerAdder adder = new SerializerAdder();
    adder.add(m_codeSegment);
  }

  private void makeCpuBody() {
    m_codeSegment.makeCpuBody(m_sootClass);
  }  
  
  private void makeGetCodeMethodThatReturnsBytes(boolean m32, String filename) {
    BytecodeLanguage bcl = new BytecodeLanguage();
    bcl.openClass(m_sootClass);
    SootClass string = Scene.v().getSootClass("java.lang.String");
    bcl.startMethod("getCubin"  + (m32 ? "32" : "64"), string.getType());
    Local thisref = bcl.refThis();
    bcl.returnValue(StringConstant.v(filename));
    bcl.endMethod();
  }
  
  private void makeGetCubinSizeMethod(boolean m32, int length){
    BytecodeLanguage bcl = new BytecodeLanguage();
    bcl.openClass(m_sootClass);
    bcl.startMethod("getCubin"+(m32 ? "32" : "64")+"Size", IntType.v());
    Local thisref = bcl.refThis();
    bcl.returnValue(IntConstant.v(length));
    bcl.endMethod();
  }

  private void makeGetCubinErrorMethod(boolean m32, boolean error){
    BytecodeLanguage bcl = new BytecodeLanguage();
    bcl.openClass(m_sootClass);
    bcl.startMethod("getCubin"+(m32 ? "32" : "64")+"Error", BooleanType.v());
    Local thisref = bcl.refThis();
    int intError = 0;
    if(error == true){
      intError = 1;
    }
    bcl.returnValue(IntConstant.v(intError));
    bcl.endMethod();
  }
  
  private void makeGetCodeMethodThatReturnsString(String gpu_code, boolean unix){    
    //make the getCode method with the results of the opencl code generation
    String name = "getCode";
    if(unix){
      name += "Unix";
    } else {
      name += "Windows";
    }
    SootMethod getCode = new SootMethod(name, new ArrayList(), RefType.v("java.lang.String"), Modifier.PUBLIC);
    getCode.setDeclaringClass(m_sootClass);
    m_sootClass.addMethod(getCode);
    
    RootbeerClassLoader.v().addGeneratedMethod(getCode.getSignature());

    JimpleBody body = m_jimple.newBody(getCode);
    UnitAssembler assembler = new UnitAssembler();

    //create an instance of self
    Local thislocal = m_jimple.newLocal("this0", m_sootClass.getType());
    Unit thisid = m_jimple.newIdentityStmt(thislocal, m_jimple.newThisRef(m_sootClass.getType()));
    assembler.add(thisid);

    //java string constants encoded in a class file have a maximum size of 65535...
    //$r1 = new java.lang.StringBuilder;
    SootClass string_builder_soot_class = Scene.v().getSootClass("java.lang.StringBuilder");
    Local r1 = m_jimple.newLocal("r1", string_builder_soot_class.getType());
    Value r1_assign_rhs = m_jimple.newNewExpr(string_builder_soot_class.getType());
    Unit r1_assign = m_jimple.newAssignStmt(r1, r1_assign_rhs);
    assembler.add(r1_assign);

    //specialinvoke $r1.<java.lang.StringBuilder: void <init>()>();
    SootMethod string_builder_ctor = string_builder_soot_class.getMethod("void <init>()");
    Value r1_ctor = m_jimple.newSpecialInvokeExpr(r1, string_builder_ctor.makeRef(), new ArrayList());
    Unit r1_ctor_unit = m_jimple.newInvokeStmt(r1_ctor);
    assembler.add(r1_ctor_unit);
    
    //r2 = $r1;
    Local r2 = m_jimple.newLocal("r2", string_builder_soot_class.getType());
    Unit r2_assign_r1 = m_jimple.newAssignStmt(r2, r1);
    assembler.add(r2_assign_r1);
    
    SootClass string_class = Scene.v().getSootClass("java.lang.String");
    SootMethod string_builder_append = string_builder_soot_class.getMethod("java.lang.StringBuilder append(java.lang.String)");

    GpuCodeSplitter splitter = new GpuCodeSplitter();
    List<String> blocks = splitter.split(gpu_code);

    for(String block : blocks){
      Value curr_string_constant = StringConstant.v(block);
        
      //virtualinvoke r2.<java.lang.StringBuilder: java.lang.StringBuilder append(java.lang.String)>("gpu code");
      List args = new ArrayList();
      args.add(curr_string_constant);
      Value invoke_expr = m_jimple.newVirtualInvokeExpr(r2, string_builder_append.makeRef(), args);
      Unit invoke_stmt = m_jimple.newInvokeStmt(invoke_expr);
      assembler.add(invoke_stmt);
    }

    //$r5 = virtualinvoke r2.<java.lang.StringBuilder: java.lang.String toString()>();
    Local r5 = m_jimple.newLocal("r5", string_class.getType());
    SootMethod to_string = string_builder_soot_class.getMethod("java.lang.String toString()");
    Value r5_rhs = m_jimple.newVirtualInvokeExpr(r2, to_string.makeRef());
    Unit r5_assign = m_jimple.newAssignStmt(r5, r5_rhs);
    assembler.add(r5_assign);

    assembler.add(m_jimple.newReturnStmt(r5));

    assembler.assemble(body);
    getCode.setActiveBody(body);
  }

  private void makeGpuBody() throws Exception {
    OpenCLScene.v().addCodeSegment(m_codeSegment);
    if(Configuration.compilerInstance().getMode() == Configuration.MODE_GPU){
      CompileResult[] result = OpenCLScene.v().getCudaCode();
      for (CompileResult res : result) {
        String suffix = res.is32Bit() ? "-32" : "-64";
        if (res.getBinary() == null) {
          makeGetCodeMethodThatReturnsBytes(res.is32Bit(), cubinFilename(false, suffix) + ".error");
          makeGetCubinSizeMethod(res.is32Bit(), 0);
          makeGetCubinErrorMethod(res.is32Bit(), true);
        } else {
          byte[] bytes = res.getBinary();
          writeBytesToFile(bytes, cubinFilename(true, suffix));
          makeGetCodeMethodThatReturnsBytes(res.is32Bit(), cubinFilename(false, suffix));
          makeGetCubinSizeMethod(res.is32Bit(), bytes.length);
          makeGetCubinErrorMethod(res.is32Bit(), false);
        }
      }
      makeGetCodeMethodThatReturnsString("", true);
      makeGetCodeMethodThatReturnsString("", false);
    } else {
      String[] code = OpenCLScene.v().getOpenCLCode();
      //code[0] is unix
      //code[1] is windows
      
      PrintWriter writer = new PrintWriter(RootbeerPaths.v().getRootbeerHome()+"pre_dead_unix.c");
      writer.println(code[0]);
      writer.flush();
      writer.close();
      
      writer = new PrintWriter(RootbeerPaths.v().getRootbeerHome()+"pre_dead_windows.c");
      writer.println(code[1]);
      writer.flush();
      writer.close();
      
      System.out.println("removing dead methods...");
      DeadMethods dead_methods = new DeadMethods();
      dead_methods.parseString(code[0]);
      code[0] = dead_methods.getResult();
      dead_methods.parseString(code[1]);
      code[1] = dead_methods.getResult();
      
      //jpp can't handle declspec very well
      code[1] = code[1].replace("void entry(char * gc_info_space,", "__declspec(dllexport)\nvoid entry(char * gc_info_space,");
      
      makeGetCodeMethodThatReturnsString(code[0], true);
      makeGetCodeMethodThatReturnsString(code[1], false);
      makeGetCodeMethodThatReturnsBytes(true, "");
      makeGetCodeMethodThatReturnsBytes(false, "");
    }
  }
  
  private String cubinFilename(boolean use_class_folder, String suffix){
    String class_name = File.separator +
            m_serializerClassName.replace(".", File.separator) +
            suffix + ".cubin";
    if(use_class_folder)
      return RootbeerPaths.v().getOutputClassFolder() + class_name;
    else
      return class_name;
  }
  
  private void writeBytesToFile(byte[] bytes, String filename) {
    try {
      File file = new File(filename);
      File parent = file.getParentFile();
      parent.mkdirs();
      OutputStream os = new FileOutputStream(filename);
      os.write(bytes);
      os.flush();
      os.close();
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  public SootField getField(String name, Type type){
    return m_sootClass.getField(name, type);
  }

  public void addFirstIterationLocal(Local local) {
    m_firstIterationLocals.add(local);
  }

  private void makeIsUsingGarbageCollectorBody() {
    BytecodeLanguage bcl = new BytecodeLanguage();
    bcl.openClass(m_sootClass);
    bcl.startMethod("isUsingGarbageCollector", BooleanType.v());
    bcl.refThis();
    if(OpenCLScene.v().getUsingGarbageCollector())
      bcl.returnValue(IntConstant.v(1));
    else
      bcl.returnValue(IntConstant.v(0));
    bcl.endMethod();
  }

  public String getRuntimeBasicBlockName() {
    return m_runtimeBasicBlockClassName;
  }

  public String getSerializerName() {
    return m_serializerClassName;
  }

  private void makeIsReadOnly() {
    BytecodeLanguage bcl = new BytecodeLanguage();
    bcl.openClass(m_sootClass);
    bcl.startMethod("isReadOnly", BooleanType.v());
    bcl.refThis();
    if(OpenCLScene.v().getReadOnlyTypes().isRootReadOnly())
      bcl.returnValue(IntConstant.v(1));
    else
      bcl.returnValue(IntConstant.v(0));
    bcl.endMethod();
  }

  private void makeExceptionNumbers() {
    String prefix = Options.v().rbcl_remap_prefix();
    if(Options.v().rbcl_remap_all() == false){
      prefix = "";
    }
    makeExceptionMethod("getNullPointerNumber", prefix+"java.lang.NullPointerException");
    makeExceptionMethod("getOutOfMemoryNumber", prefix+"java.lang.OutOfMemoryError");
  }
  
  private void makeExceptionMethod(String method_name, String cls_name) {
    SootClass soot_class = Scene.v().getSootClass(cls_name);
    int number = RootbeerClassLoader.v().getClassNumber(soot_class);
    
    BytecodeLanguage bcl = new BytecodeLanguage();
    bcl.openClass(m_sootClass);
    bcl.startMethod(method_name, IntType.v());
    bcl.refThis();
    bcl.returnValue(IntConstant.v(number));
    bcl.endMethod();
  }

}
