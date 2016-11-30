/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.parser;

import java.util.Iterator;

public class NewlineRemover extends AbstractTokenizer{

  public NewlineRemover(Iterator<Token> iter){
    m_LowerLevel = new ConcreteIteratorEx<Token>(iter);
  }

  @Override
  protected void parseNextToken() {
    Token token;
    while(true){
      token = m_LowerLevel.next();
      if(token.isNewLine() == false) {
        break;
      }
      if(m_LowerLevel.hasNext() == false){
        return;
      }
    }
    m_Tokens.add(token);
  }

}
