/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.parser;

import java.util.ArrayList;
import java.util.List;

public class TokenizationSplitter {

  public List<Token> split(Token tok, String splitter){
    List<Token> ret = new ArrayList<Token>();
    String curr = "";
    String tok_str = tok.getString();
    for(int i = 0; i < tok_str.length(); ){
      String remainder = tok_str.substring(i);
      if(remainder.startsWith(splitter)){
        if(curr.equals("") == false){
          ret.add(new Token(curr));
          curr = "";
        }
        Token new_token = new Token(splitter);
        new_token.setImmutable();
        ret.add(new_token);
        i += splitter.length();
      } else {
        curr += remainder.charAt(0);
        i += 1;
      }
    }
    if(curr.equals("") == false){
      Token new_token = new Token(curr);
      if(curr.equals(splitter))
        new_token.setImmutable();
      ret.add(new_token);
    }    
    return ret;
  }
  
  public static void main(String[] args){
    Token tok = new Token("if(( var = new Hello()) == 0){");
    TokenizationSplitter split_engine = new TokenizationSplitter();
    List<Token> tokens = split_engine.split(tok, "(");
    for(Token token : tokens){
      System.out.println(token.getString());
    }
  }
}
