/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.classloader;

import edu.syr.pcpratts.rootbeer.compiler.ClassRemapping;
import edu.syr.pcpratts.rootbeer.util.MethodSignatureUtil;
import edu.syr.pcpratts.rootbeer.util.SystemOutHandler;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import soot.jimple.InvokeExpr;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import soot.*;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

public class FastWholeProgram {
  private static FastWholeProgram m_instance;

  private final List<String> m_paths;
  private final List<String> m_classPaths;
  private final String m_tempFolder;
  private final Map<String, SootClass> m_classes;
  private final Collection<SootMethod> m_entryMethods;
  private final FastClassResolver m_resolver;
  private List<String> m_applicationClasses;
  private List<String> m_cgWorkQueue;
  private Set<String> m_cgVisited;
  private List<String> m_bodiesClasses;
  private final Logger m_log;
  private List<String> m_ignorePackages;
  private List<String> m_keepPackages;
  private List<String> m_runtimeClasses;
  private Set<String> m_resolvedMethods;
  
  private Map<SootMethod, DfsInfo> m_dfsInfos;
  private DfsInfo m_currDfsInfo;
  
  private String m_kernelOverrideClass;
  private List<SootClass> m_kernelClasses;
  private List<SootClass> m_testFactoryClasses;
  private boolean m_singleKernel;
  private boolean m_disableClassRemapping;
  
  public static FastWholeProgram v(){
    if(m_instance == null){
      m_instance = new FastWholeProgram();
    }
    return m_instance;
  }
  
  private FastWholeProgram() {
    m_log = Logger.getLogger("edu.syr.pcpratts");
    showAllLogging();
    m_paths = new ArrayList<String>();
    m_classPaths = new ArrayList<String>();
    m_entryMethods = new HashSet<SootMethod>(); //faster lookups
    m_classes = new HashMap<String, SootClass>();
    m_kernelClasses = new ArrayList<SootClass>();
    m_testFactoryClasses = new ArrayList<SootClass>();

    m_tempFolder = "jar-contents";
    File file = new File(m_tempFolder);
    file.mkdirs();

    m_applicationClasses = new ArrayList<String>();
    
    m_resolver = new FastClassResolver(m_tempFolder, m_classPaths, m_paths, m_applicationClasses);
    
    m_ignorePackages = new ArrayList<String>();
    m_ignorePackages.add("edu.syr.pcpratts.compressor.");
    m_ignorePackages.add("edu.syr.pcpratts.deadmethods.");
    m_ignorePackages.add("edu.syr.pcpratts.jpp.");
    m_ignorePackages.add("edu.syr.pcpratts.rootbeer.");
    m_ignorePackages.add("pack.");
    m_ignorePackages.add("jasmin.");
    m_ignorePackages.add("soot.");
    m_ignorePackages.add("beaver.");
    m_ignorePackages.add("polyglot.");
    m_ignorePackages.add("org.antlr.");
    m_ignorePackages.add("java_cup.");
    m_ignorePackages.add("ppg.");
    m_ignorePackages.add("antlr.");
    m_ignorePackages.add("jas.");
    m_ignorePackages.add("scm.");
    m_ignorePackages.add("org.xmlpull.v1.");
    m_ignorePackages.add("android.util.");
    m_ignorePackages.add("android.content.res.");
    m_ignorePackages.add("org.apache.commons.codec.");
    
    m_keepPackages = new ArrayList<String>();
    m_keepPackages.add("edu.syr.pcpratts.rootbeer.testcases.");
    m_keepPackages.add("edu.syr.pcpratts.rootbeer.runtime.remap.");
    
    m_runtimeClasses = new ArrayList<String>();
    m_runtimeClasses.add("edu.syr.pcpratts.rootbeer.generate.bytecode.Constants");
    m_runtimeClasses.add("edu.syr.pcpratts.rootbeer.runtime.RootbeerFactory");
    m_runtimeClasses.add("edu.syr.pcpratts.rootbeer.runtime.Rootbeer");
    m_runtimeClasses.add("edu.syr.pcpratts.rootbeer.runtime.RootbeerGpu");
    m_runtimeClasses.add("edu.syr.pcpratts.rootbeer.runtime.Kernel");
    m_runtimeClasses.add("edu.syr.pcpratts.rootbeer.runtime.CompiledKernel");
    m_runtimeClasses.add("edu.syr.pcpratts.rootbeer.runtime.Serializer");
    m_runtimeClasses.add("edu.syr.pcpratts.rootbeer.runtime.memory.Memory");
    m_runtimeClasses.add("edu.syr.pcpratts.rootbeer.runtime.Sentinal");
    m_runtimeClasses.add("edu.syr.pcpratts.rootbeer.test.TestSerialization");
    m_runtimeClasses.add("edu.syr.pcpratts.rootbeer.test.TestSerializationFactory");
    m_runtimeClasses.add("edu.syr.pcpratts.rootbeer.test.TestException");
    m_runtimeClasses.add("edu.syr.pcpratts.rootbeer.test.TestExceptionFactory");
    m_runtimeClasses.add("edu.syr.pcpratts.rootbeer.runtime.util.Stopwatch");
    m_runtimeClasses.add("edu.syr.pcpratts.rootbeer.runtime.PrivateFields");
    
    m_resolvedMethods = new HashSet<String>();
    m_cgWorkQueue = new LinkedList<String>();
    
    m_dfsInfos = new HashMap<SootMethod, DfsInfo>();
  }

  public void addRootbeerPath(String path, boolean run_tests) {
    m_paths.add(path);
    if(run_tests == false){
      m_keepPackages.remove("edu.syr.pcpratts.rootbeer.testcases.");
    }
  }

  /**
   * Adds a path of source to be analyzed
   *
   * @param path the path
   */
  public void addPath(String path) {
    m_paths.add(normalizePath(path));
  }

  private String normalizePath(String path) {
    File file = new File(path);
    if(file.isDirectory() == false){
      return path;
    }
    if (path.endsWith(File.separator) == false) {
      path += File.separator;
    }
    return path;
  }

  /**
   * Add many paths to analyze
   *
   * @param paths the paths to analyze. No nulls allowed.
   */
  public void addPath(Collection<String> paths) {
    List<String> list_paths = new ArrayList<String>();
    list_paths.addAll(paths);
    for (int i = 0; i < list_paths.size(); ++i) {
      list_paths.set(i, normalizePath(list_paths.get(i)));
    }
    m_paths.addAll(paths);
  }

  /**
   * Adds one class path entry
   *
   * @param path
   */
  public void addClassPath(String path) {
    m_classPaths.add(path);
  }

  /**
   * Adds many class path entries
   *
   * @param path
   */
  public void addClassPath(Collection<String> paths) {
    m_classPaths.addAll(paths);
  }

  /**
   * Adds all the jars in the directory to the class path, recursively
   * @param folder 
   */
  public void addClassPathDir(String folder) {
    File file = new File(folder);
    if (file.exists() == false) {
      throw new RuntimeException("folder: " + folder + " does not exist");
    }
    File[] children = file.listFiles();
    for (File child : children) {
      if (child.isDirectory()) {
        addClassPathDir(child.getAbsolutePath());
      } else {
        String name = child.getName();
        if (name.startsWith(".")) {
          continue;
        }
        if (name.endsWith(".jar") == false) {
          continue;
        }
        addClassPath(child.getAbsolutePath());
      }
    }
  }
  
  public void init() {
    try {
      System.out.println("loading application jars...");
      for (String path : m_paths) {
        File file = new File(path);
        if (file.isDirectory()) {
          extractPath(path);
        } else {
          extractJar(path);
        }
      }
      m_resolver.cachePackageNames();
      loadBuiltIns();
      loadToHierarchies();
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  private void loadToHierarchies(){
    System.out.println("loading to hierarchies...");
    for(String app_class : m_applicationClasses){
      SootClass soot_class = m_resolver.resolveClass(app_class, SootClass.HIERARCHY);
      Iterator<SootClass> iter = soot_class.getInterfaces().iterator();
      while(iter.hasNext()){
        SootClass curr = iter.next();
        if(curr.getName().equals("edu.syr.pcpratts.rootbeer.runtime.Kernel")){
          if(m_kernelClasses.contains(soot_class) == false){
            m_kernelClasses.add(soot_class);
            m_resolver.resolveClass(soot_class.getName(), SootClass.BODIES);
          }
        }
        if(curr.getName().equals("edu.syr.pcpratts.rootbeer.test.TestException")){
          if(m_testFactoryClasses.contains(curr) == false){
            m_testFactoryClasses.add(soot_class);
            m_resolver.resolveClass(soot_class.getName(), SootClass.BODIES);
          }
        } 
        if(curr.getName().equals("edu.syr.pcpratts.rootbeer.test.TestSerialization")){
          if(m_testFactoryClasses.contains(curr) == false){
            m_testFactoryClasses.add(soot_class);
            m_resolver.resolveClass(soot_class.getName(), SootClass.BODIES);
          }
        } 
      }
    }
  }
  
  public List<SootClass> getTestFactoryClasses(){
    return m_testFactoryClasses;
  }

  private SootMethod fullyResolveMethod(String method_signature, boolean force_resolve){
    MethodSignatureUtil util = new MethodSignatureUtil(method_signature);
    final String soot_class_str = util.getClassName();
    if(m_resolvedMethods.contains(method_signature)){
      return m_resolver.resolveMethod(method_signature);
    }
    m_resolvedMethods.add(method_signature);
    //m_log.log(Level.FINER, "Resolving to bodies:  "+soot_class_str);
    SootClass soot_class = m_resolver.resolveClass(soot_class_str, SootClass.BODIES);
    SootMethod curr = m_resolver.resolveMethod(method_signature);
    if (curr == null || curr.isConcrete() == false) {
      //m_log.log(Level.FINER, "Unable to find a concrete body for "+soot_class_str);
      return null;
    } //hack to make sure it really loads it all
    SootClass actual_class = curr.getDeclaringClass();
    if(force_resolve){
      m_resolver.forceResolveClass(actual_class.getName(), SootClass.BODIES);
    } else {
      m_resolver.resolveClass(actual_class.getName(), SootClass.BODIES);
    }      
    return curr;
  }
  
  public void loadToBodyLater(String curr_signature){
    SootMethod curr = fullyResolveMethod(curr_signature, false);
    loadToBody(curr, false);
  }
  
  private void loadToBody(String curr_signature) {
    SootMethod curr = fullyResolveMethod(curr_signature, true);
    loadToBody(curr, true);
  }
  
  public boolean isApplicationClass(SootClass soot_class){
    String filename = classToFilename(soot_class.getName());
    String jar_name = m_resolver.getJarNameForFilename(filename, soot_class.getName());
    return isApplicationJar(jar_name);
  }
  
  private void loadToBody(SootMethod curr, boolean force_resolve){
    if(curr == null){
      return;
    }
    
    //m_log.log(Level.FINEST, "call graph dfs: "+curr.getSignature());
    
    m_currDfsInfo.addMethod(curr.getSignature());

    Body body = null;
    try{
      body = curr.retrieveActiveBody();
    } catch (RuntimeException e){
      //m_log.log(Level.FINER, "cannot find body for method: "+curr.getSignature());
      return;
    }
    
    SootClass soot_class = curr.getDeclaringClass();
    String filename = classToFilename(soot_class.getName());
    String jar_name = m_resolver.getJarNameForFilename(filename, soot_class.getName());
    if(isApplicationJar(jar_name)){
      soot_class.setApplicationClass();
    }

    //Search for all invokes and add them to the call graph
    for (Unit curr_unit : body.getUnits()){
      for (ValueBox box : (List<ValueBox>)curr_unit.getUseAndDefBoxes()) {
        final Value value = box.getValue();
        if (value instanceof InvokeExpr) {
          final InvokeExpr expr = (InvokeExpr) value;
          try {
            final SootMethodRef method_ref = expr.getMethodRef();
            final String class_name = method_ref.declaringClass().getName();
            
            if(m_bodiesClasses != null){
              if (m_bodiesClasses.contains(class_name) == false) {
                m_bodiesClasses.add(class_name);
              }
            }
            
            String dest_sig = method_ref.getSignature();
            SootMethod dest_method = fullyResolveMethod(dest_sig, force_resolve);
            if(dest_method == null){
              continue;
            }
            
            while(true){
              
              if(dest_method.isConcrete()){
                try {
                  Body dest_body = dest_method.retrieveActiveBody();
                } catch(Exception ex){
                  //continue;
                }

                //m_log.log(Level.FINEST, "adding edge: "+curr.getSignature()+" -> "+dest_sig);
                //m_callGraph.addEdge(curr.getSignature(), dest_sig, (Stmt) curr_unit);
                m_cgWorkQueue.add(dest_sig);
              }
              
              SootClass curr_class = dest_method.getDeclaringClass();
              dest_method = getSuperMethod(curr_class, dest_method);
              if(dest_method == null){
                break;
              }
              
              dest_sig = dest_method.getSignature();
            }
          } catch (Exception ex) {
            m_log.log(Level.WARNING, "Exception while recording call", ex);
          }
        }
      }
    } 
  }
  
  private SootMethod getSuperMethod(SootClass curr_class, SootMethod dest_method){
    while(true){
      if(curr_class.hasSuperclass() == false){
        return null;
      }
      SootClass parent = curr_class.getSuperclass();
      if(parent.declaresMethod(dest_method.getSubSignature())){
        return parent.getMethod(dest_method.getSubSignature());
      } else {
        curr_class = parent;
      }
    }
  }

  private void extractPath(String path) throws Exception {
    File file = new File(path);
    File[] children = file.listFiles();
    for (File child : children) {
      extractPath(path, child.getName());
    }
  }

  private void extractPath(String root, String subfolder) throws Exception {
    String full_name = root + File.separator + subfolder;
    File file = new File(full_name);
    if (file.isDirectory()) {
      file.mkdirs();
      File dest = new File(m_tempFolder + File.separator + subfolder);
      dest.mkdirs();
      File[] children = file.listFiles();
      for (File child : children) {
        extractPath(root, subfolder + File.separator + child.getName());
      }
    } else {
      String class_name = filenameToClass(File.separator + subfolder, File.separator);
      m_applicationClasses.add(class_name);
      FileInputStream fin = new FileInputStream(full_name);
      FileOutputStream fout = new FileOutputStream(m_tempFolder + File.separator + subfolder);
      WriteStream writer = new WriteStream();
      writer.write(fin, fout);
      fin.close();
      fout.close();
    }
  }

  private void extractJar(String path) {
    File pathFile = new File(path);
    try {
      JarInputStream is = new JarInputStream(new FileInputStream(pathFile));
      while (true) {
        JarEntry entry = is.getNextJarEntry();
        if (entry == null) {
          break;
        }
        if(entry.isDirectory() == false){
          if(entry.getName().endsWith(".class") == false){
            continue;
          }
          String class_name = filenameToClass(entry.getName(), "/");
          if(ignorePackage(class_name) == false){
            m_applicationClasses.add(class_name);
          }
        }
        WriteJarEntry writer = new WriteJarEntry();
        writer.write(entry, is, m_tempFolder);
      }
      is.close();
    } catch (Exception ex) {
      m_log.log(Level.WARNING, "Unable to extract Jar", ex);
    }
  }

  private void loadToSignatures() throws Exception {
    System.out.println("loading to signatures...");
    File root = new File(m_tempFolder);
    loadToSignatures(root);
  }

  private void addBasicClass(String class_name) {
    addBasicClass(class_name, SootClass.HIERARCHY);
  }

  private void addBasicClass(String class_name, int level) {
    m_resolver.resolveClass(class_name, level);
  }

  private void loadBuiltIns() {
    System.out.println("loading built-ins...");
    addBasicClass("java.lang.Object");
    addBasicClass("java.lang.Class", SootClass.SIGNATURES);

    addBasicClass("java.lang.Void", SootClass.SIGNATURES);
    addBasicClass("java.lang.Boolean", SootClass.SIGNATURES);
    addBasicClass("java.lang.Byte", SootClass.SIGNATURES);
    addBasicClass("java.lang.Character", SootClass.SIGNATURES);
    addBasicClass("java.lang.Short", SootClass.SIGNATURES);
    addBasicClass("java.lang.Integer", SootClass.SIGNATURES);
    addBasicClass("java.lang.Long", SootClass.SIGNATURES);
    addBasicClass("java.lang.Float", SootClass.SIGNATURES);
    addBasicClass("java.lang.Double", SootClass.SIGNATURES);

    addBasicClass("java.lang.String");
    addBasicClass("java.lang.StringBuffer", SootClass.SIGNATURES);

    addBasicClass("java.lang.Error");
    addBasicClass("java.lang.AssertionError", SootClass.SIGNATURES);
    addBasicClass("java.lang.Throwable", SootClass.SIGNATURES);
    addBasicClass("java.lang.NoClassDefFoundError", SootClass.SIGNATURES);
    addBasicClass("java.lang.ExceptionInInitializerError");
    addBasicClass("java.lang.RuntimeException");
    addBasicClass("java.lang.ClassNotFoundException");
    addBasicClass("java.lang.ArithmeticException");
    addBasicClass("java.lang.ArrayStoreException");
    addBasicClass("java.lang.ClassCastException");
    addBasicClass("java.lang.IllegalMonitorStateException");
    addBasicClass("java.lang.IndexOutOfBoundsException");
    addBasicClass("java.lang.ArrayIndexOutOfBoundsException");
    addBasicClass("java.lang.NegativeArraySizeException");
    addBasicClass("java.lang.NullPointerException");
    addBasicClass("java.lang.InstantiationError");
    addBasicClass("java.lang.InternalError");
    addBasicClass("java.lang.OutOfMemoryError");
    addBasicClass("java.lang.StackOverflowError");
    addBasicClass("java.lang.UnknownError");
    addBasicClass("java.lang.ThreadDeath");
    addBasicClass("java.lang.ClassCircularityError");
    addBasicClass("java.lang.ClassFormatError");
    addBasicClass("java.lang.IllegalAccessError");
    addBasicClass("java.lang.IncompatibleClassChangeError");
    addBasicClass("java.lang.LinkageError");
    addBasicClass("java.lang.VerifyError");
    addBasicClass("java.lang.NoSuchFieldError");
    addBasicClass("java.lang.AbstractMethodError");
    addBasicClass("java.lang.NoSuchMethodError");
    addBasicClass("java.lang.UnsatisfiedLinkError");

    addBasicClass("java.lang.Thread");
    addBasicClass("java.lang.Runnable");
    addBasicClass("java.lang.Cloneable");

    addBasicClass("java.io.Serializable");

    addBasicClass("java.lang.ref.Finalizer");
  }

  private void loadToSignatures(File file) throws Exception {
    if (file.isDirectory()) {
      File[] children = file.listFiles();
      for (File child : children) {
        loadToSignatures(child);
      }
      return;
    }
    String name = file.getAbsolutePath();
    if (name.endsWith(".class") == false) {
      return;
    }
    File full_temp_folder = new File(m_tempFolder);
    name = name.substring(full_temp_folder.getAbsolutePath().length());
    if (isFilename(name)) {
      name = filenameToClass(name, File.separator);
    }
    if (m_applicationClasses.contains(name) == false) {
      return;
    }
    //m_log.log(Level.FINE, "Loading class to signature: "+name);

    SootClass loaded = m_resolver.resolveClass(name, SootClass.SIGNATURES);
    m_classes.put(name, loaded);
  }

  private boolean isFilename(String name) {
    if (name.endsWith(".class")) {
      return true;
    }
    return false;
  }

  private String filenameToClass(String name, String sep) {
    if (name.startsWith(sep)) {
      name = name.substring(sep.length());
    }
    name = name.replace(".class", "");
    name = name.replace(sep, ".");
    return name;
  }

  /**
   * Enables full logging. Logging will be sent to System.out.
   */
  public void showAllLogging() {
    Logger parent = Logger.getLogger(this.getClass().getPackage().getName());
    parent.setLevel(Level.ALL);
    Handler handler = new SystemOutHandler();
    m_log.setUseParentHandlers(true);
    m_log.setLevel(Level.ALL);
    m_log.addHandler(handler);
  }

  public List<String> getApplicationClasses() {
    return m_applicationClasses;
  }

  private boolean ignorePackage(String class_name) {
    for(String runtime_class : m_runtimeClasses){
      if(class_name.equals(runtime_class)){
        return false;
      }
    }
    for(String keep_package : m_keepPackages){
      if(class_name.startsWith(keep_package)){
        return false;
      }
    }
    for(String ignore_package : m_ignorePackages){
      if(class_name.startsWith(ignore_package)){
        return true;
      }
    }
    return false;
  }
  
  public boolean shouldDfsMethod(SootMethod method){
    SootClass soot_class = method.getDeclaringClass();
    String pkg = soot_class.getPackageName();
    if(ignorePackage(pkg)){
      return false;
    } 
    return true;
  }

  private String classToFilename(String name) {
    name = name.replace(".", "/");
    name += ".class";
    return name;
  }

  private boolean isApplicationJar(String jar_name) {
    return m_paths.contains(jar_name);
  }

  public DfsInfo getDfsInfo(SootMethod entry) {
    if(m_singleKernel){
      return m_currDfsInfo;
    }
    if(m_dfsInfos.containsKey(entry) == false){
      return null;
    } else {
      return m_dfsInfos.get(entry);
    }
  }

  public void singleKernel(SootMethod kernel_method) {
    m_currDfsInfo = new DfsInfo(kernel_method);
    m_singleKernel = true;
  }

  public List<SootClass> getKernelClasses() {
    return m_kernelClasses;
  }

  public void fullyLoad(SootMethod kernel_method, boolean find_reachables, boolean disable_class_remapping) {
    System.out.println("running dfs on: "+kernel_method.getDeclaringClass().getName()+"...");
    m_disableClassRemapping = disable_class_remapping;
    m_currDfsInfo = new DfsInfo(kernel_method);    
    m_dfsInfos.put(kernel_method, m_currDfsInfo);
    
    doDfs(kernel_method);
    if(m_disableClassRemapping = false){
      buildFullCallGraph(kernel_method);
    }
    if(find_reachables){
      findReachableMethods();
    }
    buildHierarchy();
  }
  
  private void doDfs(SootMethod method){
    String signature = method.getSignature();
    if(m_currDfsInfo.containsMethod(signature)){
      return;
    }
    m_currDfsInfo.addMethod(signature);
        
    SootClass soot_class = method.getDeclaringClass();
    addType(soot_class.getType());
    
    DfsValueSwitch value_switch = new DfsValueSwitch();
    value_switch.run(method);
    
    Set<Type> types = value_switch.getTypes();
    for(Type type : types){
      addType(type);
    }
    
    Set<DfsMethodRef> methods = value_switch.getMethodRefs();
    for(DfsMethodRef ref : methods){
      SootMethodRef mref = ref.getSootMethodRef();
      SootClass method_class = mref.declaringClass();
      m_resolver.resolveClass(method_class.getName(), SootClass.BODIES);
      SootMethod dest = mref.resolve();

      if(dest.isConcrete() == false){
        continue;
      } 
      
      addType(method_class.getType());
      
      m_currDfsInfo.addCallGraphEdge(method, ref.getStmt(), dest);
      doDfs(dest);
    }
    
    Set<SootFieldRef> fields = value_switch.getFieldRefs();
    for(SootFieldRef ref : fields){
      addType(ref.type());
      
      SootField field = ref.resolve();
      m_currDfsInfo.addField(field);
    }
    
    Set<Type> instance_ofs = value_switch.getInstanceOfs();
    for(Type type : instance_ofs){
      m_currDfsInfo.addInstanceOf(type);
    }
    
    while(soot_class.hasSuperclass()){
      SootClass super_class = soot_class.getSuperclass();
      if(super_class.declaresMethod(method.getSubSignature())){
        SootMethod super_method = super_class.getMethod(method.getSubSignature());
        doDfs(super_method);
      }
      soot_class = super_class;
    }
  }

  private void addType(Type type) {
    List<Type> queue = new LinkedList<Type>();
    queue.add(type);
    while(queue.isEmpty() == false){
      Type curr = queue.get(0);
      queue.remove(0);
      
      if(m_currDfsInfo.containsType(curr)){
        continue;
      }
        
      m_currDfsInfo.addType(curr);
      
      SootClass type_class = findTypeClass(curr);
      if(type_class == null){
        continue;
      }
      
      type_class = m_resolver.resolveClass(type_class.getName(), SootClass.SIGNATURES);
      
      if(type_class.hasSuperclass()){
        queue.add(type_class.getSuperclass().getType());
        
        m_currDfsInfo.addSuperClass(curr, type_class.getSuperclass().getType());
      }
      
      if(type_class.hasOuterClass()){
        queue.add(type_class.getOuterClass().getType());
      }
    }
  }
  
  private SootClass findTypeClass(Type type){
    if(type instanceof ArrayType){
      ArrayType array_type = (ArrayType) type;
      return findTypeClass(array_type.baseType);
    } else if(type instanceof RefType){
      RefType ref_type = (RefType) type;
      return ref_type.getSootClass();
    } else {
      //PrimType and VoidType
      return null;
    } 
  }

  public FastClassResolver getResolver() {
    return m_resolver;
  }

  public void buildFullCallGraph(SootMethod kernel_method) {
    System.out.println("building full call graph for: "+kernel_method.getDeclaringClass().getName()+"...");
    List<String> methods = new ArrayList<String>();
    for(String cls : m_applicationClasses){
      SootClass soot_class = m_resolver.resolveClass(cls, SootClass.BODIES);
      for(SootMethod method : soot_class.getMethods()){
        methods.add(method.getSignature());
      }
    }
    
    MethodSignatureUtil util = new MethodSignatureUtil();
    for(int i = 0; i < methods.size(); ++i){
      String method_sig = methods.get(i);
      util.parse(method_sig);
      SootClass soot_class = Scene.v().getSootClass(util.getClassName());
      SootMethod method = soot_class.getMethod(util.getMethodSubSignature());
      
      if(method.isConcrete() == false){
        continue;
      }
        
      DfsValueSwitch value_switch = new DfsValueSwitch();
      value_switch.run(method);
        
      Set<DfsMethodRef> method_refs = value_switch.getMethodRefs();
      for(DfsMethodRef dfs_ref : method_refs){
        m_currDfsInfo.addCallGraphEdge(method, dfs_ref.getStmt(), dfs_ref.getSootMethodRef().resolve());
      }
    }
  }

  public void findReachableMethods() {
    
    List<SootMethod> queue = new LinkedList<SootMethod>();
    Set<SootMethod> visited = new HashSet<SootMethod>();
    
    SootMethod root_method = m_currDfsInfo.getRootMethod();
    CallGraph call_graph = m_currDfsInfo.getCallGraph();
            
    SootClass soot_class = root_method.getDeclaringClass();
    
    for(SootMethod method : soot_class.getMethods()){
      Iterator<Edge> into = call_graph.edgesInto(method);
      addToQueue(into, queue, visited);
    }
    
    while(queue.isEmpty() == false){
      SootMethod curr = queue.get(0);
      queue.remove(0);
      
      doDfs(curr);
      
      Iterator<Edge> curr_into = call_graph.edgesInto(curr);
      
      addToQueue(curr_into, queue, visited);
    }
  }
  
  private void addToQueue(Iterator<Edge> edges, List<SootMethod> queue, Set<SootMethod> visited){
    while(edges.hasNext()){
      Edge edge = edges.next();
      
      SootMethod src = edge.src();
      if(visited.contains(src) == false && FastWholeProgram.v().shouldDfsMethod(src)){
        queue.add(src);
        visited.add(src);
      }
      
      SootMethod dest = edge.tgt();
      if(visited.contains(dest) == false && FastWholeProgram.v().shouldDfsMethod(dest)){
        queue.add(dest);
        visited.add(dest);
      }
    }
  }

  public void buildHierarchy(){   
    System.out.println("building class hierarchy...");
    m_currDfsInfo.expandArrayTypes();
    m_currDfsInfo.orderTypes();
    m_currDfsInfo.createClassHierarchy(); 
  }
}
