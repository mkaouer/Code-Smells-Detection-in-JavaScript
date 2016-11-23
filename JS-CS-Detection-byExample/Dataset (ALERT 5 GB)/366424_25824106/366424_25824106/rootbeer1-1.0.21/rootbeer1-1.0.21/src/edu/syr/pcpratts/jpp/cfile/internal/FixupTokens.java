/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.cfile.internal;

import edu.syr.pcpratts.jpp.cfile.CFile;
import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.CStatement;
import edu.syr.pcpratts.jpp.parser.ForSemiExpression;
import edu.syr.pcpratts.jpp.parser.ParserFactory;
import edu.syr.pcpratts.jpp.parser.PutbackIterator;
import edu.syr.pcpratts.jpp.parser.SemiExpression;
import edu.syr.pcpratts.jpp.parser.Token;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FixupTokens {

  private CFile m_Ret;

  public CFile fixup(CFile file){

    PutbackIterator<CFileItem> iter = file.iterator();
    m_Ret = new CFile();

    while(iter.hasNext()){
      CFileItem next = iter.next();
      if(next instanceof CStatement == false){
        m_Ret.addItem(next);
        continue;
      }

      //now we have found a CStatement
      List<Token> tokens = new ArrayList<Token>();
      tokens.addAll(next.getTokens());

      while(iter.hasNext()){
        CFileItem nnext = iter.next();
        if(nnext instanceof CStatement){
          tokens.addAll(nnext.getTokens());
        } else {
          addFixedTokens(tokens);
          tokens.clear();
          m_Ret.addItem(nnext);
          break;
        }
      }
      if(tokens.isEmpty() == false){
        addFixedTokens(tokens);
      }
    }

    return m_Ret;
  }

  private void addFixedTokens(List<Token> tokens) {
    Iterator<CStatement>  iter = new SemiExpression(tokens.iterator());
    Iterator<CFileItem>   semi = new ForSemiExpression(iter);
    while(semi.hasNext()){
      CFileItem next = semi.next();
      m_Ret.addItem(next);
    }
  }
}
