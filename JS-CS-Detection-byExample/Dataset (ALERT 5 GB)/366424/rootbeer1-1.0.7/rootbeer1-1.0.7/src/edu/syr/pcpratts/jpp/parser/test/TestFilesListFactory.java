/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.parser.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TestFilesListFactory {

  private PrintWriter m_Writer;
  private Set<String> m_Passed;
  
  public TestFilesListFactory(){
    String passed_cache = "passed_cache.txt";
    loadPassed(passed_cache);
    try {
      m_Writer = new PrintWriter(passed_cache);
      writePassed();
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  public List<String> create(){
    List<String> ret = new ArrayList<String>();
    //ret.add("testFiles"+File.separator+"ScratchPad.cpp");
    //ret.add("testFiles"+File.separator+"graph.h");
    //ret.add("testFiles"+File.separator+"ActionsAndRules.h");
    //addDirectory(ret, "testFiles" + File.separator);
    addDirectory(ret, "jpp-testsuite" + File.separator);
    addDirectory(ret, "gcc-testsuite" + File.separator);
    return ret;
  }

  public void addDirectory(List<String> ret, String directory){
    File f = new File(directory);
    File[] files = f.listFiles();
    for(File file : files){
      if(file.isDirectory()){
        addDirectory(ret, file.getAbsolutePath());
      } else if(file.isFile()){
        if(file.getAbsolutePath().endsWith(".passed"))
          continue;
        if(file.getAbsolutePath().endsWith(".exp") || 
           file.getAbsolutePath().endsWith(".s")   ||
           file.getAbsolutePath().endsWith(".hs")  ||
           file.getAbsolutePath().endsWith(".x")){
          file.delete();
        } else if(m_Passed.contains(file.getAbsolutePath()) == false){
          ret.add(file.getAbsolutePath());
        }
      }
    }
  }

  void setPassed(String file) {
    m_Writer.println(file);
    m_Writer.flush();
  }

  private void loadPassed(String passed_cache) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(passed_cache));
      m_Passed = new HashSet<String>();
      while(true){
        String line = reader.readLine();
        if(line == null){
          reader.close();
          return;
        }
        m_Passed.add(line);
      }
    } catch(Exception ex){
      ex.printStackTrace();
    } 
  }

  private void writePassed() {
    Iterator<String> iter = m_Passed.iterator();
    while(iter.hasNext()){
      m_Writer.println(iter.next());
      m_Writer.flush();
    }
  }
}
