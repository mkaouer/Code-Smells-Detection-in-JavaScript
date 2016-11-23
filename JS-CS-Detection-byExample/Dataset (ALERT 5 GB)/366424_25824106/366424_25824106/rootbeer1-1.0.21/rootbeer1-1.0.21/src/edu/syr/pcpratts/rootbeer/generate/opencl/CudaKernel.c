
__device__ int
edu_syr_pcpratts_classConstant(int type_num){
  int * temp = (int *) m_Local[2];   
  return temp[type_num];
}

__device__  char *
edu_syr_pcpratts_gc_deref(char * gc_info, int handle){

  char * data_arr = (char * ) m_Local[0];
  long long lhandle = handle;
  lhandle = lhandle << 4;
  return &data_arr[lhandle];
}

__device__ int
edu_syr_pcpratts_gc_malloc(char * gc_info, long long size){
  size_t space_size = m_Local[1];
  long long ret = edu_syr_pcpratts_gc_malloc_no_fail(gc_info, size);
  size_t end = ret + size + 8L;
  if(end >= space_size){
    return -1;
  }
  ret = ret >> 4;
  return (int) ret;
}

__device__ long long
edu_syr_pcpratts_gc_malloc_no_fail(char * gc_info, long long size){
  unsigned long long * addr = (unsigned long long *) (gc_info + TO_SPACE_FREE_POINTER_OFFSET);
  size += 8;
  long long ret;
    
  ret = atomicAdd(addr, (unsigned long long) size);
  int mod = ret % 8;
  if(mod != 0)
    ret += mod;

  return ret;
}

__device__  void
edu_syr_pcpratts_gc_init(char * to_space, size_t space_size, int * java_lang_class_refs){
  m_Local[0] = (size_t) to_space;
  m_Local[1] = (size_t) space_size;
  m_Local[2] = (size_t) java_lang_class_refs;
}

__device__
long long java_lang_System_nanoTime(char * gc_info, int * exception){
  return (long long) clock64();
}

__global__ void entry(char * gc_info, char * to_space, int * handles, 
  long long * to_space_free_ptr, long long * space_size, int * exceptions,
  int * java_lang_class_refs, int num_blocks){

  unsigned long long * addr = ( unsigned long long * ) ( gc_info + TO_SPACE_FREE_POINTER_OFFSET );
  *addr = *to_space_free_ptr;
  
  edu_syr_pcpratts_gc_init(to_space, *space_size, java_lang_class_refs);
  __syncthreads();

  int loop_control = blockIdx.x * blockDim.x + threadIdx.x;
  if(loop_control >= num_blocks){  
    return;
  } else {
    int handle = handles[loop_control];
    int exception = 0;   
    %%invoke_run%%(gc_info, handle, &exception);
    exceptions[loop_control] = exception;
  }
}