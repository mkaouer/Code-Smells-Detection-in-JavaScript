/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarToFolder {

  private StringDelegate m_StringDelegate;
  
  public JarToFolder(){
    m_StringDelegate = null;
  }
  
  public JarToFolder(StringDelegate del){
    m_StringDelegate = del;
  }
  
  public void writeJar(String jar, String folder) throws Exception {
    folder = normalizeFolder(folder);
    ZipInputStream zin = new ZipInputStream(new FileInputStream(jar));
    while(true){
      ZipEntry entry = zin.getNextEntry();
      if(entry == null)
        break;
      if(shouldWrite(entry) == false)
        continue;
      String name = entry.getName();
      if(entry.isDirectory() == false && m_StringDelegate != null && name.endsWith(".class")){
        m_StringDelegate.call(name);
      }
      String s = File.separator;
      if(s.equals("\\")){
        name = name.replace("/", "\\");
      }
      String outname = folder+name;
      
      JarEntryHelp.mkdir(outname);

      if(entry.isDirectory() == false){
        OutputStream fout = new FileOutputStream(outname);
        write(zin, fout);
        fout.flush();
        fout.close();
      }
    }
    zin.close();
  }

  protected boolean shouldWrite(ZipEntry entry){
    return true;
  }

  private String normalizeFolder(String folder){
    String sep = File.separator;
    if(folder.endsWith(sep) == false)
      folder += sep;
    return folder;
  }

  private void write(ZipInputStream zin, OutputStream fout) throws Exception {
    while(true){
      byte[] buffer = new byte[4096];
      int len = zin.read(buffer);
      if(len == -1)
        break;
      fout.write(buffer, 0, len);
    }
  }
}
