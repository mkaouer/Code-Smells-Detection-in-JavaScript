/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.test.serialization;

import edu.syr.pcpratts.rootbeer.runtime.Serializer;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.CompiledKernel;
import edu.syr.pcpratts.rootbeer.runtime.memory.BufferPrinter;
import edu.syr.pcpratts.rootbeer.runtime.memory.BasicSwappedMemory;
import edu.syr.pcpratts.rootbeer.test.AlignmentCheckedMemory;
import edu.syr.pcpratts.rootbeer.test.TestSerialization;
import java.util.List;

public class SerializationTester {

  private BasicSwappedMemory m_ToSpace;
  private BasicSwappedMemory m_TextureMem;
  private BasicSwappedMemory m_HandlesMap;
  private TestSerialization m_Tester;

  public SerializationTester(TestSerialization tester){
    int to_space_size = 1024*1024*102;  //100 meg
    m_ToSpace = new AlignmentCheckedMemory(to_space_size);
    m_TextureMem = new AlignmentCheckedMemory(to_space_size);
    m_HandlesMap = new AlignmentCheckedMemory(to_space_size*8);
    m_Tester = tester;
  }

  public boolean test() {
    try {
      List<Kernel> objects = m_Tester.create();
      write(objects);
    
      if(readAndTest(objects) == false)
        return false;
      return true;
    } catch(Exception ex){
      fail();
      ex.printStackTrace();
      return false;
    }
  }

  private void fail(){
    BufferPrinter printer = new BufferPrinter();
    printer.print(m_ToSpace, 0, 256+64);
  }

  private void write(List<Kernel> objects) {
    CompiledKernel first = (CompiledKernel) objects.get(0);
    Serializer visitor = first.getSerializer(m_ToSpace, m_TextureMem);

    for(Kernel object : objects){
      object.gpuMethod();
      CompiledKernel job = (CompiledKernel) object;
      long handle = visitor.writeToHeap(job);
      m_HandlesMap.writeLong(handle);
    }
  }

  private boolean readAndTest(List<Kernel> objects){
    CompiledKernel first = (CompiledKernel) objects.get(0);
    Serializer visitor = first.getSerializer(m_ToSpace, null);

    List<Kernel> new_objects = m_Tester.create();

    int i = 0;
    m_ToSpace.setAddress(0);
    m_HandlesMap.setAddress(0);
    for(Kernel object : new_objects){
      int handle = m_HandlesMap.readInt();
      m_ToSpace.setAddress(handle);

      CompiledKernel job = (CompiledKernel) object;
      Kernel curr = (Kernel) visitor.readFromHeap(job, true, handle);
      if(m_Tester.compare(objects.get(i), curr) == false){
        System.out.println("Comparison failed at: "+i);
        fail();
        return false;
      }
      ++i;
    }
    return true;
  }
}
