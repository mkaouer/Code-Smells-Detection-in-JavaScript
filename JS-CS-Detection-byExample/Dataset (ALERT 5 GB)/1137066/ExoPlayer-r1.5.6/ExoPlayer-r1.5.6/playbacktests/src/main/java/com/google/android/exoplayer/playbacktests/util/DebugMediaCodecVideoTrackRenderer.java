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
package com.google.android.exoplayer.playbacktests.util;

import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.util.Assertions;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Handler;

/**
 * Decodes and renders video using {@link MediaCodecVideoTrackRenderer}. Provides buffer timestamp
 * assertions.
 */
@TargetApi(16)
public class DebugMediaCodecVideoTrackRenderer extends MediaCodecVideoTrackRenderer {

  private static final int ARRAY_SIZE = 1000;

  public final long[] timestampsList = new long[ARRAY_SIZE];

  private int startIndex;
  private int queueSize;
  private boolean enableBufferTimestampAssertions;

  public DebugMediaCodecVideoTrackRenderer(Context context, SampleSource source,
      MediaCodecSelector mediaCodecSelector, int videoScalingMode, long allowedJoiningTimeMs,
      Handler eventHandler, EventListener eventListener, int maxDroppedFrameCountToNotify,
      boolean enableBufferTimestampAssertions) {
    super(context, source, mediaCodecSelector, videoScalingMode, allowedJoiningTimeMs, null, false,
        eventHandler, eventListener, maxDroppedFrameCountToNotify);
    this.enableBufferTimestampAssertions = enableBufferTimestampAssertions;
    startIndex = 0;
    queueSize = 0;
  }

  @Override
  protected void onQueuedInputBuffer(long presentationTimeUs) {
    if (enableBufferTimestampAssertions) {
      insertTimestamp(presentationTimeUs);
      maybeShiftTimestampsList();
    }
  }

  @Override
  protected void onProcessedOutputBuffer(long presentationTimeUs) {
    if (enableBufferTimestampAssertions) {
      Assertions.checkArgument(dequeueTimestamp() == presentationTimeUs);
    }
  }

  private void insertTimestamp(long presentationTimeUs) {
    for (int i = startIndex + queueSize - 1; i >= startIndex; i--) {
      if (presentationTimeUs >= timestampsList[i]) {
        timestampsList[i + 1] = presentationTimeUs;
        queueSize++;
        return;
      }
      timestampsList[i + 1] = timestampsList[i];
    }
    timestampsList[startIndex] = presentationTimeUs;
    queueSize++;
  }

  private void maybeShiftTimestampsList() {
    if (startIndex + queueSize == ARRAY_SIZE) {
      System.arraycopy(timestampsList, startIndex, timestampsList, 0, queueSize);
      startIndex = 0;
    }
  }

  private long dequeueTimestamp() {
    startIndex++;
    queueSize--;
    return timestampsList[startIndex - 1];
  }
}
