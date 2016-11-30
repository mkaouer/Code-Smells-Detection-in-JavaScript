/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.jpp.typetable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Combinatorics {
    
  public Combinatorics(){
  }
  
  /**
   * @param size the number of digits in the result
   * @return 
   */
  public List<List<Integer>> generate(int size){
    List<List<Integer>> ret = new ArrayList<List<Integer>>();
    
    if(size == 1){
      List<Integer> zero = new ArrayList<Integer>();
      zero.add(0);
      ret.add(zero);
      List<Integer> one = new ArrayList<Integer>();
      one.add(1);
      ret.add(one);
      return ret;
    }
    
    for(int i = 0; i < Math.pow(size, size); ++i){
      List<Integer> curr = baseN(i, size);
      addZeros(curr, size);  
      ret.add(curr);
    }
    return ret;
  }
  
  public List<List<Integer>> generateNoRepeat(int size){
    List<List<Integer>> repeating = generate(size);
    List<List<Integer>> ret = new ArrayList<List<Integer>>();
    for(List<Integer> curr : repeating){
      Set<Integer> visited = new TreeSet<Integer>();
      boolean should_add = true;
      for(Integer i : curr){
        if(visited.contains(i)){
          should_add = false;
          break;
        }
        visited.add(i);
      }
      if(should_add)
        ret.add(curr);
    }
    return ret;
  }
  
  private void display(List<List<Integer>> list){
    for(List<Integer> sublist : list){
      for(int i = 0; i < sublist.size(); ++i){
        System.out.print(sublist.get(i)+" ");
        if(i == sublist.size()-1)
          System.out.println();
      }
    }
  }
  
  public static void main(String[] args){
    Combinatorics brute = new Combinatorics();
    //brute.display(brute.generate(0));
    //brute.display(brute.generate(1));
    //brute.display(brute.generate(2));
    brute.display(brute.generateNoRepeat(3));
    //brute.display(brute.generate(4));
  }

  private List<Integer> baseN(int number, int n) {
    List<Integer> ret = new ArrayList<Integer>();
    if(number == 0){
      ret.add(0);
      return ret;
    }
    while(number > 0){
      int mod = number % n;
      ret.add(mod);
      number /= n;
    }
    
    return ret;
  }

  private void addZeros(List<Integer> curr, int size) {
    List<Integer> ret = new ArrayList<Integer>();
    for(Integer num : curr)
      ret.add(num);
    for(int i = curr.size(); i < size; ++i){
      ret.add(0);
    }
    curr.clear();
    curr.addAll(ret);
  }
}
