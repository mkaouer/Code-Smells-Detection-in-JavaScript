package ij.plugin;
import java.awt.*;
import java.io.*;
import ij.*;
import ij.io.*;
import ij.process.*;
import ij.measure.*;

/**	Opens and displays FITS images. ImageJ does not support
	signed 16-bits so 16-bit FITS images are converted to
	unsigned by adding 32768. The FITS format is described at
	"ftp://nssdc.gsfc.nasa.gov/pub/fits".
*/
public class FITS extends ImagePlus implements PlugIn {

	public void run(String arg) {
		OpenDialog od = new OpenDialog("Open FITS...", arg);
		String directory = od.getDirectory();
		String fileName = od.getFileName();
		if (fileName==null)
			return;
		IJ.showStatus("Opening: " + directory + fileName);
		FitsDecoder decoder = new FitsDecoder(directory, fileName);
		FileInfo fi = null;
		try {fi = decoder.getInfo();}
		catch (IOException e) {}
		if (fi!=null && fi.width>0 && fi.height>0 && fi.offset>0) {
			FileOpener fo = new FileOpener(fi);
			ImagePlus imp = fo.open(false);
			ImageProcessor ip = imp.getProcessor();
			setProcessor(fileName, ip);
			setCalibration(imp.getCalibration());
			setProperty("Info", decoder.getHeaderInfo());
			if (arg.equals("")) show();
		} else
			IJ.error("This does not appear to be a FITS file.");
		IJ.showStatus("");
	}
	
}

class FitsDecoder {
	private String directory, fileName;
	private DataInputStream f;
	private StringBuffer info = new StringBuffer(512);
	private double bscale, bzero;

	public FitsDecoder(String directory, String fileName) {
		this.directory = directory;
		this.fileName = fileName;
	}

	FileInfo getInfo() throws IOException {
		FileInfo fi = new FileInfo();
		fi.fileFormat = fi.FITS;
		fi.fileName = fileName;
		fi.directory = directory;
		fi.width = 0;
		fi.height = 0;
		fi.offset = 0;
		
		f = new DataInputStream(new FileInputStream(directory + fileName));
		String s = getString(80);
		info.append(s+"\n");
		if (!s.startsWith("SIMPLE"))
			{f.close(); return null;}
		int count = 1;
		do {
			count++;
			s = getString(80);
			info.append(s+"\n");
			if (s.startsWith("BITPIX")) {
				int bitsPerPixel = getInteger(s);
				if (bitsPerPixel==8)
					fi.fileType = FileInfo.GRAY8;
				else if (bitsPerPixel==16)
					fi.fileType = FileInfo.GRAY16_SIGNED;
				else if (bitsPerPixel==32)
					fi.fileType = FileInfo.GRAY32_INT;
				else if (bitsPerPixel==-32)
					fi.fileType = FileInfo.GRAY32_FLOAT;
				else {
					IJ.error("BITPIX must be 8, 16, 32 or -32 (float).");
					f.close();
					return null;
				}
			} else if (s.startsWith("NAXIS1"))
				fi.width = getInteger(s);
			else if (s.startsWith("NAXIS2"))
				fi.height = getInteger(s);
			else if (s.startsWith("BSCALE"))
				bscale = getFloat(s);
			else if (s.startsWith("BZERO"))
				bzero = getFloat(s);
			if (count>360)
				{f.close(); return null;}
		} while (!s.startsWith("END"));
		f.close();
		fi.offset = 2880+2880*((count*80)/2880);
		if (fi.fileType==FileInfo.GRAY16_SIGNED && !(bscale==1.0&&bzero==32768.0)) {
			double[] coeff = new double[2];
			coeff[0] = -32768.0;
			coeff[1] = 1.0;
    		fi.calibrationFunction = Calibration.STRAIGHT_LINE;
     		fi.coefficients = coeff;
    		fi.valueUnit = "gray value";
		}
		return fi;
	}

	String getString(int length) throws IOException {
		byte[] b = new byte[length];
		f.read(b);
		return new String(b);
	}

	int getInteger(String s) {
		s = s.substring(10, 30);
		s = s.trim();
		return Integer.parseInt(s);
	}

	double getFloat(String s) {
		s = s.substring(10, 30);
		s = s.trim();
		Double d;
		try {d = new Double(s);}
		catch (NumberFormatException e){d = null;}
		if (d!=null)
			return(d.doubleValue());
		else
			return 0.0;
	}

	String getHeaderInfo() {
		return new String(info);
	}

}
