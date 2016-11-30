/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.runtime;

import java.util.ArrayList;
import java.util.List;


public class BufferPrinter {

  public void print(byte[] array){
    List<String> lines = new ArrayList<String>();
    String curr_line = "";
    int elements_per_line = 16;

    int item_count = 1;
    for(int i = 0; i < array.length; ++i){
      String curr = getString(array[i]);
      curr_line += curr+" ";
      if(item_count >= elements_per_line){
        lines.add(curr_line);
        curr_line = "";
        item_count = 1;
      } else {
        item_count++;
      }
    }
    if(curr_line.equals("") == false)
      lines.add(curr_line);

    List<String> line_numbers = createLineNumbers(lines.size(), elements_per_line, 0);
    for(int i = 0; i < lines.size(); ++i){
      System.out.print(line_numbers.get(i));
      System.out.println(lines.get(i));
    }
  }
  
  public void print(Memory mem, long start_ptr, int length){
    long previous = mem.getPointer();
    mem.setAddress(start_ptr);

    List<String> lines = new ArrayList<String>();
    String curr_line = "";
    int elements_per_line = 16;

    int item_count = 1;
    for(int i = 0; i < length; ++i){
      String curr = getString(mem.readByte());
      curr_line += curr+" ";
      if(item_count >= elements_per_line){
        lines.add(curr_line);
        curr_line = "";
        item_count = 1;
      } else {
        item_count++;
      }
    }
    if(curr_line.equals("") == false)
      lines.add(curr_line);

    List<String> line_numbers = createLineNumbers(lines.size(), elements_per_line, start_ptr);
    for(int i = 0; i < lines.size(); ++i){
      System.out.print(line_numbers.get(i));
      System.out.println(lines.get(i));
    }
    
    mem.setAddress(previous);
  }

  private String getString(byte data) {
    String ret = Integer.toHexString(data);
    while(ret.length() < 2){
      ret = "0"+ret;
    }
    return ret.substring(ret.length()-2);
  }

  private List<String> createLineNumbers(int num_lines, int elements_per_line,
    long start_ptr) {

    List<String> unpadded = new ArrayList<String>();
    for(int i = 0; i < num_lines; ++i){
      long line_num = start_ptr + (i * elements_per_line);
      unpadded.add(line_num+": ");
    }
    int max_len = unpadded.get(unpadded.size()-1).length();
    List<String> ret = new ArrayList<String>();
    for(int i = 0; i < unpadded.size(); ++i){
      String curr = unpadded.get(i);
      while(curr.length() < max_len)
        curr = " "+curr;
      ret.add(curr);
    }
    return ret;
  }
}
