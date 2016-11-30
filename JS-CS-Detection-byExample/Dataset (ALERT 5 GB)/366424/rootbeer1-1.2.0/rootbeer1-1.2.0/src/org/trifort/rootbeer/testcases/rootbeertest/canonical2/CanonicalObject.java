package org.trifort.rootbeer.testcases.rootbeertest.canonical2;


public class CanonicalObject {

  private CanonicalObject object;
  private CanonicalArrays arrays;
  private float sum;
  
  public CanonicalObject(boolean recurse){
    arrays = new CanonicalArrays();
    if(recurse){
      object = new CanonicalObject(false);
    }
  }
  
  public void sumContents(){
    sum = 0;
    if(object != null){
      object.sumContents();
      sum += object.getResult();
    }
    arrays.sum();
    sum += arrays.getResult();
  }
  
  public synchronized float getResult(){
    return sum;
  }
}
