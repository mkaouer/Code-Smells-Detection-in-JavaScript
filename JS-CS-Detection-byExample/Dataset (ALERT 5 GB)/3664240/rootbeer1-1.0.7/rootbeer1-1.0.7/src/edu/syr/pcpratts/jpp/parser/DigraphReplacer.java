/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.parser;

import java.util.Iterator;

public class DigraphReplacer extends AbstractTokenizer {

  public DigraphReplacer(Iterator<Token> iter){
    m_LowerLevel = new ConcreteIteratorEx<Token>(iter);
  }

  @Override
  protected void parseNextToken() {
    Token token = m_LowerLevel.next();
    token = replace(token);
    m_Tokens.add(token);
  }

  private Token replace(Token token) {
    String str = token.getString();
    if(str.equals("<:"))
      return new Token("[");
    if(str.equals(":>"))
      return new Token("]");
    if(str.equals("<%"))
      return new Token("{");
    if(str.equals("%>"))
      return new Token("}");
    if(str.equals("%:"))
      return new Token("#");
    return token;
  }
}
