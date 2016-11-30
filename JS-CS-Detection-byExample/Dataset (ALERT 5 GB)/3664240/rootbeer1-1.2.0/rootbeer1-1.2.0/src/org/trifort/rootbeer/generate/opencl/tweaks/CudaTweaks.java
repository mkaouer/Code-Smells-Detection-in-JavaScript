/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl.tweaks;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.trifort.rootbeer.compressor.Compressor;
import org.trifort.rootbeer.configuration.Configuration;
import org.trifort.rootbeer.configuration.RootbeerPaths;
import org.trifort.rootbeer.deadmethods.DeadMethods;
import org.trifort.rootbeer.generate.opencl.tweaks.GencodeOptions.CompileArchitecture;
import org.trifort.rootbeer.util.CompilerRunner;
import org.trifort.rootbeer.util.CudaPath;
import org.trifort.rootbeer.util.WindowsCompile;

public class CudaTweaks extends Tweaks {

  @Override
  public String getGlobalAddressSpaceQualifier() {
    return "";
  }

  @Override
  public String getUnixHeaderPath() {
    return "/org/trifort/rootbeer/generate/opencl/CudaHeader.c";
  }
  
  @Override
  public String getWindowsHeaderPath() {
    return "/org/trifort/rootbeer/generate/opencl/CudaHeader.c";
  }
  
  @Override
  public String getBothHeaderPath() {
    return null;
  }
  
  @Override
  public String getBarrierPath() {
    return null;
  }
  
  @Override
  public String getGarbageCollectorPath() {
    return "/org/trifort/rootbeer/generate/opencl/GarbageCollector.c";
  }

  @Override
  public String getUnixKernelPath() {
    return "/org/trifort/rootbeer/generate/opencl/CudaKernel.c";
  }
  
  @Override
  public String getWindowsKernelPath() {
    return "/org/trifort/rootbeer/generate/opencl/CudaKernel.c";
  }

  @Override
  public String getBothKernelPath() {
    return null;
  }
      
  /**
   * Compiles CUDA code.
   *
   * @param cuda_code string containing code.
   * @param compileArch determine if we need to build 32bit, 64bit or both.
   * @return an array containing compilation results. You can use <tt>is32Bit()</tt> on each element 
   * to determine if it is 32 bit or 64bit code. If compilation for an architecture fails, only the 
   * offending element is returned.
   */
  public CompileResult[] compileProgram(String cuda_code, CompileArchitecture compileArch) {
    PrintWriter writer;
    try {
      writer = new PrintWriter(RootbeerPaths.v().getRootbeerHome() + "pre_dead.cu");
      writer.println(cuda_code);
      writer.flush();
      writer.close();

      DeadMethods dead_methods = new DeadMethods();
      dead_methods.parseString(cuda_code);
      cuda_code = dead_methods.getResult();

      //Compressor compressor = new Compressor();
      //cuda_code = compressor.compress(cuda_code);

      File generated = new File(RootbeerPaths.v().getRootbeerHome() + "generated.cu");
      writer = new PrintWriter(generated);
      writer.println(cuda_code.toString());
      writer.flush();
      writer.close();

      CudaPath cuda_path = new CudaPath();
      GencodeOptions options_gen = new GencodeOptions();
      String gencode_options = options_gen.getOptions();
      
      ParallelCompile parallel_compile = new ParallelCompile();
      return parallel_compile.compile(generated, cuda_path, gencode_options, compileArch);
    } catch (Exception ex) {
      throw new RuntimeException("Failed to compile cuda code.", ex);
    }
  }

  @Override
  public String getDeviceFunctionQualifier() {
    return "__device__";
  }

}
