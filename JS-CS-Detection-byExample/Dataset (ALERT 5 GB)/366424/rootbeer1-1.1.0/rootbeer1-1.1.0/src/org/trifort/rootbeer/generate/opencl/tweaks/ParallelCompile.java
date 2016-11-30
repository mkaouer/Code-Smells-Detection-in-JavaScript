/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl.tweaks;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.trifort.rootbeer.generate.opencl.tweaks.GencodeOptions.CompileArchitecture;
import org.trifort.rootbeer.runtime.BlockingQueue;
import org.trifort.rootbeer.util.CudaPath;


public class ParallelCompile implements Runnable {

  private BlockingQueue<ParallelCompileJob> m_toCores;
  private BlockingQueue<ParallelCompileJob> m_fromCores;
  
  public ParallelCompile(){
    m_toCores = new BlockingQueue<ParallelCompileJob>();
    m_fromCores = new BlockingQueue<ParallelCompileJob>();
    
    int num_cores = 2;
    for(int i = 0; i < num_cores; ++i){
      Thread thread = new Thread(this);
      thread.setDaemon(true);
      thread.start();
    }
  }
  
  /**
   * @return an array containing compilation results. You can use <tt>is32Bit()</tt> on each element 
   * to determine if it is 32 bit or 64bit code. If compilation for an architecture fails, only the 
   * offending element is returned.
   */
  public CompileResult[] compile(File generated, CudaPath cuda_path, 
    String gencode_options, CompileArchitecture compileArch){
    
    boolean single_result = false;
    
    switch (compileArch) {
      case Arch32bit:
        System.out.println("compiling CUDA code for 32bit only...");
        m_toCores.put(new ParallelCompileJob(generated, cuda_path, gencode_options, true));
        single_result = true;
        break;
      case Arch64bit:
        System.out.println("compiling CUDA code for 64bit only...");
        m_toCores.put(new ParallelCompileJob(generated, cuda_path, gencode_options, false));
        single_result = true;
        break;
      case Arch32bit64bit:
        System.out.println("compiling CUDA code for 32bit and 64bit...");
        m_toCores.put(new ParallelCompileJob(generated, cuda_path, gencode_options, true));
        m_toCores.put(new ParallelCompileJob(generated, cuda_path, gencode_options, false));
        single_result = false;
        break;
    }
    
    if(single_result){
      ParallelCompileJob job = m_fromCores.take();
      CompileResult result = job.getResult();
      CompileResult[] ret = new CompileResult[1];
      ret[0] = result;
      return ret;
    } else {
      ParallelCompileJob ret1 = m_fromCores.take();
      ParallelCompileJob ret2 = m_fromCores.take();
    
      CompileResult[] ret = new CompileResult[2];
      if(ret1.getResult().is32Bit()){
        ret[0] = ret1.getResult();
        ret[1] = ret2.getResult();
      } else {
        ret[0] = ret2.getResult();
        ret[1] = ret1.getResult();
      }
      return ret;
    }
  }

  public void run() {
    ParallelCompileJob job = m_toCores.take();
    job.compile();
    m_fromCores.put(job);
  }
  
}
