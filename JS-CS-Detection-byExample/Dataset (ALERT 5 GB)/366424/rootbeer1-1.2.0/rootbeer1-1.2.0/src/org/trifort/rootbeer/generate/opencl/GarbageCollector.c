#define GC_OBJ_TYPE_COUNT char
#define GC_OBJ_TYPE_COLOR char
#define GC_OBJ_TYPE_TYPE int
#define GC_OBJ_TYPE_CTOR_USED char
#define GC_OBJ_TYPE_SIZE int

#define COLOR_GREY 0
#define COLOR_BLACK 1
#define COLOR_WHITE 2

#define OBJECT_HEADER_POSITION_GC_COUNT         0
#define OBJECT_HEADER_POSITION_GC_COLOR         1
#define OBJECT_HEADER_POSITION_CTOR_USED        3
#define OBJECT_HEADER_POSITION_CLASS_NUMBER     4
#define OBJECT_HEADER_POSITION_OBJECT_SIZE      8
#define OBJECT_HEADER_POSITION_MONITOR          16

$$__device__$$ void org_trifort_gc_collect();
$$__device__$$ void org_trifort_gc_assign(int * lhs, int rhs);
$$__device__$$ $$__global$$ char * org_trifort_gc_deref(int handle);
$$__device__$$ int org_trifort_gc_malloc(int size);
$$__device__$$ int org_trifort_gc_malloc_no_fail(int size);
$$__device__$$ int org_trifort_classConstant(int type_num);
$$__device__$$ long long java_lang_System_nanoTime(int * exception);

#define CACHE_SIZE_BYTES 32
#define CACHE_SIZE_INTS (CACHE_SIZE_BYTES / sizeof(int))
#define CACHE_ENTRY_SIZE 4

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

$$__device__$$
int org_trifort_rootbeer_get_string_char_array(int thisref, int * exception)
{
  char * thisref_deref;
  if(thisref == -1){
    *exception = %%java_lang_NullPointerException_TypeNumber%%; 
    return -1;
  }
  thisref_deref = org_trifort_gc_deref(thisref);
  return *(( int *) &thisref_deref[32]);
}

$$__device__$$
void org_trifort_rootbeer_set_string_char_array(int thisref, int value, int * exception)
{
  char * thisref_deref;
  if(thisref == -1){
    *exception = %%java_lang_NullPointerException_TypeNumber%%; 
    return;
  }
  thisref_deref = org_trifort_gc_deref(thisref);
  *(( int *) &thisref_deref[32]) = value;
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
org_trifort_array_length(int thisref, int * exception){
  $$__global$$ char * thisref_deref;
  
  if(thisref == -1){
    *exception = %%java_lang_NullPointerException_TypeNumber%%; 
    return 0;
  }
  thisref_deref = org_trifort_gc_deref(thisref);
  int ret = org_trifort_getint(thisref_deref, 12);
  return ret;
}

$$__device__$$
void org_trifort_exitMonitorRef(int thisref, int old){
  char * mem = org_trifort_gc_deref(thisref); 
  mem += 16;
  if(old == -1){    
    org_trifort_threadfence();  
    atomicExch((int *) mem, -1); 
  }
}

$$__device__$$
void org_trifort_exitMonitorMem(char * mem, int old){
  if(old == -1){   
    org_trifort_threadfence(); 
    atomicExch((int *) mem, -1);
  }
}

$$__device__$$ 
long long java_lang_Double_doubleToLongBits(double value, int * exception){
  long long ret = *((long long *) ((double *) &value));
  return ret;
}

$$__device__$$ 
double java_lang_Double_longBitsToDouble(long long value, int * exception){
  double ret = *((double *) ((long long *) &value));
  return ret;
}

$$__device__$$
int java_lang_Float_floatToIntBits(float value, int * exception){
  int ret = *((int *) ((float *) &value));
  return ret;
}  

$$__device__$$
float java_lang_Float_intBitsToFloat(int value, int * exception){
  float ret = *((float *) ((int *) &value));
  return ret;
}

$$__device__$$ double java_lang_StrictMath_exp(double parameter0 , int * exception ) { 
  return exp(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_log(double parameter0 , int * exception ) { 
  return log(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_log10(double parameter0 , int * exception ) { 
  return log10(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_sqrt(double parameter0 , int * exception ) { 
  return sqrt(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_cbrt(double parameter0 , int * exception ) { 
  //2.2204460492503131e-16 is DBL_EPSILON
  if (fabs(parameter0) < 2.2204460492503131e-16){
    return 0.0;
  }

  if (parameter0 > 0.0) {
    return pow(parameter0, 1.0/3.0);
  }

  return -pow(-parameter0, 1.0/3.0);
} 

$$__device__$$ double java_lang_StrictMath_IEEEremainder(double parameter0 , double parameter1, int * exception ) { 
  return remainder(parameter0, parameter1); 
} 

$$__device__$$ double java_lang_StrictMath_ceil(double parameter0 , int * exception ) { 
  return ceil(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_floor(double parameter0 , int * exception ) { 
  return floor(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_sin(double parameter0 , int * exception ) { 
  return sin(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_cos(double parameter0 , int * exception ) { 
  return cos(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_tan(double parameter0 , int * exception ) { 
  return tan(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_asin(double parameter0 , int * exception ) { 
  return asin(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_acos(double parameter0 , int * exception ) { 
  return acos(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_atan(double parameter0 , int * exception ) { 
  return atan(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_atan2(double parameter0 , double parameter1, int * exception ) { 
  return atan2(parameter0, parameter1); 
} 

$$__device__$$ double java_lang_StrictMath_pow(double parameter0 , double parameter1, int * exception ) { 
  return pow(parameter0, parameter1); 
} 

$$__device__$$ double java_lang_StrictMath_sinh(double parameter0 , int * exception ) { 
  return sinh(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_cosh(double parameter0 , int * exception ) { 
  return cosh(parameter0); 
} 

$$__device__$$ double java_lang_StrictMath_tanh(double parameter0 , int * exception ) { 
  return tanh(parameter0); 
} 

$$__device__$$ 
void org_trifort_rootbeer_runtime_GpuStopwatch_start(int thisref, int * exception){
  long long int time;
  
  time = clock64();
  instance_setter_org_trifort_rootbeer_runtime_GpuStopwatch_m_start(thisref, time, exception);
}

$$__device__$$ 
void org_trifort_rootbeer_runtime_GpuStopwatch_stop(int thisref, int * exception){
  long long int time;
  
  time = clock64();
  instance_setter_org_trifort_rootbeer_runtime_GpuStopwatch_m_stop(thisref, time, exception);
}

$$__device__$$ 
char org_trifort_rootbeer_runtime_RootbeerGpu_isOnGpu(int * exception){
  return 1;
}

$$__device__$$ 
int org_trifort_rootbeer_runtime_RootbeerGpu_getThreadId(int * exception){
  return getThreadId();
}

$$__device__$$ 
int org_trifort_rootbeer_runtime_RootbeerGpu_getThreadIdxx(int * exception){
  return getThreadIdxx();
}

$$__device__$$ 
int org_trifort_rootbeer_runtime_RootbeerGpu_getBlockIdxx(int * exception){
  return getBlockIdxx();
}

$$__device__$$ 
int org_trifort_rootbeer_runtime_RootbeerGpu_getBlockDimx(int * exception){
  return getBlockDimx();
}

$$__device__$$ 
long long org_trifort_rootbeer_runtime_RootbeerGpu_getGridDimx(int * exception){
  return getGridDimx();
}


$$__device__$$ 
long long org_trifort_rootbeer_runtime_RootbeerGpu_getRef(int ref, int * exception){
  return ref;
}

$$__device__$$
char org_trifort_rootbeer_runtime_RootbeerGpu_getSharedByte(int index, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(
      index, 0, %%shared_mem_size%%, exception);
    return 0;
  }
#endif
  return m_shared[index]; 
}

$$__device__$$
void org_trifort_rootbeer_runtime_RootbeerGpu_setSharedByte(int index, char value, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds( 
      index, 0, %%shared_mem_size%%, exception);
    return;
  }
#endif
  m_shared[index] = value;
}
  
$$__device__$$
char org_trifort_rootbeer_runtime_RootbeerGpu_getSharedChar(int index, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(
      index, 0, %%shared_mem_size%%, exception);
    return 0;
  }
#endif
  return m_shared[index]; 
}

$$__device__$$
void org_trifort_rootbeer_runtime_RootbeerGpu_setSharedChar(int index, char value, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(
      index, 0, %%shared_mem_size%%, exception);
    return;
  }
#endif
  m_shared[index] = value;
}
  
$$__device__$$
char org_trifort_rootbeer_runtime_RootbeerGpu_getSharedBoolean(int index, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(
      index, 0, %%shared_mem_size%%, exception);
    return 0;
  }
#endif
  return m_shared[index]; 
}

$$__device__$$
void org_trifort_rootbeer_runtime_RootbeerGpu_setSharedBoolean(int index, char value, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(
      index, 0, %%shared_mem_size%%, exception);
    return;
  }
#endif
  m_shared[index] = value;
}
  
$$__device__$$
short org_trifort_rootbeer_runtime_RootbeerGpu_getSharedShort(int index, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index + 2 >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(
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
void org_trifort_rootbeer_runtime_RootbeerGpu_setSharedShort(int index, short value, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index + 2 >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(
      index, 0, %%shared_mem_size%%, exception);
    return;
  }
#endif
  m_shared[index] = (char) (value & 0xff);
  m_shared[index + 1] = (char) ((value >> 8) & 0xff);
}

$$__device__$$
int org_trifort_rootbeer_runtime_RootbeerGpu_getSharedInteger(int index, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index + 4 >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(
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
void org_trifort_rootbeer_runtime_RootbeerGpu_setSharedInteger(int index, int value, int * exception){  
#ifdef ARRAY_CHECKS
  if(index < 0 || index + 4 >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(
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
long long org_trifort_rootbeer_runtime_RootbeerGpu_getSharedLong(int index, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index + 8 >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds( 
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
void org_trifort_rootbeer_runtime_RootbeerGpu_setSharedLong(int index, long long value, int * exception){
#ifdef ARRAY_CHECKS
  if(index < 0 || index + 8 >= %%shared_mem_size%%){
    *exception = org_trifort_rootbeer_runtimegpu_GpuException_arrayOutOfBounds(
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
float org_trifort_rootbeer_runtime_RootbeerGpu_getSharedFloat(int index, int * exception){
  int int_value = org_trifort_rootbeer_runtime_RootbeerGpu_getSharedInteger(index, exception);
  return *((float *) &int_value);
}

$$__device__$$
void org_trifort_rootbeer_runtime_RootbeerGpu_setSharedFloat(int index, float value, int * exception){
  int int_value = *((int *) &value);
  org_trifort_rootbeer_runtime_RootbeerGpu_setSharedInteger(index, int_value, exception);
}
  
$$__device__$$
double org_trifort_rootbeer_runtime_RootbeerGpu_getSharedDouble(int index, int * exception){
  long long long_value = org_trifort_rootbeer_runtime_RootbeerGpu_getSharedLong(index, exception);
  return *((double *) &long_value);
}

$$__device__$$
void org_trifort_rootbeer_runtime_RootbeerGpu_setSharedDouble(int index, double value, int * exception){
  long long long_value = *((long long *) &value);
  org_trifort_rootbeer_runtime_RootbeerGpu_setSharedLong(index, long_value, exception);
}

$$__device__$$
int org_trifort_rootbeer_runtime_RootbeerGpu_getSharedObject(int index, int * exception){
  return org_trifort_rootbeer_runtime_RootbeerGpu_getSharedInteger(index, exception);
}

$$__device__$$
void org_trifort_rootbeer_runtime_RootbeerGpu_setSharedObject(int index, int value, int * exception){
  org_trifort_rootbeer_runtime_RootbeerGpu_setSharedInteger(index, value, exception);
}
  
$$__device__$$
void java_io_PrintStream_println0_9_(int thisref, int str_ret, int * exception){
  int valueref;
  int count;
  int i;
  int curr_offset;

  char * valueref_deref;

  valueref = org_trifort_rootbeer_get_string_char_array(str_ret, exception);  
  if(*exception != 0){
    return; 
  } 
  
  count = org_trifort_array_length(valueref, exception);
  if(*exception != 0){
    return; 
  } 
  valueref_deref = (char *) org_trifort_gc_deref(valueref);
  for(i = 0; i < count; ++i){
    curr_offset = 32 + (i * 4);
    printf("%c", valueref_deref[curr_offset]);
  }
  printf("\n");
}

$$__device__$$
void java_io_PrintStream_println0_11_(int thisref, int obj_ref, int * exception){
  int str_ref;
  int valueref;
  int count;
  int i;
  int curr_offset;
  char * valueref_deref;

  str_ref = java_lang_String_valueOf(obj_ref, exception);
  if(*exception != 0){
    return; 
  }
  
  valueref = org_trifort_rootbeer_get_string_char_array(str_ref, exception);  
  if(*exception != 0){
    return; 
  } 
  
  count = org_trifort_array_length(valueref, exception);
  if(*exception != 0){
    return; 
  } 
  valueref_deref = (char *) org_trifort_gc_deref(valueref);
  for(i = 0; i < count; ++i){
    curr_offset = 32 + (i * 4);
    printf("%c", valueref_deref[curr_offset]);
  }
  printf("\n");
}

$$__device__$$
void java_io_PrintStream_println0_(int thisref, int * exception){
  printf("\n");
}

$$__device__$$
void java_io_PrintStream_println0_1_(int thisref, int value, int * exception){
  printf("%d\n", value);
}

$$__device__$$
void java_io_PrintStream_println0_2_(int thisref, char value, int * exception){
  printf("%d\n", value);
}

$$__device__$$
void java_io_PrintStream_println0_3_(int thisref, char value, int * exception){
  printf("%c\n", value);
}

$$__device__$$
void java_io_PrintStream_println0_4_(int thisref, short value, int * exception){
  printf("%d\n", value);
}

$$__device__$$
void java_io_PrintStream_println0_5_(int thisref, int value, int * exception){
  printf("%d\n", value);
}

$$__device__$$
void java_io_PrintStream_println0_6_(int thisref, long long value, int * exception){
  printf("%lld\n", value);
}

$$__device__$$
void java_io_PrintStream_println0_7_(int thisref, float value, int * exception){
  printf("%e\n", value);
}

$$__device__$$
void java_io_PrintStream_println0_8_(int thisref, double value, int * exception){
  printf("%e\n", value);
}

$$__device__$$
void java_io_PrintStream_print0_9_(int thisref, int str_ret, int * exception){
  int valueref;
  int count;
  int i;
  int curr_offset;

  char * valueref_deref;

  valueref = org_trifort_rootbeer_get_string_char_array(str_ret, exception);  
  if(*exception != 0){
    return; 
  } 
  
  count = org_trifort_array_length(valueref, exception);
  if(*exception != 0){
    return; 
  } 
  valueref_deref = (char *) org_trifort_gc_deref(valueref);
  for(i = 0; i < count; ++i){
    curr_offset = 32 + (i * 4);
    printf("%c", valueref_deref[curr_offset]);
  }
}

$$__device__$$
void java_io_PrintStream_print0_11_(int thisref, int obj_ref, int * exception){
  int str_ref;
  int valueref;
  int count;
  int i;
  int curr_offset;
  char * valueref_deref;

  str_ref = java_lang_String_valueOf(obj_ref, exception);
  if(*exception != 0){
    return; 
  }
  
  valueref = org_trifort_rootbeer_get_string_char_array(str_ref, exception);  
  if(*exception != 0){
    return; 
  } 
  
  count = org_trifort_array_length(valueref, exception);
  if(*exception != 0){
    return; 
  } 
  valueref_deref = (char *) org_trifort_gc_deref(valueref);
  for(i = 0; i < count; ++i){
    curr_offset = 32 + (i * 4);
    printf("%c", valueref_deref[curr_offset]);
  }
}

$$__device__$$
void java_io_PrintStream_print0_1_(int thisref, int value, int * exception){
  printf("%d", value);
}

$$__device__$$
void java_io_PrintStream_print0_2_(int thisref, char value, int * exception){
  printf("%d", value);
}

$$__device__$$
void java_io_PrintStream_print0_3_(int thisref, char value, int * exception){
  printf("%c", value);
}

$$__device__$$
void java_io_PrintStream_print0_4_(int thisref, short value, int * exception){
  printf("%d", value);
}

$$__device__$$
void java_io_PrintStream_print0_5_(int thisref, int value, int * exception){
  printf("%d", value);
}

$$__device__$$
void java_io_PrintStream_print0_6_(int thisref, long long value, int * exception){
  printf("%lld", value);
}

$$__device__$$
void java_io_PrintStream_print0_7_(int thisref, float value, int * exception){
  printf("%e", value);
}

$$__device__$$
void java_io_PrintStream_print0_8_(int thisref, double value, int * exception){
  printf("%e", value);
}

$$__device__$$
int org_trifort_rootbeer_runtime_RootbeerAtomicInt_atomicAdd(int thisref, int value, int * exception){
  char * thisref_deref;
  int * array;

  thisref_deref = org_trifort_gc_deref(thisref) ;
  thisref_deref += 32;
  array = (int *) thisref_deref;
  return atomicAdd(array, value);
}

$$__device__$$
double org_trifort_rootbeer_runtime_RootbeerGpu_sin(double value, int * exception){
  return sin(value);
}

$$__device__$$ 
void org_trifort_rootbeer_runtime_RootbeerGpu_syncthreads(int * exception){
  org_trifort_syncthreads();
}

$$__device__$$ 
void org_trifort_rootbeer_runtime_RootbeerGpu_threadfence(int * exception){
  org_trifort_threadfence();
}

$$__device__$$ 
void org_trifort_rootbeer_runtime_RootbeerGpu_threadfenceBlock(int * exception){
  org_trifort_threadfence_block();
}

$$__device__$$
void org_trifort_rootbeer_runtime_RootbeerGpu_threadfenceSystem(int * exception){
  org_trifort_threadfence_system();
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

$$__device__$$ void
org_trifort_gc_set_to_space_address($$__global$$ char * value){
  org_trifort_setlong(TO_SPACE_OFFSET, (long long) value);
}

$$__device__$$ $$__global$$ long long *
org_trifort_gc_get_to_space_address(){
  long long value = org_trifort_getlong(TO_SPACE_OFFSET);
  return ($$__global$$ long long *) value;
}

$$__device__$$ long long
org_trifort_gc_get_to_space_free_ptr(){
  return org_trifort_getlong(TO_SPACE_FREE_POINTER_OFFSET);
}

$$__device__$$ void
org_trifort_gc_set_to_space_free_ptr(long long value){
  org_trifort_setlong(TO_SPACE_FREE_POINTER_OFFSET, value);
}

$$__device__$$ int
org_trifort_gc_get_space_size(){
  return org_trifort_getint(SPACE_SIZE_OFFSET);
}

$$__device__$$
int org_trifort_rootbeer_string_from_chars(int parameter0, int * exception) {
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
  org_trifort_gc_assign(&thisref, org_trifort_gc_malloc(48)); 
  if(thisref == -1) { 
    *exception = %%java_lang_OutOfMemoryError_TypeNumber%%; 
    return -1; 
  } 
  thisref_deref = org_trifort_gc_deref(thisref); 
  org_trifort_gc_set_count(thisref_deref, 1); 
  org_trifort_gc_set_color(thisref_deref, COLOR_GREY); 
  org_trifort_gc_set_type(thisref_deref, %%java_lang_String_TypeNumber%%); 
  org_trifort_gc_set_ctor_used(thisref_deref, 1); 
  org_trifort_gc_set_size(thisref_deref, 48); 
  org_trifort_gc_init_monitor(thisref_deref); 

  org_trifort_rootbeer_set_string_char_array(thisref, parameter0, exception);
  return thisref; 
}

//<java.lang.String: void <init>(java.lang.String)>
$$__device__$$
int java_lang_String_initab850b60f96d11de8a390800200c9a660_9_(int parameter0, int * exception) { 

  int i;
  int len;
  int characters_src;
  int characters_copy;
  char ch;
  
  characters_src = org_trifort_rootbeer_get_string_char_array(parameter0, exception);
  if(*exception != 0){
    return 0;
  }

  len = org_trifort_array_length(characters_src, exception);

  characters_copy = char__array_new(len, exception);
  for(i = 0; i < len; ++i){
    ch = char__array_get(characters_src, i, exception);
    char__array_set(characters_copy, i, ch, exception);
  }
  return org_trifort_rootbeer_string_from_chars(characters_copy, exception);
} 

//<java.lang.String: void <init>(char[])>
$$__device__$$
int java_lang_String_initab850b60f96d11de8a390800200c9a660_a12_(int parameter0, int * exception){

  int i;
  int len;
  int characters_src;
  int characters_copy;
  char ch;

  len = org_trifort_array_length(parameter0, exception);

  characters_copy = char__array_new(len, exception);
  for(i = 0; i < len; ++i){
    ch = char__array_get(parameter0, i, exception);
    char__array_set(characters_copy, i, ch, exception);
  }
  return org_trifort_rootbeer_string_from_chars(characters_copy, exception);
}

__device__ void java_lang_String_initab850b60f96d11de8a390800200c9a66_body0_a12_(int thisref, int parameter0, int * exception);

$$__device__$$ int 
char__array_new(int size, int * exception);

$$__device__$$ void 
char__array_set(int thisref, int parameter0, char parameter1, int * exception);

$$__device__$$ int
org_trifort_char_constant(char * str_constant, int * exception){
  int i;
  int len = org_trifort_strlen(str_constant);
  int characters = char__array_new(len, exception);
  for(i = 0; i < len; ++i){
    char__array_set(characters, i, str_constant[i], exception);
  }
  
  return characters;
}

$$__device__$$ int
org_trifort_string_constant(char * str_constant, int * exception){
  int characters;

  characters = org_trifort_char_constant(str_constant, exception);
  return org_trifort_rootbeer_string_from_chars(characters, exception);
}

$$__device__$$ void
org_trifort_gc_assign(int * lhs_ptr, int rhs){
  *lhs_ptr = rhs;
}

$$__device__$$ void
org_trifort_gc_assign_global($$__global$$ int * lhs_ptr, int rhs){
  *lhs_ptr = rhs;
}
 
$$__device__$$ int java_lang_StackTraceElement__array_get(int thisref, int parameter0, int * exception);
$$__device__$$ void java_lang_StackTraceElement__array_set(int thisref, int parameter0, int parameter1, int * exception);
$$__device__$$ int java_lang_StackTraceElement__array_new(int size, int * exception);
$$__device__$$ int java_lang_StackTraceElement_initab850b60f96d11de8a390800200c9a660_3_3_3_4_(int parameter0, int parameter1, int parameter2, int parameter3, int * exception);
$$__device__$$ void instance_setter_java_lang_RuntimeException_stackDepth(int thisref, int parameter0);
$$__device__$$ int instance_getter_java_lang_RuntimeException_stackDepth(int thisref);
$$__device__$$ int java_lang_StackTraceElement__array_get(int thisref, int parameter0, int * exception);
$$__device__$$ int instance_getter_java_lang_Throwable_stackTrace(int thisref, int * exception);
$$__device__$$ void instance_setter_java_lang_Throwable_stackTrace(int thisref, int parameter0, int * exception);

$$__device__$$ int java_lang_Throwable_fillInStackTrace(int thisref, int * exception){
  //int trace = java_lang_StackTraceElement__array_new(8, exception);
  //instance_setter_java_lang_Throwable_stackTrace(thisref, trace, exception);
  return thisref;
}

$$__device__$$ int java_lang_Throwable_getStackTraceElement(int thisref, int parameter0, int * exception){
  //int array = instance_getter_java_lang_Throwable_stackTrace(thisref, exception);
  //return java_lang_StackTraceElement__array_get(array, parameter0, exception);
  return -1;
}

$$__device__$$ int java_lang_Throwable_getStackTraceDepth(int thisref, int * exception){
  return 0;
}

$$__device__$$ void org_trifort_fillInStackTrace(int exception, char * class_name, char * method_name){
}

$$__device__$$ void instance_setter_java_lang_Throwable_cause(int thisref, int parameter0, int * exception);
$$__device__$$ void instance_setter_java_lang_Throwable_detailMessage(int thisref, int parameter0, int * exception);
$$__device__$$ void instance_setter_java_lang_Throwable_stackDepth(int thisref, int parameter0, int * exception);
$$__device__$$ void java_lang_VirtualMachineError_initab850b60f96d11de8a390800200c9a66_body0_(int thisref, int * exception);

$$__device__$$ int java_lang_OutOfMemoryError_initab850b60f96d11de8a390800200c9a66(int * exception){
  int r0 = -1;
  int thisref = org_trifort_gc_malloc(40);
  char * thisref_deref = org_trifort_gc_deref(thisref);

  //class info
  org_trifort_gc_set_count(thisref_deref, 0);
  org_trifort_gc_set_color(thisref_deref, COLOR_GREY);
  //TODO: get replacement string for this below 9
  org_trifort_gc_set_type(thisref_deref, 9);
  org_trifort_gc_set_ctor_used(thisref_deref, 1);
  org_trifort_gc_set_size(thisref_deref, 40);

  instance_setter_java_lang_Throwable_cause(thisref, -1, exception);
  instance_setter_java_lang_Throwable_detailMessage(thisref, -1, exception);
  instance_setter_java_lang_Throwable_stackTrace(thisref, -1, exception);

  //r0 := @this: java.lang.OutOfMemoryError
  org_trifort_gc_assign(& r0 ,  thisref );

  //specialinvoke r0.<java.lang.VirtualMachineError: void <init>()>()
  java_lang_VirtualMachineError_initab850b60f96d11de8a390800200c9a66_body0_(
   thisref, exception);
  return thisref;
}


$$__device__$$ int
java_lang_Object_hashCode(int thisref, int * exception){
  return thisref;
}

$$__device__$$ int
java_lang_Class_getName(int thisref , int * exception ) { 
  int $r1 =-1 ; 
  $r1 = instance_getter_java_lang_Class_name (thisref , exception ) ; 
  if ( * exception != 0 ) { 
    return 0 ; 
  } 
  return $r1;
}

$$__device__$$ int
java_lang_Object_getClass(int thisref, int * exception ) { 
  char * mem_loc = org_trifort_gc_deref(thisref);
  int type = org_trifort_gc_get_type(mem_loc);
  return org_trifort_classConstant(type);
}

$$__device__$$ int
java_lang_StringValue_from(int thisref, int * exception ) { 
  int i, size, new_ref;
  char * mem_loc, * new_mem_loc;
  
  mem_loc = org_trifort_gc_deref(thisref);
  size = org_trifort_gc_get_size(mem_loc);
  new_ref = org_trifort_gc_malloc(size);
  new_mem_loc = org_trifort_gc_deref(new_ref);
  
  for(i = 0; i < size; ++i){
    new_mem_loc[i] = mem_loc[i];  
  }
  
  return new_ref;
}

$$__device__$$ int
java_util_Arrays_copyOf(int object_array, int new_size, int * exception ){
  int ret;
  char * ret_deref;
  char * object_array_deref;
  int length;
  int i;
  
  ret = org_trifort_gc_malloc(32 + (4 * new_size));
  ret_deref = org_trifort_gc_deref(ret);
  object_array_deref = org_trifort_gc_deref(object_array);
    
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
int java_lang_StringBuilder_initab850b60f96d11de8a390800200c9a660_(int * exception){ 
  int thisref;
  char * thisref_deref;
  int chars;

  thisref = org_trifort_gc_malloc(48);
  if(thisref == -1){
    *exception = %%java_lang_OutOfMemoryError_TypeNumber%%; 
    return -1; 
  }

  thisref_deref = org_trifort_gc_deref(thisref);
  org_trifort_gc_set_count(thisref_deref, 1); 
  org_trifort_gc_set_color(thisref_deref, COLOR_GREY); 
  org_trifort_gc_set_type(thisref_deref, %%java_lang_StringBuilder_TypeNumber%%); 
  org_trifort_gc_set_ctor_used(thisref_deref, 1); 
  org_trifort_gc_set_size(thisref_deref, 48); 
  org_trifort_gc_init_monitor(thisref_deref); 

  chars = char__array_new(0, exception);
  instance_setter_java_lang_AbstractStringBuilder_value(thisref, chars, exception); 
  instance_setter_java_lang_AbstractStringBuilder_count(thisref, 0, exception);
  return thisref; 
}

//<java.lang.StringBuilder: java.lang.StringBuilder void(java.lang.String)>
$$__device__$$ 
int java_lang_StringBuilder_initab850b60f96d11de8a390800200c9a660_9_(
  int str ,int * exception){
 
  int thisref; 
  int str_value;
  int str_count;  

  char * thisref_deref; 
  thisref = -1;
  org_trifort_gc_assign (& thisref , org_trifort_gc_malloc (48 ) ) ; 
  if ( thisref ==-1 ) { 
    * exception = %%java_lang_OutOfMemoryError_TypeNumber%%; 
    return-1 ; 
  } 
  thisref_deref = org_trifort_gc_deref (thisref ) ; 
  org_trifort_gc_set_count ( thisref_deref , 0 ) ; 
  org_trifort_gc_set_color ( thisref_deref , COLOR_GREY ) ; 
  org_trifort_gc_set_type ( thisref_deref , %%java_lang_StringBuilder_TypeNumber%% ) ; 
  org_trifort_gc_set_ctor_used ( thisref_deref , 1 ) ; 
  org_trifort_gc_set_size ( thisref_deref , 44 ) ; 
  org_trifort_gc_init_monitor ( thisref_deref ) ; 

  str_value = org_trifort_rootbeer_get_string_char_array(str, exception);
  str_count = org_trifort_array_length(str_value, exception);

  instance_setter_java_lang_AbstractStringBuilder_value(thisref, str_value, exception); 
  instance_setter_java_lang_AbstractStringBuilder_count(thisref, str_count, exception); 
  return thisref; 
} 

//<java.lang.StringBuilder: java.lang.StringBuilder append(java.lang.String)>
$$__device__$$ 
int java_lang_StringBuilder_append10_9_(int thisref,
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
  sb_value = instance_getter_java_lang_AbstractStringBuilder_value(thisref,
    exception);

  sb_count = instance_getter_java_lang_AbstractStringBuilder_count(thisref,
    exception);

  //get string value and count
  str_value = org_trifort_rootbeer_get_string_char_array(parameter0, 
    exception);
    
  str_count = org_trifort_array_length(str_value, exception);

  new_count = sb_count + str_count;
  new_sb_value = char__array_new(new_count, exception);
  for(i = 0; i < sb_count; ++i){
    ch = char__array_get(sb_value, i, exception);
    char__array_set(new_sb_value, i, ch, exception);
  }
  for(i = 0; i < str_count; ++i){
    ch = char__array_get(str_value, i, exception);
    char__array_set(new_sb_value, sb_count + i, ch, exception);
  }

  instance_setter_java_lang_AbstractStringBuilder_value(thisref,
    new_sb_value, exception);

  instance_setter_java_lang_AbstractStringBuilder_count(thisref,
    new_count, exception);

  return thisref;
}

//<java.lang.StringBuilder: java.lang.StringBuilder append(boolean)>
$$__device__$$ 
int java_lang_StringBuilder_append10_1_(int thisref,
  bool parameter0, int * exception){
  
  int str = java_lang_Boolean_toString9_1_(parameter0, exception);
  return java_lang_StringBuilder_append10_9_(thisref, str, exception);
}

//<java.lang.StringBuilder: java.lang.StringBuilder append(char)>
$$__device__$$ 
int java_lang_StringBuilder_append10_3_(int thisref,
  int parameter0, int * exception){
  
  int str = java_lang_Character_toString9_3_(parameter0, exception);
  return java_lang_StringBuilder_append10_9_(thisref, str, exception);
}

//<java.lang.StringBuilder: java.lang.StringBuilder append(double)>
$$__device__$$ 
int java_lang_StringBuilder_append10_8_(int thisref,
  double parameter0, int * exception){
  
  int str = java_lang_Double_toString9_8_(parameter0, exception);
  return java_lang_StringBuilder_append10_9_(thisref, str, exception);
}

//<java.lang.StringBuilder: java.lang.StringBuilder append(float)>
$$__device__$$ 
int java_lang_StringBuilder_append10_7_(int thisref,
  float parameter0, int * exception){
  
  int str = java_lang_Float_toString9_7_(parameter0, exception);
  return java_lang_StringBuilder_append10_9_(thisref, str, exception);
}

//<java.lang.StringBuilder: java.lang.StringBuilder append(int)>
$$__device__$$ 
int java_lang_StringBuilder_append10_5_(int thisref,
  int parameter0, int * exception){

  int str = java_lang_Integer_toString9_5_(parameter0, exception);
  return java_lang_StringBuilder_append10_9_(thisref, str, exception);
}

//<java.lang.StringBuilder: java.lang.StringBuilder append(long)>
$$__device__$$ 
int java_lang_StringBuilder_append10_6_(int thisref,
  long long parameter0, int * exception){

  int str = java_lang_Long_toString9_6_(parameter0, exception);
  return java_lang_StringBuilder_append10_9_(thisref, str, exception);
}

//<java.lang.StringBuilder: java.lang.String toString()>
$$__device__$$ 
int java_lang_StringBuilder_toString9_(int thisref,
  int * exception){

  int value;
  int count;
  int new_chars; 
  int i;
  char c;

  value = instance_getter_java_lang_AbstractStringBuilder_value(thisref,
    exception);
  count = org_trifort_array_length(value, exception);
  new_chars = char__array_new(count, exception);

  for(i = 0; i < count; ++i){
    c = char__array_get(value, i, exception);
    char__array_set(new_chars, i, c, exception);
  }

  return org_trifort_rootbeer_string_from_chars(new_chars, exception);
}

//<java.lang.Integer: java.lang.Integer <init>(int)>
$$__device__$$
int java_lang_Integer_initab850b60f96d11de8a390800200c9a66(
  int int_value, int * exception){
  int thisref;
  char * thisref_deref;

  thisref = -1;
  thisref = org_trifort_gc_malloc(48);
  if ( thisref ==-1 ) { 
    * exception = %%java_lang_OutOfMemoryError_TypeNumber%%; 
    return-1 ; 
  }
  thisref_deref = org_trifort_gc_deref(thisref);
  org_trifort_gc_set_count(thisref_deref, 0);
  org_trifort_gc_set_color(thisref_deref, COLOR_GREY);
  org_trifort_gc_set_type(thisref_deref, %%java_lang_Integer_TypeNumber%%);
  org_trifort_gc_set_ctor_used(thisref_deref, 1);
  org_trifort_gc_set_size(thisref_deref, 48);
  org_trifort_gc_init_monitor(thisref_deref);

  instance_setter_java_lang_Integer_value(thisref, int_value, exception);
  return thisref;
}

//<java.lang.Integer: java.lang.Integer valueOf(int)>
$$__device__$$
int java_lang_Integer_valueOf(int int_value, int * exception) {
  int return_obj = -1;
  
  org_trifort_gc_assign(
    &return_obj, java_lang_Integer_initab850b60f96d11de8a390800200c9a66(
    int_value, exception));
  
  if(*exception != 0) {
    return 0; 
  }

  return return_obj;
}

// typeof_Boolean
$$__device__$$
bool at_illecker_typeof_Boolean(int thisref){
  char * thisref_deref;
  GC_OBJ_TYPE_TYPE type;
  if(thisref == -1){
    return false;
  }
  thisref_deref = org_trifort_gc_deref(thisref);
  type = org_trifort_gc_get_type(thisref_deref);
  if(type==%%java_lang_Boolean_TypeNumber%%) {
    return true;
  }
  return false;
}

// typeof_Integer
$$__device__$$
bool at_illecker_typeof_Integer(int thisref){
  char * thisref_deref;
  GC_OBJ_TYPE_TYPE type;
  if(thisref == -1){
    return false;
  }
  thisref_deref = org_trifort_gc_deref(thisref);
  type = org_trifort_gc_get_type(thisref_deref);
  if(type==%%java_lang_Integer_TypeNumber%%) {
    return true;
  }
  return false;
}

// typeof_Long
$$__device__$$
bool at_illecker_typeof_Long(int thisref){
  char * thisref_deref;
  GC_OBJ_TYPE_TYPE type;
  if(thisref == -1){
    return false;
  }
  thisref_deref = org_trifort_gc_deref(thisref);
  type = org_trifort_gc_get_type(thisref_deref);
  if(type==%%java_lang_Long_TypeNumber%%) {
    return true;
  }
  return false;
}

// typeof_Float
$$__device__$$
bool at_illecker_typeof_Float(int thisref){
  char * thisref_deref;
  GC_OBJ_TYPE_TYPE type;
  if(thisref == -1){
    return false;
  }
  thisref_deref = org_trifort_gc_deref(thisref);
  type = org_trifort_gc_get_type(thisref_deref);
  if(type==%%java_lang_Float_TypeNumber%%) {
    return true;
  }
  return false;
}

// typeof_Double
$$__device__$$
bool at_illecker_typeof_Double(int thisref){
  char * thisref_deref;
  GC_OBJ_TYPE_TYPE type;
  if(thisref == -1){
    return false;
  }
  thisref_deref = org_trifort_gc_deref(thisref);
  type = org_trifort_gc_get_type(thisref_deref);
  if(type==%%java_lang_Double_TypeNumber%%) {
    return true;
  }
  return false;
}

// typeof_String
$$__device__$$
bool at_illecker_typeof_String(int thisref){
  char * thisref_deref;
  GC_OBJ_TYPE_TYPE type;
  if(thisref == -1){
    return false;
  }
  thisref_deref = org_trifort_gc_deref(thisref);
  type = org_trifort_gc_get_type(thisref_deref);
  if(type==%%java_lang_String_TypeNumber%%) {
    return true;
  }
  return false;
}

//<java.lang.Object: java.lang.String toString()>
$$__device__$$
int java_lang_Object_toString9_(int this_ref, int * exception){
  int sb_ref = -1;
  int class_ref = -1;
  int class_name = -1;
  int hash_code = -1;
  int hex_string = -1;
  int ret_str = -1;
  
  org_trifort_gc_assign(
    &sb_ref, java_lang_StringBuilder_initab850b60f96d11de8a390800200c9a660_(exception));  
  if(*exception != 0) {
    return 0;
  }
  
  class_ref = java_lang_Object_getClass(this_ref, exception);
  if(*exception != 0) {
    return 0;
  }
  
  class_name  = java_lang_Class_getName(class_ref, exception);
  if(*exception != 0) {
    return 0;
  }
  
  sb_ref = java_lang_StringBuilder_append10_9_(sb_ref,  class_name, exception);
  if(*exception != 0) {
    return 0;
  }
  
  sb_ref = java_lang_StringBuilder_append10_9_(sb_ref,
    org_trifort_string_constant((char *) "@", exception), exception);
  if(*exception != 0) {
    return 0;
  }
  
  // TODO
  //hash_code = invoke_java_lang_Object_hashCode(this_ref, exception);
  //if(*exception != 0) {
  //  return 0;
  //}
  
  //hex_string = java_lang_Integer_toHexString9_5_(hash_code, exception);
  //if(*exception != 0) {
  //  return 0;
  //}
  
  //sb_ref = java_lang_StringBuilder_append10_9_(sb_ref,  hex_string, exception);
  //if(*exception != 0) {
  //  return 0;
  //}
  
  ret_str = java_lang_StringBuilder_toString9_(sb_ref, exception);
  if(*exception != 0) {
    return 0;
  }
  
  return ret_str;
}

//<java.lang.String: java.lang.String valueOf(java.lang.Object)>
$$__device__$$
int java_lang_String_valueOf(int obj_ref, int * exception) {
  int return_str = -1;
  char bool_val;

  if (obj_ref != -1) {
    
    // check type
    if (at_illecker_typeof_Boolean(obj_ref)) {
      bool_val = instance_getter_java_lang_Boolean_value(obj_ref, exception);
      if (bool_val == 0) {
        return_str = org_trifort_string_constant((char *) "false", exception);
      } else {
        return_str = org_trifort_string_constant((char *) "true", exception);
      }
    } else if (at_illecker_typeof_Integer(obj_ref)) {
      return_str = java_lang_Integer_toString9_5_(
        instance_getter_java_lang_Integer_value(obj_ref, exception), exception);
    } else if (at_illecker_typeof_Long(obj_ref)) {
      return_str = java_lang_Long_toString9_6_(
        instance_getter_java_lang_Long_value(obj_ref, exception), exception);
    } else if (at_illecker_typeof_Float(obj_ref)) {
      return_str = java_lang_Float_toString9_7_(
        instance_getter_java_lang_Float_value(obj_ref, exception), exception);
    } else if (at_illecker_typeof_Double(obj_ref)) {
      return_str = java_lang_Double_toString9_8_(
        instance_getter_java_lang_Double_value(obj_ref, exception), exception);
    } else if (at_illecker_typeof_String(obj_ref)) {
      return_str = obj_ref;
    } else {
      return_str = java_lang_Object_toString9_(obj_ref, exception);
    }
  } else {
    return_str = org_trifort_string_constant((char *) "null", exception);
  }
  
  return return_str;
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
int at_illecker_double_to_string(double fvalue, int max, int * exception) {
  int signvalue = 0;
  double ufvalue;
  long long intpart;
  long long fracpart;
  char iconvert[20];
  char fconvert[20];
  int iplace = 0;
  int fplace = 0;
  int zprelen = 0; // preceding zeros

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
  // multiplying by a factor of 10, this might remove preceding zeros
  fracpart = at_illecker_round(at_illecker_pow10(max) * (ufvalue - intpart));
  if (fracpart < 0) {
    fracpart = 0;
  }

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
  
  // Calculate preceding zeros
  // preceding zeros might be removed in fracpart
  zprelen = max - fplace;
  if (zprelen < 0) {
    zprelen = 0;
  }

  if (fplace == 20) {
    fplace--;
  }
  fconvert[fplace] = 0;

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

    // Add preceding zeros
    while (zprelen > 0) {
      at_illecker_set_char(buffer, &currlen, maxlen, '0');
      --zprelen;
    }

    // Add digits
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

  return org_trifort_string_constant(buffer, exception);
}

//<java.lang.Double: java.lang.String toString(double)>
$$__device__$$ 
int java_lang_Double_toString9_8_(double double_val, int * exception) {

  // Default is 6 digits after decimal point
  return at_illecker_double_to_string(double_val, 6, exception);
}

//<java.lang.Float: java.lang.String toString(float)>
$$__device__$$ 
int java_lang_Float_toString9_7_(float float_val, int * exception){

  // Default is 6 digits after decimal point
  return at_illecker_double_to_string((double)float_val, 6, exception);
}

// local long to string method
// http://www.opensource.apple.com/source/srm/srm-6/srm/lib/snprintf.c
$$__device__$$
int at_illecker_long_to_string(long long value, int max, int base, int * exception) {
  int signvalue = 0;
  unsigned long long uvalue;
  char convert[20];
  int place = 0;
  int zpadlen = 0; // lasting zeros

  char buffer[21];
  int maxlen = 21; // 20 digits + sign
  int currlen = 0;
  
  if (max < 0) {
    max = 0;
  }
  uvalue = value;

  // Set sign if negative
  if(value < 0) {
    signvalue = '-';
    uvalue = -value;
  }

  // Convert integer part
  do {
    convert[place++] = "0123456789abcdef"[uvalue % (unsigned)base];
    uvalue = (uvalue / (unsigned)base );
  } while(uvalue && (place < 20));

  if (place == 20) {
    place--;
  }
  convert[place] = 0;

  // Calc lasting zeros for padding
  zpadlen = max - place;
  if (zpadlen < 0) {
    zpadlen = 0;
  }

  // Set sign
  if (signvalue) {
    at_illecker_set_char(buffer, &currlen, maxlen, signvalue);
  }

  // Add digits
  while (place > 0)  {
    at_illecker_set_char(buffer, &currlen, maxlen, convert[--place]);
  }

  // Add lasting zeros
  while (zpadlen > 0) {
    at_illecker_set_char(buffer, &currlen, maxlen, '0');
    --zpadlen;
  }

  // Terminate string
  if (currlen < maxlen - 1) {
    buffer[currlen] = '\0';
  } else {
    buffer[maxlen - 1] = '\0';
  }

  return org_trifort_string_constant(buffer, exception);
}

//<java.lang.Long: java.lang.String toString(long)>
$$__device__$$ 
int java_lang_Long_toString9_6_(long long long_val, int * exception){
  return at_illecker_long_to_string(long_val, 0, 10, exception);
}

//<java.lang.Integer: java.lang.String toString(int)>
$$__device__$$ 
int java_lang_Integer_toString9_5_(int int_val, int * exception){
  return at_illecker_long_to_string((long long)int_val, 0, 10, exception);
}

// Returns the position of the first character of the first match.
// If no matches were found, the function returns -1
$$__device__$$
int at_illecker_strpos(int str_value, int str_count,
                       int sub_str_value, int sub_str_count,
                       int start_pos, int * exception) {
  
  if ( (str_count == 0) || (sub_str_count == 0) ||
      (start_pos > str_count)) {
    return -1;
  }
  
  for (int i = start_pos; i < str_count; i++) {
    if (char__array_get(str_value, i, exception) !=
        char__array_get(sub_str_value, 0, exception)) {
      continue;
    }
    int found_pos = i;
    int found_sub_string = true;
    for (int j = 1; j < sub_str_count; j++) {
      i++;
      if (char__array_get(str_value, i, exception) !=
          char__array_get(sub_str_value, j, exception)) {
        found_sub_string = false;
        break;
      }
    }
    if (found_sub_string) {
      return found_pos;
    }
  }
  return -1;
}

//<java.lang.String: int indexOf(java.lang.String)>
$$__device__$$
int java_lang_String_indexOf(int str_obj_ref,
                             int search_str_obj_ref, int * exception) {
  int str_value = 0;
  int str_count = 0;
  int search_str_value = 0;
  int search_str_count = 0;
  
  str_value = org_trifort_rootbeer_get_string_char_array(str_obj_ref, exception);
  str_count = org_trifort_array_length(str_value, exception);
  
  search_str_value = org_trifort_rootbeer_get_string_char_array(search_str_obj_ref, exception);
  search_str_count = org_trifort_array_length(search_str_value, exception);
  
  return at_illecker_strpos(str_value, str_count, search_str_value, search_str_count, 0, exception);
}

//<java.lang.String: int indexOf(java.lang.String, int fromIndex)>
$$__device__$$
int java_lang_String_indexOf(int str_obj_ref,
                             int search_str_obj_ref, int from_index, int * exception) {
  int str_value = 0;
  int str_count = 0;
  int search_str_value = 0;
  int search_str_count = 0;
  
  str_value = org_trifort_rootbeer_get_string_char_array(str_obj_ref, exception);
  str_count = org_trifort_array_length(str_value, exception);
  
  search_str_value = org_trifort_rootbeer_get_string_char_array(search_str_obj_ref, exception);
  search_str_count = org_trifort_array_length(search_str_value, exception);
  
  return at_illecker_strpos(str_value, str_count, search_str_value, search_str_count, from_index, exception);
}

// Returns a substring from given start index
$$__device__$$
int at_illecker_substring(int str_value, int str_count,
                          int begin_index, int end_index, int * exception) {
  int new_length = 0;
  int new_string = -1;
  
  // set new length
  if (end_index == -1) { // copy to end
    new_length = str_count - begin_index;
  } else {
    if (end_index < str_count) {
      new_length = end_index - begin_index;
    } else {
      new_length = str_count - begin_index;
    }
  }
  
  new_string = char__array_new(new_length, exception);
  
  for(int i = 0; i < new_length; i++) {
    char__array_set(new_string, i, char__array_get(str_value, begin_index, exception), exception);
    begin_index++;
  }
  
  return org_trifort_rootbeer_string_from_chars(new_string, exception);
}

//<java.lang.String: java.lang.String substring(int)>
$$__device__$$
int java_lang_String_substring(int str_obj_ref, int begin_index, int * exception) {
  int str_value = 0;
  int str_count = 0;
  
  str_value = org_trifort_rootbeer_get_string_char_array(str_obj_ref, exception);
  str_count = org_trifort_array_length(str_value, exception);
  
  return at_illecker_substring(str_value, str_count, begin_index, -1, exception);
}

//<java.lang.String: java.lang.String substring(int,int)>
$$__device__$$
int java_lang_String_substring(int str_obj_ref, int begin_index,
                               int end_index, int * exception) {
  int str_value = 0;
  int str_count = 0;
  
  str_value = org_trifort_rootbeer_get_string_char_array(str_obj_ref, exception);
  str_count = org_trifort_array_length(str_value, exception);
  
  return at_illecker_substring(str_value, str_count, begin_index, end_index, exception);
}

// Returns the amount of occurrences of substring in string
// If no matches were found, the function returns 0
$$__device__$$
int at_illecker_strcnt(int str_value, int str_count,
                       int sub_str_value, int sub_str_count, int * exception) {
  int occurrences = 0;
  
  if ( (str_count == 0) || (sub_str_count == 0) ) {
    return 0;
  }
  
  for (int i = 0; i < str_count; i++) {
    if (char__array_get(str_value, i, exception) !=
        char__array_get(sub_str_value, 0, exception)) {
      continue;
    }
    bool found_sub_string = true;
    for (int j = 1; j < sub_str_count; j++) {
      i++;
      if (char__array_get(str_value, i, exception) !=
          char__array_get(sub_str_value, j, exception)) {
        found_sub_string = false;
        break;
      }
    }
    if (found_sub_string) {
      occurrences++;
    }
  }
  return occurrences;
}

// local split method
$$__device__$$
int at_illecker_split(int str_obj_ref, int delim_str_obj_ref,
                      int limit, int * exception) {
  int return_obj = -1;
  int start = 0;
  int end = 0;
  int str_value = 0;
  int str_count = 0;
  int delim_str_value = 0;
  int delim_str_count = 0;
  int delim_occurrences = 0;
  
  str_value = org_trifort_rootbeer_get_string_char_array(str_obj_ref, exception);
  str_count = org_trifort_array_length(str_value, exception);
  
  delim_str_value = org_trifort_rootbeer_get_string_char_array(delim_str_obj_ref, exception);
  delim_str_count = org_trifort_array_length(delim_str_value, exception);
  
  // count delimiters, needed for array size
  delim_occurrences = at_illecker_strcnt(str_value, str_count,
                                         delim_str_value, delim_str_count, exception);
  
  if ( (limit <= 0) || (limit > delim_occurrences) ) {
    return_obj = java_lang_String__array_new(delim_occurrences + 1, exception);
    limit = delim_occurrences + 1;
  } else {
    return_obj = java_lang_String__array_new(limit, exception);
  }
  
  if (delim_occurrences == 0) {
    // return this string
    java_lang_String__array_set(return_obj, 0, str_obj_ref, exception);
    
  } else {
    
    // parse string for tokens
    for (int i = 0; i < limit - 1; i++) {
      end = at_illecker_strpos(str_value, str_count,
                               delim_str_value, delim_str_count, start, exception);
      
      if (end == -1) {
        break;
      }
      
      // add token - substring(start, end - start)
      java_lang_String__array_set(return_obj, i,
                                  at_illecker_substring(str_value, str_count, start, end, exception), exception);
      
      // Exclude the delimiter in the next search
      start = end + delim_str_count;
    }
    
    // add last token
    if (end != -1) {
      // substring(start, END_OF_STRING)
      java_lang_String__array_set(return_obj, limit - 1,
                                  at_illecker_substring(str_value, str_count, start, -1, exception), exception);
    }
  }
  return return_obj;
}

//<java.lang.String: java.lang.String[] split(java.lang.String,int)>
$$__device__$$
int java_lang_String_split(int str_obj_ref, int delim_str_obj_ref, int limit, int * exception) {
  return at_illecker_split(str_obj_ref, delim_str_obj_ref, limit, exception);
}

//<java.lang.String: java.lang.String[] split(java.lang.String)>
$$__device__$$
int java_lang_String_split(int str_obj_ref, int delim_str_obj_ref, int * exception) {
  return at_illecker_split(str_obj_ref, delim_str_obj_ref, 0, exception);
}

$$__device__$$ int 
java_lang_Object_clone(int thisref, int * exception){
  char * src_deref;
  char * dest_deref;
  int dest;
  int size;
  
  if(thisref == -1){
    *exception = %%java_lang_NullPointerException_TypeNumber%%;
    return 0;
  }
  
  src_deref = org_trifort_gc_deref(thisref);
  size = org_trifort_getint(src_deref, OBJECT_HEADER_POSITION_OBJECT_SIZE);
  dest = org_trifort_gc_malloc(size);
  if(dest == -1){
    *exception = %%java_lang_OutOfMemoryError_TypeNumber%%;
    return 0;
  }
  dest_deref = org_trifort_gc_deref(dest);
  org_trifort_gc_memcpy(dest_deref, src_deref, size);
  org_trifort_gc_set_ctor_used(dest_deref, 1);
  return dest;
}

$$__device__$$
bool at_illecker_is_digit(unsigned char c) {
  return ((c)>='0' && (c)<='9');
}

$$__device__$$
bool at_illecker_is_space(unsigned char c) {
  return ((c)==' ' || (c)=='\f' || (c)=='\n' || (c)=='\r' || (c)=='\t' || (c)=='\v');
}

// local string to unsigned long method
// http://www.opensource.apple.com/source/tcl/tcl-14/tcl/compat/strtoul.c
// Argument1: String of ASCII digits, possibly preceded by white space.
// Argument2: Where to store address of terminating character, or NULL.
// Argument3: Base for conversion.  Must be less than 37.
// If 0, then the base is chosen from the leading characters of string:
// "0x" means hex, "0" means octal, anything else means decimal.
$$__device__$$
unsigned long long at_illecker_strtoul(const char *string, char **end_ptr, int base) {
  register const char *p;
  register unsigned long long result = 0;
  register unsigned digit;
  int anyDigits = 0;
  int negative=0;
  int overflow=0;
  
  char cvtIn[] = {
    0, 1, 2, 3, 4, 5, 6, 7, 8, 9,		/* '0' - '9' */
    100, 100, 100, 100, 100, 100, 100,		/* punctuation */
    10, 11, 12, 13, 14, 15, 16, 17, 18, 19,	/* 'A' - 'Z' */
    20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
    30, 31, 32, 33, 34, 35,
    100, 100, 100, 100, 100, 100,		/* punctuation */
    10, 11, 12, 13, 14, 15, 16, 17, 18, 19,	/* 'a' - 'z' */
    20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
    30, 31, 32, 33, 34, 35
  };
  
  // Skip any leading blanks.
  p = string;
  while (at_illecker_is_space((unsigned char) (*p))) {
    p += 1;
  }
  // Check for a sign.
  if (*p == '-') {
    negative = 1;
    p += 1;
  } else {
    if (*p == '+') {
      p += 1;
    }
  }
  
  // If no base was provided, pick one from the leading characters
  // of the string.
  if (base == 0) {
    if (*p == '0') {
      p += 1;
      if ((*p == 'x') || (*p == 'X')) {
        p += 1;
        base = 16;
      } else {
        // Must set anyDigits here, otherwise "0" produces a
        // "no digits" error.
        anyDigits = 1;
        base = 8;
      }
    } else {
      base = 10;
    }
  } else if (base == 16) {
    // Skip a leading "0x" from hex numbers.
    if ((p[0] == '0') && ((p[1] == 'x') || (p[1] == 'X'))) {
      p += 2;
    }
  }
  
  // Sorry this code is so messy, but speed seems important. Do
  // different things for base 8, 10, 16, and other.
  if (base == 8) {
    unsigned long long maxres = 0xFFFFFFFFUL >> 3; // ULONG_MAX = 0xFFFFFFFFUL
    for ( ; ; p += 1) {
      digit = *p - '0';
      if (digit > 7) {
        break;
      }
      if (result > maxres) {
        overflow = 1;
      }
      result = (result << 3);
      if (digit > (0xFFFFFFFFUL - result)) {
        overflow = 1;
      }
      result += digit;
      anyDigits = 1;
    }
  } else if (base == 10) {
    unsigned long long maxres = 0xFFFFFFFFUL / 10; // ULONG_MAX = 0xFFFFFFFFUL
    for ( ; ; p += 1) {
      digit = *p - '0';
      if (digit > 9) {
        break;
      }
      if (result > maxres) {
        overflow = 1;
      }
      result *= 10;
      if (digit > (0xFFFFFFFFUL - result)) {
        overflow = 1;
      }
      result += digit;
      anyDigits = 1;
    }
  } else if (base == 16) {
    unsigned long long maxres = 0xFFFFFFFFUL >> 4;
    for ( ; ; p += 1) {
      digit = *p - '0';
      if (digit > ('z' - '0')) {
        break;
      }
      digit = cvtIn[digit];
      if (digit > 15) {
        break;
      }
      if (result > maxres) {
        overflow = 1;
      }
      result = (result << 4);
      if (digit > (0xFFFFFFFFUL - result)) {
        overflow = 1;
      }
      result += digit;
      anyDigits = 1;
    }
  } else if ( base >= 2 && base <= 36 ) {
    unsigned long long maxres = 0xFFFFFFFFUL / base;
    for ( ; ; p += 1) {
      digit = *p - '0';
      if (digit > ('z' - '0')) {
        break;
      }
      digit = cvtIn[digit];
      if (digit >= ( (unsigned) base )) {
        break;
      }
      if (result > maxres) {
        overflow = 1;
      }
      result *= base;
      if (digit > (0xFFFFFFFFUL - result)) {
        overflow = 1;
      }
      result += digit;
      anyDigits = 1;
    }
  }
  
  // See if there were any digits at all.
  if (!anyDigits) {
    p = string;
  }
  
  if (end_ptr != 0) {
    /* unsafe, but required by the strtoul prototype */
    *end_ptr = (char *) p;
  }
  
  if (overflow) {
    return 0xFFFFFFFFUL;
  }
  
  if (negative) {
    return -result;
  }
  return result;
}

// local string to long method
// http://www.opensource.apple.com/source/tcl/tcl-14/tcl/compat/strtol.c
// Argument1: String of ASCII digits, possibly preceded by white space.
// Argument2: Where to store address of terminating character, or NULL.
// Argument3: Base for conversion.  Must be less than 37.
// If 0, then the base is chosen from the leading characters of string:
// "0x" means hex, "0" means octal, anything else means decimal.
$$__device__$$
long long at_illecker_strtol(const char *string, char **end_ptr, int base) {
  register const char *p;
  long long result;
  
  // Skip any leading blanks.
  p = string;
  while (at_illecker_is_space((unsigned char) (*p))) {
    p += 1;
  }
  // Check for a sign.
  if (*p == '-') {
    p += 1;
    result = -(at_illecker_strtoul(p, end_ptr, base));
  } else {
    if (*p == '+') {
      p += 1;
    }
    result = at_illecker_strtoul(p, end_ptr, base);
  }
  if ((result == 0) && (end_ptr != 0) && (*end_ptr == p)) {
    *end_ptr = (char *) string;
  }
  return result;
}

// local string to double method
// http://www.opensource.apple.com/source/tcl/tcl-14/tcl/compat/strtod.c
$$__device__$$
double at_illecker_strtod(const char *string) {
  int sign = 0; // FALSE
  int expSign = 0; // FALSE
  double fraction, dblExp, *d;
  register const char *p;
  register int c;
  int exp = 0;
  int fracExp = 0;
  int mantSize;
  int decPt;
  const char *pExp;
  
  int maxExponent = 511;
  double powersOf10[] = {
    10.,
    100.,
    1.0e4,
    1.0e8,
    1.0e16,
    1.0e32,
    1.0e64,
    1.0e128,
    1.0e256
  };
  
  // Strip off leading blanks and check for a sign.
  p = string;
  while (at_illecker_is_space((unsigned char) (*p))) {
    p += 1;
  }
  
  if (*p == '-') {
    sign = 1; // TRUE
    p += 1;
  } else {
    if (*p == '+') {
      p += 1;
    }
    sign = 0; // FALSE
  }
  
  // Count the number of digits in the mantissa (including the decimal
  // point), and also locate the decimal point.
  decPt = -1;
  for (mantSize = 0; ; mantSize += 1) {
    c = *p;
    if (!at_illecker_is_digit(c)) {
      if ((c != '.') || (decPt >= 0)) {
        break;
      }
      decPt = mantSize;
    }
    p += 1;
  }
  
  // Now suck up the digits in the mantissa.  Use two integers to
  // collect 9 digits each (this is faster than using floating-point).
  // If the mantissa has more than 18 digits, ignore the extras, since
  // they can't affect the value anyway.
  pExp  = p;
  p -= mantSize;
  if (decPt < 0) {
    decPt = mantSize;
  } else {
    mantSize -= 1;
  }
  if (mantSize > 18) {
    fracExp = decPt - 18;
    mantSize = 18;
  } else {
    fracExp = decPt - mantSize;
  }
  if (mantSize == 0) {
    fraction = 0.0;
    p = string;
    goto done;
  } else {
    int frac1, frac2;
    frac1 = 0;
    for ( ; mantSize > 9; mantSize -= 1) {
      c = *p;
      p += 1;
      if (c == '.') {
        c = *p;
        p += 1;
      }
      frac1 = 10*frac1 + (c - '0');
    }
    frac2 = 0;
    for (; mantSize > 0; mantSize -= 1) {
      c = *p;
      p += 1;
      if (c == '.') {
        c = *p;
        p += 1;
      }
      frac2 = 10*frac2 + (c - '0');
    }
    fraction = (1.0e9 * frac1) + frac2;
  }
  
  // Skim off the exponent.
  p = pExp;
  if ((*p == 'E') || (*p == 'e')) {
    p += 1;
    if (*p == '-') {
      expSign = 1; // TRUE
      p += 1;
    } else {
      if (*p == '+') {
        p += 1;
      }
      expSign = 0; // FALSE
    }
    if (!at_illecker_is_digit((unsigned char) (*p))) {
      p = pExp;
      goto done;
    }
    while (at_illecker_is_digit((unsigned char) (*p))) {
      exp = exp * 10 + (*p - '0');
      p += 1;
    }
  }
  if (expSign) {
    exp = fracExp - exp;
  } else {
    exp = fracExp + exp;
  }
  
  // Generate a floating-point number that represents the exponent.
  // Do this by processing the exponent one bit at a time to combine
  // many powers of 2 of 10. Then combine the exponent with the
  // fraction.
  if (exp < 0) {
    expSign = 1; // TRUE
    exp = -exp;
  } else {
    expSign = 0; // FALSE
  }
  if (exp > maxExponent) {
    exp = maxExponent;
    // errno = ERANGE;
  }
  dblExp = 1.0;
  for (d = powersOf10; exp != 0; exp >>= 1, d += 1) {
    if (exp & 01) {
      dblExp *= *d;
    }
  }
  if (expSign) {
    fraction /= dblExp;
  } else {
    fraction *= dblExp;
  }
  
done:
  if (sign) {
    return -fraction;
  }
  return fraction;
}

//<java.lang.Long: long parseLong(java.lang.String)>
$$__device__$$
long long java_lang_Long_parseLong(int str_obj_ref, int * exception) {
  int str_value = 0;
  int str_count = 0;
  char str_val[255]; // max len of 255
  
  str_value = org_trifort_rootbeer_get_string_char_array(str_obj_ref, exception);
  str_count = org_trifort_array_length(str_value, exception);
  
  // Check if str_count > 255, then truncate
  if (str_count > 255) {
    str_count = 255;
  }
  
  // Convert string to char[]
  for(int i = 0; i < str_count; i++){
    str_val[i] = char__array_get(str_value, i, exception);
  }
  str_val[str_count] = '\0';
  
  return at_illecker_strtol(str_val, 0, 0);
}

//<java.lang.Integer: int parseInt(java.lang.String)>
$$__device__$$
int java_lang_Integer_parseInt(int str_obj_ref, int * exception) {
  return java_lang_Long_parseLong(str_obj_ref, exception);
}

//<java.lang.Double: double parseDouble(java.lang.String)>
$$__device__$$
double java_lang_Double_parseDouble(int str_obj_ref, int * exception) {
  int str_value = 0;
  int str_count = 0;
  char str_val[255]; // max len of 255
  
  str_value = org_trifort_rootbeer_get_string_char_array(str_obj_ref, exception);
  str_count = org_trifort_array_length(str_value, exception);
  
  // Check if str_count > 255, then truncate
  if (str_count > 255) {
    str_count = 255;
  }
  
  // Convert string to char[]
  for(int i = 0; i < str_count; i++){
    str_val[i] = char__array_get(str_value, i, exception);
  }
  str_val[str_count] = '\0';
  
  return at_illecker_strtod(str_val);
}

//<java.lang.Float: float parseFloat(java.lang.String)>
$$__device__$$
float java_lang_Float_parseFloat(int str_obj_ref, int * exception) {
  return java_lang_Double_parseDouble(str_obj_ref, exception);
}

$$__device__$$ 
int org_trifort_rootbeer_testcases_rootbeertest_serialization_ForceArrayNewRunOnGpu_getStringArray9_(int thisref, int * exception){
  int r0 = -1;
  int r1 = -1;
  r0  =  thisref ;
  r1  = java_lang_String__array_new(1 , exception);

  java_lang_String__array_set(r1, 0,  org_trifort_string_constant((char *) "testForceArrayNew", exception) , exception);
  if(*exception != 0) {
    return 0; 
  }
  return java_lang_String__array_get(r1, 0, exception);
}
