/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.cfile;

import edu.syr.pcpratts.jpp.parser.Token;
import java.util.ArrayList;
import java.util.List;

public class CStatement extends CFileItem {

  private List<Token> m_Tokens;
  private int m_BraceCount;

  public CStatement(List<Token> tokens){
    m_Tokens = tokens;
  }

  public CStatement(Token token){
    m_Tokens = new ArrayList<Token>();
    m_Tokens.add(token);
  }

  public List<Token> getTokens(){
    return m_Tokens;
  }

  @Override
  public String toString(){
    int spaces = 2 * m_BraceCount;
    String ret = "";
    /*
    if(getTemplates().isEmpty() == false){
      ret += "/////////////////////////////////////////////////\n";
      for(CTemplate template : getTemplates()){
        ret += "//  Template: "+template.toString()+"\n";
      }
      ret += "/////////////////////////////////////////////////\n";      
    }
    */
    for(int i = 0; i < spaces; ++i){
      ret += " ";
    }
    for(int i = 0; i < m_Tokens.size(); ++i){
      String prev, curr, next;
      if(i - 1 >= 0){
        prev = m_Tokens.get(i-1).getString();
      } else {
        prev = ""; 
      }
      curr = m_Tokens.get(i).getString();
      if(i + 1 < m_Tokens.size()){
        next = m_Tokens.get(i+1).getString();
      } else {
        next = "";
      }
      ret += curr;
      if(shouldSpace(prev, curr, next)){
        ret += " ";
      }
    }
    if(ret.startsWith("#"))
      ret += "\n";
    return ret;
  }
  
  public String toPrettyString(){
    int spaces = 2 * m_BraceCount;
    String ret = "";
    for(int i = 0; i < spaces; ++i){
      ret += " ";
    }
    for(Token curr : m_Tokens){
      ret += curr.getString() + " ";
      if(curr.getString().equals(";"))
        ret += "\n";
    }
    if(ret.startsWith("#"))
      ret += "\n";
    return ret; 
  }

  public int braceCount(int count) {
    m_BraceCount = count;
    if(m_Tokens.size() < 1)
      return count;
    Token last = m_Tokens.get(m_Tokens.size()-1);
    if(last.getString().equals("{"))
      return count + 1;
    if(last.getString().equals("}")){
      m_BraceCount--;
      return count - 1;
    }
    return count;
  }

  private boolean shouldSpace(String prev, String curr, String next) {
    if(curr.equals("."))
      return false;
    if(next.equals("."))
      return false;
    if(curr.equals("-"))
      return false;
    if(next.equals("-"))
      return false;
    return true;
  }
}
