/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.cfile;

import edu.syr.pcpratts.jpp.parser.PutbackIterator;
import edu.syr.pcpratts.jpp.parser.Token;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CClass extends CFileItem {

  private List<CFileItem> m_Items;

  public CClass(List<CFileItem> tokens){
    CFileItem first = tokens.get(0);
    addNamespace(first.getNamespace());
    List<CTemplate> templates = first.getTemplates();
    for(CTemplate template : templates){
      addTemplate(template);
    }
    m_Items = tokens;
  }
  
  public CType getType(){
    CFileItem first = m_Items.get(0);
    List<Token> tokens = first.getTokens();
    String type_name = tokens.get(1).getString();
    CType ret = new CType(type_name);
    ret.addTemplates(getTemplates());
    ret.setNamespace(getNamespace());
    return ret;
  }

  @Override
  public String toString(){
    String ret = "";
    //ret += "/////////////////////////////////////////////////\n";
    //ret += "//  Parsed Class. "+getNamespace()+"  \n";
    //List<CTemplate> templates = getTemplates();
    //for(CTemplate template : templates){
    //  ret += "//  Template: "+template.toString()+"\n";
    //}
    //ret += "/////////////////////////////////////////////////\n";

    for(CFileItem stmt : m_Items){
      ret += stmt.toString() + "\n";
    }
    //ret += "/////////////////////////////////////////////////\n";
    //ret += "//  End Class. \n";
    //ret += "/////////////////////////////////////////////////\n";
    return ret;
  }

  public List<Token> getTokens() {
    List<Token> ret = new ArrayList<Token>();
    for(CFileItem stmt : m_Items){
      ret.addAll(stmt.getTokens());
    }
    return ret;
  }

  public PutbackIterator<CFileItem> subiterator(){
    List<CFileItem> sub_items = new ArrayList<CFileItem>();
    for(int i = 1; i < m_Items.size()-2; ++i){
      sub_items.add(m_Items.get(i));
    }
    Iterator<CFileItem> iter = sub_items.iterator();
    return new PutbackIterator<CFileItem>(iter);
  }
  
  public PutbackIterator<CFileItem> iterator(){
    Iterator<CFileItem> iter = m_Items.iterator();
    return new PutbackIterator<CFileItem>(iter);
  }
  
  public void subreplace(List<CFileItem> new_list) {
    //this handles the case where the SemiExpression is:
    //class Vertex ;
    if(new_list.isEmpty())
      return;
    
    List<CFileItem> ret = new ArrayList<CFileItem>();
    ret.add(m_Items.get(0));
    for(CFileItem item : new_list)
      ret.add(item);

    ret.add(m_Items.get(m_Items.size()-2));
    ret.add(m_Items.get(m_Items.size()-1));
    m_Items = ret;
  }
}
