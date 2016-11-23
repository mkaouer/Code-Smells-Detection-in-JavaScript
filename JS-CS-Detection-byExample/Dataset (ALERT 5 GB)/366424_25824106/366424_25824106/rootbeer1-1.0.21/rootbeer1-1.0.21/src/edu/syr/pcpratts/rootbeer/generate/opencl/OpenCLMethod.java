/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl;

import edu.syr.pcpratts.rootbeer.Constants;
import edu.syr.pcpratts.rootbeer.classloader.FastWholeProgram;
import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.generate.bytecode.StaticOffsets;
import edu.syr.pcpratts.rootbeer.generate.opencl.body.MethodJimpleValueSwitch;
import edu.syr.pcpratts.rootbeer.generate.opencl.body.OpenCLBody;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.Tweaks;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import soot.*;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;

/**
 * Represents an OpenCL function. 
 * @author pcpratts
 */
public class OpenCLMethod {
  private final SootMethod m_sootMethod;
  private SootClass m_sootClass;
  private Set<String> m_dontMangleMethods;
  
  public OpenCLMethod(SootMethod soot_method, SootClass soot_class){
    m_sootMethod = soot_method;
    m_sootClass = soot_class;
    createDontMangleMethods();
  }
  
  public String getReturnString(){
    StringBuilder ret = new StringBuilder();
    if(isConstructor()){
      ret.append("int");
    } else {
      OpenCLType return_type = new OpenCLType(m_sootMethod.getReturnType());
      ret.append(return_type.getRefString());
    }
    return ret.toString();
  }
  
  private String getRestOfArgumentListStringInternal(){
    StringBuilder ret = new StringBuilder();
    List args = m_sootMethod.getParameterTypes();
    
    if(args.size() != 0)
      ret.append(", ");
    
    for(int i = 0; i < args.size(); ++i){
      Type curr_arg = (Type) args.get(i);
      OpenCLType parameter_type = new OpenCLType(curr_arg);
      ret.append(parameter_type.getRefString());
      ret.append(" parameter" + Integer.toString(i));
      if(i < args.size()-1)
        ret.append(", ");
    }
    ret.append(", int * exception");
    ret.append(")");
    return ret.toString();
  }
  
  private String getArgumentListStringInternal(boolean override_ctor){
    StringBuilder ret = new StringBuilder();
    ret.append("(");

    String address_space_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
    if(isConstructor() == true){
      ret.append(address_space_qual+" char * gc_info");
    } else if((isConstructor() == false || override_ctor == true) && m_sootMethod.isStatic() == false){
      ret.append(address_space_qual+" char * gc_info, int thisref");
    } else {
      ret.append(address_space_qual+" char * gc_info");
    }
    
    ret.append(getRestOfArgumentListStringInternal());
    return ret.toString();
  }

  public String getArgumentListString(boolean ctor_body){
    if(ctor_body){
      String address_space_qual = Tweaks.v().getGlobalAddressSpaceQualifier();
      String ret = "("+address_space_qual+" char * gc_info, int thisref";
      ret += getRestOfArgumentListStringInternal();
      return ret;
    } else {
      return getArgumentListStringInternal(false);
    }
  }

  public String getArgumentListStringPolymorphic(){
    return getArgumentListStringInternal(true);
  }

  private String getMethodDecl(boolean ctor_body){
    StringBuilder ret = new StringBuilder();
    ret.append(Tweaks.v().getDeviceFunctionQualifier()+" ");
    if(ctor_body){
      ret.append("void");
    } else {
      ret.append(getReturnString());
    }
    ret.append(" ");
    ret.append(getPolymorphicNameInternal(ctor_body));
    ret.append(getArgumentListString(ctor_body));
    return ret.toString();
  }
  
  public String getMethodPrototype(){
    String ret = getMethodDecl(false)+";\n";
    if(isConstructor()){
      ret += getMethodDecl(true)+";\n";
    }
    return ret;
  }

  private boolean isLinux(){
    String s = File.separator;
    if(s.equals("/")){
      return true;
    }
    return false;
  }
  
  private String synchronizedEnter(){
    String ret = "";
    
    if(m_sootMethod.isStatic() == false){
      ret += "if(thisref == -1){\n";
      ret += "  *exception = "+Constants.NullPointerNumber+";\n";
      if(returnsAValue()){
        ret += "  return 0;\n";
      } else {
        ret += "  return;\n";
      }
      ret += "}\n";
    }
    ret += "int id = getThreadId();\n";
    StaticOffsets static_offsets = new StaticOffsets();
    int junk_index = static_offsets.getEndIndex() - 4;
    int mystery_index = junk_index - 4;
    if(m_sootMethod.isStatic()){
      int offset = static_offsets.getIndex(m_sootClass);
      ret += "char * mem = edu_syr_pcpratts_gc_deref(gc_info, 0);\n";
      ret += "char * trash = mem + "+junk_index+";\n";
      ret += "char * mystery = mem + "+mystery_index+";\n";
      ret += "mem += "+offset+";\n";
    } else {
      ret += "char * mem = edu_syr_pcpratts_gc_deref(gc_info, thisref);\n";
      ret += "char * trash = edu_syr_pcpratts_gc_deref(gc_info, 0) + "+junk_index+";\n";
      ret += "char * mystery = trash - 4;\n";
      ret += "mem += 12;\n";
    }
    ret += "int count = 0;\n";
    ret += "int old;\n";
    ret += "while(count < 100){\n";
    ret += "  old = atomicCAS((int *) mem, -1 , id);\n";
    ret += "  *((int *) trash) = old;\n";
    if(isLinux()){
      ret += "  if(old == -1 || old == id){\n";
    } else {
      ret += "  if(old != -1 && old != id){\n";
      ret += "    count++;\n";
      ret += "    if(count > 99 && *((int *) mystery) == 0){\n";
      ret += "      count = 0;\n";
      ret += "    }\n";
      ret += "  } else {\n"; 
    }
    return ret;
  }
  
  public String getMethodBody(){
    loadToBody();
    StringBuilder ret = new StringBuilder();
    if(shouldEmitBody()){
      ret.append(getMethodDecl(false)+"{\n");
      try {
        if(methodIsRuntimeBasicBlockRun() == false){
          if(isSynchronized()){
            ret.append(synchronizedEnter()); 
          }
          OpenCLBody ocl_body = new OpenCLBody(m_sootMethod, isConstructor());
          ret.append(ocl_body.getBody());
          if(isSynchronized()){
            if(isLinux()){
              ret.append("  } else {");
              ret.append("    count++;\n");
              ret.append("    if(count > 99 && *((int *) mystery) == 0){\n");
              ret.append("      count = 0;\n");
              ret.append("    }\n");
              ret.append("  }\n"); 
              ret.append("}\n"); 
            } else {
              ret.append("  }\n");
              ret.append("}\n");
            }
          }
          if(returnsAValue()){
            ret.append("return 0;");
          }
        }
      } catch(RuntimeException ex){
        System.out.println("error creating method body: "+m_sootMethod.getSignature());
        OpenCLMethod ocl_method = new OpenCLMethod(m_sootMethod, m_sootClass);
        if(ocl_method.returnsAValue())
          ret.append("return 0;\n");
        else
          ret.append("\n");
      }
      ret.append("}\n");
      if(isConstructor()){
        ret.append(getMethodDecl(true)+"{\n"); 
        OpenCLBody ocl_body = new OpenCLBody(m_sootMethod.retrieveActiveBody());
        ret.append(ocl_body.getBody());
        ret.append("}\n");
      }
    }
    return ret.toString();
  }
  
  public String getConstructorBodyInvokeString(SpecialInvokeExpr arg0){
    StringBuilder ret = new StringBuilder();

    ret.append(getPolymorphicNameInternal(true) +"(");
    List args = arg0.getArgs();
    List<String> args_list = new ArrayList<String>();
    args_list.add("gc_info");
    args_list.add("thisref");
    
    for(int i = 0; i < args_list.size() - 1; ++i){
      ret.append(args_list.get(i));
      ret.append(",\n ");
    }
    if(args_list.size() > 0){
      ret.append(args_list.get(args_list.size()-1));
      if(args.size() > 0)
        ret.append(",\n ");
    }
    
    MethodJimpleValueSwitch quick_value_switch = new MethodJimpleValueSwitch(ret);
    for(int i = 0; i < args.size(); ++i){
      Value arg = (Value) args.get(i);
      arg.apply(quick_value_switch);
      if(i < args.size() - 1)
        ret.append(",\n ");
    }
    ret.append(", exception");
    ret.append(")");
    
    return ret.toString();
  }

  public String getInstanceInvokeString(InstanceInvokeExpr arg0){
    Value base = arg0.getBase();
    Type base_type = base.getType();
    List<Type> hierarchy;
    if(base_type instanceof ArrayType){
      hierarchy = new ArrayList<Type>();
      SootClass obj = Scene.v().getSootClass("java.lang.Object");
      hierarchy.add(obj.getType());
    } else if (base_type instanceof RefType){
      RefType ref_type = (RefType) base_type;
      hierarchy = RootbeerScene.v().getDfsInfo().getHierarchy(ref_type.getSootClass());
    } else {
      throw new UnsupportedOperationException("how do we handle this case?");
    }
    
    IsPolyMorphic poly_checker = new IsPolyMorphic();    
    if(poly_checker.isPoly(m_sootMethod, hierarchy) == false || isConstructor() || arg0 instanceof SpecialInvokeExpr){
      return writeInstanceInvoke(arg0, "", m_sootClass.getType());
    } else if(hierarchy.size() == 0){
      System.out.println("size = 0");
      return null;
    } else {
      return writeInstanceInvoke(arg0, "invoke_", hierarchy.get(0));
    } 
  }

  public String getStaticInvokeString(StaticInvokeExpr expr){
    StringBuilder ret = new StringBuilder();

    ret.append(getPolymorphicName()+"(");
    List args = expr.getArgs();
    List<String> args_list = new ArrayList<String>();
    args_list.add("gc_info");

    for(int i = 0; i < args_list.size() - 1; ++i){
      ret.append(args_list.get(i));
      ret.append(", ");
    }
    if(args_list.size() > 0){
      ret.append(args_list.get(args_list.size()-1));
      if(args.size() > 0)
        ret.append(", ");
    }
    MethodJimpleValueSwitch quick_value_switch = new MethodJimpleValueSwitch(ret);
    for(int i = 0; i < args.size(); ++i){
      Value arg = (Value) args.get(i);
      arg.apply(quick_value_switch);
      if(i < args.size() - 1)
        ret.append(", ");
    }
    ret.append(", exception");
    ret.append(")");
    return ret.toString();
  }

  private String writeInstanceInvoke(InstanceInvokeExpr arg0, String method_prefix, Type type){
    if(type instanceof RefType == false){
      throw new RuntimeException("please report bug in OpenCLMethod.writeInstanceInvoke");
    }
    RefType ref_type = (RefType) type;
    OpenCLMethod corrected_this = new OpenCLMethod(m_sootMethod, ref_type.getSootClass());
    StringBuilder ret = new StringBuilder();
    Value base = arg0.getBase();
    if(base instanceof Local == false)
      throw new UnsupportedOperationException("How do we handle an invoke on a non loca?");
    Local local = (Local) base;
    if(isConstructor()){
      ret.append("edu_syr_pcpratts_gc_assign (gc_info, \n&"+local.getName()+", ");
    }

    String function_name = method_prefix+corrected_this.getPolymorphicName();
    ret.append(function_name+"(");
    List args = arg0.getArgs();
    List<String> args_list = new ArrayList<String>();
    args_list.add("gc_info");
    
    //write the thisref
    if(isConstructor() == false)
      args_list.add(local.getName());

    for(int i = 0; i < args_list.size() - 1; ++i){
      ret.append(args_list.get(i));
      ret.append(",\n ");
    }
    if(args_list.size() > 0){
      ret.append(args_list.get(args_list.size()-1));
      if(args.size() > 0)
        ret.append(",\n ");
    }
    
    MethodJimpleValueSwitch quick_value_switch = new MethodJimpleValueSwitch(ret);
    for(int i = 0; i < args.size(); ++i){
      Value arg = (Value) args.get(i);
      arg.apply(quick_value_switch);
      if(i < args.size() - 1)
        ret.append(",\n ");
    }
    ret.append(", exception");
    ret.append(")");
    
    if(isConstructor()){
      ret.append(")");
    }

    return ret.toString();
  }

  public boolean isConstructor(){
    String method_name = m_sootMethod.getName();
    if(method_name.equals("<init>"))
      return true;
    return false;
  }
  
  public String getPolymorphicName(){
    return getPolymorphicNameInternal(false);
  }
  
  private String getPolymorphicNameInternal(boolean ctor_body){
    String ret = getBaseMethodName();
    if(ctor_body){
      ret += "_body";  
    }
    if(m_dontMangleMethods.contains(ret) == false)
      ret += NameMangling.v().mangleArgs(m_sootMethod);
    return ret;
  }

  private String getBaseMethodName(){
    OpenCLClass ocl_class = new OpenCLClass(m_sootClass);

    String method_name = m_sootMethod.getName();
    //here I use a certain uuid for init so there is low chance of collisions
    method_name = method_name.replace("<init>", "init"+OpenCLScene.v().getUuid());

    String ret = ocl_class.getName()+"_"+method_name;
    return ret;
  }
  
  private boolean shouldEmitBody(){
    String ret = getBaseMethodName();
    if(m_dontMangleMethods.contains(ret))
      return false;
    return true;
  }
  
  @Override
  public String toString(){
    return getPolymorphicName();
  }

  private boolean methodIsRuntimeBasicBlockRun() {
    if(m_sootClass.getName().equals("edu.syr.pcpratts.javaautogpu.runtime.RuntimeBasicBlock") == false)
      return false;
    if(m_sootMethod.getName().equals("run") == false)
      return false;
    return true;
  }

  public boolean returnsAValue() {
    if(isConstructor())
      return true;
    Type t = m_sootMethod.getReturnType();
    if(t instanceof VoidType)
      return false;
    return true;
  }

  public boolean isSynchronized() {
    return m_sootMethod.isSynchronized();
  }
  
  private void createDontMangleMethods() {
    m_dontMangleMethods = new HashSet<String>();
    m_dontMangleMethods.add("java_lang_StrictMath_exp");
    m_dontMangleMethods.add("java_lang_StrictMath_log");
    m_dontMangleMethods.add("java_lang_StrictMath_log10");
    m_dontMangleMethods.add("java_lang_StrictMath_sqrt");
    m_dontMangleMethods.add("java_lang_StrictMath_cbrt");
    m_dontMangleMethods.add("java_lang_StrictMath_IEEEremainder");    
    m_dontMangleMethods.add("java_lang_StrictMath_ceil");
    m_dontMangleMethods.add("java_lang_StrictMath_floor");
    m_dontMangleMethods.add("java_lang_StrictMath_sin");
    m_dontMangleMethods.add("java_lang_StrictMath_cos");
    m_dontMangleMethods.add("java_lang_StrictMath_tan");
    m_dontMangleMethods.add("java_lang_StrictMath_asin");
    m_dontMangleMethods.add("java_lang_StrictMath_acos");
    m_dontMangleMethods.add("java_lang_StrictMath_atan");
    m_dontMangleMethods.add("java_lang_StrictMath_atan2");
    m_dontMangleMethods.add("java_lang_StrictMath_pow");
    m_dontMangleMethods.add("java_lang_StrictMath_sinh");
    m_dontMangleMethods.add("java_lang_StrictMath_cosh");
    m_dontMangleMethods.add("java_lang_StrictMath_tanh");
    m_dontMangleMethods.add("java_lang_Double_doubleToLongBits");
    m_dontMangleMethods.add("java_lang_Double_longBitsToDouble");
    m_dontMangleMethods.add("java_lang_System_arraycopy");
    m_dontMangleMethods.add("java_lang_Throwable_fillInStackTrace");
    m_dontMangleMethods.add("java_lang_Throwable_getStackTraceDepth");
    m_dontMangleMethods.add("java_lang_Throwable_getStackTraceElement");
    m_dontMangleMethods.add("java_lang_Object_clone");
    m_dontMangleMethods.add("java_lang_Object_hashCode");
    m_dontMangleMethods.add("java_lang_OutOfMemoryError_initab850b60f96d11de8a390800200c9a66");
    m_dontMangleMethods.add("edu_syr_pcpratts_rootbeer_runtime_RootbeerGpu_isOnGpu");
    m_dontMangleMethods.add("edu_syr_pcpratts_rootbeer_runtime_RootbeerGpu_getThreadId");
    m_dontMangleMethods.add("edu_syr_pcpratts_rootbeer_runtime_RootbeerGpu_getRef");
    m_dontMangleMethods.add("java_lang_System_nanoTime");
    m_dontMangleMethods.add("java_lang_Class_getName");
    m_dontMangleMethods.add("java_lang_Object_getClass");
    m_dontMangleMethods.add("java_lang_StringValue_from");
  }

  public String getSignature() {
    return m_sootMethod.getSignature();
  }

  private void loadToBody() {
    FastWholeProgram.v().loadToBodyLater(m_sootMethod.getSignature());
  }
}