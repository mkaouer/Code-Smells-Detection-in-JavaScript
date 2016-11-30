/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.util;

import java.io.BufferedReader;
import java.io.FileReader;

public class ReadFile {

  private String m_Filename;
  
  public ReadFile(String filename){
    m_Filename = filename;  
  }
  
  public String read() throws Exception {
    StringBuilder ret = new StringBuilder();
    BufferedReader reader = new BufferedReader(new FileReader(m_Filename));
    while(true){
      String line = reader.readLine();
      if(line == null)
        break;
      ret.append(line);
      ret.append("\n");
    }
    return ret.toString();    
  }
}
