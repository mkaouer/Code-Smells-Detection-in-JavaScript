#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <unistd.h>

#include <sys/time.h>
#include <pthread.h>

pthread_key_t threadIdxxKey = 0;
pthread_key_t blockIdxxKey = 0;
pthread_key_t blockDimxKey = 0;
pthread_key_t gridDimxKey = 0;
pthread_mutex_t atom_add_mutex;
pthread_mutex_t thread_id_mutex;
pthread_mutex_t barrier_mutex;
pthread_mutex_t thread_gate_mutex;

void lock_atom_add(){
  pthread_mutex_lock(&atom_add_mutex);
}

void unlock_atom_add(){
  pthread_mutex_unlock(&atom_add_mutex);
}

void lock_thread_id(){
  pthread_mutex_lock(&thread_id_mutex);
}

void unlock_thread_id(){
  pthread_mutex_unlock(&thread_id_mutex);
}

void barrier_mutex_lock(){
  pthread_mutex_lock(&barrier_mutex);
}

void barrier_mutex_unlock(){
  pthread_mutex_unlock(&barrier_mutex);
}

void thread_gate_mutex_lock(){
  pthread_mutex_lock(&thread_gate_mutex);
}

void thread_gate_mutex_unlock(){
  pthread_mutex_unlock(&thread_gate_mutex);
}
 
int getThreadId(){
  return getBlockIdxx() * getBlockDimx() + getThreadIdxx();
}

int getThreadIdxx(){
  return (int) pthread_getspecific(threadIdxxKey);
}

int getBlockIdxx(){
  return (int) pthread_getspecific(blockIdxxKey);
}

int getBlockDimx(){
  return (int) pthread_getspecific(blockDimxKey);
}

int getGridDimx(){
  return (int) pthread_getspecific(gridDimxKey);
}

long long java_lang_System_nanoTime(char * gc_info, int * exception){
  struct timeval tm;
  gettimeofday(&tm, 0);
  return tm.tv_sec * 1000000 + tm.tv_usec;
}

void org_trifort_sleep(int micro_seconds){
  usleep(micro_seconds);
}
