/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.parser.cpp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class CppRunner {
  
  public CppRunner(){
  }
  
  public Reader run(String input) throws Exception {
    List<String> lines = getLines(input);
    StringBuilder builder = new StringBuilder();
    for(String str : lines){
      builder.append(str);
      builder.append("\n");
    }
    return new StringReader(builder.toString());
  }

  private List<String> getLines(String input) throws Exception {
    List<String> ret = new ArrayList<String>();
    Process p = Runtime.getRuntime().exec("cpp -P "+input);
    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
    while(true){
      String line = reader.readLine();
      if(line == null)
        break;
      ret.add(line);
    }
    reader.close();
    return ret;
  }
    
  public static void main(String[] args){  
    try {
      CppRunner runner = new CppRunner();
      String file = "/home/pcpratts/code/jpp/gcc-testsuite/c-c++-common/torture/complex-sign-mul-minus-one.c";
      Reader reader1 = runner.run(file);
      BufferedReader reader = new BufferedReader(reader1);
      while(true){
        String line = reader.readLine();
        if(line == null)
          break;
        System.out.println(line);
      }
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
}
