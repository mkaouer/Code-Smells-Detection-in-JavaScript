package ij.plugin.filter;
import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import java.util.*;

/** This plugin does convolutions on real images using user user defined kernels. */

public class Convolver implements PlugInFilter {

	static final int BYTE=0, SHORT=1, FLOAT=2, RGB=3;
	ImagePlus imp;
	static String kernelText = ".0625  .125  .0625\n.125    .25    .125\n.0625  .125  .0625";
	static boolean autoScale = true;
	static boolean createSelection = false;
	int kw, kh;
	static float[] kernel;
	static int slice;
	static boolean canceled;
	static ImageWindow win;

	public int setup(String arg, ImagePlus imp) {
 		IJ.register(Convolver.class);
		this.imp = imp;
		slice = 0;
		canceled = false;
		return DOES_ALL+DOES_STACKS;
	}

	public void run(ImageProcessor ip) {
		if (canceled)
			return;
		slice++;
		if (slice==1) {
			kernel = getKernel();
			if (kernel==null)
				{canceled=true; return;}
			if ((kw&1)==0) {
				IJ.showMessage("Convolver","The kernel must be square and have an\n"
					+"odd width. This kernel is "+kw+"x"+kh+".");
				canceled = true;
				return;
			}
			win = imp.getWindow();
			win.running = true;
			IJ.showStatus("Convolver: convolving with "+kw+"x"+kh+" kernel");
			imp.startTiming();
		}
		if (win.running!=true)
			{canceled=true; return;}
		convolve(ip, kernel, kw, kh);
		if (slice>1)
			IJ.showStatus(slice+"/"+imp.getStackSize());
		if (slice==imp.getStackSize()) {
			ip.resetMinAndMax();
			if (createSelection)
				imp.setRoi(kw/2,kh/2,imp.getWidth()-(kw/2)*2, imp.getHeight()-(kh/2)*2);
		}
	}
	
	float[] getKernel() {
		GenericDialog gd = new GenericDialog("Convolver...", IJ.getInstance());
		gd.addTextAreas(kernelText, null, 10, 30);
		gd.addCheckbox("Scale", autoScale);
		gd.addCheckbox("Create Selection", createSelection);
		gd.showDialog();
		if (gd.wasCanceled()) {
			canceled = true;
			return null;
		}
		kernelText = gd.getNextText();
		autoScale = gd.getNextBoolean();
		createSelection = gd.getNextBoolean();
		StringTokenizer st = new StringTokenizer(kernelText);
		int n = st.countTokens();
		kw = (int)Math.sqrt(n);
		kh = kw;
		n = kw*kh;
		float[] k = new float[n];
		for (int i=0; i<n; i++)
			k[i] = (float)getNum(st);
		//IJ.write("kw: "+kw);
		return k;
	}

	double getNum(StringTokenizer st) {
		Double d;
		String token = st.nextToken();
		try {d = new Double(token);}
		catch (NumberFormatException e){d = null;}
		if (d!=null)
			return(d.doubleValue());
		else
			return 0.0;
	}

	public void convolve(ImageProcessor ip, float[] kernel, int kw, int kh) {
		int type;
		if (ip instanceof ByteProcessor)
			type = BYTE;
		else if (ip instanceof ShortProcessor)
			type = SHORT;
		else if (ip instanceof FloatProcessor)
			type = FLOAT;
		else
			type = RGB;
		if (type==RGB) {
			convolveRGB(ip, kernel, kw, kh);
			return;
		}
		ImageProcessor ip2=ip.convertToFloat();
		convolveFloat(ip2, kernel, kw, kh);
		switch (type) {
			case BYTE:
				ip2 = ip2.convertToByte(false);
				byte[] pixels = (byte[])ip.getPixels();
				byte[] pixels2 = (byte[])ip2.getPixels();
				System.arraycopy(pixels2, 0, pixels, 0, pixels.length);
				break;
			case SHORT:
				ip2 = ip2.convertToShort(false);
				short[] pixels16 = (short[])ip.getPixels();
				short[] pixels16b = (short[])ip2.getPixels();
				System.arraycopy(pixels16b, 0, pixels16, 0, pixels16.length);
				break;
			case FLOAT:
				break;
		}
	}

	public void convolveRGB(ImageProcessor ip, float[] kernel, int kw, int kh) {
		int width = ip.getWidth();
		int height = ip.getHeight();
		int size = width*height;
		byte[] r = new byte[size];
		byte[] g = new byte[size];
		byte[] b = new byte[size];
		((ColorProcessor)ip).getRGB(r,g,b);
		ImageProcessor rip = new ByteProcessor(width, height, r, null);
		ImageProcessor gip = new ByteProcessor(width, height, g, null);
		ImageProcessor bip = new ByteProcessor(width, height, b, null);
		ImageProcessor ip2 = rip.convertToFloat();
		convolveFloat(ip2, kernel, kw, kh);
		ImageProcessor r2 = ip2.convertToByte(false);
		ip2 = gip.convertToFloat();
		convolveFloat(ip2, kernel, kw, kh);
		ImageProcessor g2 = ip2.convertToByte(false);
		ip2 = bip.convertToFloat();
		convolveFloat(ip2, kernel, kw, kh);
		ImageProcessor b2 = ip2.convertToByte(false);
		((ColorProcessor)ip).setRGB((byte[])r2.getPixels(), (byte[])g2.getPixels(), (byte[])b2.getPixels());
	}

	public void convolveFloat(ImageProcessor ip, float[] kernel, int kw, int kh) {
		int width = ip.getWidth();
		int height = ip.getHeight();
		int uc = kw/2;    
		int vc = kh/2;
		float[] pixels = (float[])ip.getPixels();
		float[] pixels2 = (float[])ip.getPixelsCopy();
		for (int i=0; i<width*height; i++)
			pixels[i] = 0f;

		double scale = 1.0;
		if (autoScale) {
			double sum = 0.0;
			for (int i=0; i<kernel.length; i++)
				sum += kernel[i];
			if (sum!=0.0)
				scale = (float)(1.0/sum);
		}

 		int progress = Math.max(height/25,1);
		double sum;
		int offset, i;  
		for(int y=vc; y<height-vc; y++) {
			if (y%progress ==0) IJ.showProgress((double)y/height);
			//IJ.write(""+y);
			for(int x=uc; x<width-uc; x++) {
				sum = 0.0;
				i = 0;
				for(int v=-vc; v <= vc; v++) {
					offset = x+(y+v)*width;
					for(int u = -uc; u <= uc; u++) {
    						sum +=pixels2[offset+u] * kernel[i++];
	    				}
	    			}
				pixels[x+y*width] = (float)(sum*scale);
					
			}
    		}
   		IJ.showProgress(1.0);
   	 }

}


