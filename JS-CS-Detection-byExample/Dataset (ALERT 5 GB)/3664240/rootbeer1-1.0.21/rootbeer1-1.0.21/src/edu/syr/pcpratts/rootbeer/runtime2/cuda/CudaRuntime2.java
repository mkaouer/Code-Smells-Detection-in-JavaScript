/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://chirrup.org/
 * 
 * See the file LICENSE for copying permission.
 */

package edu.syr.pcpratts.rootbeer.runtime2.cuda;

import edu.syr.pcpratts.rootbeer.Configuration;
import edu.syr.pcpratts.rootbeer.Constants;
import edu.syr.pcpratts.rootbeer.RootbeerPaths;
import edu.syr.pcpratts.rootbeer.runtime.Serializer;
import edu.syr.pcpratts.rootbeer.runtime.ParallelRuntime;
import edu.syr.pcpratts.rootbeer.runtime.PartiallyCompletedParallelJob;
import edu.syr.pcpratts.rootbeer.runtime.ReadOnlyAnalyzer;
import edu.syr.pcpratts.rootbeer.runtime.RootbeerGpu;
import edu.syr.pcpratts.rootbeer.runtime.Kernel;
import edu.syr.pcpratts.rootbeer.runtime.CompiledKernel;
import edu.syr.pcpratts.rootbeer.runtime.memory.BufferPrinter;
import edu.syr.pcpratts.rootbeer.runtime.memory.Memory;
import edu.syr.pcpratts.rootbeer.runtime.util.Stopwatch;
import edu.syr.pcpratts.rootbeer.util.ResourceReader;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import org.xml.sax.InputSource;

public class CudaRuntime2 implements ParallelRuntime {

  private static CudaRuntime2 m_Instance;
  
  public static CudaRuntime2 v(){
    if(m_Instance == null){
      m_Instance = new CudaRuntime2();
    }
    return m_Instance;
  }
  
  private int m_NumBlocks;
  
  private List<Memory> m_ToSpace;
  private List<Memory> m_Texture;
  private Handles m_Handles;
  private Handles m_ExceptionHandles;
  private long m_ToSpaceAddr;
  private long m_GpuToSpaceAddr;
  private long m_TextureAddr;
  private long m_GpuTextureAddr;
  private long m_HandlesAddr;
  private long m_GpuHandlesAddr;
  private long m_ExceptionsHandlesAddr;
  private long m_GpuExceptionsHandlesAddr;
  private long m_ToSpaceSize;
  private int m_NumCores;
  private int m_NumBlocksRun;
  private int m_BlockShape;
  private int m_GridShape;
  private long m_MaxGridDim;
  private long m_NumMultiProcessors;
  private long m_serializationTime;
  private long m_executionTime;
  private long m_deserializationTime;
  
  private List<Kernel> m_JobsToWrite;
  private List<Kernel> m_JobsWritten;
  private List<Kernel> m_NotWritten;
  private List<Long> m_HandlesCache;
  private CompiledKernel m_FirstJob;
  private PartiallyCompletedParallelJob m_Partial;
  
  private List<ToSpaceReader> m_Readers;
  private List<ToSpaceWriter> m_Writers;
  
  private List<Serializer> m_serializers;
  
  private CpuRunner m_CpuRunner;
  private BlockShaper m_BlockShaper;
  
  private CudaRuntime2(){
    Stopwatch watch = new Stopwatch();
    watch.start();    
    CudaLoader loader = new CudaLoader();
    loader.load();
        
    m_BlockShaper = new BlockShaper();
    // the 200*1024*1024 factor here is the amount of free memory that should NOT
    // be allocated for transfering data to and from the gpu
    setup(m_BlockShaper.getMaxBlocksPerProc(), m_BlockShaper.getMaxThreadsPerBlock(), getReserveMem());
    m_JobsToWrite = new ArrayList<Kernel>();
    m_JobsWritten = new ArrayList<Kernel>();  
    m_NotWritten = new ArrayList<Kernel>();
    m_HandlesCache = new ArrayList<Long>();
    m_ToSpace = new ArrayList<Memory>();
    m_Texture = new ArrayList<Memory>();
    m_Readers = new ArrayList<ToSpaceReader>();
    m_Writers = new ArrayList<ToSpaceWriter>();    
   
    //there is a bug in the concurrent serializer. setting num_cores to 1 right now.
    //next version of rootbeer should have a faster concurrent serializer anyway
    m_NumCores = 1;
    //m_NumCores = Runtime.getRuntime().availableProcessors();
    
    m_serializers = new ArrayList<Serializer>();
    AtomicLong to_space_inst_ptr = new AtomicLong(0);
    AtomicLong to_space_static_ptr = new AtomicLong(0);
    AtomicLong texture_inst_ptr = new AtomicLong(0);
    AtomicLong texture_static_ptr = new AtomicLong(0);
    for(int i = 0; i < m_NumCores; ++i){
      m_ToSpace.add(new FastMemory(m_ToSpaceAddr, to_space_inst_ptr, to_space_static_ptr, m_ToSpaceSize));
      m_Texture.add(new FastMemory(m_TextureAddr, texture_inst_ptr, texture_static_ptr, m_ToSpaceSize));
      m_Readers.add(new ToSpaceReader());
      m_Writers.add(new ToSpaceWriter());
    }
    m_Handles = new Handles(m_HandlesAddr, m_GpuHandlesAddr);
    m_ExceptionHandles = new Handles(m_ExceptionsHandlesAddr, m_GpuExceptionsHandlesAddr);
    m_CpuRunner = new CpuRunner();
    watch.stopAndPrint("CudaRuntime2 ctor: ");
  }
  
  private long getReserveMem(){
    File file = new File(RootbeerPaths.v().getConfigFile());
    if(file.exists() == false){
      long reserve_mem = findReserveMem(m_BlockShaper.getMaxBlocksPerProc(), m_BlockShaper.getMaxThreadsPerBlock());
      Properties props = new Properties();
      props.setProperty("reserve_mem", Long.toString(reserve_mem));
      try {
        OutputStream fout = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(fout);
        props.store(writer, "");
        writer.flush();
        fout.flush();
        writer.close();
        fout.close();
        return reserve_mem;
      } catch(Exception ex){
        ex.printStackTrace();
        return reserve_mem;
      }
    } else {
      try {
        InputStream fin = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
        Properties props = new Properties();
        props.load(reader);
        return Integer.parseInt(props.getProperty("reserve_mem"));
      } catch(Exception ex){
        ex.printStackTrace();
        return findReserveMem(m_BlockShaper.getMaxBlocksPerProc(), m_BlockShaper.getMaxThreadsPerBlock());
      }
    }
  }
  
  public void memoryTest(){
    MemoryTest test = new MemoryTest();
    test.run(m_ToSpace.get(0));
  }
  
  public PartiallyCompletedParallelJob run(Iterator<Kernel> jobs){
    
    Stopwatch watch2 = new Stopwatch();
    watch2.start();
    RootbeerGpu.setIsOnGpu(true);
    m_Partial = new PartiallyCompletedParallelJob(jobs);
    
    boolean any_jobs = writeBlocks(jobs);
    if(any_jobs == false){
      return m_Partial;
    }
    String filename = m_FirstJob.getCubin();
    if(filename.endsWith(".error")){
      return m_Partial;
    }
    calculateShape();
    compileCode();
    
    Stopwatch watch = new Stopwatch();
    watch.start();
    runOnGpu();
    watch.stop();
    m_executionTime = watch.elapsedTimeMillis();
    
    readBlocks();
    unload();
        
    runExtraBlocks();
    
    RootbeerGpu.setIsOnGpu(false);
    watch2.stopAndPrint("CudaRuntime2.run: ");
    return m_Partial;
  }

  public boolean writeBlocks(Iterator<Kernel> iter) {
        
    Stopwatch watch = new Stopwatch();
    watch.start();
    for(Memory mem : m_ToSpace){
      mem.setAddress(0);
    }
    m_Handles.activate();
    m_Handles.resetPointer();
    m_JobsToWrite.clear();
    m_JobsWritten.clear();
    m_HandlesCache.clear();
    m_NotWritten.clear();
    m_serializers.clear();
    
    ReadOnlyAnalyzer analyzer = null;
    
    boolean first_block = true;    
    int count = 0;
    while(iter.hasNext()){
      Kernel job = iter.next();      
      if(first_block){
        m_FirstJob = (CompiledKernel) job;
        first_block = false;    
      }  
      
      m_JobsToWrite.add(job);
      if(count + 1 == m_BlockShaper.getMaxThreads(m_NumMultiProcessors))
        break;
      count++;
    }
    if(count == 0){
      return false;
    }
    
    for(int i = 0; i < m_NumCores; ++i){
      Memory mem = m_ToSpace.get(i);
      Memory texture_mem = m_Texture.get(i);
      mem.clearHeapEndPtr();
      texture_mem.clearHeapEndPtr();
      Serializer visitor = m_FirstJob.getSerializer(mem, texture_mem);
      visitor.setAnalyzer(analyzer);
      m_serializers.add(visitor);
    }
    
    //write the statics to the heap
    m_serializers.get(0).writeStaticsToHeap();
    
    int items_per = m_JobsToWrite.size() / m_NumCores;
    for(int i = 0; i < m_NumCores; ++i){
      Serializer visitor = m_serializers.get(i);
      int end_index;
      if(i == m_NumCores - 1){
        end_index = m_JobsToWrite.size();
      } else {
        end_index = (i+1)*items_per;
      }
      List<Kernel> items = m_JobsToWrite.subList(i*items_per, end_index);
      m_Writers.get(i).write(items, visitor);
    }
    
    for(int i = 0; i < m_NumCores; ++i){
      ToSpaceWriterResult result = m_Writers.get(i).join(); 
      List<Long> handles = result.getHandles();
      List<Kernel> items = result.getItems();      
      m_JobsWritten.addAll(items);
      m_Partial.enqueueJobs(items);
      m_HandlesCache.addAll(handles);
      m_NotWritten.addAll(result.getNotWrittenItems());
      for(Long handle : handles){
        m_Handles.writeLong(handle);
      }
    }
    
    m_Partial.addNotWritten(m_NotWritten);

    writeClassTypeRef(m_serializers.get(0).getClassRefArray());
    
    watch.stop();
    m_serializationTime = watch.elapsedTimeMillis();
    
    if(Configuration.getRunAllTests() == false){
      BufferPrinter printer = new BufferPrinter();
      printer.print(m_ToSpace.get(0), 0, 896);
    }

    return true;
  }

  private void compileCode() {
    String filename = m_FirstJob.getCubin();
    File file = new File(filename);
    String dest_filename = RootbeerPaths.v().getRootbeerHome()+File.separator+file.getName();
    try {
      ResourceReader.writeToFile(filename, dest_filename);
    } catch(Exception ex){
      ex.printStackTrace(); 
    }
    loadFunction(getHeapEndPtr(), dest_filename, m_NumBlocksRun);
  }
  
  private void runOnGpu(){
    System.out.println("Running "+m_NumBlocksRun+" blocks.");
    System.out.println("BlockShape: "+m_BlockShape+" GridShape: "+m_GridShape);    
    
    try {
      runBlocks(m_NumBlocksRun, m_BlockShape, m_GridShape);
    } catch(RuntimeException ex){
      reinit(m_BlockShaper.getMaxBlocksPerProc(), m_BlockShaper.getMaxThreadsPerBlock(), getReserveMem()); 
      throw ex;
    }
  }  
    
  private void calculateShape() {    
    m_BlockShaper.run(m_JobsWritten.size(), m_NumMultiProcessors);
    m_GridShape = m_BlockShaper.gridShape();
    m_BlockShape = m_BlockShaper.blockShape();
    m_NumBlocksRun = m_GridShape * m_BlockShape;    
    if(m_NumBlocksRun > m_JobsWritten.size()){
      m_NumBlocksRun = m_JobsWritten.size(); 
    }
  }
  
  public void readBlocks() {    
    Stopwatch watch = new Stopwatch();
    watch.start();
    for(int i = 0; i < m_NumCores; ++i)
      m_ToSpace.get(i).setAddress(0);    
    
    m_ExceptionHandles.activate();
    
    if(Configuration.getRunAllTests() == false){
      BufferPrinter printer = new BufferPrinter();
      printer.print(m_ToSpace.get(0), 0, 2048);
    }
    
    for(int i = 0; i < m_NumBlocksRun; ++i){
      long ref = m_ExceptionHandles.readLong();
      if(ref != 0){
        long ref_num = ref >> 4;
        if(ref_num == Constants.NullPointerNumber){
          throw new NullPointerException(); 
        } else if(ref_num == Constants.OutOfMemoryNumber){
          throw new OutOfMemoryError();
        }
        Memory mem = m_ToSpace.get(0);
        Memory texture_mem = m_Texture.get(0);
        Serializer visitor = m_serializers.get(0);
        mem.setAddress(ref);           
        Object except = visitor.readFromHeap(null, true, ref);
        if(except instanceof Error){
          Error except_th = (Error) except;
          throw except_th;
        } else {
          throw new RuntimeException((Throwable) except);
        }
      }
    }    
    
    //read the statics from the heap
    m_serializers.get(0).readStaticsFromHeap();
    
    int items_per = m_NumBlocksRun / m_NumCores;
    for(int i = 0; i < m_NumCores; ++i){
      Serializer visitor = m_serializers.get(i);
      int end_index;
      if(i == m_NumCores - 1){
        end_index = m_NumBlocksRun;
      } else {
        end_index = (i+1)*items_per;
      }
      List<Long> handles = m_HandlesCache.subList(i*items_per, end_index);
      List<Kernel> jobs = m_JobsWritten.subList(i*items_per, end_index);
      m_Readers.get(i).read(jobs, handles, visitor);
    }
    
    for(int i = 0; i < m_NumCores; ++i){
      m_Readers.get(i).join();  
    }
    
    watch.stop();
    m_deserializationTime = watch.elapsedTimeMillis();
  }
  
  private void runExtraBlocks(){
    if(m_NumBlocksRun == m_JobsWritten.size())
      return;
    
    List<Kernel> cpu_jobs = m_JobsWritten.subList(m_NumBlocksRun, m_JobsWritten.size());
    m_CpuRunner.run(cpu_jobs);   
    m_CpuRunner.join();
    
    for(Kernel job : cpu_jobs){
      m_Partial.enqueueJob(job);
    }      
  }
  
  private long getHeapEndPtr() {
    long max = Long.MIN_VALUE;
    for(Memory mem : m_ToSpace){
      if(mem.getHeapEndPtr() > max)
        max = mem.getHeapEndPtr();
    }
    System.out.println("heap end ptr: "+max);
    return max;
  }

  public boolean isGpuPresent() {
    return true;
  }

  public long getExecutionTime() {
    return m_executionTime;
  }

  public long getSerializationTime() {
    return m_serializationTime;
  }
  
  public long getDeserializationTime() {
    return m_deserializationTime;
  }
  
  private native long findReserveMem(int max_blocks, int max_threads);
  private native void setup(int max_blocks_per_proc, int max_threads_per_block, long free_memory);
  public static native void printDeviceInfo();
  private native void loadFunction(long heap_end_ptr, String filename, int num_blocks);
  private native void writeClassTypeRef(int[] refs);
  private native int runBlocks(int size, int block_shape, int grid_shape);
  private native void unload();
  private native void reinit(int max_blocks_per_proc, int max_threads_per_block, long free_memory);
  
}
