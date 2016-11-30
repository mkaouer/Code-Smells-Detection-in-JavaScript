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

public class CType extends CFileItem {

  private List<Token> m_Tokens;
  private List<CTemplate> m_Templates;
  private CNamespace m_Namespace;
  
  public CType(List<String> tokens){
    m_Tokens = new ArrayList<Token>();
    for(String token : tokens){
      m_Tokens.add(new Token(token));
    }
    m_Templates = new ArrayList<CTemplate>();
  }

  public CType(String type_name) {
    m_Tokens = new ArrayList<Token>();
    m_Tokens.add(new Token(type_name));
    m_Templates = new ArrayList<CTemplate>();
  }
  
  public void addTemplates(List<CTemplate> templates){
    m_Templates.addAll(templates);
  }
  
  public void setNamespace(CNamespace namespace){
    m_Namespace = namespace;
  }
  
  @Override 
  public String toString(){
    String ret = "";
    for(CTemplate template : m_Templates){
      ret += template.toString()+"\n";
    }
    if(m_Namespace != null){
      ret += m_Namespace.toString()+"::";
    }
    for(Token token : m_Tokens){
      ret += token.getString() + " ";
    }
    return ret;
  }

  @Override
  public List<Token> getTokens() {
    return m_Tokens;
  }
}
