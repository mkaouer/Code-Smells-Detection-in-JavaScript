/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl;

import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.OpenCLField;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.FieldCloner;
import edu.syr.pcpratts.rootbeer.generate.bytecode.ReadOnlyTypes;
import edu.syr.pcpratts.rootbeer.generate.codesegment.CodeSegment;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.CompositeField;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.FieldCodeGeneration;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.FieldTypeSwitch;
import edu.syr.pcpratts.rootbeer.generate.opencl.fields.OffsetCalculator;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.CompileResult;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.CudaTweaks;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.Tweaks;
import edu.syr.pcpratts.rootbeer.util.ResourceReader;
import edu.syr.pcpratts.rootbeer.util.MethodSignatureUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import soot.*;

public class OpenCLScene {
  private static OpenCLScene m_instance;
  private static int m_curentIdent;
  private Map<String, OpenCLClass> m_classes;
  private Map<String, String> m_oclToSoot;
  private Set<OpenCLArrayType> m_arrayTypes;
  private CodeSegment m_codeSegment;
  private MethodHierarchies m_methodHierarchies;
  private boolean m_usesGarbageCollector;
  private SootClass m_rootSootClass;
  private int m_endOfStatics;
  private ReadOnlyTypes m_readOnlyTypes;
  private Set<OpenCLInstanceof> m_instanceOfs;
  
  static {
    m_curentIdent = 0;
  }

  public OpenCLScene(){
    m_codeSegment = null;
    m_classes = new LinkedHashMap<String, OpenCLClass>();
    m_oclToSoot = new HashMap<String, String>();
    m_arrayTypes = new LinkedHashSet<OpenCLArrayType>();
    m_methodHierarchies = new MethodHierarchies();
    m_instanceOfs = new HashSet<OpenCLInstanceof>();
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
    return RootbeerScene.v().getDfsInfo().getClassNumber(soot_class);
  }
  
  public void addMethod(SootMethod soot_method){
    SootClass soot_class = soot_method.getDeclaringClass();

    OpenCLClass ocl_class = getOpenCLClass(soot_class);
    ocl_class.addMethod(new OpenCLMethod(soot_method, soot_class));

    //add the method 
    m_methodHierarchies.addMethod(soot_method);
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
    return m_classes.get(soot_class.getName());
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
  
  public OpenCLClass addClass(SootClass soot_class){
    OpenCLClass ocl_class = new OpenCLClass(soot_class);
    
    if(m_classes.containsKey(soot_class.getName()) == false){
      m_classes.put(soot_class.getName(), ocl_class);
    } else {
      ocl_class = m_classes.get(soot_class.getName());
    }
    
    if(m_oclToSoot.containsKey(ocl_class.getName()) == false){
      m_oclToSoot.put(ocl_class.getName(), soot_class.getName());
    }
  
    return ocl_class;
  }
  
  private String makeSourceCode() throws Exception {
    m_usesGarbageCollector = false;
    
    Set<String> methods = RootbeerScene.v().getDfsInfo().getAllMethods();
    MethodSignatureUtil util = new MethodSignatureUtil();
    for(String method_sig : methods){
      util.parse(method_sig);
      String cls = util.getClassName();
      String method_sub_sig = util.getMethodSubSignature();
      SootClass soot_class = Scene.v().getSootClass(cls);
      OpenCLScene.v().addClass(soot_class);
      SootMethod method = RootbeerScene.v().getMethod(soot_class, method_sub_sig);
      addMethod(method);
    }
    
    Set<SootField> fields = RootbeerScene.v().getDfsInfo().getFields();
    for(SootField field : fields){
      addField(field);
    }
    
    Set<ArrayType> array_types = RootbeerScene.v().getDfsInfo().getArrayTypes();
    for(ArrayType array_type : array_types){
      OpenCLArrayType ocl_array_type = new OpenCLArrayType(array_type);
      addArrayType(ocl_array_type);
    }
    
    Set<Type> instanceofs = RootbeerScene.v().getDfsInfo().getInstanceOfs();
    for(Type type : instanceofs){
      addInstanceof(type);
    }
    
    StringBuilder ret = new StringBuilder();
    ret.append(headerString());
    ret.append(methodPrototypesString());
    ret.append(garbageCollectorString());
    ret.append(methodBodiesString());

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

  public CompileResult getCudaCode() throws Exception {
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
    
    ArrayCopyGenerate arr_generate = new ArrayCopyGenerate();
    protos.add(arr_generate.getProto());
    
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
    for(OpenCLInstanceof type : m_instanceOfs){
      bodies.add(type.getBody());
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

  public void addCodeSegment(CodeSegment codeSegment){
    this.m_codeSegment = codeSegment;
    m_rootSootClass = codeSegment.getRootSootClass();    
    m_readOnlyTypes = new ReadOnlyTypes(codeSegment.getRootMethod());
    getOpenCLClass(m_rootSootClass);
  }

  public boolean isArrayLocalWrittenTo(Local local){
    return m_codeSegment.getReadWriteFieldInspector().localRepresentingArrayIsWrittenOnGpu(local);
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
}
