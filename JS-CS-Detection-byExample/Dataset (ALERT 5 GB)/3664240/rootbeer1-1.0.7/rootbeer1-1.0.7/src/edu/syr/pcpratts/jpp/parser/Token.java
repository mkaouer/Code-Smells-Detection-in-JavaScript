/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.parser;

public class Token {

  private String m_String;
  private boolean m_Immutable;
  private boolean m_Preprocessor;
  private boolean m_Comment;
  private boolean m_NewLine;

  public Token(String str){
    m_String = str;
    m_Immutable = false;
    if(stringIsComment())
      m_Comment = true;
    if(stringIsPreprocessor())
      m_Preprocessor = true;
  }

  public void setImmutable(){
    m_Immutable = true;
  }

  public boolean isComment(){
    return m_Comment;
  }

  public boolean isPreprocessor(){
    return m_Preprocessor;
  }

  public boolean isImmutable(){
    return m_Immutable;
  }

  public String getString(){
    return m_String;
  }

  @Override
  public String toString(){
    return m_String;
  }

  private boolean stringIsComment() {
    if(m_String.startsWith("//"))
      return true;
    if(m_String.startsWith("/*"))
      return true;
    return false;
  }

  private boolean stringIsPreprocessor() {
    if(m_String.startsWith("#"))
      return true;
    return false;
  }

  void setNewLine() {
    m_NewLine = true;
  }

  public boolean isNewLine(){
    if(m_String.equals("\n"))
      return true;
    if(m_String.equals("\r"))
      return true;
    return m_NewLine;
  }

  void setComment() {
    m_Comment = true;
  }
}
