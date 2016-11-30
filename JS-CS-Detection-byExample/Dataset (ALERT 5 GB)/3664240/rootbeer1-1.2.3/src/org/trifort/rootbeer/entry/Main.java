/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.entry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.configuration.Configuration;
import org.trifort.rootbeer.generate.opencl.tweaks.GencodeOptions.CompileArchitecture;
import org.trifort.rootbeer.generate.opencl.tweaks.GencodeOptions.ComputeCapability;
import org.trifort.rootbeer.runtime.CUDALoader;
import org.trifort.rootbeer.runtime.GpuDevice;
import org.trifort.rootbeer.runtime.Rootbeer;

public class Main {
  
  public int m_mode;
  private int m_num_args;
  private boolean m_runTests;
  private boolean m_runHardTests;
  private boolean m_disableClassRemapping;
  private String m_testCase;
  private boolean m_simpleCompile;
  private static boolean m_printDeviceInfo = false;
  private static boolean m_largeMemTests = false;
  private String m_mainJar;
  private List<String> m_libJars;
  private List<String> m_directories;
  private String m_destJar;
  
  public Main(){
    m_libJars = new ArrayList<String>();
    m_directories = new ArrayList<String>();
    m_mode = Configuration.MODE_GPU;
    m_simpleCompile = false;
    m_runHardTests = false;
  }
  
  public static boolean largeMemTests(){
    return m_largeMemTests;
  }
  
  private void parseArgs(String[] args) {
    m_num_args = args.length;
    
    boolean arch32bit = false;
    boolean arch64bit = false;
    for(int i = 0; i < args.length; ++i){
      String arg = args[i];
      if(arg.equals("-nemu")){
        m_mode = Configuration.MODE_NEMU;
      } else if(arg.equals("-jemu")){
        m_mode = Configuration.MODE_JEMU;
      } else if(arg.equals("-remap-sparse")){ 
        Configuration.compilerInstance().setRemapSparse();
      } else if(arg.equals("-mainjar")){
        m_mainJar = safeGet(args, i+1, "-mainjar");
        ++i;
      } else if(arg.equals("-libjar")){
        String lib = safeGet(args, i+1, "-libjar");
        m_libJars.add(lib);
        ++i;
      } else if(arg.equals("-directory")){
        String dir = safeGet(args, i+1, "-directory");
        m_directories.add(dir);
        ++i;
      } else if(arg.equals("-destjar")){
        m_destJar = safeGet(args, i+1, "-destjar");
        ++i;
      } else if(arg.equals("-runtests")){
        m_runTests = true;
        m_testCase = null;
        m_runHardTests = true;
      } else if(arg.equals("-runeasytests")){
        m_runTests = true;
        m_testCase = null;
        m_runHardTests = false;
      } else if(arg.equals("-runtest")){
        m_runTests = true;
        m_testCase = safeGet(args, i+1, "-runtest");
        m_runHardTests = true;
        ++i;
      } else if(arg.equals("-printdeviceinfo")){
        m_printDeviceInfo = true;
      } else if(arg.equals("-disable-class-remapping")){
        m_disableClassRemapping = true;
      } else if(arg.equals("-large-mem-tests")){
        m_largeMemTests = true;
      } else if(arg.equals("-maxrregcount")){
        String count = safeGet(args, i+1, "-maxrregcount");
        ++i;
        Configuration.compilerInstance().setMaxRegCount(Integer.parseInt(count));
      } else if(arg.equals("-noarraychecks")){
        Configuration.compilerInstance().setArrayChecks(false);
      } else if(arg.equals("-nodoubles")){
        Configuration.compilerInstance().setDoubles(false);
      } else if(arg.equals("-norecursion")){
        Configuration.compilerInstance().setRecursion(false);
      } else if(arg.equals("-noexceptions")){
        Configuration.compilerInstance().setExceptions(false);
      } else if(arg.equals("-keepmains")){
        Configuration.compilerInstance().setKeepMains(true);
      } else if(arg.equals("-shared-mem-size")){
        String size = safeGet(args, i+1, "-shared-mem-size");
        ++i;
        int int_size = Integer.parseInt(size);
        Configuration.compilerInstance().setSharedMemSize(int_size);
      } else if(arg.equals("-32bit")) {
        arch32bit = true;
      } else if(arg.equals("-64bit")) {
        arch64bit = true;
      } else if(arg.equals("-manualcuda")){
        String filename = safeGet(args, i+1, "-manualcuda");
        ++i;
        Configuration.compilerInstance().setManualCuda();
        Configuration.compilerInstance().setManualCudaFilename(filename);
      } else if(arg.equals("-computecapability")){
        String computeCapability = safeGet(args, i+1, "-computecapability");
        ++i;
        if (computeCapability.equalsIgnoreCase("sm_11")) {
          Configuration.compilerInstance().setComputeCapability(ComputeCapability.SM_11);
        } else if (computeCapability.equalsIgnoreCase("sm_12")) {
          Configuration.compilerInstance().setComputeCapability(ComputeCapability.SM_12);
        } else if (computeCapability.equalsIgnoreCase("sm_20")) {
          Configuration.compilerInstance().setComputeCapability(ComputeCapability.SM_20);
        } else if (computeCapability.equalsIgnoreCase("sm_21")) {
          Configuration.compilerInstance().setComputeCapability(ComputeCapability.SM_21);
        } else if (computeCapability.equalsIgnoreCase("sm_30")) {
          Configuration.compilerInstance().setComputeCapability(ComputeCapability.SM_30);
        } else if (computeCapability.equalsIgnoreCase("sm_35")) {
          Configuration.compilerInstance().setComputeCapability(ComputeCapability.SM_35);
        } else {
          System.out.println("Unsupported compute capability: "+ computeCapability);
        }
      } else if(m_simpleCompile == false){      
        m_mainJar = arg;
        m_destJar = safeGet(args, i+1, arg);
        
        File main_jar = new File(m_mainJar);
        if(main_jar.exists() == false){
          System.out.println("Cannot find: "+m_mainJar);
          System.exit(0);
        }
        
        ++i;
        m_simpleCompile = true;
      }
    }
    
    if(Configuration.compilerInstance().getRecursion() && m_printDeviceInfo == false){
      System.out.println("warning: sm_12 and sm_11 not supported with recursion. use -norecursion to enable.");
    }
    
    if(Configuration.compilerInstance().getDoubles() && m_printDeviceInfo == false){
      System.out.println("warning: sm_12 and sm_11 not supported with doubles. use -nodoubles to enable.");
    }

    if(arch32bit && !arch64bit) {
      Configuration.compilerInstance().setCompileArchitecture(CompileArchitecture.Arch32bit);
    } else if(!arch32bit && arch64bit) {
      Configuration.compilerInstance().setCompileArchitecture(CompileArchitecture.Arch64bit);
    } else {
      Configuration.compilerInstance().setCompileArchitecture(CompileArchitecture.Arch32bit64bit);
    }
    
    Configuration.compilerInstance().setMode(m_mode);
  }
  
  private String safeGet(String[] args, int index, String argname) {
    if(index >= args.length){
      System.out.println(argname+" needs another argument after it.");
      System.exit(-1);
    }
    return args[index];
  }

  private void run() {
    // Now we have loaded the dll's if we need to print the device details to it
    if(m_printDeviceInfo){
      if(m_num_args == 1){
        printDeviceInfo();
      } else {
        System.out.println("-printdeviceinfo can only be used by itself. Remove other arguments.");  
        System.out.flush();
        return;
      }
    }
    
    if(m_runTests){
      RootbeerTest test = new RootbeerTest();
      test.runTests(m_testCase, m_runHardTests);
      return;
    } 
    
    RootbeerCompiler compiler = new RootbeerCompiler();
    if(m_disableClassRemapping){
      compiler.disableClassRemapping(); 
    }
    
    if(m_simpleCompile){
      try {
        compiler.compile(m_mainJar, m_destJar);
      } catch(Exception ex){
        ex.printStackTrace();
      }
    } else {
      try {
        compiler.compile(m_mainJar, m_libJars, m_directories, m_destJar);
      } catch(Exception ex){
        ex.printStackTrace();
      }
    }
  }
  
  private void printDeviceInfo() {
    Rootbeer rootbeer = new Rootbeer();
    List<GpuDevice> devices = rootbeer.getDevices();
    System.out.println("device count: "+devices.size());
    for(GpuDevice device : devices){
      System.out.println("device: "+device.getDeviceName());
      System.out.println("  compute_capability: "+device.getMajorVersion()+"."+device.getMinorVersion());
      System.out.println("  total_global_memory: "+device.getTotalGlobalMemoryBytes()+" bytes");
      System.out.println("  max_shared_memory_per_block: "+device.getMaxSharedMemoryPerBlock()+" bytes");
      System.out.println("  num_multiprocessors: "+device.getMultiProcessorCount());
      System.out.println("  clock_rate: "+device.getClockRateHz()+" Hz");
      System.out.println("  max_block_dim_x: "+device.getMaxBlockDimX());
      System.out.println("  max_block_dim_y: "+device.getMaxBlockDimY());
      System.out.println("  max_block_dim_z: "+device.getMaxBlockDimZ());
      System.out.println("  max_grid_dim_x: "+device.getMaxGridDimX());
      System.out.println("  max_grid_dim_x: "+device.getMaxGridDimY());
      System.out.println("  max_threads_per_multiprocessor: "+device.getMaxThreadsPerMultiprocessor());
    }
  }

  public static void main(String[] args){
    Main main = new Main();
    main.parseArgs(args);
    main.run();
  }
}
