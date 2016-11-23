/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.parser.preprocessor;

import edu.syr.pcpratts.jpp.parser.Token;
import java.util.List;

public class Macro {

  private List<Token> m_Tokens;

  Macro(List<Token> tokens) {
    m_Tokens = tokens;
  }

}
