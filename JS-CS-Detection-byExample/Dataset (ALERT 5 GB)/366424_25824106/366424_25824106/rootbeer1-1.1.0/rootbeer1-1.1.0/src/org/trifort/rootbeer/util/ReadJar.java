/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.trifort.rootbeer.configuration.RootbeerPaths;
import org.trifort.rootbeer.entry.RootbeerCompiler;
import org.trifort.rootbeer.util.JarEntryHelp;

public class ReadJar {

  private boolean m_UseDirectories;

  public void writeRuntimeClass(String cls) {
    m_UseDirectories = true;
    URL url = RootbeerCompiler.class.getResource(cls);
    String scheme = url.getProtocol();
    if(scheme == null)
      throw new RuntimeException("Cannot read class "+cls+" from Rootbeer jar");
    if(scheme.equals("jar"))
      writeClassFromJar(url, cls);
    else if(scheme.equals("file"))
      writeClassFromFile(url, cls);
    else
      throw new RuntimeException("Cannot read class "+cls+" from Rootbeer jar");
  }

  public void writeFileWithoutDirectories(String path) {
    m_UseDirectories = false;
    URL url = RootbeerCompiler.class.getResource(path);
    String scheme = url.getProtocol();
    if(scheme == null)
      throw new RuntimeException("Cannot read class "+path+" from Rootbeer jar");
    if(scheme.equals("jar"))
      writeClassFromJar(url, path);
    else if(scheme.equals("file"))
      writeClassFromFile(url, path);
    else
      throw new RuntimeException("Cannot read class "+path+" from Rootbeer jar");
  }


  private void writeClassFromJar(URL url, String cls) {
    try {
      JarURLConnection con = (JarURLConnection) url.openConnection();
      JarFile archive = con.getJarFile();
      /* Search for the entries you care about. */
      Enumeration<JarEntry> entries = archive.entries();
      while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();
        if(jarEntryEqual(entry.getName(), cls) == false)
          continue;
        String entry_name = entry.getName();
        String filename = RootbeerPaths.v().getJarContentsFolder() + File.separator + entry_name;

        if(m_UseDirectories)
          JarEntryHelp.mkdir(filename);

        if(entry.isDirectory()){
          return;
        }

        InputStream is = archive.getInputStream(entry);
        writeToFile(is, filename);
      }
    } catch(IOException ex){
      throw new RuntimeException(ex);
    }
  }

  private void writeClassFromFile(URL url, String cls) {
    try {
      InputStream fin = url.openStream();
      String outfilename = RootbeerPaths.v().getJarContentsFolder() + cls;

      if(m_UseDirectories)
        JarEntryHelp.mkdir(outfilename);
      
      writeToFile(fin, outfilename);
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }

  private void writeToFile(InputStream is, String filename) throws IOException {
    if(m_UseDirectories == false){
      File f = new File(filename);
      filename = f.getName();
    }

    FileOutputStream fout = new FileOutputStream(filename);
    byte[] buffer = new byte[1024];
    try {
      while(true){
        int count = is.read(buffer);
        if(count == -1)
          break;
        fout.write(buffer, 0, count);
      }
    } finally {
      fout.flush();
      fout.close();
      is.close();
    }
  }

  private boolean jarEntryEqual(String name, String cls) {
    name = "/" + name;
    if(name.equals(cls))
      return true;
    return false;
  }

}
