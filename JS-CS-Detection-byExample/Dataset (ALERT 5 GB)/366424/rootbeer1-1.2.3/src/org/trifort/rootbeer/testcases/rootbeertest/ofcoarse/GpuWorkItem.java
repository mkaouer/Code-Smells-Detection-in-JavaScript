/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.ofcoarse;

import org.trifort.rootbeer.runtime.Kernel;

public class GpuWorkItem implements Kernel {

  private int i;
  private CoarseningPhysics physics;
  private GpuList<Droplet> prev;
  private GpuList<Droplet> next;

  public GpuWorkItem(int i, CoarseningPhysics physics, GpuList<Droplet> prev){
    this.i = i;
    this.physics = physics;
    this.prev = prev;
  }

  @Override
  public void gpuMethod() {
    physics.setCurrAge(i);
    next = physics.makePrediction(prev);
  }

  public GpuList<Droplet> getNext(){
    return next;
  }

  boolean compare(GpuWorkItem grhs) {
    if(grhs == null){
      System.out.println("grhs == null");
      return false;
    }
    if(grhs.next == null){
      System.out.println("grhs.next == null");
      return false;
    }
    if(next.size() != grhs.next.size()){
      System.out.println("next.size(): "+next.size());
      System.out.println("grhs.next.size(): "+grhs.next.size());
      return false;
    }
    GpuList<GpuNumber> l_lst = physics.getDebug();
    GpuList<GpuNumber> r_lst = grhs.physics.getDebug();
    if(l_lst.size() != r_lst.size()){
      System.out.println("list size not match");
      System.out.println("l_lst.size(): "+l_lst.size());
      System.out.println("r_lst.size(): "+r_lst.size());
      return false;
    }
    for(int index = 0; index < l_lst.size(); ++index){
      System.out.println("debug: "+l_lst.get(index).getDouble()+" "+r_lst.get(index).getDouble());
    }    
    for(int index = 0; index < next.size(); ++index){
      Droplet lhs = next.get(index);
      Droplet rhs = grhs.next.get(index);
      if(lhs == null && rhs != null){
        System.out.println("lhs is null");
        return false;
      }
      if(rhs == null && lhs != null){
        System.out.println("rhs is null at: "+index);
        return false;
      }
      if(lhs.getCenter() == null){
        System.out.println("lhs.getCenter() is null");
        return false;
      }
      if(rhs.getCenter() == null){
        System.out.println("rhs.getCenter() is null");
        return false;
      }
      System.out.println("4: "+index);
      if(lhs.compareSums(rhs) == false)
        return false;
      System.out.println("5: "+index);
      double dist1 = PointDistance.distance(lhs.getCenter(), rhs.getCenter());
      if(dist1 > 0.1){
        System.out.println("dist1: "+dist1);
        System.out.println("lhs.X: "+lhs.getCenter().X);
        System.out.println("lhs.Y: "+lhs.getCenter().Y);
        System.out.println("rhs.X: "+rhs.getCenter().X);
        System.out.println("rhs.Y: "+rhs.getCenter().Y);        
        return false;
      }
      System.out.println("6");
      if(StrictMath.abs(lhs.getVolume() - rhs.getVolume()) > 0.1)
        return false;
    }
    return true;
  }
}