/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.entry;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class JarClassLoader {

  private URLClassLoader m_loader;
  
  public JarClassLoader(String jar_name) throws MalformedURLException {
    URL[] urls = filenameToUrl(jar_name);
    m_loader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader().getParent());
  }
 
  private URL[] filenameToUrl(String filename) throws MalformedURLException {
    File f = new File(filename);
    String path = f.getAbsolutePath();
    URL url = new URL("file:///"+path);
    URL[] urls = new URL[1];
    urls[0] = url;
    return urls;
  }
  
  public ClassLoader getLoader(){
    return m_loader;
  }
}
