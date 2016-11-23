/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl;

import soot.jimple.NewExpr;
import soot.rbclassload.MethodSignatureUtil;
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

import org.trifort.rootbeer.configuration.Configuration;
import org.trifort.rootbeer.configuration.RootbeerPaths;
import org.trifort.rootbeer.entry.ForcedFields;
import org.trifort.rootbeer.entry.CompilerSetup;
import org.trifort.rootbeer.generate.bytecode.MethodCodeSegment;
import org.trifort.rootbeer.generate.bytecode.ReadOnlyTypes;
import org.trifort.rootbeer.generate.opencl.fields.CompositeField;
import org.trifort.rootbeer.generate.opencl.fields.CompositeFieldFactory;
import org.trifort.rootbeer.generate.opencl.fields.FieldCodeGeneration;
import org.trifort.rootbeer.generate.opencl.fields.FieldTypeSwitch;
import org.trifort.rootbeer.generate.opencl.fields.OffsetCalculator;
import org.trifort.rootbeer.generate.opencl.fields.OpenCLField;
import org.trifort.rootbeer.generate.opencl.tweaks.CompileResult;
import org.trifort.rootbeer.generate.opencl.tweaks.CudaTweaks;
import org.trifort.rootbeer.generate.opencl.tweaks.Tweaks;
import org.trifort.rootbeer.util.ReadFile;
import org.trifort.rootbeer.util.ResourceReader;

import soot.*;
import soot.rbclassload.FieldSignatureUtil;
import soot.rbclassload.NumberedType;
import soot.rbclassload.RootbeerClassLoader;

public class OpenCLScene {
  private static OpenCLScene m_instance;
  private static int m_curentIdent;
  private Map<String, OpenCLClass> m_classes;
  private Set<OpenCLArrayType> m_arrayTypes;
  private MethodHierarchies m_methodHierarchies;
  private boolean m_usesGarbageCollector;
  private SootClass m_rootSootClass;
  private int m_endOfStatics;
  private ReadOnlyTypes m_readOnlyTypes;
  private Set<OpenCLInstanceof> m_instanceOfs;
  private List<CompositeField> m_compositeFields;
  private List<SootMethod> m_methods;
  private ClassConstantNumbers m_constantNumbers;
  private FieldCodeGeneration m_fieldCodeGeneration;
  
  static {
    m_curentIdent = 0;
  }

  public OpenCLScene(){
  }
  
  public void init(){
    m_classes = new LinkedHashMap<String, OpenCLClass>();
    m_arrayTypes = new LinkedHashSet<OpenCLArrayType>();
    m_methodHierarchies = new MethodHierarchies();
    m_instanceOfs = new HashSet<OpenCLInstanceof>();
    m_methods = new ArrayList<SootMethod>();
    m_constantNumbers = new ClassConstantNumbers();
    m_fieldCodeGeneration = new FieldCodeGeneration();
    loadTypes(); 
  }

  public static OpenCLScene v(){
    return m_instance;
  }
  
  public static void setInstance(OpenCLScene scene){
    m_instance = scene;
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

  public int getEndOfStatics(){
    return m_endOfStatics;
  }

  public int getClassType(SootClass soot_class){
    return RootbeerClassLoader.v().getClassNumber(soot_class);
  }
  
  public void addMethod(SootMethod soot_method){
    SootClass soot_class = soot_method.getDeclaringClass();

    OpenCLClass ocl_class = getOpenCLClass(soot_class);
    ocl_class.addMethod(new OpenCLMethod(soot_method, soot_class));

    //add the method 
    m_methodHierarchies.addMethod(soot_method);
    m_methods.add(soot_method);
  }
  
  public List<SootMethod> getMethods(){
    return m_methods;
  }

  public void addArrayType(OpenCLArrayType array_type){
    if(m_arrayTypes.contains(array_type))
      return;
    m_arrayTypes.add(array_type);
  }  
  
  public void addInstanceof(Type type){
    OpenCLInstanceof to_add = new OpenCLInstanceof(type);
    if(m_instanceOfs.contains(to_add) == false){
      m_instanceOfs.add(to_add);
    }
  }

  public OpenCLClass getOpenCLClass(SootClass soot_class){    
    String class_name = soot_class.getName();
    if(m_classes.containsKey(class_name)){
      return m_classes.get(class_name);
    } else {
      OpenCLClass ocl_class = new OpenCLClass(soot_class);
      m_classes.put(class_name, ocl_class);
      return ocl_class;
    }
  }

  public void addField(SootField soot_field){
    SootClass soot_class = soot_field.getDeclaringClass();
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
  
  private void writeTypesToFile(List<NumberedType> types){
    try {
      PrintWriter writer = new PrintWriter(RootbeerPaths.v().getTypeFile());
      for(NumberedType type : types){
        writer.println(type.getNumber()+" "+type.getType().toString());
      }
      writer.flush();
      writer.close();
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  public int getOutOfMemoryNumber(){
    SootClass soot_class = Scene.v().getSootClass("java.lang.OutOfMemoryError");
    int ret = RootbeerClassLoader.v().getClassNumber(soot_class); 
    return ret;
  }
  
  private void loadTypes(){
    Set<String> methods = RootbeerClassLoader.v().getDfsInfo().getMethods();  
    MethodSignatureUtil util = new MethodSignatureUtil();
    for(String method_sig : methods){
      util.parse(method_sig);
      SootMethod method = util.getSootMethod();
      addMethod(method);
    }
    CompilerSetup compiler_setup = new CompilerSetup();
    for(String extra_method : compiler_setup.getExtraMethods()){
      util.parse(extra_method);
      addMethod(util.getSootMethod());
    }
    
    Set<SootField> fields = RootbeerClassLoader.v().getDfsInfo().getFields();
    for(SootField field : fields){
      addField(field);
    }

    FieldSignatureUtil field_util = new FieldSignatureUtil();
    ForcedFields forced_fields = new ForcedFields();
    for(String field_sig : forced_fields.get()){
      field_util.parse(field_sig);
      addField(field_util.getSootField());
    }
    
    Set<ArrayType> array_types = RootbeerClassLoader.v().getDfsInfo().getArrayTypes();
    for(ArrayType array_type : array_types){
      OpenCLArrayType ocl_array_type = new OpenCLArrayType(array_type);
      addArrayType(ocl_array_type);
    }
    for(ArrayType array_type : compiler_setup.getExtraArrayTypes()){
      OpenCLArrayType ocl_array_type = new OpenCLArrayType(array_type);
      addArrayType(ocl_array_type);
    }
    
    Set<Type> instanceofs = RootbeerClassLoader.v().getDfsInfo().getInstanceOfs();
    for(Type type : instanceofs){
      addInstanceof(type);
    }
    
    buildCompositeFields();  
  }
  
  private String[] makeSourceCode() throws Exception {
    if(Configuration.compilerInstance().isManualCuda()){
      String filename = Configuration.compilerInstance().getManualCudaFilename();
      String cuda_code = readCode(filename);
          
      String[] ret = new String[2];
      ret[0] = cuda_code;
      ret[1] = cuda_code;
      return ret;
    }
    
    m_usesGarbageCollector = false;
    
    List<NumberedType> types = RootbeerClassLoader.v().getDfsInfo().getNumberedTypes();
    writeTypesToFile(types);
        
    StringBuilder unix_code = new StringBuilder();
    StringBuilder windows_code = new StringBuilder();
    
    String method_protos = methodPrototypesString();
    String gc_string = garbageCollectorString();
    String bodies_string = methodBodiesString();
    
    unix_code.append(headerString(true));
    unix_code.append(method_protos);
    unix_code.append(gc_string);
    unix_code.append(bodies_string);
    unix_code.append(kernelString(true));

    windows_code.append(headerString(false));
    windows_code.append(method_protos);
    windows_code.append(gc_string);
    windows_code.append(bodies_string);
    windows_code.append(kernelString(false));
    
    String cuda_unix = setupEntryPoint(unix_code);
    String cuda_windows = setupEntryPoint(windows_code);
    
    //print out code for debugging
    PrintWriter writer = new PrintWriter(new FileWriter(RootbeerPaths.v().getRootbeerHome()+"generated_unix.cu"));
    writer.println(cuda_unix);
    writer.flush();
    writer.close();
    
    //print out code for debugging
    writer = new PrintWriter(new FileWriter(RootbeerPaths.v().getRootbeerHome()+"generated_windows.cu"));
    writer.println(cuda_windows);
    writer.flush();
    writer.close();
    
    NameMangling.v().writeTypesToFile();
        
    String[] ret = new String[2];
    ret[0] = cuda_unix;
    ret[1] = cuda_windows;
    return ret;
  }
  
  private String readCode(String filename){
    ReadFile reader = new ReadFile(filename);
    try {
      return reader.read();
    } catch(Exception ex){
      ex.printStackTrace(System.out);
      throw new RuntimeException(ex);
    }
  }

  private String setupEntryPoint(StringBuilder builder){
    String cuda_code = builder.toString();
    String mangle = NameMangling.v().mangle(VoidType.v());
    String replacement = getRuntimeBasicBlockClassName()+"_gpuMethod"+mangle;
    //class names can have $ in them, make them regex safe
    replacement = replacement.replace("$", "\\$");
    cuda_code = cuda_code.replaceAll("%%invoke_run%%", replacement);  
    
    int string_builder_number = RootbeerClassLoader.v().getClassNumber("java.lang.StringBuilder");
    String sbn_str = "" + string_builder_number;
    cuda_code = cuda_code.replaceAll("%%java_lang_StringBuilder_TypeNumber%%", sbn_str);
    
    int null_pointer_number = RootbeerClassLoader.v().getClassNumber("java.lang.NullPointerException");
    String np_str = "" + null_pointer_number;
    cuda_code = cuda_code.replaceAll("%%java_lang_NullPointerException_TypeNumber%%", np_str);

    int out_of_memory_number = RootbeerClassLoader.v().getClassNumber("java.lang.OutOfMemoryError");
    String out_of_memory_str = "" + out_of_memory_number;
    cuda_code = cuda_code.replaceAll("%%java_lang_OutOfMemoryError_TypeNumber%%", out_of_memory_str);
    
    int size = Configuration.compilerInstance().getSharedMemSize();
    String size_str = ""+size;
    cuda_code = cuda_code.replaceAll("%%shared_mem_size%%", size_str);
    
    boolean exceptions = Configuration.compilerInstance().getExceptions();
    String exceptions_str;
    if(exceptions){
      exceptions_str = ""+1;
    } else {
      exceptions_str = ""+0;
    }
    cuda_code = cuda_code.replaceAll("%%using_exceptions%%", exceptions_str);
    
    int string_number = RootbeerClassLoader.v().getClassNumber("java.lang.String");
    String string_str = "" + string_number;
    cuda_code = cuda_code.replaceAll("%%java_lang_String_TypeNumber%%", string_str);

    int integer_number = RootbeerClassLoader.v().getClassNumber("java.lang.Integer");
    String integer_str = "" + integer_number;
    cuda_code = cuda_code.replaceAll("%%java_lang_Integer_TypeNumber%%", integer_str);
    
    int long_number = RootbeerClassLoader.v().getClassNumber("java.lang.Long");
    String long_str = "" + long_number;
    cuda_code = cuda_code.replaceAll("%%java_lang_Long_TypeNumber%%", long_str);
    
    int float_number = RootbeerClassLoader.v().getClassNumber("java.lang.Float");
    String float_str = "" + float_number;
    cuda_code = cuda_code.replaceAll("%%java_lang_Float_TypeNumber%%", float_str);
    
    int double_number = RootbeerClassLoader.v().getClassNumber("java.lang.Double");
    String double_str = "" + double_number;
    cuda_code = cuda_code.replaceAll("%%java_lang_Double_TypeNumber%%", double_str);

    int boolean_number = RootbeerClassLoader.v().getClassNumber("java.lang.Boolean");
    String boolean_str = "" + boolean_number;
    cuda_code = cuda_code.replaceAll("%%java_lang_Boolean_TypeNumber%%", boolean_str);
    
    return cuda_code;
  }
  
  public String[] getOpenCLCode() throws Exception {
    String[] source_code = makeSourceCode();
    return source_code;
  }

  public CompileResult[] getCudaCode() throws Exception {
    String[] source_code = makeSourceCode();
    return new CudaTweaks().compileProgram(source_code[0], Configuration.compilerInstance().getCompileArchitecture());
  }

  private String headerString(boolean unix) throws IOException {
    String defines = "";
    if(Configuration.compilerInstance().getArrayChecks()){
      defines += "#define ARRAY_CHECKS\n"; 
    }
    
    String specific_path;
    if(unix){
      specific_path = Tweaks.v().getUnixHeaderPath();
    } else {
      specific_path = Tweaks.v().getWindowsHeaderPath();
    }
    if(specific_path == null)
      return "";
    String both_path = Tweaks.v().getBothHeaderPath();
    String both_header = "";
    if(both_path != null){
      both_header = ResourceReader.getResource(both_path);
    }
    String specific_header = ResourceReader.getResource(specific_path);
    
    String barrier_path = Tweaks.v().getBarrierPath();
    String barrier_code = "";
    if(barrier_path != null){
      barrier_code = ResourceReader.getResource(barrier_path);
    }
    
    return defines + "\n" + specific_header + "\n" + both_header + "\n" + barrier_code;
  }
  
  private String kernelString(boolean unix) throws IOException {
    String kernel_path;
    if(unix){
      kernel_path = Tweaks.v().getUnixKernelPath();
    } else {
      kernel_path = Tweaks.v().getWindowsKernelPath();
    }
    String specific_kernel_code = ResourceReader.getResource(kernel_path);
    String both_kernel_code = "";
    String both_kernel_path = Tweaks.v().getBothKernelPath();
    if(both_kernel_path != null){
      both_kernel_code = ResourceReader.getResource(both_kernel_path);
    }
    return both_kernel_code + "\n" + specific_kernel_code;
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
    
    ArrayCopyGenerate arr_generate = new ArrayCopyGenerate();
    protos.add(arr_generate.getProto());
    
    List<OpenCLMethod> methods = m_methodHierarchies.getMethods();
    for(OpenCLMethod method : methods){ 
      protos.add(method.getMethodPrototype());
    }    
    List<OpenCLPolymorphicMethod> poly_methods = m_methodHierarchies.getPolyMorphicMethods();
    for(OpenCLPolymorphicMethod poly_method : poly_methods){
      protos.add(poly_method.getMethodPrototypes());
    }
    protos.add(m_fieldCodeGeneration.prototypes(m_classes));
    for(OpenCLArrayType array_type : m_arrayTypes){
      protos.add(array_type.getPrototypes());
    }
    for(OpenCLInstanceof type : m_instanceOfs){
      protos.add(type.getPrototype());
    }
    Iterator<String> iter = protos.iterator();
    while(iter.hasNext()){
      ret.append(iter.next());
    }
    return ret.toString();
  }

  private String methodBodiesString() throws IOException{
    StringBuilder ret = new StringBuilder();
    if(m_usesGarbageCollector){
      ret.append("#define USING_GARBAGE_COLLECTOR\n");
    }
    
    //a set is used so duplicates get filtered out
    Set<String> bodies = new HashSet<String>();
    
    ArrayCopyTypeReduction reduction = new ArrayCopyTypeReduction();
    Set<OpenCLArrayType> new_types = reduction.run(m_arrayTypes, m_methodHierarchies);
    
    ArrayCopyGenerate arr_generate = new ArrayCopyGenerate();
    bodies.add(arr_generate.get(new_types));
    
    List<OpenCLMethod> methods = m_methodHierarchies.getMethods();
    for(OpenCLMethod method : methods){ 
      bodies.add(method.getMethodBody());
    }
    List<OpenCLPolymorphicMethod> poly_methods = m_methodHierarchies.getPolyMorphicMethods();
    for(OpenCLPolymorphicMethod poly_method : poly_methods){
      bodies.addAll(poly_method.getMethodBodies());
    }
    FieldTypeSwitch type_switch = new FieldTypeSwitch();
    String field_bodies = m_fieldCodeGeneration.bodies(m_classes, type_switch);
    bodies.add(field_bodies);
    for(OpenCLArrayType array_type : m_arrayTypes){
      bodies.add(array_type.getBodies());
    }
    for(OpenCLInstanceof type : m_instanceOfs){
      bodies.add(type.getBody());
    }
    Iterator<String> iter = bodies.iterator();
    ret.append(type_switch.getFunctions());
    while(iter.hasNext()){
      ret.append(iter.next());
    }
    return ret.toString();
  }
  
  public OffsetCalculator getOffsetCalculator(SootClass soot_class){
    List<CompositeField> composites = getCompositeFields();
    for(CompositeField composite : composites){
      List<SootClass> classes = composite.getClasses();
      if(classes.contains(soot_class))
        return new OffsetCalculator(composite);
    }
    throw new RuntimeException("Cannot find composite field for soot_class");
  }

  public void addCodeSegment(MethodCodeSegment codeSegment){
    m_rootSootClass = codeSegment.getRootSootClass();    
    m_readOnlyTypes = new ReadOnlyTypes(codeSegment.getRootMethod());
    getOpenCLClass(m_rootSootClass);
  }

  public boolean isArrayLocalWrittenTo(Local local){
    return true;
  }
  
  public ReadOnlyTypes getReadOnlyTypes(){
    return m_readOnlyTypes;
  }

  public boolean isRootClass(SootClass soot_class) {
    return soot_class.getName().equals(m_rootSootClass.getName());
  }

  public Map<String, OpenCLClass> getClassMap(){
    return m_classes;
  }

  public List<CompositeField> getCompositeFields() {
    return m_compositeFields;
  }

  private void buildCompositeFields() {
    CompositeFieldFactory factory = new CompositeFieldFactory();
    factory.setup(m_classes);
    m_compositeFields = factory.getCompositeFields();
  }
  
  public ClassConstantNumbers getClassConstantNumbers(){
    return m_constantNumbers;
  }
}
