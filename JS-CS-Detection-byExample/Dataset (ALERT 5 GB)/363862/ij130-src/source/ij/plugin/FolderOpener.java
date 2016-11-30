package ij.plugin;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.awt.event.*;
import ij.*;
import ij.io.*;
import ij.gui.*;
import ij.process.*;

/** Opens a folder of images as a stack. */
public class FolderOpener implements PlugIn {

	private static boolean grayscale;
	private static boolean halfSize;
	private int n, start, increment;
	private String filter;
	private FileInfo fi;
	private String info1;

	public void run(String arg) {
		OpenDialog od = new OpenDialog("Open Image Sequence...", arg);
		String directory = od.getDirectory();
		String name = od.getFileName();
		if (name==null)
			return;

		String[] list = new File(directory).list();
		if (list==null)
			return;
		IJ.register(FolderOpener.class);
		ij.util.StringSorter.sort(list);
		if (IJ.debugMode) IJ.log("FolderOpener: "+directory+" ("+list.length+" files)");
		int width=0,height=0,type=0;
		ImageStack stack = null;
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		
		try {
			for (int i=0; i<list.length; i++) {
				if (list[i].endsWith(".txt"))
					continue;
				ImagePlus imp = new Opener().openImage(directory, list[i]);
				if (imp!=null) {
					width = imp.getWidth();
					height = imp.getHeight();
					type = imp.getType();
					fi = imp.getOriginalFileInfo();
					if (!showDialog(imp, list))
						return;
					break;
				}
			}
			if (width==0) {
				IJ.showMessage("Import Sequence", "This folder does not appear to contain any TIFF,\n"
				+ "JPEG, BMP, DICOM, GIF, FITS or PGM files.");
				return;
			}

			if (n<1)
				n = list.length;
			if (start<1 || start>list.length)
				start = 1;
			if (start+n-1>list.length)
				n = list.length-start+1;
			int filteredImages = n;
			if (filter!=null && (filter.equals("") || filter.equals("*")))
				filter = null;
			if (filter!=null) {
				filteredImages = 0;
  				for (int i=start-1; i<start-1+n; i++) {
 					if (list[i].indexOf(filter)>=0)
 						filteredImages++;
 				}
  				if (filteredImages==0) {
  					IJ.error("None of the "+n+" files contain\n the string '"+filter+"' in their name.");
  					return;
  				}
  			}
  			n = filteredImages;
  			
			int count = 0;
			int counter = 0;
			for (int i=start-1; i<list.length; i++) {
				if (list[i].endsWith(".txt"))
					continue;
				if (filter!=null && (list[i].indexOf(filter)<0))
					continue;
				if ((counter++%increment)!=0)
					continue;
				ImagePlus imp = new Opener().openImage(directory, list[i]);
				if (imp!=null && stack==null) {
					width = imp.getWidth();
					height = imp.getHeight();
					type = imp.getType();
					ColorModel cm = imp.getProcessor().getColorModel();
					if (halfSize)
						stack = new ImageStack(width/2, height/2, cm);
					else
						stack = new ImageStack(width, height, cm);
					info1 = (String)imp.getProperty("Info");
				}
				if (imp==null) {
					if (!list[i].startsWith("."))
						IJ.log(list[i] + ": unable to open");
				} else if (imp.getWidth()!=width || imp.getHeight()!=height)
					IJ.log(list[i] + ": wrong dimensions");
				else if (imp.getType()!=type)
					IJ.log(list[i] + ": wrong type");
				else {
					count = stack.getSize()+1;
					IJ.showStatus(count+"/"+n);
					IJ.showProgress((double)count/n);
					ImageProcessor ip = imp.getProcessor();
					if (grayscale) {
						ImageConverter ic = new ImageConverter(imp);
						ic.convertToGray8();
						ip = imp.getProcessor();
					}
					if (halfSize)
						ip = ip.resize(width/2, height/2);
					if (ip.getMin()<min) min = ip.getMin();
					if (ip.getMax()>max) max = ip.getMax();
					String label = imp.getTitle();
					String info = (String)imp.getProperty("Info");
					if (info!=null)
						label += "\n" + info;
					stack.addSlice(label, ip);
				}
				if (count>=n)
					break;
				//System.gc();
			}
		} catch(OutOfMemoryError e) {
			IJ.outOfMemory("FolderOpener");
			stack.trim();
		}
		if (stack!=null && stack.getSize()>0) {
			ImagePlus imp2 = new ImagePlus("Stack", stack);
			if (imp2.getType()==ImagePlus.GRAY16 || imp2.getType()==ImagePlus.GRAY32)
				imp2.getProcessor().setMinAndMax(min, max);
			imp2.setFileInfo(fi); // saves FileInfo of the first image
			if (imp2.getStackSize()==1 && info1!=null)
				imp2.setProperty("Info", info1);
			imp2.show();
		}
		IJ.showProgress(1.0);
	}
	
	boolean showDialog(ImagePlus imp, String[] list) {
		int fileCount = list.length;
		FolderOpenerDialog gd = new FolderOpenerDialog("Sequence Options", imp, list);
		gd.addNumericField("Number of Images: ", fileCount, 0);
		gd.addNumericField("Starting Image: ", 1, 0);
		gd.addNumericField("Increment: ", 1, 0);
		gd.addStringField("File Name Contains: ", "");
		gd.addCheckbox("Convert to 8-bit Grayscale", grayscale);
		gd.addCheckbox("Open 1/2 Size", halfSize);
		gd.addMessage("10000 x 10000 x 1000 (100.3MB)");
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		n = (int)gd.getNextNumber();
		start = (int)gd.getNextNumber();
		increment = (int)gd.getNextNumber();
		if (increment<1)
			increment = 1;
		filter = gd.getNextString();
		grayscale = gd.getNextBoolean();
		halfSize = gd.getNextBoolean();
		return true;
	}

}

class FolderOpenerDialog extends GenericDialog {
	ImagePlus imp;
	int fileCount;
 	boolean eightBits, halfSize;
 	String saveFilter = "";
 	String[] list;

	public FolderOpenerDialog(String title, ImagePlus imp, String[] list) {
		super(title);
		this.imp = imp;
		this.list = list;
		this.fileCount = list.length;
	}

	protected void setup() {
		setStackInfo();
	}
 	
	public void itemStateChanged(ItemEvent e) {
 		setStackInfo();
	}
	
	public void textValueChanged(TextEvent e) {
 		setStackInfo();
	}

	void setStackInfo() {
		int width = imp.getWidth();
		int height = imp.getHeight();
		int bytesPerPixel = 1;
 		eightBits = ((Checkbox)checkbox.elementAt(0)).getState();
 		halfSize = ((Checkbox)checkbox.elementAt(1)).getState();
 		int n = getNumber(numberField.elementAt(0));
		int start = getNumber(numberField.elementAt(1));
		int inc = getNumber(numberField.elementAt(2));
		if (n<1)
			n = fileCount;
		if (start<1 || start>fileCount)
			start = 1;
		if (start+n-1>fileCount) {
			n = fileCount-start+1;
			//TextField tf = (TextField)numberField.elementAt(0);
			//tf.setText(""+nImages);
		}
		if (inc<1)
			inc = 1;
 		TextField tf = (TextField)stringField.elementAt(0);
 		String filter = tf.getText();
		// IJ.write(nImages+" "+startingImage);
 		if (!filter.equals("") && !filter.equals("*")) {
 			int n2 = n;
 			n = 0;
 			for (int i=start-1; i<start-1+n2; i++)
 				if (list[i].indexOf(filter)>=0) {
 					n++;
 					//IJ.write(n+" "+list[i]);
				}
   			saveFilter = filter;
 		}
		switch (imp.getType()) {
			case ImagePlus.GRAY16:
				bytesPerPixel=2;break;
			case ImagePlus.COLOR_RGB:
			case ImagePlus.GRAY32:
				bytesPerPixel=4; break;
		}
		if (eightBits)
			bytesPerPixel = 1;
		if (halfSize) {
			width /= 2;
			height /= 2;
		}
		int n2 = n/inc;
		if (n2<0)
			n2 = 0;
		double size = (double)(width*height*n2*bytesPerPixel)/(1024*1024);
 		((Label)theLabel).setText(width+" x "+height+" x "+n2+" ("+IJ.d2s(size,1)+"MB)");
	}

	public int getNumber(Object field) {
		TextField tf = (TextField)field;
		String theText = tf.getText();
		double value;
		Double d;
		try {d = new Double(theText);}
		catch (NumberFormatException e){
			d = null;
		}
		if (d!=null)
			return (int)d.doubleValue();
		else
			return 0;
      }

}
