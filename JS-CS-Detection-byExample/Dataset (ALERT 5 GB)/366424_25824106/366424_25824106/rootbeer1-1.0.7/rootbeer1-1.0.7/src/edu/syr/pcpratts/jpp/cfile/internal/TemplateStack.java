/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.cfile.internal;

import edu.syr.pcpratts.jpp.cfile.CTemplate;
import java.util.ArrayList;
import java.util.List;

public class TemplateStack {

  private Stack<String> m_Type;
  private Stack<CTemplate> m_Template;
  
  public TemplateStack(){
    m_Type = new Stack<String>();
    m_Template = new Stack<CTemplate>();
  }

  public boolean isEmpty(){
    return m_Type.isEmpty();
  }

  public void push(String scope, CTemplate template){
    m_Type.push(scope);
    m_Template.push(template);
  }

  public void pop(){
    m_Type.pop();
    m_Template.pop();
  }
  
  public List<CTemplate> getList(){
    List<CTemplate> ret = new ArrayList<CTemplate>();
    List<String> type = m_Type.getList();
    List<CTemplate> templates = m_Template.getList();
    for(int i = 0; i < type.size(); ++i){
      if(type.get(i).equals("template")){
        ret.add(templates.get(i));
      }
    }
    return ret;
  }
}
