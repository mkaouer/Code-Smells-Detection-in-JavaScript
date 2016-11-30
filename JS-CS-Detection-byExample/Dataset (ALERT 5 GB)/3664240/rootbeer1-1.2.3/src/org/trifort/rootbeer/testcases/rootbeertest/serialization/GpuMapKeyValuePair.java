package org.trifort.rootbeer.testcases.rootbeertest.serialization;

public class GpuMapKeyValuePair {

  private Object m_key;
  private Object m_value;
  private GpuMapKeyValuePair m_next;

  public GpuMapKeyValuePair(Object key, Object value) {
    this.m_key = key;
    this.m_value = value;
    this.m_next = null;
  }

  public void setKey(Object key) {
    this.m_key = key;
  }

  public Object getKey() {
    return m_key;
  }

  public void setValue(Object value) {
    this.m_value = value;
  }

  public Object getValue() {
    return m_value;
  }

  public void setNext(GpuMapKeyValuePair next) {
    this.m_next = next;
  }

  public GpuMapKeyValuePair getNext() {
    return m_next;
  }
}