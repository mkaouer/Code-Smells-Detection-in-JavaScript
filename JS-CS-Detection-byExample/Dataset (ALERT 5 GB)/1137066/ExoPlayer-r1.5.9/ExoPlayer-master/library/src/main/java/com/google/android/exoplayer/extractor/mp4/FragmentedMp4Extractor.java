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

import com.google.android.exoplayer.C;
import com.google.android.exoplayer.ParserException;
import com.google.android.exoplayer.drm.DrmInitData;
import com.google.android.exoplayer.drm.DrmInitData.SchemeInitData;
import com.google.android.exoplayer.extractor.ChunkIndex;
import com.google.android.exoplayer.extractor.Extractor;
import com.google.android.exoplayer.extractor.ExtractorInput;
import com.google.android.exoplayer.extractor.ExtractorOutput;
import com.google.android.exoplayer.extractor.PositionHolder;
import com.google.android.exoplayer.extractor.SeekMap;
import com.google.android.exoplayer.extractor.TrackOutput;
import com.google.android.exoplayer.extractor.mp4.Atom.ContainerAtom;
import com.google.android.exoplayer.extractor.mp4.Atom.LeafAtom;
import com.google.android.exoplayer.util.Assertions;
import com.google.android.exoplayer.util.MimeTypes;
import com.google.android.exoplayer.util.NalUnitUtil;
import com.google.android.exoplayer.util.ParsableByteArray;
import com.google.android.exoplayer.util.Util;

import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

/**
 * Facilitates the extraction of data from the fragmented mp4 container format.
 */
public class FragmentedMp4Extractor implements Extractor {

  private static final String TAG = "FragmentedMp4Extractor";
  private static final int SAMPLE_GROUP_TYPE_seig = Util.getIntegerCodeForString("seig");

  /**
   * Flag to work around an issue in some video streams where every frame is marked as a sync frame.
   * The workaround overrides the sync frame flags in the stream, forcing them to false except for
   * the first sample in each segment.
   * <p>
   * This flag does nothing if the stream is not a video stream.
   */
  public static final int FLAG_WORKAROUND_EVERY_VIDEO_FRAME_IS_SYNC_FRAME = 1;

  /**
   * Flag to ignore any tfdt boxes in the stream.
   */
  public static final int FLAG_WORKAROUND_IGNORE_TFDT_BOX = 2;

  /**
   * Flag to indicate that the {@link Track} was sideloaded, instead of being declared by the MP4
   * container.
   */
  private static final int FLAG_SIDELOADED = 4;

  private static final byte[] PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE =
      new byte[] {-94, 57, 79, 82, 90, -101, 79, 20, -94, 68, 108, 66, 124, 100, -115, -12};

  // Parser states.
  private static final int STATE_READING_ATOM_HEADER = 0;
  private static final int STATE_READING_ATOM_PAYLOAD = 1;
  private static final int STATE_READING_ENCRYPTION_DATA = 2;
  private static final int STATE_READING_SAMPLE_START = 3;
  private static final int STATE_READING_SAMPLE_CONTINUE = 4;

  // Workarounds.
  private final int flags;
  private final Track sideloadedTrack;

  // Track-linked data bundle, accessible as a whole through trackID.
  private final SparseArray<TrackBundle> trackBundles;

  // Temporary arrays.
  private final ParsableByteArray nalStartCode;
  private final ParsableByteArray nalLength;
  private final ParsableByteArray encryptionSignalByte;

  // Parser state.
  private final ParsableByteArray atomHeader;
  private final byte[] extendedTypeScratch;
  private final Stack<ContainerAtom> containerAtoms;

  private int parserState;
  private int atomType;
  private long atomSize;
  private int atomHeaderBytesRead;
  private ParsableByteArray atomData;
  private long endOfMdatPosition;

  private TrackBundle currentTrackBundle;
  private int sampleSize;
  private int sampleBytesWritten;
  private int sampleCurrentNalBytesRemaining;

  // Extractor output.
  private ExtractorOutput extractorOutput;

  // Whether extractorOutput.seekMap has been invoked.
  private boolean haveOutputSeekMap;

  public FragmentedMp4Extractor() {
    this(0);
  }

  /**
   * @param flags Flags to allow parsing of faulty streams.
   */
  public FragmentedMp4Extractor(int flags) {
    this(flags, null);
  }

  /**
   * @param flags Flags to allow parsing of faulty streams.
   * @param sideloadedTrack Sideloaded track information, in the case that the extractor
   *     will not receive a moov box in the input data.
   */
  public FragmentedMp4Extractor(int flags, Track sideloadedTrack) {
    this.sideloadedTrack = sideloadedTrack;
    this.flags = flags | (sideloadedTrack != null ? FLAG_SIDELOADED : 0);
    atomHeader = new ParsableByteArray(Atom.LONG_HEADER_SIZE);
    nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
    nalLength = new ParsableByteArray(4);
    encryptionSignalByte = new ParsableByteArray(1);
    extendedTypeScratch = new byte[16];
    containerAtoms = new Stack<>();
    trackBundles = new SparseArray<>();
    enterReadingAtomHeaderState();
  }

  @Override
  public final boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
    return Sniffer.sniffFragmented(input);
  }

  @Override
  public final void init(ExtractorOutput output) {
    extractorOutput = output;
    if (sideloadedTrack != null) {
      TrackBundle bundle = new TrackBundle(output.track(0));
      bundle.init(sideloadedTrack, new DefaultSampleValues(0, 0, 0, 0));
      trackBundles.put(0, bundle);
      extractorOutput.endTracks();
    }
  }

  @Override
  public final void seek() {
    int trackCount = trackBundles.size();
    for (int i = 0; i < trackCount; i++) {
      trackBundles.valueAt(i).reset();
    }
    containerAtoms.clear();
    enterReadingAtomHeaderState();
  }

  @Override
  public final void release() {
    // Do nothing
  }

  @Override
  public final int read(ExtractorInput input, PositionHolder seekPosition)
      throws IOException, InterruptedException {
    while (true) {
      switch (parserState) {
        case STATE_READING_ATOM_HEADER:
          if (!readAtomHeader(input)) {
            return Extractor.RESULT_END_OF_INPUT;
          }
          break;
        case STATE_READING_ATOM_PAYLOAD:
          readAtomPayload(input);
          break;
        case STATE_READING_ENCRYPTION_DATA:
          readEncryptionData(input);
          break;
        default:
          if (readSample(input)) {
            return RESULT_CONTINUE;
          }
      }
    }
  }

  private void enterReadingAtomHeaderState() {
    parserState = STATE_READING_ATOM_HEADER;
    atomHeaderBytesRead = 0;
  }

  private boolean readAtomHeader(ExtractorInput input) throws IOException, InterruptedException {
    if (atomHeaderBytesRead == 0) {
      // Read the standard length atom header.
      if (!input.readFully(atomHeader.data, 0, Atom.HEADER_SIZE, true)) {
        return false;
      }
      atomHeaderBytesRead = Atom.HEADER_SIZE;
      atomHeader.setPosition(0);
      atomSize = atomHeader.readUnsignedInt();
      atomType = atomHeader.readInt();
    }

    if (atomSize == Atom.LONG_SIZE_PREFIX) {
      // Read the extended atom size.
      int headerBytesRemaining = Atom.LONG_HEADER_SIZE - Atom.HEADER_SIZE;
      input.readFully(atomHeader.data, Atom.HEADER_SIZE, headerBytesRemaining);
      atomHeaderBytesRead += headerBytesRemaining;
      atomSize = atomHeader.readUnsignedLongToLong();
    }

    long atomPosition = input.getPosition() - atomHeaderBytesRead;
    if (atomType == Atom.TYPE_moof) {
      // The data positions may be updated when parsing the tfhd/trun.
      int trackCount = trackBundles.size();
      for (int i = 0; i < trackCount; i++) {
        TrackFragment fragment = trackBundles.valueAt(i).fragment;
        fragment.auxiliaryDataPosition = atomPosition;
        fragment.dataPosition = atomPosition;
      }
    }

    if (atomType == Atom.TYPE_mdat) {
      currentTrackBundle = null;
      endOfMdatPosition = atomPosition + atomSize;
      if (!haveOutputSeekMap) {
        extractorOutput.seekMap(SeekMap.UNSEEKABLE);
        haveOutputSeekMap = true;
      }
      parserState = STATE_READING_ENCRYPTION_DATA;
      return true;
    }

    if (shouldParseContainerAtom(atomType)) {
      long endPosition = input.getPosition() + atomSize - Atom.HEADER_SIZE;
      containerAtoms.add(new ContainerAtom(atomType, endPosition));
      if (atomSize == atomHeaderBytesRead) {
        processAtomEnded(endPosition);
      } else {
        // Start reading the first child atom.
        enterReadingAtomHeaderState();
      }
    } else if (shouldParseLeafAtom(atomType)) {
      if (atomHeaderBytesRead != Atom.HEADER_SIZE) {
        throw new ParserException("Leaf atom defines extended atom size (unsupported).");
      }
      if (atomSize > Integer.MAX_VALUE) {
        throw new ParserException("Leaf atom with length > 2147483647 (unsupported).");
      }
      atomData = new ParsableByteArray((int) atomSize);
      System.arraycopy(atomHeader.data, 0, atomData.data, 0, Atom.HEADER_SIZE);
      parserState = STATE_READING_ATOM_PAYLOAD;
    } else {
      if (atomSize > Integer.MAX_VALUE) {
        throw new ParserException("Skipping atom with length > 2147483647 (unsupported).");
      }
      atomData = null;
      parserState = STATE_READING_ATOM_PAYLOAD;
    }

    return true;
  }

  private void readAtomPayload(ExtractorInput input) throws IOException, InterruptedException {
    int atomPayloadSize = (int) atomSize - atomHeaderBytesRead;
    if (atomData != null) {
      input.readFully(atomData.data, Atom.HEADER_SIZE, atomPayloadSize);
      onLeafAtomRead(new LeafAtom(atomType, atomData), input.getPosition());
    } else {
      input.skipFully(atomPayloadSize);
    }
    processAtomEnded(input.getPosition());
  }

  private void processAtomEnded(long atomEndPosition) throws ParserException {
    while (!containerAtoms.isEmpty() && containerAtoms.peek().endPosition == atomEndPosition) {
      onContainerAtomRead(containerAtoms.pop());
    }
    enterReadingAtomHeaderState();
  }

  private void onLeafAtomRead(LeafAtom leaf, long inputPosition) throws ParserException {
    if (!containerAtoms.isEmpty()) {
      containerAtoms.peek().add(leaf);
    } else if (leaf.type == Atom.TYPE_sidx) {
      ChunkIndex segmentIndex = parseSidx(leaf.data, inputPosition);
      extractorOutput.seekMap(segmentIndex);
      haveOutputSeekMap = true;
    } else if (leaf.type == Atom.TYPE_emsg) {
      parseEmsg(leaf.data, inputPosition);
    }
  }

  private void onContainerAtomRead(ContainerAtom container) throws ParserException {
    if (container.type == Atom.TYPE_moov) {
      onMoovContainerAtomRead(container);
    } else if (container.type == Atom.TYPE_moof) {
      onMoofContainerAtomRead(container);
    } else if (!containerAtoms.isEmpty()) {
      containerAtoms.peek().add(container);
    }
  }

  private void onMoovContainerAtomRead(ContainerAtom moov) {
    Assertions.checkState(sideloadedTrack == null, "Unexpected moov box.");
    List<Atom.LeafAtom> moovLeafChildren = moov.leafChildren;
    int moovLeafChildrenSize = moovLeafChildren.size();

    DrmInitData.Mapped drmInitData = null;
    for (int i = 0; i < moovLeafChildrenSize; i++) {
      LeafAtom child = moovLeafChildren.get(i);
      if (child.type == Atom.TYPE_pssh) {
        if (drmInitData == null) {
          drmInitData = new DrmInitData.Mapped();
        }
        byte[] psshData = child.data.data;
        UUID uuid = PsshAtomUtil.parseUuid(psshData);
        if (uuid == null) {
          Log.w(TAG, "Skipped pssh atom (failed to extract uuid)");
        } else {
          drmInitData.put(PsshAtomUtil.parseUuid(psshData),
              new SchemeInitData(MimeTypes.VIDEO_MP4, psshData));
        }
      }
    }
    if (drmInitData != null) {
      extractorOutput.drmInitData(drmInitData);
    }

    // Read declaration of track fragments in the Moov box.
    ContainerAtom mvex = moov.getContainerAtomOfType(Atom.TYPE_mvex);
    SparseArray<DefaultSampleValues> defaultSampleValuesArray = new SparseArray<>();
    long duration = -1;
    int mvexChildrenSize = mvex.leafChildren.size();
    for (int i = 0; i < mvexChildrenSize; i++) {
      Atom.LeafAtom atom = mvex.leafChildren.get(i);
      if (atom.type == Atom.TYPE_trex) {
        Pair<Integer, DefaultSampleValues> trexData = parseTrex(atom.data);
        defaultSampleValuesArray.put(trexData.first, trexData.second);
      } else if (atom.type == Atom.TYPE_mehd) {
        duration = parseMehd(atom.data);
      }
    }

    // Construction of tracks.
    SparseArray<Track> tracks = new SparseArray<>();
    int moovContainerChildrenSize = moov.containerChildren.size();
    for (int i = 0; i < moovContainerChildrenSize; i++) {
      Atom.ContainerAtom atom = moov.containerChildren.get(i);
      if (atom.type == Atom.TYPE_trak) {
        Track track = AtomParsers.parseTrak(atom, moov.getLeafAtomOfType(Atom.TYPE_mvhd), duration,
            false);
        if (track != null) {
          tracks.put(track.id, track);
        }
      }
    }
    int trackCount = tracks.size();

    if (trackBundles.size() == 0) {
      // We need to create the track bundles.
      for (int i = 0; i < trackCount; i++) {
        trackBundles.put(tracks.valueAt(i).id, new TrackBundle(extractorOutput.track(i)));
      }
      extractorOutput.endTracks();
    } else {
      Assertions.checkState(trackBundles.size() == trackCount);
    }

    // Initialization of tracks and default sample values.
    for (int i = 0; i < trackCount; i++) {
      Track track = tracks.valueAt(i);
      trackBundles.get(track.id).init(track, defaultSampleValuesArray.get(track.id));
    }
  }

  private void onMoofContainerAtomRead(ContainerAtom moof) throws ParserException {
    parseMoof(moof, trackBundles, flags, extendedTypeScratch);
  }

  /**
   * Parses a trex atom (defined in 14496-12).
   */
  private static Pair<Integer, DefaultSampleValues> parseTrex(ParsableByteArray trex) {
    trex.setPosition(Atom.FULL_HEADER_SIZE);
    int trackId = trex.readInt();
    int defaultSampleDescriptionIndex = trex.readUnsignedIntToInt() - 1;
    int defaultSampleDuration = trex.readUnsignedIntToInt();
    int defaultSampleSize = trex.readUnsignedIntToInt();
    int defaultSampleFlags = trex.readInt();

    return Pair.create(trackId, new DefaultSampleValues(defaultSampleDescriptionIndex,
        defaultSampleDuration, defaultSampleSize, defaultSampleFlags));
  }

  /**
   * Parses an mehd atom (defined in 14496-12).
   */
  private static long parseMehd(ParsableByteArray mehd) {
    mehd.setPosition(Atom.HEADER_SIZE);
    int fullAtom = mehd.readInt();
    int version = Atom.parseFullAtomVersion(fullAtom);
    return version == 0 ? mehd.readUnsignedInt() : mehd.readUnsignedLongToLong();
  }

  private static void parseMoof(ContainerAtom moof, SparseArray<TrackBundle> trackBundleArray,
      int flags, byte[] extendedTypeScratch) throws ParserException {
    int moofContainerChildrenSize = moof.containerChildren.size();
    for (int i = 0; i < moofContainerChildrenSize; i++) {
      Atom.ContainerAtom child = moof.containerChildren.get(i);
      if (child.type == Atom.TYPE_traf) {
        parseTraf(child, trackBundleArray, flags, extendedTypeScratch);
      }
    }
  }

  /**
   * Parses a traf atom (defined in 14496-12).
   */
  private static void parseTraf(ContainerAtom traf, SparseArray<TrackBundle> trackBundleArray,
      int flags, byte[] extendedTypeScratch) throws ParserException {
    if (traf.getChildAtomOfTypeCount(Atom.TYPE_trun) != 1) {
      throw new ParserException("Trun count in traf != 1 (unsupported).");
    }

    LeafAtom tfhd = traf.getLeafAtomOfType(Atom.TYPE_tfhd);
    TrackBundle trackBundle = parseTfhd(tfhd.data, trackBundleArray, flags);
    if (trackBundle == null) {
      return;
    }

    TrackFragment fragment = trackBundle.fragment;
    long decodeTime = fragment.nextFragmentDecodeTime;
    trackBundle.reset();

    LeafAtom tfdtAtom = traf.getLeafAtomOfType(Atom.TYPE_tfdt);
    if (tfdtAtom != null && (flags & FLAG_WORKAROUND_IGNORE_TFDT_BOX) == 0) {
      decodeTime = parseTfdt(traf.getLeafAtomOfType(Atom.TYPE_tfdt).data);
    }

    LeafAtom trun = traf.getLeafAtomOfType(Atom.TYPE_trun);
    parseTrun(trackBundle, decodeTime, flags, trun.data);

    LeafAtom saiz = traf.getLeafAtomOfType(Atom.TYPE_saiz);
    if (saiz != null) {
      TrackEncryptionBox trackEncryptionBox = trackBundle.track
          .sampleDescriptionEncryptionBoxes[fragment.header.sampleDescriptionIndex];
      parseSaiz(trackEncryptionBox, saiz.data, fragment);
    }

    LeafAtom saio = traf.getLeafAtomOfType(Atom.TYPE_saio);
    if (saio != null) {
      parseSaio(saio.data, fragment);
    }

    LeafAtom senc = traf.getLeafAtomOfType(Atom.TYPE_senc);
    if (senc != null) {
      parseSenc(senc.data, fragment);
    }

    LeafAtom sbgp = traf.getLeafAtomOfType(Atom.TYPE_sbgp);
    LeafAtom sgpd = traf.getLeafAtomOfType(Atom.TYPE_sgpd);
    if (sbgp != null && sgpd != null) {
      parseSgpd(sbgp.data, sgpd.data, fragment);
    }

    int childrenSize = traf.leafChildren.size();
    for (int i = 0; i < childrenSize; i++) {
      LeafAtom atom = traf.leafChildren.get(i);
      if (atom.type == Atom.TYPE_uuid) {
        parseUuid(atom.data, fragment, extendedTypeScratch);
      }
    }
  }

  private static void parseSaiz(TrackEncryptionBox encryptionBox, ParsableByteArray saiz,
      TrackFragment out) throws ParserException {
    int vectorSize = encryptionBox.initializationVectorSize;
    saiz.setPosition(Atom.HEADER_SIZE);
    int fullAtom = saiz.readInt();
    int flags = Atom.parseFullAtomFlags(fullAtom);
    if ((flags & 0x01) == 1) {
      saiz.skipBytes(8);
    }
    int defaultSampleInfoSize = saiz.readUnsignedByte();

    int sampleCount = saiz.readUnsignedIntToInt();
    if (sampleCount != out.length) {
      throw new ParserException("Length mismatch: " + sampleCount + ", " + out.length);
    }

    int totalSize = 0;
    if (defaultSampleInfoSize == 0) {
      boolean[] sampleHasSubsampleEncryptionTable = out.sampleHasSubsampleEncryptionTable;
      for (int i = 0; i < sampleCount; i++) {
        int sampleInfoSize = saiz.readUnsignedByte();
        totalSize += sampleInfoSize;
        sampleHasSubsampleEncryptionTable[i] = sampleInfoSize > vectorSize;
      }
    } else {
      boolean subsampleEncryption = defaultSampleInfoSize > vectorSize;
      totalSize += defaultSampleInfoSize * sampleCount;
      Arrays.fill(out.sampleHasSubsampleEncryptionTable, 0, sampleCount, subsampleEncryption);
    }
    out.initEncryptionData(totalSize);
  }

  /**
   * Parses a saio atom (defined in 14496-12).
   *
   * @param saio The saio atom to parse.
   * @param out The {@link TrackFragment} to populate with data from the saio atom.
   */
  private static void parseSaio(ParsableByteArray saio, TrackFragment out) throws ParserException {
    saio.setPosition(Atom.HEADER_SIZE);
    int fullAtom = saio.readInt();
    int flags = Atom.parseFullAtomFlags(fullAtom);
    if ((flags & 0x01) == 1) {
      saio.skipBytes(8);
    }

    int entryCount = saio.readUnsignedIntToInt();
    if (entryCount != 1) {
      // We only support one trun element currently, so always expect one entry.
      throw new ParserException("Unexpected saio entry count: " + entryCount);
    }

    int version = Atom.parseFullAtomVersion(fullAtom);
    out.auxiliaryDataPosition +=
        version == 0 ? saio.readUnsignedInt() : saio.readUnsignedLongToLong();
  }

  /**
   * Parses a tfhd atom (defined in 14496-12), updates the corresponding {@link TrackFragment} and
   * returns the {@link TrackBundle} of the corresponding {@link Track}. If the tfhd does not refer
   * to any {@link TrackBundle}, {@code null} is returned and no changes are made.
   *
   * @param tfhd The tfhd atom to parse.
   * @param trackBundles The track bundles, one of which corresponds to the tfhd atom being parsed.
   * @return The {@link TrackBundle} to which the {@link TrackFragment} belongs, or null if the tfhd
   *     does not refer to any {@link TrackBundle}.
   */
  private static TrackBundle parseTfhd(ParsableByteArray tfhd,
      SparseArray<TrackBundle> trackBundles, int flags) {
    tfhd.setPosition(Atom.HEADER_SIZE);
    int fullAtom = tfhd.readInt();
    int atomFlags = Atom.parseFullAtomFlags(fullAtom);
    int trackId = tfhd.readInt();
    TrackBundle trackBundle = trackBundles.get((flags & FLAG_SIDELOADED) == 0 ? trackId : 0);
    if (trackBundle == null) {
      return null;
    }
    if ((atomFlags & 0x01 /* base_data_offset_present */) != 0) {
      long baseDataPosition = tfhd.readUnsignedLongToLong();
      trackBundle.fragment.dataPosition = baseDataPosition;
      trackBundle.fragment.auxiliaryDataPosition = baseDataPosition;
    }

    DefaultSampleValues defaultSampleValues = trackBundle.defaultSampleValues;
    int defaultSampleDescriptionIndex =
        ((atomFlags & 0x02 /* default_sample_description_index_present */) != 0)
        ? tfhd.readUnsignedIntToInt() - 1 : defaultSampleValues.sampleDescriptionIndex;
    int defaultSampleDuration = ((atomFlags & 0x08 /* default_sample_duration_present */) != 0)
        ? tfhd.readUnsignedIntToInt() : defaultSampleValues.duration;
    int defaultSampleSize = ((atomFlags & 0x10 /* default_sample_size_present */) != 0)
        ? tfhd.readUnsignedIntToInt() : defaultSampleValues.size;
    int defaultSampleFlags = ((atomFlags & 0x20 /* default_sample_flags_present */) != 0)
        ? tfhd.readUnsignedIntToInt() : defaultSampleValues.flags;
    trackBundle.fragment.header = new DefaultSampleValues(defaultSampleDescriptionIndex,
        defaultSampleDuration, defaultSampleSize, defaultSampleFlags);
    return trackBundle;
  }

  /**
   * Parses a tfdt atom (defined in 14496-12).
   *
   * @return baseMediaDecodeTime The sum of the decode durations of all earlier samples in the
   *     media, expressed in the media's timescale.
   */
  private static long parseTfdt(ParsableByteArray tfdt) {
    tfdt.setPosition(Atom.HEADER_SIZE);
    int fullAtom = tfdt.readInt();
    int version = Atom.parseFullAtomVersion(fullAtom);
    return version == 1 ? tfdt.readUnsignedLongToLong() : tfdt.readUnsignedInt();
  }

  /**
   * Parses a trun atom (defined in 14496-12).
   *
   * @param trackBundle The {@link TrackBundle} that contains the {@link TrackFragment} into
   *     which parsed data should be placed.
   * @param decodeTime The decode time of the first sample in the fragment run.
   * @param flags Flags to allow any required workaround to be executed.
   * @param trun The trun atom to parse.
   */
  private static void parseTrun(TrackBundle trackBundle, long decodeTime, int flags,
      ParsableByteArray trun) {
    trun.setPosition(Atom.HEADER_SIZE);
    int fullAtom = trun.readInt();
    int atomFlags = Atom.parseFullAtomFlags(fullAtom);

    Track track = trackBundle.track;
    TrackFragment fragment = trackBundle.fragment;
    DefaultSampleValues defaultSampleValues = fragment.header;

    int sampleCount = trun.readUnsignedIntToInt();
    if ((atomFlags & 0x01 /* data_offset_present */) != 0) {
      fragment.dataPosition += trun.readInt();
    }

    boolean firstSampleFlagsPresent = (atomFlags & 0x04 /* first_sample_flags_present */) != 0;
    int firstSampleFlags = defaultSampleValues.flags;
    if (firstSampleFlagsPresent) {
      firstSampleFlags = trun.readUnsignedIntToInt();
    }

    boolean sampleDurationsPresent = (atomFlags & 0x100 /* sample_duration_present */) != 0;
    boolean sampleSizesPresent = (atomFlags & 0x200 /* sample_size_present */) != 0;
    boolean sampleFlagsPresent = (atomFlags & 0x400 /* sample_flags_present */) != 0;
    boolean sampleCompositionTimeOffsetsPresent =
        (atomFlags & 0x800 /* sample_composition_time_offsets_present */) != 0;

    // Offset to the entire video timeline. In the presence of B-frames this is usually used to
    // ensure that the first frame's presentation timestamp is zero.
    long edtsOffset = 0;

    // Currently we only support a single edit that moves the entire media timeline (indicated by
    // duration == 0). Other uses of edit lists are uncommon and unsupported.
    if (track.editListDurations != null && track.editListDurations.length == 1
        && track.editListDurations[0] == 0) {
      edtsOffset = Util.scaleLargeTimestamp(track.editListMediaTimes[0], 1000, track.timescale);
    }

    fragment.initTables(sampleCount);
    int[] sampleSizeTable = fragment.sampleSizeTable;
    int[] sampleCompositionTimeOffsetTable = fragment.sampleCompositionTimeOffsetTable;
    long[] sampleDecodingTimeTable = fragment.sampleDecodingTimeTable;
    boolean[] sampleIsSyncFrameTable = fragment.sampleIsSyncFrameTable;

    long timescale = track.timescale;
    long cumulativeTime = decodeTime;
    boolean workaroundEveryVideoFrameIsSyncFrame = track.type == Track.TYPE_vide
        && (flags & FLAG_WORKAROUND_EVERY_VIDEO_FRAME_IS_SYNC_FRAME) != 0;
    for (int i = 0; i < sampleCount; i++) {
      // Use trun values if present, otherwise tfhd, otherwise trex.
      int sampleDuration = sampleDurationsPresent ? trun.readUnsignedIntToInt()
          : defaultSampleValues.duration;
      int sampleSize = sampleSizesPresent ? trun.readUnsignedIntToInt() : defaultSampleValues.size;
      int sampleFlags = (i == 0 && firstSampleFlagsPresent) ? firstSampleFlags
          : sampleFlagsPresent ? trun.readInt() : defaultSampleValues.flags;
      if (sampleCompositionTimeOffsetsPresent) {
        // The BMFF spec (ISO 14496-12) states that sample offsets should be unsigned integers in
        // version 0 trun boxes, however a significant number of streams violate the spec and use
        // signed integers instead. It's safe to always parse sample offsets as signed integers
        // here, because unsigned integers will still be parsed correctly (unless their top bit is
        // set, which is never true in practice because sample offsets are always small).
        int sampleOffset = trun.readInt();
        sampleCompositionTimeOffsetTable[i] = (int) ((sampleOffset * 1000) / timescale);
      } else {
        sampleCompositionTimeOffsetTable[i] = 0;
      }
      sampleDecodingTimeTable[i] =
          Util.scaleLargeTimestamp(cumulativeTime, 1000, timescale) - edtsOffset;
      sampleSizeTable[i] = sampleSize;
      sampleIsSyncFrameTable[i] = ((sampleFlags >> 16) & 0x1) == 0
          && (!workaroundEveryVideoFrameIsSyncFrame || i == 0);
      cumulativeTime += sampleDuration;
    }
    fragment.nextFragmentDecodeTime = cumulativeTime;
  }

  private static void parseUuid(ParsableByteArray uuid, TrackFragment out,
      byte[] extendedTypeScratch) throws ParserException {
    uuid.setPosition(Atom.HEADER_SIZE);
    uuid.readBytes(extendedTypeScratch, 0, 16);

    // Currently this parser only supports Microsoft's PIFF SampleEncryptionBox.
    if (!Arrays.equals(extendedTypeScratch, PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE)) {
      return;
    }

    // Except for the extended type, this box is identical to a SENC box. See "Portable encoding of
    // audio-video objects: The Protected Interoperable File Format (PIFF), John A. Bocharov et al,
    // Section 5.3.2.1."
    parseSenc(uuid, 16, out);
  }

  private static void parseSenc(ParsableByteArray senc, TrackFragment out) throws ParserException {
    parseSenc(senc, 0, out);
  }

  private static void parseSenc(ParsableByteArray senc, int offset, TrackFragment out)
      throws ParserException {
    senc.setPosition(Atom.HEADER_SIZE + offset);
    int fullAtom = senc.readInt();
    int flags = Atom.parseFullAtomFlags(fullAtom);

    if ((flags & 0x01 /* override_track_encryption_box_parameters */) != 0) {
      // TODO: Implement this.
      throw new ParserException("Overriding TrackEncryptionBox parameters is unsupported.");
    }

    boolean subsampleEncryption = (flags & 0x02 /* use_subsample_encryption */) != 0;
    int sampleCount = senc.readUnsignedIntToInt();
    if (sampleCount != out.length) {
      throw new ParserException("Length mismatch: " + sampleCount + ", " + out.length);
    }

    Arrays.fill(out.sampleHasSubsampleEncryptionTable, 0, sampleCount, subsampleEncryption);
    out.initEncryptionData(senc.bytesLeft());
    out.fillEncryptionData(senc);
  }

  private static void parseSgpd(ParsableByteArray sbgp, ParsableByteArray sgpd, TrackFragment out)
      throws ParserException {
    sbgp.setPosition(Atom.HEADER_SIZE);
    int sbgpFullAtom = sbgp.readInt();
    if (sbgp.readInt() != SAMPLE_GROUP_TYPE_seig) {
      // Only seig grouping type is supported.
      return;
    }
    if (Atom.parseFullAtomVersion(sbgpFullAtom) == 1) {
      sbgp.skipBytes(4);
    }
    if (sbgp.readInt() != 1) {
      throw new ParserException("Entry count in sbgp != 1 (unsupported).");
    }

    sgpd.setPosition(Atom.HEADER_SIZE);
    int sgpdFullAtom = sgpd.readInt();
    if (sgpd.readInt() != SAMPLE_GROUP_TYPE_seig) {
      // Only seig grouping type is supported.
      return;
    }
    int sgpdVersion = Atom.parseFullAtomVersion(sgpdFullAtom);
    if (sgpdVersion == 1) {
      if (sgpd.readUnsignedInt() == 0) {
        throw new ParserException("Variable length decription in sgpd found (unsupported)");
      }
    } else if (sgpdVersion >= 2) {
      sgpd.skipBytes(4);
    }
    if (sgpd.readUnsignedInt() != 1) {
      throw new ParserException("Entry count in sgpd != 1 (unsupported).");
    }
    // CencSampleEncryptionInformationGroupEntry
    sgpd.skipBytes(2);
    boolean isProtected = sgpd.readUnsignedByte() == 1;
    if (!isProtected) {
      return;
    }
    int initVectorSize = sgpd.readUnsignedByte();
    byte[] keyId = new byte[16];
    sgpd.readBytes(keyId, 0, keyId.length);
    out.definesEncryptionData = true;
    out.trackEncryptionBox = new TrackEncryptionBox(isProtected, initVectorSize, keyId);
  }

  protected void parseEmsg(ParsableByteArray atom, long inputPosition) throws ParserException {
    // Do nothing.
  }

  /**
   * Parses a sidx atom (defined in 14496-12).
   */
  private static ChunkIndex parseSidx(ParsableByteArray atom, long inputPosition)
      throws ParserException {
    atom.setPosition(Atom.HEADER_SIZE);
    int fullAtom = atom.readInt();
    int version = Atom.parseFullAtomVersion(fullAtom);

    atom.skipBytes(4);
    long timescale = atom.readUnsignedInt();
    long earliestPresentationTime;
    long offset = inputPosition;
    if (version == 0) {
      earliestPresentationTime = atom.readUnsignedInt();
      offset += atom.readUnsignedInt();
    } else {
      earliestPresentationTime = atom.readUnsignedLongToLong();
      offset += atom.readUnsignedLongToLong();
    }

    atom.skipBytes(2);

    int referenceCount = atom.readUnsignedShort();
    int[] sizes = new int[referenceCount];
    long[] offsets = new long[referenceCount];
    long[] durationsUs = new long[referenceCount];
    long[] timesUs = new long[referenceCount];

    long time = earliestPresentationTime;
    long timeUs = Util.scaleLargeTimestamp(time, C.MICROS_PER_SECOND, timescale);
    for (int i = 0; i < referenceCount; i++) {
      int firstInt = atom.readInt();

      int type = 0x80000000 & firstInt;
      if (type != 0) {
        throw new ParserException("Unhandled indirect reference");
      }
      long referenceDuration = atom.readUnsignedInt();

      sizes[i] = 0x7FFFFFFF & firstInt;
      offsets[i] = offset;

      // Calculate time and duration values such that any rounding errors are consistent. i.e. That
      // timesUs[i] + durationsUs[i] == timesUs[i + 1].
      timesUs[i] = timeUs;
      time += referenceDuration;
      timeUs = Util.scaleLargeTimestamp(time, C.MICROS_PER_SECOND, timescale);
      durationsUs[i] = timeUs - timesUs[i];

      atom.skipBytes(4);
      offset += sizes[i];
    }

    return new ChunkIndex(sizes, offsets, durationsUs, timesUs);
  }

  private void readEncryptionData(ExtractorInput input) throws IOException, InterruptedException {
    TrackBundle nextTrackBundle = null;
    long nextDataOffset = Long.MAX_VALUE;
    int trackBundlesSize = trackBundles.size();
    for (int i = 0; i < trackBundlesSize; i++) {
      TrackFragment trackFragment = trackBundles.valueAt(i).fragment;
      if (trackFragment.sampleEncryptionDataNeedsFill
          && trackFragment.auxiliaryDataPosition < nextDataOffset) {
        nextDataOffset = trackFragment.auxiliaryDataPosition;
        nextTrackBundle = trackBundles.valueAt(i);
      }
    }
    if (nextTrackBundle == null) {
      parserState = STATE_READING_SAMPLE_START;
      return;
    }
    int bytesToSkip = (int) (nextDataOffset - input.getPosition());
    if (bytesToSkip < 0) {
      throw new ParserException("Offset to encryption data was negative.");
    }
    input.skipFully(bytesToSkip);
    nextTrackBundle.fragment.fillEncryptionData(input);
  }

  /**
   * Attempts to extract the next sample in the current mdat atom.
   * <p>
   * If there are no more samples in the current mdat atom then the parser state is transitioned
   * to {@link #STATE_READING_ATOM_HEADER} and {@code false} is returned.
   * <p>
   * It is possible for a sample to be extracted in part in the case that an exception is thrown. In
   * this case the method can be called again to extract the remainder of the sample.
   *
   * @param input The {@link ExtractorInput} from which to read data.
   * @return True if a sample was extracted. False otherwise.
   * @throws IOException If an error occurs reading from the input.
   * @throws InterruptedException If the thread is interrupted.
   */
  private boolean readSample(ExtractorInput input) throws IOException, InterruptedException {
    if (parserState == STATE_READING_SAMPLE_START) {
      if (currentTrackBundle == null) {
        currentTrackBundle = getNextFragmentRun(trackBundles);
        if (currentTrackBundle == null) {
          // We've run out of samples in the current mdat. Discard any trailing data and prepare to
          // read the header of the next atom.
          int bytesToSkip = (int) (endOfMdatPosition - input.getPosition());
          if (bytesToSkip < 0) {
            throw new ParserException("Offset to end of mdat was negative.");
          }
          input.skipFully(bytesToSkip);
          enterReadingAtomHeaderState();
          return false;
        }

        long nextDataPosition = currentTrackBundle.fragment.dataPosition;
        // We skip bytes preceding the next sample to read.
        int bytesToSkip = (int) (nextDataPosition - input.getPosition());
        if (bytesToSkip < 0) {
          throw new ParserException("Offset to sample data was negative.");
        }
        input.skipFully(bytesToSkip);
      }
      sampleSize = currentTrackBundle.fragment
          .sampleSizeTable[currentTrackBundle.currentSampleIndex];
      if (currentTrackBundle.fragment.definesEncryptionData) {
        sampleBytesWritten = appendSampleEncryptionData(currentTrackBundle);
        sampleSize += sampleBytesWritten;
      } else {
        sampleBytesWritten = 0;
      }
      parserState = STATE_READING_SAMPLE_CONTINUE;
      sampleCurrentNalBytesRemaining = 0;
    }

    TrackFragment fragment = currentTrackBundle.fragment;
    Track track = currentTrackBundle.track;
    TrackOutput output = currentTrackBundle.output;
    int sampleIndex = currentTrackBundle.currentSampleIndex;
    if (track.nalUnitLengthFieldLength != -1) {
      // Zero the top three bytes of the array that we'll use to parse nal unit lengths, in case
      // they're only 1 or 2 bytes long.
      byte[] nalLengthData = nalLength.data;
      nalLengthData[0] = 0;
      nalLengthData[1] = 0;
      nalLengthData[2] = 0;
      int nalUnitLengthFieldLength = track.nalUnitLengthFieldLength;
      int nalUnitLengthFieldLengthDiff = 4 - track.nalUnitLengthFieldLength;
      // NAL units are length delimited, but the decoder requires start code delimited units.
      // Loop until we've written the sample to the track output, replacing length delimiters with
      // start codes as we encounter them.
      while (sampleBytesWritten < sampleSize) {
        if (sampleCurrentNalBytesRemaining == 0) {
          // Read the NAL length so that we know where we find the next one.
          input.readFully(nalLength.data, nalUnitLengthFieldLengthDiff, nalUnitLengthFieldLength);
          nalLength.setPosition(0);
          sampleCurrentNalBytesRemaining = nalLength.readUnsignedIntToInt();
          // Write a start code for the current NAL unit.
          nalStartCode.setPosition(0);
          output.sampleData(nalStartCode, 4);
          sampleBytesWritten += 4;
          sampleSize += nalUnitLengthFieldLengthDiff;
        } else {
          // Write the payload of the NAL unit.
          int writtenBytes = output.sampleData(input, sampleCurrentNalBytesRemaining, false);
          sampleBytesWritten += writtenBytes;
          sampleCurrentNalBytesRemaining -= writtenBytes;
        }
      }
    } else {
      while (sampleBytesWritten < sampleSize) {
        int writtenBytes = output.sampleData(input, sampleSize - sampleBytesWritten, false);
        sampleBytesWritten += writtenBytes;
      }
    }

    long sampleTimeUs = fragment.getSamplePresentationTime(sampleIndex) * 1000L;
    int sampleFlags = (fragment.definesEncryptionData ? C.SAMPLE_FLAG_ENCRYPTED : 0)
        | (fragment.sampleIsSyncFrameTable[sampleIndex] ? C.SAMPLE_FLAG_SYNC : 0);
    int sampleDescriptionIndex = fragment.header.sampleDescriptionIndex;
    byte[] encryptionKey = null;
    if (fragment.definesEncryptionData) {
      encryptionKey = fragment.trackEncryptionBox != null
          ? fragment.trackEncryptionBox.keyId
          : track.sampleDescriptionEncryptionBoxes[sampleDescriptionIndex].keyId;
    }
    output.sampleMetadata(sampleTimeUs, sampleFlags, sampleSize, 0, encryptionKey);

    currentTrackBundle.currentSampleIndex++;
    if (currentTrackBundle.currentSampleIndex == fragment.length) {
      currentTrackBundle = null;
    }
    parserState = STATE_READING_SAMPLE_START;
    return true;
  }

  /**
   * Returns the {@link TrackBundle} whose fragment run has the earliest file position out of those
   * yet to be consumed, or null if all have been consumed.
   */
  private static TrackBundle getNextFragmentRun(SparseArray<TrackBundle> trackBundles) {
    TrackBundle nextTrackBundle = null;
    long nextTrackRunOffset = Long.MAX_VALUE;

    int trackBundlesSize = trackBundles.size();
    for (int i = 0; i < trackBundlesSize; i++) {
      TrackBundle trackBundle = trackBundles.valueAt(i);
      if (trackBundle.currentSampleIndex == trackBundle.fragment.length) {
        // This track fragment contains no more runs in the next mdat box.
      } else {
        long trunOffset = trackBundle.fragment.dataPosition;
        if (trunOffset < nextTrackRunOffset) {
          nextTrackBundle = trackBundle;
          nextTrackRunOffset = trunOffset;
        }
      }
    }
    return nextTrackBundle;
  }

  /**
   * Appends the corresponding encryption data to the {@link TrackOutput} contained in the given
   * {@link TrackBundle}.
   *
   * @param trackBundle The {@link TrackBundle} that contains the {@link Track} for which the
   *     Sample encryption data must be output.
   * @return The number of written bytes.
   */
  private int appendSampleEncryptionData(TrackBundle trackBundle) {
    TrackFragment trackFragment = trackBundle.fragment;
    ParsableByteArray sampleEncryptionData = trackFragment.sampleEncryptionData;
    int sampleDescriptionIndex = trackFragment.header.sampleDescriptionIndex;
    TrackEncryptionBox encryptionBox = trackFragment.trackEncryptionBox != null
        ? trackFragment.trackEncryptionBox
        : trackBundle.track.sampleDescriptionEncryptionBoxes[sampleDescriptionIndex];
    int vectorSize = encryptionBox.initializationVectorSize;
    boolean subsampleEncryption = trackFragment
        .sampleHasSubsampleEncryptionTable[trackBundle.currentSampleIndex];

    // Write the signal byte, containing the vector size and the subsample encryption flag.
    encryptionSignalByte.data[0] = (byte) (vectorSize | (subsampleEncryption ? 0x80 : 0));
    encryptionSignalByte.setPosition(0);
    TrackOutput output = trackBundle.output;
    output.sampleData(encryptionSignalByte, 1);
    // Write the vector.
    output.sampleData(sampleEncryptionData, vectorSize);
    // If we don't have subsample encryption data, we're done.
    if (!subsampleEncryption) {
      return 1 + vectorSize;
    }
    // Write the subsample encryption data.
    int subsampleCount = sampleEncryptionData.readUnsignedShort();
    sampleEncryptionData.skipBytes(-2);
    int subsampleDataLength = 2 + 6 * subsampleCount;
    output.sampleData(sampleEncryptionData, subsampleDataLength);
    return 1 + vectorSize + subsampleDataLength;
  }

  /** Returns whether the extractor should parse a leaf atom with type {@code atom}. */
  private static boolean shouldParseLeafAtom(int atom) {
    return atom == Atom.TYPE_hdlr || atom == Atom.TYPE_mdhd || atom == Atom.TYPE_mvhd
        || atom == Atom.TYPE_sidx || atom == Atom.TYPE_stsd || atom == Atom.TYPE_tfdt
        || atom == Atom.TYPE_tfhd || atom == Atom.TYPE_tkhd || atom == Atom.TYPE_trex
        || atom == Atom.TYPE_trun || atom == Atom.TYPE_pssh || atom == Atom.TYPE_saiz
        || atom == Atom.TYPE_saio || atom == Atom.TYPE_senc || atom == Atom.TYPE_sbgp
        || atom == Atom.TYPE_sgpd || atom == Atom.TYPE_uuid || atom == Atom.TYPE_elst
        || atom == Atom.TYPE_mehd || atom == Atom.TYPE_emsg;
  }

  /** Returns whether the extractor should parse a container atom with type {@code atom}. */
  private static boolean shouldParseContainerAtom(int atom) {
    return atom == Atom.TYPE_moov || atom == Atom.TYPE_trak || atom == Atom.TYPE_mdia
        || atom == Atom.TYPE_minf || atom == Atom.TYPE_stbl || atom == Atom.TYPE_moof
        || atom == Atom.TYPE_traf || atom == Atom.TYPE_mvex || atom == Atom.TYPE_edts;
  }

  /**
   * Holds data corresponding to a single track.
   */
  private static final class TrackBundle {

    public final TrackFragment fragment;
    public final TrackOutput output;

    public Track track;
    public DefaultSampleValues defaultSampleValues;
    public int currentSampleIndex;

    public TrackBundle(TrackOutput output) {
      fragment = new TrackFragment();
      this.output = output;
    }

    public void init(Track track, DefaultSampleValues defaultSampleValues) {
      this.track = Assertions.checkNotNull(track);
      this.defaultSampleValues = Assertions.checkNotNull(defaultSampleValues);
      output.format(track.mediaFormat);
      reset();
    }

    public void reset() {
      fragment.reset();
      currentSampleIndex = 0;
    }

  }

}
