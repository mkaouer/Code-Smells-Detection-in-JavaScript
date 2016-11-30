/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.classloader;

import java.io.InputStream;
import java.io.OutputStream;

public class WriteStream {

  public void write(InputStream fin, OutputStream fout) throws Exception {
    while(true){
      int read_len = 4096;
      byte[] buffer = new byte[read_len];
      int curr_read = fin.read(buffer);
      if(curr_read == -1){
        break;
      }
      byte[] to_write = new byte[curr_read];
      for(int i = 0; i < curr_read; ++i){
        to_write[i] = buffer[i];
      }
      fout.write(to_write);
    }
  }
}
