
DWORD run(void * data)
{
  int thread_idxx;
  int block_idxx;
  long long lhandle;
  int exception;
  int handle;    
  int index;

  lock_thread_id();
  thread_idxx = global_thread_id;
    
  ++global_thread_id;
  unlock_thread_id();
  
  block_idxx = global_block_idxx;

  TlsSetValue(blockIdxxKey, (void *) block_idxx);
  TlsSetValue(blockDimxKey, (void *) global_block_dimx);
  TlsSetValue(gridDimxKey, (void *) global_grid_dimx);
  TlsSetValue(threadIdxxKey, (void *) thread_idxx);

  index = block_idxx * global_block_dimx + thread_idxx;
  lhandle = global_handles[index];
  lhandle = lhandle >> 4;
  handle = (int) lhandle;
  exception = 0;
  %%invoke_run%%(global_gc_info, handle, &exception);
  global_exceptions[index] = exception;
  
  barrier_mutex_lock();
  global_thread_count--;
  barrier_mutex_unlock();
  return 0;
}

void entry(char * gc_info_space,
           long long * to_space,
           long long * handles,
           long long * to_space_free_ptr,
           long long * exceptions,
           int * java_lang_class_refs,
           long long space_size,
           int num_threads,
           int grid_dimx,
           int block_dimx){
  int i;
  int rc;
  int num_cores;
  char * gc_info;
  HANDLE * threads;
  int block_i;
  int thread_start;
  int thread_stop;
  int thread_count;

  gc_info = org_trifort_gc_init(gc_info_space, to_space,
    *to_space_free_ptr, space_size);

  global_grid_dimx = grid_dimx;
  global_block_dimx = block_dimx;
  global_gc_info = gc_info;
  global_handles = handles;
  global_exceptions = exceptions;
  global_class_refs = java_lang_class_refs;

  InitializeCriticalSection(&atom_add_mutex);
  blockIdxxKey = TlsAlloc();
  blockDimxKey = TlsAlloc();
  gridDimxKey = TlsAlloc();
  threadIdxxKey = TlsAlloc();
  InitializeCriticalSection(&thread_id_mutex);
  InitializeCriticalSection(&barrier_mutex);
  InitializeCriticalSection(&thread_gate_mutex);

  for(block_i = 0; block_i < grid_dimx; ++block_i){
    thread_start = block_i * block_dimx;
    thread_stop = (block_i + 1) * block_dimx;
    if(thread_stop > num_threads){
      thread_stop = num_threads;
    }
    thread_count = thread_stop - thread_start;

    threads = (HANDLE *) malloc(sizeof(HANDLE) * thread_count);

    global_num_cores = 4;
    global_thread_count = thread_count;
    global_block_idxx = block_i;
    global_thread_id = 0;
    global_barrier_count1 = 0;
    global_barrier_count2 = 0;
    global_barrier_count3 = 0;
    global_thread_gate_count = global_num_cores;

    for(i = 0; i < thread_count; ++i){
      threads[i] = CreateThread(NULL, 0, &run, NULL, 0, NULL);
    }

    for(i = 0; i < thread_count; ++i){
      WaitForSingleObject(threads[i], INFINITE);
    }

    free(threads);
  }
  fflush(stdout);
}
