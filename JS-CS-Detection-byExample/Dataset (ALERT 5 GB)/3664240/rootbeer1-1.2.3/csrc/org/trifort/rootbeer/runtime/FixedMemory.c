#include "FixedMemory.h"
#include <cuda.h>

/*
 * Class:     org_trifort_rootbeer_runtime_FixedMemory
 * Method:    readByte
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doReadByte
  (JNIEnv *env, jobject this_obj, jlong ptr, jlong cpu_base){

  char * charHostMemory = (char *) cpu_base;
  jbyte ret = charHostMemory[ptr];
  return ret;
}

/*
 * Class:     org_trifort_rootbeer_runtime_FixedMemory
 * Method:    readBoolean
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doReadBoolean
  (JNIEnv *env, jobject this_obj, jlong ptr, jlong cpu_base){

  char * charHostMemory = (char *) cpu_base;
  jboolean ret = charHostMemory[ptr];
  return ret;
}

/*
 * Class:     org_trifort_rootbeer_runtime_FixedMemory
 * Method:    readShort
 * Signature: ()S
 */
JNIEXPORT jshort JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doReadShort
  (JNIEnv *env, jobject this_obj, jlong ptr, jlong cpu_base){

  short * shortHostMemory = (short *) cpu_base;
  jshort ret = shortHostMemory[ptr / 2];

  return ret;
}

/*
 * Class:     org_trifort_rootbeer_runtime_FixedMemory
 * Method:    readInt
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doReadInt
  (JNIEnv *env, jobject this_obj, jlong ptr, jlong cpu_base){

  int * intHostMemory = (int *) cpu_base;
  jint ret = intHostMemory[ptr / 4];

  return ret;
}

/*
 * Class:     org_trifort_rootbeer_runtime_FixedMemory
 * Method:    readFloat
 * Signature: ()F
 */
JNIEXPORT jfloat JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doReadFloat
  (JNIEnv *env, jobject this_obj, jlong ptr, jlong cpu_base){

  float * floatHostMemory = (float *) cpu_base;
  jfloat ret = floatHostMemory[ptr / 4];

  return ret;
}

/*
 * Class:     org_trifort_rootbeer_runtime_FixedMemory
 * Method:    readDouble
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doReadDouble
  (JNIEnv *env, jobject this_obj, jlong ptr, jlong cpu_base){

  double * doubleHostMemory = (double *) cpu_base;
  jdouble ret = doubleHostMemory[ptr / 8];

  return ret;
}

/*
 * Class:     org_trifort_rootbeer_runtime_FixedMemory
 * Method:    readLong
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doReadLong
  (JNIEnv *env, jobject this_obj, jlong ptr, jlong cpu_base){

  jlong * longHostMemory = (jlong *) cpu_base;
  jlong ret = longHostMemory[ptr / 8];

  return ret;
}

/*
 * Class:     org_trifort_rootbeer_runtime_FixedMemory
 * Method:    writeByte
 * Signature: (B)V
 */
JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doWriteByte
  (JNIEnv *env, jobject this_obj, jlong ptr, jbyte value, jlong cpu_base){

  char * charHostMemory = (char *) cpu_base;
  charHostMemory[ptr] = value;
}

/*
 * Class:     org_trifort_rootbeer_runtime_FixedMemory
 * Method:    writeBoolean
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doWriteBoolean
  (JNIEnv *env, jobject this_obj, jlong ptr, jboolean value, jlong cpu_base){

  char * charHostMemory = (char *) cpu_base;
  charHostMemory[ptr] = value;
}

/*
 * Class:     org_trifort_rootbeer_runtime_FixedMemory
 * Method:    writeShort
 * Signature: (S)V
 */
JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doWriteShort
  (JNIEnv *env, jobject this_obj, jlong ptr, jshort value, jlong cpu_base){

  short * shortHostMemory = (short *) cpu_base;
  shortHostMemory[ptr / 2] = value;
}

/*
 * Class:     org_trifort_rootbeer_runtime_FixedMemory
 * Method:    writeInt
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doWriteInt
  (JNIEnv *env, jobject this_obj, jlong ptr, jint value, jlong cpu_base){

  int * intHostMemory = (int *) cpu_base;
  intHostMemory[ptr / 4] = value;
}

/*
 * Class:     org_trifort_rootbeer_runtime_FixedMemory
 * Method:    writeFloat
 * Signature: (F)V
 */
JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doWriteFloat
  (JNIEnv *env, jobject this_obj, jlong ptr, jfloat value, jlong cpu_base){

  float * floatHostMemory = (float *) cpu_base;
  floatHostMemory[ptr / 4] = value;
}

/*
 * Class:     org_trifort_rootbeer_runtime_FixedMemory
 * Method:    writeDouble
 * Signature: (D)V
 */
JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doWriteDouble
  (JNIEnv *env, jobject this_obj, jlong ptr, jdouble value, jlong cpu_base){

  double * doubleHostMemory = (double *) cpu_base;
  doubleHostMemory[ptr / 8] = value;
}

/*
 * Class:     org_trifort_rootbeer_runtime_FixedMemory
 * Method:    writeLong
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doWriteLong
  (JNIEnv *env, jobject this_obj, jlong ptr, jlong value, jlong cpu_base){

  jlong * longHostMemory = (jlong *) cpu_base;
  longHostMemory[ptr / 8] = value;
}

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doWriteByteArray
  (JNIEnv *env, jobject this_obj, jbyteArray array, jlong ref, jint start, jint len){
  jbyte * dest = (jbyte *) (ref + start);
  (*env)->GetByteArrayRegion(env, array, start, len, dest);
}

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doWriteBooleanArray
  (JNIEnv *env, jobject this_obj, jbooleanArray array, jlong ref, jint start, jint len){
  jboolean * dest = (jboolean *) (ref + start);
  (*env)->GetBooleanArrayRegion(env, array, start, len, dest);
}

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doWriteShortArray
  (JNIEnv *env, jobject this_obj, jshortArray array, jlong ref, jint start, jint len){
  jshort * dest = (jshort *) (ref + start);
  (*env)->GetShortArrayRegion(env, array, start, len, dest);
}

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doWriteIntArray
  (JNIEnv *env, jobject this_obj, jintArray array, jlong ref, jint start, jint len){
  int * dest = (int *) (ref + start);
  (*env)->GetIntArrayRegion(env, array, start, len, dest);
}

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doWriteFloatArray
  (JNIEnv *env, jobject this_obj, jfloatArray array, jlong ref, jint start, jint len){
  
  float * dest = (float *) (ref + start);
  (*env)->GetFloatArrayRegion(env, array, start, len, dest);
}

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doWriteDoubleArray
  (JNIEnv *env, jobject this_obj, jdoubleArray array, jlong ref, jint start, jint len){
  jdouble * dest = (jdouble *) (ref + start);
  (*env)->GetDoubleArrayRegion(env, array, start, len, dest);
}

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doWriteLongArray
  (JNIEnv *env, jobject this_obj, jlongArray array, jlong ref, jint start, jint len){
  jlong * dest = (jlong *) (ref + start);
  (*env)->GetLongArrayRegion(env, array, start, len, dest);
}

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doReadByteArray
  (JNIEnv *env, jobject this_obj, jbyteArray array, jlong ref, jint start, jint len){

  jbyte * dest = (jbyte *) (ref + start);
  (*env)->SetByteArrayRegion(env, array, start, len, dest);
}

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doReadBooleanArray
  (JNIEnv *env, jobject this_obj, jbooleanArray array, jlong ref, jint start, jint len){

  jboolean * dest = (jboolean *) (ref + start);
  (*env)->SetBooleanArrayRegion(env, array, start, len, dest);
}

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doReadShortArray
  (JNIEnv *env, jobject this_obj, jshortArray array, jlong ref, jint start, jint len){

  jshort * dest = (jshort *) (ref + start);
  (*env)->SetShortArrayRegion(env, array, start, len, dest);
}

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doReadIntArray
  (JNIEnv *env, jobject this_obj, jintArray array, jlong ref, jint start, jint len){

  int * dest = (int *) (ref + start);
  (*env)->SetIntArrayRegion(env, array, start, len, dest);
}

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doReadFloatArray
  (JNIEnv *env, jobject this_obj, jfloatArray array, jlong ref, jint start, jint len){

  float * dest = (float *) (ref + start);
  (*env)->SetFloatArrayRegion(env, array, start, len, dest);
}

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doReadDoubleArray
  (JNIEnv *env, jobject this_obj, jdoubleArray array, jlong ref, jint start, jint len){

  jdouble * dest = (jdouble *) (ref + start);
  (*env)->SetDoubleArrayRegion(env, array, start, len, dest);
}

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_doReadLongArray
  (JNIEnv *env, jobject this_obj, jlongArray array, jlong ref, jint start, jint len){

  jlong * dest = (jlong *) (ref + start);
  (*env)->SetLongArrayRegion(env, array, start, len, dest);
}

JNIEXPORT jlong JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_malloc
  (JNIEnv *env, jobject this_obj, jlong size){

  return (jlong) calloc(size, 1);
}

JNIEXPORT void JNICALL Java_org_trifort_rootbeer_runtime_FixedMemory_free
  (JNIEnv *env, jobject this_obj, jlong address){

  free((void *) address);
}

