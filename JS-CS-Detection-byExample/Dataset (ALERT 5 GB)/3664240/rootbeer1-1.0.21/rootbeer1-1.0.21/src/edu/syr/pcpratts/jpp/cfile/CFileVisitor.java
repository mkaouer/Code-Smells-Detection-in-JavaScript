/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.cfile;

import edu.syr.pcpratts.jpp.parser.PutbackIterator;

public abstract class CFileVisitor {

  boolean m_UseSubIterators;
  
  public CFileVisitor(boolean use_subiterators){
    m_UseSubIterators = use_subiterators;  
  }
  
  public void visit(CFile cfile){
    PutbackIterator<CFileItem> iter = cfile.iterator();
    doVisit(iter);
  }  

  private void doVisit(PutbackIterator<CFileItem> iter) {
    while(iter.hasNext()){
      CFileItem next = iter.next();
      visitItem(next);
      if(next instanceof CClass){
        CClass cclass = (CClass) next;
        if(m_UseSubIterators)
          doVisit(cclass.subiterator());
        else 
          doVisit(cclass.iterator());
      } else if(next instanceof CMethod){
        CMethod cmethod = (CMethod) next;
        if(m_UseSubIterators)
          doVisit(cmethod.subiterator());
        else
          doVisit(cmethod.iterator());
      }
    }
  }
  
  protected abstract void visitItem(CFileItem item);
}
