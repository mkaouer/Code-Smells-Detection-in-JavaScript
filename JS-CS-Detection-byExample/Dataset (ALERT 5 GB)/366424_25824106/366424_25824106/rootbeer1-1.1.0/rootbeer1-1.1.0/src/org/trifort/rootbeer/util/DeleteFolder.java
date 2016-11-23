/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.util;

import java.io.File;

public class DeleteFolder {

  public void delete(String folder){
    File f = new File(folder);
    if(f.exists() == false)
      return;
    File[] files = f.listFiles();
    for(File file : files){
      if(file.isDirectory()){
        delete(file.getAbsolutePath());
        file.delete();
      } else if(file.isFile()){
        file.delete();
      }
    }
    f.delete();
  }
}
