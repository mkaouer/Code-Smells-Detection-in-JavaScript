/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.generate.opencl.fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.trifort.rootbeer.generate.opencl.OpenCLScene;
import org.trifort.rootbeer.generate.opencl.tweaks.Tweaks;

import soot.SootClass;

public class FieldTypeSwitch {

  private Map<String, String> m_BodyToNameMap;
  private int m_CurrFunctionNum;
  
  public FieldTypeSwitch(){
    m_BodyToNameMap = new HashMap<String, String>();
    m_CurrFunctionNum = 0;
  }
  
  public String getFunctions() {
    StringBuilder ret = new StringBuilder();
    Iterator<String> iter = m_BodyToNameMap.keySet().iterator();
    while(iter.hasNext()){
      String body = iter.next();
      String function_name = m_BodyToNameMap.get(body);
      String qual = Tweaks.v().getDeviceFunctionQualifier();
      ret.append(qual+" int "+function_name+"(int type){\n");
      ret.append(body);
      ret.append("}\n");
    }
    return ret.toString();
  }

  String typeSwitchName(Map<Integer, List<SootClass>> offsets) {
    String body = produceBody(offsets);
    if(m_BodyToNameMap.containsKey(body)){
      return m_BodyToNameMap.get(body);
    } else {
      String base_name = "org_trifort_type_switch";
      base_name += m_CurrFunctionNum;
      m_CurrFunctionNum++;
      m_BodyToNameMap.put(body, base_name);
      return base_name;
    }
  }

  private String produceBody(Map<Integer, List<SootClass>> offsets) {
    int[] sorted_keys = sortKeys(offsets);
    StringBuilder ret = new StringBuilder();
    ret.append("int offset;\n");
    ret.append("switch(type){\n");
    for(int key : sorted_keys){
      List<SootClass> classes = offsets.get(key);
      Collections.sort(classes, new NumberedTypeSortComparator(true));
      for(SootClass sclass : classes){
        ret.append(" case "+OpenCLScene.v().getClassType(sclass)+":\n");
      }
      ret.append("  offset = "+key+";\n");
      ret.append("  break;\n");
    }
    ret.append("default:\n");
    ret.append("  offset = -1;\n");
    ret.append("  break;\n");
    ret.append("}\n");
    ret.append("return offset;\n");
    return ret.toString();
  }

  private int[] sortKeys(Map<Integer, List<SootClass>> offsets) {
    int[] array = new int[offsets.size()];
    int index = 0;
    Iterator<Integer> iter = offsets.keySet().iterator();
    while(iter.hasNext()){
      int key = iter.next();
      array[index] = key;
      index++;
    }
    Arrays.sort(array);
    return array;
  }
}
