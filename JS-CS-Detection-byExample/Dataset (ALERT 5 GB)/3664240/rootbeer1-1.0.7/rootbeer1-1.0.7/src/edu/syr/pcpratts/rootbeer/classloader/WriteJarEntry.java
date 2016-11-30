/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.classloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.jar.JarEntry;

public class WriteJarEntry {

  public void write(JarEntry entry, InputStream is, String m_tempFolder) throws Exception {
    String name = entry.getName();
    String filename = m_tempFolder + File.separator + name;            
    if(entry.isDirectory()){
      File file = new File(filename);
      file.mkdirs();
      return;
    } else {
      File file = new File(filename);
      File parent = file.getParentFile();
      parent.mkdirs();
    }
    FileOutputStream fout = new FileOutputStream(filename);
    WriteStream writer = new WriteStream();
    writer.write(is, fout);
    fout.flush();
    fout.close();
  }
  
}
