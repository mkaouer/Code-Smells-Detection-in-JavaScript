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
package com.google.android.exoplayer.hls;

import com.google.android.exoplayer.chunk.VideoFormatSelectorUtil;

import android.content.Context;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A default {@link HlsTrackSelector} implementation.
 */
public final class DefaultHlsTrackSelector implements HlsTrackSelector {

  private static final int TYPE_DEFAULT = 0;
  private static final int TYPE_VTT = 1;

  private final Context context;
  private final int type;

  /**
   * Creates a {@link DefaultHlsTrackSelector} that selects the streams defined in the playlist.
   *
   * @param context A context.
   * @return The selector instance.
   */
  public static DefaultHlsTrackSelector newDefaultInstance(Context context) {
    return new DefaultHlsTrackSelector(context, TYPE_DEFAULT);
  }

  /**
   * Creates a {@link DefaultHlsTrackSelector} that selects subtitle renditions.
   *
   * @return The selector instance.
   */
  public static DefaultHlsTrackSelector newVttInstance() {
    return new DefaultHlsTrackSelector(null, TYPE_VTT);
  }

  private DefaultHlsTrackSelector(Context context, int type) {
    this.context = context;
    this.type = type;
  }

  @Override
  public void selectTracks(HlsMasterPlaylist playlist, Output output) throws IOException {
    if (type == TYPE_VTT) {
      List<Variant> subtitleVariants = playlist.subtitles;
      if (subtitleVariants != null && !subtitleVariants.isEmpty()) {
        for (int i = 0; i < subtitleVariants.size(); i++) {
          output.fixedTrack(playlist, subtitleVariants.get(i));
        }
      }
      return;
    }

    // Type is TYPE_DEFAULT.

    ArrayList<Variant> enabledVariantList = new ArrayList<>();
    int[] variantIndices = VideoFormatSelectorUtil.selectVideoFormatsForDefaultDisplay(
        context, playlist.variants, null, false);
    for (int i = 0; i < variantIndices.length; i++) {
      enabledVariantList.add(playlist.variants.get(variantIndices[i]));
    }

    ArrayList<Variant> definiteVideoVariants = new ArrayList<>();
    ArrayList<Variant> definiteAudioOnlyVariants = new ArrayList<>();
    for (int i = 0; i < enabledVariantList.size(); i++) {
      Variant variant = enabledVariantList.get(i);
      if (variant.format.height > 0 || variantHasExplicitCodecWithPrefix(variant, "avc")) {
        definiteVideoVariants.add(variant);
      } else if (variantHasExplicitCodecWithPrefix(variant, "mp4a")) {
        definiteAudioOnlyVariants.add(variant);
      }
    }

    if (!definiteVideoVariants.isEmpty()) {
      // We've identified some variants as definitely containing video. Assume variants within the
      // master playlist are marked consistently, and hence that we have the full set. Filter out
      // any other variants, which are likely to be audio only.
      enabledVariantList = definiteVideoVariants;
    } else if (definiteAudioOnlyVariants.size() < enabledVariantList.size()) {
      // We've identified some variants, but not all, as being audio only. Filter them out to leave
      // the remaining variants, which are likely to contain video.
      enabledVariantList.removeAll(definiteAudioOnlyVariants);
    } else {
      // Leave the enabled variants unchanged. They're likely either all video or all audio.
    }

    if (enabledVariantList.size() > 1) {
      Variant[] enabledVariants = new Variant[enabledVariantList.size()];
      enabledVariantList.toArray(enabledVariants);
      output.adaptiveTrack(playlist, enabledVariants);
    }
    for (int i = 0; i < enabledVariantList.size(); i++) {
      output.fixedTrack(playlist, enabledVariantList.get(i));
    }
  }

  private static boolean variantHasExplicitCodecWithPrefix(Variant variant, String prefix) {
    String codecs = variant.format.codecs;
    if (TextUtils.isEmpty(codecs)) {
      return false;
    }
    String[] codecArray = codecs.split("(\\s*,\\s*)|(\\s*$)");
    for (int i = 0; i < codecArray.length; i++) {
      if (codecArray[i].startsWith(prefix)) {
        return true;
      }
    }
    return false;
  }

}
