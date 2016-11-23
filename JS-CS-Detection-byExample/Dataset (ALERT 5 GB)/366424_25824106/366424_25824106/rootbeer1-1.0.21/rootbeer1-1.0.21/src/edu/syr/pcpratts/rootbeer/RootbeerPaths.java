/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer;

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
    return folder+File.separator+"config";
  }
  
  public String getRootbeerHome(){
    String home = System.getProperty("user.home");
    File folder = new File(home+File.separator+".rootbeer"+File.separator);
    if(folder.exists() == false){
      folder.mkdirs(); 
    }
    return folder.getAbsolutePath();
  }
}
