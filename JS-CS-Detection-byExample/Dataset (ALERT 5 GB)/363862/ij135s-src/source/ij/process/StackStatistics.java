package ij.process;
import ij.*;
import ij.gui.*;
import ij.measure.*;
import ij.plugin.filter.Analyzer;
import java.awt.*;

/** Statistics, including the histogram, of a stack. */
public class StackStatistics extends ImageStatistics {
	
	public StackStatistics(ImagePlus imp) {
		doCalculations(imp, 256, 0.0, 0.0);
	}

	public StackStatistics(ImagePlus imp, int nBins, double histMin, double histMax) {
		//if (imp.getBitDepth()==8)
		//	histMax = 255.0;
		doCalculations(imp, nBins, histMin, histMax);
		//IJ.log("StackStatistics: "+histMin+"  "+histMax+"  "+nBins);
	}

    void doCalculations(ImagePlus imp,  int bins, double histogramMin, double histogramMax) {
        ImageProcessor ip = imp.getProcessor();
		boolean limitToThreshold = (Analyzer.getMeasurements()&LIMIT)!=0;
		double minThreshold = -Float.MAX_VALUE;
		double maxThreshold = Float.MAX_VALUE;
		if (limitToThreshold && ip.getMinThreshold()!=ip.NO_THRESHOLD) {
			minThreshold=ip.getMinThreshold();
			maxThreshold=ip.getMaxThreshold();
		}
    	nBins = bins;
    	histMin = histogramMin;
    	histMax = histogramMax;
        ImageStack stack = imp.getStack();
        int size = stack.getSize();
        Calibration cal = imp.getCalibration();
        ip.setRoi(imp.getRoi());
        byte[] mask = ip.getMaskArray();
        float[] cTable = imp.getCalibration().getCTable();
        histogram = new int[nBins];
        double v;
        double sum = 0;
        double sum2 = 0;
        int width, height;
        int rx, ry, rw, rh;
        double pw, ph;
        
        width = ip.getWidth();
        height = ip.getHeight();
        Rectangle roi = ip.getRoi();
        if (roi != null) {
            rx = roi.x;
            ry = roi.y;
            rw = roi.width;
            rh = roi.height;
        } else {
            rx = 0;
            ry = 0;
            rw = width;
            rh = height;
        }
        
        pw = 1.0;
        ph = 1.0;
        roiX = rx*pw;
        roiY = ry*ph;
        roiWidth = rw*pw;
        roiHeight = rh*ph;
        boolean fixedRange = histMin!=0 || histMax!=0.0;
        
        // calculate min and max
		double roiMin = Double.MAX_VALUE;
		double roiMax = -Double.MAX_VALUE;
		for (int slice=1; slice<=size; slice++) {
			IJ.showStatus("Calculating stack histogram...");
			IJ.showProgress(slice/2, size);
			ip = stack.getProcessor(slice);
			ip.setCalibrationTable(cTable);
			for (int y=ry, my=0; y<(ry+rh); y++, my++) {
				int i = y * width + rx;
				int mi = my * rw;
				for (int x=rx; x<(rx+rw); x++) {
					if (mask==null || mask[mi++]!=0) {
						v = ip.getPixelValue(x,y);
						if (v>=minThreshold && v<=maxThreshold) {
							if (v<roiMin) roiMin = v;
							if (v>roiMax) roiMax = v;
						}
					}
					i++;
				}
			}
		 }
		min = roiMin;
		max = roiMax;
		if (fixedRange) {
			if (min<histMin) min = histMin;
			if (max>histMax) max = histMax;
		} else {
			histMin = min; 
			histMax =  max;
		}
       
        // Generate histogram
        double scale = nBins/( histMax-histMin);
        pixelCount = 0;
        int index;
        boolean first = true;
        for (int slice=1; slice<=size; slice++) {
            IJ.showProgress(size/2+slice/2, size);
            ip = stack.getProcessor(slice);
            ip.setCalibrationTable(cTable);
            for (int y=ry, my=0; y<(ry+rh); y++, my++) {
                int i = y * width + rx;
                int mi = my * rw;
                for (int x=rx; x<(rx+rw); x++) {
                    if (mask==null || mask[mi++]!=0) {
                        v = ip.getPixelValue(x,y);
						if (v>=minThreshold && v<=maxThreshold && v>=histMin && v<=histMax) {
							pixelCount++;
							sum += v;
							sum2 += v*v;
							index = (int)(scale*(v-histMin));
							if (index>=nBins)
								index = nBins-1;
							histogram[index]++;
						}
                    }
                    i++;
                }
            }
        }
        area = pixelCount*pw*ph;
        mean = sum/pixelCount;
        //IJ.write(sum+" "+stats.pixelCount);
        calculateStdDev(pixelCount, sum, sum2);
        histMin = cal.getRawValue(histMin); 
        histMax =  cal.getRawValue(histMax);
        binSize = (histMax-histMin)/nBins;
        //IJ.log(stats.histMin+" "+stats.histMax+" "+stats.nBins);
        dmode = getMode(cal);
        IJ.showStatus("");
        IJ.showProgress(1.0);
    }

   double getMode(Calibration cal) {
        int count;
        maxCount = 0;
        for (int i = 0; i < nBins; i++) {
            count = histogram[i];
            if (count > maxCount) {
                maxCount = count;
                mode = i;
            }
        }
       return cal.getCValue(histMin+mode*binSize);
    }
    
}