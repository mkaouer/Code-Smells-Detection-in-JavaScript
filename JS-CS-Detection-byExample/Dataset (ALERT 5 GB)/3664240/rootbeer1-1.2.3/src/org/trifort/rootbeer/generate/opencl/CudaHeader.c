#ifndef NAN
#include <math_constants.h>
#define NAN CUDART_NAN
#endif

#ifndef INFINITY
#include <math_constants.h>
#define INFINITY CUDART_INF
#endif

#include <stdio.h>

__constant__ size_t m_Local[3];
__shared__ char m_shared[%%shared_mem_size%%];

__device__
int getThreadId(){
  int blockSize = blockDim.x * blockDim.y * blockDim.z;
  
  int ret = (blockIdx.x * gridDim.y * blockSize) +
            (blockIdx.y * blockSize) +
            (threadIdx.x * blockDim.y * blockDim.z) +
            (threadIdx.y * blockDim.z) +
            (threadIdx.z);
  return ret;
}

__device__
int getThreadIdxx(){
  return threadIdx.x;
}

__device__
int getThreadIdxy(){
  return threadIdx.y;
}

__device__
int getThreadIdxz(){
  return threadIdx.z;
}

__device__
int getBlockIdxx(){
  return blockIdx.x;
}

__device__
int getBlockIdxy(){
  return blockIdx.y;
}

__device__
int getBlockDimx(){
  return blockDim.x;
}

__device__
int getBlockDimy(){
  return blockDim.y;
}

__device__
int getBlockDimz(){
  return blockDim.z;
}

__device__
long long getGridDimx(){
  return gridDim.x;
}

__device__
long long getGridDimy(){
  return gridDim.y;
}

__device__
void org_trifort_syncthreads(){
  __syncthreads();
}

__device__
void org_trifort_threadfence(){
  __threadfence();
}

__device__
void org_trifort_threadfence_block(){
  __threadfence_block();
}

__device__
void org_trifort_threadfence_system(){
  __threadfence_system();
}

__device__ clock_t global_now;
