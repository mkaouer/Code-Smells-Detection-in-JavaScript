#include "edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2.h"

#include <assert.h>
#include <cuda.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define CHECK_STATUS(env,msg,status) \
if (CUDA_SUCCESS != status) {\
  throw_cuda_errror_exception(env, msg, status);\
  return;\
}

#define CHECK_STATUS_RTN(env,msg,status,rtn) \
if (CUDA_SUCCESS != status) {\
  throw_cuda_errror_exception(env, msg, status);\
  return rtn;\
}

static CUdevice cuDevice;
static CUmodule cuModule;
static CUfunction cuFunction;
static CUcontext cuContext;

static void * toSpace;
static void * textureMemory;
static void * handlesMemory;
static void * exceptionsMemory;

static CUdeviceptr gcInfoSpace;
static CUdeviceptr gpuToSpace;
static CUdeviceptr gpuTexture;
static CUdeviceptr gpuHandlesMemory;
static CUdeviceptr gpuExceptionsMemory;
static CUdeviceptr gpuClassMemory;
static CUdeviceptr gpuHeapEndPtr;
static CUdeviceptr gpuBufferSize;
static CUtexref    cache;

static jclass thisRefClass;

static jlong heapEndPtr;
static jlong bufferSize;
static jlong classMemSize;
static jlong numBlocks;
static int maxGridDim;
static int numMultiProcessors;

static int textureMemSize;
static size_t gc_space_size;

/**
* Throws a runtimeexception called CudaMemoryException
* allocd - number of bytes tried to allocate
* id - variable the memory assignment was for
*/
void throw_cuda_errror_exception(JNIEnv *env, const char *message, int error) {
  char msg[1024];
  jclass exp;
  jfieldID fid;
  int a = 0;
  int b = 0;
  char name[1024];

  if(error == CUDA_SUCCESS){
    return;
  }

  exp = (*env)->FindClass(env,"edu/syr/pcpratts/rootbeer/runtime2/cuda/CudaErrorException");

  // we truncate the message to 900 characters to stop any buffer overflow
  switch(error){
    case CUDA_ERROR_OUT_OF_MEMORY:
      sprintf(msg, "CUDA_ERROR_OUT_OF_MEMORY: %.900s",message);
      break;
    case CUDA_ERROR_NO_BINARY_FOR_GPU:
      cuDeviceGetName(name,1024,cuDevice);
      cuDeviceComputeCapability(&a, &b, cuDevice);
      sprintf(msg, "No binary for gpu. Selected %s (%d.%d). 2.0 compatibility required.", name, a, b);
      break;
    default:
      sprintf(msg, "ERROR STATUS:%i : %.900s", error, message);
  }

  fid = (*env)->GetFieldID(env,exp, "cudaError_enum", "I");
  (*env)->SetLongField(env,exp,fid, (jint)error);

  (*env)->ThrowNew(env,exp,msg);
  
  return;
}

void setLongField(JNIEnv *env, jobject obj, const char * name, jlong value){

  jfieldID fid = (*env)->GetFieldID(env, thisRefClass, name, "J");
  (*env)->SetLongField(env, obj, fid, value);
  
  return;
}

void getBestDevice(JNIEnv *env){
  int num_devices;
  int status;
  int i;
  CUdevice temp_device;
  int curr_multiprocessors;
  int max_multiprocessors = -1;
  int max_i = -1;
  
  status = cuDeviceGetCount(&num_devices);
  CHECK_STATUS(env,"error in cuDeviceGetCount",status)
          
  if(num_devices == 0)
      throw_cuda_errror_exception(env,"0 Cuda Devices were found",0);
  
  for(i = 0; i < num_devices; ++i){
    status = cuDeviceGet(&temp_device, i);
    CHECK_STATUS(env,"error in cuDeviceGet",status)
            
    status = cuDeviceGetAttribute(&curr_multiprocessors, CU_DEVICE_ATTRIBUTE_MULTIPROCESSOR_COUNT, temp_device);    
    CHECK_STATUS(env,"error in cuDeviceGetAttribute",status)
            
    if(curr_multiprocessors > max_multiprocessors)
    {
      max_multiprocessors = curr_multiprocessors;
      max_i = i;
    }
  }

  status = cuDeviceGet(&cuDevice, max_i); 
  CHECK_STATUS(env,"error in cuDeviceGet",status)
          
  status = cuDeviceGetAttribute(&maxGridDim, CU_DEVICE_ATTRIBUTE_MAX_GRID_DIM_X, cuDevice);    
  CHECK_STATUS(env,"error in cuDeviceGetAttribute",status)
          
  numMultiProcessors = max_multiprocessors;

  return;
}

void savePointers(JNIEnv * env, jobject this_ref){
  thisRefClass = (*env)->GetObjectClass(env, this_ref);
  setLongField(env, this_ref, "m_ToSpaceAddr", (jlong) toSpace);
  setLongField(env, this_ref, "m_GpuToSpaceAddr", (jlong) gpuToSpace);
  setLongField(env, this_ref, "m_TextureAddr", (jlong) textureMemory);
  setLongField(env, this_ref, "m_GpuTextureAddr", (jlong) gpuTexture);
  setLongField(env, this_ref, "m_HandlesAddr", (jlong) handlesMemory);
  setLongField(env, this_ref, "m_GpuHandlesAddr", (jlong) gpuHandlesMemory);
  setLongField(env, this_ref, "m_ExceptionsHandlesAddr", (jlong) exceptionsMemory);
  setLongField(env, this_ref, "m_GpuExceptionsHandlesAddr", (jlong) gpuExceptionsMemory);
  setLongField(env, this_ref, "m_ToSpaceSize", (jlong) bufferSize);
  setLongField(env, this_ref, "m_MaxGridDim", (jlong) maxGridDim);
  setLongField(env, this_ref, "m_NumMultiProcessors", (jlong) numMultiProcessors);
  setLongField(env, this_ref, "m_NumBlocks", (jlong) numBlocks);
  
  return;
}

void initDevice(JNIEnv * env, jobject this_ref, jint max_blocks_per_proc, jint max_threads_per_block, jlong free_space)
{          
  int status;
  int deviceCount = 0;
  size_t f_mem;
  size_t t_mem;
  size_t to_space_size;
  textureMemSize = 1;

  status = cuDeviceGetCount(&deviceCount);
  CHECK_STATUS(env,"error in cuDeviceGetCount",status)

  getBestDevice(env);
  
  status = cuCtxCreate(&cuContext, CU_CTX_MAP_HOST, cuDevice);  
  CHECK_STATUS(env,"error in cuCtxCreate",status)
  
  status = cuMemGetInfo(&f_mem, &t_mem);
  CHECK_STATUS(env,"error in cuMemGetInfo",status)
          
  to_space_size = f_mem;
  
  numBlocks = numMultiProcessors * max_threads_per_block * max_blocks_per_proc;
  
  //space for 100 types in the scene
  classMemSize = sizeof(jint)*100;
  
  gc_space_size = 1024;
  to_space_size -= (numBlocks * sizeof(jlong));
  to_space_size -= (numBlocks * sizeof(jlong));
  to_space_size -= gc_space_size;
  to_space_size -= free_space;
  to_space_size -= classMemSize;
  //leave 10MB for module
  to_space_size -= 10L*1024L*1024L;

  //to_space_size -= textureMemSize;
  bufferSize = to_space_size;

  status = cuMemHostAlloc(&toSpace, to_space_size, 0);  
  CHECK_STATUS(env,"toSpace memory allocation failed",status)
    
  status = cuMemAlloc(&gpuToSpace, to_space_size);
  CHECK_STATUS(env,"gpuToSpace memory allocation failed",status)
    
  status = cuMemAlloc(&gpuClassMemory, classMemSize);
  CHECK_STATUS(env,"gpuClassMemory memory allocation failed",status)
  
/*
  status = cuMemHostAlloc(&textureMemory, textureMemSize, 0);  
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuMemHostAlloc textureMemory %d\n", status);
  }

  status = cuMemAlloc(&gpuTexture, textureMemSize);
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuMemAlloc gpuTexture %d\n", status);
  }
*/

  status = cuMemHostAlloc(&handlesMemory, numBlocks * sizeof(jlong), CU_MEMHOSTALLOC_WRITECOMBINED); 
  CHECK_STATUS(env,"handlesMemory memory allocation failed",status)

  status = cuMemAlloc(&gpuHandlesMemory, numBlocks * sizeof(jlong)); 
  CHECK_STATUS(env,"gpuHandlesMemory memory allocation failed",status)

  status = cuMemHostAlloc(&exceptionsMemory, numBlocks * sizeof(jlong), 0); 
  CHECK_STATUS(env,"exceptionsMemory memory allocation failed",status)

  status = cuMemAlloc(&gpuExceptionsMemory, numBlocks * sizeof(jlong)); 
  CHECK_STATUS(env,"gpuExceptionsMemory memory allocation failed",status)

  status = cuMemAlloc(&gcInfoSpace, gc_space_size);  
  CHECK_STATUS(env,"gcInfoSpace memory allocation failed",status)

  status = cuMemAlloc(&gpuHeapEndPtr, 8);
  CHECK_STATUS(env,"gpuHeapEndPtr memory allocation failed",status)

  status = cuMemAlloc(&gpuBufferSize, 8);
  CHECK_STATUS(env,"gpuBufferSize memory allocation failed",status)

  savePointers(env, this_ref);
  
  return;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2
 * Method:    reinit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2_reinit
  (JNIEnv * env, jobject this_ref, jint max_blocks_per_proc, jint max_threads_per_block, jlong free_space)
{
  cuMemFreeHost(toSpace);
  cuMemFree(gpuToSpace);
  cuMemFree(gpuClassMemory);
  cuMemFreeHost(handlesMemory);
  cuMemFree(gpuHandlesMemory);
  cuMemFreeHost(exceptionsMemory);
  cuMemFree(gpuExceptionsMemory);
  cuMemFree(gcInfoSpace);
  cuMemFree(gpuHeapEndPtr);
  cuMemFree(gpuBufferSize);
  cuCtxDestroy(cuContext);
  initDevice(env, this_ref, max_blocks_per_proc, max_threads_per_block, free_space);
  
  return;
}

size_t initContext(JNIEnv * env, jint max_blocks_per_proc, jint max_threads_per_block)
{
  size_t to_space_size;
  int status;
  int deviceCount = 0;
  size_t f_mem;
  size_t t_mem;
  
  status = cuDeviceGetCount(&deviceCount);
  CHECK_STATUS_RTN(env,"error in cuDeviceGetCount",status, 0);

  getBestDevice(env);

  status = cuCtxCreate(&cuContext, CU_CTX_MAP_HOST, cuDevice);
  CHECK_STATUS_RTN(env,"error in cuCtxCreate",status, 0)
  
  status = cuMemGetInfo (&f_mem, &t_mem);
  CHECK_STATUS_RTN(env,"error in cuMemGetInfo",status, 0)
  
  to_space_size = f_mem;

  //space for 100 types in the scene
  classMemSize = sizeof(jint)*100;
  
  numBlocks = numMultiProcessors * max_threads_per_block * max_blocks_per_proc;
  
  gc_space_size = 1024;
  to_space_size -= (numBlocks * sizeof(jlong));
  to_space_size -= (numBlocks * sizeof(jlong));
  to_space_size -= gc_space_size;
  to_space_size -= classMemSize;
  //leave 10MB for module
  to_space_size -= 10L*1024L*1024L;
  
  return to_space_size;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2
 * Method:    findReserveMem
 * Signature: ()I
 */
JNIEXPORT jlong JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2_findReserveMem
  (JNIEnv * env, jobject this_ref, jint max_blocks_per_proc, jint max_threads_per_block)
{
  size_t to_space_size;
  size_t temp_size;
  int status;
  int deviceCount = 0;
  jlong prev_i;
  jlong i;
  size_t f_mem;
  size_t t_mem;

  status = cuInit(0);
  CHECK_STATUS_RTN(env,"error in cuInit",status, 0)

  printf("automatically determining CUDA reserve space...\n");
  
  to_space_size = initContext(env, max_blocks_per_proc, max_threads_per_block);
  numBlocks = numMultiProcessors * max_threads_per_block * max_blocks_per_proc;
  
  for(i = 1024L*1024L; i < to_space_size; i += 100L*1024L*1024L){
    temp_size = to_space_size - i;
  
    printf("attempting allocation with temp_size: %lu to_space_size: %lu i: %ld\n", temp_size, to_space_size, i);
 
    status = cuMemHostAlloc(&toSpace, temp_size, 0);  
    if(status != CUDA_SUCCESS){
      cuCtxDestroy(cuContext);
      initContext(env, max_blocks_per_proc, max_threads_per_block);
      continue;
    }
    
    status = cuMemAlloc(&gpuToSpace, temp_size);
    if(status != CUDA_SUCCESS){
      cuCtxDestroy(cuContext);
      initContext(env, max_blocks_per_proc, max_threads_per_block);
      continue;
    } 

    status = cuMemAlloc(&gpuClassMemory, classMemSize);
    if(status != CUDA_SUCCESS){
      cuCtxDestroy(cuContext);
      initContext(env, max_blocks_per_proc, max_threads_per_block);
      continue;
    } 

    status = cuMemHostAlloc(&handlesMemory, numBlocks * sizeof(jlong), 0); 
    if(status != CUDA_SUCCESS){
      cuCtxDestroy(cuContext);
      initContext(env, max_blocks_per_proc, max_threads_per_block);
      continue;
    } 

    status = cuMemAlloc(&gpuHandlesMemory, numBlocks * sizeof(jlong)); 
    if(status != CUDA_SUCCESS){
      cuCtxDestroy(cuContext);
      initContext(env, max_blocks_per_proc, max_threads_per_block);
      continue;
    } 

    status = cuMemHostAlloc(&exceptionsMemory, numBlocks * sizeof(jlong), 0); 
    if(status != CUDA_SUCCESS){
      cuCtxDestroy(cuContext);
      initContext(env, max_blocks_per_proc, max_threads_per_block);
      continue;
    } 

    status = cuMemAlloc(&gpuExceptionsMemory, numBlocks * sizeof(jlong)); 
    if(status != CUDA_SUCCESS){
      cuCtxDestroy(cuContext);
      initContext(env, max_blocks_per_proc, max_threads_per_block);
      continue;
    } 

    status = cuMemAlloc(&gcInfoSpace, gc_space_size);  
    if(status != CUDA_SUCCESS){
      cuCtxDestroy(cuContext);
      initContext(env, max_blocks_per_proc, max_threads_per_block);
      continue;
    } 

    status = cuMemAlloc(&gpuHeapEndPtr, 8);
    if(status != CUDA_SUCCESS){
      cuCtxDestroy(cuContext);
      initContext(env, max_blocks_per_proc, max_threads_per_block);
      continue;
    } 

    status = cuMemAlloc(&gpuBufferSize, 8);
    if(status != CUDA_SUCCESS){
      cuCtxDestroy(cuContext);
      initContext(env, max_blocks_per_proc, max_threads_per_block);
      continue;
    } 


    bufferSize = temp_size;
    savePointers(env, this_ref);

    return i;
  }
  throw_cuda_errror_exception(env, "unable to find enough space using CUDA", 0); 
  return 0;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2
 * Method:    printDeviceInfo
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2_printDeviceInfo
  (JNIEnv *env, jclass cls)
{
    int i, a=0, b=0, status;
    int num_devices = 0;
    char str[1024];
    size_t free_mem, total_mem;
 
    status = cuInit(0);
    CHECK_STATUS(env,"error in cuInit",status)
    
    cuDeviceGetCount(&num_devices);
    printf("%d cuda gpus found\n", num_devices);
 
    for (i = 0; i < num_devices; ++i)
    {
        CUdevice dev;
        status = cuDeviceGet(&dev, i);
        CHECK_STATUS(env,"error in cuDeviceGet",status)

        status = cuCtxCreate(&cuContext, CU_CTX_MAP_HOST, dev);
        CHECK_STATUS(env,"error in cuCtxCreate",status)
                
        printf("\nGPU:%d\n", i);
        
        if(cuDeviceComputeCapability(&a, &b, dev) == CUDA_SUCCESS)
            printf("Version:                       %i.%i\n", a, b);
        
        if(cuDeviceGetName(str,1024,dev) == CUDA_SUCCESS)
            printf("Name:                          %s\n", str);
        
        if(cuMemGetInfo(&free_mem, &total_mem) == CUDA_SUCCESS){
          #if (defined linux || defined __APPLE_CC__)
            printf("Total global memory:           %zu/%zu (Free/Total) MBytes\n", free_mem/1024/1024, total_mem/1024/1024);
          #else
            printf("Total global memory:           %Iu/%Iu (Free/Total) MBytes\n", free_mem/1024/1024, total_mem/1024/1024);
          #endif
        }
        
        if(cuDeviceGetAttribute(&a, CU_DEVICE_ATTRIBUTE_MAX_REGISTERS_PER_BLOCK,dev) == CUDA_SUCCESS)
            printf("Total registers per block:     %i\n", a);
        if(cuDeviceGetAttribute(&a, CU_DEVICE_ATTRIBUTE_WARP_SIZE,dev) == CUDA_SUCCESS)
            printf("Warp size:                     %i\n", a);
        if(cuDeviceGetAttribute(&a, CU_DEVICE_ATTRIBUTE_MAX_PITCH,dev) == CUDA_SUCCESS)
            printf("Maximum memory pitch:          %i\n", a);
        if(cuDeviceGetAttribute(&a, CU_DEVICE_ATTRIBUTE_MAX_THREADS_PER_BLOCK,dev) == CUDA_SUCCESS)
            printf("Maximum threads per block:     %i\n", a);        
        if(cuDeviceGetAttribute(&a, CU_DEVICE_ATTRIBUTE_MAX_SHARED_MEMORY_PER_BLOCK,dev) == CUDA_SUCCESS)
            printf("Total shared memory per block  %.2f KB\n", a/1024.0);
        if(cuDeviceGetAttribute(&a, CU_DEVICE_ATTRIBUTE_CLOCK_RATE,dev) == CUDA_SUCCESS)
            printf("Clock rate:                    %.2f MHz\n",  a/1000000.0);
        if(cuDeviceGetAttribute(&a, CU_DEVICE_ATTRIBUTE_MEMORY_CLOCK_RATE,dev) == CUDA_SUCCESS)
            printf("Memory Clock rate:             %.2f\n",  a/1000000.0);
        if(cuDeviceGetAttribute(&a, CU_DEVICE_ATTRIBUTE_TOTAL_CONSTANT_MEMORY,dev) == CUDA_SUCCESS)
            printf("Total constant memory:         %.2f MB\n",  a/1024.0/1024.0);
        if(cuDeviceGetAttribute(&a, CU_DEVICE_ATTRIBUTE_INTEGRATED,dev) == CUDA_SUCCESS)
            printf("Integrated:                    %i\n",  a);
        if(cuDeviceGetAttribute(&a, CU_DEVICE_ATTRIBUTE_MAX_THREADS_PER_MULTIPROCESSOR,dev) == CUDA_SUCCESS)
            printf("Max threads per multiprocessor:%i\n",  a);    
        if(cuDeviceGetAttribute(&a, CU_DEVICE_ATTRIBUTE_MULTIPROCESSOR_COUNT,dev) == CUDA_SUCCESS)
            printf("Number of multiprocessors:     %i\n",  a);    
      
        if(cuDeviceGetAttribute(&a, CU_DEVICE_ATTRIBUTE_MAX_BLOCK_DIM_X,dev) == CUDA_SUCCESS)
            printf("Maximum dimension x of block:  %i\n", a);
        if(cuDeviceGetAttribute(&a, CU_DEVICE_ATTRIBUTE_MAX_BLOCK_DIM_Y,dev) == CUDA_SUCCESS)
            printf("Maximum dimension y of block:  %i\n", a);
        if(cuDeviceGetAttribute(&a, CU_DEVICE_ATTRIBUTE_MAX_BLOCK_DIM_Z,dev) == CUDA_SUCCESS)
            printf("Maximum dimension z of block:  %i\n", a);
        
        if(cuDeviceGetAttribute(&a, CU_DEVICE_ATTRIBUTE_MAX_GRID_DIM_X,dev) == CUDA_SUCCESS)
            printf("Maximum dimension x of grid:   %i\n", a);
        if(cuDeviceGetAttribute(&a, CU_DEVICE_ATTRIBUTE_MAX_GRID_DIM_Y,dev) == CUDA_SUCCESS)
            printf("Maximum dimension y of grid:   %i\n", a);
        if(cuDeviceGetAttribute(&a, CU_DEVICE_ATTRIBUTE_MAX_GRID_DIM_Z,dev) == CUDA_SUCCESS)
            printf("Maximum dimension z of grid:   %i\n", a);
			
        cuCtxDestroy(cuContext);
    } 
	
	return;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2
 * Method:    setup
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2_setup
  (JNIEnv *env, jobject this_ref, jint max_blocks_per_proc, jint max_threads_per_block, jlong free_space)
{
  int status;
  
  status = cuInit(0);
  CHECK_STATUS(env,"error in cuInit",status)

  initDevice(env, this_ref, max_blocks_per_proc, max_threads_per_block, free_space);
  
  return;
}

void * readCubinFile(const char * filename){

  int i;
  jlong size;
  char * ret;
  int block_size;
  int num_blocks;
  int leftover;
  char * dest;
  
  FILE * file = fopen(filename, "r");
  fseek(file, 0, SEEK_END);
  size = ftell(file);
  fseek(file, 0, SEEK_SET);

  ret = (char *) malloc(size);
  block_size = 4096;
  num_blocks = (int) (size / block_size);
  leftover = (int) (size % block_size);

  dest = ret;
  for(i = 0; i < num_blocks; ++i){
    fread(dest, 1, block_size, file);
    dest += block_size;
  }
  if(leftover != 0){
    fread(dest, 1, leftover, file);
  }

  fclose(file);
  return (void *) ret;
}

void * readCubinFileFromBuffers(JNIEnv *env, jobject buffers, jint size, jint total_size){
  int i, j;
  int dest_offset = 0;
  int len;
  char * data;
  char * ret = (char *) malloc(total_size);

  jclass cls = (*env)->GetObjectClass(env, buffers);
  jmethodID mid = (*env)->GetMethodID(env, cls, "get", "(I)Ljava/lang/Object;");
  for(i = 0; i < size; ++i){
    jobject buffer = (*env)->CallObjectMethod(env, buffers, mid, i);
    jbyteArray * arr = (jbyteArray*) &buffer;
    len = (*env)->GetArrayLength(env, *arr);
    data = (*env)->GetByteArrayElements(env, *arr, NULL);
    memcpy((void *) (ret + dest_offset), (void *) data, len);
    dest_offset += len;
    (*env)->ReleaseByteArrayElements(env, *arr, data, 0);
  }

  return (void *) ret;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2
 * Method:    writeClassTypeRef
 * Signature: ([I)V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2_writeClassTypeRef
  (JNIEnv *env, jobject this_ref, jintArray jarray)
{
  int i;
  jint * native_array = (*env)->GetIntArrayElements(env, jarray, 0);
  cuMemcpyHtoD(gpuClassMemory, native_array, classMemSize);
  (*env)->ReleaseIntArrayElements(env, jarray, native_array, 0);
  
  return;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2
 * Method:    loadFunction
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2_loadFunction
  (JNIEnv *env, jobject this_obj, jlong heap_end_ptr, jobject buffers, jint size, jint total_size, jint num_blocks){

  void * fatcubin;
  int offset;
  CUresult status;
  char * native_filename;
  heapEndPtr = heap_end_ptr;
  
  cuCtxPushCurrent(cuContext);
  fatcubin = readCubinFileFromBuffers(env, buffers, size, total_size);
  status = cuModuleLoadFatBinary(&cuModule, fatcubin);
  CHECK_STATUS(env, "error in cuModuleLoad", status);
  free(fatcubin);

  status = cuModuleGetFunction(&cuFunction, cuModule, "_Z5entryPcS_PiPxS1_S0_S0_i"); 
  CHECK_STATUS(env,"error in cuModuleGetFunction",status)

  status = cuFuncSetCacheConfig(cuFunction, CU_FUNC_CACHE_PREFER_L1);
  CHECK_STATUS(env,"error in cuFuncSetCacheConfig",status)

  status = cuParamSetSize(cuFunction, (7 * sizeof(CUdeviceptr) + sizeof(int))); 
  CHECK_STATUS(env,"error in cuParamSetSize",status)

  offset = 0;
  status = cuParamSetv(cuFunction, offset, (void *) &gcInfoSpace, sizeof(CUdeviceptr)); 
  CHECK_STATUS(env,"error in cuParamSetv gcInfoSpace",status)
  offset += sizeof(CUdeviceptr);

  status = cuParamSetv(cuFunction, offset, (void *) &gpuToSpace, sizeof(CUdeviceptr)); 
  CHECK_STATUS(env,"error in cuParamSetv gpuToSpace",status)
  offset += sizeof(CUdeviceptr);

  status = cuParamSetv(cuFunction, offset, (void *) &gpuHandlesMemory, sizeof(CUdeviceptr)); 
  CHECK_STATUS(env,"error in cuParamSetv gpuHandlesMemory %",status)
  offset += sizeof(CUdeviceptr);

  status = cuParamSetv(cuFunction, offset, (void *) &gpuHeapEndPtr, sizeof(CUdeviceptr)); 
  CHECK_STATUS(env,"error in cuParamSetv gpuHeapEndPtr",status)
  offset += sizeof(CUdeviceptr);

  status = cuParamSetv(cuFunction, offset, (void *) &gpuBufferSize, sizeof(CUdeviceptr));
  CHECK_STATUS(env,"error in cuParamSetv gpuBufferSize",status)
  offset += sizeof(CUdeviceptr); 

  status = cuParamSetv(cuFunction, offset, (void *) &gpuExceptionsMemory, sizeof(CUdeviceptr)); 
  CHECK_STATUS(env,"error in cuParamSetv gpuExceptionsMemory",status)
  offset += sizeof(CUdeviceptr);

  status = cuParamSetv(cuFunction, offset, (void *) &gpuClassMemory, sizeof(CUdeviceptr)); 
  CHECK_STATUS(env,"error in cuParamSetv gpuClassMemory",status)
  offset += sizeof(CUdeviceptr);

  status = cuParamSeti(cuFunction, offset, num_blocks); 
  CHECK_STATUS(env,"error in cuParamSetv num_blocks",status)
  offset += sizeof(int);
  cuCtxPopCurrent(&cuContext);
  
  return;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2
 * Method:    runBlocks
 * Signature: (I)V
 */
JNIEXPORT jint JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2_runBlocks
  (JNIEnv *env, jobject this_obj, jint num_blocks, jint block_shape, jint grid_shape){

  CUresult status;
  jlong * infoSpace = (jlong *) malloc(gc_space_size);
  infoSpace[1] = heapEndPtr;
  cuCtxPushCurrent(cuContext);
  cuMemcpyHtoD(gcInfoSpace, infoSpace, gc_space_size);
  cuMemcpyHtoD(gpuToSpace, toSpace, heapEndPtr);
  //cuMemcpyHtoD(gpuTexture, textureMemory, textureMemSize);
  cuMemcpyHtoD(gpuHandlesMemory, handlesMemory, num_blocks * sizeof(jlong));
  cuMemcpyHtoD(gpuHeapEndPtr, &heapEndPtr, sizeof(jlong));
  cuMemcpyHtoD(gpuBufferSize, &bufferSize, sizeof(jlong));
  
/*
  status = cuModuleGetTexRef(&cache, cuModule, "m_Cache");  
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuModuleGetTexRef %d\n", status);
  }

  status = cuTexRefSetAddress(0, cache, gpuTexture, textureMemSize);
  if (CUDA_SUCCESS != status) 
  {
    printf("error in cuTextRefSetAddress %d\n", status);
  }
*/

  status = cuFuncSetBlockShape(cuFunction, block_shape, 1, 1);
  if(status != CUDA_SUCCESS){
    free(infoSpace);
    cuCtxPopCurrent(&cuContext);
  }
  CHECK_STATUS_RTN(env,"error in cuFuncSetBlockShape",status, (jint)status);

  status = cuLaunchGrid(cuFunction, grid_shape, 1);
  if(status != CUDA_SUCCESS){
    free(infoSpace);
    cuCtxPopCurrent(&cuContext);
  }
  CHECK_STATUS_RTN(env,"error in cuLaunchGrid",status, (jint)status)

  status = cuCtxSynchronize();  
  if(status != CUDA_SUCCESS){
    free(infoSpace);
    cuCtxPopCurrent(&cuContext);
  }
  CHECK_STATUS_RTN(env,"error in cuCtxSynchronize",status, (jint)status)
  
  cuMemcpyDtoH(infoSpace, gcInfoSpace, gc_space_size);
  heapEndPtr = infoSpace[1];
  cuMemcpyDtoH(toSpace, gpuToSpace, heapEndPtr);
  cuMemcpyDtoH(exceptionsMemory, gpuExceptionsMemory, num_blocks * sizeof(jlong));
  free(infoSpace);
  cuCtxPopCurrent(&cuContext);

  return 0;
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2
 * Method:    unload
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime2_cuda_CudaRuntime2_unload
  (JNIEnv *env, jobject this_obj){

  cuModuleUnload(cuModule);
  cuFunction = (CUfunction) 0;  
 
  return;
}
