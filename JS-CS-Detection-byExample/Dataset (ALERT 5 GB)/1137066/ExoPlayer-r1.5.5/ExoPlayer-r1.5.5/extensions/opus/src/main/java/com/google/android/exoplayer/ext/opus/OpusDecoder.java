/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer.ext.opus;

import com.google.android.exoplayer.ext.opus.OpusDecoderWrapper.OpusHeader;

import java.nio.ByteBuffer;

/**
 * JNI Wrapper for the libopus Opus decoder.
 */
/* package */ class OpusDecoder {

  private static final boolean IS_AVAILABLE;
  static {
    boolean isAvailable;
    try {
      System.loadLibrary("opus");
      System.loadLibrary("opusJNI");
      isAvailable = true;
    } catch (UnsatisfiedLinkError exception) {
      isAvailable = false;
    }
    IS_AVAILABLE = isAvailable;
  }

  private final long nativeDecoderContext;

  /**
   * Creates the Opus Decoder.
   *
   * @param opusHeader OpusHeader used to initialize the decoder.
   * @throws OpusDecoderException if the decoder initialization fails.
   */
  public OpusDecoder(OpusHeader opusHeader) throws OpusDecoderException {
    nativeDecoderContext = opusInit(
        opusHeader.sampleRate, opusHeader.channelCount, opusHeader.numStreams,
        opusHeader.numCoupled, opusHeader.gain, opusHeader.streamMap);
    if (nativeDecoderContext == 0) {
      throw new OpusDecoderException("Failed to initialize decoder");
    }
  }

  /**
   * Decodes an Opus Encoded Stream.
   *
   * @param inputBuffer buffer containing the encoded data. Must be allocated using allocateDirect.
   * @param inputSize size of the input buffer.
   * @param outputBuffer buffer to write the decoded data. Must be allocated using allocateDirect.
   * @param outputSize Maximum capacity of the output buffer.
   * @return number of decoded bytes.
   * @throws OpusDecoderException if decode fails.
   */
  public int decode(ByteBuffer inputBuffer, int inputSize, ByteBuffer outputBuffer,
      int outputSize) throws OpusDecoderException {
    int result = opusDecode(nativeDecoderContext, inputBuffer, inputSize, outputBuffer, outputSize);
    if (result < 0) {
      throw new OpusDecoderException("Decode error: " + opusGetErrorMessage(result));
    }
    return result;
  }

  /**
   * Closes the native decoder.
   */
  public void close() {
    opusClose(nativeDecoderContext);
  }

  /**
   * Resets the native decode on discontinuity (during seek for example).
   */
  public void reset() {
    opusReset(nativeDecoderContext);
  }

  /**
   * Returns whether the underlying libopus library is available.
   */
  public static boolean isLibopusAvailable() {
    return IS_AVAILABLE;
  }

  /**
   * Returns the version string of the underlying libopus decoder.
   */
  public static native String getLibopusVersion();

  private native long opusInit(int sampleRate, int channelCount, int numStreams, int numCoupled,
      int gain, byte[] streamMap);
  private native int opusDecode(long decoder, ByteBuffer inputBuffer, int inputSize,
      ByteBuffer outputBuffer, int outputSize);
  private native void opusClose(long decoder);
  private native void opusReset(long decoder);
  private native String opusGetErrorMessage(int errorCode);

}
