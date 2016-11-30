/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.parser.preprocessor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IncludePath {

  private List<String> m_Folders;
  private String m_Filename;

  public IncludePath(){
    m_Folders = new ArrayList<String>();
  }

  public void addFolder(String folder){
    m_Folders.add(folder);
  }

  public void startFile(String file){
    m_Filename = file;
  }

  public String getAbsolutePath(String path, boolean library_file){
    String[] tokens = tokenizePath(path);

    if(library_file){
      for(String folder : m_Folders){
        File possible_match = matches(folder, tokens, 0);
        if(possible_match != null){
          return possible_match.getAbsolutePath();
        }
      }
    } else {
      File f = new File(m_Filename);
      String folder = f.getParent();
      File possible_match = matches(folder, tokens, 0);
      if(possible_match != null){
        return possible_match.getAbsolutePath();
      }
    }
    throw new RuntimeException("Cannot find path: "+path);
  }

  private String[] tokenizePath(String path){
    String s = File.separator;
    if(s.equals("\\"))
      s = "\\\\";
    String[] tokens = path.split(s);
    return tokens;
  }

  private File matches(String folder, String[] tokens, int index) {
    File f = new File(folder);
    File[] files = f.listFiles();
    for(File file : files){
      if(file.getName().equals(tokens[index]) == false)
        continue;
      //if at the file level
      if(index == tokens.length-1){
        if(file.isFile()) {
          return file;
        } else {
          return null;
        }
      } else {
        if(file.isDirectory()){
          return matches(file.getAbsolutePath(), tokens, index+1);
        } else {
          return null;
        }
      }
    }
    return null;
  }

  private String combine(String folder, String path) {
    String[] folder_tokens = tokenizePath(folder);
    String[] path_tokens = tokenizePath(path);
    List<String> all_tokens = new ArrayList<String>();
    for(String folder_token : folder_tokens){
      all_tokens.add(folder_token);
    }
    for(String path_token : path_tokens){
      all_tokens.add(path_token);
    }
    String ret = "";
    int index = 0;
    for(String token : all_tokens){
      if(token.equals(""))
        continue;
      ret += token;
      if(index < all_tokens.size()-1)
        ret += File.separator;
      ++index;
    }
    return ret;
  }
}
