package ij.plugin;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.*;
import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.frame.Editor;
import ij.text.TextWindow;
	
/**	Copies and pastes images to the clipboard. Java 1.4 or later is 
	required to copy to or paste from the system clipboard. */
public class Clipboard implements PlugIn, Transferable {
	static java.awt.datatransfer.Clipboard clipboard;
	
	public void run(String arg) {
		if (IJ.altKeyDown()) {
			if (arg.equals("copy"))
				arg = "scopy";
			else if (arg.equals("paste"))
				arg = "spaste";
		}
  		if (arg.equals("copy"))
			copy(false);
  		else if (arg.equals("paste"))
			paste();
  		else if (arg.equals("cut"))
			copy(true);
  		else if (arg.equals("scopy"))
			copyToSystem();
		else if (arg.equals("showsys"))
			showSystemClipboard();
		else if (arg.equals("show"))
			showInternalClipboard();
	}
	
	void copy(boolean cut) {
		ImagePlus imp = WindowManager.getCurrentImage();
		if (imp!=null)
	 		imp.copy(cut);
	 	else
	 		IJ.noImage();
	}
	
	void paste() {
		if (ImagePlus.getClipboard()==null) {
			if (IJ.isJava14())
				showSystemClipboard();
			else
				IJ.noImage();
		} else {
			ImagePlus imp = WindowManager.getCurrentImage();
			if (imp!=null)
				imp.paste();
			else
				showInternalClipboard	();
		}
	}

	boolean setup() {
		if (!IJ.isJava14()) {
			IJ.error("Clipboard", "Java 1.4 or later required");
			return false;
		}
		if (clipboard==null)
			clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		return true;
	}
	
	void copyToSystem() {
		if (!setup()) return;
		try {
			clipboard.setContents(this, null);
		} catch (Throwable t) {}
	}
	
	void showSystemClipboard() {
		if (!setup()) return;
		IJ.showStatus("Opening system clipboard...");
		try {
			Transferable transferable = clipboard.getContents(null);
			boolean imageSupported = transferable.isDataFlavorSupported(DataFlavor.imageFlavor);
			boolean textSupported = transferable.isDataFlavorSupported(DataFlavor.stringFlavor);
			if (!imageSupported && IJ.isMacOSX() && displayMacImage(transferable))
				return;
			if (imageSupported) {
				Image img = (Image)transferable.getTransferData(DataFlavor.imageFlavor);
				if (img==null) {
					IJ.error("Unable to convert image on system clipboard");
					IJ.showStatus("");
					return;
				}
				int width = img.getWidth(null);
				int height = img.getHeight(null);
				BufferedImage   bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics g = bi.createGraphics();
				g.drawImage(img, 0, 0, null);
				g.dispose();
				WindowManager.checkForDuplicateName = true;
				new ImagePlus("Clipboard", bi).show();
			} else if (textSupported) {
				String text = (String)transferable.getTransferData(DataFlavor.stringFlavor);
				Editor ed = new Editor();
				ed.setSize(600, 300);
				ed.create("Clipboard", text);
				IJ.showStatus("");
			} else
				IJ.error("Unable to find an image on the system clipboard");
		} catch (Throwable e) {
			CharArrayWriter caw = new CharArrayWriter();
			PrintWriter pw = new PrintWriter(caw);
			e.printStackTrace(pw);
			new TextWindow("Exception", caw.toString(), 350, 250);
		}
	}
	
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { DataFlavor.imageFlavor };
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return DataFlavor.imageFlavor.equals(flavor);
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
		if (!isDataFlavorSupported(flavor))
			throw new UnsupportedFlavorException(flavor);
		ImagePlus imp = WindowManager.getCurrentImage();
		if ( imp != null) {
			ImageProcessor ip = imp.getProcessor();
			ip = ip.crop();
			int w = ip.getWidth();
			int h = ip.getHeight();
			IJ.showStatus(w+"x"+h+ " image copied to system clipboard");
			Image img = IJ.getInstance().createImage(w, h);
			Graphics g = img.getGraphics();
			g.drawImage(ip.createImage(), 0, 0, null);
			g.dispose();
			return img;
		} else {
			//IJ.noImage();
			return null;
		}
	}
	
	void showInternalClipboard() {
		ImagePlus clipboard = ImagePlus.getClipboard();
		if (clipboard!=null) {
			ImageProcessor ip = clipboard.getProcessor();
			ImagePlus imp2 = new ImagePlus("Clipboard", ip.duplicate());
			Roi roi = clipboard.getRoi();
			imp2.killRoi();
			if (roi!=null && roi.isArea() && roi.getType()!=Roi.RECTANGLE) {
				roi = (Roi)roi.clone();
				roi.setLocation(0, 0);
				imp2.setRoi(roi);
				WindowManager.setTempCurrentImage(imp2);
				IJ.run("Clear Outside");
				imp2.killRoi();
			}
			WindowManager.checkForDuplicateName = true;          
			imp2.show();
		} else
			IJ.error("The internal clipboard is empty.");
	}
	
    boolean displayMacImage(Transferable t) {
    	Image img = getMacImage(t);
    	if (img!=null) {
			WindowManager.checkForDuplicateName = true;          
			new ImagePlus("Clipboard", img).show();
		}
		return img!=null;
    }

	// Mac OS X's data transfer handling is horribly broken. We sometimes
	// need to use the "image/x-pict" MIME type and then Quicktime
	// for Java in order to get the image data.
	Image getMacImage(Transferable t) {
		if (!isQTJavaInstalled())
			return null;
		Image img = null;
		DataFlavor[] d = t.getTransferDataFlavors();
		if (d==null || d.length==0)
			return null;
		try {
			Object is = t.getTransferData(d[0]);
			if (is==null || !(is instanceof InputStream))
				return null;
			img = getImageFromPictStream((InputStream)is);
		} catch (Exception e) {}
		return img;
    }
    
	// Converts a PICT to an AWT image using QuickTime for Java.
	// This code was contributed by Gord Peters.
	Image getImageFromPictStream(InputStream is) {
		try {
			ByteArrayOutputStream baos= new ByteArrayOutputStream();
			// We need to strip the header from the data because a PICT file
			// has a 512 byte header and then the data, but in our case we only
			// need the data. --GP
			byte[] header= new byte[512];
			byte[] buf= new byte[4096];
			int retval= 0, size= 0;
			baos.write(header, 0, 512);
			while ( (retval= is.read(buf, 0, 4096)) > 0)
				baos.write(buf, 0, retval);		
			baos.close();
			size = baos.size();
			//IJ.log("size: "+size); IJ.wait(2000);
			if (size<=0)
				return null;
			byte[] imgBytes= baos.toByteArray();
			// Again with the uglyness.  Here we need to use the Quicktime
			// for Java code in order to create an Image object from
			// the PICT data we received on the clipboard.  However, in
			// order to get this to compile on other platforms, we use
			// the Java reflection API.
			//
			// For reference, here is the equivalent code without
			// reflection:
			//
			//
			// if (QTSession.isInitialized() == false) {
			//     QTSession.open();
			// }
			// QTHandle handle= new QTHandle(imgBytes);
			// GraphicsImporter gi=
			//     new GraphicsImporter(QTUtils.toOSType("PICT"));
			// gi.setDataHandle(handle);
			// QDRect qdRect= gi.getNaturalBounds();
			// GraphicsImporterDrawer gid= new GraphicsImporterDrawer(gi);
			// QTImageProducer qip= new QTImageProducer(gid,
			//                          new Dimension(qdRect.getWidth(),
			//                                        qdRect.getHeight()));
			// return(Toolkit.getDefaultToolkit().createImage(qip));
			//
			// --GP
			//IJ.log("quicktime.QTSession");
			Class c = Class.forName("quicktime.QTSession");
			Method m = c.getMethod("isInitialized", null);
			Boolean b= (Boolean)m.invoke(null, null);			
			if (b.booleanValue() == false) {
				m= c.getMethod("open", null);
				m.invoke(null, null);
			}
			c= Class.forName("quicktime.util.QTHandle");
			Constructor con = c.getConstructor(new Class[] {imgBytes.getClass() });
			Object handle= con.newInstance(new Object[] { imgBytes });
			String s= new String("PICT");
			c = Class.forName("quicktime.util.QTUtils");
			m = c.getMethod("toOSType", new Class[] { s.getClass() });
			Integer type= (Integer)m.invoke(null, new Object[] { s });
			c = Class.forName("quicktime.std.image.GraphicsImporter");
			con = c.getConstructor(new Class[] { type.TYPE });
			Object importer= con.newInstance(new Object[] { type });
			m = c.getMethod("setDataHandle",
			new Class[] { Class.forName("quicktime.util." + "QTHandleRef") });
			m.invoke(importer, new Object[] { handle });
			m = c.getMethod("getNaturalBounds", null);
			Object rect= m.invoke(importer, null);
			c = Class.forName("quicktime.app.view.GraphicsImporterDrawer");
			con = c.getConstructor(new Class[] { importer.getClass() });
			Object iDrawer = con.newInstance(new Object[] { importer });
			m = rect.getClass().getMethod("getWidth", null);
			Integer width= (Integer)m.invoke(rect, null);
			m = rect.getClass().getMethod("getHeight", null);
			Integer height= (Integer)m.invoke(rect, null);
			Dimension d= new Dimension(width.intValue(), height.intValue());
			c = Class.forName("quicktime.app.view.QTImageProducer");
			con = c.getConstructor(new Class[] { iDrawer.getClass(), d.getClass() });
			Object producer= con.newInstance(new Object[] { iDrawer, d });
			if (producer instanceof ImageProducer)
				return(Toolkit.getDefaultToolkit().createImage((ImageProducer)producer));
		} catch (Exception e) {
			IJ.showStatus("QuickTime for java error");
		}
		return null;
    }

	// Retuns true if QuickTime for Java is installed.
	// This code was contributed by Gord Peters.
	boolean isQTJavaInstalled() {
		boolean isInstalled = false;
		try {
			Class c= Class.forName("quicktime.QTSession");
			isInstalled = true;
		} catch (Exception e) {}
		return isInstalled;
	}

}



