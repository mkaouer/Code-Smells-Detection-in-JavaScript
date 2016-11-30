package ij.plugin.filter;
import java.awt.*;
import java.util.*;
import ij.*;
import ij.gui.*;
import ij.process.*;
import ij.text.*;
import ij.measure.*;
import ij.io.*;
import ij.util.Tools;

/** This plugin implements the Image/Show Info command. */
public class Info implements PlugInFilter {
    private ImagePlus imp;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL+NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		String info = getImageInfo(imp, ip);
		if (info.indexOf("----")>0)
			showInfo(info, 400, 500);
		else
			showInfo(info, 300, 300);
	}

	public String getImageInfo(ImagePlus imp, ImageProcessor ip) {
		String infoProperty = null;
		if (imp.getStackSize()>1) {
			ImageStack stack = imp.getStack();
			String label = stack.getSliceLabel(imp.getCurrentSlice());
			if (label!=null && label.indexOf('\n')>0)
				infoProperty = label;
		}
		if (infoProperty==null)
			infoProperty = (String)imp.getProperty("Info");
		String info = getInfo(imp, ip);
		if (infoProperty!=null)
			return infoProperty + "\n------------------------\n" + info;
		else
			return info;		
	}

	String getInfo(ImagePlus imp, ImageProcessor ip) {
		String s = new String("\n");
		s += "Title: " + imp.getTitle() + "\n";
		Calibration cal = imp.getCalibration();
    	int nSlices = imp.getStackSize();
		int digits = imp.getBitDepth()==32?4:0;
		if (cal.scaled()) {
			String unit = cal.getUnit();
			String units = cal.getUnits();
	    	s += "Width:  "+IJ.d2s(imp.getWidth()*cal.pixelWidth,2)+" " + units+" ("+imp.getWidth()+")\n";
	    	s += "Height:  "+IJ.d2s(imp.getHeight()*cal.pixelHeight,2)+" " + units+" ("+imp.getHeight()+")\n";
	    	if (nSlices>1)
	    		s += "Depth:  "+IJ.d2s(nSlices*cal.pixelDepth,2)+" " + units+" ("+nSlices+")\n";	    			    	
	    	if (nSlices>1)
	    		s += "Voxel size: "+IJ.d2s(cal.pixelWidth,2) + "x" + IJ.d2s(cal.pixelHeight,2)+"x"+IJ.d2s(cal.pixelDepth,2) + "\n";	    		
	    	double xResolution = 1.0/cal.pixelWidth;
	    	double yResolution = 1.0/cal.pixelHeight;
	    	int places = Tools.getDecimalPlaces(xResolution, yResolution);
	    	if (xResolution==yResolution)
	    		s += "Resolution:  "+IJ.d2s(xResolution,places) + " pixels per "+unit+"\n";
	    	else {
	    		s += "X Resolution:  "+IJ.d2s(xResolution,places) + " pixels per "+unit+"\n";
	    		s += "Y Resolution:  "+IJ.d2s(yResolution,places) + " pixels per "+unit+"\n";
	    	}
	    } else {
	    	s += "Width:  " + imp.getWidth() + " pixels\n";
	    	s += "Height:  " + imp.getHeight() + " pixels\n";
	    	if (nSlices>1)
	    		s += "Depth:  " + nSlices + " pixels\n";
	    }
	    String zOrigin = nSlices>1||cal.zOrigin!=0.0?","+d2s(cal.zOrigin):"";
	    s += "Coordinate origin:  " + d2s(cal.xOrigin)+","+d2s(cal.yOrigin)+zOrigin+"\n";
	    int type = imp.getType();
    	switch (type) {
	    	case ImagePlus.GRAY8:
	    		s += "Bits per pixel: 8 ";
	    		String lut = "LUT";
	    		if (imp.getProcessor().isColorLut())
	    			lut = "color " + lut;
	    		else
	    			lut = "grayscale " + lut;
	    		if (imp.isInvertedLut())
	    			lut = "inverting " + lut;
	    		s += "(" + lut + ")\n";
	    		break;
	    	case ImagePlus.GRAY16: case ImagePlus.GRAY32:
	    		if (type==ImagePlus.GRAY16) {
	    			String sign = cal.isSigned16Bit()?"signed":"unsigned";
	    			s += "Bits per pixel: 16 ("+sign+")\n";
	    		} else
	    			s += "Bits per pixel: 32 (float)\n";
				s += "Display range: ";
				double min = ip.getMin();
				double max = ip.getMax();
	    		if (cal.calibrated()) {
	    			min = cal.getCValue((int)min);
	    			max = cal.getCValue((int)max);
	    		}
		    	s += IJ.d2s(min,digits) + " - " + IJ.d2s(max,digits) + "\n";
	    		break;
	    	case ImagePlus.COLOR_256:
	    		s += "Bits per pixel: 8 (color LUT)\n";
	    		break;
	    	case ImagePlus.COLOR_RGB:
	    		s += "Bits per pixel: 32 (RGB)\n";
	    		break;
    	}
		double interval = cal.frameInterval;		
    	if (nSlices>1) {
    		ImageStack stack = imp.getStack();
    		int slice = imp.getCurrentSlice();
    		String number = slice + "/" + nSlices;
    		String label = stack.getShortSliceLabel(slice);
    		if (label!=null && label.length()>0)
    			label = " (" + label + ")";
    		else
    			label = "";
			if (interval>0.0) {
				s += "Frame: " + number + label + "\n";
				if (interval<1.0) {
					double rate = 1.0/interval;
					String sRate = Math.abs(rate-Math.round(rate))<0.00001?IJ.d2s(rate,0):IJ.d2s(rate,5);
					s += "Frame rate: " + sRate + " fps\n";
				} else
					s += "Frame interval: " + IJ.d2s(interval,5) + " seconds\n";
			} else
				s += "Slice: " + number + label + "\n";
		}

		if (ip.getMinThreshold()==ip.NO_THRESHOLD)
	    	s += "No Threshold\n";
	    else {
	    	double lower = ip.getMinThreshold();
	    	double upper = ip.getMaxThreshold();
			int dp = digits;
			if (cal.calibrated()) {
				lower = cal.getCValue((int)lower);
				upper = cal.getCValue((int)upper);
				dp = cal.isSigned16Bit()?0:4;
			}
			s += "Threshold: "+IJ.d2s(lower,dp)+"-"+IJ.d2s(upper,dp)+"\n";
		}
		ImageWindow win = imp.getWindow();
		ImageCanvas ic = win!=null?win.getCanvas():null;
    	double mag = ic!=null?ic.getMagnification():1.0;
    	if (mag!=1.0)
			s += "Magnification: " + mag + "\n";
			
	    if (cal.calibrated()) {
	    	s += " \n";
	    	int curveFit = cal.getFunction();
			s += "Calibration Function: ";
			if (curveFit==Calibration.UNCALIBRATED_OD)
				s += "Uncalibrated OD\n";	    	
			else
				s += CurveFitter.fList[curveFit]+"\n";
			double[] c = cal.getCoefficients();
			if (c!=null) {
				s += "  a: "+IJ.d2s(c[0],6)+"\n";
				s += "  b: "+IJ.d2s(c[1],6)+"\n";
				if (c.length>=3)
					s += "  c: "+IJ.d2s(c[2],6)+"\n";
				if (c.length>=4)
					s += "  c: "+IJ.d2s(c[3],6)+"\n";
				if (c.length>=5)
					s += "  c: "+IJ.d2s(c[4],6)+"\n";
			}
			s += "  Unit: \""+cal.getValueUnit()+"\"\n";	    	
	    } else
	    	s += "Uncalibrated\n";

	    FileInfo fi = imp.getOriginalFileInfo();
		if (fi!=null) {
			if (fi.directory!=null && fi.fileName!=null) {
				s += "Path: " + fi.directory + fi.fileName + "\n";
			}
			if (fi.url!=null && !fi.url.equals("")) {
				s += "URL: " + fi.url + "\n";
			}
		}
	    
	    Roi roi = imp.getRoi();
	    if (roi == null) {
			if (cal.calibrated())
	    		s += " \n";
	    	s += "No Selection\n";
	    } else {
	    	s += " \n";
	    	s += roi.getTypeAsString()+" Selection";
    		String name = roi.getName();
    		if (name!=null)
				s += " (\"" + name + "\")";
			s += "\n";			
	    	Rectangle r = roi.getBounds();
	    	if (roi instanceof Line) {
	    		Line line = (Line)roi;
	    		s += "  X1: " + IJ.d2s(line.x1d*cal.pixelWidth) + "\n";
	    		s += "  Y1: " + IJ.d2s(yy(line.y1d,imp)*cal.pixelHeight) + "\n";
	    		s += "  X2: " + IJ.d2s(line.x2d*cal.pixelWidth) + "\n";
	    		s += "  Y2: " + IJ.d2s(yy(line.y2d,imp)*cal.pixelHeight) + "\n";
	    	
			} else if (cal.scaled()) {
				s += "  X: " + IJ.d2s(r.x*cal.pixelWidth) + " (" + r.x + ")\n";
				s += "  Y: " + IJ.d2s(yy(r.y,imp)*cal.pixelHeight) + " (" +  r.y + ")\n";
				s += "  Width: " + IJ.d2s(r.width*cal.pixelWidth) + " (" +  r.width + ")\n";
				s += "  Height: " + IJ.d2s(r.height*cal.pixelHeight) + " (" +  r.height + ")\n";
			} else {
				s += "  X: " + r.x + "\n";
				s += "  Y: " + yy(r.y,imp) + "\n";
				s += "  Width: " + r.width + "\n";
				s += "  Height: " + r.height + "\n";
	    	}
	    }
	    
		return s;
	}
	
    String d2s(double n) {
		return n==(int)n?Integer.toString((int)n):IJ.d2s(n);
	}

	// returns a Y coordinate based on the "Invert Y Coodinates" flag
	int yy(int y, ImagePlus imp) {
		return Analyzer.updateY(y, imp.getHeight());
	}

	// returns a Y coordinate based on the "Invert Y Coodinates" flag
	double yy(double y, ImagePlus imp) {
		return Analyzer.updateY(y, imp.getHeight());
	}

	void showInfo(String info, int width, int height) {
		new TextWindow("Info for "+imp.getTitle(), info, width, height);
	}
	
}
