/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewlineSplicer implements Iterator<Token> {

  private Iterator<Token> m_LowerLevel;
  private Token m_Curr;
  private Token m_Next;

  private int m_CharsLeft;
  private List<Token> m_Putbacks;

  public NewlineSplicer(Iterator<Token> iter) {
    m_LowerLevel = iter;
    if(m_LowerLevel.hasNext())
      m_Curr = m_LowerLevel.next();
    if(m_LowerLevel.hasNext())
      m_Next = m_LowerLevel.next();
    m_CharsLeft = -1;
    m_Putbacks = new ArrayList<Token>();
  }

  public boolean hasNext() {
    if(m_Putbacks.isEmpty() == false)
      return true;
    if(m_LowerLevel.hasNext() == false){
      if(m_CharsLeft == -1){
        m_CharsLeft = 3;
      }
      m_CharsLeft--;
      if(m_CharsLeft > 0)
        return true;
      return false;
    } else {
      return true;
    }
  }

  public Token next() {
    Token ret;
    if(m_Putbacks.isEmpty() == false){
      ret = m_Putbacks.get(0);
      m_Putbacks.remove(0);
      return ret;
    } 
    if(shouldSplice()){
      read();
      if(m_LowerLevel.hasNext())
        read();
      ret = new Token(" ");
    } else {
      ret = m_Curr;
      read();
    }
    return ret;
  }

  private void read(){
    m_Curr = m_Next;

    if(m_CharsLeft != -1)
      m_Next = new Token("");
    else
      m_Next = m_LowerLevel.next();
  }

  public void remove() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  private boolean shouldSplice() {
    //handles empty file
    if(m_Curr == null)
      return false;
    
    if(m_Curr.getString().equals("\\")){
      List<Token> temp_putbacks = new ArrayList<Token>();
      while(m_Next.getString().equals(" ")){
        m_Putbacks.add(m_Curr);
        read();
        if(m_LowerLevel.hasNext() == false)
          break;
      }
      if(m_Next.getString().equals("\n")){
        m_Putbacks.clear();
        return true;
      } else if (m_Next.getString().equals("\r")){
        m_Putbacks.clear();
        return true;
      } else {
        m_Putbacks.addAll(temp_putbacks);
      }
    }
    return false;
  }
}
