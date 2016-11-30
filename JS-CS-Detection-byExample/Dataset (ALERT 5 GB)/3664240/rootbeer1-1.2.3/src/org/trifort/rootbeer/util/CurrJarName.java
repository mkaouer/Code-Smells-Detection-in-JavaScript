/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.util;

import java.io.File;
import java.net.URLDecoder;

public class CurrJarName {

  public String get(){
    try {
      String path = CurrJarName.class.getProtectionDomain().getCodeSource().getLocation().getPath();
      String ret = URLDecoder.decode(path, "UTF-8");
      File file = new File(ret);
      if(file.getName().equals("ant.jar")){
        return "Rootbeer.jar";
      }
      if(file.getName().equals("classes")){
        return "Rootbeer.jar";
      }
      return ret;
    } catch(Exception ex){
      ex.printStackTrace();
      return "Rootbeer.jar";
    }
  }
}
