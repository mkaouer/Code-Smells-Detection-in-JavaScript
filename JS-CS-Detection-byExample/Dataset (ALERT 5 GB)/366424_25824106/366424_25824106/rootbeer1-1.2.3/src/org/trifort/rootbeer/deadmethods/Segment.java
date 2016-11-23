/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.deadmethods;

public class Segment {

  private String m_str;
  private int m_type;
  
  public Segment(String str, int type){
    m_str = str;
    m_type = type;
  }
  
  public String getString(){
    return m_str;
  }
  
  public int getType(){
    return m_type;
  }
  
  @Override
  public String toString(){
    if(m_type == SegmentParser.TYPE_FREE){
      return "TYPE_FREE: " + m_str;
    } else if(m_type == SegmentParser.TYPE_COMMENT){
      return "TYPE_COMMENT: " + m_str;
    } else if(m_type == SegmentParser.TYPE_COMMENT){
      return "TYPE_COMMENT: " + m_str;
    } else if(m_type == SegmentParser.TYPE_STRING){
      return "TYPE_STRING: " + m_str;
    } else if(m_type == SegmentParser.TYPE_CHAR){
      return "TYPE_CHAR: " + m_str;
    } else if(m_type == SegmentParser.TYPE_DEFINE){
      return "TYPE_DEFINE: " + m_str;
    } 
    throw new RuntimeException("unknown type: "+m_type+" str: "+m_str);
  }
}