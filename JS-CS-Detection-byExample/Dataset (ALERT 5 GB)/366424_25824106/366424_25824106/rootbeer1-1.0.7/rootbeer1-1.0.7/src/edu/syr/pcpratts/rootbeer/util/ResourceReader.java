/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.util;

import edu.syr.pcpratts.rootbeer.Constants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ResourceReader {

  public static String getResource(String path) throws IOException {
    InputStream is = ResourceReader.class.getResourceAsStream(path);
    StringBuilder ret = new StringBuilder();
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    while(true){
      String line = reader.readLine();
      if(line == null)
        break;
      ret.append(line + "\n");
    }
    is.close();
    return ret.toString();
  }
  
  public static List<byte[]> getResourceArray(String jar_path) throws IOException {
    InputStream is = ResourceReader.class.getResourceAsStream(jar_path);
    if(is == null){
      jar_path = Constants.OUTPUT_CLASS_FOLDER + File.separator + jar_path;
      is = new FileInputStream(jar_path);
    }
    List<byte[]> ret = new ArrayList<byte[]>();
    while(true){
      byte[] buffer = new byte[32*1024];
      int len = is.read(buffer);
      if(len == -1)
        break;
      byte[] small_buffer = new byte[len];
      for(int i = 0; i < len; ++i){
        small_buffer[i] = buffer[i];
      }
      ret.add(small_buffer);
    }
    is.close();
    return ret;
  }
}
