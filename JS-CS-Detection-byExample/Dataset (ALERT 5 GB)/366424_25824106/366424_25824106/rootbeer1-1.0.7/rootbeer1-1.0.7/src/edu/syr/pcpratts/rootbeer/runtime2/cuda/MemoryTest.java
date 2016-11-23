/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime2.cuda;

import edu.syr.pcpratts.rootbeer.runtime.memory.BufferPrinter;
import edu.syr.pcpratts.rootbeer.runtime.memory.Memory;

public class MemoryTest {

  public void run(Memory mem) {
    validate(testBytes(mem));
    System.out.println("TEST PASSED");
  }

  private boolean testBytes(Memory mem) {
    for(int i = 0; i < 500; ++i){
      mem.writeByte((byte) i);
    }
    BufferPrinter printer = new BufferPrinter();
    printer.print(mem, 0, 500);
    mem.setAddress(0);
    for(int i = 0; i < 500; ++i){
      byte b = mem.readByte();
      if(b != (byte) i){
        System.out.println("Byte fail at: "+i);
        return false;
      }
    }
    return true;
  }

  private void validate(boolean value) {
    if(value == false){
      System.out.println("TEST FAILED");
      System.exit(0);
    }
  }
  
  public static void main(String[] args){
    CudaRuntime2.v().memoryTest();
  }
}
