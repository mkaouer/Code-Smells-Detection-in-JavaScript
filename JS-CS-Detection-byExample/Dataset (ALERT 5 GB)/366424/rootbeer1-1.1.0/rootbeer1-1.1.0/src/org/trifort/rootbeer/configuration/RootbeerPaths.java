/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.configuration;

import java.io.File;

/**
 *
 * @author pcpratts
 */
public class RootbeerPaths {

  private static RootbeerPaths m_instance;
  
  public static RootbeerPaths v(){
    if(m_instance == null){  
      m_instance = new RootbeerPaths();
    }
    return m_instance;
  }
  
  public String getConfigFile(){
    String folder = getRootbeerHome();
    return folder+"config";
  }
  
  public String getJarContentsFolder(){
    String folder = getRootbeerHome();
    return folder+"jar-contents";
  }
  
  public String getOutputJarFolder(){
    String folder = getRootbeerHome();
    return folder+"output-jar";
  }
  
  public String getOutputClassFolder(){
    String folder = getRootbeerHome();
    return folder+"output-class";
  }
  
  public String getOutputShimpleFolder(){
    String folder = getRootbeerHome();
    return folder+"output-shimple";
  }

  public String getOutputJimpleFolder() {
    String folder = getRootbeerHome();
    return folder+"output-jimple";
  }
  
  public String getTypeFile(){
    String folder = getRootbeerHome();
    return folder+"types";
  }
  
  public String getRootbeerHome(){
    String home = System.getProperty("user.home");
    File folder = new File(home+File.separator+".rootbeer"+File.separator);
    if(folder.exists() == false){
      folder.mkdirs(); 
    }
    return folder.getAbsolutePath()+File.separator;
  }
}
