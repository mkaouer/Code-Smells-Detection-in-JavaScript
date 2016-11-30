/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.cfile;

import edu.syr.pcpratts.jpp.parser.Token;
import java.util.List;


public class CParsedStatement extends CFileItem {

  private List<CFileItem> m_Items;
  
  @Override
  public List<Token> getTokens() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
}
