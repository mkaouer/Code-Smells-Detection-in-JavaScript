package org.trifort.rootbeer.runtime;

import org.trifort.rootbeer.runtimegpu.GpuException;

public class GpuFuture {
  
  private volatile boolean ready;
  private volatile Throwable ex;
  
  public GpuFuture(){
    ready = false;
  }
  
  public void signal() {
    ready = true;
  }

  public void reset() {
    ex = null;
    ready = false;
  }

  public void take() {
    while(!ready){
      //do nothing
    }
    if(ex != null){
      if(ex instanceof NullPointerException){
        throw (NullPointerException) ex;
      } else if(ex instanceof OutOfMemoryError){
        throw (OutOfMemoryError) ex;
      } else if(ex instanceof Error){
        throw (Error) ex;
      } else if(ex instanceof ArrayIndexOutOfBoundsException){
        throw (ArrayIndexOutOfBoundsException) ex;
      } else if(ex instanceof RuntimeException){
        throw (RuntimeException) ex;
      } else {
        throw new RuntimeException(ex);
      }
    }
  }

  public void setException(Exception ex) {
    this.ex = ex;
  }
}
