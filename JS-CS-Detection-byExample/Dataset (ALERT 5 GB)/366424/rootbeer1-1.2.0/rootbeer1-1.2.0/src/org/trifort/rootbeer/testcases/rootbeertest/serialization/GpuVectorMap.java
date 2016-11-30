package org.trifort.rootbeer.testcases.rootbeertest.serialization;

public class GpuVectorMap {
  public static final int DEFAULT_CAPACITY = 16;
  private GpuMapKeyValuePair[] m_values = null;
  private boolean m_used = false;

  public GpuVectorMap() {
    this(DEFAULT_CAPACITY);
  }

  public GpuVectorMap(int size) {
    this.m_values = new GpuMapKeyValuePair[size];
  }

  public int size() {
    return m_values.length;
  }

  public void clear() {
    if (m_used) {
      for (int i = 0; i < m_values.length; i++) {
        m_values[i] = null;
      }
    }
  }

  private boolean equalsKey(GpuMapKeyValuePair entry, long otherKey) {
    if (entry != null) {
      long key = (Long) entry.getKey();
      return (key == otherKey);
    }
    return false;
  }

  public int indexForKey(long key) {
    return (int) (key % m_values.length);
  }

  public double[] get(long key) {
    GpuMapKeyValuePair entry = m_values[indexForKey(key)];
    while (entry != null && !equalsKey(entry, key)) {
      entry = entry.getNext();
    }
    return (entry != null) ? (double[]) entry.getValue() : null;
  }

  public void put(long key, double[] value) {
    m_used = true;
    int bucketIndex = indexForKey(key);
    GpuMapKeyValuePair entry = m_values[bucketIndex];
    if (entry != null) {
      System.out.println("put3");
      boolean done = false;
      while (!done) {
        if (equalsKey(entry, key)) {
          entry.setValue(value);
          done = true;
        } else if (entry.getNext() == null) {
          entry.setNext(new GpuMapKeyValuePair(key, value));
          done = true;
        }
        entry = entry.getNext();
      }
    } else {
      GpuMapKeyValuePair key_pair = new GpuMapKeyValuePair(key, value);
      m_values[bucketIndex] = key_pair;
    }
  }
}