/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CompilerRunner {

  public List<String> run(String command) {
    try {
      List<String> ret = new ArrayList<String>();
      Process p = Runtime.getRuntime().exec(command);
      StreamReader reader1 = new StreamReader(p.getInputStream());
      StreamReader reader2 = new StreamReader(p.getErrorStream());
      reader1.join();
      reader2.join();
      int error_code = p.waitFor();
      if(error_code != 0){
        reader1.print();
        reader2.print();
        ret.addAll(reader1.m_Lines);
        ret.addAll(reader2.m_Lines);
      }
      p.destroy();
      return ret;
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new RuntimeException(ex);
    }
  }  
  
  private class StreamReader implements Runnable {

    private InputStream m_InputStream;
    private Thread m_Thread;
    private List<String> m_Lines;
    
    public StreamReader(InputStream is){
      m_InputStream = is;
      m_Lines = new ArrayList<String>();
      m_Thread = new Thread(this);
      m_Thread.setDaemon(true);
      m_Thread.start();
    }   
    
    public void join(){
      try {
        m_Thread.join();
      } catch(Exception ex){
        ex.printStackTrace();
      }
    }
    
    public void run() {
      BufferedReader reader = new BufferedReader(new InputStreamReader(m_InputStream));
      while(true){
        try {
          String line = reader.readLine();
          if(line == null)
            break;
          m_Lines.add(line);
        } catch(Exception ex){
          break; 
        }        
      }
    }

    private void print() {
      for(String line : m_Lines){
        System.out.println(line);
      }
    }
    
  }
}
