package org.trifort.rootbeer.testcases.rootbeertest.canonical2;

class CanonicalArrays {

  private float[][] floatArray;
  private float arraySum;
  
  public CanonicalArrays(){
    floatArray = new float[2][];
    for(int i = 0; i < 2; ++i){
      floatArray[i] = new float[2];
      for(int j = 0; j < 2; ++j){
        floatArray[i][j] = i * 2 + j;
      }
    }
  }
  
  public void sum(){
    arraySum = 0;
    for(int i = 0; i < floatArray.length; ++i){
      for(int j = 0; j < floatArray[i].length; ++j){
        arraySum += floatArray[i][j];
      }
    }
  }
  
  public float getResult(){
    synchronized(this){
      return arraySum;
    }
  }
}
