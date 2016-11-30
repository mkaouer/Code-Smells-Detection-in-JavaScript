/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.serialization;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;

public class StrictMatchRunOnGpu implements Kernel {
  
  private double m_pow;
  private double m_floor;
  private double m_abs;
  private double m_sqrt;
  private double m_round;
  
  public StrictMatchRunOnGpu(){
  }
  
  @Override
  public void gpuMethod() {
    m_floor = StrictMath.floor(StrictMath.PI);
    m_abs = StrictMath.abs(StrictMath.PI);
    m_sqrt = StrictMath.sqrt(StrictMath.PI);
    m_round = StrictMath.round(StrictMath.PI);
    m_pow = StrictMath.pow(StrictMath.PI, 2.0/3.0);
  }

  public boolean compare(StrictMatchRunOnGpu rhs){
    //System.out.println("floor: "+m_floor+" "+rhs.m_floor);
    //System.out.println("abs: "+m_abs+" "+rhs.m_abs);
    //System.out.println("sqrt: "+m_sqrt+" "+rhs.m_sqrt);
    //System.out.println("round: "+m_round+" "+rhs.m_round);
    //System.out.println("pow: "+m_pow+" "+rhs.m_pow);
    if(m_floor != rhs.m_floor)
      return false;
    if(m_abs != rhs.m_abs)
      return false;
    if(m_sqrt != rhs.m_sqrt)
      return false;
    if(m_round != rhs.m_round)
      return false;
    if(m_pow != rhs.m_pow)
      return false;
    return true;
  }
}
