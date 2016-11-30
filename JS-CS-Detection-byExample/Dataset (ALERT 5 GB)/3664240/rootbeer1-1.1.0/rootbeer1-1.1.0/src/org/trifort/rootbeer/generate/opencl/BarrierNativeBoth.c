
void edu_syr_pcpratts_barrier(){
  volatile int count;

  barrier_mutex_lock();
  global_barrier_count2 = 0;
  global_barrier_count1++;
  barrier_mutex_unlock();

  while(1){
    barrier_mutex_lock();
    count = global_barrier_count1;

    if(count >= global_thread_count){
      barrier_mutex_unlock();
      break;
    } else {
      barrier_mutex_unlock();
      edu_syr_pcpratts_sleep(100);
    }
  }

  barrier_mutex_lock();
  global_barrier_count3 = 0;
  global_barrier_count2++;
  barrier_mutex_unlock();

  while(1){
    barrier_mutex_lock();
    count = global_barrier_count2;

    if(count >= global_thread_count){
      barrier_mutex_unlock();
      break;
    } else {
      barrier_mutex_unlock();
      edu_syr_pcpratts_sleep(100);
    }
  }

  barrier_mutex_lock();
  global_barrier_count1 = 0;
  global_barrier_count3++;
  barrier_mutex_unlock();

  while(1){
    barrier_mutex_lock();
    count = global_barrier_count3;
    if(count >= global_thread_count){
      barrier_mutex_unlock();
      break;
    } else {
      barrier_mutex_unlock();
      edu_syr_pcpratts_sleep(100);
    }
  }
}
