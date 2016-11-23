package org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate;

public final class GpuVectorMap2 {
  public static final int DEFAULT_CAPACITY = 16;
  private GpuLongVectorPair[] m_values = null;
  private boolean m_used = false;

  public GpuVectorMap2() {
    this(DEFAULT_CAPACITY);
  }

  public GpuVectorMap2(int size) {
    this.m_values = new GpuLongVectorPair[size];
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

  private boolean equalsKey(GpuLongVectorPair entry, long otherKey) {
    if (entry != null) {
      return (entry.getKey() == otherKey);
    }
    return false;
  }

  public int indexForKey(long key) {
    return (int) (key % m_values.length);
  }

  public double[] get(long key) {
    GpuLongVectorPair entry = m_values[indexForKey(key)];
    while (entry != null && !equalsKey(entry, key)) {
      entry = entry.getNext();
    }
    return (entry != null) ? entry.getValue() : null;
  }

  public synchronized void put(long key, double[] value) {
    m_used = true;
    int bucketIndex = indexForKey(key);
    GpuLongVectorPair entry = m_values[bucketIndex];
    if (entry != null) {
      boolean done = false;
      while (!done) {
        if (equalsKey(entry, key)) {
          entry.setValue(value);
          done = true;
        } else if (entry.getNext() == null) {
          entry.setNext(new GpuLongVectorPair(key, value));
          done = true;
        }
        entry = entry.getNext();
      }
    } else {
      m_values[bucketIndex] = new GpuLongVectorPair(key, value);
    }
  }

  public synchronized void add(long key, double[] value) {
    m_used = true;
    int bucketIndex = indexForKey(key);
    GpuLongVectorPair entry = m_values[bucketIndex];
    if (entry != null) {
      boolean done = false;
      while (!done) {
        if (equalsKey(entry, key)) {
          double[] vector = (double[]) entry.getValue();
          for (int i = 0; i < vector.length; i++) {
            vector[i] += value[i];
          }
          entry.setValue(vector);
          done = true;
        } else if (entry.getNext() == null) {
          entry.setNext(new GpuLongVectorPair(key, value));
          done = true;
        }
        entry = entry.getNext();
      }
    } else {
      m_values[bucketIndex] = new GpuLongVectorPair(key, value);
    }
  }

}
