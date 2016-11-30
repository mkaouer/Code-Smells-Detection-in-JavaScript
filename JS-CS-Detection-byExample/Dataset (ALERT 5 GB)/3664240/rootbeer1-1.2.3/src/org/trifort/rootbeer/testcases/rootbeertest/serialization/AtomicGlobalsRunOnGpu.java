package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.runtime.RootbeerGpu;

public class AtomicGlobalsRunOnGpu implements Kernel {

  private int[] intArray;
  private long[] longArray;
  private float[] floatArray;
  private int[] intArray2;
  private long[] longArray2;
  private float[] floatArray2;
  
  public AtomicGlobalsRunOnGpu(int[] intArray, long[] longArray, 
      float[] floatArray, int[] intArray2, long[] longArray2, 
      float[] floatArray2){
    this.intArray = intArray;
    this.longArray = longArray;
    this.floatArray = floatArray;
    this.intArray2 = intArray2;
    this.longArray2 = longArray2;
    this.floatArray2 = floatArray2;
  }
  
  @Override
  public void gpuMethod() {
    RootbeerGpu.atomicAddGlobal(intArray, 0, 2);
    RootbeerGpu.atomicAddGlobal(longArray, 0, 2);
    RootbeerGpu.atomicAddGlobal(floatArray, 0, 2);
    RootbeerGpu.atomicAddGlobal(intArray, 1, 2);
    RootbeerGpu.atomicAddGlobal(longArray, 1, 2);
    RootbeerGpu.atomicAddGlobal(floatArray, 1, 2);
    RootbeerGpu.atomicAddGlobal(intArray, 2, 2);
    RootbeerGpu.atomicAddGlobal(longArray, 2, 2);
    RootbeerGpu.atomicAddGlobal(floatArray, 2, 2);
    
    RootbeerGpu.atomicSubGlobal(intArray, 0, 1);
    RootbeerGpu.atomicSubGlobal(intArray, 1, 1);
    RootbeerGpu.atomicSubGlobal(intArray, 2, 1);
    
    RootbeerGpu.atomicExchGlobal(intArray2, 0, 2);
    RootbeerGpu.atomicExchGlobal(longArray2, 0, 2);
    RootbeerGpu.atomicExchGlobal(floatArray2, 0, 2);
    
    RootbeerGpu.atomicMinGlobal(intArray2, 0, 2);
    RootbeerGpu.atomicMaxGlobal(intArray2, 0, 2);
    
    RootbeerGpu.atomicCASGlobal(intArray2, 0, 2, 1);
    RootbeerGpu.atomicAndGlobal(intArray2, 0, 2);
    RootbeerGpu.atomicOrGlobal(intArray2, 0, 2);
    RootbeerGpu.atomicXorGlobal(intArray2, 0, 2);
  }

  public boolean compare(AtomicGlobalsRunOnGpu rhs) {
    for(int i = 0; i < intArray.length; ++i){
      int value1 = intArray[i];
      int value2 = rhs.intArray[i];
      if(value1 != value2){
        System.out.println("intArray");
        System.out.println("index: "+i);
        System.out.println("value1: "+value1);
        System.out.println("value2: "+value2);
        return false;
      }
    }    
    for(int i = 0; i < longArray.length; ++i){
      long value1 = longArray[i];
      long value2 = rhs.longArray[i];
      if(value1 != value2){
        System.out.println("longArray");
        System.out.println("index: "+i);
        System.out.println("value1: "+value1);
        System.out.println("value2: "+value2);
        return false;
      }
    }  
    for(int i = 0; i < floatArray.length; ++i){
      float value1 = floatArray[i];
      float value2 = rhs.floatArray[i];
      if(value1 != value2){
        System.out.println("floatArray");
        System.out.println("index: "+i);
        System.out.println("value1: "+value1);
        System.out.println("value2: "+value2);
        return false;
      }
    }
    return true;
  }
}
