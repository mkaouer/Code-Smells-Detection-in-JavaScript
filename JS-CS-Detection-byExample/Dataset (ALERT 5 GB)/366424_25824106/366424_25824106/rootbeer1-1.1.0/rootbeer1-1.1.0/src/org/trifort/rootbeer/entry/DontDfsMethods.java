/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.entry;

import java.util.HashSet;
import java.util.Set;

public class DontDfsMethods {

  private Set<String> m_methods;
  
  public DontDfsMethods(){
    m_methods = new HashSet<String>();
    m_methods.add("<java.lang.Object: void <clinit>()>");
    m_methods.add("<java.lang.Object: void registerNatives()>");
    m_methods.add("<java.lang.StrictMath: double exp(double)>");
    m_methods.add("<java.lang.StrictMath: double log(double)>");
    m_methods.add("<java.lang.StrictMath: double log10(double)>");
    m_methods.add("<java.lang.StrictMath: double log(double)>");
    m_methods.add("<java.lang.StrictMath: double sqrt(double)>");
    m_methods.add("<java.lang.StrictMath: double cbrt(double)>");
    m_methods.add("<java.lang.StrictMath: double IEEEremainder(double,double)>");
    m_methods.add("<java.lang.StrictMath: double ceil(double)>");
    m_methods.add("<java.lang.StrictMath: double floor(double)>");
    m_methods.add("<java.lang.StrictMath: double sin(double)>");
    m_methods.add("<java.lang.StrictMath: double cos(double)>");
    m_methods.add("<java.lang.StrictMath: double tan(double)>");
    m_methods.add("<java.lang.StrictMath: double asin(double)>");
    m_methods.add("<java.lang.StrictMath: double acos(double)>");
    m_methods.add("<java.lang.StrictMath: double atan(double)>");
    m_methods.add("<java.lang.StrictMath: double atan2(double,double)>");
    m_methods.add("<java.lang.StrictMath: double pow(double,double)>");
    m_methods.add("<java.lang.StrictMath: double sinh(double)>");
    m_methods.add("<java.lang.StrictMath: double cosh(double)>");
    m_methods.add("<java.lang.StrictMath: double tanh(double)>");
    m_methods.add("<java.lang.Double: long doubleToLongBits(double)>");
    m_methods.add("<java.lang.Double: double longBitsToDouble(long)>");
    m_methods.add("<java.lang.Float: int floatToIntBits(float)>");
    m_methods.add("<java.lang.Float: float intBitsToFloat(int)>");
    m_methods.add("<java.lang.System: void arraycopy(java.lang.Object,int,java.lang.Object,int,int)>");
    m_methods.add("<java.lang.Throwable: java.lang.Throwable fillInStackTrace()>");
    m_methods.add("<java.lang.Throwable: int getStackTraceDepth()>");
    m_methods.add("<java.lang.Throwable: java.lang.StackTraceElement getStackTraceElement(int)>");
    m_methods.add("<java.lang.Object: java.lang.Object clone()>");
    m_methods.add("<java.lang.Object: int hashCode()>");
    m_methods.add("<org.trifort.rootbeer.runtime.GpuStopwatch: void start()>");
    m_methods.add("<org.trifort.rootbeer.runtime.GpuStopwatch: void stop()>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: boolean isOnGpu()>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: int getThreadId()>"); 
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: int getThreadIdxx()>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: int getBlockIdxx()>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: int getBlockDimx()>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: int getGridDimx()>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: long getRef(java.lang.Object)>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void syncthreads()>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void threadfence()>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void threadfenceBlock()>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: java.lang.Object getSharedObject(int)>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedObject(int,java.lang.Object)>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: byte getSharedByte(int)>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedByte(int,byte)>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: char getSharedChar(int)>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedChar(int,char)>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: boolean getSharedBoolean(int)>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedBoolean(int,boolean)>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: short getSharedShort(int)>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedShort(int,short)>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: int getSharedInteger(int)>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedInteger(int,int)>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: long getSharedLong(int)>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedLong(int,long)>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: float getSharedFloat(int)>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedFloat(int,float)>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: double getSharedDouble(int)>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedDouble(int,double)>");
    m_methods.add("<org.trifort.rootbeer.runtime.RootbeerGpu: double sin(double)>");
    m_methods.add("<java.lang.System: long nanoTime()>");
    m_methods.add("<java.lang.Class: java.lang.String getName()>");
    m_methods.add("<java.lang.Object: java.lang.Class getClass()>");
    m_methods.add("<java.lang.StringValue: char[] 'from'(char[])>");
    m_methods.add("<java.util.Arrays: java.lang.Object[] copyOf(java.lang.Object[],int)>");
    m_methods.add("<java.lang.String: void <init>(char[])>");
    m_methods.add("<java.lang.Integer: void(int)>");
    m_methods.add("<java.lang.Integer: java.lang.Integer valueOf(int)>");
  }
  
  public Set<String> get(){
    return m_methods;
  }
}
