/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.typetable;

import edu.syr.pcpratts.jpp.cfile.CType;
import java.util.ArrayList;
import java.util.List;

public class BuiltInTypes {

  private List<CType> m_Types;
  
  public BuiltInTypes(){
    m_Types = new ArrayList<CType>();
    addSimpleType("void");
    addSimpleType("char");
    addSimpleType("short");
    addSimpleType("int");
    addSimpleType("long");
    addSimpleType("wchar_t");
    addSimpleType("bool");
    
    addType("unsigned wchar_t");
    addType("signed wchar_t");
    
    addType("unsigned char");
    addType("signed char");
    
    addType("unsigned short");
    addType("signed short");
    
    addType("unsigned int");
    addType("signed int");
    
    addType("unsigned long");
    addType("signed long");
        
    addType("short int");
    addType("unsigned short int");
    addType("signed short int");
    
    addType("long int");
    addType("unsigned long int");
    addType("signed long int");
    
    addType("long long");
    addType("unsigned long long");
    addType("signed long long");
  }
  
  public List<CType> get(){
    return m_Types;
  }
  
  private void addType(String type){
    String[] tokens = type.split("\\s+");
    Combinatorics combo = new Combinatorics();
    List<List<Integer>> combinations = combo.generateNoRepeat(tokens.length);
    for(List<Integer> combination : combinations){
      List<String> list = new ArrayList<String>();
      for(Integer index : combination){
        list.add(tokens[index]);
      }      
      m_Types.add(new CType(list));
    }
  }

  private void addSimpleType(String type) {
    List<String> tokens = new ArrayList<String>();
    tokens.add(type);
    m_Types.add(new CType(tokens));
  }
  
  public static void main(String[] args){
  
    BuiltInTypes builtins = new BuiltInTypes();
    List<CType> types = builtins.get();
    int index = 0;
    for(CType type : types){
      System.out.println(type.toString()+" x"+index+" = 0;");
      index++;
    }
    
  }
}
