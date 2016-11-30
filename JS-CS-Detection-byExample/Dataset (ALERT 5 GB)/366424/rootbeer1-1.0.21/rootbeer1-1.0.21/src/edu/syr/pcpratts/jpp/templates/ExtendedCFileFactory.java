/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.templates;

import edu.syr.pcpratts.jpp.cfile.CFile;
import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.CFileVisitor;
import edu.syr.pcpratts.jpp.cfile.Pass;
import edu.syr.pcpratts.jpp.cfile.internal.CFileFactory;
import edu.syr.pcpratts.jpp.parser.PutbackIterator;
import edu.syr.pcpratts.jpp.typetable.TypeTable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExtendedCFileFactory {

  private TypeTable m_TypeTable;
  
  private ExtendedCFileFactory(TypeTable type_table) {
    m_TypeTable = type_table;
  }

  public CFile create(CFile cfile){
     
    List<CFileVisitor> passes = new ArrayList<CFileVisitor>();
    passes.add(new TypeAnnotatorPass(m_TypeTable));
    passes.add(new MethodDeclFinderPass());
    
    for(CFileVisitor pass : passes){
      pass.visit(cfile);
    }

    return cfile;
  }
  
  public static void main(String[] args){
    try {
      CFileFactory factory1 = new CFileFactory();
      String filename = "testFiles"+File.separator+"test4.cpp";
      CFile cfile = factory1.create(filename);
      
      TypeTable type_table = new TypeTable(cfile);
      ExtendedCFileFactory factory2 = new ExtendedCFileFactory(type_table);
      cfile = factory2.create(cfile);
      
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
}
