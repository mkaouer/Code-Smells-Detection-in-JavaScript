/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl;

import edu.syr.pcpratts.rootbeer.Constants;
import edu.syr.pcpratts.rootbeer.classloader.FastWholeProgram;
import edu.syr.pcpratts.rootbeer.generate.bytecode.TypeHistory;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.OpenCLField;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.FieldCloner;
import edu.syr.pcpratts.rootbeer.generate.bytecode.ReadOnlyTypes;
import edu.syr.pcpratts.rootbeer.generate.bytecode.SortedTypeHistory;
import edu.syr.pcpratts.rootbeer.generate.codesegment.CodeSegment;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.CompositeField;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.FieldCodeGeneration;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.FieldTypeSwitch;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.OffsetCalculator;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.CudaTweaks;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.Tweaks;
import edu.syr.pcpratts.rootbeer.util.ResourceReader;
import edu.syr.pcpratts.rootbeer.util.SignatureUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.codec.digest.DigestUtils;
import soot.*;
import soot.jimple.NewMultiArrayExpr;

public class OpenCLScene {
  private static OpenCLScene m_instance;
  private static int m_curentIdent;
  private ClassHierarchy m_classHierarchy;
  private Map<String, OpenCLClass> m_classes;
  private Map<String, String> m_oclToSoot;
  private Set<OpenCLArrayType> m_arrayTypes;
  private CodeSegment m_codeSegment;
  private MethodHierarchies m_methodHierarchies;
  private boolean m_usesGarbageCollector;
  private SootClass m_rootSootClass;
  private int m_endOfStatics;
  private List<Type> m_types;
  private ReadOnlyTypes m_readOnlyTypes;
  private TypeHistory m_typeHistory;
  private Map<ArrayType, List<Integer>> m_multiArrayDimensions;
  
  static {
    m_curentIdent = 0;
  }

  private void resetInstance(){
    m_codeSegment = null;
    m_classHierarchy = new ClassHierarchy();
    m_classes = new LinkedHashMap<String, OpenCLClass>();
    m_oclToSoot = new HashMap<String, String>();
    m_arrayTypes = new LinkedHashSet<OpenCLArrayType>();
    m_methodHierarchies = new MethodHierarchies();
    m_types = new ArrayList<Type>();
    m_multiArrayDimensions = new HashMap<ArrayType, List<Integer>>();
  }

  private OpenCLScene(){
    resetInstance();
  }

  public static OpenCLScene v(){
    if(m_instance == null)
      m_instance = new OpenCLScene();
    return m_instance;
  }

  public static void releaseV(){
    m_instance = null;
    m_curentIdent++;
  }
  
  public String getIdent(){
    return "" + m_curentIdent;
  }

  public String getUuid(){
    return "ab850b60f96d11de8a390800200c9a66";
  }

  public List<SootClass> getClassHierarchy(SootClass soot_class){
    return m_classHierarchy.getClassHierarchy(soot_class);
  }

  public int getClassTypeNonRef(Type type){
    if(type.equals(ByteType.v())){
      return 0;
    } else if(type.equals(CharType.v())){
      return 1;
    } else if(type.equals(ShortType.v())){
      return 2;
    } else if(type.equals(IntType.v())){
      return 3;
    } else if(type.equals(LongType.v())){
      return 4;
    } else if(type.equals(FloatType.v())){
      return 5;
    } else if(type.equals(DoubleType.v())){
      return 6;
    } else if(type.equals(BooleanType.v())){
      return 7;
    } else {
      throw new UnsupportedOperationException("Unknown type");
    }
  }

  public int getClassType(Type type){
    String type_string = type.toString();
    if(type instanceof ArrayType){
      ArrayType array_type = (ArrayType) type;
      return 10 + m_classHierarchy.getClassTypeArray(array_type);
    } else if(type_string.equals("java.lang.String")){
      return 8;
    } else if(type_string.equals("java.lang.OutOfMemoryError")){
      return 9;
    } else if(type_string.equals("java.lang.NullPointerException")){
      return Constants.NullPointerNumber; 
    } else if(type instanceof RefType){
      RefType ref_type = (RefType) type;
      return 10 + m_classHierarchy.getClassType(ref_type.getSootClass());
    } else {
      return getClassTypeNonRef(type);
    }
  }

  public int getEndOfStatics(){
    return m_endOfStatics;
  }

  public int getClassType(SootClass soot_class){
    return getClassType(soot_class.getType());
  }

  public void addMethod(SootMethod soot_method){
    SootClass soot_class = soot_method.getDeclaringClass();

    OpenCLClass ocl_class = getOpenCLClass(soot_class);
    ocl_class.addMethod(new OpenCLMethod(soot_method, soot_class));

    //add the method 
    m_methodHierarchies.addMethod(soot_method);
    m_classHierarchy.getClassHierarchy(soot_class);
  }

  public void addArrayType(OpenCLArrayType array_type){
    if(m_arrayTypes.contains(array_type))
      return;
    m_arrayTypes.add(array_type);
    
    ArrayType soot_type = array_type.getArrayType();
    for(int i = 1; i < soot_type.numDimensions; ++i){
      ArrayType new_type = ArrayType.v(soot_type.baseType, i);
      OpenCLArrayType new_ocl_type = new OpenCLArrayType(new_type);
      if(m_arrayTypes.contains(new_ocl_type))
        continue;
      m_arrayTypes.add(new_ocl_type);                
    }
  }

  public OpenCLClass getOpenCLClass(SootClass soot_class){
    //add the class to the scene if it is not there allready
    OpenCLClass ocl_class = new OpenCLClass(soot_class);
    if(m_classes.containsKey(ocl_class.getName()))
      ocl_class = m_classes.get(ocl_class.getName());
    m_classes.put(ocl_class.getName(), ocl_class);
    m_oclToSoot.put(ocl_class.getName(), soot_class.getName());
    return ocl_class;
  }

  public void addField(SootField soot_field){
    SootClass soot_class = soot_field.getDeclaringClass();

    List<SootClass> hierarchy = m_classHierarchy.getClassHierarchy(soot_class);
    for(SootClass curr : hierarchy){
      OpenCLClass ocl_class = getOpenCLClass(curr);
      ocl_class.findAllUsedMethodsAndFields();
      addType(curr.getType());
    }
    
    OpenCLClass ocl_class = getOpenCLClass(soot_class);
    ocl_class.addField(new OpenCLField(soot_field, soot_class));
  }

  private String getRuntimeBasicBlockClassName(){
    SootClass soot_class = m_rootSootClass;
    OpenCLClass ocl_class = getOpenCLClass(soot_class);
    return ocl_class.getName();
  }

  private String readCudaCodeFromFile(){
    try {
      BufferedReader reader = new BufferedReader(new FileReader("generated.cu"));
      String ret = "";
      while(true){
        String temp = reader.readLine();
        if(temp == null)
          return ret;
        ret += temp+"\n";
      }
    } catch(Exception ex){
      throw new RuntimeException();
    }
  }

  public void setUsingGarbageCollector(){
    m_usesGarbageCollector = true;
  }

  public boolean getUsingGarbageCollector(){
    return m_usesGarbageCollector;
  }
  
  private String makeSourceCode() throws Exception {
    m_usesGarbageCollector = false;
    findAllUsedClassesMethodsFieldsAndArrayTypes();
    StringBuilder ret = new StringBuilder();

    ret.append(headerString());
    ret.append(garbageCollectorString());
    ret.append(methodPrototypesString());
    ret.append(methodBodiesString());
    String prev_hash = "";
    String curr_hash = DigestUtils.md5Hex(ret.toString());
    
    while(prev_hash.equals(curr_hash) == false){
      ret = new StringBuilder();
      
      ret.append(headerString());
      ret.append(garbageCollectorString());
      ret.append(methodPrototypesString());
      ret.append(methodBodiesString());
      
      prev_hash = curr_hash;
      curr_hash = DigestUtils.md5Hex(ret.toString());
    }

    String cuda_code;
    //for debugging you can read the cuda code from a generated.cu
    if(true){
      cuda_code = ret.toString();
    } else {
      cuda_code = readCudaCodeFromFile();
    }
    String mangle = NameMangling.v().mangle(VoidType.v());
    String replacement = getRuntimeBasicBlockClassName()+"_gpuMethod"+mangle;
    //class names can have $ in them, make them regex safe
    replacement = replacement.replace("$", "\\$");
    cuda_code = cuda_code.replaceAll("%%invoke_run%%", replacement);
    
    //print out code for debugging
    PrintWriter writer = new PrintWriter(new FileWriter("generated.cu"));
    writer.println(cuda_code.toString());
    writer.flush();
    writer.close();
    
    return cuda_code;
  }

  public String getOpenCLCode() throws Exception {
    String source_code = makeSourceCode();
    return source_code;
  }

  public List<byte[]> getCudaCode() throws Exception {
    String source_code = makeSourceCode();
    return new CudaTweaks().compileProgram(source_code);
  }

  private String headerString() throws IOException {
    String path = Tweaks.v().getHeaderPath();
    if(path == null)
      return "";
    return ResourceReader.getResource(path);
  }
  
  private String garbageCollectorString() throws IOException {
    String path = Tweaks.v().getGarbageCollectorPath();
    String ret = ResourceReader.getResource(path);
    ret = ret.replace("$$__device__$$", Tweaks.v().getDeviceFunctionQualifier());
    ret = ret.replace("$$__global$$", Tweaks.v().getGlobalAddressSpaceQualifier());
    return ret;
  }

  private String methodPrototypesString(){
    //using a set so duplicates get filtered out.
    Set<String> protos = new HashSet<String>();
    StringBuilder ret = new StringBuilder();
    List<OpenCLMethod> methods = m_methodHierarchies.getMethods();
    for(OpenCLMethod method : methods){ 
      protos.add(method.getMethodPrototype());
    }    
    List<OpenCLPolymorphicMethod> poly_methods = m_methodHierarchies.getPolyMorphicMethods();
    for(OpenCLPolymorphicMethod poly_method : poly_methods){
      protos.add(poly_method.getMethodPrototype());
    }
    FieldCodeGeneration gen = new FieldCodeGeneration();
    protos.add(gen.prototypes(m_classes, m_codeSegment.getReadWriteFieldInspector()));
    for(OpenCLArrayType array_type : m_arrayTypes){
      protos.add(array_type.getPrototypes());
    }
    Iterator<String> iter = protos.iterator();
    while(iter.hasNext()){
      ret.append(iter.next());
    }
    return ret.toString();
  }

  private String methodBodiesString() throws IOException{
    StringBuilder ret = new StringBuilder();
    if(m_usesGarbageCollector)
      ret.append("#define USING_GARBAGE_COLLECTOR\n");
    //a set is used so duplicates get filtered out
    Set<String> bodies = new HashSet<String>();
    
    ArrayCopyGenerate arr_generate = new ArrayCopyGenerate();
    bodies.add(arr_generate.get(m_arrayTypes));
    
    ObjectCloneGenerate clone_generate = new ObjectCloneGenerate();
    bodies.add(clone_generate.get(m_arrayTypes, m_classes, m_oclToSoot));
    
    List<OpenCLMethod> methods = m_methodHierarchies.getMethods();
    for(OpenCLMethod method : methods){ 
      bodies.add(method.getMethodBody());
    }
    List<OpenCLPolymorphicMethod> poly_methods = m_methodHierarchies.getPolyMorphicMethods();
    for(OpenCLPolymorphicMethod poly_method : poly_methods){
      bodies.add(poly_method.getMethodBody());
    }
    FieldTypeSwitch type_switch = new FieldTypeSwitch();
    FieldCodeGeneration gen = new FieldCodeGeneration();
    String field_bodies = gen.bodies(m_classes, 
      m_codeSegment.getReadWriteFieldInspector(), type_switch);
    bodies.add(field_bodies);
    for(OpenCLArrayType array_type : m_arrayTypes){
      bodies.add(array_type.getBodies());
    }
    Iterator<String> iter = bodies.iterator();
    ret.append(type_switch.getFunctions());
    while(iter.hasNext()){
      ret.append(iter.next());
    }
    String kernel_path = Tweaks.v().getKernelPath();
    ret.append(ResourceReader.getResource(kernel_path));
    return ret.toString();
  }
  
  public OffsetCalculator getOffsetCalculator(SootClass soot_class){
    FieldCloner cloner = new FieldCloner();
    cloner.setup(m_classes);
    List<CompositeField> composites = cloner.getCompositeFields();
    for(CompositeField composite : composites){
      List<SootClass> classes = composite.getClasses();
      if(classes.contains(soot_class))
        return new OffsetCalculator(composite);
    }
    throw new RuntimeException("Cannot find composite field for soot_class");
  }

  private void findAllUsedClassesMethodsFieldsAndArrayTypes() {
    FindMethodsFieldsAndArrayTypes.reset();
    
    addBuiltinRequirements();
    
    SignatureUtil util = new SignatureUtil();
    
    Set<String> dfs_methods = FastWholeProgram.v().getDfsMethods(m_codeSegment.getRootMethod());
    Iterator<String> iter = dfs_methods.iterator();
    while(iter.hasNext()){
      String sig = iter.next();
      SootClass soot_class = Scene.v().getSootClass(util.classFromMethodSig(sig));
      addMethod(soot_class.getMethod(util.methodSubSigFromMethodSig(sig)));
    }

    m_codeSegment.findAllUsedMethodsAndFields();

    int prev_size = 0;
    List<OpenCLMethod> all_methods = m_methodHierarchies.getMethods();
    while(prev_size != all_methods.size()){
      prev_size = all_methods.size();
      all_methods = m_methodHierarchies.getMethods();
      for(OpenCLMethod method : all_methods){
        method.findAllUsedMethodsAndFields();
      }
    }

    m_codeSegment.findAllUsedArrayTypes();
    
    all_methods = m_methodHierarchies.getMethods();
    for(OpenCLMethod method : all_methods){
      method.findAllUsedArrayTypes();
    }
    
    m_typeHistory = new TypeHistory(m_codeSegment.getRootSootClass());
    List<Type> scene_types = OpenCLScene.v().getTypes();
    for(Type scene_type : scene_types){
      m_typeHistory.addType(scene_type);
    }
  }

  public void addCodeSegment(CodeSegment codeSegment){
    resetInstance();
    this.m_codeSegment = codeSegment;
    m_rootSootClass = codeSegment.getRootSootClass();    
    m_readOnlyTypes = new ReadOnlyTypes(codeSegment.getRootMethod());
    getOpenCLClass(m_rootSootClass);
  }

  public boolean isArrayLocalWrittenTo(Local local){
    return m_codeSegment.getReadWriteFieldInspector().localRepresentingArrayIsWrittenOnGpu(local);
  }

  private void addBuiltinRequirements() {
    FastWholeProgram.v().loadToBodyLater("<java.lang.String: void <init>(char[])>");
    SootClass string_class = Scene.v().getSootClass("java.lang.String");
    SootMethod ctor_method = string_class.getMethod("void <init>(char[])");
    addMethod(ctor_method);
    
    SootClass rootbeer_gpu_class = Scene.v().getSootClass("edu.syr.pcpratts.rootbeer.runtime.RootbeerGpu");
    SootMethod getThreadId = rootbeer_gpu_class.getMethodByName("getThreadId");
    addMethod(getThreadId);
    
    ArrayType char_array = ArrayType.v(CharType.v(), 1);
    OpenCLArrayType ocl_array = new OpenCLArrayType(char_array);
    m_arrayTypes.add(ocl_array);
    
    SootClass throwable_class = Scene.v().getSootClass("java.lang.Throwable");
    SootMethod getStackTrace = throwable_class.getMethod("java.lang.StackTraceElement[] getStackTrace()");
    addMethod(getStackTrace);
        
    SootClass stack_trace_elem = Scene.v().getSootClass("java.lang.StackTraceElement");
    SootMethod stack_ctor = stack_trace_elem.getMethod("void <init>(java.lang.String,java.lang.String,java.lang.String,int)");
    addMethod(stack_ctor);
    
    SootClass out_of_mem = Scene.v().getSootClass("java.lang.OutOfMemoryError");
    SootMethod out_ctor = out_of_mem.getMethod("void <init>()");
    addMethod(out_ctor);
    addType(out_of_mem.getType());
    addType("java.lang.VirtualMachineError");
    addType("java.lang.Error");
  }

  private void addType(String cls){
    SootClass soot_class = Scene.v().getSootClass(cls);
    addType(soot_class.getType());
  }
  
  public void addType(Type type) {
    if(m_types.contains(type) == false){
      m_types.add(type);
    }
  }
  
  public List<Type> getTypes(){
    return m_types;
  }

  public ReadOnlyTypes getReadOnlyTypes(){
    return m_readOnlyTypes;
  }

  public boolean isRootClass(SootClass soot_class) {
    return soot_class.getName().equals(m_rootSootClass.getName());
  }
  
  public List<Type> getOrderedHistory() {        
    List<Type> ordered_history = m_typeHistory.getHistory();    
    SortedTypeHistory sorter = new SortedTypeHistory();
    List<Type> all_possible_types = sorter.sort(ordered_history);    
    return all_possible_types;
  }  
  
  public List<RefType> getRefTypeOrderedHistory() {        
    List<Type> all_possible_types = getOrderedHistory();
    List<RefType> ret = new ArrayList<RefType>();
    for(Type type : all_possible_types){
      if(type instanceof RefType)
        ret.add((RefType) type);
    }
    return ret;
  }

  public TypeHistory getTypeHistory() {
    return m_typeHistory;
  }

  public Map<String, OpenCLClass> getClassMap(){
    return m_classes;
  }

  public void addNewMultiArray(NewMultiArrayExpr expr) {
    ArrayType type = expr.getBaseType();
    List<Integer> dimensions;
    if(m_multiArrayDimensions.containsKey(type)){
      dimensions = m_multiArrayDimensions.get(type);
    } else {
      dimensions = new ArrayList<Integer>();
      m_multiArrayDimensions.put(type, dimensions);
    }
    int dim = expr.getSizeCount();
    if(dimensions.contains(dim))
      return;
    dimensions.add(dim);
  }
  
  public List<Integer> getMultiArrayDimensions(ArrayType type){
    return m_multiArrayDimensions.get(type);
  }
}
