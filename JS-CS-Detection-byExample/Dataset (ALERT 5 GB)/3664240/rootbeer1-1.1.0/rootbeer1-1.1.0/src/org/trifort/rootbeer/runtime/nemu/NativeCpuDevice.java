/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.runtime.nemu;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.trifort.rootbeer.configuration.RootbeerPaths;
import org.trifort.rootbeer.runtime.BlockShaper;
import org.trifort.rootbeer.runtime.CompiledKernel;
import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.Memory_old;
import org.trifort.rootbeer.runtime.Serializer;
import org.trifort.rootbeer.runtime.ThreadConfig;
import org.trifort.rootbeer.util.ResourceReader;
import org.trifort.rootbeer.util.WindowsCompile;


public class NativeCpuDevice {
  
  private List<CompiledKernel> m_Blocks;
  private boolean m_nativeCpuInitialized;
  private BlockShaper m_blockShaper;
  
  public NativeCpuDevice(){
    m_nativeCpuInitialized = false;
    m_blockShaper = new BlockShaper();
  }
  
  public long getMaxEnqueueSize() {
    return 1024*1024*1024;
  }

  public void flushQueue() {
    
  }
  
  public void run(Kernel kernel_template, ThreadConfig thread_config){
    /*
    int block_shape = thread_config.getGridShapeX();
    int thread_shape = thread_config.getBlockShapeX();
    int num_threads = thread_config.getNumThreads();
    NativeCpuGcHeap heap = new NativeCpuGcHeap(this);
    int size = heap.writeRuntimeBasicBlock(kernel_template, num_threads);
    m_Blocks = heap.getBlocks();
    
    List<Memory> mems = heap.getMemory();    
    String lib_name = compileNativeCpuDev();
    BasicMemory to_space = (BasicMemory) mems.get(0);
    BasicMemory handles = (BasicMemory) mems.get(1);
    BasicMemory heap_end_ptr = (BasicMemory) mems.get(2);
    BasicMemory gc_info = (BasicMemory) mems.get(3);
    BasicMemory exceptions = (BasicMemory) mems.get(4);
    
    Serializer serializer = heap.getSerializer();
    runOnCpu(to_space.getBuffer(), to_space.getBuffer().size(), 
      handles.getBuffer().get(0), heap_end_ptr.getBuffer().get(0),
      gc_info.getBuffer().get(0), exceptions.getBuffer().get(0), 
      serializer.getClassRefArray(), num_threads, block_shape, thread_shape, 
      lib_name);
    
    heap.readRuntimeBasicBlock(kernel_template, num_threads);
    */
  }
  
  private native void runOnCpu(List<byte[]> to_space, int to_space_size, 
    byte[] handles, byte[] heap_end_ptr, byte[] gc_info, byte[] exceptions, 
    int[] java_lang_class_refs, int num_threads, int block_shape, 
    int thread_shape, String library_name);

  public long getMaxMemoryAllocSize() {
    return 1024*1024*1024;
  }
  
  private void extractFromNative(String filename, String nemu) throws Exception {
    String str = ResourceReader.getResource("/org/trifort/rootbeer/runtime2/native/"+filename);
    PrintWriter writer = new PrintWriter(nemu+filename);
    writer.println(str);
    writer.flush();
    writer.close();
  }
  
  private String compileMac(File nemu_file) throws Exception {
    String nemu = nemu_file.getAbsolutePath()+File.separator;
    
    String name = "libnemu";
    
    int status;
    String cmd;
    Process p;
    
    String cflags = "-fno-common -Os -arch i386 -arch x86_64 -c";
    
    if(m_nativeCpuInitialized == false){
      cmd = "llvm-gcc "+cflags+" -I/System/Library/Frameworks/JavaVM.framework/Versions/A/Headers "+nemu+"NativeCpuDevice.c -o "+nemu+"NativeCpuDevice.o"; 
      p = Runtime.getRuntime().exec(cmd, null, nemu_file);
      status = p.waitFor();
      if(status != 0){
        System.out.println("Compilation failure!");
        System.out.println(cmd);
        System.exit(-1);
      }
      p.destroy();
    }
    
    cmd = "llvm-gcc "+cflags+" -lpthread "+nemu+"generated.c -o "+nemu+"generated.o";
    p = Runtime.getRuntime().exec(cmd, null, nemu_file);
    status = p.waitFor();
    if(status != 0){
      System.out.println("Compilation failure!");
      System.out.println(cmd);
      System.exit(-1);
    }
    p.destroy();
    
    String ldflags = "-arch i386 -arch x86_64 -dynamiclib";
    
    if(m_nativeCpuInitialized == false){
      cmd = "llvm-gcc "+ldflags+" -o "+nemu+"nativecpudev.dylib -dylib "+nemu+"NativeCpuDevice.o -lc";
      p = Runtime.getRuntime().exec(cmd, null, nemu_file);
      status = p.waitFor();
      if(status != 0){
        System.out.println("Compilation failure!");
        System.out.println(cmd);
        System.exit(-1);
      }
      p.destroy();
    }
    
    cmd = "llvm-gcc "+ldflags+" -o "+nemu+name+".dylib -dylib "+nemu+"generated.o -lc";
    p = Runtime.getRuntime().exec(cmd, null, nemu_file);
    status = p.waitFor();
    if(status != 0){
      System.out.println("Compilation failure!");
      System.out.println(cmd);
      System.exit(-1);
    }
    p.destroy();

    if(m_nativeCpuInitialized == false){
      File f1 = new File(nemu+"nativecpudev.dylib");
      System.load(f1.getAbsolutePath());  
      m_nativeCpuInitialized = true;
    }

    File f2 = new File(nemu+name+".dylib");
    return f2.getAbsolutePath();
  }
  
  private String compileLinux(File nemu_file) throws Exception {
    String nemu = nemu_file.getAbsolutePath()+File.separator;

    String name = "libnemu";

    int status;
    String cmd;
    Process p;
    
    if(m_nativeCpuInitialized == false){
      cmd = "gcc -ggdb -Wall -fPIC -g -c -I/usr/lib/jvm/default-java/include/ -I/usr/lib/jvm/default-java/include/linux "+nemu+"NativeCpuDevice.c -o "+nemu+"NativeCpuDevice.o";
      p = Runtime.getRuntime().exec(cmd, null, nemu_file);
      status = p.waitFor();
      if(status != 0){
        System.out.println("Compilation failure!");
        System.out.println(cmd);
        System.exit(-1);
      }
      p.destroy();
    }

    cmd = "gcc -ggdb -fPIC -Wall -g -c -lpthread "+nemu+"generated.c -o "+nemu+"generated.o";
    p = Runtime.getRuntime().exec(cmd, null, nemu_file);
    status = p.waitFor();
    if(status != 0){
      System.out.println("Compilation failure!");
      System.out.println(cmd);
      System.exit(-1);
    }
    p.destroy();

    cmd = "gcc -shared -Wl,-soname,"+name+" -o "+nemu+name+".so.1 "+nemu+"generated.o -lc";
    p = Runtime.getRuntime().exec(cmd, null, nemu_file);
    status = p.waitFor();
    if(status != 0){
      System.out.println("Compilation failure!");
      System.out.println(cmd);
      System.exit(-1);
    }
    p.destroy();

    if(m_nativeCpuInitialized == false){
      cmd = "gcc -shared -Wl,-soname,nativecpudev -o "+nemu+"nativecpudev.so.1 "+nemu+"NativeCpuDevice.o "+nemu+"generated.o -lc";
      p = Runtime.getRuntime().exec(cmd, null, nemu_file);
      status = p.waitFor();
      if(status != 0){
        System.out.println("Compilation failure!");
        System.out.println(cmd);
        System.exit(-1);
      }
      p.destroy();
    }

    if(m_nativeCpuInitialized == false){
      File f1 = new File(nemu+"nativecpudev.so.1");
      System.load(f1.getAbsolutePath());  
      m_nativeCpuInitialized = true;
    }

    File f2 = new File(nemu+name+".so.1");
    return f2.getAbsolutePath();
  }
  
  private String compileWindows(File nemu_file){
    String nemu = nemu_file.getAbsolutePath()+File.separator;
    String name = "libnemu";
    
    WindowsCompile compiler = new WindowsCompile();
    String jdk_path = compiler.jdkPath();
  
    if(m_nativeCpuInitialized == false){
      windowsCompile("cl /I\""+jdk_path+"\\include\" /I\""+jdk_path+"\\include\\win32\" "+nemu+"NativeCpuDevice.c /link /DLL /OUT:\""+nemu+"nativecpudevice.dll\" /MACHINE:X64");
    }
    
    windowsCompile("cl /I\""+jdk_path+"\\include\" /I\""+jdk_path+"\\include\\win32\" "+nemu+"generated.c /link /DLL /OUT:\""+nemu+"libnemu.dll\" /MACHINE:X64");
  
    if(m_nativeCpuInitialized == false){
      File f1 = new File(nemu+"nativecpudevice.dll");
      System.load(f1.getAbsolutePath());
      m_nativeCpuInitialized = true;
    }
    
    File f2 = new File(nemu+name+".dll");
    return f2.getAbsolutePath();
  }
  
  private void windowsCompile(String cmd){
    boolean arch64 = true;
    String arch_str = System.getProperty("os.arch");
    if(arch_str == null || arch_str.equals("x86")){
      arch64 = false; 
    }
    WindowsCompile compiler = new WindowsCompile();
    List<String> errors = compiler.compile(cmd, arch64);
    if(errors.isEmpty() == false){
      System.out.println("compilation failed!");
      for(String error : errors){
        System.out.println(error);
      }
      System.exit(-1);
    }
  }

  private String compileNativeCpuDev() {
    try {
      String code = "";
      if ("Mac OS X".equals(System.getProperty("os.name"))){
        code = m_Blocks.get(0).getCodeUnix();
      } else if(File.separator.equals("/")){
        code = m_Blocks.get(0).getCodeUnix();
      } else { 
        code = m_Blocks.get(0).getCodeWindows();
      }    
      
      File nemu_file = new File(RootbeerPaths.v().getRootbeerHome()+"nemu");
      if(nemu_file.exists() == false){
        nemu_file.mkdirs();  
      }
      
      String nemu = nemu_file.getAbsolutePath()+File.separator;
      extractFromNative("NativeCpuDevice.c", nemu);
      extractFromNative("org_trifort_rootbeer_runtime_nativecpu_NativeCpuDevice.h", nemu);
      
      PrintWriter writer = new PrintWriter(nemu+"generated.c");
      writer.println(code);
      writer.flush();
      writer.close();
      
      if ("Mac OS X".equals(System.getProperty("os.name"))){
        return compileMac(nemu_file); 
      } else if(File.separator.equals("/")){
        return compileLinux(nemu_file);
      } else { 
        return compileWindows(nemu_file);
      }      
    } catch(Exception ex){
      ex.printStackTrace();
      System.exit(0);
      return null;
    }
  }

  public long getGlobalMemSize() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public long getNumBlocks() {
    return 1024*1024*1024;
  }
  
}
