package org.trifort.rootbeer.testcases.rootbeertest.kerneltemplate;

public final class GpuLongVectorPair {

  private long m_key;
  private double[] m_value;
  private GpuLongVectorPair m_next;

  public GpuLongVectorPair(long key, double[] value) {
    this.m_key = key;
    this.m_value = value;
    this.m_next = null;
  }

  public void setKey(long key) {
    this.m_key = key;
  }

  public long getKey() {
    return m_key;
  }

  public void setValue(double[] value) {
    this.m_value = value;
  }

  public double[] getValue() {
    return m_value;
  }

  public void setNext(GpuLongVectorPair next) {
    this.m_next = next;
  }

  public GpuLongVectorPair getNext() {
    return m_next;
  }
}
