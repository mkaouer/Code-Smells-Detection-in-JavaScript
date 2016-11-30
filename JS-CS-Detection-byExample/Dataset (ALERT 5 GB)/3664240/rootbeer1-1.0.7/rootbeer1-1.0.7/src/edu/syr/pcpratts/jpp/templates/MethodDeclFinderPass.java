/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.templates;

import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.CFileVisitor;

class MethodDeclFinderPass extends CFileVisitor {

  public MethodDeclFinderPass() {
    super(false);
  }

  @Override
  protected void visitItem(CFileItem item) {
    
  }
  
}
