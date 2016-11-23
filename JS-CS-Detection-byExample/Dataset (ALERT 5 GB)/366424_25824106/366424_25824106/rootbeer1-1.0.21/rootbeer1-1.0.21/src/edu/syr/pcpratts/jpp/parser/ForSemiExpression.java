/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.parser;

import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.CStatement;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ForSemiExpression implements Iterator<CFileItem>{

  private Iterator<CStatement> m_LowerLevel;
  private int m_BraceCount;

  public ForSemiExpression(Iterator<CStatement> iter){
    m_LowerLevel = iter;
    m_BraceCount = 0;
  }
  
  public boolean hasNext() {
    return m_LowerLevel.hasNext();
  }

  public CStatement next() {
    List<Token> tokens = new ArrayList<Token>();
    CStatement stmt = m_LowerLevel.next();
    List<Token> curr = stmt.getTokens();
    tokens.addAll(curr);
    if(forTokenList(curr)){
      addTokens(tokens);
      addTokens(tokens);
    }
    CStatement ret = new CStatement(tokens);
    m_BraceCount = ret.braceCount(m_BraceCount);
    return ret;
  }

  private void addTokens(List<Token> existing){
    if(m_LowerLevel.hasNext() == false)
      return;
    existing.addAll(m_LowerLevel.next().getTokens());
  }

  public void remove() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  private boolean forTokenList(List<Token> curr) {
    if(curr.isEmpty())
      return false;
    Token first = curr.get(0);
    if(first.getString().equals("for"))
      return true;
    return false;
  }

}
