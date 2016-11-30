package ij.plugin.frame;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import ij.*;
import ij.plugin.*;
import ij.process.*;
import ij.gui.*;
import ij.measure.*;

/** Adjusts brightness and contrast of the active image. This class is
	multi-threaded to provide a more responsive user interface. */
public class ContrastAdjuster extends PlugInFrame implements PlugIn, Runnable, ActionListener, AdjustmentListener {

	ContrastPlot plot = new ContrastPlot();
	Thread thread;
	
	int brightnessValue = -1;
	int contrastValue = -1;
	int sliderRange = 256;
	boolean doAutoAdjust,doReset,doHistogram,doApplyLut,doThreshold,doUpdate;
	
	Panel panel, tPanel;
	Button autoB, resetB, histB, applyB, threshB, updateB;
	int previousImageID;
	int previousType;
	ImageJ ij;
	double min, max;
	double defaultMin, defaultMax;
	int contrast, brightness;
	boolean RGBImage;
	Scrollbar contrastSlider, brightnessSlider;
	Label minLabel, maxLabel, brightnessLabel, contrastLabel;
	boolean done;

	public ContrastAdjuster() {
		super("B&C");
		ij = IJ.getInstance();
		Font monoFont = new Font("Monospaced", Font.PLAIN, 12);
		Font sanFont = new Font("SansSerif", Font.PLAIN, 12);
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(gridbag);
		
		// plot
		c.gridx = 0;
		int y = 0;
		c.gridy = y++;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(10, 10, 0, 10);
		gridbag.setConstraints(plot, c);
		add(plot);
		
		// min and max
		panel = new Panel();
		c.gridy = y++;
		c.insets = new Insets(0, 10, 0, 10);
		gridbag.setConstraints(panel, c);
    	panel.setLayout(new BorderLayout());
		minLabel = new Label("      ", Label.LEFT);
    	minLabel.setFont(monoFont);
		panel.add("West", minLabel);
		maxLabel = new Label("      " , Label.RIGHT);
    	maxLabel.setFont(monoFont);
		panel.add("East", maxLabel);
		add(panel);

		// brightness slider
		brightnessSlider = new Scrollbar(Scrollbar.HORIZONTAL, sliderRange/2, 1, 0, sliderRange);
		c.gridy = y++;
		c.insets = new Insets(5, 10, 0, 10);
		gridbag.setConstraints(brightnessSlider, c);
		add(brightnessSlider);
		brightnessSlider.addAdjustmentListener(this);
		brightnessSlider.setUnitIncrement(1);
		
		// brightness label
		panel = new Panel();
		c.gridy = y++;
		c.insets = new Insets(0, 10, 0, 10);
		gridbag.setConstraints(panel, c);
    	panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
    	Label label = new Label("Brightness");
    	label.setFont(sanFont);
		panel.add(label);
		brightnessLabel = new Label("", Label.LEFT);
    	brightnessLabel.setFont(monoFont);
		panel.add(brightnessLabel);
		add(panel);

		// contrast slider
		contrastSlider = new Scrollbar(Scrollbar.HORIZONTAL, sliderRange/2, 1, 0, sliderRange);
		c.gridy = y++;
		c.insets = new Insets(5, 10, 0, 10);
		gridbag.setConstraints(contrastSlider, c);
		add(contrastSlider);
		contrastSlider.addAdjustmentListener(this);
		contrastSlider.setUnitIncrement(1);
		
		// contrast label
		panel = new Panel();
		c.gridy = y++;
		c.insets = new Insets(0, 10, 0, 10);
		gridbag.setConstraints(panel, c);
    	panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
    	label = new Label("Contrast");
    	label.setFont(sanFont);
		panel.add(label);
		contrastLabel = new Label("", Label.LEFT);
    	contrastLabel.setFont(monoFont);
		panel.add(contrastLabel);
		add(panel);

		// buttons
		panel = new Panel();
		panel.setLayout(new GridLayout(3, 2, 0, 0));
		autoB = new Button("Auto");
		autoB.addActionListener(this);
		autoB.addKeyListener(ij);
		panel.add(autoB);
		resetB = new Button("Reset");
		resetB.addActionListener(this);
		resetB.addKeyListener(ij);
		panel.add(resetB);
		histB = new Button("Hist");
		histB.addActionListener(this);
		histB.addKeyListener(ij);
		panel.add(histB);
		applyB = new Button("Apply");
		applyB.addActionListener(this);
		applyB.addKeyListener(ij);
		panel.add(applyB);
		threshB = new Button("Thresh");
		threshB.addActionListener(this);
		threshB.addKeyListener(ij);
		panel.add(threshB);
		updateB = new Button("Update");
		updateB.addActionListener(this);
		updateB.addKeyListener(ij);
		panel.add(updateB);
		c.gridy = y++;
		c.insets = new Insets(10, 5, 10, 5);
		gridbag.setConstraints(panel, c);
		add(panel);
		
 		addKeyListener(ij);  // ImageJ handles keyboard shortcuts
		pack();
		GUI.center(this);
		setVisible(true);

		thread = new Thread(this, "ContrastAdjuster");
		//thread.setPriority(thread.getPriority()-1);
		thread.start();
	}
	
	public synchronized void adjustmentValueChanged(AdjustmentEvent e) {
		if (e.getSource()==contrastSlider)
			contrastValue = contrastSlider.getValue();
		else
			brightnessValue = brightnessSlider.getValue();
		notify();
	}

	public synchronized  void actionPerformed(ActionEvent e) {
		Button b = (Button)e.getSource();
		if (b==null) return;
		if (b==resetB)
			doReset = true;
		else if (b==autoB)
			doAutoAdjust = true;
		else if (b==histB)
			doHistogram = true;
		else if (b==applyB)
			doApplyLut = true;
		else if (b==threshB)
			doThreshold = true;
		else if (b==updateB)
			doUpdate = true;
		notify();
	}
	
	ImageProcessor setup(ImagePlus imp) {
		ImageProcessor ip;
		int type = imp.getType();
		RGBImage = type==ImagePlus.COLOR_RGB;
		boolean newIP = !imp.isProcessor(); // newIP true when an RGB image is reverted
		ip = imp.getProcessor();
		if (imp.getID()!=previousImageID||(RGBImage&&newIP)||type!=previousType) {
	 		if (RGBImage)
	 			ip.snapshot();
			if ((ip instanceof ShortProcessor) || (ip instanceof FloatProcessor)) {
				ip.resetMinAndMax();
				defaultMin = ip.getMin();
				defaultMax = ip.getMax();
			} else {
				defaultMin = 0;
				defaultMax = 255;
			}
			min = ip.getMin();
			max = ip.getMax();
			if (IJ.debugMode) {
				IJ.write("min: " + min);
				IJ.write("max: " + max);
				IJ.write("defaultMin: " + defaultMin);
				IJ.write("defaultMax: " + defaultMax);
			}
			plot.defaultMin = defaultMin;
			plot.defaultMax = defaultMax;
			plot.histogram = null;
		}
	 	previousImageID = imp.getID();
	 	previousType = type;
	 	return ip;
	}

	void updatePlot() {
		plot.min = min;
		plot.max = max;
		plot.repaint();
	}
	
	void updateLabels(ImageProcessor ip) {
		if (ip instanceof FloatProcessor) {
			minLabel.setText(""+IJ.d2s(ip.getMin()));
			maxLabel.setText(""+IJ.d2s(ip.getMax()));
		}
		else {
			minLabel.setText(""+(int)ip.getMin());
			maxLabel.setText(""+(int)ip.getMax());
		}
	}

	void updateScrollBars() {
		double mid = sliderRange/2;
		double c = ((defaultMax-defaultMin)/(max-min))*mid;
		if (c>mid)
			c = sliderRange - ((max-min)/(defaultMax-defaultMin))*mid;
		contrast = (int)c;
		contrastSlider.setValue(contrast);
		double level = min + (max-min)/2.0;
		double normalizedLevel = 1.0 - (level - defaultMin)/(defaultMax-defaultMin);
		brightness = (int)(normalizedLevel*sliderRange);
		//IJ.write("" + level + " " + brightness);
		brightnessSlider.setValue(brightness);
	}
	
	/** Restore image outside non-rectangular roi. */
  	void doMasking(ImagePlus imp, ImageProcessor ip) {
		int[] mask = imp.getMask();
		if (mask!=null)
			ip.reset(mask);
	}

	void adjustBrightness(ImagePlus imp, ImageProcessor ip, double bvalue) {
		double center = defaultMin + (defaultMax-defaultMin)*((sliderRange-bvalue)/sliderRange);
		double width = max-min;
		min = center - width/2.0;
		max = center + width/2.0;
		ip.setMinAndMax(min, max);
		if (min==max)
			setThreshold(ip);
		if (RGBImage) doMasking(imp, ip);
	}

	void adjustContrast(ImagePlus imp, ImageProcessor ip, int cvalue) {
		double slope;
		double center = min + (max-min)/2.0;
		double range = defaultMax-defaultMin;
		double mid = sliderRange/2;
		if (cvalue<=mid)
			slope = cvalue/mid;
		else
			slope = mid/(sliderRange-cvalue);
		if (slope>0.0) {
			min = center-(0.5*range)/slope;
			max = center+(0.5*range)/slope;
		}
		ip.setMinAndMax(min, max);
		if (RGBImage) doMasking(imp, ip);
	}

	void reset(ImageProcessor ip) {
 		if (RGBImage)
			ip.reset();
		if ((ip instanceof ShortProcessor) || (ip instanceof FloatProcessor)) {
			ip.resetMinAndMax();
			defaultMin = ip.getMin();
			defaultMax = ip.getMax();
			plot.defaultMin = defaultMin;
			plot.defaultMax = defaultMax;
		}
		min = defaultMin;
		max = defaultMax;
		ip.setMinAndMax(min, max);
		updateScrollBars();
	}

	void update(ImagePlus imp, ImageProcessor ip) {
		if (min==0.0 && max==0.0 || imp.getType()!=previousType)
			IJ.beep();
		else {
			ip.setMinAndMax(min, max);
			updateScrollBars();
		}
	}

	void plotHistogram(ImagePlus imp, ImageProcessor ip) {
 		if (RGBImage)
			reset(ip);
		plot.setHistogram(imp.getStatistics());
	}

	void apply(ImagePlus imp, ImageProcessor ip) {
		if (imp.getType()==ImagePlus.COLOR_RGB && imp.getStackSize()>1)
			{applyRGBStack(imp); return;}
		if (imp.getType()!=ImagePlus.GRAY8)
			{IJ.beep(); IJ.showStatus("Apply requires an 8-bit grayscale image or an RGB stack"); return;}
		int[] table = new int[256];
		LookUpTable lut = imp.createLut();
		IndexColorModel cm = (IndexColorModel)lut.getColorModel();
		boolean invertedLut = ((ByteProcessor)ip).isInvertedLut();
		for (int i=0; i<cm.getMapSize(); i++)
			table[i] = invertedLut?255-cm.getRed(i)&255:cm.getRed(i)&255;
		if (imp.getStackSize()>1) {
			ImageStack stack = imp.getStack();
			YesNoCancelDialog d = new YesNoCancelDialog(this,
				"Entire Stack?", "Apply LUT to all "+stack.getSize()+" slices in the stack?");
			if (d.cancelPressed())
				return;
			if (d.yesPressed())
				new StackProcessor(stack, ip).applyTable(table);
			else
				ip.applyTable(table);
		} else
			ip.applyTable(table);
		reset(ip);
		imp.changes = true;
		if (plot.histogram!=null)
			plotHistogram(imp, ip);
	}

	void applyRGBStack(ImagePlus imp) {
		int current = imp.getCurrentSlice();
		int n = imp.getStackSize();
		if (!IJ.showMessageWithCancel("Update Entire Stack?",
		"Apply brightness and contrast settings\n"+
		"to all "+n+" slices in the stack?\n \n"+
		"NOTE: There is no Undo for this operation."))
			return;
		for (int i=1; i<=n; i++) {
			if (i!=current) {
				imp.setSlice(i);
				ImageProcessor ip = imp.getProcessor();
				ip.setMinAndMax(min, max);
				IJ.showProgress((double)i/n);
			}
		}
		imp.setSlice(current);
		imp.changes = true;
	}

	void threshold(ImagePlus imp, ImageProcessor ip) {
		int threshold = (int)((defaultMax-defaultMin)/2.0);
		min = threshold;
		max = threshold;
		ip.setMinAndMax(min, max);
		setThreshold(ip);
		updateScrollBars();
	}

	void setThreshold(ImageProcessor ip) {
		if (!(ip instanceof ByteProcessor))
			return;
		if (((ByteProcessor)ip).isInvertedLut())
			ip.setThreshold(max, 255, ImageProcessor.NO_LUT_UPDATE);
		else
			ip.setThreshold(0, max, ImageProcessor.NO_LUT_UPDATE);
	}

	void autoAdjust(ImagePlus imp, ImageProcessor ip) {
 		if (RGBImage)
			ip.reset();
		Calibration cal = imp.getCalibration();
		imp.setCalibration(null);
		ImageStatistics stats = imp.getStatistics(); // get uncalibrated stats
		imp.setCalibration(cal);
		int[] histogram = stats.histogram;
		int threshold = stats.pixelCount/5000;
		int i = -1;
		boolean found = false;
		do {
			i++;
			found = histogram[i] > threshold;
		} while (!found && i<255);
		int hmin = i;
		i = 256;
		do {
			i--;
			found = histogram[i] > threshold;
		} while (!found && i>0);
		int hmax = i;
		if (hmax>hmin) {
			imp.killRoi();
			min = stats.histMin+hmin*stats.binSize;
			max = stats.histMin+hmax*stats.binSize;
			ip.setMinAndMax(min, max);
		}
		updateScrollBars();
		Roi roi = imp.getRoi();
		if (roi!=null) {
			int[] mask = roi.getMask();
			if (mask!=null)
				ip.reset(mask);
		}
	}
	
	static final int RESET=0, AUTO=1, HIST=2, APPLY=3, THRESHOLD=4, BRIGHTNESS=5, CONTRAST=6, UPDATE=7;

	// Separate thread that does the potentially time-consuming processing 
	public void run() {
		while (!done) {
			synchronized(this) {
				try {wait();}
				catch(InterruptedException e) {}
			}
			doUpdate();
		}
	}

	void doUpdate() {
		ImagePlus imp;
		ImageProcessor ip;
		int action;
		int bvalue = brightnessValue;
		int cvalue = contrastValue;
		if (doReset) action = RESET;
		else if (doAutoAdjust) action = AUTO;
		else if (doHistogram) action = HIST;
		else if (doApplyLut) action = APPLY;
		else if (doThreshold) action = THRESHOLD;
		else if (doUpdate) action = UPDATE;
		else if (brightnessValue>=0) action = BRIGHTNESS;
		else if (contrastValue>=0) action = CONTRAST;
		else return;
		brightnessValue = -1;
		contrastValue = -1;
		doReset = false;
		doAutoAdjust = false;
		doHistogram = false;
		doApplyLut = false;
		doThreshold = false;
		doUpdate = false;
		imp = WindowManager.getCurrentImage();
		if (imp==null) {
			IJ.beep();
			IJ.showStatus("No image");
			return;
		}
		if (!imp.lock())
			{imp=null; return;}
		if (action!=UPDATE)
			ip = setup(imp);
		else
			ip = imp.getProcessor();
		//IJ.write("setup: "+(imp==null?"null":imp.getTitle()));
		switch (action) {
			case RESET: reset(ip); break;
			case AUTO: autoAdjust(imp, ip); break;
			case HIST: plotHistogram(imp, ip); break;
			case APPLY: apply(imp, ip); break;
			case THRESHOLD: threshold(imp, ip); break;
			case UPDATE: update(imp, ip); break;
			case BRIGHTNESS: adjustBrightness(imp, ip, bvalue); break;
			case CONTRAST: adjustContrast(imp, ip, cvalue); break;
		}
		updatePlot();
		updateLabels(ip);
		imp.updateAndDraw();
		imp.unlock();
	}

} // ContrastAdjuster class


class ContrastPlot extends Canvas implements MouseListener {
	
	static final int WIDTH = 120, HEIGHT=80;
	double defaultMin = 0;
	double defaultMax = 255;
	double min = 0;
	double max = 255;
	int[] histogram;
	int hmax;
	Image os;
	Graphics osg;
	
	public ContrastPlot() {
		addMouseListener(this);
		setSize(WIDTH+1, HEIGHT+1);
	}

	void setHistogram(ImageStatistics stats) {
		int maxCount2 = 0;
		histogram = stats.histogram;
		for (int i = 0; i < stats.nBins; i++)
		if ((histogram[i] > maxCount2) && (i != stats.mode))
			maxCount2 = histogram[i];
		hmax = stats.maxCount;
		if ((hmax>(maxCount2 * 2)) && (maxCount2 != 0)) {
			hmax = (int)(maxCount2 * 1.5);
			histogram[stats.mode] = hmax;
        	}
		os = null;
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		int x1, y1, x2, y2;
		double scale = (double)WIDTH/(defaultMax-defaultMin);
		double slope = 0.0;
		if (max!=min)
			slope = HEIGHT/(max-min);
		if (min>=defaultMin) {
			x1 = (int)(scale*(min-defaultMin));
			y1 = HEIGHT;
		} else {
			x1 = 0;
			if (max>min)
				y1 = HEIGHT-(int)((defaultMin-min)*slope);
			else
				y1 = HEIGHT;
		}
		if (max<=defaultMax) {
			x2 = (int)(scale*(max-defaultMin));
			y2 = 0;
		} else {
			x2 = WIDTH;
			if (max>min)
				y2 = HEIGHT-(int)((defaultMax-min)*slope);
			else
				y2 = 0;
		}
		if (histogram!=null) {
			if (os==null) {
				os = createImage(WIDTH,HEIGHT);
				osg = os.getGraphics();
				osg.setColor(Color.white);
				osg.fillRect(0, 0, WIDTH, HEIGHT);
				osg.setColor(Color.gray);
				for (int i = 0; i < WIDTH; i++)
					osg.drawLine(i, HEIGHT, i, HEIGHT - ((int)(HEIGHT * histogram[i*2])/hmax));
				osg.dispose();
			}
			g.drawImage(os, 0, 0, this);
		} else {
			g.setColor(Color.white);
			g.fillRect(0, 0, WIDTH, HEIGHT);
		}
		g.setColor(Color.black);
 		g.drawLine(x1, y1, x2, y2);
 		g.drawRect(0, 0, WIDTH, HEIGHT);
     	}

	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}

} // ContrastPlot class
