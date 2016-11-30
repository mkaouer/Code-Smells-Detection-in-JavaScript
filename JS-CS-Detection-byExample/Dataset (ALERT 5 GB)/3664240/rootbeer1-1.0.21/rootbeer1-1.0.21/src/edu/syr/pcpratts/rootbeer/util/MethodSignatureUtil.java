/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.util;

import java.util.ArrayList;
import java.util.List;

public class MethodSignatureUtil {

  private String m_className;
  private String m_returnType;
  private String m_methodName;
  private List<String> m_params;

  public MethodSignatureUtil(){  
  }
  
  public MethodSignatureUtil(String method_signature) {
    parse(method_signature);
  }
  
  public void parse(String signature){
    String[] tokens0 = signature.split(":");
    String cls = tokens0[0].trim();
    m_className = cls.substring(1);
    
    String method_sub_sig = tokens0[1].trim();
    method_sub_sig = method_sub_sig.substring(0, method_sub_sig.length()-1);
    
    String[] tokens1 = method_sub_sig.split("\\(");
    String front = tokens1[0].trim();
    String back = tokens1[1].trim();
    
    String[] tokens2 = front.split(" ");
    m_returnType = tokens2[0].trim();
    m_methodName = tokens2[1].trim();
    
    String params = back.substring(0, back.length()-1);
    String[] param_tokens = params.split(",");
    m_params = new ArrayList<String>();
    
    for(String param : param_tokens){
      String curr = param.trim();
      if(curr.equals("") == false){
        m_params.add(curr); 
      }
    }
  }
  
  private void print(){
    System.out.println("return_type: ["+m_returnType+"]");
    System.out.println("class_name: ["+m_className+"]");
    System.out.println("method_name: ["+m_methodName+"]");
    System.out.print("args: [");
    for(String arg : m_params){
      System.out.println("  {"+arg+"}");
    }
    System.out.println("]");
  }
  
  public String getClassName(){
    return m_className;
  }
  
  public void setClassName(String class_name){
    m_className = class_name; 
  }
  
  public String getReturnType(){
    return m_returnType;
  }
  
  public void setReturnType(String return_type){
    m_returnType = return_type; 
  }
  
  public String getMethodName(){
    return m_methodName;
  }
  
  public void setMethodName(String method_name){
    m_methodName = method_name; 
  }
  
  public List<String> getParameterTypes(){
    return m_params;
  }
  
  public void setParameterTypes(List<String> params){
    m_params = params; 
  }
  
  public String getMethodSubSignature(){
    StringBuilder ret = new StringBuilder();
    ret.append(m_returnType);
    ret.append(" ");
    ret.append(m_methodName);
    ret.append("(");
    for(int i = 0; i < m_params.size(); ++i){
      ret.append(m_params.get(i));
      if(i < m_params.size() - 1){
        ret.append(","); 
      }
    }
    ret.append(")");
    return ret.toString();
  }
  
  @Override
  public String toString(){
    StringBuilder ret = new StringBuilder();
    ret.append("<");
    ret.append(m_className);
    ret.append(": ");
    ret.append(m_returnType);
    ret.append(" ");
    ret.append(m_methodName);
    ret.append("(");
    for(int i = 0; i < m_params.size(); ++i){
      ret.append(m_params.get(i));
      if(i < m_params.size() - 1){
        ret.append(","); 
      }
    }
    ret.append(")>");
    return ret.toString();
  }
  
  public static void main(String[] args){
    String sig = "<rootbeertest.GpuWorkItem: void <init>()>";
    MethodSignatureUtil util = new MethodSignatureUtil();
    util.parse(sig);
    util.print();
  }
}
