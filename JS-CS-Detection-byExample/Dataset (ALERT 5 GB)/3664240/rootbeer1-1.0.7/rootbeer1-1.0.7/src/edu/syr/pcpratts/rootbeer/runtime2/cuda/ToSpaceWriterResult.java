/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime2.cuda;

import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import java.util.List;

public class ToSpaceWriterResult {
  
  private List<Long> m_Handles;
  private List<Kernel> m_Items;
  private List<Kernel> m_NotWrittenItems;
  
  public ToSpaceWriterResult(List<Long> handles, List<Kernel> items,
    List<Kernel> not_written){
    
    m_Handles = handles;
    m_Items = items;
    m_NotWrittenItems = not_written;
  }
  
  public List<Long> getHandles(){
    return m_Handles;
  }
  
  public List<Kernel> getItems(){
    return m_Items;
  }
  
  public List<Kernel> getNotWrittenItems(){
    return m_NotWrittenItems; 
  }
}
