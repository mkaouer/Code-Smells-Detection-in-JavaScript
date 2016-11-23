/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp;

import java.io.File;

public class Main {

  public static void main(String[] args) {
    Jpp jpp = new Jpp(true);
    try {
      String filename = "testFiles"+File.separator+"test3.cpp";
      jpp.translateFile(filename);
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
}
