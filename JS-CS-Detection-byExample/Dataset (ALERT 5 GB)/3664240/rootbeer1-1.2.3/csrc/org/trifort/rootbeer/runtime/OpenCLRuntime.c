#include "CUDARuntime.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#ifdef __APPLE__
#include <OpenCL/cl.h>
#else
#include <CL/cl.h>
#endif

JNIEXPORT jobject JNICALL Java_org_trifort_rootbeer_runtime_OpenCLRuntime_loadGpuDevices
  (JNIEnv * env, jobject this_ref)
{
  int i;
  int j;
  cl_int status;
  
  jclass array_list_class;
  jmethodID array_list_init;
  jmethodID array_list_add;
  jobject ret;

  jclass gpu_device_class;
  jmethodID gpu_device_init;
  jobject gpu_device;

  cl_uint num_platforms;
  cl_platform_id platforms[10];
  cl_device_id device_ids[64];
  cl_uint num_devices;
  size_t param_size;

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
  gpu_device_init = (*env)->GetStaticMethodID(env, gpu_device_class, "newOpenCLDevice", 
    "(Ljava/lang/String;)Lorg/trifort/rootbeer/runtime/GpuDevice;");

  status = clGetPlatformIDs(10, platforms, &num_platforms);
  if(status != CL_SUCCESS){
    return ret;
  }

  for(i = 0; i < num_platforms; ++i){
    clGetDeviceIDs(platforms[i], CL_DEVICE_TYPE_ALL, 64, device_ids, &num_devices);

    for(j = 0; j < num_devices; ++j){
      clGetDeviceInfo(device_ids[j], CL_DEVICE_NAME, 4096, device_name, &param_size);
      gpu_device = (*env)->CallObjectMethod(env, gpu_device_class, gpu_device_init,
        (*env)->NewStringUTF(env, device_name));
      (*env)->CallBooleanMethod(env, ret, array_list_add, gpu_device);
    }
  }

  return ret;
}
