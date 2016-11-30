#define GC_OBJ_TYPE_COUNT char
#define GC_OBJ_TYPE_COLOR char
#define GC_OBJ_TYPE_TYPE int
#define GC_OBJ_TYPE_CTOR_USED char
#define GC_OBJ_TYPE_SIZE int

#define COLOR_GREY 0
#define COLOR_BLACK 1
#define COLOR_WHITE 2

$$__device__$$ void org_trifort_gc_collect($$__global$$ char * gc_info);
$$__device__$$ void org_trifort_gc_assign($$__global$$ char * gc_info, int * lhs, int rhs);
$$__device__$$ $$__global$$ char * org_trifort_gc_deref($$__global$$ char * gc_info, int handle);
$$__device__$$ int org_trifort_gc_malloc($$__global$$ char * gc_info, int size);
$$__device__$$ unsigned long long org_trifort_gc_malloc_no_fail($$__global$$ char * gc_info, int size);
$$__device__$$ int org_trifort_classConstant(int type_num);
$$__device__$$ long long java_lang_System_nanoTime($$__global$$ char * gc_info, int * exception);

#define CACHE_SIZE_BYTES 32
#define CACHE_SIZE_INTS (CACHE_SIZE_BYTES / sizeof(int))
#define CACHE_ENTRY_SIZE 4

#define TO_SPACE_OFFSET               0
#define TO_SPACE_FREE_POINTER_OFFSET  8

$$__device__$$
void org_trifort_exitMonitorRef($$__global$$ char * gc_info, int thisref, int old){
  char * mem = org_trifort_gc_deref(gc_info, thisref); 
  mem += 16;
  if(old == -1){    
    org_trifort_threadfence();  
    atomicExch((int *) mem, -1); 
  }
}

$$__device__$$
void org_trifort_exitMonitorMem($$__global$$ char * gc_info, char * mem, int old){
  if(old == -1){   
    org_trifort_threadfence(); 
    atomicExch((int *) mem, -1);
  }
}

$$__device__$$ 
long long java_lang_Double_doubleToLongBits($$__global$$ char * gc_info, double value, int * exception){
  long long ret = *((long long *) ((double *) &value));
  return ret;
}

$$__device__$$ 
double java_lang_Double_longBitsToDouble($$__global$$ char * gc_info, long long value, int * exception){
  double ret = *((double *) ((long long *) &value));
  return ret;
}

$$__device__$$
int java_lang_Float_floatToIntBits($$__global$$ char * gc_info, float value, int * exception){
  int ret = *((int *) ((float *) &value));
  return ret;
}  

$$__device__$$
float java_lang_Float_intBitsToFloat($$__global$$ char * gc_info, int value, int * exception){
  float ret = *((float *) ((int *) &value));
  return ret;
}

$$__device__$$ double java_lang_StrictMath_exp( char * gc_info , double parameter0 , int * exception ) { 
  return exp(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_log( char * gc_info , double parameter0 , int * exception ) { 
  return log(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_log10( char * gc_info , double parameter0 , int * exception ) { 
  return log10(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_sqrt( char * gc_info , double parameter0 , int * exception ) { 
  return sqrt(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_cbrt( char * gc_info , double parameter0 , int * exception ) { 
  //2.2204460492503131e-16 is DBL_EPSILON
  if (fabs(parameter0) < 2.2204460492503131e-16){
    return 0.0;
  }

  if (parameter0 > 0.0) {
    return pow(parameter0, 1.0/3.0);
  }

  return -pow(-parameter0, 1.0/3.0);
} 

$$__device__$$ double java_lang_StrictMath_IEEEremainder( char * gc_info , double parameter0 , double parameter1, int * exception ) { 
  return remainder(parameter0, parameter1); 
} 

$$__device__$$ double java_lang_StrictMath_ceil( char * gc_info , double parameter0 , int * exception ) { 
  return ceil(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_floor( char * gc_info , double parameter0 , int * exception ) { 
  return floor(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_sin( char * gc_info , double parameter0 , int * exception ) { 
  return sin(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_cos( char * gc_info , double parameter0 , int * exception ) { 
  return cos(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_tan( char * gc_info , double parameter0 , int * exception ) { 
  return tan(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_asin( char * gc_info , double parameter0 , int * exception ) { 
  return asin(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_acos( char * gc_info , double parameter0 , int * exception ) { 
  return acos(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_atan( char * gc_info , double parameter0 , int * exception ) { 
  return atan(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_atan2( char * gc_info , double parameter0 , double parameter1, int * exception ) { 
  return atan2(parameter0, parameter1); 
} 

$$__device__$$ double java_lang_StrictMath_pow( char * gc_info , double parameter0 , double parameter1, int * exception ) { 
  return pow(parameter0, parameter1); 
} 

$$__device__$$ double java_lang_StrictMath_sinh( char * gc_info , double parameter0 , int * exception ) { 
  return sinh(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_cosh( char * gc_info , double parameter0 , int * exception ) { 
  return cosh(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_tanh( char * gc_info , double parameter0 , int * exception ) { 
  return tanh(parameter0); 
} 

$$__device__$$ 
void org_trifort_rootbeer_runtime_GpuStopwatch_start($$__global$$ char * gc_info, int thisref, int * exception){
  long long int time;
  
  time = clock64();
  instance_setter_org_trifort_rootbeer_runtime_GpuStopwatch_m_start(gc_info, thisref, time, exception);
}

$$__device__$$ 
void org_trifort_rootbeer_runtime_GpuStopwatch_stop($$__global$$ char * gc_info, int thisref, int * exception){
  long long int time;
  
  time = clock64();
  instance_setter_org_trifort_rootbeer_runtime_GpuStopwatch_m_stop(gc_info, thisref, time, exception);
}

$$__device__$$ 
char org_trifort_rootbeer_runtime_RootbeerGpu_isOnGpu($$__global$$ char * gc_info, int * exception){
  return 1;
}

$$__device__$$ 
int org_trifort_rootbeer_runtime_RootbeerGpu_getThreadId($$__global$$ char * gc_info, int * exception){
  return getThreadId();
}

$$__device__$$ 
int org_trifort_rootbeer_runtime_RootbeerGpu_getThreadIdxx($$__global$$ char * gc_info, int * exception){
  return getThreadIdxx();
}

$$__device__$$ 
int org_trifort_rootbeer_runtime_RootbeerGpu_getBlockIdxx($$__global$$ char * gc_info, int * exception){
  return getBlockIdxx();
}

$$__device__$$ 
int org_trifort_rootbeer_runtime_RootbeerGpu_getBlockDimx($$__global$$ char * gc_info, int * exception){
  return getBlockDimx();
}

$$__device__$$ 
int org_trifort_rootbeer_runtime_RootbeerGpu_getGridDimx($$__global$$ char * gc_info, int * exception){
  return getGridDimx();
}


$$__device__$$ 
long long org_trifort_rootbeer_runtime_RootbeerGpu_getRef($$__global$$ char * gc_info, int ref, int * exception){
  return ref;
}

$$__device__$$
char org_trifort_rootbeer_runtime_RootbeerGpu_getSharedByte($$__global$$ char * gc_info, int index, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(gc_info, 
      index, 0, %%shared_mem_size%%, exception);
    return 0;
  }
#endif
  return m_shared[index]; 
}

$$__device__$$
void org_trifort_rootbeer_runtime_RootbeerGpu_setSharedByte($$__global$$ char * gc_info, int index, char value, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(gc_info, 
      index, 0, %%shared_mem_size%%, exception);
    return;
  }
#endif
  m_shared[index] = value;
}
  
$$__device__$$
char org_trifort_rootbeer_runtime_RootbeerGpu_getSharedChar($$__global$$ char * gc_info, int index, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(gc_info, 
      index, 0, %%shared_mem_size%%, exception);
    return 0;
  }
#endif
  return m_shared[index]; 
}

$$__device__$$
void org_trifort_rootbeer_runtime_RootbeerGpu_setSharedChar($$__global$$ char * gc_info, int index, char value, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(gc_info, 
      index, 0, %%shared_mem_size%%, exception);
    return;
  }
#endif
  m_shared[index] = value;
}
  
$$__device__$$
char org_trifort_rootbeer_runtime_RootbeerGpu_getSharedBoolean($$__global$$ char * gc_info, int index, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(gc_info, 
      index, 0, %%shared_mem_size%%, exception);
    return 0;
  }
#endif
  return m_shared[index]; 
}

$$__device__$$
void org_trifort_rootbeer_runtime_RootbeerGpu_setSharedBoolean($$__global$$ char * gc_info, int index, char value, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(gc_info, 
      index, 0, %%shared_mem_size%%, exception);
    return;
  }
#endif
  m_shared[index] = value;
}
  
$$__device__$$
short org_trifort_rootbeer_runtime_RootbeerGpu_getSharedShort($$__global$$ char * gc_info, int index, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index + 2 >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(gc_info, 
      index, 0, %%shared_mem_size%%, exception);
    return 0;
  }  
#endif
  short ret = 0;
  ret |= m_shared[index] & 0xff;
  ret |= (m_shared[index + 1] << 8) & 0xff00;
  return ret;
}

$$__device__$$
void org_trifort_rootbeer_runtime_RootbeerGpu_setSharedShort($$__global$$ char * gc_info, int index, short value, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index + 2 >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(gc_info, 
      index, 0, %%shared_mem_size%%, exception);
    return;
  }
#endif
  m_shared[index] = (char) (value & 0xff);
  m_shared[index + 1] = (char) ((value >> 8) & 0xff);
}

$$__device__$$
int org_trifort_rootbeer_runtime_RootbeerGpu_getSharedInteger($$__global$$ char * gc_info, int index, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index + 4 >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(gc_info, 
      index, 0, %%shared_mem_size%%, exception);
    return 0;
  }
#endif
  int ret = m_shared[index] & 0x000000ff;
  ret |= (m_shared[index + 1] << 8)  & 0x0000ff00;
  ret |= (m_shared[index + 2] << 16) & 0x00ff0000;
  ret |= (m_shared[index + 3] << 24) & 0xff000000; 
  return ret;
}

$$__device__$$
void org_trifort_rootbeer_runtime_RootbeerGpu_setSharedInteger($$__global$$ char * gc_info, int index, int value, int * exception){  
#ifdef ARRAY_CHECKS
  if(index < 0 || index + 4 >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(gc_info, 
      index, 0, %%shared_mem_size%%, exception);
    return;
  }
#endif
  m_shared[index] = (char) (value & 0xff);
  m_shared[index + 1] = (char) ((value >> 8)  & 0xff);
  m_shared[index + 2] = (char) ((value >> 16) & 0xff);
  m_shared[index + 3] = (char) ((value >> 24) & 0xff);
}

$$__device__$$
long long org_trifort_rootbeer_runtime_RootbeerGpu_getSharedLong($$__global$$ char * gc_info, int index, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index + 8 >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(gc_info, 
      index, 0, %%shared_mem_size%%, exception);
    return 0;
  }
#endif
  long long ret = 0;
  ret |=  ((long long) m_shared[index]) & 0x00000000000000ffL;
  ret |= ((long long) m_shared[index + 1] << 8)  & 0x000000000000ff00L;
  ret |= ((long long) m_shared[index + 2] << 16) & 0x0000000000ff0000L;
  ret |= ((long long) m_shared[index + 3] << 24) & 0x00000000ff000000L;
  ret |= ((long long) m_shared[index + 4] << 32) & 0x000000ff00000000L;
  ret |= ((long long) m_shared[index + 5] << 40) & 0x0000ff0000000000L;
  ret |= ((long long) m_shared[index + 6] << 48) & 0x00ff000000000000L;
  ret |= ((long long) m_shared[index + 7] << 56) & 0xff00000000000000L;
  return ret;
}

$$__device__$$
void org_trifort_rootbeer_runtime_RootbeerGpu_setSharedLong($$__global$$ char * gc_info, int index, long long value, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index + 8 >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(gc_info, 
      index, 0, %%shared_mem_size%%, exception);
    return;
  }
#endif
  m_shared[index] = (char) (value & 0x00000000000000ffL);
  m_shared[index + 1] = (char) ((value >> 8)  & 0x00000000000000ffL);
  m_shared[index + 2] = (char) ((value >> 16) & 0x00000000000000ffL);
  m_shared[index + 3] = (char) ((value >> 24) & 0x00000000000000ffL);
  m_shared[index + 4] = (char) ((value >> 32) & 0x00000000000000ffL);
  m_shared[index + 5] = (char) ((value >> 40) & 0x00000000000000ffL);
  m_shared[index + 6] = (char) ((value >> 48) & 0x00000000000000ffL);
  m_shared[index + 7] = (char) ((value >> 56) & 0x00000000000000ffL);
}
  
$$__device__$$
float org_trifort_rootbeer_runtime_RootbeerGpu_getSharedFloat($$__global$$ char * gc_info, int index, int * exception){
  int int_value = org_trifort_rootbeer_runtime_RootbeerGpu_getSharedInteger(gc_info, index, exception);
  return *((float *) &int_value);
}

$$__device__$$
void org_trifort_rootbeer_runtime_RootbeerGpu_setSharedFloat($$__global$$ char * gc_info, int index, float value, int * exception){
  int int_value = *((int *) &value);
  org_trifort_rootbeer_runtime_RootbeerGpu_setSharedInteger(gc_info, index, int_value, exception);
}
  
$$__device__$$
double org_trifort_rootbeer_runtime_RootbeerGpu_getSharedDouble($$__global$$ char * gc_info, int index, int * exception){
  long long long_value = org_trifort_rootbeer_runtime_RootbeerGpu_getSharedLong(gc_info, index, exception);
  return *((double *) &long_value);
}

$$__device__$$
void org_trifort_rootbeer_runtime_RootbeerGpu_setSharedDouble($$__global$$ char * gc_info, int index, double value, int * exception){
  long long long_value = *((long long *) &value);
  org_trifort_rootbeer_runtime_RootbeerGpu_setSharedLong(gc_info, index, long_value, exception);
}

$$__device__$$
int org_trifort_rootbeer_runtime_RootbeerGpu_getSharedObject($$__global$$ char * gc_info, int index, int * exception){
  return org_trifort_rootbeer_runtime_RootbeerGpu_getSharedInteger(gc_info, index, exception);
}

$$__device__$$
void org_trifort_rootbeer_runtime_RootbeerGpu_setSharedObject($$__global$$ char * gc_info, int index, int value, int * exception){
  org_trifort_rootbeer_runtime_RootbeerGpu_setSharedInteger(gc_info, index, value, exception);
}
  
$$__device__$$
void java_io_PrintStream_println0_9_($$__global$$ char * gc_info, int thisref, int str_ret, int * exception){
  int valueref;
  int count;
  int offset;
  int i;
  int curr_offset;

  char * valueref_deref;

  valueref = instance_getter_java_lang_String_value(gc_info, str_ret, exception);  
  if(*exception != 0){
    return; 
  } 
  count = instance_getter_java_lang_String_count(gc_info, str_ret, exception);
  if(*exception != 0){
    return; 
  } 
  offset = instance_getter_java_lang_String_offset(gc_info, str_ret, exception);
  if(*exception != 0){
    return; 
  } 
  valueref_deref = (char *) org_trifort_gc_deref(gc_info, valueref);
  for(i = offset; i < count; ++i){
    curr_offset = 32 + (i * 4);
    printf("%c", valueref_deref[curr_offset]);
  }
  printf("\n");
}

$$__device__$$
void java_io_PrintStream_println0_($$__global$$ char * gc_info, int thisref, int * exception){
  printf("\n");
}

$$__device__$$
void java_io_PrintStream_println0_1_($$__global$$ char * gc_info, int thisref, int value, int * exception){
  printf("%d\n", value);
}

$$__device__$$
void java_io_PrintStream_println0_2_($$__global$$ char * gc_info, int thisref, char value, int * exception){
  printf("%d\n", value);
}

$$__device__$$
void java_io_PrintStream_println0_3_($$__global$$ char * gc_info, int thisref, char value, int * exception){
  printf("%c\n", value);
}

$$__device__$$
void java_io_PrintStream_println0_4_($$__global$$ char * gc_info, int thisref, short value, int * exception){
  printf("%d\n", value);
}

$$__device__$$
void java_io_PrintStream_println0_5_($$__global$$ char * gc_info, int thisref, int value, int * exception){
  printf("%d\n", value);
}

$$__device__$$
void java_io_PrintStream_println0_6_($$__global$$ char * gc_info, int thisref, long long value, int * exception){
  printf("%lld\n", value);
}

$$__device__$$
void java_io_PrintStream_println0_7_($$__global$$ char * gc_info, int thisref, float value, int * exception){
  printf("%e\n", value);
}

$$__device__$$
void java_io_PrintStream_println0_8_($$__global$$ char * gc_info, int thisref, double value, int * exception){
  printf("%e\n", value);
}

$$__device__$$
void java_io_PrintStream_print0_9_($$__global$$ char * gc_info, int thisref, int str_ret, int * exception){
  int valueref;
  int count;
  int offset;
  int i;
  int curr_offset;

  char * valueref_deref;

  valueref = instance_getter_java_lang_String_value(gc_info, str_ret, exception);  
  if(*exception != 0){
    return; 
  } 
  count = instance_getter_java_lang_String_count(gc_info, str_ret, exception);
  if(*exception != 0){
    return; 
  } 
  offset = instance_getter_java_lang_String_offset(gc_info, str_ret, exception);
  if(*exception != 0){
    return; 
  } 
  valueref_deref = (char *) org_trifort_gc_deref(gc_info, valueref);
  for(i = offset; i < count; ++i){
    curr_offset = 32 + (i * 4);
    printf("%c", valueref_deref[curr_offset]);
  }
}

$$__device__$$
void java_io_PrintStream_print0_1_($$__global$$ char * gc_info, int thisref, int value, int * exception){
  printf("%d", value);
}

$$__device__$$
void java_io_PrintStream_print0_2_($$__global$$ char * gc_info, int thisref, char value, int * exception){
  printf("%d", value);
}

$$__device__$$
void java_io_PrintStream_print0_3_($$__global$$ char * gc_info, int thisref, char value, int * exception){
  printf("%c", value);
}

$$__device__$$
void java_io_PrintStream_print0_4_($$__global$$ char * gc_info, int thisref, short value, int * exception){
  printf("%d", value);
}

$$__device__$$
void java_io_PrintStream_print0_5_($$__global$$ char * gc_info, int thisref, int value, int * exception){
  printf("%d", value);
}

$$__device__$$
void java_io_PrintStream_print0_6_($$__global$$ char * gc_info, int thisref, long long value, int * exception){
  printf("%lld", value);
}

$$__device__$$
void java_io_PrintStream_print0_7_($$__global$$ char * gc_info, int thisref, float value, int * exception){
  printf("%e", value);
}

$$__device__$$
void java_io_PrintStream_print0_8_($$__global$$ char * gc_info, int thisref, double value, int * exception){
  printf("%e", value);
}

$$__device__$$
int org_trifort_rootbeer_runtime_RootbeerAtomicInt_atomicAdd($$__global$$ char * gc_info, int thisref, int value, int * exception){
  char * thisref_deref;
  int * array;

  thisref_deref = org_trifort_gc_deref ( gc_info , thisref ) ;
  thisref_deref += 32;
  array = (int *) thisref_deref;
  return atomicAdd(array, value);
}

$$__device__$$
double org_trifort_rootbeer_runtime_RootbeerGpu_sin($$__global$$ char * gc_info, double value, int * exception){
  return sin(value);
}

$$__device__$$ 
void org_trifort_rootbeer_runtime_RootbeerGpu_syncthreads($$__global$$ char * gc_info, int * exception){
  org_trifort_syncthreads();
}

$$__device__$$ 
void org_trifort_rootbeer_runtime_RootbeerGpu_threadfence($$__global$$ char * gc_info, int * exception){
  org_trifort_threadfence();
}

$$__device__$$ 
void org_trifort_rootbeer_runtime_RootbeerGpu_threadfenceBlock($$__global$$ char * gc_info, int * exception){
  org_trifort_threadfence_block();
}

$$__device__$$ char
org_trifort_cmp(long long lhs, long long rhs){
  if(lhs > rhs)
    return 1;
  if(lhs < rhs)
    return -1;
  return 0;
}

$$__device__$$ char
org_trifort_cmpl(double lhs, double rhs){
  if(lhs > rhs)
    return 1;
  if(lhs < rhs)
    return -1;
  if(lhs == rhs)
    return 0;
  return -1;
}

$$__device__$$ char
org_trifort_cmpg(double lhs, double rhs){
  if(lhs > rhs)
    return 1;
  if(lhs < rhs)
    return -1;
  if(lhs == rhs)
    return 0;
  return 1;
}


$$__device__$$ void
org_trifort_gc_memcpy($$__global$$ char * dest, $$__global$$ char * src, int len) {
  int i;
  for(i = 0; i < len; ++i){
    dest[i] = src[i];
  }
}

$$__device__$$ double org_trifort_modulus(double a, double b)
{
  long result = (long) ( a / b );
  return a - ((double) result) * b;
}

$$__device__$$ int
org_trifort_gc_get_loc($$__global$$ char * mem_loc, int count){
  mem_loc += sizeof(GC_OBJ_TYPE_COUNT) + sizeof(GC_OBJ_TYPE_COLOR) +
    sizeof(char) + sizeof(GC_OBJ_TYPE_CTOR_USED) + sizeof(GC_OBJ_TYPE_SIZE) +
    sizeof(GC_OBJ_TYPE_TYPE) + count * sizeof(int);
  return (($$__global$$ int *) mem_loc)[0];
}

$$__device__$$ void
org_trifort_gc_set_count($$__global$$ char * mem_loc, GC_OBJ_TYPE_COUNT value){
  mem_loc[0] = value;
}

$$__device__$$ GC_OBJ_TYPE_COUNT
org_trifort_gc_get_count($$__global$$ char * mem_loc){
  return mem_loc[0];
}

$$__device__$$ void
org_trifort_gc_set_color($$__global$$ char * mem_loc, GC_OBJ_TYPE_COLOR value){
  mem_loc += sizeof(GC_OBJ_TYPE_COUNT);
  mem_loc[0] = value;
}

$$__device__$$ void
org_trifort_gc_init_monitor($$__global$$ char * mem_loc){
  int * addr;
  mem_loc += 16;
  addr = (int *) mem_loc;
  *addr = -1;
}

$$__device__$$ GC_OBJ_TYPE_COLOR
org_trifort_gc_get_color($$__global$$ char * mem_loc){
  mem_loc += sizeof(GC_OBJ_TYPE_COUNT);
  return mem_loc[0];
}

$$__device__$$ void
org_trifort_gc_set_type($$__global$$ char * mem_loc, GC_OBJ_TYPE_TYPE value){
  mem_loc += sizeof(GC_OBJ_TYPE_COUNT) + sizeof(GC_OBJ_TYPE_COLOR) + sizeof(char) +
    sizeof(GC_OBJ_TYPE_CTOR_USED);
  *(($$__global$$ GC_OBJ_TYPE_TYPE *) &mem_loc[0]) = value;
}

$$__device__$$ GC_OBJ_TYPE_TYPE
org_trifort_gc_get_type($$__global$$ char * mem_loc){
  mem_loc += sizeof(GC_OBJ_TYPE_COUNT) + sizeof(GC_OBJ_TYPE_COLOR) + sizeof(char) +
    sizeof(GC_OBJ_TYPE_CTOR_USED);
  return *(($$__global$$ GC_OBJ_TYPE_TYPE *) &mem_loc[0]);
}

$$__device__$$ void
org_trifort_gc_set_ctor_used($$__global$$ char * mem_loc, GC_OBJ_TYPE_CTOR_USED value){
  mem_loc += sizeof(GC_OBJ_TYPE_COUNT) + sizeof(GC_OBJ_TYPE_COLOR) + sizeof(char);
  mem_loc[0] = value;
}

$$__device__$$ GC_OBJ_TYPE_CTOR_USED
org_trifort_gc_get_ctor_used($$__global$$ char * mem_loc){
  mem_loc += sizeof(GC_OBJ_TYPE_COUNT) + sizeof(GC_OBJ_TYPE_COLOR) + sizeof(char);
  return mem_loc[0];
}

$$__device__$$ void
org_trifort_gc_set_size($$__global$$ char * mem_loc, GC_OBJ_TYPE_SIZE value){
  mem_loc += sizeof(GC_OBJ_TYPE_COUNT) + sizeof(GC_OBJ_TYPE_COLOR) + sizeof(char) + 
    sizeof(GC_OBJ_TYPE_CTOR_USED) + sizeof(GC_OBJ_TYPE_TYPE);
  *(($$__global$$ GC_OBJ_TYPE_SIZE *) &mem_loc[0]) = value;
}

$$__device__$$ GC_OBJ_TYPE_SIZE
org_trifort_gc_get_size($$__global$$ char * mem_loc){
  mem_loc += sizeof(GC_OBJ_TYPE_COUNT) + sizeof(GC_OBJ_TYPE_COLOR) + sizeof(char) + 
    sizeof(GC_OBJ_TYPE_CTOR_USED) + sizeof(GC_OBJ_TYPE_TYPE);
  return *(($$__global$$ GC_OBJ_TYPE_SIZE *) &mem_loc[0]);
}

$$__device__$$ char org_trifort_getchar($$__global$$ char * buffer, int pos){
  return buffer[pos];
}

$$__device__$$ void org_trifort_setchar($$__global$$ char * buffer, int pos, char value){
  buffer[pos] = value;
}

$$__device__$$ short org_trifort_getshort($$__global$$ char * buffer, int pos){
  return *(($$__global$$ short *) &buffer[pos]);
}

$$__device__$$ void org_trifort_setshort($$__global$$ char * buffer, int pos, short value){
  *(($$__global$$ short *) &buffer[pos]) = value;
}

$$__device__$$ int org_trifort_getint($$__global$$ char * buffer, int pos){
  return *(($$__global$$ int *) &buffer[pos]);
}

$$__device__$$ void org_trifort_setint($$__global$$ char * buffer, int pos, int value){
  *(($$__global$$ int *) &buffer[pos]) = value;
}

$$__device__$$ long long org_trifort_getlong($$__global$$ char * buffer, int pos){
  return *(($$__global$$ long *) &buffer[pos]);
}

$$__device__$$ void org_trifort_setlong($$__global$$ char * buffer, int pos, long long value){
  *(($$__global$$ long long *) &buffer[pos]) = value;
}

$$__device__$$ size_t org_trifort_getsize_t($$__global$$ char * buffer, int pos){
  return *(($$__global$$ size_t *) &buffer[pos]);
}

$$__device__$$ void org_trifort_setsize_t($$__global$$ char * buffer, int pos, size_t value){
  *(($$__global$$ size_t *) &buffer[pos]) = value;
}

$$__device__$$ void
org_trifort_gc_set_to_space_address($$__global$$ char * gc_info, $$__global$$ char * value){
  org_trifort_setlong(gc_info, TO_SPACE_OFFSET, (long long) value);
}

$$__device__$$ $$__global$$ long long *
org_trifort_gc_get_to_space_address($$__global$$ char * gc_info){
  long long value = org_trifort_getlong(gc_info, TO_SPACE_OFFSET);
  return ($$__global$$ long long *) value;
}

$$__device__$$ long long
org_trifort_gc_get_to_space_free_ptr($$__global$$ char * gc_info){
  return org_trifort_getlong(gc_info, TO_SPACE_FREE_POINTER_OFFSET);
}

$$__device__$$ void
org_trifort_gc_set_to_space_free_ptr($$__global$$ char * gc_info, long long value){
  org_trifort_setlong(gc_info, TO_SPACE_FREE_POINTER_OFFSET, value);
}

$$__device__$$ int
org_trifort_gc_get_space_size($$__global$$ char * gc_info){
  return org_trifort_getint(gc_info, SPACE_SIZE_OFFSET);
}

$$__device__$$ int
org_trifort_strlen(char * str_constant){
  int ret = 0;
  while(1){
    if(str_constant[ret] != '\0'){
      ret++;
    } else {
      return ret;
    }
  }
}

$$__device__$$ int
org_trifort_array_length($$__global$$ char * gc_info, int thisref){
  //if(thisref & 0x1000000000000000L){
  //  thisref &= 0x0fffffffffffffffL;
  //  thisref += 8;
  //  return org_trifort_cache_get_int(thisref);
  //} else {
    $$__global$$ char * thisref_deref = org_trifort_gc_deref(gc_info, thisref);
    int ret = org_trifort_getint(thisref_deref, 12);
    return ret;
  //}
}

$$__device__$$
int java_lang_String_initab850b60f96d11de8a390800200c9a66(char * gc_info, int parameter0, int * exception) { 
  int r0 = -1; 
  int r1 = -1; 
  int i0; 
  int $r2 = -1; 
  int thisref; 
  char * thisref_deref; 
  int i;
  int len;
  int characters_copy;
  char ch;
  
  thisref = -1; 
  org_trifort_gc_assign(gc_info, &thisref, org_trifort_gc_malloc(gc_info, 48)); 
  if(thisref == -1) { 
    *exception = %%java_lang_NullPointerException_TypeNumber%%; 
    return -1; 
  } 
  thisref_deref = org_trifort_gc_deref(gc_info, thisref); 
  org_trifort_gc_set_count(thisref_deref, 1); 
  org_trifort_gc_set_color(thisref_deref, COLOR_GREY); 
  org_trifort_gc_set_type(thisref_deref, %%java_lang_String_TypeNumber%%); 
  org_trifort_gc_set_ctor_used(thisref_deref, 1); 
  org_trifort_gc_set_size(thisref_deref, 48); 
  org_trifort_gc_init_monitor(thisref_deref); 

  len = org_trifort_array_length(gc_info, parameter0);
  characters_copy = char__array_new(gc_info, len, exception);
  for(i = 0; i < len; ++i){
    ch = char__array_get(gc_info, parameter0, i, exception);
    char__array_set(gc_info, characters_copy, i, ch, exception);
  }
  instance_setter_java_lang_String_value(gc_info, thisref, characters_copy, exception); 
  instance_setter_java_lang_String_count(gc_info, thisref, len, exception); 
  instance_setter_java_lang_String_offset(gc_info, thisref, 0, exception); 
  return thisref; 
} 

$$__device__$$ int 
char__array_new($$__global$$ char * gc_info, int size, int * exception);

$$__device__$$ void 
char__array_set($$__global$$ char * gc_info, int thisref, int parameter0, char parameter1, int * exception);

$$__device__$$ int
org_trifort_string_constant($$__global$$ char * gc_info, char * str_constant, int * exception){
  int i;
  int len = org_trifort_strlen(str_constant);
  int characters = char__array_new(gc_info, len, exception);
  for(i = 0; i < len; ++i){
    char__array_set(gc_info, characters, i, str_constant[i], exception);
  }
  
  return java_lang_String_initab850b60f96d11de8a390800200c9a66(gc_info, characters, exception);
}

$$__device__$$ void
org_trifort_gc_assign($$__global$$ char * gc_info, int * lhs_ptr, int rhs){
  *lhs_ptr = rhs;
}

$$__device__$$ void
org_trifort_gc_assign_global($$__global$$ char * gc_info, $$__global$$ int * lhs_ptr, int rhs){
  *lhs_ptr = rhs;
}
 
$$__device__$$ int java_lang_StackTraceElement__array_get($$__global$$ char * gc_info, int thisref, int parameter0, int * exception);
$$__device__$$ void java_lang_StackTraceElement__array_set($$__global$$ char * gc_info, int thisref, int parameter0, int parameter1, int * exception);
$$__device__$$ int java_lang_StackTraceElement__array_new($$__global$$ char * gc_info, int size, int * exception);
$$__device__$$ int java_lang_StackTraceElement_initab850b60f96d11de8a390800200c9a660_3_3_3_4_($$__global$$ char * gc_info, int parameter0, int parameter1, int parameter2, int parameter3, int * exception);
$$__device__$$ void instance_setter_java_lang_RuntimeException_stackDepth($$__global$$ char * gc_info, int thisref, int parameter0);
$$__device__$$ int instance_getter_java_lang_RuntimeException_stackDepth($$__global$$ char * gc_info, int thisref);
$$__device__$$ int java_lang_StackTraceElement__array_get($$__global$$ char * gc_info, int thisref, int parameter0, int * exception);
$$__device__$$ int instance_getter_java_lang_Throwable_stackTrace($$__global$$ char * gc_info, int thisref, int * exception);
$$__device__$$ void instance_setter_java_lang_Throwable_stackTrace($$__global$$ char * gc_info, int thisref, int parameter0, int * exception);

$$__device__$$ int java_lang_Throwable_fillInStackTrace($$__global$$ char * gc_info, int thisref, int * exception){
  //int trace = java_lang_StackTraceElement__array_new(gc_info, 8, exception);
  //instance_setter_java_lang_Throwable_stackTrace(gc_info, thisref, trace, exception);
  return thisref;
}

$$__device__$$ int java_lang_Throwable_getStackTraceElement($$__global$$ char * gc_info, int thisref, int parameter0, int * exception){
  //int array = instance_getter_java_lang_Throwable_stackTrace(gc_info, thisref, exception);
  //return java_lang_StackTraceElement__array_get(gc_info, array, parameter0, exception);
  return -1;
}

$$__device__$$ int java_lang_Throwable_getStackTraceDepth($$__global$$ char * gc_info, int thisref, int * exception){
  return 0;
}

$$__device__$$ void org_trifort_fillInStackTrace($$__global$$ char * gc_info, int exception, char * class_name, char * method_name){
}

$$__device__$$ void instance_setter_java_lang_Throwable_cause($$__global$$ char * gc_info, int thisref, int parameter0, int * exception);
$$__device__$$ void instance_setter_java_lang_Throwable_detailMessage($$__global$$ char * gc_info, int thisref, int parameter0, int * exception);
$$__device__$$ void instance_setter_java_lang_Throwable_stackDepth($$__global$$ char * gc_info, int thisref, int parameter0, int * exception);
$$__device__$$ void java_lang_VirtualMachineError_initab850b60f96d11de8a390800200c9a66_body0_($$__global$$ char * gc_info, int thisref, int * exception);

$$__device__$$ int java_lang_OutOfMemoryError_initab850b60f96d11de8a390800200c9a66($$__global$$ char * gc_info, int * exception){
  int r0 = -1;
  int thisref = org_trifort_gc_malloc(gc_info, 40);
  char * thisref_deref = org_trifort_gc_deref(gc_info, thisref);

  //class info
  org_trifort_gc_set_count(thisref_deref, 0);
  org_trifort_gc_set_color(thisref_deref, COLOR_GREY);
  org_trifort_gc_set_type(thisref_deref, 9);
  org_trifort_gc_set_ctor_used(thisref_deref, 1);
  org_trifort_gc_set_size(thisref_deref, 40);

  instance_setter_java_lang_Throwable_cause(gc_info, thisref, -1, exception);
  instance_setter_java_lang_Throwable_detailMessage(gc_info, thisref, -1, exception);
  instance_setter_java_lang_Throwable_stackTrace(gc_info, thisref, -1, exception);

  //r0 := @this: java.lang.OutOfMemoryError
  org_trifort_gc_assign(gc_info, & r0 ,  thisref );

  //specialinvoke r0.<java.lang.VirtualMachineError: void <init>()>()
  java_lang_VirtualMachineError_initab850b60f96d11de8a390800200c9a66_body0_(gc_info,
   thisref, exception);
  return thisref;
}


$$__device__$$ int
java_lang_Object_hashCode($$__global$$ char * gc_info, int thisref, int * exception){
  return thisref;
}

$$__device__$$ int
java_lang_Class_getName( char * gc_info , int thisref , int * exception ) { 
  int $r1 =-1 ; 
  $r1 = instance_getter_java_lang_Class_name ( gc_info , thisref , exception ) ; 
  if ( * exception != 0 ) { 
    return 0 ; 
  } 
  return $r1;
}

$$__device__$$ int
java_lang_Object_getClass( char * gc_info , int thisref, int * exception ) { 
  char * mem_loc = org_trifort_gc_deref(gc_info, thisref);
  int type = org_trifort_gc_get_type(mem_loc);
  return org_trifort_classConstant(type);
}

$$__device__$$ int
java_lang_StringValue_from( char * gc_info , int thisref, int * exception ) { 
  int i, size, new_ref;
  char * mem_loc, * new_mem_loc;
  
  mem_loc = org_trifort_gc_deref(gc_info, thisref);
  size = org_trifort_gc_get_size(mem_loc);
  new_ref = org_trifort_gc_malloc(gc_info, size);
  new_mem_loc = org_trifort_gc_deref(gc_info, new_ref);
  
  for(i = 0; i < size; ++i){
    new_mem_loc[i] = mem_loc[i];  
  }
  
  return new_ref;
}

$$__device__$$ int
java_util_Arrays_copyOf(char * gc_info, int object_array, int new_size, int * exception ){
  int ret;
  char * ret_deref;
  char * object_array_deref;
  int length;
  int i;
  
  ret = org_trifort_gc_malloc(gc_info, 32 + (4 * new_size));
  ret_deref = org_trifort_gc_deref(gc_info, ret);
  object_array_deref = org_trifort_gc_deref(gc_info, object_array);
    
  for(i = 0; i < 32; ++i){
    ret_deref[i] = object_array_deref[i];
  }

  length = org_trifort_getint(object_array_deref, 12);
  org_trifort_setint(ret_deref, 8, 32 + (4 * new_size));
  org_trifort_setint(ret_deref, 12, new_size);

  if(length < new_size){
    for(i = 0; i < length * 4; ++i){
      ret_deref[32+i], object_array_deref[32+i];
    }
    int diff = new_size - length;
    for(i = 0; i < diff; ++i){
      * ((int *) &ret_deref[32 + (length * 4) + (i * 4)]) = -1;
    }
  } else {
    for(i = 0; i < new_size * 4; ++i){
      ret_deref[32+i], object_array_deref[32+i];
    }
  }

  return ret; 
}

//<java.lang.StringBuilder: java.lang.StringBuilder init()>
$$__device__$$
int java_lang_StringBuilder_initab850b60f96d11de8a390800200c9a660_(char * gc_info, int * exception){ 
  int thisref;
  char * thisref_deref;
  int chars;

  thisref = org_trifort_gc_malloc(gc_info , 48);
  if(thisref == -1){
    *exception = %%java_lang_NullPointerException_TypeNumber%%; 
    return -1; 
  }

  thisref_deref = org_trifort_gc_deref(gc_info, thisref);
  org_trifort_gc_set_count(thisref_deref, 1); 
  org_trifort_gc_set_color(thisref_deref, COLOR_GREY); 
  org_trifort_gc_set_type(thisref_deref, %%java_lang_StringBuilder_TypeNumber%%); 
  org_trifort_gc_set_ctor_used(thisref_deref, 1); 
  org_trifort_gc_set_size(thisref_deref, 48); 
  org_trifort_gc_init_monitor(thisref_deref); 

  chars = char__array_new(gc_info, 0, exception);
  instance_setter_java_lang_AbstractStringBuilder_value(gc_info, thisref, chars, exception); 
  instance_setter_java_lang_AbstractStringBuilder_count(gc_info, thisref, 0, exception);
  return thisref; 
}

//<java.lang.StringBuilder: java.lang.StringBuilder void(java.lang.String)>
$$__device__$$ 
int java_lang_StringBuilder_initab850b60f96d11de8a390800200c9a6610_9_(char * gc_info, 
  int str ,int * exception){
 
  int thisref; 
  int str_value;
  int str_count;  

  char * thisref_deref; 
  thisref = -1;
  org_trifort_gc_assign ( gc_info , & thisref , org_trifort_gc_malloc ( gc_info , 48 ) ) ; 
  if ( thisref ==-1 ) { 
    * exception = %%java_lang_NullPointerException_TypeNumber%%; 
    return-1 ; 
  } 
  thisref_deref = org_trifort_gc_deref ( gc_info , thisref ) ; 
  org_trifort_gc_set_count ( thisref_deref , 0 ) ; 
  org_trifort_gc_set_color ( thisref_deref , COLOR_GREY ) ; 
  org_trifort_gc_set_type ( thisref_deref , %%java_lang_StringBuilder_TypeNumber%% ) ; 
  org_trifort_gc_set_ctor_used ( thisref_deref , 1 ) ; 
  org_trifort_gc_set_size ( thisref_deref , 44 ) ; 
  org_trifort_gc_init_monitor ( thisref_deref ) ; 

  str_value = instance_getter_java_lang_String_value(gc_info, str, exception);
  str_count = instance_getter_java_lang_String_count(gc_info, str, exception);

  instance_setter_java_lang_AbstractStringBuilder_value(gc_info, thisref, str_value, exception); 
  instance_setter_java_lang_AbstractStringBuilder_count(gc_info, thisref, str_count, exception); 
  return thisref; 
} 

//<java.lang.StringBuilder: java.lang.StringBuilder append(java.lang.String)>
$$__device__$$ 
int java_lang_StringBuilder_append10_9_(char * gc_info, int thisref,
  int parameter0, int * exception){

  int sb_value;
  int sb_count;
  int str_value;
  int str_count;
  int new_count;
  int new_sb_value;
  int i;
  char ch;
  int new_str;

  //get string builder value and count
  sb_value = instance_getter_java_lang_AbstractStringBuilder_value(gc_info, thisref,
    exception);

  sb_count = instance_getter_java_lang_AbstractStringBuilder_count(gc_info, thisref,
    exception);

  //get string value and count
  str_value = instance_getter_java_lang_String_value(gc_info, parameter0,
    exception);

  str_count = instance_getter_java_lang_String_count(gc_info, parameter0,
    exception);

  new_count = sb_count + str_count;
  new_sb_value = char__array_new(gc_info, new_count, exception);
  for(i = 0; i < sb_count; ++i){
    ch = char__array_get(gc_info, sb_value, i, exception);
    char__array_set(gc_info, new_sb_value, i, ch, exception);
  }
  for(i = 0; i < str_count; ++i){
    ch = char__array_get(gc_info, str_value, i, exception);
    char__array_set(gc_info, new_sb_value, sb_count + i, ch, exception);
  }

  //make new String
  new_str = java_lang_String_initab850b60f96d11de8a390800200c9a66(gc_info, 
    new_sb_value, exception);

  //return new StringBuilder from String
  return java_lang_StringBuilder_initab850b60f96d11de8a390800200c9a6610_9_(gc_info,
    new_str, exception);
}

//<java.lang.StringBuilder: java.lang.StringBuilder append(boolean)>
$$__device__$$ 
int java_lang_StringBuilder_append10_1_(char * gc_info, int thisref,
  bool parameter0, int * exception){
  
  int str = java_lang_Boolean_toString9_1_(gc_info, parameter0, exception);
  return java_lang_StringBuilder_append10_9_(gc_info, thisref, str, exception);
}

//<java.lang.StringBuilder: java.lang.StringBuilder append(char)>
$$__device__$$ 
int java_lang_StringBuilder_append10_3_(char * gc_info, int thisref,
  int parameter0, int * exception){
  
  int str = java_lang_Character_toString9_3_(gc_info, parameter0, exception);
  return java_lang_StringBuilder_append10_9_(gc_info, thisref, str, exception);
}

//<java.lang.StringBuilder: java.lang.StringBuilder append(double)>
$$__device__$$ 
int java_lang_StringBuilder_append10_8_(char * gc_info, int thisref,
  double parameter0, int * exception){
  
  int str = java_lang_Double_toString9_8_(gc_info, parameter0, exception);
  return java_lang_StringBuilder_append10_9_(gc_info, thisref, str, exception);
}

//<java.lang.StringBuilder: java.lang.StringBuilder append(float)>
$$__device__$$ 
int java_lang_StringBuilder_append10_7_(char * gc_info, int thisref,
  float parameter0, int * exception){
  
  int str = java_lang_Float_toString9_7_(gc_info, parameter0, exception);
  return java_lang_StringBuilder_append10_9_(gc_info, thisref, str, exception);
}

//<java.lang.StringBuilder: java.lang.StringBuilder append(int)>
$$__device__$$ 
int java_lang_StringBuilder_append10_5_(char * gc_info, int thisref,
  int parameter0, int * exception){

  int str = java_lang_Integer_toString9_5_(gc_info, parameter0, exception);
  return java_lang_StringBuilder_append10_9_(gc_info, thisref, str, exception);
}

//<java.lang.StringBuilder: java.lang.StringBuilder append(long)>
$$__device__$$ 
int java_lang_StringBuilder_append10_6_(char * gc_info, int thisref,
  long long parameter0, int * exception){

  int str = java_lang_Long_toString9_6_(gc_info, parameter0, exception);
  return java_lang_StringBuilder_append10_9_(gc_info, thisref, str, exception);
}

//<java.lang.StringBuilder: java.lang.String toString()>
$$__device__$$ 
int java_lang_StringBuilder_toString9_(char * gc_info, int thisref,
  int * exception){
 
  int value = instance_getter_java_lang_AbstractStringBuilder_value(gc_info, thisref,
    exception);
  return java_lang_String_initab850b60f96d11de8a390800200c9a66(gc_info, value, 
    exception);
}

//<java.lang.Integer: java.lang.Integer init(int)>
$$__device__$$
int java_lang_Integer_initab850b60f96d11de8a390800200c9a660_5_(char * gc_info, 
  int int_value, int * exception){
  int thisref;
  char * thisref_deref;

  thisref = -1;
  thisref = org_trifort_gc_malloc(gc_info , 48);
  if ( thisref ==-1 ) { 
    * exception = %%java_lang_NullPointerException_TypeNumber%%; 
    return-1 ; 
  }
  thisref_deref = org_trifort_gc_deref(gc_info, thisref);
  org_trifort_gc_set_count(thisref_deref, 0);
  org_trifort_gc_set_color(thisref_deref, COLOR_GREY);
  org_trifort_gc_set_type(thisref_deref, %%java_lang_Integer_TypeNumber%%);
  org_trifort_gc_set_ctor_used(thisref_deref, 1);
  org_trifort_gc_set_size(thisref_deref, 48);
  org_trifort_gc_init_monitor(thisref_deref);

  // instance_setter_java_lang_Integer_value(gc_info, thisref, 0, exception);
  instance_setter_java_lang_Integer_value(gc_info, thisref, int_value, exception);
  return thisref;
}

//<java.lang.Integer: java.lang.Integer valueOf(int)>
$$__device__$$
int java_lang_Integer_valueOf(char * gc_info, int int_value, int * exception) {
  int return_obj = -1;
  
  org_trifort_gc_assign(gc_info, 
    &return_obj, java_lang_Integer_initab850b60f96d11de8a390800200c9a660_5_(gc_info,
    int_value, exception));
  
  if(*exception != 0) {
    return 0; 
  }

  return return_obj;
}

$$__device__$$
double at_illecker_abs_val(double value) {
  double result = value;
  if (value < 0) {
    result = -value;
  }
  return result;
}

$$__device__$$
double at_illecker_pow10(int exp) {
  double result = 1;
  while (exp) {
    result *= 10;
    exp--;
  }
  return result;
}

$$__device__$$
long long at_illecker_round(double value) {
  long long intpart;
  intpart = value;
  value = value - intpart;
  if (value >= 0.5) {
    intpart++;
  }
  return intpart;
}

$$__device__$$
void at_illecker_set_char(char *buffer, int *currlen, int maxlen, char c) {
  if (*currlen < maxlen) {
    buffer[(*currlen)++] = c;
  }
}

// local double to string method
// http://www.opensource.apple.com/source/srm/srm-6/srm/lib/snprintf.c
$$__device__$$
int at_illecker_double_to_string(char * gc_info, double fvalue, int max, int * exception) {
  int signvalue = 0;
  double ufvalue;
  long long intpart;
  long long fracpart;
  char iconvert[20];
  char fconvert[20];
  int iplace = 0;
  int fplace = 0;
  int zpadlen = 0; // lasting zeros

  char buffer[64];
  int maxlen = 64;
  int currlen = 0;

  // Max digits after decimal point, default is 6
  if (max < 0) {
    max = 6;
  }
  // Sorry, we only support 9 digits past the decimal because of our 
  // conversion method
  if (max > 9) {
    max = 9;
  }

  // Set sign if negative
  if (fvalue < 0) {
    signvalue = '-';
  }

  ufvalue = at_illecker_abs_val(fvalue);
  intpart = ufvalue;

  // We "cheat" by converting the fractional part to integer by
  // multiplying by a factor of 10
  fracpart = at_illecker_round(at_illecker_pow10(max) * (ufvalue - intpart));

  if (fracpart >= at_illecker_pow10(max)) {
    intpart++;
    fracpart -= at_illecker_pow10(max);
  }

  // Convert integer part
  do {
    iconvert[iplace++] = "0123456789abcdef"[intpart % 10];
    intpart = (intpart / 10);
  } while(intpart && (iplace < 20));

  if (iplace == 20) {
    iplace--;
  }
  iconvert[iplace] = 0;

  // Convert fractional part
  do {
    fconvert[fplace++] = "0123456789abcdef"[fracpart % 10];
    fracpart = (fracpart / 10);
  } while(fracpart && (fplace < 20));
  
  if (fplace == 20) {
    fplace--;
  }
  fconvert[fplace] = 0;

  // Calc lasting zeros for padding
  zpadlen = max - fplace;
  if (zpadlen < 0) {
    zpadlen = 0;
  }

  // Set sign
  if (signvalue) {
    at_illecker_set_char(buffer, &currlen, maxlen, signvalue);
  }

  // Set integer part
  while (iplace > 0) {
    at_illecker_set_char(buffer, &currlen, maxlen, iconvert[--iplace]);
  }

  // Check if decimal point is needed
  if (max > 0) {
    // Set decimal point
    // This should probably use locale to find the correct
    // char to print out.
    at_illecker_set_char(buffer, &currlen, maxlen, '.');

    // Add lasting zeros
    while (zpadlen > 0) {
      at_illecker_set_char(buffer, &currlen, maxlen, '0');
      --zpadlen;
    }

    while (fplace > 0) {
      at_illecker_set_char(buffer, &currlen, maxlen, fconvert[--fplace]);
    }
  }

  // Terminate string
  if (currlen < maxlen - 1) {
    buffer[currlen] = '\0';
  } else {
    buffer[maxlen - 1] = '\0';
  }

  return org_trifort_string_constant(gc_info, buffer, exception);
}

//<java.lang.Double: java.lang.String toString(double)>
$$__device__$$ 
int java_lang_Double_toString9_8_(char * gc_info, double double_val, int * exception) {

  // Default is 6 digits after decimal point
  return at_illecker_double_to_string(gc_info, double_val, 6, exception);
}

//<java.lang.Float: java.lang.String toString(float)>
$$__device__$$ 
int java_lang_Float_toString9_7_(char * gc_info, float float_val, int * exception){

  // Default is 6 digits after decimal point
  return at_illecker_double_to_string(gc_info, (double)float_val, 6, exception);
}
