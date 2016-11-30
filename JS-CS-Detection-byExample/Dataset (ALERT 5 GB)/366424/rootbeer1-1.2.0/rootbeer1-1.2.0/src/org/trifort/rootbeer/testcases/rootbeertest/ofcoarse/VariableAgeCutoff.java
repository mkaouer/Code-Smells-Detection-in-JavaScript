/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.rootbeer.testcases.rootbeertest.ofcoarse;

import java.util.Random;

public class VariableAgeCutoff {

  private GpuList<VAgeStepItem> m_VAgeStepItems;
  private int m_MovieSize;
  
  public VariableAgeCutoff(int width, int height, int movie_size){
    m_VAgeStepItems = new GpuList<VAgeStepItem>();
    m_MovieSize = movie_size;
    
    int item_size = 150;
    for(int x = 0; x < width; x += item_size){
      for(int y = 0; y < height; y += item_size){
        m_VAgeStepItems.add(new VAgeStepItem(x, y, item_size));
      }
    }
  }
  
  public int getAgeStepItemsSize(){
    return m_VAgeStepItems.size();
  }

  public void set(String variable_list){
    String[] tokens = variable_list.split(",");
    for(int i = 0; i < m_VAgeStepItems.size(); ++i){
      VAgeStepItem item = m_VAgeStepItems.get(i);
      String token = tokens[i];
      int value = Integer.parseInt(token);
      item.m_AgeStep = value;
    }
  }

  private VariableAgeCutoff(){ 
    m_VAgeStepItems = new GpuList<VAgeStepItem>();
  }
  
  public boolean pastAgeStep(Point p, double time) {
    VAgeStepItem item = get(p);
    if(item == null)
      return false;
    return item.pastAgeStep((int) time);
  }
  
  @Override
  public VariableAgeCutoff clone(){
    VariableAgeCutoff ret = new VariableAgeCutoff();
    ret.m_MovieSize = m_MovieSize;
    for(int i = 0; i < m_VAgeStepItems.size(); ++i){
      VAgeStepItem item = m_VAgeStepItems.get(i);
      ret.m_VAgeStepItems.add(item.clone());
    }
    return ret;
  }

  private VAgeStepItem get(Point p) {
    for(int i = 0; i < m_VAgeStepItems.size(); ++i){
      VAgeStepItem item = m_VAgeStepItems.get(i);
      if(item.contains(p))
        return item;
    }
    return null;
  }

  public VariableAgeCutoff cloneRandom(Random rand) {
    VariableAgeCutoff ret = clone();
    for(int i = 0; i < m_VAgeStepItems.size(); ++i){
      VAgeStepItem item = m_VAgeStepItems.get(i);
      item.m_AgeStep += mod(rand);
    }
    return ret;
  }

  private double mod(Random rand) {
    double next = rand.nextDouble();
    next *= 6;
    next -= 3;

    if(next < 0)
      next = 0;
    if(next > m_MovieSize)
      next = m_MovieSize;
    return next;
  }

  public void random(Random rand) {
    for(int i = 0; i < m_VAgeStepItems.size(); ++i){
      VAgeStepItem item = m_VAgeStepItems.get(i);
      item.m_AgeStep = rand.nextInt(m_MovieSize);
    }
  }
  
  public String toCsvString(){
    StringBuilder ret = new StringBuilder();
    int count = 0;
    for(int i = 0; i < m_VAgeStepItems.size(); ++i){
      VAgeStepItem item = m_VAgeStepItems.get(i);
      ret.append(item.m_AgeStep);
      if(count < m_VAgeStepItems.size() - 1)
        ret.append(",");
      count++;
    }
    return ret.toString();
  }

  public void ageReplicate(int age_step) {
    GpuList<Integer> to_replicate = createListOfRandomIntegers(m_VAgeStepItems.size());
    for(int i = 0; i < to_replicate.size(); ++i){
      Integer index = to_replicate.get(i);
      VAgeStepItem item = m_VAgeStepItems.get(index);
      item.m_AgeStep = age_step;
    }
  }

  private GpuList<Integer> createListOfRandomIntegers(int size) {
    GpuList<Integer> all = createListOfSize(size);
    GpuList<Integer> ret = new GpuList<Integer>();
    Random rand = new Random();
    int ret_size = rand.nextInt(size);
    for(int i = 0; i < ret_size; ++i){
      int index = rand.nextInt(all.size()-1);
      int curr = all.get(index);
      all.remove(index);
      ret.add(curr);
    }
    return ret;
  }

  private GpuList<Integer> createListOfSize(int size){
    GpuList<Integer> ret = new GpuList<Integer>();
    for(int i = 0; i < size; ++i){
      ret.add(i);
    }
    return ret;
  }

  void setAgeStep(int index, int age_step) {
    m_VAgeStepItems.get(index).m_AgeStep = age_step;
  }
  
  public class VAgeStepItem {

    private BoundingBox m_Box;
    public int m_AgeStep;
    
    public VAgeStepItem(int x, int y, int item_size) {
      GpuList<Point> points = new GpuList<Point>();
      points.add(new Point(x+item_size, y+item_size));
      points.add(new Point(x+item_size, y-item_size));
      points.add(new Point(x-item_size, y+item_size));
      points.add(new Point(x-item_size, y-item_size));
      m_Box = new BoundingBox(points, 1);
    }
    
    private VAgeStepItem(){
    }

    private boolean contains(Point p1) {
      Point p2 = new Point(p1.X, p1.Y);
      return m_Box.contains(p2);
    }

    private boolean pastAgeStep(int time) {
      if(time > m_AgeStep)
        return true;
      return false;
    }
    
    @Override
    public VAgeStepItem clone(){
      VAgeStepItem ret = new VAgeStepItem();
      ret.m_AgeStep = m_AgeStep;
      ret.m_Box = new BoundingBox(m_Box);
      return ret;
    }
  }
}
