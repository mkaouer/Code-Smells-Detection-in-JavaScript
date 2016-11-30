package ij.plugin.filter;
import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import java.awt.event.*;

/** Implements the "Plot Profile" command. */
public class Profiler implements PlugInFilter {

	ImagePlus imp;
	static boolean verticalProfile;

	public int setup(String arg, ImagePlus imp) {
		if (arg.equals("set"))
			{doOptions(); return DONE;}
		this.imp = imp;
		return DOES_ALL+NO_UNDO+NO_CHANGES+ROI_REQUIRED;
	}

	public void run(ImageProcessor ip) {
		boolean averageHorizontally = verticalProfile || IJ.altKeyDown();
		new ProfilePlot(imp, averageHorizontally).createWindow();
	}

	public void doOptions() {
		double ymin = ProfilePlot.getFixedMin();
		double ymax = ProfilePlot.getFixedMax();
		boolean fixedScale = ymin!=0.0 || ymax!=0.0;
		boolean wasFixedScale = fixedScale;
		
		GenericDialog gd = new GenericDialog("Profile Plot Options", IJ.getInstance());
		gd.addNumericField("Width (pixels):", PlotWindow.plotWidth, 0);
		gd.addNumericField("Height (pixels):", PlotWindow.plotHeight, 0);
		gd.addNumericField("Minimum Y:", ymin, 2);
		gd.addNumericField("Maximum Y:", ymax, 2);
		gd.addCheckbox("Fixed Y-axis Scale", fixedScale);
		gd.addCheckbox("Do Not Save X-Values", !PlotWindow.saveXValues);
		gd.addCheckbox("Auto-close", PlotWindow.autoClose);
		gd.addCheckbox("Vertical Profile", verticalProfile);
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int w = (int)gd.getNextNumber();
		int h = (int)gd.getNextNumber();
		if (w<300) w = 300;
		if (w>screen.width-140) w = screen.width-140;
		if (h<100) h = 100;
		if (h>screen.height-300) h = screen.height-300;
		PlotWindow.plotWidth = w;
		PlotWindow.plotHeight = h;
		ymin = gd.getNextNumber();
		ymax = gd.getNextNumber();
		fixedScale = gd.getNextBoolean();
		PlotWindow.saveXValues = !gd.getNextBoolean();
		PlotWindow.autoClose = gd.getNextBoolean();
		verticalProfile = gd.getNextBoolean();
		if (!fixedScale && !wasFixedScale && (ymin!=0.0 || ymax!=0.0))
			fixedScale = true;
		if (!fixedScale) {
			ymin = 0.0;
			ymax = 0.0;
		}
		ProfilePlot.setMinAndMax(ymin, ymax);
		IJ.register(Profiler.class);
	}
		
}

