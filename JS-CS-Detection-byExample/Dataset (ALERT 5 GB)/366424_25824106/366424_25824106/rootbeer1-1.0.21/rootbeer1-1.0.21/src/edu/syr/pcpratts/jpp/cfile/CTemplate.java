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

public class CTemplate extends CFileItem {

  private List<CFileItem> m_Items;

  public CTemplate(List<CFileItem> items){
    m_Items = items;
  }

  public List<Token> getTokens() {
    List<Token> ret = new ArrayList<Token>();
    for(CFileItem item : m_Items){
      ret.addAll(item.getTokens());
    }
    return ret;
  }

  @Override
  public String toString(){
    String ret = "";

    //ret += getNamespace();

    for(CFileItem stmt : m_Items){
      ret += stmt.toString() + " ";
    }
    return ret;
  }
}
