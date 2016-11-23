package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class PolymorphicRunOnGpu implements Kernel {

  private PolymorphicClassBase m_object;
  private int[] m_array;
  
  public PolymorphicRunOnGpu(){
    m_object = new PolymorphicClassBase();
  }
  
  @Override
  public void gpuMethod() {
    PolymorphicClassDerived derived = new PolymorphicClassDerived();
    derived.init();
    m_object = derived;
    m_array = new int[2];
    m_array[0] = 1;
    m_array[1] = 2;
  }

  public boolean compare(PolymorphicRunOnGpu rhs) {
    if(m_object instanceof PolymorphicClassDerived == false){
      System.out.println("m_object type");
      return false;
    }
    if(rhs.m_object instanceof PolymorphicClassDerived == false){
      System.out.println("rhs.m_object type");
      return false;
    }
    PolymorphicClassDerived lhs_derived = (PolymorphicClassDerived) m_object;
    PolymorphicClassDerived rhs_derived = (PolymorphicClassDerived) rhs.m_object;
    if(lhs_derived.getValue2() != rhs_derived.getValue2()){
      System.out.println("m_value2");
      System.out.println("lhs: "+lhs_derived.getValue2());
      System.out.println("rhs: "+rhs_derived.getValue2());
      return false;
    }
    if(m_array == null){
      System.out.println("m_array null");
      return false;
    }
    if(rhs.m_array == null){
      System.out.println("rhs.m_array null");
      return false;
    }
    if(m_array.length != rhs.m_array.length){
      System.out.println("m_array.length");
      System.out.println("lhs: "+m_array.length);
      System.out.println("rhs: "+rhs.m_array.length);
      return false;
    }
    if(m_array[0] != rhs.m_array[0]){
      System.out.println("m_array[0]");
      return false;
    }
    if(m_array[1] != rhs.m_array[1]){
      System.out.println("m_array[1]");
      return false;
    }
    return true;
  }

}
