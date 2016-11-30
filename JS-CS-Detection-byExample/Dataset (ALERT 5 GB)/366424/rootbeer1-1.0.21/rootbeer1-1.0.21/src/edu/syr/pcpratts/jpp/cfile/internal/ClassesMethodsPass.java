/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.cfile.internal;

import edu.syr.pcpratts.jpp.cfile.Pass;
import edu.syr.pcpratts.jpp.cfile.CClass;
import edu.syr.pcpratts.jpp.cfile.CFile;
import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.CMethod;
import edu.syr.pcpratts.jpp.parser.PutbackIterator;
import java.util.ArrayList;
import java.util.List;

class ClassesMethodsPass implements Pass {
  private int m_PrevCount;
  private int m_Count;
  private CFile m_NextFile;

  public CFile execute(PutbackIterator<CFileItem> iter) {
    m_PrevCount = -1;
    m_Count = 0;
    CFile prev_file = new CFile(iter);

    while(m_Count != m_PrevCount){
      m_PrevCount = m_Count;
      m_Count = 0;
      iter = prev_file.iterator();
      m_NextFile = new CFile();
      List<CFileItem> ret = handleIterator(iter);
      for(CFileItem item : ret){
        m_NextFile.addItem(item);
      }
      prev_file = m_NextFile;
    }
    return prev_file;
  }

  private List<CFileItem> handleIterator(PutbackIterator<CFileItem> iter){
    List<CFileItem> ret = new ArrayList<CFileItem>();
    while(iter.hasNext()){
      m_Count++;
      CFileItem next = iter.next();
      if(next instanceof CClass){
        CClass cclass = (CClass) next;
        Pass pass = new MethodPass();
        List<CFileItem> new_list = createNewList(pass, cclass.subiterator());
        cclass.subreplace(new_list);
        ret.add(cclass);
      } else if(next instanceof CMethod){
        CMethod cmethod = (CMethod) next;
        Pass pass = new ClassPass();
        List<CFileItem> new_list = createNewList(pass, cmethod.subiterator());
        cmethod.subreplace(new_list);
        ret.add(cmethod);
      } else {
        ret.add(next);
      }
    }
    return ret;
  }

  List<CFileItem> createNewList(Pass pass, PutbackIterator<CFileItem> iter){
    CFile file = pass.execute(iter);
    PutbackIterator<CFileItem> iter2 = file.iterator();
    return handleIterator(iter2);
  }
}
