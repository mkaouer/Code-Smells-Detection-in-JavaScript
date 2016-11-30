package ij.plugin.filter;
import ij.*;
import ij.gui.*;
import ij.process.*;
import java.awt.*;

/** This plugin implements the Invert, Smooth, Sharpen, Detect Edges, 
	Add Noise and Reduce Noise commands. */
public class Filters implements PlugInFilter {
	
	private String arg;
	private ImagePlus imp;
	private static double sd = 25.0;
	private int slice;
	private boolean canceled;

	public int setup(String arg, ImagePlus imp) {
		this.arg = arg;
		this.imp = imp;
		if (imp!=null) {
			Roi roi = imp.getRoi();
			if (roi!=null && roi.getType()>Roi.TRACED_ROI)
				imp.killRoi(); // ignore any line selection
		}
		return IJ.setupDialog(imp, DOES_ALL+SUPPORTS_MASKING);
	}

	public void run(ImageProcessor ip) {
	
		if (arg.equals("invert")) {
	 		ip.invert();
	 		return;
	 	}
	 	
		if (arg.equals("smooth")) {
	 		ip.smooth();
	 		return;
	 	}
	 	
		if (arg.equals("sharpen")) {
	 		ip.sharpen();
	 		return;
	 	}
	 	
		if (arg.equals("edge")) {
			ip.findEdges();
	 		return;
		}
						
	 	if (arg.equals("add")) {
	 		ip.noise(25.0);
	 		return;
	 	}
	 	
	 	if (arg.equals("noise")) {
	 		if (canceled)
	 			return;
	 		slice++;
	 		if (slice==1) {
				GenericDialog gd = new GenericDialog("Gaussian Noise");
				gd.addNumericField("Standard Deviation:", sd, 2);
				gd.showDialog();
				if (gd.wasCanceled()) {
					canceled = true;
					return;
				}
				sd = gd.getNextNumber();
			}
	 		ip.noise(sd);
	 		IJ.register(Filters.class);
	 		return;
	 	}
	 	
		if (arg.equals("median")) {
			if (imp.getBitDepth()==16 || imp.getBitDepth()==32) {
				imp.unlock();
				IJ.run("Median...", "radius=1.5");			
			} else
				ip.medianFilter();
	 		return;
		}

	}

}
