/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime2.cuda;

import java.util.ArrayList;
import java.util.List;

public class PointerStack {

  private List<Long> m_Stack;
  private int m_Index;
  private final int m_DefaultDepth;
  
  public PointerStack(){
    m_DefaultDepth = 16;
    m_Stack = new ArrayList<Long>(m_DefaultDepth);
    m_Index = 0;
    for(int i = 0; i < m_DefaultDepth; ++i)
      m_Stack.add(0L);
  }
  
  public void push(long value){
    m_Index++;
    if(m_Index < m_DefaultDepth){
      m_Stack.set(m_Index, value);
      return;
    } else {   
      while(m_Stack.size() <= m_Index){
        m_Stack.add(0L);
      }
      m_Stack.set(m_Index, value);
    }
  }
  
  public long pop(){
    long ret = m_Stack.get(m_Index);
    m_Index--;
    return ret;
  }
}
