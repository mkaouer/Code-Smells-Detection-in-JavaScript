/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp;

import edu.syr.pcpratts.jpp.cfile.CFile;
import edu.syr.pcpratts.jpp.cfile.CFileItem;
import edu.syr.pcpratts.jpp.cfile.internal.CFileFactory;
import edu.syr.pcpratts.jpp.parser.ParserFactory;
import edu.syr.pcpratts.jpp.parser.PutbackIterator;
import edu.syr.pcpratts.jpp.parser.preprocessor.IncludePath;
import edu.syr.pcpratts.jpp.parser.preprocessor.Preprocessor;
import edu.syr.pcpratts.jpp.parser.test.IteratorTester;
import edu.syr.pcpratts.jpp.typetable.TypeTable;
import java.io.FileNotFoundException;
import java.io.Reader;

public class Jpp {

  private IncludePath m_IncludePath;

  public Jpp(boolean is_cpp) {
    m_IncludePath = new IncludePath();
    JppSettings.v().setCpp(is_cpp);
  }

  public void addIncludeFolder(String path){
    m_IncludePath.addFolder(path);
  }

  public void translateFile(String filename) throws FileNotFoundException {

    //Preprocessor preproc = new Preprocessor(m_IncludePath);
    //Reader reader = preproc.process(filename);

    ParserFactory pfactory = new ParserFactory();
    PutbackIterator<CFileItem> iter = pfactory.createCppFromFilename(filename);

    CFileFactory cfactory = new CFileFactory();
    CFile cfile = cfactory.create(iter);
    
    TypeTable type_table = new TypeTable(cfile);

    System.out.println(cfile.toString());
  }
}
