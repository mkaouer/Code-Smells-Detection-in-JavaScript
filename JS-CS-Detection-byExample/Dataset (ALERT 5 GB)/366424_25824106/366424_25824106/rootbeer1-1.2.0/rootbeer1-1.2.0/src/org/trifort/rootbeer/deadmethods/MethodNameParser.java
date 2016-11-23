/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.deadmethods;

import java.util.ArrayList;
import java.util.List;

public class MethodNameParser {
  
  public List<String> parse(List<Block> blocks){
    List<String> ret = new ArrayList<String>();
    for(Block block : blocks){
      if(block.isMethod()){
        String name = parseMethodName(block);
        ret.add(name);
        Method method = new Method(name);
        block.setMethod(method);
      }
    }
    return ret;
  }

  private String parseMethodName(Block block) {
    String str = block.getFullString();
    int pos = str.indexOf('(');
    int first_char_pos = pos - 1;
    while(first_char_pos >= 0){
      char c = str.charAt(first_char_pos);
      if(c == ' '){
        --first_char_pos;
      } else {
        break;
      }
    }
    int first_space_pos = first_char_pos - 1;
    while(first_space_pos >= 0){
      char c = str.charAt(first_space_pos);
      if(c == ' '){
        break;
      } else {
        --first_space_pos;
      }
    }
    String method_name = str.substring(first_space_pos+1, first_char_pos+1);
    return method_name;
  }
}
