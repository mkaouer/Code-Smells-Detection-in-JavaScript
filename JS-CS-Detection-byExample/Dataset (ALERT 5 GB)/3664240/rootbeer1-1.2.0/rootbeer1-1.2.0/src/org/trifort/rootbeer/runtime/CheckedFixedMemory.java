package org.trifort.rootbeer.runtime;

public class CheckedFixedMemory extends FixedMemory {

  public CheckedFixedMemory(long size) {
    super(size);
  }

  @Override
  public void incrementAddress(int offset){
    super.incrementAddress(offset);
    if(currPointer() > m_size){
      throw new RuntimeException("address out of range: "+currPointer());
    }
  }

  @Override
  public void setAddress(long address){
    if(address > m_size){
      throw new RuntimeException("address out of range: "+address);
    }
    super.setAddress(address);
  }
  
  @Override
  protected long currPointer(){
    long ret = super.currPointer();
    if(ret > m_size || ret < 0){
      throw new RuntimeException("address out of range: "+ret);
    }
    return ret;
  }
}
