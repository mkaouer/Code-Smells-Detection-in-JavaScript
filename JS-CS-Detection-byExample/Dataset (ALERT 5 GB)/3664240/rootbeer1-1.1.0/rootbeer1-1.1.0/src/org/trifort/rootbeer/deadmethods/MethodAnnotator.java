/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.deadmethods;

import java.util.ArrayList;
import java.util.List;

public class MethodAnnotator {
  
  public void parse(List<Block> blocks, List<String> method_names){
    for(Block block : blocks){
      if(block.isMethod() == false){
        continue;
      }
      parseMethod(block, method_names);
    }
  }

  private void parseMethod(Block block, List<String> method_names) {
    String str = block.getFullStringNoStrings();
    String block_name = block.getMethod().getName();

    List<String> invoked = new ArrayList<String>();
    for(String method_name : method_names){
      if(method_name.equals(block_name)){
        continue;
      }
      int start_pos = 0;
      
      outer_while:
      while(true){
        int pos = str.indexOf(method_name, start_pos);
        if(pos == -1 || pos == 0){
          break;
        }
        char c1 = str.charAt(pos - 1);
        if(Character.isLetter(c1) || Character.isDigit(c1)){
          start_pos += method_name.length();
          continue;
        }
        pos += method_name.length();
        while(pos < str.length()){
          char c2 = str.charAt(pos);
          if(c2 == ' ' || c2 == '\n'){
            pos++;
            continue;
          } else if(c2 == '('){
            invoked.add(method_name);
            break outer_while;
          } else {
            start_pos += method_name.length();
            break;
          }
        }
      }
    }
    block.getMethod().setInvoked(invoked);
  }
}
