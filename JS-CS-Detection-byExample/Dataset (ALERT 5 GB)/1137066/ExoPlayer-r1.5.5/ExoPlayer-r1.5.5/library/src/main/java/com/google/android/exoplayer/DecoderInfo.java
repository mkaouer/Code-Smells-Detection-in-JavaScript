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
package com.google.android.exoplayer;

/**
 * Contains information about a media decoder.
 */
public final class DecoderInfo {

  /**
   * The name of the decoder.
   * <p>
   * May be passed to {@link android.media.MediaCodec#createByCodecName(String)} to create an
   * instance of the decoder.
   */
  public final String name;

  /**
   * Whether the decoder supports seamless resolution switches.
   *
   * @see android.media.MediaCodecInfo.CodecCapabilities#isFeatureSupported(String)
   * @see android.media.MediaCodecInfo.CodecCapabilities#FEATURE_AdaptivePlayback
   */
  public final boolean adaptive;

  /**
   * @param name The name of the decoder.
   * @param adaptive Whether the decoder is adaptive.
   */
  /* package */ DecoderInfo(String name, boolean adaptive) {
    this.name = name;
    this.adaptive = adaptive;
  }

}
