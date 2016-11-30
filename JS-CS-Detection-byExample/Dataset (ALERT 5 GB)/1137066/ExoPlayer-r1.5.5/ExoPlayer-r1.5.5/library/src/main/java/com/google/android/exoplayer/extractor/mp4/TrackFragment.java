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
package com.google.android.exoplayer.extractor.mp4;

import com.google.android.exoplayer.extractor.ExtractorInput;
import com.google.android.exoplayer.util.ParsableByteArray;

import java.io.IOException;

/**
 * A holder for information corresponding to a single fragment of an mp4 file.
 */
/* package */ final class TrackFragment {

  /**
   * The default values for samples from the track fragment header.
   */
  public DefaultSampleValues header;
  /**
   * The position (byte offset) of the start of sample data.
   */
  public long dataPosition;
  /**
   * The position (byte offset) of the start of auxiliary data.
   */
  public long auxiliaryDataPosition;
  /**
   * The number of samples contained by the fragment.
   */
  public int length;
  /**
   * The size of each sample in the run.
   */
  public int[] sampleSizeTable;
  /**
   * The composition time offset of each sample in the run.
   */
  public int[] sampleCompositionTimeOffsetTable;
  /**
   * The decoding time of each sample in the run.
   */
  public long[] sampleDecodingTimeTable;
  /**
   * Indicates which samples are sync frames.
   */
  public boolean[] sampleIsSyncFrameTable;
  /**
   * True if the fragment defines encryption data. False otherwise.
   */
  public boolean definesEncryptionData;
  /**
   * If {@link #definesEncryptionData} is true, indicates which samples use sub-sample encryption.
   * Undefined otherwise.
   */
  public boolean[] sampleHasSubsampleEncryptionTable;
  /**
   * If {@link #definesEncryptionData} is true, indicates the length of the sample encryption data.
   * Undefined otherwise.
   */
  public int sampleEncryptionDataLength;
  /**
   * If {@link #definesEncryptionData} is true, contains binary sample encryption data. Undefined
   * otherwise.
   */
  public ParsableByteArray sampleEncryptionData;
  /**
   * Whether {@link #sampleEncryptionData} needs populating with the actual encryption data.
   */
  public boolean sampleEncryptionDataNeedsFill;

  /**
   * Resets the fragment.
   * <p>
   * The {@link #length} is set to 0, and both {@link #definesEncryptionData} and
   * {@link #sampleEncryptionDataNeedsFill} is set to false.
   */
  public void reset() {
    length = 0;
    definesEncryptionData = false;
    sampleEncryptionDataNeedsFill = false;
  }

  /**
   * Configures the fragment for the specified number of samples.
   * <p>
   * The {@link #length} of the fragment is set to the specified sample count, and the contained
   * tables are resized if necessary such that they are at least this length.
   *
   * @param sampleCount The number of samples in the new run.
   */
  public void initTables(int sampleCount) {
    length = sampleCount;
    if (sampleSizeTable == null || sampleSizeTable.length < length) {
      // Size the tables 25% larger than needed, so as to make future resize operations less
      // likely. The choice of 25% is relatively arbitrary.
      int tableSize = (sampleCount * 125) / 100;
      sampleSizeTable = new int[tableSize];
      sampleCompositionTimeOffsetTable = new int[tableSize];
      sampleDecodingTimeTable = new long[tableSize];
      sampleIsSyncFrameTable = new boolean[tableSize];
      sampleHasSubsampleEncryptionTable = new boolean[tableSize];
    }
  }

  /**
   * Configures the fragment to be one that defines encryption data of the specified length.
   * <p>
   * {@link #definesEncryptionData} is set to true, {@link #sampleEncryptionDataLength} is set to
   * the specified length, and {@link #sampleEncryptionData} is resized if necessary such that it
   * is at least this length.
   *
   * @param length The length in bytes of the encryption data.
   */
  public void initEncryptionData(int length) {
    if (sampleEncryptionData == null || sampleEncryptionData.limit() < length) {
      sampleEncryptionData = new ParsableByteArray(length);
    }
    sampleEncryptionDataLength = length;
    definesEncryptionData = true;
    sampleEncryptionDataNeedsFill = true;
  }

  /**
   * Fills {@link #sampleEncryptionData} from the provided input.
   *
   * @param input An {@link ExtractorInput} from which to read the encryption data.
   */
  public void fillEncryptionData(ExtractorInput input) throws IOException, InterruptedException {
    input.readFully(sampleEncryptionData.data, 0, sampleEncryptionDataLength);
    sampleEncryptionData.setPosition(0);
    sampleEncryptionDataNeedsFill = false;
  }

  /**
   * Fills {@link #sampleEncryptionData} from the provided source.
   *
   * @param source A source from which to read the encryption data.
   */
  public void fillEncryptionData(ParsableByteArray source) {
    source.readBytes(sampleEncryptionData.data, 0, sampleEncryptionDataLength);
    sampleEncryptionData.setPosition(0);
    sampleEncryptionDataNeedsFill = false;
  }

  public long getSamplePresentationTime(int index) {
    return sampleDecodingTimeTable[index] + sampleCompositionTimeOffsetTable[index];
  }

}
