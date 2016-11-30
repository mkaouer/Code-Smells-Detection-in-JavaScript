/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer;

import edu.syr.pcpratts.rootbeer.classloader.FastWholeProgram;
import edu.syr.pcpratts.rootbeer.compiler.*;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.CudaTweaks;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.NativeCpuTweaks;
import edu.syr.pcpratts.rootbeer.generate.opencl.tweaks.Tweaks;
import edu.syr.pcpratts.rootbeer.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import pack.Pack;
import soot.*;
import soot.options.Options;
import soot.util.JasminOutputStream;

public class RootbeerCompiler {

  private String m_classOutputFolder;
  private String m_jimpleOutputFolder;
  private FastWholeProgram m_fastLoader;
  
  public RootbeerCompiler(){
    clearOutputFolders();
    
    m_classOutputFolder = Constants.OUTPUT_CLASS_FOLDER;
    m_jimpleOutputFolder = "output-jimple";
    
    if(Configuration.compilerInstance().getMode() == Configuration.MODE_GPU){      
      Tweaks.setInstance(new CudaTweaks());
    } else {
      Tweaks.setInstance(new NativeCpuTweaks());
    }
    
    m_fastLoader = FastWholeProgram.v();
  }
  
  public void compile(String main_jar, List<String> lib_jars, List<String> dirs, String dest_jar) {
    
  }
    
  private List<String> getRuntimeJars(){
    List<String> ret = new ArrayList<String>();
    String s = File.separator;
    if(System.getProperty("os.name").equals("Mac OS X")) {
	    //in Mac OS X, rt.jar is split into classes.jar and ui.jar
      ret.add(System.getProperty("java.home")+s+".."+s+"Classes"+s+"classes.jar");
      ret.add(System.getProperty("java.home")+s+".."+s+"Classes"+s+"ui.jar");
	  } else {
      //if windows or linux
      ret.add(System.getProperty("java.home")+s+"lib"+s+"rt.jar");
      ret.add(System.getProperty("java.home")+s+"lib"+s+"jce.jar");
      ret.add(System.getProperty("java.home")+s+"lib"+s+"charsets.jar");
      ret.add(System.getProperty("java.home")+s+"lib"+s+"jsse.jar");
    }
    return ret;
  }  
    
  public void compile(String jar_filename, String outname, String test_case) throws Exception {
    Options.v().set_allow_phantom_refs(true);
    
    m_fastLoader.addPath(jar_filename);
    m_fastLoader.addClassPath(getRuntimeJars());
    m_fastLoader.init();
    m_fastLoader.singleKernel();
    
    FindKernelForTestCase finder = new FindKernelForTestCase();
    String kernel = finder.get(test_case, m_fastLoader.getApplicationClasses());
    m_fastLoader.findKernelClasses(kernel); 
    
    List<String> kernel_classes = new ArrayList<String>();
    kernel_classes.add(kernel);
    
    compileForKernels(outname, kernel_classes);
  }
  
  public void compile(String jar_filename, String outname) throws Exception {
    Options.v().set_allow_phantom_refs(true);
    
    m_fastLoader.addPath(jar_filename);
    m_fastLoader.addClassPath(getRuntimeJars());
    m_fastLoader.init();
    
    List<String> kernel_classes = m_fastLoader.findKernelClasses();    
    compileForKernels(outname, kernel_classes);
  }
  
  private void compileForKernels(String outname, List<String> kernel_classes) throws Exception {
    
    String[] sorted = new String[kernel_classes.size()];
    sorted = kernel_classes.toArray(sorted);
    Arrays.sort(sorted);
    kernel_classes.clear();
    for(String cls : sorted){
      kernel_classes.add(cls);
    }
    
    Map<String, List<String>> reachables = new HashMap<String, List<String>>();
    List<String> all_reachables = new ArrayList<String>();
    
    for(String kernel : kernel_classes){
      SootClass soot_class = Scene.v().getSootClass(kernel);
      SootMethod kernel_method = soot_class.getMethod("void gpuMethod()");
      FastWholeProgram.v().getDfsMethods(kernel_method);
      
      System.out.println("finding kernel reachable methods for: "+soot_class.getShortName()+"...");
    
      KernelReachableMethods reachable_finder = new KernelReachableMethods();
      List<String> one_class = new ArrayList<String>();
      one_class.add(kernel);
      List<String> curr_reachables = reachable_finder.get(one_class);
    
      all_reachables.addAll(curr_reachables);
      reachables.put(kernel, curr_reachables);
    }
      
    ClassRemappingTransform transform = null;
    
    if(!Main.disable_class_remapping()){
      System.out.println("remapping some classes to GPU versions...");
      transform = new ClassRemappingTransform(false);
      transform.run(all_reachables);
      transform.finishClone();
    }
    
    Transform2 transform2 = new Transform2();
    for(String cls : kernel_classes){
      List<String> curr_reachables = reachables.get(cls);
      RootbeerScene.v().setReachableMethods(curr_reachables);
      
      SootClass soot_class = Scene.v().getSootClass(cls);
      SootMethod kernel_method = soot_class.getMethod("void gpuMethod()");
      FastWholeProgram.v().getDfsMethods(kernel_method);
      
      transform2.run(cls);
    }
    
    Set<String> app_classes = new HashSet<String>();
    
    if(!Main.disable_class_remapping()){
      app_classes.addAll(transform.getModifiedClasses());
    }
    
    SignatureUtil util = new SignatureUtil();
    for(String method_sig : all_reachables){
      String class_name = util.classFromMethodSig(method_sig);
      if(app_classes.contains(class_name) == false){
        app_classes.add(class_name);
      }
    }
    
    for(String cls : app_classes){
      loadAllMethods(cls);
      writeClassFile(cls);
      writeJimpleFile(cls);
    }
    
    List<String> added_classes = RootbeerScene.v().getAddedClasses();
    for(String cls : added_classes){
      loadAllMethods(cls);
      writeClassFile(cls);
      writeJimpleFile(cls);
    }    
    
    makeOutJar();
    pack(outname);
  }
  
  public void pack(String outjar_name) throws Exception {
    Pack p = new Pack();
    String main_jar = Constants.OUTPUT_JAR_FOLDER + File.separator + "partial-ret.jar";
    List<String> lib_jars = new ArrayList<String>();
    CurrJarName jar_name = new CurrJarName();
    lib_jars.add(jar_name.get());
    p.run(main_jar, lib_jars, outjar_name);
  }

  public void makeOutJar() throws Exception {
    JarEntryHelp.mkdir(Constants.OUTPUT_JAR_FOLDER + File.separator);
    String outfile = Constants.OUTPUT_JAR_FOLDER + File.separator + "partial-ret.jar";

    ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outfile));
    addJarInputManifestFiles(zos);
    addOutputClassFiles(zos);
    addConfigurationFile(zos);
    zos.flush();
    zos.close();
  }
  
  private void addJarInputManifestFiles(ZipOutputStream zos) throws Exception {
    List<File> jar_input_files = getFiles(Constants.JAR_CONTENTS_FOLDER);
    for(File f : jar_input_files){
      if(f.getPath().contains("META-INF")){
        writeFileToOutput(f, zos, Constants.JAR_CONTENTS_FOLDER);
      }
    }
  }

  private void addOutputClassFiles(ZipOutputStream zos) throws Exception {
    List<File> output_class_files = getFiles(Constants.OUTPUT_CLASS_FOLDER);
    for(File f : output_class_files){
      writeFileToOutput(f, zos, Constants.OUTPUT_CLASS_FOLDER);
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
      String s = File.separator;
      if(s.equals("\\"))
        s = "\\\\";
      String[] tokens = abs_path.split(folder+s);
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
    String name = "edu/syr/pcpratts/rootbeer/runtime/config.txt";
    ZipEntry entry = new ZipEntry(name);
    entry.setSize(1);
    byte[] contents = new byte[1];
    contents[0] = (byte) Configuration.compilerInstance().getMode();
    
    entry.setCrc(calcCrc32(contents));
    zos.putNextEntry(entry);
    zos.write(contents);
    zos.flush();
    
    FileOutputStream fout = new FileOutputStream(Constants.OUTPUT_CLASS_FOLDER+File.separator+name);
    fout.write(contents);
    fout.flush();
    fout.close();
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
    if(cls.equals("java.lang.Object"))
      return;
    try {
      SootClass c = Scene.v().getSootClass(cls);
      JimpleWriter writer = new JimpleWriter();
      writer.write(classNameToFileName(cls, true), c);
    } catch(Exception ex){
      System.out.println("Error writing .jimple: "+cls);
    }   
  }
  
  private void writeClassFile(String cls, String filename){
    if(cls.equals("java.lang.Object"))
      return;
    FileOutputStream fos = null;
    PrintWriter writer = null;
    SootClass c = Scene.v().getSootClass(cls);
    try {
      fos = new FileOutputStream(filename);
      OutputStream out1 = new JasminOutputStream(fos);
      writer = new PrintWriter(new OutputStreamWriter(out1));
      new soot.jimple.JasminClass(c).print(writer);
    } catch(Exception ex){
      System.out.println("Error writing .class: "+cls);
      if(cls.equals("java.lang.Object") == false){
        ex.printStackTrace();
        PrintWriter writer2 = new PrintWriter(System.out);
        try {
          List<SootMethod> methods = c.getMethods();
          for(SootMethod method : methods){
            if(method.hasActiveBody()){
              System.out.println(method.getSignature());
              Body body = method.getActiveBody();
              Printer.v().printTo(body, writer2);
              writer2.flush();
              System.out.flush();
            }
          }
        } catch(Exception ex2){
          ex2.printStackTrace(); 
        }
      }
    } finally { 
      writer.flush();
      writer.close();
      try {
        fos.close(); 
      } catch(Exception ex){ }
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
  
  private void copyClass(String cls) {
    String dest = classNameToFileName(cls, false);

    String src = cls.replace(".", File.separator);
    src += ".class";
    File f = new File(Constants.JAR_CONTENTS_FOLDER);
    src = f.getAbsolutePath() + File.separator + src;

    copyFile(dest, src);
  }
  
  private void copyFile(String dest, String src) {
    try {
      InputStream is = new FileInputStream(src);
      OutputStream os = new FileOutputStream(dest);
      while(true){
        byte[] buffer = new byte[1024];
        int len = is.read(buffer);
        if(len == -1)
          break;
        os.write(buffer, 0, len);
      }
      os.flush();
      os.close();
      is.close();
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
    
  private String remapFilename(String cls) {
    File f = new File("src");
    
    cls = cls.replace(".", File.separator);
    cls += ".class";
    
    cls = f.getAbsolutePath()+File.separator + cls;
    
    File f2 = new File(cls);
    String folder = f2.getParent();
    new File(folder).mkdirs();
    
    return cls;
  }

  private void loadAllMethods(String cls) {
    SootClass soot_class = Scene.v().getSootClass(cls);
    List<SootMethod> methods = soot_class.getMethods();
    for(SootMethod method : methods){
      if(method.isConcrete()){
        m_fastLoader.loadToBodyLater(method.getSignature());
        Body body = method.retrieveActiveBody();
        SpecialInvokeFixup fixup = new SpecialInvokeFixup();
        method.setActiveBody(fixup.fixup(body));
      }
    }
  }
  
  private void clearOutputFolders() {
    DeleteFolder deleter = new DeleteFolder();
    deleter.delete(Constants.OUTPUT_JAR_FOLDER);
    deleter.delete(Constants.OUTPUT_CLASS_FOLDER);
    deleter.delete(Constants.OUTPUT_SHIMPLE_FOLDER);
  }
}
