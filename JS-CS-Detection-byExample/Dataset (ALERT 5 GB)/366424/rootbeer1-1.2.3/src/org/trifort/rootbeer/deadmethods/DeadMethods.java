/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.deadmethods;

import java.util.List;
import java.util.Set;

import org.trifort.rootbeer.util.ReadFile;

public class DeadMethods {
  
  private List<Block> m_blocks;
  private Set<String> m_live;
  
  public void parseFile(String filename) {
    ReadFile reader = new ReadFile(filename);
    String contents = "";
    try {
      contents = reader.read();
    } catch(Exception ex){
      ex.printStackTrace(System.out);
    }
    
    parseString(contents);
  }
  
  public void parseString(String contents){
    SegmentParser segment_parser = new SegmentParser();
    List<Segment> segments = segment_parser.parse(contents);
    //for(Segment segment : segments){
    //  System.out.println(segment.toString());
    //}
    
    BlockParser block_parser = new BlockParser();
    List<Block> blocks = block_parser.parse(segments);
    //for(Block block : blocks){
    //  System.out.println("<block>");
    //  System.out.println(block.toString());
    //  System.out.println("</block>");
    //}
    
    MethodNameParser name_parser = new MethodNameParser();
    List<String> method_names = name_parser.parse(blocks);
    //for(Block block : blocks){
    //  if(block.isMethod()){
    //    Method method = block.getMethod();
    //    System.out.println(method.getName());
    //  }
    //}
    
    MethodAnnotator annotator = new MethodAnnotator();
    annotator.parse(blocks, method_names);
    //for(Block block : blocks){
    //  if(block.isMethod()){
    //    Method method = block.getMethod();
    //    System.out.println("name: "+method.getName());
    //    for(String invoked : method.getInvoked()){
    //      System.out.println("  invoked: "+invoked);
    //    }
    //  }
    //}
    
    m_blocks = blocks;
  }
  
  private String outputLive(List<Block> blocks, Set<String> live){
    StringBuilder ret = new StringBuilder();
    for(Block block : blocks){
      if(block.isMethod()){
        Method method = block.getMethod();
        String name = method.getName();
        if(live.contains(name) == false){
          continue;
        }
        ret.append(block.toString());
        ret.append("\n");
      } else {
        ret.append(block.toString());
        ret.append("\n");
      }
    }
    return ret.toString();  
  }
  
  public String getResult(){
    if(m_live == null){
      LiveMethodDetector detector = new LiveMethodDetector();
      m_live = detector.parse(m_blocks);
    }
    
    return outputLive(m_blocks, m_live);
  }
  
  public String getCompressedResult(){
    if(m_live == null){
      LiveMethodDetector detector = new LiveMethodDetector();
      m_live = detector.parse(m_blocks);
    }
    
    MethodNameCompressor compressor = new MethodNameCompressor();
    List<Block> blocks = compressor.parse(m_blocks, m_live);
    
    return outputLive(blocks, m_live);
  }
  
  public static void main(String[] args){
    DeadMethods dead_methods = new DeadMethods();
    dead_methods.parseFile("/home/pcpratts/.rootbeer/generated_unix.cu");
    String ret = dead_methods.getResult();
    //System.out.println(ret);
  }
}
