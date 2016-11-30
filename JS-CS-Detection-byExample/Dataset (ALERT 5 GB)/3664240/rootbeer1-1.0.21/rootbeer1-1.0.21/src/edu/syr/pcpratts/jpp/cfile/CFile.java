/* Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.cfile;

import edu.syr.pcpratts.jpp.parser.PutbackIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CFile {

  private List<CFileItem> m_Items;

  public CFile(){
    m_Items = new ArrayList<CFileItem>();
  }

  public CFile(PutbackIterator<CFileItem> iter){
    m_Items = new ArrayList<CFileItem>();
    while(iter.hasNext()){
      m_Items.add(iter.next());
    }
  }

  public void addItem(CFileItem item) {
    m_Items.add(item);
  }

  @Override
  public String toString(){
    String ret = "";
    for(CFileItem item : m_Items){
      ret += item.toString() + "\n";
    }
    return ret;
  }

  public PutbackIterator<CFileItem> iterator(){
    Iterator<CFileItem> iter = m_Items.iterator();
    return new PutbackIterator<CFileItem>(iter);
  }
}
