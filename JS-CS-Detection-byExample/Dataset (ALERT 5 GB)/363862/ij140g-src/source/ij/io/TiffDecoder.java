package ij.io;
import ij.util.Tools;
import java.io.*;
import java.util.*;
import java.net.*;

/**
Decodes single and multi-image TIFF files. LZW decompression
code contributed by Curtis Rueden.
*/
public class TiffDecoder {

	// tags
	public static final int NEW_SUBFILE_TYPE = 254;
	public static final int IMAGE_WIDTH = 256;
	public static final int IMAGE_LENGTH = 257;
	public static final int BITS_PER_SAMPLE = 258;
	public static final int COMPRESSION = 259;
	public static final int PHOTO_INTERP = 262;
	public static final int IMAGE_DESCRIPTION = 270;
	public static final int STRIP_OFFSETS = 273;
	public static final int SAMPLES_PER_PIXEL = 277;
	public static final int ROWS_PER_STRIP = 278;
	public static final int STRIP_BYTE_COUNT = 279;
	public static final int X_RESOLUTION = 282;
	public static final int Y_RESOLUTION = 283;
	public static final int PLANAR_CONFIGURATION = 284;
	public static final int RESOLUTION_UNIT = 296;
	public static final int SOFTWARE = 305;
	public static final int DATE_TIME = 306;
	public static final int PREDICTOR = 317;
	public static final int COLOR_MAP = 320;
	public static final int SAMPLE_FORMAT = 339;
	public static final int METAMORPH1 = 33628;
	public static final int METAMORPH2 = 33629;
	public static final int IPLAB = 34122;
	public static final int NIH_IMAGE_HDR = 43314;
	public static final int META_DATA_BYTE_COUNTS = 50838; // private tag registered with Adobe
	public static final int META_DATA = 50839; // private tag registered with Adobe
	
	//constants
	static final int UNSIGNED = 1;
	static final int SIGNED = 2;
	static final int FLOATING_POINT = 3;

	//field types
	static final int SHORT = 3;
	static final int LONG = 4;

	// metadata types
	static final int MAGIC_NUMBER = 0x494a494a;  // "IJIJ"
	static final int INFO = 0x696e666f;  // "info" (Info image property)
	static final int LABELS = 0x6c61626c;  // "labl" (slice labels)
	static final int RANGES = 0x72616e67;  // "rang" (display ranges)
	static final int LUTS = 0x6c757473;  // "luts" (channel LUTs)

	private String directory;
	private String name;
	private String url;
	protected RandomAccessStream in;
	protected boolean debugMode;
	private boolean littleEndian;
	private String dInfo;
	private int ifdCount;
	private int[] metaDataCounts;
		
	public TiffDecoder(String directory, String name) {
		this.directory = directory;
		this.name = name;
	}

	public TiffDecoder(InputStream in, String name) {
		directory = "";
		this.name = name;
		url = "";
		this.in = new RandomAccessStream(in);
	}

	final int getInt() throws IOException {
		int b1 = in.read();
		int b2 = in.read();
		int b3 = in.read();
		int b4 = in.read();
		if (littleEndian)
			return ((b4 << 24) + (b3 << 16) + (b2 << 8) + (b1 << 0));
		else
			return ((b1 << 24) + (b2 << 16) + (b3 << 8) + b4);
	}

	int getShort() throws IOException {
		int b1 = in.read();
		int b2 = in.read();
		if (littleEndian)
			return ((b2 << 8) + b1);
		else
			return ((b1 << 8) + b2);
	}

	int OpenImageFileHeader() throws IOException {
	// Open 8-byte Image File Header at start of file.
	// Returns the offset in bytes to the first IFD or -1
	// if this is not a valid tiff file.
		int byteOrder = in.readShort();
		if (byteOrder==0x4949) // "II"
			littleEndian = true;
		else if (byteOrder==0x4d4d) // "MM"
			littleEndian = false;
		else {
			in.close();
			return -1;
		}
		int magicNumber = getShort(); // 42
		int offset = getInt();
		return offset;
	}
		
	int getValue(int fieldType, int count) throws IOException {
		int value = 0;
		int unused;
		if (fieldType==SHORT && count==1) {
				value = getShort();
				unused = getShort();
		}
		else
			value = getInt();
		return value;
	}	
	
	void getColorMap(int offset, FileInfo fi) throws IOException {
		byte[] colorTable16 = new byte[768*2];
		long saveLoc = in.getLongFilePointer();
		in.seek(offset);
		in.readFully(colorTable16);
		in.seek(saveLoc);
		fi.lutSize = 256;
		fi.reds = new byte[256];
		fi.greens = new byte[256];
		fi.blues = new byte[256];
		int j = 0;
		if (littleEndian)
			j++;
		for (int i=0; i<256; i++) {
			fi.reds[i] = colorTable16[j];
			fi.greens[i] = colorTable16[512+j];
			fi.blues[i] = colorTable16[1024+j];
			j += 2;
		}
		fi.fileType = FileInfo.COLOR8;
	}
	
	byte[] getString(int count, int offset) throws IOException {
		count--; // skip null byte at end of string
		if (count<=0)
			return null;
		byte[] bytes = new byte[count];
		long saveLoc = in.getLongFilePointer();
		in.seek(offset);
		in.readFully(bytes);
		in.seek(saveLoc);
		return bytes;
	}

	/** Save the image description in the specified FileInfo. ImageJ
		saves spatial and density calibration data in this string. For
		stacks, it also saves the number of images to avoid having to
		decode an IFD for each image. */
	public void saveImageDescription(byte[] description, FileInfo fi) {
		if (description.length<7)
			return;
        String id = new String(description);
		fi.description = id;
        int index1 = id.indexOf("images=");
        if (index1>0) {
            int index2 = id.indexOf("\n", index1);
            if (index2>0) {
                String images = id.substring(index1+7,index2);
                int n = (int)Tools.parseDouble(images, 0.0);
                if (n>1) fi.nImages = n;
            }
        }
	}

	void decodeNIHImageHeader(int offset, FileInfo fi) throws IOException {
		long saveLoc = in.getLongFilePointer();
		
		in.seek(offset+12);
		int version = in.readShort();
		
		in.seek(offset+160);
		double scale = in.readDouble();
		if (version>106 && scale!=0.0) {
			fi.pixelWidth = 1.0/scale;
			fi.pixelHeight = fi.pixelWidth;
		} 

		// spatial calibration
		in.seek(offset+172);
		int units = in.readShort();
		if (version<=153) units += 5;
		switch (units) {
			case 5: fi.unit = "nanometer"; break;
			case 6: fi.unit = "micrometer"; break;
			case 7: fi.unit = "mm"; break;
			case 8: fi.unit = "cm"; break;
			case 9: fi.unit = "meter"; break;
			case 10: fi.unit = "km"; break;
			case 11: fi.unit = "inch"; break;
			case 12: fi.unit = "ft"; break;
			case 13: fi.unit = "mi"; break;
		}

		// density calibration
		in.seek(offset+182);
		int fitType = in.read();
		int unused = in.read();
		int nCoefficients = in.readShort();
		if (fitType==11) {
			fi.calibrationFunction = 21; //Calibration.UNCALIBRATED_OD
			fi.valueUnit = "U. OD";
		} else if (fitType>=0 && fitType<=8 && nCoefficients>=1 && nCoefficients<=5) {
			switch (fitType) {
				case 0: fi.calibrationFunction = 0; break; //Calibration.STRAIGHT_LINE
				case 1: fi.calibrationFunction = 1; break; //Calibration.POLY2
				case 2: fi.calibrationFunction = 2; break; //Calibration.POLY3
				case 3: fi.calibrationFunction = 3; break; //Calibration.POLY4
				case 5: fi.calibrationFunction = 4; break; //Calibration.EXPONENTIAL
				case 6: fi.calibrationFunction = 5; break; //Calibration.POWER
				case 7: fi.calibrationFunction = 6; break; //Calibration.LOG
				case 8: fi.calibrationFunction = 10; break; //Calibration.RODBARD2 (NIH Image)
			}
			fi.coefficients = new double[nCoefficients];
			for (int i=0; i<nCoefficients; i++) {
				fi.coefficients[i] = in.readDouble();
			}
			in.seek(offset+234);
			int size = in.read();
			StringBuffer sb = new StringBuffer();
			if (size>=1 && size<=16) {
				for (int i=0; i<size; i++)
					sb.append((char)(in.read()));
				fi.valueUnit = new String(sb);
			} else
				fi.valueUnit = " ";
		}
			
		in.seek(offset+260);
		int nImages = in.readShort();
		if(nImages>=2 && (fi.fileType==FileInfo.GRAY8||fi.fileType==FileInfo.COLOR8)) {
			fi.nImages = nImages;
			fi.pixelDepth = in.readFloat();	//SliceSpacing
			int skip = in.readShort();		//CurrentSlice
			fi.frameInterval = in.readFloat();
			//ij.IJ.write("fi.pixelDepth: "+fi.pixelDepth);
		}
			
		in.seek(offset+272);
		float aspectRatio = in.readFloat();
		if (version>140 && aspectRatio!=0.0)
			fi.pixelHeight = fi.pixelWidth/aspectRatio;
		
		in.seek(saveLoc);
	}
	
	void dumpTag(int tag, int count, int value, FileInfo fi) {
		String name;
		switch (tag) {
			case NEW_SUBFILE_TYPE: name="NewSubfileType"; break;
			case IMAGE_WIDTH: name="ImageWidth"; break;
			case IMAGE_LENGTH: name="ImageLength"; break;
			case STRIP_OFFSETS: name="StripOffsets"; break;
			case PHOTO_INTERP: name="PhotoInterp"; break;
			case IMAGE_DESCRIPTION: name="ImageDescription"; break;
			case BITS_PER_SAMPLE: name="BitsPerSample"; break;
			case SAMPLES_PER_PIXEL: name="SamplesPerPixel"; break;
			case ROWS_PER_STRIP: name="RowsPerStrip"; break;
			case STRIP_BYTE_COUNT: name="StripByteCount"; break;
			case X_RESOLUTION: name="XResolution"; break;
			case Y_RESOLUTION: name="YResolution"; break;
			case RESOLUTION_UNIT: name="ResolutionUnit"; break;
			case SOFTWARE: name="Software"; break;
			case DATE_TIME: name="DateTime"; break;
			case PLANAR_CONFIGURATION: name="PlanarConfiguration"; break;
			case COMPRESSION: name="Compression"; break; 
			case PREDICTOR: name="Predictor"; break; 
			case COLOR_MAP: name="ColorMap"; break; 
			case SAMPLE_FORMAT: name="SampleFormat"; break; 
			case NIH_IMAGE_HDR: name="NIHImageHeader"; break; 
			case META_DATA_BYTE_COUNTS: name="MetaDataByteCounts"; break; 
			case META_DATA: name="MetaData"; break; 
			default: name="???"; break;
		}
		String cs = (count==1)?"":", count=" + count;
		dInfo += "    " + tag + ", \"" + name + "\", value=" + value + cs + "\n";
		//ij.IJ.log(tag + ", \"" + name + "\", value=" + value + cs + "\n");
	}

	double getRational(int loc) throws IOException {
		long saveLoc = in.getLongFilePointer();
		in.seek(loc);
		int numerator = getInt();
		int denominator = getInt();
		in.seek(saveLoc);
		//System.out.println("numerator: "+numerator);
		//System.out.println("denominator: "+denominator);
		if (denominator!=0)
			return (double)numerator/denominator;
		else
			return 0.0;
	}
	
	FileInfo OpenIFD() throws IOException {
	// Get Image File Directory data
		int tag, fieldType, count, value;
		int nEntries = getShort();
		if (nEntries<1 || nEntries>1000)
			return null;
		ifdCount++;
		FileInfo fi = new FileInfo();
		for (int i=0; i<nEntries; i++) {
			tag = getShort();
			fieldType = getShort();
			count = getInt();
			value = getValue(fieldType, count);
			if (debugMode && ifdCount<10) dumpTag(tag, count, value, fi);
			//ij.IJ.write(i+"/"+nEntries+" "+tag + ", count=" + count + ", value=" + value);
			//if (tag==0) return null;
			switch (tag) {
				case IMAGE_WIDTH: 
					fi.width = value;
					break;
				case IMAGE_LENGTH: 
					fi.height = value;
					break;
 				case STRIP_OFFSETS: 
					if (count==1)
						fi.stripOffsets = new int[] {value};
					else {
						long saveLoc = in.getLongFilePointer();
						in.seek(value);
						fi.stripOffsets = new int[count];
						for (int c=0; c<count; c++) {
							fi.stripOffsets[c] = getInt();
							if (c > 0 && fi.stripOffsets[c] < fi.stripOffsets[c - 1] && fi.stripOffsets[c]!=0)
								error("Strip offsets are not in order");
						}
						in.seek(saveLoc);
					}
					fi.offset = count > 0 ? fi.stripOffsets[0] : value;
					break;
				case STRIP_BYTE_COUNT:
					if (count==1)
						fi.stripLengths = new int[] {value};
					else {
						long saveLoc = in.getLongFilePointer();
						in.seek(value);
						fi.stripLengths = new int[count];
						for (int c=0; c<count; c++)
							fi.stripLengths[c] = getInt();
						in.seek(saveLoc);
					}
					break;
 				case PHOTO_INTERP:
 					fi.whiteIsZero = value==0;
					break;
				case BITS_PER_SAMPLE:
						if (count==1) {
							if (value==8)
								fi.fileType = FileInfo.GRAY8;
							else if (value==16) {
								fi.fileType = FileInfo.GRAY16_UNSIGNED;
								fi.intelByteOrder = littleEndian;
							} else if (value==32) {
								fi.fileType = FileInfo.GRAY32_INT;
								fi.intelByteOrder = littleEndian;
							} else if (value==12) {
								fi.fileType = FileInfo.GRAY12_UNSIGNED;
								fi.intelByteOrder = littleEndian;
							} else if (value==1)
								fi.fileType = FileInfo.BITMAP;
							else
								error("Unsupported BitsPerSample: " + value);
						} else if (count==3) {
							long saveLoc = in.getLongFilePointer();
							in.seek(value);
							int bitDepth = getShort();
							if (!(bitDepth==8||bitDepth==16))
								error("ImageJ can only open 8 and 16 bit/channel RGB images ("+bitDepth+")");
							if (bitDepth==16) {
								fi.intelByteOrder = littleEndian;
								fi.fileType = FileInfo.RGB48;
							}
							in.seek(saveLoc);
						}
						break;
				case SAMPLES_PER_PIXEL:
					fi.samplesPerPixel = value;
					if (value==3 && fi.fileType!=FileInfo.RGB48)
						fi.fileType = FileInfo.RGB;
					break;
				case X_RESOLUTION:
					double xScale = getRational(value); 
					if (xScale!=0.0) fi.pixelWidth = 1.0/xScale; 
					break;
				case Y_RESOLUTION:
					double yScale = getRational(value); 
					if (yScale!=0.0) fi.pixelHeight = 1.0/yScale; 
					break;
				case RESOLUTION_UNIT:
					if (value==1&&fi.unit==null)
						fi.unit = " ";
					else if (value==2) {
						if (fi.pixelWidth==1.0/72.0) {
							fi.pixelWidth = 1.0;
							fi.pixelHeight = 1.0;
						} else
							fi.unit = "inch";
					} else if (value==3)
						fi.unit = "cm";
					break;
				case PLANAR_CONFIGURATION:
					if (value==2 && fi.fileType==FileInfo.RGB48)
							 fi.fileType = FileInfo.GRAY16_UNSIGNED;
					if (value==2 && fi.fileType==FileInfo.RGB)
						fi.fileType = FileInfo.RGB_PLANAR;
					if (value!=2 && !((fi.samplesPerPixel==1)||(fi.samplesPerPixel==3))) {
						String msg = "Unsupported interleaved SamplesPerPixel: " + fi.samplesPerPixel;
						if (value==4)
							msg += " \n \n" + "ImageJ cannot open CMYK and RGB+alpha TIFFs";
						error(msg);
					}
					break;
				case COMPRESSION:
					if (value==5) { // LZW compression is handled
						int bpp = fi.getBytesPerPixel();
						if (bpp==6)
							error("ImageJ cannot open 48-bit LZW compressed TIFFs");
						fi.compression = FileInfo.LZW;
					} else if (value!=1 && !(value==7&&fi.width<500)) {
						// don't abort with Spot camera compressed (7) thumbnails
						// otherwise, this is an unknown compression type
						fi.compression = FileInfo.COMPRESSION_UNKNOWN;
						error("ImageJ cannot open TIFF files " +
							"compressed in this fashion ("+value+")");
					}
					break;
				case PREDICTOR:
					if (value==2 && fi.compression==FileInfo.LZW)
						fi.compression = FileInfo.LZW_WITH_DIFFERENCING;
					break;
				case COLOR_MAP: 
					if (count==768 && fi.fileType==fi.GRAY8)
						getColorMap(value, fi);
					break;
				case SAMPLE_FORMAT:
					if (fi.fileType==FileInfo.GRAY32_INT && value==FLOATING_POINT)
						fi.fileType = FileInfo.GRAY32_FLOAT;
					if (fi.fileType==FileInfo.GRAY16_UNSIGNED) {
						if (value==SIGNED)
							fi.fileType = FileInfo.GRAY16_SIGNED;
						if (value==FLOATING_POINT)
							error("ImageJ cannot open16-bit float TIFFs");
					}
					break;
				case IMAGE_DESCRIPTION: 
					if (ifdCount==1) {
						byte[] s = getString(count,value);
						if (s!=null) saveImageDescription(s,fi);
					}
					break;
				case METAMORPH1: case METAMORPH2:
					if (name.indexOf(".STK")>0 || name.indexOf(".stk")>0) {
						if (tag==METAMORPH2)
							fi.nImages=count;
						else
							fi.nImages=9999;
					}
					break;
				case IPLAB: 
					fi.nImages=value;
					break;
				case NIH_IMAGE_HDR: 
					if (count==256)
						decodeNIHImageHeader(value, fi);
					break;
 				case META_DATA_BYTE_COUNTS: 
					long saveLoc = in.getLongFilePointer();
					in.seek(value);
					metaDataCounts = new int[count];
					for (int c=0; c<count; c++)
						metaDataCounts[c] = getInt();
					in.seek(saveLoc);
					break;
 				case META_DATA: 
 					getMetaData(value, fi);
 					break;
				default:
					if (tag>10000 && tag<32768 && ifdCount>1)
						return null;
			}
		}
		fi.fileFormat = fi.TIFF;
		fi.fileName = name;
		fi.directory = directory;
		if (url!=null)
			fi.url = url;
		return fi;
	}

	void getMetaData(int loc, FileInfo fi) throws IOException {
		if (metaDataCounts==null || metaDataCounts.length==0)
			return;
		int maxTypes = 10;
		long saveLoc = in.getLongFilePointer();
		in.seek(loc);
		int n = metaDataCounts.length;
		int hdrSize = metaDataCounts[0];
		if (hdrSize<12 || hdrSize>804)
			{in.seek(saveLoc); return;}
		int magicNumber = getInt();
		if (magicNumber!=MAGIC_NUMBER)  // "IJIJ"
			{in.seek(saveLoc); return;}
		int nTypes = (hdrSize-4)/8;
		int[] types = new int[nTypes];
		int[] counts = new int[nTypes];
		
		if (debugMode) dInfo += "Metadata:\n";
		int extraMetaDataEntries = 0;
		for (int i=0; i<nTypes; i++) {
			types[i] = getInt();
			counts[i] = getInt();
			if (types[i]<0xffffff)
				extraMetaDataEntries += counts[i];
			if (debugMode) {
				String id = "";
				if (types[i]==INFO) id = " (Info property)";
				if (types[i]==LABELS) id = " (slice labels)";
				if (types[i]==RANGES) id = " (display ranges)";
				if (types[i]==LUTS) id = " (luts)";
				dInfo += "   "+i+" "+Integer.toHexString(types[i])+" "+counts[i]+id+"\n";
			}
		}
		fi.metaDataTypes = new int[extraMetaDataEntries];
		fi.metaData = new byte[extraMetaDataEntries][];
		int start = 1;
		int eMDindex = 0;
		for (int i=0; i<nTypes; i++) {
			if (types[i]==INFO)
				getInfoProperty(start, fi);
			else if (types[i]==LABELS)
				getSliceLabels(start, start+counts[i]-1, fi);
			else if (types[i]==RANGES)
				getDisplayRanges(start, fi);
			else if (types[i]==LUTS)
				getLuts(start, start+counts[i]-1, fi);
			else if (types[i]<0xffffff) {
				for (int j=start; j<start+counts[i]; j++) { 
					int len = metaDataCounts[j]; 
					fi.metaData[eMDindex] = new byte[len]; 
					in.readFully(fi.metaData[eMDindex], len); 
					fi.metaDataTypes[eMDindex] = types[i]; 
					eMDindex++; 
				} 
			} else
				skipUnknownType(start, start+counts[i]-1);
			start += counts[i];
		}
		in.seek(saveLoc);
	}

	void getInfoProperty(int first, FileInfo fi) throws IOException {
		int len = metaDataCounts[first];
	    byte[] buffer = new byte[len];
		in.readFully(buffer, len);
		len /= 2;
		char[] chars = new char[len];
		for (int j=0, k=0; j<len; j++)
			chars[j] = (char)((buffer[k++]<<8) + buffer[k++]);
		fi.info = new String(chars);
	}

	void getSliceLabels(int first, int last, FileInfo fi) throws IOException {
		fi.sliceLabels = new String[last-first+1];
	    int index = 0;
	    byte[] buffer = new byte[metaDataCounts[first]];
		for (int i=first; i<=last; i++) {
			int len = metaDataCounts[i];
			if (len>0) {
				if (len>buffer.length)
					buffer = new byte[len];
				in.readFully(buffer, len);
				len /= 2;
				char[] chars = new char[len];
				for (int j=0, k=0; j<len; j++)
					chars[j] = (char)((buffer[k++]<<8) + buffer[k++]);
				fi.sliceLabels[index++] = new String(chars);
				//ij.IJ.log(i+"  "+fi.sliceLabels[i-1]+"  "+len);
			} else
				fi.sliceLabels[index++] = null;
		}
	}

	void getDisplayRanges(int first, FileInfo fi) throws IOException {
		int n = metaDataCounts[first]/8;
		fi.displayRanges = new double[n];
		for (int i=0; i<n; i++)
			fi.displayRanges[i] = in.readDouble();
	}

	void getLuts(int first, int last, FileInfo fi) throws IOException {
		fi.channelLuts = new byte[last-first+1][];
	    int index = 0;
		for (int i=first; i<=last; i++) {
			int len = metaDataCounts[i];
			fi.channelLuts[index] = new byte[len];
            in.readFully(fi.channelLuts[index], len);
            index++;
		}
	}

	void error(String message) throws IOException {
		if (in!=null) in.close();
		throw new IOException(message);
	}
	
	void skipUnknownType(int first, int last) throws IOException {
	    byte[] buffer = new byte[metaDataCounts[first]];
		for (int i=first; i<=last; i++) {
			int len = metaDataCounts[i];
            if (len>buffer.length)
                buffer = new byte[len];
            in.readFully(buffer, len);
		}
	}

	public void enableDebugging() {
		debugMode = true;
	}
	
	
	public FileInfo[] getTiffInfo() throws IOException {
		int ifdOffset;
		Vector info;
				
		if (in==null)
			in = new RandomAccessStream(new RandomAccessFile(directory + name, "r"));
		info = new Vector();
		ifdOffset = OpenImageFileHeader();
		if (ifdOffset<0) {
			in.close();
			return null;
		}
		if (debugMode) dInfo = "\n  " + name + ": opening\n";
		while (ifdOffset>0) {
			in.seek(ifdOffset);
			FileInfo fi = OpenIFD();
			if (fi!=null) {
				info.addElement(fi);
				ifdOffset = getInt();
			} else
				ifdOffset = 0;
			if (debugMode && ifdCount<10) dInfo += "  nextIFD=" + ifdOffset + "\n";
			if (fi!=null) {
				if (fi.nImages>1) // ignore extra IFDs in ImageJ and NIH Image stacks
					ifdOffset = 0;
			}
		}
		if (info.size()==0) {
			in.close();
			return null;
		} else {
			FileInfo[] fi = new FileInfo[info.size()];
			info.copyInto((Object[])fi);
			if (debugMode) fi[0].debugInfo = dInfo;
			if (url!=null) {
				in.seek(0);
				fi[0].inputStream = in;
			} else
				in.close();
			return fi;
		}
	}

}
