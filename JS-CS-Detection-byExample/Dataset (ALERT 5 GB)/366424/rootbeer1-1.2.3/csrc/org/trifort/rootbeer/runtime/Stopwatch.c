#include "Stopwatch.h"

void stopwatchStart(struct stopwatch * watch){
#if (defined linux || defined __APPLE_CC__)
  gettimeofday(&(watch->startTime), 0);
#else
  QueryPerformanceCounter((LARGE_INTEGER*)&(watch->startTime));
#endif
}

void stopwatchStop(struct stopwatch * watch){
#if (defined linux || defined __APPLE_CC__)
  struct timeval endTime;
  long seconds, useconds;

  gettimeofday(&endTime, 0);

  seconds  = endTime.tv_sec  - (watch->startTime).tv_sec;
  useconds = endTime.tv_usec - (watch->startTime).tv_usec;

  watch->time = (seconds * 1000) + (useconds / 1000);
#else
  QueryPerformanceCounter((LARGE_INTEGER*)&(watch->stopTime));
#endif
}

long long stopwatchTimeMS(struct stopwatch * watch){
  #if (defined linux || defined __APPLE_CC__)
    return watch->time;
  #else
    long long freq;
    long long d;
    QueryPerformanceFrequency((LARGE_INTEGER*)&freq);
    d = watch->stopTime - watch->startTime;
    return (d * 1000UL) / freq;
  #endif
}
