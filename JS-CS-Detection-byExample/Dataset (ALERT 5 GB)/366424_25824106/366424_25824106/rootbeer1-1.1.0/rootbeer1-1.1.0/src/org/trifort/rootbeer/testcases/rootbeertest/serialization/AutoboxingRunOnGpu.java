package org.trifort.rootbeer.testcases.rootbeertest.serialization;

import org.trifort.rootbeer.runtime.Kernel;

public class AutoboxingRunOnGpu implements Kernel {

  private Double m_double0;
  private Integer m_int0;
  private Integer m_int1;
  private Integer m_int2;
  
  public void gpuMethod() {
    m_double0 = returnDouble();
    m_int0 = returnInteger0();
    m_int1 = returnInteger1();
    m_int2 = returnInteger2();
  }

  private double returnDouble() {
    return 10;
  }
  
  // values between -128 and 0 will fail because of problems in
  // static_getter_java_lang_Integer$IntegerCache_high or
  // static_getter_java_lang_Integer$IntegerCache_cache
  /*
     if ( i0  <  -128   ) goto label0;
     $i1  = static_getter_java_lang_Integer$IntegerCache_high(gc_info, exception);
     if ( i0  >  $i1   ) goto label0;
     $r0  = static_getter_java_lang_Integer$IntegerCache_cache(gc_info, exception);
     $i2  =  i0  +  128  ;
     $r1  = java_lang_Integer__array_get(gc_info, $r0, $i2, exception);
     if(*exception != 0) {
     return 0; }
     return  $r1 ;
   */
  private int returnInteger0() {
    return -30; 
  }
  
  private int returnInteger1() {
    return 0; 
  }
  
  private int returnInteger2() {
    return 30; 
  }
  
  public double getDouble(){
    return m_double0;
  }
  
  public double getInteger0(){
    return m_int0;
  }
  
  public double getInteger1(){
    return m_int1;
  }

  public double getInteger2(){
    return m_int2;
  }
  
  
  public boolean compare(AutoboxingRunOnGpu rhs) {
    if(getDouble() != rhs.getDouble()){
      System.out.println("m_double0");
      System.out.println("lhs: "+getDouble());
      System.out.println("rhs: "+rhs.getDouble());
      return false;
    }
    if(getInteger2() != rhs.getInteger2()){
      System.out.println("m_int2");
      System.out.println("lhs: "+getInteger2());
      System.out.println("rhs: "+rhs.getInteger2());
      return false;
    }
    if(getInteger0() != rhs.getInteger0()){
      System.out.println("m_int0");
      System.out.println("lhs: "+getInteger0());
      System.out.println("rhs: "+rhs.getInteger0());
      return false;
    }
    if(getInteger1() != rhs.getInteger1()){
      System.out.println("m_int1");
      System.out.println("lhs: "+getInteger1());
      System.out.println("rhs: "+rhs.getInteger1());
      return false;
    }
    return true;
  }
  
}
