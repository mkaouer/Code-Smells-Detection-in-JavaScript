/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.templates;

import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.CFileVisitor;
import edu.syr.pcpratts.jpp.cfile.CStatement;
import edu.syr.pcpratts.jpp.typetable.TypeTable;

public class TypeAnnotatorPass extends CFileVisitor {

  private TypeTable m_TypeTable;  
  
  TypeAnnotatorPass(TypeTable type_table) {
    super(false);
    
    m_TypeTable = type_table;
  }

  @Override
  protected void visitItem(CFileItem item) {
    if(item instanceof CStatement == false)
      return;
    
    CStatement stmt = (CStatement) item;
    System.out.println(stmt.toString());
      
  }

}
