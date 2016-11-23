/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer;

import edu.syr.pcpratts.rootbeer.runtime2.cuda.CudaLoader;
import edu.syr.pcpratts.rootbeer.runtime2.cuda.CudaRuntime2;
import java.util.ArrayList;
import java.util.List;

public class Main {
  
  public int m_mode;
  private int m_num_args;
  private boolean m_runTests;
  private static boolean m_disable_class_remapping=false;
  private String m_testCase;
  private boolean m_simpleCompile;
  private static boolean m_printDeviceInfo = false;
  private String m_mainJar;
  private List<String> m_libJars;
  private List<String> m_directories;
  private String m_destJar;
  
  public Main(){
    m_libJars = new ArrayList<String>();
    m_directories = new ArrayList<String>();
    m_mode = Configuration.MODE_GPU;
    m_simpleCompile = false;
  }

  public static boolean disable_class_remapping(){
    return m_disable_class_remapping;
  }
  
  private void parseArgs(String[] args) {
    m_num_args = args.length;
    
    for(int i = 0; i < args.length; ++i){
      String arg = args[i];
      if(arg.equals("-nemu")){
        m_mode = Configuration.MODE_NEMU;
      } else if(arg.equals("-jemu")){
        m_mode = Configuration.MODE_JEMU;
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
      } else if(arg.equals("-runtest")){
        m_runTests = true;
        m_testCase = safeGet(args, i+1, "-runtest");
        ++i;
      } else if(arg.equals("-printdeviceinfo")){
        m_printDeviceInfo = true;
      } else if(arg.equals("-disable-class-remapping")){
        m_disable_class_remapping = true;
      } else {      
        m_mainJar = arg;
        m_destJar = safeGet(args, i+1, arg);
        m_simpleCompile = true;
        break;
      }
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
            CudaLoader loader = new CudaLoader();
            loader.load();
            CudaRuntime2.printDeviceInfo();
        } else {
            System.out.println("-printdeviceinfo can only be used by itself. Remove other arguments.");  
            System.out.flush();
            return;
        }
    }
    
    if(m_runTests){
      RootbeerTest test = new RootbeerTest();
      test.runTests(m_testCase);
      return;
    } 
    
    if(m_simpleCompile){
      RootbeerCompiler compiler = new RootbeerCompiler();
      try {
        compiler.compile(m_mainJar, m_destJar);
      } catch(Exception ex){
        ex.printStackTrace();
      }
    } else {
      RootbeerCompiler compiler = new RootbeerCompiler();
      try {
        compiler.compile(m_mainJar, m_libJars, m_directories, m_destJar);
      } catch(Exception ex){
        ex.printStackTrace();
      }
    }
  }
  
  public static void main(String[] args){
    Main main = new Main();
    main.parseArgs(args);
    main.run();
  }
}
