#include "CUDARuntime.h"
#include "Stopwatch.h"
#include <cuda.h>

struct ContextState {
  CUdevice device;
  CUcontext context;
  CUmodule module;
  CUfunction function;

  CUdeviceptr gpu_info_space;
  CUdeviceptr gpu_object_mem;
  CUdeviceptr gpu_handles_mem;
  CUdeviceptr gpu_exceptions_mem;
  CUdeviceptr gpu_class_mem;
  CUdeviceptr gpu_heap_end;

  void * cpu_object_mem;
  void * cpu_handles_mem;
  void * cpu_exceptions_mem;
  void * cpu_class_mem;

  jlong cpu_object_mem_size;
  jlong cpu_handles_mem_size;
  jlong cpu_exceptions_mem_size;
  jlong cpu_class_mem_size;

  jint * info_space;
  jint block_count_x;
  jint block_count_y;
  jint using_kernel_templates_offset;
  jint using_exceptions;
  jint context_built;

  struct stopwatch execMemcopyToDevice;
  struct stopwatch execGpuRun;
  struct stopwatch execMemcopyFromDevice;
};

jclass cuda_memory_class;
jmethodID get_address_method;
jmethodID get_size_method;
jmethodID get_heap_end_method;
jmethodID set_heap_end_method;
jclass stats_row_class;
jmethodID set_driver_times;

#define CHECK_STATUS(env,msg,status,device) \
if (CUDA_SUCCESS != status) {\
  throw_cuda_errror_exception(env, msg, status, device);\
  return;\
}

/**
* Throws a runtimeexception called CudaMemoryException
* allocd - number of bytes tried to allocate
* id - variable the memory assignment was for
*/
void throw_cuda_errror_exception(JNIEnv *env, const char *message, int error,
  CUdevice device) {

  char msg[1024];
  jclass exp;
  jfieldID fid;
  int a = 0;
  int b = 0;
  char name[1024];

  exp = (*env)->FindClass(env,"org/trifort/rootbeer/runtime/CudaErrorException");

  // we truncate the message to 900 characters to stop any buffer overflow
  switch(error){
    case CUDA_ERROR_OUT_OF_MEMORY:
      sprintf(msg, "CUDA_ERROR_OUT_OF_MEMORY: %.900s",message);
      break;
    case CUDA_ERROR_NO_BINARY_FOR_GPU:
      cuDeviceGetName(name,1024,device);
      cuDeviceComputeCapability(&a, &b, device);
      sprintf(msg, "No binary for gpu. %s Selected %s (%d.%d). 2.0 compatibility required.", message, name, a, b);
      break;
    default:
      sprintf(msg, "ERROR STATUS:%i : %.900s", error, message);
  }

  fid = (*env)->GetFieldID(env,exp, "cudaError_enum", "I");
  (*env)->SetLongField(env,exp,fid, (jint)error);
  (*env)->ThrowNew(env,exp,msg);
  return;
}

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_CUDAContext_initializeDriver
  (JNIEnv *env, jobject this_ref)
{
  cuda_memory_class = (*env)->FindClass(env, "org/trifort/rootbeer/runtime/FixedMemory");
  get_address_method = (*env)->GetMethodID(env, cuda_memory_class, "getAddress", "()J");
  get_size_method = (*env)->GetMethodID(env, cuda_memory_class, "getSize", "()J");
  get_heap_end_method = (*env)->GetMethodID(env, cuda_memory_class, "getHeapEndPtr", "()J");
  set_heap_end_method = (*env)->GetMethodID(env, cuda_memory_class, "setHeapEndPtr", "(J)V");
  stats_row_class = (*env)->FindClass(env, "org/trifort/rootbeer/runtime/StatsRow");
  set_driver_times = (*env)->GetMethodID(env, stats_row_class, "setDriverTimes", "(JJJ)V");
}

JNIEXPORT jlong JNICALL Java_org_trifort_rootbeer_runtime_CUDAContext_allocateNativeContext
  (JNIEnv *env, jobject this_ref)
{
  struct ContextState * ret = (struct ContextState *) malloc(sizeof(struct ContextState));
  ret->context_built = 0;
  return (jlong) ret;
}

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_CUDAContext_freeNativeContext
  (JNIEnv *env, jobject this_ref, jlong reference)
{
  struct ContextState * stateObject = (struct ContextState *) reference;

  if(stateObject->context_built){
    free(stateObject->info_space);
    cuMemFree(stateObject->gpu_info_space);
    cuMemFree(stateObject->gpu_object_mem);
    cuMemFree(stateObject->gpu_handles_mem);
    cuMemFree(stateObject->gpu_exceptions_mem);
    cuMemFree(stateObject->gpu_class_mem);
    cuMemFree(stateObject->gpu_heap_end);
    cuCtxDestroy(stateObject->context);
  }

  free(stateObject);
}

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_CUDAContext_nativeBuildState
  (JNIEnv *env, jobject this_ref, jlong nativeContext, jint device_index,
   jbyteArray cubin_file, jint cubin_length, jint thread_count_x, jint thread_count_y,
   jint thread_count_z, jint block_count_x, jint block_count_y,
   jint num_threads, jobject object_mem, jobject handles_mem,
   jobject exceptions_mem, jobject class_mem,
   jint using_exceptions, jint cache_config)

{
  CUresult status;
  void * fatcubin;
  int offset;
  CUfunc_cache cache_config_enum;
  jint objectMemSizeShifted;

  struct ContextState * stateObject = (struct ContextState *) nativeContext;

  stateObject->block_count_x = block_count_x;
  stateObject->block_count_y = block_count_y;
  stateObject->using_exceptions = using_exceptions;

  status = cuDeviceGet(&(stateObject->device), device_index);
  CHECK_STATUS(env, "Error in cuDeviceGet", status, stateObject->device)

  status = cuCtxCreate(&(stateObject->context), CU_CTX_MAP_HOST,
    stateObject->device);
  CHECK_STATUS(env,"Error in cuCtxCreate", status, stateObject->device)

  fatcubin = malloc(cubin_length);
  (*env)->GetByteArrayRegion(env, cubin_file, 0, cubin_length, fatcubin);

  status = cuModuleLoadFatBinary(&(stateObject->module), fatcubin);
  CHECK_STATUS(env, "Error in cuModuleLoad", status, stateObject->device)
  free(fatcubin);

  status = cuModuleGetFunction(&(stateObject->function), stateObject->module,
    "_Z5entryPiS_ii");
  CHECK_STATUS(env, "Error in cuModuleGetFunction", status, stateObject->device)

  if(cache_config != 0){
    switch(cache_config){
      case 1:
        cache_config_enum = CU_FUNC_CACHE_PREFER_SHARED;
        break;
      case 2:
        cache_config_enum = CU_FUNC_CACHE_PREFER_L1;
        break;
      case 3:
        cache_config_enum = CU_FUNC_CACHE_PREFER_EQUAL;
        break;
    }
    status = cuFuncSetCacheConfig(stateObject->function, cache_config_enum);
    CHECK_STATUS(env, "Error in cuFuncSetCacheConfig", status, stateObject->device)
  }

  stateObject->cpu_object_mem = (void *) (*env)->CallLongMethod(env, object_mem, get_address_method);
  stateObject->cpu_handles_mem = (void *) (*env)->CallLongMethod(env, handles_mem, get_address_method);
  stateObject->cpu_exceptions_mem = (void *) (*env)->CallLongMethod(env, exceptions_mem, get_address_method);
  stateObject->cpu_class_mem = (void *) (*env)->CallLongMethod(env, class_mem, get_address_method);

  stateObject->cpu_object_mem_size = (*env)->CallLongMethod(env, object_mem, get_size_method);
  stateObject->cpu_handles_mem_size = (*env)->CallLongMethod(env, handles_mem, get_size_method);
  stateObject->cpu_exceptions_mem_size = (*env)->CallLongMethod(env, exceptions_mem, get_size_method);
  stateObject->cpu_class_mem_size = (*env)->CallLongMethod(env, class_mem, get_size_method);

  //----------------------------------------------------------------------------
  //allocate mem
  //----------------------------------------------------------------------------

  stateObject->info_space = (jint *) malloc(4);

  status = cuMemAlloc(&(stateObject->gpu_info_space), 4);
  CHECK_STATUS(env, "Error in cuMemAlloc: gpu_info_mem", status, stateObject->device)

  status = cuMemAlloc(&(stateObject->gpu_object_mem), stateObject->cpu_object_mem_size);
  CHECK_STATUS(env, "Error in cuMemAlloc: gpu_object_mem", status, stateObject->device)

  status = cuMemAlloc(&(stateObject->gpu_handles_mem), stateObject->cpu_handles_mem_size);
  CHECK_STATUS(env, "Error in cuMemAlloc: gpu_object_mem", status, stateObject->device)

  if(using_exceptions){
    status = cuMemAlloc(&(stateObject->gpu_exceptions_mem), stateObject->cpu_exceptions_mem_size);
    CHECK_STATUS(env, "Error in cuMemAlloc: gpu_exceptions_mem", status, stateObject->device)
  }

  status = cuMemAlloc(&(stateObject->gpu_class_mem), stateObject->cpu_class_mem_size);
  CHECK_STATUS(env, "Error in cuMemAlloc: gpu_class_mem", status, stateObject->device)

  status = cuMemAlloc(&(stateObject->gpu_heap_end), 4);
  CHECK_STATUS(env, "Error in cuMemAlloc: gpu_heap_end", status, stateObject->device)

  //----------------------------------------------------------------------------
  //set function parameters
  //----------------------------------------------------------------------------
  status = cuParamSetSize(stateObject->function, (2 * sizeof(CUdeviceptr)) + (2 * sizeof(int)));
  CHECK_STATUS(env, "Error in cuParamSetSize", status, stateObject->device)

  offset = 0;
  status = cuParamSetv(stateObject->function, offset, (void *) &(stateObject->gpu_handles_mem), sizeof(CUdeviceptr));
  CHECK_STATUS(env, "Error in cuParamSetv: gpu_handles_mem", status, stateObject->device)
  offset += sizeof(CUdeviceptr);

  status = cuParamSetv(stateObject->function, offset, (void *) &(stateObject->gpu_exceptions_mem), sizeof(CUdeviceptr));
  CHECK_STATUS(env, "Error in cuParamSetv: gpu_exceptions_mem", status, stateObject->device)
  offset += sizeof(CUdeviceptr);

  status = cuParamSeti(stateObject->function, offset, num_threads);
  CHECK_STATUS(env, "Error in cuParamSeti: num_threads", status, stateObject->device)
  offset += sizeof(int);

  stateObject->using_kernel_templates_offset = offset;
  offset += sizeof(int);

  status = cuFuncSetBlockShape(stateObject->function, thread_count_x, thread_count_y, thread_count_z);
  CHECK_STATUS(env, "Error in cuFuncSetBlockShape", status, stateObject->device);

  stateObject->context_built = 1;
}

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_CUDAContext_cudaRun
  (JNIEnv *env, jobject this_ref, jlong nativeContext, jobject object_mem,
   jint using_kernel_templates, jobject stats_row)
{
  CUdeviceptr deviceGlobalFreePointer;
  CUdeviceptr deviceMLocal;
  size_t bytes;
  CUresult status;
  jlong heap_end_long;
  jint heap_end_int;
  jthrowable exc;
  unsigned long long hostMLocal[3];
  struct ContextState * stateObject = (struct ContextState *) nativeContext;

  stopwatchStart(&(stateObject->execMemcopyToDevice));

  heap_end_long = (*env)->CallLongMethod(env, object_mem, get_heap_end_method);
  heap_end_long >>= 4;
  heap_end_int = (jint) heap_end_long;
  stateObject->info_space[0] = heap_end_int;

  status = cuModuleGetGlobal(&deviceGlobalFreePointer, &bytes, stateObject->module, "global_free_pointer");
  CHECK_STATUS(env, "Error in cuModuleGetGlobal: global_free_pointer", status, stateObject->device)

  status = cuModuleGetGlobal(&deviceMLocal, &bytes, stateObject->module, "m_Local");
  CHECK_STATUS(env, "Error in cuModuleGetGlobal: m_Local", status, stateObject->device)

  //----------------------------------------------------------------------------
  //copy data
  //----------------------------------------------------------------------------
  status = cuMemcpyHtoD(deviceGlobalFreePointer, stateObject->info_space, 4);
  CHECK_STATUS(env, "Error in cuMemcpyHtoD: deviceGlobalFreePointer", status, stateObject->device)

  hostMLocal[0] = stateObject->gpu_object_mem;
  hostMLocal[1] = (stateObject->cpu_object_mem_size >> 4);
  hostMLocal[2] = stateObject->gpu_class_mem;

  status = cuMemcpyHtoD(deviceMLocal, hostMLocal, sizeof(hostMLocal));
  CHECK_STATUS(env, "Error in cuMemcpyHtoD: deviceMLocal", status, stateObject->device)

  status = cuMemcpyHtoD(stateObject->gpu_object_mem, stateObject->cpu_object_mem, stateObject->cpu_object_mem_size);
  CHECK_STATUS(env, "Error in cuMemcpyHtoD: gpu_object_mem", status, stateObject->device)

  status = cuMemcpyHtoD(stateObject->gpu_handles_mem, stateObject->cpu_handles_mem, stateObject->cpu_handles_mem_size);
  CHECK_STATUS(env, "Error in cuMemcpyHtoD: gpu_handles_mem", status, stateObject->device)

  status = cuMemcpyHtoD(stateObject->gpu_class_mem, stateObject->cpu_class_mem, stateObject->cpu_class_mem_size);
  CHECK_STATUS(env, "Error in cuMemcpyHtoD: gpu_class_mem", status, stateObject->device)

  status = cuMemcpyHtoD(stateObject->gpu_heap_end, &(heap_end_int), sizeof(jint));
  CHECK_STATUS(env, "Error in cuMemcpyHtoD: gpu_heap_end", status, stateObject->device)

  if(stateObject->using_exceptions){
    status = cuMemcpyHtoD(stateObject->gpu_exceptions_mem, stateObject->cpu_exceptions_mem, stateObject->cpu_exceptions_mem_size);
    CHECK_STATUS(env, "Error in cuMemcpyDtoH: gpu_exceptions_mem", status, stateObject->device)
  }

  stopwatchStop(&(stateObject->execMemcopyToDevice));

  //----------------------------------------------------------------------------
  //launch
  //----------------------------------------------------------------------------

  stopwatchStart(&(stateObject->execGpuRun));

  status = cuParamSeti(stateObject->function, stateObject->using_kernel_templates_offset, using_kernel_templates);
  CHECK_STATUS(env, "Error in cuParamSeti: using_kernel_templates", status, stateObject->device)

  status = cuLaunchGrid(stateObject->function, stateObject->block_count_x,
    stateObject->block_count_y);
  CHECK_STATUS(env, "Error in cuLaunchGrid", status, stateObject->device)

  status = cuCtxSynchronize();
  CHECK_STATUS(env, "Error in cuCtxSynchronize", status, stateObject->device)

  stopwatchStop(&(stateObject->execGpuRun));

  //----------------------------------------------------------------------------
  //copy data back
  //----------------------------------------------------------------------------

  stopwatchStart(&(stateObject->execMemcopyFromDevice));

  status = cuMemcpyDtoH(stateObject->info_space, deviceGlobalFreePointer, 4);
  CHECK_STATUS(env, "Error in cuMemcpyDtoH: deviceGlobalFreePointer", status, stateObject->device)

  heap_end_long = stateObject->info_space[0];
  heap_end_long <<= 4;

  status = cuMemcpyDtoH(stateObject->cpu_object_mem, stateObject->gpu_object_mem, heap_end_long);
  CHECK_STATUS(env, "Error in cuMemcpyDtoH: gpu_object_mem", status, stateObject->device)

  if(stateObject->using_exceptions){
    status = cuMemcpyDtoH(stateObject->cpu_exceptions_mem, stateObject->gpu_exceptions_mem, stateObject->cpu_exceptions_mem_size);
    CHECK_STATUS(env, "Error in cuMemcpyDtoH: gpu_exceptions_mem", status, stateObject->device)
  }

  (*env)->CallVoidMethod(env, object_mem, set_heap_end_method, heap_end_long);

  stopwatchStop(&(stateObject->execMemcopyFromDevice));

  (*env)->CallVoidMethod(env, stats_row, set_driver_times,
    stopwatchTimeMS(&(stateObject->execMemcopyToDevice)),
    stopwatchTimeMS(&(stateObject->execGpuRun)),
    stopwatchTimeMS(&(stateObject->execMemcopyFromDevice)));
}
