#ifndef ROOTBEER_STOPWATCH_H
#define ROOTBEER_STOPWATCH_H

#if (defined linux || defined __APPLE_CC__)
  #include <sys/time.h>
#else
  #include <Windows.h>
#endif

struct stopwatch {
#if (defined linux || defined __APPLE_CC__)
  struct timeval startTime;
  long long time;
#else
  long long startTime;
  long long stopTime;
#endif
};

void stopwatchStart(struct stopwatch * watch);
void stopwatchStop(struct stopwatch * watch);
long long stopwatchTimeMS(struct stopwatch * watch);

#endif
