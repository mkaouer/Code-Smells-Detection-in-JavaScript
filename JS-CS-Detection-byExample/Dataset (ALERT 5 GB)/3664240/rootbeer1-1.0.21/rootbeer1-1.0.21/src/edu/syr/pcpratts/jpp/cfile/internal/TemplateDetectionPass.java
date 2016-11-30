/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.cfile.internal;

import edu.syr.pcpratts.jpp.cfile.Pass;
import edu.syr.pcpratts.jpp.cfile.CFile;
import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.CStatement;
import edu.syr.pcpratts.jpp.cfile.CTemplate;
import edu.syr.pcpratts.jpp.parser.PutbackIterator;
import edu.syr.pcpratts.jpp.parser.Token;
import java.util.ArrayList;
import java.util.List;

public class TemplateDetectionPass implements Pass {

  private PutbackIterator<CFileItem> m_Iterator;

  public CFile execute(PutbackIterator<CFileItem> iter) {
    m_Iterator = iter;
    CFile ret = new CFile();
    while(iter.hasNext()){
      CFileItem next = m_Iterator.next();
      if(next instanceof CStatement == false){
        ret.addItem(next);
        continue;
      }
      CStatement stmt = (CStatement) next;
      List<Token> tokens = stmt.getTokens();
      if(isTemplateStatement(tokens)){
        CTemplate ctemplate = parseTemplate(stmt);
        ret.addItem(ctemplate);
      } else {
        ret.addItem(next);
      }
    }
    return ret;
  }

  private boolean isTemplateStatement(List<Token> tokens) {
    if(tokens.isEmpty())
      return false;
    Token first = tokens.get(0);
    if(first.getString().equals("template"))
      return true;
    return false;
  }

  private CTemplate parseTemplate(CStatement opening_statement) {
    List<CFileItem> stmts = new ArrayList<CFileItem>();
    List<Token> tokens = opening_statement.getTokens();
    stmts.add(new CStatement(tokens.get(0)));
    for(int i = 1; i < tokens.size(); ++i){
      m_Iterator.putback(new CStatement(tokens.get(i)));
    }
    int scope_count = 0;
    while(m_Iterator.hasNext()){
      CFileItem stmt = m_Iterator.next();
      List<Token> new_tokens = stmt.getTokens();
      if(new_tokens.isEmpty())
        continue;
      Token last = new_tokens.get(new_tokens.size()-1);
      if(last.getString().equals("<")){
        scope_count++;
      } else if(last.getString().equals(">")){
        scope_count--;
      }
      stmts.add(stmt);
      if(scope_count == 0)
        break;
    }
    return new CTemplate(stmts);
  }
}
