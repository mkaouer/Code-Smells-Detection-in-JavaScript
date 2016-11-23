package org.trifort.rootbeer.runtime;

public class GpuDevice {

  public static final int DEVICE_TYPE_CUDA = 1;
  public static final int DEVICE_TYPE_OPENCL = 2;
  public static final int DEVICE_TYPE_NEMU = 3;
  public static final int DEVICE_TYPE_JAVA = 4;
  
  public static GpuDevice newCudaDevice(int device_id, int major_version, 
    int minor_version, String device_name, long free_global_mem_size, 
    long total_global_mem_size, int max_registers_per_block, int warp_size, 
    int max_pitch, int max_threads_per_block, int max_shared_memory_per_block, 
    int clock_rate, int memory_clock_rate, int constant_mem_size, 
    boolean integrated, int max_threads_per_multiprocessor,
    int multiprocessor_count, int max_block_dim_x, int max_block_dim_y,
    int max_block_dim_z, int max_grid_dim_x, int max_grid_dim_y,
    int max_grid_dim_z){
    
    GpuDevice ret = new GpuDevice(DEVICE_TYPE_CUDA);
    ret.setDeviceId(device_id);
    ret.setVersion(major_version, minor_version, 0);
    ret.setDeviceName(device_name);
    ret.setFreeGlobalMemoryBytes(free_global_mem_size);
    ret.setTotalGlobalMemoryBytes(total_global_mem_size);
    ret.setMaxRegistersPerBlock(max_registers_per_block);
    ret.setWarpSize(warp_size);
    ret.setMaxPitch(max_pitch);
    ret.setMaxThreadsPerBlock(max_threads_per_block);
    ret.setMaxSharedMemoryPerBlock(max_shared_memory_per_block);
    ret.setClockRateHz(clock_rate);
    ret.setMemoryClockRateHz(memory_clock_rate);
    ret.setTotalConstantMemoryBytes(constant_mem_size);
    ret.setIntegrated(integrated);
    ret.setMaxThreadsPerMultiprocessor(max_threads_per_multiprocessor);
    ret.setMultiProcessorCount(multiprocessor_count);
    ret.setMaxBlockDimX(max_block_dim_x);
    ret.setMaxBlockDimY(max_block_dim_y);
    ret.setMaxBlockDimZ(max_block_dim_z);
    ret.setMaxGridDimX(max_grid_dim_x);
    ret.setMaxGridDimY(max_grid_dim_y);
    ret.setMaxGridDimZ(max_grid_dim_z);
    return ret;
  }
  
  public static GpuDevice newOpenCLDevice(String device_name){
    GpuDevice ret = new GpuDevice(DEVICE_TYPE_OPENCL);
    ret.setDeviceName(device_name);
    return ret;
  }
 
  private int m_deviceType;
  private int m_deviceId;
  private int m_majorVersion;
  private int m_minorVersion;
  private int m_patchVersion;
  private String m_name;
  private long m_freeGlobalMemoryBytes;
  private long m_totalGlobalMemoryBytes;
  private int m_maxRegistersPerBlock;
  private int m_warpSize;
  private int m_maxPitch;
  private int m_maxThreadsPerBlock;
  private int m_maxSharedMemoryPerBlock;
  private int m_clockRateHz;
  private int m_memoryClockRateHz;
  private int m_totalConstantMemoryBytes;
  private boolean m_integrated;
  private int m_maxThreadsPerMultiprocessor;
  private int m_multiProcessorCount;
  private int m_maxBlockDimX;
  private int m_maxBlockDimY;
  private int m_maxBlockDimZ;
  private int m_maxGridDimX;
  private int m_maxGridDimY;
  private int m_maxGridDimZ;

  public GpuDevice(int device_type) {
    m_deviceType = device_type;
  }
  
  public Context createContext(){
    if(m_deviceType == DEVICE_TYPE_CUDA){
      return new CUDAContext(this);
    } else {
      throw new UnsupportedOperationException();
    }
  }
  
  public Context createContext(int memorySize){
    if(m_deviceType == DEVICE_TYPE_CUDA){
      CUDAContext ret = new CUDAContext(this);
      ret.setMemorySize(memorySize);
      return ret;
    } else {
      throw new UnsupportedOperationException();
    }
  }
  
  public int getDeviceType(){
    return m_deviceType;
  }
  
  public void setDeviceId(int device_id){
    m_deviceId = device_id;
  }
  
  public int getDeviceId(){
    return m_deviceId;
  }
  
  public void setVersion(int major, int minor, int patch){
    m_majorVersion = major;
    m_minorVersion = minor;
    m_patchVersion = patch;
  }
  
  public int getMajorVersion(){
    return m_majorVersion;
  }
  
  public int getMinorVersion(){
    return m_minorVersion;
  }
  
  public int getPatchVersion(){
    return m_patchVersion;
  }
  
  public void setDeviceName(String name){
    m_name = name;
  }
  
  public String getDeviceName(){
    return m_name;
  }
  
  public void setFreeGlobalMemoryBytes(long size){
    m_freeGlobalMemoryBytes = size;
  }
  
  public long getFreeGlobalMemoryBytes(){
    return m_freeGlobalMemoryBytes;
  }

  public void setTotalGlobalMemoryBytes(long size){
    m_totalGlobalMemoryBytes = size;
  }
  
  public long getTotalGlobalMemoryBytes(){
    return m_totalGlobalMemoryBytes;
  }
  
  public void setMaxRegistersPerBlock(int value){
    m_maxRegistersPerBlock = value;
  }
  
  public int getMaxRegistersPerBlock(){
    return m_maxRegistersPerBlock;
  }
  
  public void setWarpSize(int value){
    m_warpSize = value;
  }
  
  public int getWarpSize(){
    return m_warpSize;
  }
  
  public void setMaxPitch(int value){
    m_maxPitch = value;
  }
  
  public int getMaxPitch(){
    return m_maxPitch;
  }
  
  public void setMaxThreadsPerBlock(int value){
    m_maxThreadsPerBlock = value;
  }
  
  public int getMaxThreadsPerBlock(){
    return m_maxThreadsPerBlock;
  }
  
  public void setMaxSharedMemoryPerBlock(int value){
    m_maxSharedMemoryPerBlock = value;
  }
  
  public int getMaxSharedMemoryPerBlock(){
    return m_maxSharedMemoryPerBlock;
  }
  
  public void setClockRateHz(int value){
    m_clockRateHz = value;
  }
  
  public int getClockRateHz(){
    return m_clockRateHz;
  }
  
  public void setMemoryClockRateHz(int value){
    m_memoryClockRateHz = value;
  }
  
  public int getMemoryClockRateHz(){
    return m_memoryClockRateHz;
  }
  
  public void setTotalConstantMemoryBytes(int value){
    m_totalConstantMemoryBytes = value;
  }
  
  public int getTotalConstantMemoryBytes(){
    return m_totalConstantMemoryBytes;
  }
  
  public void setIntegrated(boolean value){
    m_integrated = value;
  }
  
  public boolean getIntegrated(){
    return m_integrated;
  }
  
  public void setMaxThreadsPerMultiprocessor(int value){
    m_maxThreadsPerMultiprocessor = value;
  }
  
  public int getMaxThreadsPerMultiprocessor(){
    return m_maxThreadsPerMultiprocessor;
  }
  
  public void setMultiProcessorCount(int value){
    m_multiProcessorCount = value;
  }
  
  public int getMultiProcessorCount(){
    return m_multiProcessorCount;
  }
  
  public void setMaxBlockDimX(int value){
    m_maxBlockDimX = value;
  }
  
  public int getMaxBlockDimX(){
    return m_maxBlockDimX;
  }
  
  public void setMaxBlockDimY(int value){
    m_maxBlockDimY = value;
  }
  
  public int getMaxBlockDimY(){
    return m_maxBlockDimY;
  }
  
  public void setMaxBlockDimZ(int value){
    m_maxBlockDimZ = value;
  }
  
  public int getMaxBlockDimZ(){
    return m_maxBlockDimZ;
  }

  public void setMaxGridDimX(int value){
    m_maxGridDimX = value;
  }
  
  public int getMaxGridDimX(){
    return m_maxGridDimX;
  }
  
  public void setMaxGridDimY(int value){
    m_maxGridDimY = value;
  }
  
  public int getMaxGridDimY(){
    return m_maxGridDimY;
  }
  
  public void setMaxGridDimZ(int value){
    m_maxGridDimZ = value;
  }
  
  public int getMaxGridDimZ(){
    return m_maxGridDimZ;
  }
}