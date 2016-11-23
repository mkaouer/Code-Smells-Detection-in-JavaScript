package org.trifort.rootbeer.entry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.trifort.rootbeer.generate.opencl.OpenCLArrayType;

import soot.ArrayType;
import soot.CharType;
import soot.RefType;
import soot.rbclassload.HierarchySignature;

public class CompilerSetup {

  public Set<String> getDontDfs(){
    Set<String> ret = new HashSet<String>();
    ret.add("<java.lang.Object: void <clinit>()>");
    ret.add("<java.lang.Object: void registerNatives()>");
    ret.add("<java.lang.StrictMath: double exp(double)>");
    ret.add("<java.lang.StrictMath: double log(double)>");
    ret.add("<java.lang.StrictMath: double log10(double)>");
    ret.add("<java.lang.StrictMath: double log(double)>");
    ret.add("<java.lang.StrictMath: double sqrt(double)>");
    ret.add("<java.lang.StrictMath: double cbrt(double)>");
    ret.add("<java.lang.StrictMath: double IEEEremainder(double,double)>");
    ret.add("<java.lang.StrictMath: double ceil(double)>");
    ret.add("<java.lang.StrictMath: double floor(double)>");
    ret.add("<java.lang.StrictMath: double sin(double)>");
    ret.add("<java.lang.StrictMath: double cos(double)>");
    ret.add("<java.lang.StrictMath: double tan(double)>");
    ret.add("<java.lang.StrictMath: double asin(double)>");
    ret.add("<java.lang.StrictMath: double acos(double)>");
    ret.add("<java.lang.StrictMath: double atan(double)>");
    ret.add("<java.lang.StrictMath: double atan2(double,double)>");
    ret.add("<java.lang.StrictMath: double pow(double,double)>");
    ret.add("<java.lang.StrictMath: double sinh(double)>");
    ret.add("<java.lang.StrictMath: double cosh(double)>");
    ret.add("<java.lang.StrictMath: double tanh(double)>");
    ret.add("<java.lang.System: void arraycopy(java.lang.Object,int,java.lang.Object,int,int)>");
    ret.add("<java.lang.Throwable: java.lang.Throwable fillInStackTrace()>");
    ret.add("<java.lang.Throwable: int getStackTraceDepth()>");
    ret.add("<java.lang.Throwable: java.lang.StackTraceElement getStackTraceElement(int)>");
    ret.add("<java.lang.Object: java.lang.Object clone()>");
    ret.add("<java.lang.Object: int hashCode()>");
    ret.add("<org.trifort.rootbeer.runtime.GpuStopwatch: void start()>");
    ret.add("<org.trifort.rootbeer.runtime.GpuStopwatch: void stop()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: boolean isOnGpu()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: int getThreadId()>"); 
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: int getThreadIdxx()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: int getBlockIdxx()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: int getBlockDimx()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: long getGridDimx()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: long getRef(java.lang.Object)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void syncthreads()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void threadfence()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void threadfenceBlock()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void threadfenceSystem()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: java.lang.Object getSharedObject(int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedObject(int,java.lang.Object)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: byte getSharedByte(int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedByte(int,byte)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: char getSharedChar(int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedChar(int,char)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: boolean getSharedBoolean(int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedBoolean(int,boolean)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: short getSharedShort(int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedShort(int,short)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: int getSharedInteger(int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedInteger(int,int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: long getSharedLong(int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedLong(int,long)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: float getSharedFloat(int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedFloat(int,float)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: double getSharedDouble(int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedDouble(int,double)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: double sin(double)>");
    ret.add("<org.trifort.rootbeer.testcases.rootbeertest.serialization.ForceArrayNewRunOnGpu: java.lang.String getStringArray()>");
    ret.add("<java.lang.System: long nanoTime()>");
    ret.add("<java.lang.Class: java.lang.String getName()>");
    ret.add("<java.lang.Object: java.lang.Class getClass()>");
    ret.add("<java.lang.StringValue: char[] 'from'(char[])>");
    ret.add("<java.util.Arrays: java.lang.Object[] copyOf(java.lang.Object[],int)>");
    ret.add("<java.lang.String: void <init>(char[])>");
    ret.add("<java.lang.String: java.lang.String substring(int)>");
    ret.add("<java.lang.String: java.lang.String substring(int,int)>");
    ret.add("<java.lang.String: int indexOf(java.lang.String)>");
    ret.add("<java.lang.String: int indexOf(java.lang.String,int)>");  
    ret.add("<java.lang.String: java.lang.String[] split(java.lang.String)>");
    ret.add("<java.lang.String: java.lang.String[] split(java.lang.String,int)>");
    ret.add("<java.lang.String: java.lang.String valueOf(java.lang.Object)>");

    ret.add("<java.io.PrintStream: void println()>");
    ret.add("<java.io.PrintStream: void println(java.lang.String)>");
    ret.add("<java.io.PrintStream: void println(java.lang.Object)>");
    ret.add("<java.io.PrintStream: void println(boolean)>");   
    ret.add("<java.io.PrintStream: void println(byte)>");   
    ret.add("<java.io.PrintStream: void println(char)>");    
    ret.add("<java.io.PrintStream: void println(short)>");   
    ret.add("<java.io.PrintStream: void println(int)>");     
    ret.add("<java.io.PrintStream: void println(long)>");   
    ret.add("<java.io.PrintStream: void println(float)>");
    ret.add("<java.io.PrintStream: void println(double)>");
    ret.add("<java.io.PrintStream: void print(java.lang.String)>");
    ret.add("<java.io.PrintStream: void print(java.lang.Object)>");
    ret.add("<java.io.PrintStream: void print(boolean)>");   
    ret.add("<java.io.PrintStream: void print(byte)>");   
    ret.add("<java.io.PrintStream: void print(char)>");    
    ret.add("<java.io.PrintStream: void print(short)>");   
    ret.add("<java.io.PrintStream: void print(int)>");     
    ret.add("<java.io.PrintStream: void print(long)>");   
    ret.add("<java.io.PrintStream: void print(float)>");
    ret.add("<java.io.PrintStream: void print(double)>");
    
    ret.add("<java.lang.Double: long doubleToLongBits(double)>");
    ret.add("<java.lang.Double: double longBitsToDouble(long)>");
    ret.add("<java.lang.Float: int floatToIntBits(float)>");
    ret.add("<java.lang.Float: float intBitsToFloat(int)>");
    
    ret.add("<java.lang.Double: java.lang.String toString(double)>");
    ret.add("<java.lang.Float: java.lang.String toString(float)>");
    ret.add("<java.lang.Integer: java.lang.String toString(int)>");
    ret.add("<java.lang.Long: java.lang.String toString(long)>");

    ret.add("<java.lang.Integer: void <init>(int)>");
    ret.add("<java.lang.Integer: java.lang.Integer valueOf(int)>");
    ret.add("<java.lang.Integer: int parseInt(java.lang.String)>");
    ret.add("<java.lang.Long: long parseLong(java.lang.String)>");
    ret.add("<java.lang.Float: float parseFloat(java.lang.String)>");
    ret.add("<java.lang.Double: double parseDouble(java.lang.String)>");

    ret.add("<java.lang.StringBuilder: java.lang.StringBuilder append(double)>");
    ret.add("<java.lang.StringBuilder: java.lang.StringBuilder append(float)>");
    
    return ret;
  }
  
  public Set<String> getDontEmit(){
    Set<String> ret = new HashSet<String>();
    ret.add("<java.lang.String: void <init>(java.lang.String)>");
    ret.add("<java.lang.String: void <init>(char[])>");
    ret.add("<java.lang.StringBuilder: void <init>()>");
    ret.add("<java.lang.StringBuilder: void <init>(java.lang.String)>");
    ret.add("<java.lang.StringBuilder: java.lang.StringBuilder append(boolean)>");
    ret.add("<java.lang.StringBuilder: java.lang.StringBuilder append(char)>");
    ret.add("<java.lang.StringBuilder: java.lang.StringBuilder append(double)>");
    ret.add("<java.lang.StringBuilder: java.lang.StringBuilder append(float)>");
    ret.add("<java.lang.StringBuilder: java.lang.StringBuilder append(int)>");
    ret.add("<java.lang.StringBuilder: java.lang.StringBuilder append(long)>");
    ret.add("<java.lang.StringBuilder: java.lang.StringBuilder append(java.lang.String)>");
    ret.add("<java.lang.StringBuilder: java.lang.String toString()>");
    ret.add("<java.lang.Double: java.lang.String toString(double)>");
    ret.add("<java.lang.Float: java.lang.String toString(float)>");
    ret.add("<java.lang.Integer: java.lang.String toString(int)>");
    ret.add("<java.lang.Long: java.lang.String toString(long)>");
    ret.add("<java.lang.Object: java.lang.String toString()>");
    return ret;
  }
  
  public Set<String> getDontMangle(){
    Set<String> ret = new HashSet<String>();
    ret.add("<java.lang.Object: void <clinit>()>");
    ret.add("<java.lang.Object: void registerNatives()>");
    ret.add("<java.lang.StrictMath: double exp(double)>");
    ret.add("<java.lang.StrictMath: double log(double)>");
    ret.add("<java.lang.StrictMath: double log10(double)>");
    ret.add("<java.lang.StrictMath: double log(double)>");
    ret.add("<java.lang.StrictMath: double sqrt(double)>");
    ret.add("<java.lang.StrictMath: double cbrt(double)>");
    ret.add("<java.lang.StrictMath: double IEEEremainder(double,double)>");
    ret.add("<java.lang.StrictMath: double ceil(double)>");
    ret.add("<java.lang.StrictMath: double floor(double)>");
    ret.add("<java.lang.StrictMath: double sin(double)>");
    ret.add("<java.lang.StrictMath: double cos(double)>");
    ret.add("<java.lang.StrictMath: double tan(double)>");
    ret.add("<java.lang.StrictMath: double asin(double)>");
    ret.add("<java.lang.StrictMath: double acos(double)>");
    ret.add("<java.lang.StrictMath: double atan(double)>");
    ret.add("<java.lang.StrictMath: double atan2(double,double)>");
    ret.add("<java.lang.StrictMath: double pow(double,double)>");
    ret.add("<java.lang.StrictMath: double sinh(double)>");
    ret.add("<java.lang.StrictMath: double cosh(double)>");
    ret.add("<java.lang.StrictMath: double tanh(double)>");
    ret.add("<java.lang.Double: long doubleToLongBits(double)>");
    ret.add("<java.lang.Double: double longBitsToDouble(long)>");
    ret.add("<java.lang.Float: int floatToIntBits(float)>");
    ret.add("<java.lang.Float: float intBitsToFloat(int)>");
    ret.add("<java.lang.System: void arraycopy(java.lang.Object,int,java.lang.Object,int,int)>");
    ret.add("<java.lang.Throwable: java.lang.Throwable fillInStackTrace()>");
    ret.add("<java.lang.Throwable: int getStackTraceDepth()>");
    ret.add("<java.lang.Throwable: java.lang.StackTraceElement getStackTraceElement(int)>");
    ret.add("<java.lang.Object: java.lang.Object clone()>");
    ret.add("<java.lang.Object: int hashCode()>");
    ret.add("<org.trifort.rootbeer.runtime.GpuStopwatch: void start()>");
    ret.add("<org.trifort.rootbeer.runtime.GpuStopwatch: void stop()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: boolean isOnGpu()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: int getThreadId()>"); 
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: int getThreadIdxx()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: int getBlockIdxx()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: int getBlockDimx()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: long getGridDimx()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: long getRef(java.lang.Object)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void syncthreads()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void threadfence()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void threadfenceBlock()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void threadfenceSystem()>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: java.lang.Object getSharedObject(int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedObject(int,java.lang.Object)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: byte getSharedByte(int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedByte(int,byte)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: char getSharedChar(int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedChar(int,char)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: boolean getSharedBoolean(int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedBoolean(int,boolean)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: short getSharedShort(int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedShort(int,short)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: int getSharedInteger(int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedInteger(int,int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: long getSharedLong(int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedLong(int,long)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: float getSharedFloat(int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedFloat(int,float)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: double getSharedDouble(int)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: void setSharedDouble(int,double)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerGpu: double sin(double)>");
    ret.add("<java.lang.System: long nanoTime()>");
    ret.add("<java.lang.Class: java.lang.String getName()>");
    ret.add("<java.lang.Object: java.lang.Class getClass()>");
    ret.add("<java.lang.StringValue: char[] 'from'(char[])>");
    ret.add("<java.util.Arrays: java.lang.Object[] copyOf(java.lang.Object[],int)>");
    ret.add("<java.lang.Integer: void <init>(int)>");
    ret.add("<java.lang.Integer: java.lang.Integer valueOf(int)>");
    ret.add("<java.lang.String: java.lang.String substring(int)>");
    ret.add("<java.lang.String: java.lang.String substring(int,int)>");
    ret.add("<java.lang.String: int indexOf(java.lang.String)>");
    ret.add("<java.lang.String: int indexOf(java.lang.String,int)>");  
    ret.add("<java.lang.String: java.lang.String[] split(java.lang.String)>");
    ret.add("<java.lang.String: java.lang.String[] split(java.lang.String,int)>");
    ret.add("<java.lang.String: java.lang.String valueOf(java.lang.Object)>");
    ret.add("<java.lang.Integer: int parseInt(java.lang.String)>");
    ret.add("<java.lang.Long: long parseLong(java.lang.String)>");
    ret.add("<java.lang.Float: float parseFloat(java.lang.String)>");
    ret.add("<java.lang.Double: double parseDouble(java.lang.String)>");
    ret.add("<org.trifort.rootbeer.runtime.RootbeerAtomicInt: int atomicAdd(int)>");
    ret.add("<org.trifort.rootbeer.runtimegpu.GpuException: org.trifort.rootbeer.runtimegpu.GpuException arrayOutOfBounds(int,int,int)>");
    return ret;
  }

  public Set<String> getEmitUnmanged() {
    Set<String> ret = new HashSet<String>();
    ret.add("<org.trifort.rootbeer.runtimegpu.GpuException: org.trifort.rootbeer.runtimegpu.GpuException arrayOutOfBounds(int,int,int)>");
    return ret;
  }

  public Set<ArrayType> getExtraArrayTypes() {
    Set<ArrayType> ret = new HashSet<ArrayType>();
    ret.add(ArrayType.v(CharType.v(), 1));
    ret.add(ArrayType.v(RefType.v("java.lang.String"), 1));
    return ret;
  }

  public Set<String> getExtraMethods() {
    Set<String> ret = new HashSet<String>();
    ret.add("<org.trifort.rootbeer.runtimegpu.GpuException: org.trifort.rootbeer.runtimegpu.GpuException arrayOutOfBounds(int,int,int)>");
    ret.add("<org.trifort.rootbeer.runtimegpu.GpuException: void <init>()>");
    ret.add("<java.lang.String: void <init>(char[])>");
    ret.add("<java.lang.Object: int hashCode()>");
    ret.add("<java.lang.Boolean: java.lang.String toString(boolean)>");
    ret.add("<java.lang.Character: java.lang.String toString(char)>");
    ret.add("<java.lang.Double: java.lang.String toString(double)>");
    ret.add("<java.lang.Float: java.lang.String toString(float)>");
    ret.add("<java.lang.Integer: java.lang.String toString(int)>");
    ret.add("<java.lang.Long: java.lang.String toString(long)>");
    return ret;
  }
}
