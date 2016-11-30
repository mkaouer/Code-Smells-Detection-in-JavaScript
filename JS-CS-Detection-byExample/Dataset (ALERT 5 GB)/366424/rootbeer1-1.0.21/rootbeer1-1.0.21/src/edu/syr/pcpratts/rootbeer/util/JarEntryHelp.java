/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.util;

import java.io.File;

public class JarEntryHelp {

  public static void mkdir(String name) {
    String dir_name = "";
    if(name.endsWith(File.separator)){
      dir_name = name;
    } else {
      String s = File.separator;
      if(s.equals("\\")){
        s = "\\\\";
        name = name.replace("/", "\\");
      }
      String[] tokens = name.split(s);
      if(name.startsWith(File.separator))
        dir_name += File.separator;
      for(int i = 0; i < tokens.length - 1; ++i){
        dir_name += tokens[i] + File.separator;
      }
    }
    File f = new File(dir_name);
    f.mkdirs();
  }
}
