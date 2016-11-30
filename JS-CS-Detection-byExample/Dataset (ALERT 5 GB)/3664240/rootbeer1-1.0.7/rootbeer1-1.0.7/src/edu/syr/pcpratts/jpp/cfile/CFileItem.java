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

public abstract class CFileItem {

  private CNamespace m_Namespace;
  private List<CTemplate> m_Templates;

  public CFileItem(){
    m_Templates = new ArrayList<CTemplate>();
  }

  public void addNamespace(CNamespace namespace){
    m_Namespace = namespace;
  }

  public CNamespace getNamespace(){
    return m_Namespace;
  }

  public void addTemplate(CTemplate template){
    m_Templates.add(template);      
  }
  
  public List<CTemplate> getTemplates(){
    return m_Templates;
  }
  
  public abstract List<Token> getTokens();
}
