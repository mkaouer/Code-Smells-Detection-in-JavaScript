#include "edu_syr_pcpratts_rootbeer_runtime_nativecpu_NativeCpuDevice.h"

#if (defined linux || defined __APPLE_CC__)
  #include <dlfcn.h>
#else
  #include <Windows.h>
#endif

#include <stdlib.h>
#include <stdio.h>

jbyteArray array_get(JNIEnv * env, jobject array, int index){
   jmethodID mid;
   jclass list_interface = (*env)->FindClass(env, "java/util/List");
   mid = (*env)->GetMethodID(env, list_interface, "get", "(I)Ljava/lang/Object;");
   return (*env)->CallObjectMethod(env, array, mid, index);
}

/*
 * Class:     edu_syr_pcpratts_rootbeer_runtime_nativecpu_NativeCpuDevice
 * Method:    runOnCpu
 * Signature: (Ledu/syr/pcpratts/rootbeer/runtime/memory/Memory;Ledu/syr/pcpratts/rootbeer/runtime/memory/Memory;Ledu/syr/pcpratts/rootbeer/runtime/memory/Memory;Ledu/syr/pcpratts/rootbeer/runtime/memory/Memory;)V
 */
JNIEXPORT void JNICALL Java_edu_syr_pcpratts_rootbeer_runtime_nativecpu_NativeCpuDevice_runOnCpu
  (JNIEnv * env, jobject this_ptr, jobject to_space_array, jint to_space_count, 
   jbyteArray handles, jbyteArray heap_end_ptr, jbyteArray gc_info, jbyteArray exceptions, jintArray java_lang_class_refs, 
   jint num_threads, jint block_shape, jint thread_shape, jstring lib_name){

  int i;
  jbyte * nhandles;
  jbyte * nheap_end_ptr;
  jbyte * ngc_info;
  jbyte * nexceptions;
  jbyte * nclass_refs;
  char * str;
  jlong * to_space;
  jbyteArray curr_to_space;

  void (*entry)(char * gc_info_space, jlong * to_space, jlong * handles, 
    jlong * to_space_free_ptr, jlong * exceptions, jint * class_refs, 
    jlong space_size, int num_threads, int block_shape, int thread_shape);
  
#if (defined linux || defined __APPLE_CC__)  
  void * lib_handle;
#else
  HMODULE lib_handle;
#endif

  nhandles = (*env)->GetByteArrayElements(env, handles, JNI_FALSE);
  nheap_end_ptr = (*env)->GetByteArrayElements(env, heap_end_ptr, JNI_FALSE);
  ngc_info = (*env)->GetByteArrayElements(env, gc_info, JNI_FALSE);
  nexceptions = (*env)->GetByteArrayElements(env, exceptions, JNI_FALSE);
  nclass_refs = (jbyte *) (*env)->GetIntArrayElements(env, java_lang_class_refs, JNI_FALSE);

  str = (char *) (*env)->GetStringUTFChars(env, lib_name, NULL);

#if (defined linux || defined __APPLE_CC__)  
  lib_handle = dlopen(str, RTLD_LAZY);
#else
  lib_handle = LoadLibrary(str);
#endif

  (*env)->ReleaseStringUTFChars(env, lib_name, str);

  to_space = (jlong *) malloc(sizeof(jlong *) * to_space_count);
  for(i = 0; i < to_space_count; ++i){
    curr_to_space = array_get(env, to_space_array, i);
    to_space[i] = (jlong) (*env)->GetByteArrayElements(env, curr_to_space, JNI_FALSE);
  }
  
#if (defined linux || defined __APPLE_CC__)  
  entry = dlsym(lib_handle, "entry");
#else
  entry = GetProcAddress(lib_handle, "entry");
#endif
  
  (*entry)((char *) ngc_info, to_space, (jlong *) nhandles, (jlong *) nheap_end_ptr,
    (jlong *) nexceptions, (jint *) nclass_refs, 100*1024*1024, num_threads,
    block_shape, thread_shape);  

#if (defined linux || defined __APPLE_CC__)  
  dlclose(lib_handle);
#else
  FreeLibrary(lib_handle);
#endif

  for(i = 0; i < to_space_count; ++i){
    curr_to_space = array_get(env, to_space_array, i);
    (*env)->ReleaseByteArrayElements(env, curr_to_space, (jbyte *) to_space[i], 0);
  }
  
  (*env)->ReleaseByteArrayElements(env, handles, nhandles, 0);
  (*env)->ReleaseByteArrayElements(env, heap_end_ptr, nheap_end_ptr, 0);
  (*env)->ReleaseByteArrayElements(env, gc_info, ngc_info, 0);
  (*env)->ReleaseByteArrayElements(env, exceptions, nexceptions, 0);
  (*env)->ReleaseIntArrayElements(env, java_lang_class_refs, nclass_refs, 0);

  free(to_space);
}
