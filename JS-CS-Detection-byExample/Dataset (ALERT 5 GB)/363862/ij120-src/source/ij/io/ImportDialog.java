package ij.io;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import ij.*;
import ij.gui.*;
import ij.process.*;
import ij.util.StringSorter;


/** This is a dialog box used to imports raw 8, 16, 24 and 32-bit images. */
public class ImportDialog {
	private String fileName;
    private String directory;
	static final String TYPE = "raw.type";
	static final String WIDTH = "raw.width";
	static final String HEIGHT = "raw.height";
	static final String OFFSET = "raw.offset";
	static final String N = "raw.n";
	static final String GAP = "raw.gap";
	static final String OPTIONS = "raw.options";
	static final int WHITE_IS_ZERO = 1;
	static final int INTEL_BYTE_ORDER = 2;
	static final int OPEN_ALL = 4;
	
    // default settings
    private static int choiceSelection = Prefs.getInt(TYPE,0);
    private static int width = Prefs.getInt(WIDTH,512);
    private static int height = Prefs.getInt(HEIGHT,512);
    private static int offset = Prefs.getInt(OFFSET,0);
    private static int nImages = Prefs.getInt(N,1);
    private static int gapBetweenImages = Prefs.getInt(GAP,0);
	private static int options;
    private static boolean whiteIsZero,intelByteOrder,openAll;
    private static String[] types = {"8-bit", "16-bit Signed", "16-bit Unsigned",
    	"32-bit Integer", "32-bit Real", "24-bit RGB", "24-bit RGB Planar"};
    	
    static {
    	options = Prefs.getInt(OPTIONS,0);
    	whiteIsZero = (options&WHITE_IS_ZERO)!=0;
    	intelByteOrder = (options&INTEL_BYTE_ORDER)!=0;
    	openAll = (options&OPEN_ALL)!=0;
    }
	
    public ImportDialog(String fileName, String directory) {
        this.fileName = fileName;
        this.directory = directory;
		IJ.showStatus("Importing: " + fileName);
		IJ.register(ImportDialog.class);  // keep this class from being GC'd
	}

	boolean showDialog() {
		GenericDialog gd = new GenericDialog("Import...", IJ.getInstance());
		gd.addChoice("Image Type:", types, types[choiceSelection]);
		gd.addNumericField("Image Width:", width, 0);
		gd.addNumericField("Image Height:", height, 0);
		gd.addNumericField("Offset to First Image:", offset, 0);
		gd.addNumericField("Number of Images:", nImages, 0);
		gd.addNumericField("Gap Between Images:", gapBetweenImages, 0);
		gd.addCheckbox("White is Zero", whiteIsZero);
		gd.addCheckbox("Little-Endian Byte Order", intelByteOrder);
		gd.addCheckbox("Open All Files in Folder", openAll);
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		choiceSelection = gd.getNextChoiceIndex();
		width = (int)gd.getNextNumber();
		height = (int)gd.getNextNumber();
		offset = (int)gd.getNextNumber();
		nImages = (int)gd.getNextNumber();
		gapBetweenImages = (int)gd.getNextNumber();
		whiteIsZero = gd.getNextBoolean();
		intelByteOrder = gd.getNextBoolean();
		openAll = gd.getNextBoolean();
		return true;
	}
	
	/** Opens all the images in the directory. */
	void openAll(String[] list, FileInfo fi) {
		StringSorter.sort(list);
		ImageStack stack=null;
		ImagePlus imp=null;
		for (int i=0; i<list.length; i++) {
			fi.fileName = list[i];
			imp = new FileOpener(fi).open(false);
			if (imp==null)
				IJ.write(list[i] + ": unable to open");
			else {
				if (stack==null)
					stack = imp.createEmptyStack();	
				try {
					stack.addSlice(list[i], imp.getProcessor());
				}
				catch(OutOfMemoryError e) {
					IJ.outOfMemory("OpenAll");
					stack.trim();
					break;
				}
				IJ.showStatus((stack.getSize()+1) + ": " + list[i]);
			}
		}
		imp = new ImagePlus(list[0], stack);
		imp.show();
	}
	
	/** Opens the specified image. Does nothing if the dialog was canceled. */
	public void openImage() {
		if (!showDialog())
			return;
		FileInputStream in;
		String path = directory + fileName;
		File f = new File(path);
		String imageType = types[choiceSelection];

		FileInfo fi = new FileInfo();
		fi.fileFormat = fi.RAW;
		fi.fileName = fileName;
		fi.directory = directory;
		fi.width = width;
		fi.height = height;
		fi.offset = offset;
		fi.nImages = nImages;
		fi.gapBetweenImages = gapBetweenImages;
		fi.intelByteOrder = intelByteOrder;
		fi.whiteIsZero = whiteIsZero;
		if (imageType.equals("8-bit"))
			fi.fileType = FileInfo.GRAY8;
		else if (imageType.equals("16-bit Signed"))
			fi.fileType = FileInfo.GRAY16_SIGNED;
		else if (imageType.equals("16-bit Unsigned"))
			fi.fileType = FileInfo.GRAY16_UNSIGNED;
		else if (imageType.equals("32-bit Integer"))
			fi.fileType = FileInfo.GRAY32_INT;
		else if (imageType.equals("32-bit Real"))
			fi.fileType = FileInfo.GRAY32_FLOAT;
		else if (imageType.equals("24-bit RGB"))
			fi.fileType = FileInfo.RGB;
		else if (imageType.equals("24-bit RGB Planar"))
			fi.fileType = FileInfo.RGB_PLANAR;
		else
			return;
			
		if (IJ.debugMode) IJ.write("ImportDialog: "+fi);
		if (openAll) {
			String[] list = new File(directory).list();
			if (list==null)
				return;
			openAll(list, fi);
		} else {
			FileOpener fo = new FileOpener(fi);
			fo.open();
		}
	}

	public static void savePreferences(Properties prefs) {
		prefs.put(TYPE, Integer.toString(choiceSelection));
		prefs.put(WIDTH, Integer.toString(width));
		prefs.put(HEIGHT, Integer.toString(height));
		prefs.put(OFFSET, Integer.toString(offset));
		prefs.put(N, Integer.toString(nImages));
		prefs.put(GAP, Integer.toString(gapBetweenImages));
		int options = 0;
		if (whiteIsZero)
			options |= WHITE_IS_ZERO;
		if (intelByteOrder)
			options |= INTEL_BYTE_ORDER;
		if (openAll)
			options |= OPEN_ALL;
		prefs.put(OPTIONS, Integer.toString(options));
	}

}