/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CudaPath {

  private List<String> m_windowsSearchPaths;
  private List<String> m_unixSearchPaths;
  
  public CudaPath(){
    m_windowsSearchPaths = new ArrayList<String>();
    m_unixSearchPaths = new ArrayList<String>();
    
    m_windowsSearchPaths.add("C:\\Program Files\\NVIDIA GPU Computing Toolkit\\CUDA\\");
    m_windowsSearchPaths.add("C:\\Program Files (x86)\\NVIDIA GPU Computing Toolkit\\CUDA\\");
    m_unixSearchPaths.add("/usr/local/cuda/bin/");
    m_unixSearchPaths.add("/usr/local/cuda-5.5/bin/");
    m_unixSearchPaths.add("/usr/lib/nvidia-cuda-toolkit/bin/");
  }
          
  public String get(){
    if(File.separator.equals("/")){
      return getUnix();
    } else {
      return getWindows();
    }
  }

  private String getUnix() {

    // Search for the environment variable
    if(System.getenv().containsKey("CUDA_BIN_PATH")){
      String s = System.getenv("CUDA_BIN_PATH");
      if(!s.endsWith("/")) {
        return s + "/";
      }
      return s;
    }
    
    // Search path
    BufferedReader input = null;
    try {
      Process p = Runtime.getRuntime().exec("which nvcc");
      input = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String output = input.readLine();
      if(output != null && !output.isEmpty()) {
        output = output.trim();
        return output.substring(0, output.lastIndexOf("nvcc"));
      }
    } catch (IOException e) {
      // Do nothing, go to next part
    } finally {
      try {
        input.close();
      } catch (Exception e) {
        // If this fails there is nothing we can do
      }
    }
    
    // Search globally
    input = null;
    try {
      Process p = Runtime.getRuntime().exec("whereis nvcc");
      input = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String output = input.readLine();
      if(output != null){
        String[] sp = output.split(" ");
        for(String s: sp) {
          s = s.trim();
          if(s.endsWith("nvcc")) {
            return s.substring(0, s.lastIndexOf("nvcc"));
          }
        }
      }
    } catch (IOException e) {
      // Do nothing, go to next part
    } finally {
      try {
        input.close();
      } catch (Exception e) {
        // If this fails there is nothing we can do
      }
    }
    
    // Search given paths
    for(String path : m_unixSearchPaths){
      File file = new File(path+"nvcc");
      if(file.exists()){
        return path;
      }
    }
    
    // Last resort...
    return "/usr/local/cuda/bin/";
  }

  private String getWindows() {
    for(String path : m_windowsSearchPaths){
      String nvcc = findWindowsNvcc(path);
      if(nvcc != null){
        return nvcc;
      }
    }    
    if(System.getenv().containsKey("CUDA_BIN_PATH")){
      return findWindowsNvcc(System.getenv("CUDA_BIN_PATH"));
    }
    throw new RuntimeException("cannot find nvcc.exe. Try setting the CUDA_BIN_PATH to the folder with nvcc.exe");
  }

  private String findWindowsNvcc(String path) {
    File file = new File(path);
    if(file.exists() == false){
      return null;
    }
    File[] children = file.listFiles();
    FileSorter[] sorted_children = new FileSorter[children.length];
    for(int i = 0; i < children.length; ++i){
      File child = children[i];
      sorted_children[i] = new FileSorter(child);
    }
    Arrays.sort(sorted_children);
    for(int i = 0; i < sorted_children.length; ++i){
      children[i] = sorted_children[i].getFile();
    }
    for(File child : children){
      if(child.isDirectory()){
        String nvcc = findWindowsNvcc(child.getAbsolutePath());
        if(nvcc != null){
          return nvcc;
        }
      } else {
        if(child.getName().equals("nvcc.exe")){
          return child.getAbsolutePath();
        }
      }
    }
    return null;
  }
  
  private class FileSorter implements Comparable<FileSorter> {

    private File m_file;
    
    public FileSorter(File file){
      m_file = file;
    }
    
    public int compareTo(FileSorter o) {
      String lhs = m_file.getAbsolutePath();
      String rhs = o.m_file.getAbsolutePath();
      
      return rhs.compareTo(lhs);
    }
    
    public File getFile(){
      return m_file;
    }
  }
}
