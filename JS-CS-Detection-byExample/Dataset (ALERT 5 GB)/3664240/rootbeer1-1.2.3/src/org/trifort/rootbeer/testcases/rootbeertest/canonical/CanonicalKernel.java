package org.trifort.rootbeer.testcases.rootbeertest.canonical;

import org.trifort.rootbeer.runtime.Kernel;
import org.trifort.rootbeer.testcases.rootbeertest.canonical2.CanonicalObject;

public class CanonicalKernel implements Kernel {

  private static CanonicalObject staticObject;
  
  private CanonicalObject instanceObject;
  private float result;
  
  static {
    staticObject = new CanonicalObject(true);
  }
  
  @Override
  public void gpuMethod() {
    instanceObject = new CanonicalObject(true);
    instanceObject.sumContents();
    staticObject.sumContents();
    
    result = instanceObject.getResult() + staticObject.getResult();
  }

  public boolean compare(CanonicalKernel rhs) {
    //TODO: this is failing because the static serialization has trouble
    //with complex objects
    if(result != rhs.result){
      System.out.println("result");
      System.out.println("lhs: "+result);
      System.out.println("rhs: "+rhs.result);
      return false;
    }
    return true;
  }
}
