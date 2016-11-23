/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.parser.test;

import edu.syr.pcpratts.jpp.cfile.CFile;
import edu.syr.pcpratts.jpp.cfile.internal.CFileFactory;
import edu.syr.pcpratts.jpp.parser.ParserFactory;
import java.util.ArrayList;
import java.util.List;

public class TestMain {

  public static void main(String[] args){

    TestFilesListFactory factory = new TestFilesListFactory();
    List<String> files = factory.create();

    for(String file : files){
      System.out.print("File: "+file+"...");
      boolean result;
      try {
        CFileFactory cfile_factory = new CFileFactory();
        CFile cfile = cfile_factory.create(file);
        System.out.println(cfile.toString());
        return;
      } catch(Exception ex){
        ex.printStackTrace();
      }
    }
  }

}
