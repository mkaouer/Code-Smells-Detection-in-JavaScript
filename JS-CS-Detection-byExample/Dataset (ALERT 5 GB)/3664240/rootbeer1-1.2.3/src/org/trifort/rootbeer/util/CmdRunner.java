/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CmdRunner {

  private List<String> m_outputLines;
  private List<String> m_errorLines;
  private Process m_process;
  
  public int run(String cmd, File dir){
    try {
      m_process = Runtime.getRuntime().exec(cmd, new String[0], dir);
      return processExec();
    } catch(Exception ex){
      ex.printStackTrace();
      throw new RuntimeException(ex);
    }
  }
  
  public int run(String cmd[], File dir){
    try {
      m_process = Runtime.getRuntime().exec(cmd, new String[0], dir);
      return processExec();
    } catch(Exception ex){
      ex.printStackTrace();
      throw new RuntimeException(ex);
    }
  }
  
  private int processExec() throws InterruptedException{
    StreamEater out_eater = new StreamEater(m_process.getInputStream());
    StreamEater err_eater = new StreamEater(m_process.getErrorStream());
    m_outputLines = out_eater.get();
    m_errorLines = err_eater.get();
    int ret = m_process.waitFor();
    m_process.destroy();
    return ret;
  }

  public List<String> getOutput(){
    return m_outputLines;
  }

  public List<String> getError(){
    return m_errorLines;
  }

  private class StreamEater implements Runnable {

    private List<String> m_stream;
    private InputStream m_inputStream;
    private BufferedReader m_reader;
    private volatile boolean m_done;
   
    public StreamEater(InputStream input_stream){
      m_inputStream = input_stream;
      m_reader = new BufferedReader(new InputStreamReader(m_inputStream));
      m_stream = new LinkedList<String>();
      m_done = false;
      new Thread(this).start();
    }

    public void run() {
      try {
        while(true){
          String line = m_reader.readLine();
          if(line == null)
            break;
          m_stream.add(line);
        }
      } catch(Exception ex){
      } finally {
        m_done = true;
      }
    }
    
    public List<String> get(){
      while(!m_done){
        try {
          Thread.sleep(10);
        } catch(Exception ex){
          ex.printStackTrace();
        }
      }
      return m_stream;
    }
  }
}
