/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.generate.opencl;

import edu.syr.pcpratts.rootbeer.classloader.FastWholeProgram;
import edu.syr.pcpratts.rootbeer.compiler.RootbeerScene;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import soot.ArrayType;
import soot.Scene;
import soot.SootClass;

public class ClassHierarchy {

  private Map<String, List<SootClass>> mMap;
  
  private Map<String, Integer> mClassTypes;
  private Map<ArrayType, Integer> mArrayTypes;
  private int mMaxType;

  public ClassHierarchy(){
    mMap = new LinkedHashMap<String, List<SootClass>>();

    mClassTypes = new HashMap<String, Integer>();
    mArrayTypes = new HashMap<ArrayType, Integer>();
    mMaxType = 0;
    addBuiltIn("java.lang.String");
    addBuiltIn("java.lang.StringBuilder");   
    addBuiltIn("java.lang.AbstractStringBuilder");     
  }
  
  private void addBuiltIn(String cls){
    mMaxType++;
    List<SootClass> lst = new ArrayList<SootClass>();
    SootClass soot_class = Scene.v().getSootClass(cls);
    lst.add(soot_class);
    mMap.put(cls, lst);
  }
  
  public int getClassType(SootClass soot_class){
    if(mClassTypes.containsKey(soot_class.getName()) == false){
      getClassHierarchy(soot_class);
      int ret = mMaxType;
      mMaxType++;
      mClassTypes.put(soot_class.getName(), ret);
      return ret;
    } else {
      return mClassTypes.get(soot_class.getName());
    }
  }
  
  public int getMaxClassType(){
    return mMaxType + 1;
  }

  public List<SootClass> getClassHierarchy(SootClass soot_class){
    String class_name = soot_class.getName();
    if(mMap.containsKey(class_name))
      return mMap.get(class_name);

    Iterator<SootClass> iter = RootbeerScene.v().getAllClasses().iterator();
    List<SootClass> ret = new ArrayList<SootClass>();
    while(iter.hasNext()){
      SootClass next = iter.next();
      if(isInHierarchy(next, class_name)){
        SootClass curr = next;
        while(true){
          if(ret.contains(curr) == false){
            ret.add(curr);
          }
          if(curr.hasSuperclass()){
            curr = curr.getSuperclass();
            curr = Scene.v().getSootClass(curr.getName());
          } else {
            break;
          }
        }
      }
    }
    if(ret.isEmpty()){
      ret.add(soot_class);
    }
    //put the list in the map for each class so the order for each class is the same.
    for(SootClass c : ret){
      mMap.put(c.getName(), ret);
    }
    return ret;
  }

  private boolean isInHierarchy(SootClass next, String class_name) {
    if(class_name.equals("java.lang.Object"))
      return false;
    
    if(next.getName().equals(class_name))
      return true;

    SootClass curr = next;
    while(true){
      if(FastWholeProgram.v().isApplicationClass(curr) == false)
        return false;
      if(hasSuperClass(curr) == false)
        return false;
      SootClass superclass = curr.getSuperclass();
      if(superclass.getName().equals(class_name))
        return true;
      curr = superclass;
    }
  }

  private boolean hasSuperClass(SootClass curr){
    if(curr.hasSuperclass() == false)
      return false;
    //catch java.lang.Object and don't count it
    if(curr.getName().equals("java.lang.Object"))
      return false;
    return true;
  }

  public int getClassTypeArray(ArrayType array_type) {
    if(mArrayTypes.containsKey(array_type) == false){
      int ret = mMaxType;
      mMaxType++;
      mArrayTypes.put(array_type, ret);
      return ret;
    } else {
      return mArrayTypes.get(array_type);
    }
  }

}
