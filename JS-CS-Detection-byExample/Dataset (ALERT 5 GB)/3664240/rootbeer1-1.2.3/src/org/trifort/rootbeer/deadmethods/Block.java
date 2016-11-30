/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.deadmethods;

import java.util.ArrayList;
import java.util.List;

public class Block {
  
  private List<Segment> m_segments;
  private int m_type;
  private Method m_method;
  private String m_fullString;
  private String m_fullStringNoStrings;
  
  public Block(List<Segment> segments, int type){
    m_segments = segments;
    m_type = type;
    if(type == BlockParser.TYPE_METHOD){
      StringBuilder builder1 = new StringBuilder();
      StringBuilder builder2 = new StringBuilder();
      for(int i = 0; i < segments.size(); ++i){
        Segment segment = segments.get(i);
        builder1.append(segment.getString());
        builder1.append(" ");
        if(segment.getType() != SegmentParser.TYPE_STRING){
          builder2.append(segment.getString());
          builder2.append(" ");
        }
      }
      m_fullString = builder1.toString();
      m_fullStringNoStrings = builder2.toString();
    }
  }
  
  public Block(Segment segment, int type){
    m_segments = new ArrayList<Segment>();
    m_segments.add(segment);
    m_type = type;
  }
  
  public int getType(){
    return m_type;
  }
  
  public List<Segment> getSegments(){
    return m_segments;
  }
  
  public String getFullString(){
    return m_fullString;
  }
  
  public String getFullStringNoStrings(){
    return m_fullStringNoStrings;
  }
  
  public boolean isMethod(){
    return getType() == BlockParser.TYPE_METHOD;
  }
  
  public Method getMethod(){
    return m_method;
  }
  
  public void setMethod(Method method){
    m_method = method;
  }
   
  @Override
  public String toString(){
    StringBuilder ret = new StringBuilder();
    if(m_type == BlockParser.TYPE_DECLARE || m_type == BlockParser.TYPE_DEFINE){
      for(int i = 0; i < m_segments.size(); ++i){
        Segment segment = m_segments.get(i);
        ret.append(segment.getString());
        ret.append("\n");
      }
    } else {
      for(int i = 0; i < m_segments.size(); ++i){
        Segment segment = m_segments.get(i);
        Segment nsegment = null;
        if(i < m_segments.size() - 1){
          nsegment = m_segments.get(i+1);
        }
        ret.append(segment.getString());
        if(segment.getType() != SegmentParser.TYPE_STRING &&
           (nsegment != null && nsegment.getType() != SegmentParser.TYPE_STRING)){
          ret.append("\n");
        }
      }
    }
    return ret.toString();
  }
}
