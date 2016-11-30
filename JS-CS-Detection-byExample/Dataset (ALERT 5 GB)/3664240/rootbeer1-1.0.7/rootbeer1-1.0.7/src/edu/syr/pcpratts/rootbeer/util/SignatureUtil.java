/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.util;

public class SignatureUtil {

  public String classFromMethodSig(String signature){
    String[] tokens = signature.split(":");
    String first = tokens[0].trim();
    return first.substring(1);
  }
  
  public String methodSubSigFromMethodSig(String signature){
    String[] tokens = signature.split(":");
    String second = tokens[1].trim();
    String[] tokens2 = second.split("\\)");
    return tokens2[0].trim() + ")";
  }
  
  public static void main(String[] args){
    String sig = "<rootbeertest.GpuWorkItem: void <init>()>";
    SignatureUtil util = new SignatureUtil();
    System.out.println(util.classFromMethodSig(sig));
    System.out.println(util.methodSubSigFromMethodSig(sig));
  }
}
