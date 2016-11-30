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
package com.google.android.exoplayer.text.webvtt;

import com.google.android.exoplayer.ParserException;
import com.google.android.exoplayer.text.SubtitleParser;
import com.google.android.exoplayer.util.MimeTypes;
import com.google.android.exoplayer.util.ParsableByteArray;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * A simple WebVTT parser.
 * <p>
 * @see <a href="http://dev.w3.org/html5/webvtt">WebVTT specification</a>
 */
public final class WebvttParser implements SubtitleParser {

  private final WebvttCueParser cueParser;
  private final ParsableByteArray parsableWebvttData;
  private final WebvttCue.Builder webvttCueBuilder;

  public WebvttParser() {
    cueParser = new WebvttCueParser();
    parsableWebvttData = new ParsableByteArray();
    webvttCueBuilder = new WebvttCue.Builder();
  }

  @Override
  public final boolean canParse(String mimeType) {
    return MimeTypes.TEXT_VTT.equals(mimeType);
  }

  @Override
  public final WebvttSubtitle parse(byte[] bytes, int offset, int length) throws ParserException {
    parsableWebvttData.reset(bytes, offset + length);
    parsableWebvttData.setPosition(offset);
    webvttCueBuilder.reset(); // In case a previous parse run failed with a ParserException.

    // Validate the first line of the header, and skip the remainder.
    WebvttParserUtil.validateWebvttHeaderLine(parsableWebvttData);
    while (!TextUtils.isEmpty(parsableWebvttData.readLine())) {}

    // Extract Cues
    ArrayList<WebvttCue> subtitles = new ArrayList<>();
    while (cueParser.parseNextValidCue(parsableWebvttData, webvttCueBuilder)) {
      subtitles.add(webvttCueBuilder.build());
      webvttCueBuilder.reset();
    }
    return new WebvttSubtitle(subtitles);
  }

}
