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

public class CNamespace extends CFileItem {

  private String m_Namespace;

  public CNamespace(String namespace) {
    m_Namespace = namespace;
  }

  public List<Token> getTokens() {
    List<Token> ret = new ArrayList<Token>();
    return ret;
  }

  @Override
  public String toString(){
    return m_Namespace;
  }
}
