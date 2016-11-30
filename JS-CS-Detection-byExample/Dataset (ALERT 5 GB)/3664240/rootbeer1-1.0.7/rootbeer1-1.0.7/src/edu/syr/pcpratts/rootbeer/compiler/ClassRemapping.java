/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.compiler;

import edu.syr.pcpratts.rootbeer.util.ResourceReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.*;
import soot.Scene;
import soot.SootClass;

public class ClassRemapping {

  private Map<String, String> m_Map;
  private Map<String, String> m_ClonedMap;
  private List<String> m_RuntimeClasses;
  private List<String> m_RuntimeClassesJar;
  
  public ClassRemapping(){
    m_Map = new HashMap<String, String>();
    m_ClonedMap = new HashMap<String, String>();
    m_RuntimeClasses = new ArrayList<String>();
    m_RuntimeClassesJar = new ArrayList<String>();
    put("java.util.concurrent.atomic.AtomicLong", "edu.syr.pcpratts.rootbeer.runtime.remap.GpuAtomicLong");
    put("java.util.Random", "edu.syr.pcpratts.rootbeer.runtime.remap.Random");
    put("edu.syr.pcpratts.rootbeer.testcases.rootbeertest.remaptest.CallsPrivateMethod", "edu.syr.pcpratts.rootbeer.runtime.remap.DoesntCallPrivateMethod");
    put("java.lang.Math", "edu.syr.pcpratts.rootbeer.runtime.remap.java.lang.Math");
  }

  private void put(String key, String value) {
    m_Map.put(key, value);
    m_RuntimeClasses.add(value);
    value = "/" + value.replace(".", "/") + ".class";
    m_RuntimeClassesJar.add(value);
  }
  
  public void cloneClass(String class_name){
    if(m_Map.containsKey(class_name)){
      return;
    }
    String new_name = "edu.syr.pcpratts.rootbeer.runtime.remap."+class_name;
    put(class_name, new_name);
    m_ClonedMap.put(class_name, new_name);
    
    SootClass soot_class = Scene.v().getSootClass(class_name);
    CloneClass cloner = new CloneClass();
    SootClass new_class = cloner.execute(soot_class, new_name);
    new_class.setApplicationClass();
    RootbeerScene.v().addClass(new_class);
  }
  
  
  public List<String> getRuntimeClasses(){
    return m_RuntimeClasses;
  }
  
  public List<String> getRuntimeClassesJar(){
    return m_RuntimeClassesJar;
  }

  public boolean containsKey(String cls_name) {
    return m_Map.containsKey(cls_name);
  }

  public String get(String cls_name) {
    return m_Map.get(cls_name);
  }

  public boolean cloned(String cls) {
    return m_ClonedMap.containsKey(cls);
  }

  public List<String> getCloned() {
    List<String> ret = new ArrayList<String>();
    ret.addAll(m_ClonedMap.values());
    return ret;
  }

  private String mapFilename(){
    return "/edu/syr/pcpratts/rootbeer/runtime/remap/remap.txt";
  }
  
  public void loadMap() {
    String contents = "";
    String filename = mapFilename();
    try {
      contents = ResourceReader.getResource(filename);
    } catch(Exception ex){
      contents = readFile("src"+filename);
    }
    doLoadMap(contents);
  }
  
  public void saveMap(){    
    String filename = "src"+mapFilename();
    try {
      PrintWriter writer = new PrintWriter(filename);
      Iterator<String> iter = m_ClonedMap.keySet().iterator();
      while(iter.hasNext()){
        String key = iter.next();
        String value = m_ClonedMap.get(key);
        writer.println(key+" => "+value);
      }
      writer.flush();
      writer.close();
    } catch(Exception ex){
      ex.printStackTrace();
    }
    
  }

  private String readFile(String filename) {
    String ret = "";
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      while(true){
        String line = reader.readLine();
        if(line == null){
          break;
        }
        ret += line+"\n";
      }
      return ret;
    } catch(Exception ex){
      ex.printStackTrace();
      return ret;
    }    
  }

  private void doLoadMap(String contents) {
    try {
      BufferedReader reader = new BufferedReader(new StringReader(contents));
      while(true){
        String line = reader.readLine();
        if(line == null){
          break;
        }
        String[] tokens = line.split("=>");
        if(tokens.length != 2){
          continue;
        }
        m_ClonedMap.put(tokens[0].trim(), tokens[1].trim()); 
        put(tokens[0].trim(), tokens[1].trim()); 
      }      
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }

  public List<String> getUsed() {
    loadMap();
    List<String> ret = new ArrayList<String>();
    ret.addAll(m_RuntimeClasses);
    ret.addAll(m_ClonedMap.values());
    return ret;
  }

  public Collection<String> getValues() {
    return m_Map.values();
  }
}
