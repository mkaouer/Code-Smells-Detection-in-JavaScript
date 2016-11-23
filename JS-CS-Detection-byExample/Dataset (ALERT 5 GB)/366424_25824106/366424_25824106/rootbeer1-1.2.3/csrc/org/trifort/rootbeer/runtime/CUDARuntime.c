#include "CUDARuntime.h"
#include <cuda.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

JNIEXPORT jobject JNICALL Java_org_trifort_rootbeer_runtime_CUDARuntime_loadGpuDevices
  (JNIEnv * env, jobject this_ref)
{
  int i;
  int status;
  int num_devices;
  CUdevice device;
  CUcontext context;
  
  jclass array_list_class;
  jmethodID array_list_init;
  jmethodID array_list_add;
  jobject ret;

  jclass gpu_device_class;
  jmethodID gpu_device_init;
  jobject gpu_device;

  int major_version;
  int minor_version;
  char device_name[4096];
  size_t free_mem;
  size_t total_mem;
  int registers_per_block;
  int warp_size;
  int pitch;
  int threads_per_block;
  int shared_mem_per_block;
  int clock_rate;
  int mem_clock_rate;
  int const_mem;
  int integrated;
  int threads_per_multiprocessor;
  int multiprocessor_count;
  int max_block_dim_x;
  int max_block_dim_y;
  int max_block_dim_z;
  int max_grid_dim_x;
  int max_grid_dim_y;
  int max_grid_dim_z;

  array_list_class = (*env)->FindClass(env, "java/util/ArrayList");
  array_list_init = (*env)->GetMethodID(env, array_list_class, "<init>", "()V");
  array_list_add = (*env)->GetMethodID(env, array_list_class, "add", "(Ljava/lang/Object;)Z");

  ret = (*env)->NewObject(env, array_list_class, array_list_init);

  gpu_device_class = (*env)->FindClass(env, "org/trifort/rootbeer/runtime/GpuDevice");
  gpu_device_init = (*env)->GetStaticMethodID(env, gpu_device_class, "newCudaDevice", 
    "(IIILjava/lang/String;JJIIIIIIIIZIIIIIIII)Lorg/trifort/rootbeer/runtime/GpuDevice;");

  status = cuInit(0);
  if(status != CUDA_SUCCESS){
    return ret;
  }
  
  cuDeviceGetCount(&num_devices);

  for(i = 0; i < num_devices; ++i){
    status = cuDeviceGet(&device, i);
    if(status != CUDA_SUCCESS){
      continue;
    }

    cuDeviceComputeCapability(&major_version, &minor_version, device);
    cuDeviceGetName(device_name, 4096, device);
    cuCtxCreate(&context, CU_CTX_MAP_HOST, device);
    cuMemGetInfo(&free_mem, &total_mem);
    cuCtxDestroy(context);
    cuDeviceGetAttribute(&registers_per_block, CU_DEVICE_ATTRIBUTE_MAX_REGISTERS_PER_BLOCK, device);
    cuDeviceGetAttribute(&warp_size, CU_DEVICE_ATTRIBUTE_WARP_SIZE, device);
    cuDeviceGetAttribute(&pitch, CU_DEVICE_ATTRIBUTE_MAX_PITCH, device);
    cuDeviceGetAttribute(&threads_per_block, CU_DEVICE_ATTRIBUTE_MAX_THREADS_PER_BLOCK, device);
    cuDeviceGetAttribute(&shared_mem_per_block, CU_DEVICE_ATTRIBUTE_MAX_SHARED_MEMORY_PER_BLOCK, device);
    cuDeviceGetAttribute(&clock_rate, CU_DEVICE_ATTRIBUTE_CLOCK_RATE, device);
    cuDeviceGetAttribute(&mem_clock_rate, CU_DEVICE_ATTRIBUTE_MEMORY_CLOCK_RATE, device);
    cuDeviceGetAttribute(&const_mem, CU_DEVICE_ATTRIBUTE_TOTAL_CONSTANT_MEMORY, device);
    cuDeviceGetAttribute(&integrated, CU_DEVICE_ATTRIBUTE_INTEGRATED, device);
    cuDeviceGetAttribute(&threads_per_multiprocessor, CU_DEVICE_ATTRIBUTE_MAX_THREADS_PER_MULTIPROCESSOR, device);
    cuDeviceGetAttribute(&multiprocessor_count, CU_DEVICE_ATTRIBUTE_MULTIPROCESSOR_COUNT, device);
    cuDeviceGetAttribute(&max_block_dim_x, CU_DEVICE_ATTRIBUTE_MAX_BLOCK_DIM_X, device);
    cuDeviceGetAttribute(&max_block_dim_y, CU_DEVICE_ATTRIBUTE_MAX_BLOCK_DIM_Y, device);
    cuDeviceGetAttribute(&max_block_dim_z, CU_DEVICE_ATTRIBUTE_MAX_BLOCK_DIM_Z, device);
    cuDeviceGetAttribute(&max_grid_dim_x, CU_DEVICE_ATTRIBUTE_MAX_GRID_DIM_X, device);
    cuDeviceGetAttribute(&max_grid_dim_y, CU_DEVICE_ATTRIBUTE_MAX_GRID_DIM_Y, device);
    cuDeviceGetAttribute(&max_grid_dim_z, CU_DEVICE_ATTRIBUTE_MAX_GRID_DIM_Z, device);

    gpu_device = (*env)->CallObjectMethod(env, gpu_device_class, gpu_device_init,
      i, major_version, minor_version, (*env)->NewStringUTF(env, device_name), 
      (jlong) free_mem, (jlong) total_mem, registers_per_block, warp_size, pitch, threads_per_block, 
      shared_mem_per_block, clock_rate, mem_clock_rate, const_mem, integrated, 
      threads_per_multiprocessor, multiprocessor_count, max_block_dim_x, 
      max_block_dim_y, max_block_dim_z, max_grid_dim_x, max_grid_dim_y, 
      max_grid_dim_z);
    (*env)->CallBooleanMethod(env, ret, array_list_add, gpu_device);
  }

  return ret;
}
