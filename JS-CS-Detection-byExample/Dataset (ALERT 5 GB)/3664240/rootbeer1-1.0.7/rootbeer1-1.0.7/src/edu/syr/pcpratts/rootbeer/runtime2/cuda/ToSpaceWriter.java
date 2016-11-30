/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime2.cuda;

import edu.syr.pcpratts.rootbeer.runtime.Serializer;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.CompiledKernel;
import java.util.ArrayList;
import java.util.List;

public class ToSpaceWriter {
  
  private BlockingQueue<InputItem> m_InputQueue;
  private BlockingQueue<ToSpaceWriterResult> m_OutputQueue;
  private Thread m_Thread;
  
  public ToSpaceWriter(){
    m_InputQueue = new BlockingQueue<InputItem>();
    m_OutputQueue = new BlockingQueue<ToSpaceWriterResult>();
    
    WriteThreadProc proc = new WriteThreadProc(m_InputQueue, m_OutputQueue);
    m_Thread = new Thread(proc);
    m_Thread.setDaemon(true);
    m_Thread.start();
  }
  
  public void write(List<Kernel> items, Serializer visitor){
    InputItem item = new InputItem();
    item.m_Items = items;
    item.m_Visitor = visitor;
    m_InputQueue.put(item);
  }
  
  public ToSpaceWriterResult join(){
    return m_OutputQueue.take();  
  }
  
  private class InputItem {
    public List<Kernel> m_Items;
    public Serializer m_Visitor;
  }  
  
  private class WriteThreadProc implements Runnable {

    private BlockingQueue<InputItem> m_InputQueue;
    private BlockingQueue<ToSpaceWriterResult> m_OutputQueue;
  
    public WriteThreadProc(BlockingQueue<InputItem> input_queue,
      BlockingQueue<ToSpaceWriterResult> output_queue){
      
      m_InputQueue = input_queue;
      m_OutputQueue = output_queue;
    }
    
    public void run() {
      while(true){  
        List<Long> handles = new ArrayList<Long>();   
        List<Kernel> items = new ArrayList<Kernel>();
        List<Kernel> not_written = new ArrayList<Kernel>();
        try {
          InputItem input_item = m_InputQueue.take();
          not_written.addAll(input_item.m_Items);
          for(Kernel item : input_item.m_Items){
            long handle = input_item.m_Visitor.writeToHeap(item);
            handles.add(handle);
            items.add(item);
            not_written.remove(0);
          }     
        } catch(OutOfMemoryError ex){
          //
        } finally {
          m_OutputQueue.put(new ToSpaceWriterResult(handles, items, not_written));     
        }
      }
    }
    
  }

}
