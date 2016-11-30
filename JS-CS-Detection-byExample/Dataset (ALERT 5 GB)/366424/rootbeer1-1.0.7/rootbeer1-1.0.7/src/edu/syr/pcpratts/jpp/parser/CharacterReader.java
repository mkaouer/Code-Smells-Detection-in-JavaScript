/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.parser;

import java.io.BufferedReader;
import java.util.Iterator;

/**
 * Read a single character at a time from a BufferedReader.  If the character
 * is an escape character, read two.
 * @author pcpratts
 */
public class CharacterReader implements Iterator<Token> {

  private BufferedReader m_Reader;
  private String m_NextOutput;
  private boolean m_NeedToProcessHasNext;
  private boolean m_HasNext;

  public CharacterReader(BufferedReader reader){
    m_Reader = reader;
    m_NeedToProcessHasNext = true;
    m_HasNext = false;
  }

  public boolean hasNext(){
    if(m_NeedToProcessHasNext == false)
      return m_HasNext;
    processHasNext();
    m_NeedToProcessHasNext = false;
    return m_HasNext;
  }

  private void processHasNext(){
    try {
      int c = m_Reader.read();
      if(c == -1){
        m_HasNext = false;
        return;
      }
      //page break
      if(c == 0x0c){
        c = '\n';
      }
      if(c == '\\'){
        c = m_Reader.read();
        if(c == 0x0c){
          c = '\n';
        }
        if(c == -1){
          m_HasNext = false;
          m_NextOutput = "\\";
        } else {
          m_NextOutput = "\\" + (char) c;
          m_HasNext = true; 
        }
      } else {
        m_NextOutput = "" + (char) c;
        m_HasNext = true;
      }
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
    m_NeedToProcessHasNext = false;
  }

  public Token next() {
    if(m_HasNext == false)
      throw new RuntimeException("end of stream");
    if(m_NeedToProcessHasNext)
      throw new RuntimeException("need to process hasNext");
    m_NeedToProcessHasNext = true;
    return new Token(m_NextOutput);
  }

  public void remove() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
