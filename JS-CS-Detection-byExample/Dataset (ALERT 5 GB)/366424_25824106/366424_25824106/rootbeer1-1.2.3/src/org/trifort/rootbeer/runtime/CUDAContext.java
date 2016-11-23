package org.trifort.rootbeer.runtime;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.trifort.rootbeer.configuration.Configuration;
import org.trifort.rootbeer.runtime.util.Stopwatch;
import org.trifort.rootbeer.runtimegpu.GpuException;
import org.trifort.rootbeer.util.ResourceReader;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

public class CUDAContext implements Context {

  final private GpuDevice gpuDevice;
  final private boolean is32bit;

  private long nativeContext;
  private long memorySize;
  private byte[] cubinFile;
  private Memory objectMemory;
  private Memory handlesMemory;
  private Memory textureMemory;
  private Memory exceptionsMemory;
  private Memory classMemory;
  private boolean usingUncheckedMemory;
  private long requiredMemorySize;
  private CacheConfig cacheConfig;
  private ThreadConfig threadConfig;
  private Kernel kernelTemplate;
  private CompiledKernel compiledKernel;
  private boolean usingHandles;
  
  final private StatsRow stats;
  final private Stopwatch writeBlocksStopwatch;
  final private Stopwatch runStopwatch;
  final private Stopwatch runOnGpuStopwatch;
  final private Stopwatch readBlocksStopwatch;
  
  final private ExecutorService exec;
  final private Disruptor<GpuEvent> disruptor;
  final private EventHandler<GpuEvent> handler;
  final private RingBuffer<GpuEvent> ringBuffer;
  
  static {
    initializeDriver();
  }
  
  public CUDAContext(GpuDevice device){
    exec = Executors.newCachedThreadPool(new ThreadFactory() {
      public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
      }
    });
    disruptor = new Disruptor<GpuEvent>(GpuEvent.EVENT_FACTORY, 64, exec);
    handler = new GpuEventHandler();
    disruptor.handleEventsWith(handler);
    ringBuffer = disruptor.start();
    gpuDevice = device;
    memorySize = -1;
    
    String arch = System.getProperty("os.arch");
    is32bit = arch.equals("x86") || arch.equals("i386");
    
    usingUncheckedMemory = true;
    usingHandles = false;
    nativeContext = allocateNativeContext();
    cacheConfig = CacheConfig.PREFER_NONE;
    
    stats = new StatsRow();
    writeBlocksStopwatch = new Stopwatch();
    runStopwatch = new Stopwatch();
    runOnGpuStopwatch = new Stopwatch();
    readBlocksStopwatch = new Stopwatch();
  }
  
  @Override
  public GpuDevice getDevice() {
    return gpuDevice;
  }

  @Override
  public void close() {
    disruptor.shutdown();
    exec.shutdown();
    freeNativeContext(nativeContext);
    
    if(objectMemory != null){
      objectMemory.close();
    }
    if(handlesMemory != null){
      handlesMemory.close();
    }
    if(exceptionsMemory != null){
      exceptionsMemory.close();
    }
    if(classMemory != null){
      classMemory.close();
    }
    if(textureMemory != null){
      textureMemory.close();
    }
  }

  @Override
  public void setMemorySize(long memorySize) {
    this.memorySize = memorySize;
  }
  
  @Override
  public void setKernel(Kernel kernelTemplate) {
    this.kernelTemplate = kernelTemplate;
    this.compiledKernel = (CompiledKernel) kernelTemplate;
  }

  @Override
  public void setCacheConfig(CacheConfig cacheConfig) {
    this.cacheConfig = cacheConfig;
  }
  
  @Override
  public void setUsingHandles(boolean value){
    usingHandles = value;
  }
  
  @Override
  public void useCheckedMemory(){
    this.usingUncheckedMemory = false;
  }

  @Override
  public void setThreadConfig(ThreadConfig threadConfig) {
    this.threadConfig = threadConfig;
  }
  
  @Override
  public void setThreadConfig(int threadCountX, int blockCountX,
      int numThreads) {
    setThreadConfig(threadCountX, 1, 1, blockCountX, 1, numThreads);
  }
  
  @Override
  public void setThreadConfig(int threadCountX, int threadCountY,
      int blockCountX, int blockCountY, int numThreads) {
    setThreadConfig(threadCountX, threadCountY, 1, blockCountX, blockCountY, numThreads);
  }

  @Override
  public void setThreadConfig(int threadCountX, int threadCountY,
      int threadCountZ, int blockCountX, int blockCountY,
      int numThreads) {
    this.threadConfig = new ThreadConfig(threadCountX, threadCountY, threadCountZ,
        blockCountX, blockCountY, numThreads);
  }
  
  @Override
  public void buildState(){
    String filename;
    int size = 0;
    boolean error = false;
    
    if(is32bit){
      filename = compiledKernel.getCubin32();
      size = compiledKernel.getCubin32Size();
      error = compiledKernel.getCubin32Error();
    } else {
      filename = compiledKernel.getCubin64();
      size = compiledKernel.getCubin64Size();
      error = compiledKernel.getCubin64Error();
    }

    if(error){
      throw new RuntimeException("CUDA code compiled with error");
    }
    
    cubinFile = readCubinFile(filename, size);
    
    if(usingUncheckedMemory){
      classMemory = new FixedMemory(1024);
      exceptionsMemory = new FixedMemory(getExceptionsMemSize(threadConfig));
      textureMemory = new FixedMemory(8);
      if(usingHandles){
        handlesMemory = new FixedMemory(4*threadConfig.getNumThreads());
      } else {
        handlesMemory = new FixedMemory(4);
      }
    } else {
      exceptionsMemory = new CheckedFixedMemory(getExceptionsMemSize(threadConfig));
      classMemory = new CheckedFixedMemory(1024);
      textureMemory = new CheckedFixedMemory(8);
      if(usingHandles){
        handlesMemory = new CheckedFixedMemory(4*threadConfig.getNumThreads());
      } else {
        handlesMemory = new CheckedFixedMemory(4);
      }
    }
    if(memorySize == -1){
      findMemorySize(cubinFile.length);
    }
    if(usingUncheckedMemory){
      objectMemory = new FixedMemory(memorySize);
    } else {
      objectMemory = new CheckedFixedMemory(memorySize);
    }
    
    long seq = ringBuffer.next();
    GpuEvent gpuEvent = ringBuffer.get(seq);
    gpuEvent.setValue(GpuEventCommand.NATIVE_BUILD_STATE);
    gpuEvent.getFuture().reset();
    ringBuffer.publish(seq);
    gpuEvent.getFuture().take();
  }
  
  private long getExceptionsMemSize(ThreadConfig thread_config) {
    if(Configuration.runtimeInstance().getExceptions()){
      return 4L*thread_config.getNumThreads();
    } else {
      return 4;
    }
  }
  
  private byte[] readCubinFile(String filename, int length) {
    try {
      byte[] buffer = ResourceReader.getResourceArray(filename, length);
      return buffer;
    } catch(Exception ex){
	  ex.printStackTrace();
      throw new RuntimeException(ex);
    }
  }
  
  private void findMemorySize(int cubinFileLength){
    long freeMemSizeGPU = gpuDevice.getFreeGlobalMemoryBytes();
    long freeMemSizeCPU = Runtime.getRuntime().freeMemory();
    long freeMemSize = Math.min(freeMemSizeGPU, freeMemSizeCPU);
    
    freeMemSize -= cubinFileLength;
    freeMemSize -= exceptionsMemory.getSize();
    freeMemSize -= classMemory.getSize();
    freeMemSize -= 2048;
    
    if(freeMemSize <= 0){
      StringBuilder error = new StringBuilder();
      error.append("OutOfMemory while allocating Java CPU and GPU memory.\n");
      error.append("  Try increasing the max Java Heap Size using -Xmx and the initial Java Heap Size using -Xms.\n");
      error.append("  Try reducing the number of threads you are using.\n");
      error.append("  Try using kernel templates.\n");
      error.append("  Debugging Output:\n");
      error.append("    GPU_SIZE: "+freeMemSizeGPU+"\n");
      error.append("    CPU_SIZE: "+freeMemSizeCPU+"\n");
      error.append("    EXCEPTIONS_SIZE: "+exceptionsMemory.getSize()+"\n");
      error.append("    CLASS_MEMORY_SIZE: "+classMemory.getSize());
      throw new RuntimeException(error.toString());
    }
    memorySize = freeMemSize;
  }

  @Override
  public long getRequiredMemory() {
    return requiredMemorySize;
  }
  
  @Override
  public void run(){
    GpuFuture future = runAsync();
    future.take();
  }
  
  @Override
  public GpuFuture runAsync() {
    long seq = ringBuffer.next();
    GpuEvent gpuEvent = ringBuffer.get(seq);
    gpuEvent.setValue(GpuEventCommand.NATIVE_RUN);
    gpuEvent.getFuture().reset();
    ringBuffer.publish(seq);
    return gpuEvent.getFuture();
  }
  
  @Override
  public void run(List<Kernel> work) {
    GpuFuture future = runAsync(work);
    future.take();
  }
  
  @Override
  public GpuFuture runAsync(List<Kernel> work) {
    long seq = ringBuffer.next();
    GpuEvent gpuEvent = ringBuffer.get(seq);
    gpuEvent.setKernelList(work);
    gpuEvent.setValue(GpuEventCommand.NATIVE_RUN_LIST);
    gpuEvent.getFuture().reset();
    ringBuffer.publish(seq);
    return gpuEvent.getFuture();
  }


  @Override
  public StatsRow getStats() {
    return stats;
  }
  
  private class GpuEventHandler implements EventHandler<GpuEvent>{
    @Override
    public void onEvent(final GpuEvent gpuEvent, final long sequence, final boolean endOfBatch){
      try {
        switch(gpuEvent.getValue()){
        case NATIVE_BUILD_STATE:
          boolean usingExceptions = Configuration.runtimeInstance().getExceptions();
          nativeBuildState(nativeContext, gpuDevice.getDeviceId(), cubinFile, 
              cubinFile.length, threadConfig.getThreadCountX(), threadConfig.getThreadCountY(),
              threadConfig.getThreadCountZ(), threadConfig.getBlockCountX(),
              threadConfig.getBlockCountY(), threadConfig.getNumThreads(), 
              objectMemory, handlesMemory, exceptionsMemory, classMemory, 
              b2i(usingExceptions), cacheConfig.ordinal());
          gpuEvent.getFuture().signal();
          break;
        case NATIVE_RUN:
          writeBlocksTemplate();
          runGpu();
          readBlocksTemplate();
          gpuEvent.getFuture().signal();
          break;
        case NATIVE_RUN_LIST:
          writeBlocksList(gpuEvent.getKernelList());
          runGpu();
          readBlocksList(gpuEvent.getKernelList());
          gpuEvent.getFuture().signal();
          break;
        }
      } catch(Exception ex){
        gpuEvent.getFuture().setException(ex);
        gpuEvent.getFuture().signal();
      }
    }
  }
  
  private void writeBlocksTemplate(){
    writeBlocksStopwatch.start();
    objectMemory.clearHeapEndPtr();
    handlesMemory.setAddress(0);
    
    Serializer serializer = compiledKernel.getSerializer(objectMemory, textureMemory);
    serializer.writeStaticsToHeap();
    
    long handle = serializer.writeToHeap(compiledKernel);
    handlesMemory.writeRef(handle);
    objectMemory.align16();
   
    if(Configuration.getPrintMem()){
      BufferPrinter printer = new BufferPrinter();
      printer.print(objectMemory, 0, 256);
    }
    
    writeBlocksStopwatch.stop();
    stats.setSerializationTime(writeBlocksStopwatch.elapsedTimeMillis());
  }

  private void writeBlocksList(List<Kernel> work){
    writeBlocksStopwatch.start();
    objectMemory.clearHeapEndPtr();
    
    Serializer serializer = compiledKernel.getSerializer(objectMemory, textureMemory);
    serializer.writeStaticsToHeap();
    
    for(Kernel kernel : work){
      long handle = serializer.writeToHeap(kernel);
      handlesMemory.writeRef(handle);
    }
    objectMemory.align16();
   
    if(Configuration.getPrintMem()){
      BufferPrinter printer = new BufferPrinter();
      printer.print(objectMemory, 0, 256);
    }
    
    writeBlocksStopwatch.stop();
    stats.setSerializationTime(writeBlocksStopwatch.elapsedTimeMillis());
  }
  
  private void runGpu(){
    runOnGpuStopwatch.start();
    cudaRun(nativeContext, objectMemory, b2i(!usingHandles), stats);
    runOnGpuStopwatch.stop();
    requiredMemorySize = objectMemory.getHeapEndPtr();
    stats.setExecutionTime(runOnGpuStopwatch.elapsedTimeMillis());
  }
  
  private void readBlocksSetup(Serializer serializer){
    readBlocksStopwatch.start();
    objectMemory.setAddress(0);
    exceptionsMemory.setAddress(0);
    
    if(Configuration.runtimeInstance().getExceptions()){
      for(long i = 0; i < threadConfig.getNumThreads(); ++i){
        long ref = exceptionsMemory.readRef();
        if(ref != 0){
          long ref_num = ref >> 4;
          if(ref_num == compiledKernel.getNullPointerNumber()){
            throw new NullPointerException("NPE while running on GPU"); 
          } else if(ref_num == compiledKernel.getOutOfMemoryNumber()){
            throw new OutOfMemoryError("OOM error while running on GPU");
          }
          
          objectMemory.setAddress(ref);           
          Object except = serializer.readFromHeap(null, true, ref);
          if(except instanceof Error){
            Error except_th = (Error) except;
            throw except_th;
          } else if(except instanceof GpuException){
            GpuException gpu_except = (GpuException) except;
            throw new ArrayIndexOutOfBoundsException("array_index: "+gpu_except.m_arrayIndex+
                " array_length: "+gpu_except.m_arrayLength+" array: "+gpu_except.m_array);
          } else {
            throw new RuntimeException((Throwable) except);
          }
        }
      }    
    }
    
    serializer.readStaticsFromHeap();
  }
  
  private void readBlocksTemplate(){
    Serializer serializer = compiledKernel.getSerializer(objectMemory, textureMemory);
    readBlocksSetup(serializer);
    handlesMemory.setAddress(0);
    
    long handle = handlesMemory.readRef();
    serializer.readFromHeap(compiledKernel, true, handle);
    
    if(Configuration.getPrintMem()){
      BufferPrinter printer = new BufferPrinter();
      printer.print(objectMemory, 0, 256);
    }
    readBlocksStopwatch.stop();
    stats.setDeserializationTime(readBlocksStopwatch.elapsedTimeMillis());
  }

  public void readBlocksList(List<Kernel> kernelList) {
    Serializer serializer = compiledKernel.getSerializer(objectMemory, textureMemory);
    readBlocksSetup(serializer);
    
    handlesMemory.setAddress(0);
    for(Kernel kernel : kernelList){
      long ref = handlesMemory.readRef();
      serializer.readFromHeap(kernel, true, ref);
    }
    
    if(Configuration.getPrintMem()){
      BufferPrinter printer = new BufferPrinter();
      printer.print(objectMemory, 0, 256);
    }
    readBlocksStopwatch.stop();
    stats.setDeserializationTime(readBlocksStopwatch.elapsedTimeMillis());
  }
  
  private int b2i(boolean value){
    if(value){
      return 1;
    } else {
      return 0;
    }
  }
  
  private static native void initializeDriver();
  private native long allocateNativeContext();
  private native void freeNativeContext(long nativeContext);
  
  private native void nativeBuildState(long nativeContext, int deviceIndex, byte[] cubinFile, 
      int cubinLength, int threadCountX, int threadCountY, int threadCountZ,
      int blockCountX, int blockCountY, int numThreads, 
      Memory objectMem, Memory handlesMem, Memory exceptionsMem, Memory classMem, 
      int usingExceptions, int cacheConfig);
  
  private native void cudaRun(long nativeContext, Memory objectMem, 
      int usingKernelTemplates, StatsRow stats);

}
