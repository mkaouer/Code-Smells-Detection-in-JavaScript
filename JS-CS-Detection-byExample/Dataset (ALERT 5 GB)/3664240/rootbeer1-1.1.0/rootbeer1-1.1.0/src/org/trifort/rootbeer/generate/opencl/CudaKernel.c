
__device__ int
org_trifort_classConstant(int type_num){
  int * temp = (int *) m_Local[2];   
  return temp[type_num];
}

__device__  char *
org_trifort_gc_deref(char * gc_info, int handle){

  char * data_arr = (char * ) m_Local[0];
  long long lhandle = handle;
  lhandle = lhandle << 4;
  return &data_arr[lhandle];
}

__device__ int
org_trifort_gc_malloc(char * gc_info, int size){
  unsigned long long space_size = m_Local[1];
  unsigned long long ret = org_trifort_gc_malloc_no_fail(gc_info, size);
  unsigned long long end = ret + size + 8L;
  if(end >= space_size){
    return -1;
  }
  return (int) (ret >> 4);
}

__device__ unsigned long long
org_trifort_gc_malloc_no_fail(char * gc_info, int size){
  unsigned long long * addr = (unsigned long long *) (gc_info + TO_SPACE_FREE_POINTER_OFFSET);
  if(size % 16 != 0){
    size += (16 - (size %16));
  }

  unsigned long long ret;
  ret = atomicAdd(addr, size);
  return ret;
}

__device__  void
org_trifort_gc_init(char * to_space, size_t space_size, int * java_lang_class_refs){
  if(threadIdx.x == 0){
    m_Local[0] = (size_t) to_space;
    m_Local[1] = (size_t) space_size;
    m_Local[2] = (size_t) java_lang_class_refs;
  }
}

__device__
long long java_lang_System_nanoTime(char * gc_info, int * exception){
  return (long long) clock64();
}

__global__ void entry(char * gc_info, char * to_space, int * handles, 
  long long * to_space_free_ptr, long long * space_size, int * exceptions,
  int * java_lang_class_refs, int num_blocks){

  org_trifort_gc_init(to_space, *space_size, java_lang_class_refs);
  __syncthreads();

  int loop_control = blockIdx.x * blockDim.x + threadIdx.x;
  if(loop_control >= num_blocks){
    return;
  } else {
    int handle = handles[loop_control];
    int exception = 0;   
    %%invoke_run%%(gc_info, handle, &exception);
    exceptions[loop_control] = exception;

    __syncthreads();

    if(loop_control == 0){
      unsigned long long * addr = ( unsigned long long * ) (gc_info + TO_SPACE_FREE_POINTER_OFFSET);
      *to_space_free_ptr = *addr;    
    }
  }
}
