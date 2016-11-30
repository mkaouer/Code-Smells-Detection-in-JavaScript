/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.bytecode;

import java.util.ArrayList;
import java.util.List;

public class GpuCodeSplitter {

  public List<String> split(String code){
    List<String> ret = new ArrayList<String>();
    String[] lines = code.split("\n");
    
    StringBuilder curr_block = new StringBuilder();
    for(int i = 0; i < lines.length; ++i){
      String line = lines[i];
      if(curr_block.length() + line.length() > 60000){
        ret.add(curr_block.toString());
        curr_block = new StringBuilder();
        curr_block.append(line);
        curr_block.append("\n");
      } else {        
        curr_block.append(line);
        curr_block.append("\n");
      }
    }
    String last_line = curr_block.toString();
    if(last_line.length() > 0){
      ret.add(last_line);
    }
    return ret;
  }
}
