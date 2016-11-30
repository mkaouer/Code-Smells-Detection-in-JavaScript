/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.util;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.trifort.rootbeer.configuration.RootbeerPaths;
import org.trifort.rootbeer.generate.opencl.tweaks.CompileResult;
import org.trifort.rootbeer.util.CudaPath;

public class WindowsCompile {

  private List<String> m_visualStudioPaths;
  private List<String> m_jdkPaths;
  
  public WindowsCompile(){
    m_visualStudioPaths = new ArrayList<String>();
    m_visualStudioPaths.add("C:\\Program Files\\Microsoft Visual Studio 9.0\\VC\\vcvarsall.bat");
    m_visualStudioPaths.add("C:\\Program Files (x86)\\Microsoft Visual Studio 9.0\\VC\\vcvarsall.bat");
    
    m_visualStudioPaths.add("C:\\Program Files\\Microsoft Visual Studio 10.0\\VC\\vcvarsall.bat");
    m_visualStudioPaths.add("C:\\Program Files (x86)\\Microsoft Visual Studio 10.0\\VC\\vcvarsall.bat");
    
    m_visualStudioPaths.add("D:\\Program Files\\Microsoft Visual Studio 10.0\\VC\\vcvarsall.bat");
    m_visualStudioPaths.add("D:\\Program Files (x86)\\Microsoft Visual Studio 10.0\\VC\\vcvarsall.bat");
    
    m_visualStudioPaths.add("C:\\Program Files\\Microsoft Visual Studio 11.0\\VC\\vcvarsall.bat");
    m_visualStudioPaths.add("C:\\Program Files (x86)\\Microsoft Visual Studio 11.0\\VC\\vcvarsall.bat");
    
    m_jdkPaths = new ArrayList<String>();
    m_jdkPaths.add("C:\\Program Files\\Java\\");
    m_jdkPaths.add("C:\\Program Files (x86)\\Java\\");
  }
  
  public List<String> compile(String cmd, boolean arch64){
    File cl_script = generateScript(cmd, arch64);
    
    String command = "cmd /c \""+cl_script.getAbsolutePath()+"\"";
    CompilerRunner runner = new CompilerRunner();
    List<String> errors = runner.run(command);      
    return errors;
  }
  
  public String endl(){
    return System.getProperty("line.separator"); 
  }
  
  public String jdkPath(){
    for(String root : m_jdkPaths){
      List<String> possible_paths = findPossibleJdkPaths(root);
      if(possible_paths.isEmpty()){
        continue;
      }
      String[] path_array = new String[possible_paths.size()];
      path_array = possible_paths.toArray(path_array);
      Arrays.sort(path_array);
      return path_array[path_array.length-1];
    }
    System.out.println("JDK not installed. Please install.");
    System.exit(-1);
    return "";
  }
  
  private List<String> findPossibleJdkPaths(String root){
    List<String> ret = new ArrayList<String>();
    File file = new File(root);
    File[] children = file.listFiles();
    for(File child : children){
      String name = child.getName();
      if(name.startsWith("jdk")){
        ret.add(child.getAbsolutePath());
      }
    }
    return ret;
  }
  
  private File generateScript(String cmd, boolean arch64){
    String vs_path = "";
    if(System.getenv().containsKey("VS_VCVARSALL_PATH")){
      vs_path = System.getenv("VS_VCVARSALL_PATH");
      if(vs_path.trim().equals("")){
        vs_path = findPath(m_visualStudioPaths, "Visual Studio");
      } else {
        if(vs_path.endsWith("\\\\") == false){
          vs_path += "\\";
        }
        vs_path += "vcvarsall.bat";
      }
    } else {
      vs_path = findPath(m_visualStudioPaths, "Visual Studio");
    }
    
    String file_text = "";
    String amd64 = "amd64";
    
    String arch = System.getProperty("os.arch");
    if(arch == null){
      arch = "";
    }
    
    //http://lopica.sourceforge.net/os.html
    if(arch.equals("x86")){
      amd64 = ""; 
    }
    
    file_text += "@call \""+vs_path+"\" "+amd64+endl();
    file_text += cmd;
    String version_str;
    if(arch64){
      version_str = "_64";
    } else {
      version_str = "_32";
    }
    File cl_script = new File(RootbeerPaths.v().getRootbeerHome()+"cl_script"+version_str+".bat");
    try {
      PrintWriter writer = new PrintWriter(cl_script);
      writer.println(file_text);
      writer.flush();
      writer.close();
      return cl_script;
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }

  private String findPath(List<String> paths, String desc) {
    for(String path : paths){
      File file = new File(path);
      if(file.exists())
        return path;
    }
    throw new RuntimeException("cannot find path for: "+desc);
  }
  
}
