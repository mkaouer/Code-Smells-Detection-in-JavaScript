/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.testcases.rootbeertest.baseconversion;

public class IntList {

  private int[] m_Items;
  private int m_Count;
    
  public IntList(){
    m_Items = create(3);
    m_Count = 0;
  }
  
  public void add(int item){
    if(m_Count == m_Items.length){
      realloc();
    }
    int count = m_Count;
    m_Items[count] = item;
    m_Count = (count + 1);
  }
  
  private void realloc() {
    int[] new_items = create(m_Items.length * 2);
    for(int i = 0; i < m_Items.length; ++i){
      new_items[i] = m_Items[i];
    }
    m_Items = new_items;
  }
  
  public void remove(int index){
    for(int i = index; i < m_Count - 2; ++i){
      m_Items[i] = m_Items[i + 1];
    }
    m_Count--;
  }
  
  public int get(int index){
    return m_Items[index];
  }

  public int size() {
    return m_Count;
  }

  private int[] create(int size) {
    return new int[size];
  }

  void set(int ret_index, int value) {
    m_Items[ret_index] = value;
  }  
}
