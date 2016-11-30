/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl.tweaks;

//help: http://mlso.hao.ucar.edu/hao/acos/sw/cuda-sdk/shared/common.mk

import java.io.File;
import java.util.List;

import org.trifort.rootbeer.configuration.Configuration;
import org.trifort.rootbeer.util.CmdRunner;
import org.trifort.rootbeer.util.CudaPath;

public class GencodeOptions {

  public enum CompileArchitecture {
    Arch32bit, Arch64bit, Arch32bit64bit;
  }
  
  public enum ComputeCapability {
    ALL, SM_11, SM_12, SM_20, SM_21, SM_30, SM_35;
  }
  
  public String getOptions(){
    String version = getVersion();
    String sm_35;
    String sm_30;
    String sm_21;
    String sm_20;
    String sm_12;
    String sm_11;
    if(File.separator.equals("/")){
      sm_35 = "--generate-code arch=compute_35,code=\"sm_35,compute_35\" ";
      sm_30 = "--generate-code arch=compute_30,code=\"sm_30,compute_30\" ";
      sm_21 = "--generate-code arch=compute_20,code=\"sm_21,compute_20\" ";
      sm_20 = "--generate-code arch=compute_20,code=\"sm_20,compute_20\" ";
      sm_12 = "--generate-code arch=compute_12,code=\"sm_12,compute_12\" ";
      sm_11 = "--generate-code arch=compute_11,code=\"sm_11,compute_11\" ";
    } else {
      sm_35 = "--generate-code arch=compute_35,code=\"sm_35\" ";
      sm_30 = "--generate-code arch=compute_30,code=\"sm_30\" ";
      sm_21 = "--generate-code arch=compute_20,code=\"sm_21\" ";
      sm_20 = "--generate-code arch=compute_20,code=\"sm_20\" ";  
      sm_12 = "--generate-code arch=compute_12,code=\"sm_12\" "; 
      sm_11 = "--generate-code arch=compute_11,code=\"sm_11\" "; 
    }
    
    //sm_12 doesn't support recursion
    if(Configuration.compilerInstance().getRecursion()){
      sm_12 = "";
      sm_11 = "";
    }
    
    //sm_12 doesn't support doubles
    if(Configuration.compilerInstance().getDoubles()){
      sm_12 = "";
      sm_11 = "";
    }
    
    if ((version.equals("Cuda compilation tools, release 6.5, V6.5.12")) ||
        (version.equals("Cuda compilation tools, release 6.0, V6.0.1")) ||
        (version.equals("Cuda compilation tools, release 5.5, V5.5.0")) ||
        (version.equals("Cuda compilation tools, release 5.0, V0.2.1221"))){
      switch (Configuration.compilerInstance().getComputeCapability()) {
        case ALL:
          return sm_35 + sm_30 + sm_21 + sm_20 + sm_12 + sm_11;
        case SM_11:
          return sm_11;
        case SM_12:
          return sm_12;
        case SM_20:
          return sm_20;
        case SM_21:
          return sm_21;
        case SM_30:
          return sm_30;
        case SM_35:
          return sm_35;
        default:
          return sm_35 + sm_30 + sm_21 + sm_20 + sm_12 + sm_11;
      }
    } else if ((version.equals("Cuda compilation tools, release 4.2, V0.2.1221")) ||
        (version.equals("Cuda compilation tools, release 4.1, V0.2.1221")) ||
        (version.equals("Cuda compilation tools, release 4.0, V0.2.1221")) ||
        (version.equals("Cuda compilation tools, release 3.2, V0.2.1221"))){
      switch (Configuration.compilerInstance().getComputeCapability()) {
        case ALL:
          return sm_30 + sm_21 + sm_20 + sm_12 + sm_11;
        case SM_11:
          return sm_11;
        case SM_12:
          return sm_12;
        case SM_20:
          return sm_20;
        case SM_21:
          return sm_21;
        case SM_30:
          return sm_30;
        default:
          return sm_30 + sm_21 + sm_20 + sm_12 + sm_11;
      }
    } else if((version.equals("Cuda compilation tools, release 3.1, V0.2.1221")) ||
        (version.equals("Cuda compilation tools, release 3.0, V0.2.1221"))){
      switch (Configuration.compilerInstance().getComputeCapability()) {
        case ALL:
          return sm_20 + sm_12 + sm_11;
        case SM_11:
          return sm_11;
        case SM_12:
          return sm_12;
        case SM_20:
          return sm_20;
        default:
          return sm_20 + sm_12 + sm_11;
      }
    } else {
      throw new RuntimeException("unsupported nvcc version. version 3.0 or higher needed. arch sm_11 or higher needed.");
    }
  }

  private String getVersion() {
    CudaPath cuda_path = new CudaPath();
    String cmd[] = new String[2];
    if(File.separator.equals("/")){
      String nvcc_path = cuda_path.get() + "nvcc";
      cmd[0] = nvcc_path;
      cmd[1] = "--version";
    } else {
      String nvcc_path = cuda_path.get();
      cmd[0] = nvcc_path;
      cmd[1] = "--version";
    }
    
    CmdRunner runner = new CmdRunner();
    runner.run(cmd, new File("."));
    List<String> lines = runner.getOutput();
    if(lines.isEmpty()){
      List<String> error_lines = runner.getError();
      for(String error_line : error_lines){
        System.out.println(error_line);
      }
      throw new RuntimeException("error detecting nvcc version.");
    }
    
    String last_line = lines.get(lines.size()-1);
    return last_line;
  }
  
}
