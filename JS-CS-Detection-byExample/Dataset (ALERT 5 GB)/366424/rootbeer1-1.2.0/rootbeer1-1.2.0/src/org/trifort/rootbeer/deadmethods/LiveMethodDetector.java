/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.deadmethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LiveMethodDetector {
  
  public Set<String> parse(List<Block> blocks){
    Map<String, Method> method_map = new HashMap<String, Method>();
    for(Block block : blocks){
      if(block.isMethod() == false){
        continue;
      }
      Method method = block.getMethod();
      method_map.put(method.getName(), method);
    }
    
    LinkedList<String> queue = new LinkedList<String>();
    Set<String> visited = new HashSet<String>();
    queue.add("entry");
    queue.add("run");
    while(queue.isEmpty() == false){
      String name = queue.removeFirst();
      if(visited.contains(name)){
        continue;
      }
      visited.add(name);
      Method method = method_map.get(name);
      if(method == null){
        continue;
      }
      queue.addAll(method.getInvoked());
    }
    return visited;
  }
}
