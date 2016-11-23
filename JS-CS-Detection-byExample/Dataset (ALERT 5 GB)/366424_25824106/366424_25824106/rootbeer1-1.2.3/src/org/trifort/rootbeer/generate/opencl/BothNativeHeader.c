
#define NAN 0x7ff8000000000000L
#define INFINITY 0x7ff0000000000000L

long long atom_add(long long * addr, long long value){
  long long ret;
  lock_atom_add();
  ret = *addr;
  *addr += value;
  unlock_atom_add();
  return ret;
}

unsigned long long atomicAdd(unsigned long long * addr, long long value){
  long long ret;
  lock_atom_add();
  ret = *addr;
  *addr += value;
  unlock_atom_add();
  return ret;
}

int atomicCAS(int * addr, int compare, int set){
  int ret;
  lock_atom_add();
  ret = *addr;
  if(ret == compare)
    *addr = set;
  unlock_atom_add();
  return ret;
}

int atomicExch(int * addr, int value){
  int ret;
  lock_atom_add();
  ret = *addr;
  *addr = value;
  unlock_atom_add();
  return ret;
}

int getThreadId();
int getThreadIdxx();
int getBlockIdxx();
int getBlockDimx();
int getGridDimx();

void edu_syr_pcpratts_syncthreads();
void edu_syr_pcpratts_threadfence();
void edu_syr_pcpratts_threadfence_block();

long long m_Local[3];
int * m_Cache;
long long m_shared[80*1024];

char * global_gc_info;
long long * global_handles;
int global_thread_count;
int global_num_cores;
int global_block_idxx;
int global_block_dimx;
int global_grid_dimx;
int global_thread_id;
int global_barrier_count1;
int global_barrier_count2;
int global_barrier_count3;
int global_thread_gate_count;
long long * global_exceptions;
int * global_class_refs;
