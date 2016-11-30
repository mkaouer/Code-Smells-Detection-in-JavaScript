#include "CUDARuntime.h"
#include <cuda.h>

#define CHECK_STATUS(env,msg,status,device) \
if (CUDA_SUCCESS != status, device) {\
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
      cuDeviceGetName(name,1024,device);
      cuDeviceComputeCapability(&a, &b, device);
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

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_CUDAContext_cudaRun
  (JNIEnv *env, jobject this_ref, jint device_index, jbyteArray cubin_file, 
   jint cubin_length, jint block_shape_x, jint grid_shape_x, jint num_threads, 
   jobject object_mem, jobject handles_mem, jobject exceptions_mem, 
   jobject class_mem)
{
  CUresult status;
  CUdevice device;
  CUcontext context;
  CUmodule module;
  CUfunction function;
  void * fatcubin;
  int offset;
  int info_space_size;

  CUdeviceptr gpu_info_space;
  CUdeviceptr gpu_object_mem;
  CUdeviceptr gpu_handles_mem;
  CUdeviceptr gpu_exceptions_mem;
  CUdeviceptr gpu_class_mem;
  CUdeviceptr gpu_heap_end;
  CUdeviceptr gpu_buffer_size;

  void * cpu_object_mem;
  void * cpu_handles_mem;
  void * cpu_exceptions_mem;
  void * cpu_class_mem;
  jlong cpu_object_mem_size;
  jlong cpu_handles_mem_size;
  jlong cpu_exceptions_mem_size;
  jlong cpu_class_mem_size;
  jlong cpu_heap_end;

  jclass cuda_memory_class;
  jmethodID get_address_method;
  jmethodID get_size_method;
  jmethodID get_heap_end_method;
  
  jlong * info_space;

  //----------------------------------------------------------------------------
  //init device and function
  //----------------------------------------------------------------------------

  status = cuDeviceGet(&device, device_index);
  CHECK_STATUS(env, "Error in cuDeviceGet", status, device)

  status = cuCtxCreate(&context, CU_CTX_MAP_HOST, device);  
  CHECK_STATUS(env,"Error in cuCtxCreate", status, device)

  fatcubin = malloc(cubin_length);
  (*env)->GetByteArrayRegion(env, cubin_file, 0, cubin_length, fatcubin);

  status = cuModuleLoadFatBinary(&module, fatcubin);
  CHECK_STATUS(env, "Error in cuModuleLoad", status, device)
  free(fatcubin);

  status = cuModuleGetFunction(&function, module, "_Z5entryPcS_PiPxS1_S0_S0_i"); 
  CHECK_STATUS(env, "Error in cuModuleGetFunction", status, device)

  //----------------------------------------------------------------------------
  //get handles from java
  //----------------------------------------------------------------------------

  cuda_memory_class = (*env)->FindClass(env, "org/trifort/rootbeer/runtime/FixedMemory");
  get_address_method = (*env)->GetMethodID(env, cuda_memory_class, "getAddress", "()J");
  get_size_method = (*env)->GetMethodID(env, cuda_memory_class, "getSize", "()J");
  get_heap_end_method = (*env)->GetMethodID(env, cuda_memory_class, "getHeapEndPtr", "()J");

  cpu_object_mem = (void *) (*env)->CallLongMethod(env, object_mem, get_address_method);
  cpu_object_mem_size = (*env)->CallLongMethod(env, object_mem, get_size_method);
  cpu_heap_end = (*env)->CallLongMethod(env, object_mem, get_heap_end_method);

  cpu_handles_mem = (void *) (*env)->CallLongMethod(env, handles_mem, get_address_method);
  cpu_handles_mem_size = (*env)->CallLongMethod(env, handles_mem, get_heap_end_method);

  cpu_exceptions_mem = (void *) (*env)->CallLongMethod(env, exceptions_mem, get_address_method);
  cpu_exceptions_mem_size = (*env)->CallLongMethod(env, exceptions_mem, get_size_method);

  cpu_class_mem = (void *) (*env)->CallLongMethod(env, class_mem, get_address_method);
  cpu_class_mem_size = (*env)->CallLongMethod(env, class_mem, get_heap_end_method);

  info_space_size = 1024;
  info_space = (jlong *) malloc(info_space_size);
  info_space[1] = (*env)->CallLongMethod(env, object_mem, get_heap_end_method);

  //----------------------------------------------------------------------------
  //allocate mem
  //----------------------------------------------------------------------------

  status = cuMemAlloc(&gpu_info_space, info_space_size);  
  CHECK_STATUS(env, "Error in cuMemAlloc: gpu_info_mem", status, device)

  status = cuMemAlloc(&gpu_object_mem, cpu_object_mem_size);  
  CHECK_STATUS(env, "Error in cuMemAlloc: gpu_object_mem", status, device)

  status = cuMemAlloc(&gpu_handles_mem, cpu_handles_mem_size); 
  CHECK_STATUS(env, "Error in cuMemAlloc: gpu_handles_mem", status, device)
    
  status = cuMemAlloc(&gpu_exceptions_mem, cpu_exceptions_mem_size); 
  CHECK_STATUS(env, "Error in cuMemAlloc: gpu_exceptions_mem", status, device)

  status = cuMemAlloc(&gpu_class_mem, cpu_class_mem_size);
  CHECK_STATUS(env, "Error in cuMemAlloc: gpu_class_mem", status, device)

  status = cuMemAlloc(&gpu_heap_end, 8);
  CHECK_STATUS(env, "Error in cuMemAlloc: gpu_heap_end", status, device)

  status = cuMemAlloc(&gpu_buffer_size, 8);
  CHECK_STATUS(env, "Error in cuMemAlloc: gpu_buffer_size", status, device)

  //----------------------------------------------------------------------------
  //set function parameters
  //----------------------------------------------------------------------------

  status = cuParamSetSize(function, (7 * sizeof(CUdeviceptr) + sizeof(int))); 
  CHECK_STATUS(env, "Error in cuParamSetSize", status, device)

  offset = 0;
  status = cuParamSetv(function, offset, (void *) &gpu_info_space, sizeof(CUdeviceptr)); 
  CHECK_STATUS(env, "Error in cuParamSetv gpu_info_space", status, device)
  offset += sizeof(CUdeviceptr);

  status = cuParamSetv(function, offset, (void *) &gpu_object_mem, sizeof(CUdeviceptr)); 
  CHECK_STATUS(env, "Error in cuParamSetv: gpu_object_mem", status, device)
  offset += sizeof(CUdeviceptr);

  status = cuParamSetv(function, offset, (void *) &gpu_handles_mem, sizeof(CUdeviceptr)); 
  CHECK_STATUS(env, "Error in cuParamSetv: gpu_handles_mem %", status, device)
  offset += sizeof(CUdeviceptr);

  status = cuParamSetv(function, offset, (void *) &gpu_heap_end, sizeof(CUdeviceptr)); 
  CHECK_STATUS(env, "Error in cuParamSetv: gpu_heap_end", status, device)
  offset += sizeof(CUdeviceptr);

  status = cuParamSetv(function, offset, (void *) &gpu_buffer_size, sizeof(CUdeviceptr));
  CHECK_STATUS(env, "Error in cuParamSetv: gpu_buffer_size", status, device)
  offset += sizeof(CUdeviceptr); 

  status = cuParamSetv(function, offset, (void *) &gpu_exceptions_mem, sizeof(CUdeviceptr)); 
  CHECK_STATUS(env, "Error in cuParamSetv: gpu_exceptions_mem", status, device)
  offset += sizeof(CUdeviceptr);

  status = cuParamSetv(function, offset, (void *) &gpu_class_mem, sizeof(CUdeviceptr)); 
  CHECK_STATUS(env, "Error in cuParamSetv: gpu_class_mem", status, device)
  offset += sizeof(CUdeviceptr);

  status = cuParamSeti(function, offset, num_threads); 
  CHECK_STATUS(env, "Error in cuParamSetv: num_threads", status, device)
  offset += sizeof(int);

  //----------------------------------------------------------------------------
  //copy data
  //----------------------------------------------------------------------------

  status = cuMemcpyHtoD(gpu_info_space, info_space, info_space_size);
  CHECK_STATUS(env, "Error in cuMemcpyHtoD: info_space", status, device)

  status = cuMemcpyHtoD(gpu_object_mem, cpu_object_mem, cpu_object_mem_size);
  CHECK_STATUS(env, "Error in cuMemcpyHtoD: gpu_object_mem", status, device)

  status = cuMemcpyHtoD(gpu_handles_mem, cpu_handles_mem, cpu_handles_mem_size);
  CHECK_STATUS(env, "Error in cuMemcpyHtoD: gpu_handles_mem", status, device)

  status = cuMemcpyHtoD(gpu_class_mem, cpu_class_mem, cpu_class_mem_size);
  CHECK_STATUS(env, "Error in cuMemcpyHtoD: gpu_class_mem", status, device)

  status = cuMemcpyHtoD(gpu_heap_end, &cpu_heap_end, sizeof(jlong));
  CHECK_STATUS(env, "Error in cuMemcpyHtoD: gpu_heap_end", status, device)

  status = cuMemcpyHtoD(gpu_buffer_size, &cpu_object_mem_size, sizeof(jlong));
  CHECK_STATUS(env, "Error in cuMemcpyHtoD: gpu_buffer_size", status, device)

  //----------------------------------------------------------------------------
  //launch
  //----------------------------------------------------------------------------

  status = cuFuncSetBlockShape(function, block_shape_x, 1, 1);
  CHECK_STATUS(env, "Error in cuFuncSetBlockShape", status, device);

  status = cuLaunchGrid(function, grid_shape_x, 1);
  CHECK_STATUS(env, "Error in cuLaunchGrid", status, device)

  status = cuCtxSynchronize();  
  CHECK_STATUS(env, "Error in cuCtxSynchronize", status, device)

  //----------------------------------------------------------------------------
  //copy data back
  //----------------------------------------------------------------------------

  status = cuMemcpyDtoH(info_space, gpu_info_space, info_space_size);
  CHECK_STATUS(env, "Error in cuMemcpyDtoH: gpu_info_space", status, device)

  cpu_heap_end = info_space[1];

  status = cuMemcpyDtoH(cpu_object_mem, gpu_object_mem, cpu_heap_end);
  CHECK_STATUS(env, "Error in cuMemcpyDtoH: gpu_object_mem", status, device)

  status = cuMemcpyDtoH(cpu_exceptions_mem, gpu_exceptions_mem, cpu_exceptions_mem_size);
  CHECK_STATUS(env, "Error in cuMemcpyDtoH: gpu_object_mem", status, device)

  //----------------------------------------------------------------------------
  //free resources
  //----------------------------------------------------------------------------
  
  free(info_space);

  cuMemFree(gpu_info_space);
  cuMemFree(gpu_object_mem);
  cuMemFree(gpu_handles_mem);
  cuMemFree(gpu_exceptions_mem);
  cuMemFree(gpu_class_mem);
  cuMemFree(gpu_heap_end);
  cuMemFree(gpu_buffer_size);

  cuCtxDestroy(context);
}
