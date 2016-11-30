/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.entry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.trifort.rootbeer.compiler.*;
import org.trifort.rootbeer.configuration.Configuration;
import org.trifort.rootbeer.configuration.RootbeerPaths;
import org.trifort.rootbeer.generate.opencl.tweaks.CudaTweaks;
import org.trifort.rootbeer.generate.opencl.tweaks.NativeCpuTweaks;
import org.trifort.rootbeer.generate.opencl.tweaks.Tweaks;
import org.trifort.rootbeer.util.*;

import pack.Pack;
import soot.*;
import soot.options.Options;
import soot.rbclassload.DfsInfo;
import soot.rbclassload.ListClassTester;
import soot.rbclassload.ListMethodTester;
import soot.rbclassload.MethodTester;
import soot.rbclassload.RootbeerClassLoader;
import soot.util.JasminOutputStream;

public class RootbeerCompiler {

  private String m_classOutputFolder;
  private String m_jimpleOutputFolder;
  private String m_provider;
  private boolean m_enableClassRemapping;
  private MethodTester m_entryDetector;
  private Set<String> m_runtimePackages;
  
  public RootbeerCompiler(){
    clearOutputFolders();
    
    m_classOutputFolder = RootbeerPaths.v().getOutputClassFolder();
    m_jimpleOutputFolder = RootbeerPaths.v().getOutputJimpleFolder();
    
    if(Configuration.compilerInstance().getMode() == Configuration.MODE_GPU){      
      Tweaks.setInstance(new CudaTweaks());
    } else {
      Tweaks.setInstance(new NativeCpuTweaks());
    }
    
    m_enableClassRemapping = true;
    m_runtimePackages = new HashSet<String>();
    addRuntimePackages();
  }
  
  private void addRuntimePackages(){
    m_runtimePackages.add("com.lmax.disruptor.");
    m_runtimePackages.add("org.trifort.rootbeer.compiler.");
    m_runtimePackages.add("org.trifort.rootbeer.configuration.");
    m_runtimePackages.add("org.trifort.rootbeer.entry.");
    m_runtimePackages.add("org.trifort.rootbeer.generate.");
    m_runtimePackages.add("org.trifort.rootbeer.runtime.");
    m_runtimePackages.add("org.trifort.rootbeer.runtime2.");
    m_runtimePackages.add("org.trifort.rootbeer.test.");
    m_runtimePackages.add("org.trifort.rootbeer.util.");
  }
  
  public void disableClassRemapping(){
    m_enableClassRemapping = false; 
  }
  
  public void compile(String main_jar, List<String> lib_jars, List<String> dirs, String dest_jar) {
    
  }
    
  private void setupSoot(String jar_filename, String rootbeer_jar, boolean runtests){
    RootbeerClassLoader.v().setUserJar(jar_filename);
    //extractJar(jar_filename);
    
    List<String> proc_dir = new ArrayList<String>();
    proc_dir.add(jar_filename);
    
    Options.v().set_allow_phantom_refs(true);
    Options.v().set_rbclassload(true);
    Options.v().set_prepend_classpath(true);
    Options.v().set_process_dir(proc_dir);
    if(m_enableClassRemapping){
      Options.v().set_rbclassload_buildcg(true);
    }
    if(rootbeer_jar.equals("") == false){
      Options.v().set_soot_classpath(rootbeer_jar);
    }
    
    //Options.v().set_rbcl_remap_all(Configuration.compilerInstance().getRemapAll());
    Options.v().set_rbcl_remap_all(false);
    Options.v().set_rbcl_remap_prefix("org.trifort.rootbeer.runtime.remap.");
    
    RootbeerClassLoader.v().addEntryMethodTester(m_entryDetector);
    
    ListClassTester ignore_packages = new ListClassTester();
    ignore_packages.addPackage("com.lmax.disruptor.");
    ignore_packages.addPackage("org.trifort.rootbeer.compressor.");
    ignore_packages.addPackage("org.trifort.rootbeer.deadmethods.");
    ignore_packages.addPackage("org.trifort.rootbeer.compiler.");
    ignore_packages.addPackage("org.trifort.rootbeer.configuration.");
    ignore_packages.addPackage("org.trifort.rootbeer.entry.");
    ignore_packages.addPackage("org.trifort.rootbeer.generate.");
    ignore_packages.addPackage("org.trifort.rootbeer.test.");
    if(!runtests){
      ignore_packages.addPackage("org.trifort.rootbeer.testcases.");
    }
    ignore_packages.addPackage("org.trifort.rootbeer.util.");
    ignore_packages.addPackage("pack.");
    ignore_packages.addPackage("jasmin.");
    ignore_packages.addPackage("soot.");
    ignore_packages.addPackage("beaver.");
    ignore_packages.addPackage("polyglot.");
    ignore_packages.addPackage("org.antlr.");
    ignore_packages.addPackage("java_cup.");
    ignore_packages.addPackage("ppg.");
    ignore_packages.addPackage("antlr.");
    ignore_packages.addPackage("jas.");
    ignore_packages.addPackage("scm.");
    ignore_packages.addPackage("org.xmlpull.v1.");
    ignore_packages.addPackage("android.util.");
    ignore_packages.addPackage("android.content.res.");
    ignore_packages.addPackage("org.apache.commons.codec.");
    RootbeerClassLoader.v().addDontFollowClassTester(ignore_packages);
    
    ListClassTester keep_packages = new ListClassTester();
    for(String runtime_class : m_runtimePackages){
      keep_packages.addPackage(runtime_class);
    }
    RootbeerClassLoader.v().addToSignaturesClassTester(keep_packages);
    
    RootbeerClassLoader.v().addNewInvoke("java.lang.StringBuilder");
  
    ListMethodTester follow_tester = new ListMethodTester();
    follow_tester.addSignature("<java.lang.String: void <init>()>");
    follow_tester.addSignature("<java.lang.String: void <init>(char[])>");
    follow_tester.addSignature("<java.lang.StringBuilder: void <init>()>");
    follow_tester.addSignature("<java.lang.Boolean: java.lang.String toString(boolean)>");
    follow_tester.addSignature("<java.lang.Character: java.lang.String toString(char)>");
    follow_tester.addSignature("<java.lang.Double: java.lang.String toString(double)>");
    follow_tester.addSignature("<java.lang.Float: java.lang.String toString(float)>");
    follow_tester.addSignature("<java.lang.Integer: java.lang.String toString(int)>");
    follow_tester.addSignature("<java.lang.Long: java.lang.String toString(long)>");
    follow_tester.addSignature("<org.trifort.rootbeer.runtime.Sentinal: void <init>()>");
    follow_tester.addSignature("<org.trifort.rootbeer.runtimegpu.GpuException: void <init>()>");
    follow_tester.addSignature("<org.trifort.rootbeer.runtimegpu.GpuException: org.trifort.rootbeer.runtimegpu.GpuException arrayOutOfBounds(int,int,int)>");
    follow_tester.addSignature("<org.trifort.rootbeer.runtime.Serializer: void <init>(org.trifort.rootbeer.runtime.Memory,org.trifort.rootbeer.runtime.Memory)>");
    follow_tester.addSignature("<org.trifort.rootbeer.testcases.rootbeertest.serialization.CovarientTest: void <init>()>");
    RootbeerClassLoader.v().addFollowMethodTester(follow_tester);
    
    if(runtests){
      RootbeerClassLoader.v().addFollowClassTester(new TestCaseFollowTester());
    }
    
    if(Configuration.compilerInstance().getKeepMains()){
      MainTester main_tester = new MainTester();
      RootbeerClassLoader.v().addFollowMethodTester(main_tester);
    }    

    ListMethodTester dont_dfs_tester = new ListMethodTester();

    CompilerSetup setup = new CompilerSetup();
    for(String no_dfs : setup.getDontDfs()){
      dont_dfs_tester.addSignature(no_dfs);
    }
    RootbeerClassLoader.v().addDontFollowMethodTester(dont_dfs_tester);
    
    ForcedFields forced_fields = new ForcedFields();
    for(String field_sig : forced_fields.get()){
      RootbeerClassLoader.v().loadField(field_sig);
    }
    
    ListMethodTester to_sig_methods = new ListMethodTester();
    to_sig_methods.addSignature("<java.lang.Object: int hashCode()>");
    to_sig_methods.addSignature("<java.io.PrintStream: void println(java.lang.String)>");
    to_sig_methods.addSignature("<java.io.PrintStream: void println(int)>");
    to_sig_methods.addSignature("<java.io.PrintStream: void println(long)>");
    RootbeerClassLoader.v().addToSignaturesMethodTester(to_sig_methods);
    
    RootbeerClassLoader.v().addClassRemapping("java.util.concurrent.atomic.AtomicLong", "org.trifort.rootbeer.remap.GpuAtomicLong");
    RootbeerClassLoader.v().addClassRemapping("org.trifort.rootbeer.testcases.rootbeertest.remaptest.CallsPrivateMethod", "org.trifort.rootbeer.remap.DoesntCallPrivateMethod");
    
    RootbeerClassLoader.v().loadNecessaryClasses();
  }
  
  public void compile(String jar_filename, String outname, String test_case) throws Exception {
    TestCaseEntryPointDetector detector = new TestCaseEntryPointDetector(test_case);
    m_entryDetector = detector;
    CurrJarName jar_name = new CurrJarName();
    setupSoot(jar_filename, jar_name.get(), true);
    m_provider = detector.getProvider();
        
    List<SootMethod> kernel_methods = RootbeerClassLoader.v().getEntryPoints();
    compileForKernels(outname, kernel_methods, jar_filename);
  }
  
  public void compile(String jar_filename, String outname) throws Exception {
    compile(jar_filename, outname, false);
  }
  
  public void compile(String jar_filename, String outname, boolean run_tests) throws Exception {
    m_entryDetector = new KernelEntryPointDetector(run_tests);
    CurrJarName jar_name = new CurrJarName();
    setupSoot(jar_filename, jar_name.get(), run_tests);
    
    List<SootMethod> kernel_methods = RootbeerClassLoader.v().getEntryPoints();
    compileForKernels(outname, kernel_methods, jar_filename);
  }
  
  private void compileForKernels(String outname, List<SootMethod> kernel_methods, String jar_filename) throws Exception {
    
    if(kernel_methods.isEmpty()){
      System.out.println("There are no kernel classes. Please implement the following interface to use rootbeer:");
      System.out.println("org.trifort.runtime.Kernel");
      System.exit(0);
    }
       
    Transform2 transform2 = new Transform2();
    for(SootMethod kernel_method : kernel_methods){   
      
      System.out.println("running transform2 on: "+kernel_method.getSignature()+"...");
      RootbeerClassLoader.v().loadDfsInfo(kernel_method);
      DfsInfo dfs_info = RootbeerClassLoader.v().getDfsInfo();
      
      RootbeerDfs rootbeer_dfs = new RootbeerDfs();
      rootbeer_dfs.run(dfs_info);
      
      dfs_info.expandArrayTypes();
      dfs_info.finalizeTypes();

      SootClass soot_class = kernel_method.getDeclaringClass();
      transform2.run(soot_class.getName());
    }
    
    System.out.println("writing classes out...");
    
    Iterator<SootClass> iter = Scene.v().getClasses().iterator();
    while(iter.hasNext()){
      SootClass soot_class = iter.next();
      if(soot_class.isLibraryClass()){
        continue;
      }
      String class_name = soot_class.getName();
      boolean write = true;
      for(String runtime_class : m_runtimePackages){
        if(class_name.startsWith(runtime_class)){
          write = false;
          break;
        }
      }
      Iterator<SootClass> ifaces = soot_class.getInterfaces().iterator();
      while(ifaces.hasNext()){
        SootClass iface = ifaces.next();
        if(iface.getName().startsWith("org.trifort.rootbeer.test.")){
          write = false;
          break;
        }
      }
      if(write){
        writeJimpleFile(class_name);
        writeClassFile(class_name);
      }
    }
    
    makeOutJar(jar_filename);
    pack(outname);
  }
  
  public void pack(String outjar_name) throws Exception {
    Pack p = new Pack();
    String main_jar = RootbeerPaths.v().getOutputJarFolder() + File.separator + "partial-ret.jar";
    List<String> lib_jars = new ArrayList<String>();
    CurrJarName jar_name = new CurrJarName();
    lib_jars.add(jar_name.get());
    p.run(main_jar, lib_jars, outjar_name);
  }

  public void makeOutJar(String jar_filename) throws Exception {
    JarEntryHelp.mkdir(RootbeerPaths.v().getOutputJarFolder() + File.separator);
    String outfile = RootbeerPaths.v().getOutputJarFolder() + File.separator + "partial-ret.jar";

    ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outfile));
    addJarInputManifestFiles(zos, jar_filename);
    addOutputClassFiles(zos);
    addConfigurationFile(zos);
    zos.flush();
    zos.close();
  }
  
  private void addJarInputManifestFiles(ZipOutputStream zos, String jar_filename) throws Exception {
    ZipInputStream jin = new ZipInputStream(new FileInputStream(jar_filename));
    while(true){
      ZipEntry jar_entry = jin.getNextEntry();
      if(jar_entry == null){
        break;
      }
      if(jar_entry.getName().contains("META-INF")){
        writeFileToOutput(jin, jar_entry, zos);
      }
    }
    jin.close();
  }

  private void addOutputClassFiles(ZipOutputStream zos) throws Exception {
    List<File> output_class_files = getFiles(RootbeerPaths.v().getOutputClassFolder());
    for(File f : output_class_files){
      writeFileToOutput(f, zos, RootbeerPaths.v().getOutputClassFolder());
    }
  }
  
  private List<File> getFiles(String path) {
    File f = new File(path);
    List<File> ret = new ArrayList<File>();
    getFiles(ret, f);
    return ret;
  }
  
  private void getFiles(List<File> total_files, File dir){
    File[] files = dir.listFiles();
    for(File f : files){
      if(f.isDirectory()){
        getFiles(total_files, f);
      } else {
        total_files.add(f);
      }
    }
  }

  private String makeJarFileName(File f, String folder) {
    try {
      String abs_path = f.getAbsolutePath();
      if(f.isDirectory()){
        abs_path += File.separator; 
      }
      folder += File.separator;
      folder = folder.replace("\\", "\\\\");
      String[] tokens = abs_path.split(folder);
      String ret = tokens[1];
      if(File.separator.equals("\\")){
        ret = ret.replace("\\", "/");
      }
      return ret;
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }

  private void addConfigurationFile(ZipOutputStream zos) throws IOException {
    String folder_name = "org/trifort/rootbeer/runtime/";
    String name = folder_name + "config.txt";
    ZipEntry entry = new ZipEntry(name);
    entry.setSize(1);
    byte[] contents = new byte[2];
    contents[0] = (byte) Configuration.compilerInstance().getMode();
    if(Configuration.compilerInstance().getExceptions()){
      contents[1] = (byte) 1;
    } else {
      contents[1] = (byte) 0;
    }
    
    entry.setCrc(calcCrc32(contents));
    zos.putNextEntry(entry);
    zos.write(contents);
    zos.flush();
    
    File file = new File(RootbeerPaths.v().getOutputClassFolder()+File.separator+folder_name);
    if(file.exists() == false){
      file.mkdirs();
    }
    
    FileOutputStream fout = new FileOutputStream(RootbeerPaths.v().getOutputClassFolder()+File.separator+name);
    fout.write(contents);
    fout.flush();
    fout.close();
  }
  
  private void writeFileToOutput(ZipInputStream jin, ZipEntry jar_entry, ZipOutputStream zos) throws Exception {
    if(jar_entry.isDirectory() == false){
      
      List<byte[]> buffered = new ArrayList<byte[]>();
      int total_size = 0;
      while(true){
        byte[] buffer = new byte[4096];
        int len = jin.read(buffer);
        if(len == -1){
          break;
        }
        total_size += len;
        byte[] truncated = new byte[len];
        for(int i = 0; i < len; ++i){
          truncated[i] = buffer[i];
        }
        buffered.add(truncated);
      }

      ZipEntry entry = new ZipEntry(jar_entry.getName());
      entry.setSize(total_size);
      entry.setCrc(jar_entry.getCrc());
      zos.putNextEntry(entry);

      for(byte[] buffer : buffered){
        zos.write(buffer);
      }
      
      zos.flush();
    } else {
      zos.putNextEntry(jar_entry);
    }
  }
  
  private void writeFileToOutput(File f, ZipOutputStream zos, String folder) throws Exception {
    String name = makeJarFileName(f, folder);
    ZipEntry entry = new ZipEntry(name);
    byte[] contents = readFile(f);
    entry.setSize(contents.length);

    entry.setCrc(calcCrc32(contents));
    zos.putNextEntry(entry);

    int wrote_len = 0;
    int total_len = contents.length;
    while(wrote_len < total_len){
      int len = 4096;
      int len_left = total_len - wrote_len;
      if(len > len_left)
        len = len_left;
      zos.write(contents, wrote_len, len);
      wrote_len += len;
    }
    zos.flush();
  }

  private long calcCrc32(byte[] buffer){
    CRC32 crc = new CRC32();
    crc.update(buffer);
    return crc.getValue();
  }

  private byte[] readFile(File f) throws Exception {
    List<Byte> contents = new ArrayList<Byte>();
    byte[] buffer = new byte[4096];
    FileInputStream fin = new FileInputStream(f);
    while(true){
      int len = fin.read(buffer);
      if(len == -1)
        break;
      for(int i = 0; i < len; ++i){
        contents.add(buffer[i]);
      }
    }
    fin.close();
    byte[] ret = new byte[contents.size()];
    for(int i = 0; i < contents.size(); ++i)
      ret[i] = contents.get(i);
    return ret;
  }

  private void writeJimpleFile(String cls){  
    try {
      SootClass c = Scene.v().getSootClass(cls);
      JimpleWriter writer = new JimpleWriter();
      writer.write(classNameToFileName(cls, true), c);
    } catch(Exception ex){
      System.out.println("Error writing .jimple: "+cls);
    }   
  }
  
  private void writeClassFile(String cls, String filename){
    FileOutputStream fos = null;
    OutputStream out1 = null;
    PrintWriter writer = null;
    SootClass c = Scene.v().getSootClass(cls);
    List<String> before_sigs = getMethodSignatures(c);
    try {
      fos = new FileOutputStream(filename);
      out1 = new JasminOutputStream(fos);
      writer = new PrintWriter(new OutputStreamWriter(out1));
      new soot.jimple.JasminClass(c).print(writer);
    } catch(Exception ex){
      System.out.println("Error writing .class: "+cls);
      ex.printStackTrace(System.out);
      List<String> after_sigs = getMethodSignatures(c);
      System.out.println("Before sigs: ");
      printMethodSigs(before_sigs);
      System.out.println("After sigs: ");
      printMethodSigs(after_sigs);
    } finally { 
      try {
        writer.flush();
        writer.close();
        out1.close();
        fos.close(); 
      } catch(Exception ex){ 
        ex.printStackTrace();
      }
    }
  }
  
  private List<String> getMethodSignatures(SootClass c){
    List<String> ret = new ArrayList<String>();
    List<SootMethod> methods = c.getMethods();
    for(SootMethod method : methods){
      ret.add(method.getSignature());
    }
    return ret;
  }
  
  private void printMethodSigs(List<String> sigs){
    for(String sig : sigs){
      System.out.println("  "+sig);
    }
  }
  
  private void writeClassFile(String cls) {
    writeClassFile(cls, classNameToFileName(cls, false));
  }
  
  private String classNameToFileName(String cls, boolean jimple){
    File f;
    if(jimple)
      f = new File(m_jimpleOutputFolder);
    else
      f = new File(m_classOutputFolder);
    
    cls = cls.replace(".", File.separator);
    
    if(jimple)
      cls += ".jimple";
    else
      cls += ".class";
    
    cls = f.getAbsolutePath()+File.separator + cls;
    
    File f2 = new File(cls);
    String folder = f2.getParent();
    new File(folder).mkdirs();
    
    return cls;
  }
  
  private void clearOutputFolders() {
    DeleteFolder deleter = new DeleteFolder();
    deleter.delete(RootbeerPaths.v().getOutputJarFolder());
    deleter.delete(RootbeerPaths.v().getOutputClassFolder());
    deleter.delete(RootbeerPaths.v().getOutputShimpleFolder());
    deleter.delete(RootbeerPaths.v().getJarContentsFolder());
  }

  public String getProvider() {
    return m_provider;
  }
}
