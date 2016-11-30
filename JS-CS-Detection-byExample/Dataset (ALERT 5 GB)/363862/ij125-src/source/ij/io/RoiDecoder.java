package ij.io;
import ij.gui.*;
import java.io.*;
import java.util.*;
import java.net.*;

/*	ImageJ/NIH Image 64 byte ROI outline header
	2 byte numbers are big-endian signed shorts
	
	0-3		"Iout"
	4-5		version (>=217)
	6-7		roi type
	8-9		top
	10-11	left
	12-13	bottom
	14-15	right
	16-17	NCoordinate
	18-33	x1,y1,x2,y2 (float, unused)
	34-35	line width (unused)
	36-63	reserved (zero)
*/

/** Decodes an ImageJ, NIH Image or Scion Image ROI. */
public class RoiDecoder {

	private final int polygon=0, rect=1, oval=2, line=3,freeLine=4, segLine=5, noRoi=6,freehand=7, traced=8;
	private byte[] data;
	private String path;

	public RoiDecoder(String path) {
		this.path = path;
	}

	/** Returns the ROI. */
	public Roi getRoi() throws IOException {
		File f = new File(path);
		int size = (int)f.length();
		if (size>5000)
			throw new IOException("This is not an ImageJ ROI");
		FileInputStream fis = new FileInputStream(path);
		data = new byte[size];

		int total = 0;
		while (total<size)
			total += fis.read(data, total, size-total);
		if (getByte(0)!=73 || getByte(1)!=111)  //"Iout"
			throw new IOException("This is not an ImageJ ROI");
		int type = getByte(6);
		int top= getShort(8);
		int left = getShort(10);
		int bottom = getShort(12);
		int right = getShort(14);
		int width = right-left;
		int height = bottom-top;
		int n = getShort(16);

		Roi roi = null;
		switch (type) {
		case rect:
			roi = new Roi(left, top, width, height, null);
			break;
		case oval:
			roi = new OvalRoi(left, top, width, height, null);
			break;
		case line:
			int x1 = (int)getFloat(18);		
			int y1 = (int)getFloat(22);		
			int x2 = (int)getFloat(26);		
			int y2 = (int)getFloat(30);		
			//IJ.write("line roi: "+x1+" "+y1+" "+x2+" "+y2);
			break;
		case polygon: case freehand: case traced:
				//IJ.write("type: "+type);
				//IJ.write("n: "+n);
				//IJ.write("rect: "+left+","+top+" "+width+" "+height);
				if (n==0) break;
				int[] x = new int[n];
				int[] y = new int[n];
				int base1 = 64;
				int base2 = base1+2*n;
				int xtmp, ytmp;
				for (int i=0; i<n; i++) {
					xtmp = getShort(base1+i*2);
					if (xtmp<0) xtmp = 0;
					ytmp = getShort(base2+i*2);
					if (ytmp<0) ytmp = 0;
					x[i] = left+xtmp;
					y[i] = top+ytmp;
					//IJ.write(i+" "+getShort(base1+i*2)+" "+getShort(base2+i*2));
				}
				int roiType;
				if (type==polygon)
					roiType = Roi.POLYGON;
				else if (type==freehand)
					roiType = Roi.FREEROI;
				else
					roiType = Roi.TRACED_ROI;
				roi = new PolygonRoi(x, y, n, null, roiType);
				break;
		default:
		}
		return roi;
	}
	
	int getByte(int base) {
		return data[base]&255;
	}

	int getShort(int base) {
		int b0 = data[base]&255;
		int b1 = data[base+1]&255;
		return (short)((b0<<8) + b1);		
	}
	
	int getInt(int base) {
		int b0 = data[base]&255;
		int b1 = data[base+1]&255;
		int b2 = data[base+2]&255;
		int b3 = data[base+3]&255;
		return ((b0<<24) + (b1<<16) + (b2<<8) + b3);
	}

	float getFloat(int base) {
		return Float.intBitsToFloat(getInt(base));
	}

}