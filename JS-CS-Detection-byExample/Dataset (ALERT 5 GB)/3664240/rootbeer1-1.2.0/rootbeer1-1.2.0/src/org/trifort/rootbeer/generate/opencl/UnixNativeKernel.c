
static void * run(void * data){
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
    
  pthread_setspecific(blockIdxxKey, (void *) block_idxx);
  pthread_setspecific(blockDimxKey, (void *) global_block_dimx);
  pthread_setspecific(gridDimxKey, (void *) global_grid_dimx);
  pthread_setspecific(threadIdxxKey, (void *) thread_idxx);

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

  return NULL;
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
  pthread_t ** threads;
  pthread_t * thread;
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

  pthread_mutex_init(&atom_add_mutex, NULL);
  pthread_key_create(&blockIdxxKey, NULL);
  pthread_key_create(&blockDimxKey, NULL);
  pthread_key_create(&gridDimxKey, NULL);
  pthread_key_create(&threadIdxxKey, NULL);
  pthread_mutex_init(&thread_id_mutex, NULL);
  pthread_mutex_init(&barrier_mutex, NULL);
  pthread_mutex_init(&thread_gate_mutex, NULL);
  
  for(block_i = 0; block_i < grid_dimx; ++block_i){
    thread_start = block_i * block_dimx;
    thread_stop = (block_i + 1) * block_dimx;
    if(thread_stop > num_threads){
      thread_stop = num_threads;
    }
    thread_count = thread_stop - thread_start;

    threads = (pthread_t **) malloc(sizeof(pthread_t *) * thread_count);
    
    global_num_cores = 4;
    global_thread_count = thread_count;
    global_block_idxx = block_i;
    global_thread_id = 0;
    global_barrier_count1 = 0;
    global_barrier_count2 = 0;
    global_barrier_count3 = 0;
    global_thread_gate_count = global_num_cores;

    for(i = 0; i < thread_count; ++i){
      thread = (pthread_t *) malloc(sizeof(pthread_t));
      pthread_create(thread, NULL, &run, NULL);
      threads[i] = thread;
    }
  
    for(i = 0; i < thread_count; ++i){
      thread = threads[i];
      pthread_join(*thread, NULL);
    } 

    free(threads);
  }

  fflush(stdout);
}
