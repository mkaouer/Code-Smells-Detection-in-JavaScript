/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.parser;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Split the 'Everything else' from StringAndCommentTokenizer into
 * space separated tokens.  Leave strings and comments as is
 * @author pcpratts
 */
public class Tokenizer extends AbstractTokenizer {

  public Tokenizer(Iterator<Token> iter){
    m_LowerLevel = new ConcreteIteratorEx<Token>(iter);
    m_Tokens = new LinkedList<Token>();
  }

  protected void parseNextToken() {
    Token big_token;
    while(true){
      big_token = m_LowerLevel.next();
      if(big_token.isComment() && m_LowerLevel.hasNext()){
        continue;
      } else {
        break;
      }
    } 
    String next = big_token.getString();
    if(big_token.isImmutable() || big_token.isNewLine()){
      m_Tokens.add(big_token);
    } else {
      String[] tokens = next.split("\\s+");
      for(String token : tokens){
        m_Tokens.add(new Token(token));
      }
    }
  }
}
