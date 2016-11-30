/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.compiler;

import java.util.HashSet;
import java.util.Set;

public class AcceptableNativeMethods {

  private Set<String> m_OkMethods;
  
  public AcceptableNativeMethods(){
    m_OkMethods = new HashSet<String>();
    m_OkMethods.add("<java.lang.Throwable: java.lang.Throwable fillInStackTrace()>");
    m_OkMethods.add("<java.lang.System: void arraycopy(java.lang.Object,int,java.lang.Object,int,int)>");
    m_OkMethods.add("<java.lang.Double: long doubleToRawLongBits(double)>");
    m_OkMethods.add("<java.lang.Double: double longBitsToDouble(long)>");
    m_OkMethods.add("<java.lang.Float: int floatToRawIntBits(float)>");
    m_OkMethods.add("<java.lang.Float: float intBitsToFloat(int)>");
  }
  
  public boolean isOk(String method_signature){
    return m_OkMethods.contains(method_signature);
  }
}
