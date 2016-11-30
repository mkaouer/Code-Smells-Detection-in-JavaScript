/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.deadmethods;

import java.util.ArrayList;
import java.util.List;

public class SegmentParser {

  public static final int TYPE_FREE = 0;
  public static final int TYPE_COMMENT = 1;
  public static final int TYPE_STRING = 2;
  public static final int TYPE_CHAR = 3;
  public static final int TYPE_DEFINE = 4;
  
  public SegmentParser(){
  }
  
  public List<Segment> parse(String contents) {
    List<Segment> ret = new ArrayList<Segment>();
    int state = TYPE_FREE;
    StringBuilder accum = new StringBuilder();
    for(int i = 0; i < contents.length(); ++i){
      char c = contents.charAt(i);
      char cc = '\0';
      if(i < contents.length() - 1){
        cc = contents.charAt(i+1);
      }
      
      switch(state){
        case TYPE_FREE:
          if(c == '/' && cc == '/'){
            if(accum.length() != 0){
              ret.add(new Segment(accum.toString(), TYPE_FREE));
            }
            accum = new StringBuilder("//");
            state = TYPE_COMMENT;
            ++i;
          } else if(c == '\"'){
            if(accum.length() != 0){
              ret.add(new Segment(accum.toString(), TYPE_FREE));
            }
            accum = new StringBuilder("\"");
            state = TYPE_STRING;
          } else if(c == '\''){
            if(accum.length() != 0){
              ret.add(new Segment(accum.toString(), TYPE_FREE));
            }
            accum = new StringBuilder("\'");
            state = TYPE_CHAR;
          } else if(c == '#' && onlyWhitespace(accum)){
            if(accum.length() != 0){
              ret.add(new Segment(accum.toString(), TYPE_FREE));
            }
            accum = new StringBuilder("#");
            state = TYPE_DEFINE;
          } else if(c == '}' || c == '{'){
            accum.append(c);
            ret.add(new Segment(accum.toString(), TYPE_FREE));
            accum = new StringBuilder("");
          } else if(c == '\n'){
            if(accum.length() != 0){
              ret.add(new Segment(accum.toString(), TYPE_FREE));
            }
            accum = new StringBuilder("");
          } else {
            accum.append(c);
          }
          break;
        case TYPE_COMMENT:
          if(c == '\n'){
            if(insideEscape(contents, i - 1)){
              accum.append(c);
            } else {
              if(accum.length() != 0){
                ret.add(new Segment(accum.toString(), TYPE_COMMENT));
              }
              accum = new StringBuilder();
              state = TYPE_FREE;
            }
          } else {
            accum.append(c);
          }
          break;
        case TYPE_STRING:
          if(c == '\"'){
            if(insideEscape(contents, i - 1)){
              accum.append(c);
            } else {
              accum.append(c);
              ret.add(new Segment(accum.toString(), TYPE_STRING));
              accum = new StringBuilder();
              state = TYPE_FREE;
            }
          } else {
            accum.append(c);
          }
          break;
        case TYPE_CHAR:
          if(c == '\''){
            if(insideEscape(contents, i - 1)){
              accum.append(c);
            } else {
              accum.append(c);
              ret.add(new Segment(accum.toString(), TYPE_CHAR));
              accum = new StringBuilder();
              state = TYPE_FREE;
            }
          } else {
            accum.append(c);
          }
          break;
        case TYPE_DEFINE:
          if(c == '\n'){
            if(insideEscape(contents, i - 1)){
              accum.append(c);
            } else {
              if(accum.length() != 0){
                ret.add(new Segment(accum.toString(), TYPE_DEFINE));
              }
              accum = new StringBuilder();
              state = TYPE_FREE;
            }
          } else {
            accum.append(c);
          }
          break;
      }
    }
    if(accum.length() != 0){
      ret.add(new Segment(accum.toString(), state));
    }
    return ret;
  }
  
  private boolean insideEscape(String contents, int index){
    int count = 0;
    for(int i = index; i >= 0; --i){
      char c = contents.charAt(i);
      if(c == '\\'){
        ++count;
      } else {
        break;
      }
    }
    if(count % 2 == 0){
      return false;
    } else {
      return true;
    }
  }
  
  private boolean onlyWhitespace(StringBuilder builder){
    for(int i = 0; i < builder.length(); ++i){
      char c = builder.charAt(i);
      if(c != ' ' && c != '\n'){
        return false;
      }
    }
    return true;
  }
}
