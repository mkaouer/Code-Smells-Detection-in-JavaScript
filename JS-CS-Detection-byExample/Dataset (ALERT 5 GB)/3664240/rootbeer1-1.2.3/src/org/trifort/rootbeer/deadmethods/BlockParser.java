/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.deadmethods;

import java.util.ArrayList;
import java.util.List;

public class BlockParser {
  
  public static final int TYPE_FREE = 0;
  public static final int TYPE_DEFINE = 1;
  public static final int TYPE_DECLARE = 2;
  public static final int TYPE_METHOD = 3;

  public List<Block> parse(List<Segment> segments) {
    List<Block> ret = new ArrayList<Block>();
    for(int i = 0; i < segments.size(); ++i){
      Segment segment = segments.get(i);
      if(segment.getType() == SegmentParser.TYPE_DEFINE){
        ret.add(new Block(segment, TYPE_DEFINE));
      } else if(segment.getType() == SegmentParser.TYPE_FREE){
        String str = segment.getString().trim();
        if(str.isEmpty()){
          continue;
        }
        char last_char = str.charAt(str.length() - 1);
        if(last_char == ';'){
          ret.add(new Block(segment, TYPE_DECLARE));
        } else {
          List<Segment> block_segments = new ArrayList<Segment>();
          block_segments.add(segment);
          int brace_count = 0;
          if(last_char == '{'){
            brace_count++;
          }
          for(int j = i + 1; j < segments.size(); ++j){
            Segment curr = segments.get(j);
            if(curr.getType() == SegmentParser.TYPE_COMMENT){
              continue;
            }
            block_segments.add(curr);
            String str2 = curr.getString().trim();
            if(str2.isEmpty()){
              continue;
            }
            char last_char2 = str2.charAt(str2.length() - 1);
            if(last_char2 == ';' && brace_count == 0){
              ret.add(new Block(block_segments, TYPE_DECLARE));
              i = j;
              break;
            }
            if(last_char2 == '{'){
              brace_count++;
            } else if(last_char2 == '}'){
              brace_count--;
              if(brace_count == 0){
                ret.add(new Block(block_segments, TYPE_METHOD));
                i = j;
                break;
              }
            }
          }
        }
      }
    }
    return ret;
  }
  
}
