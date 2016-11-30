package ij;
import ij.plugin.Converter;
import ij.plugin.frame.Recorder;
import java.awt.*;
import java.util.*;
import ij.gui.*;

/** This class consists of static methods used to manage ImageJ's windows. */
public class WindowManager {

	private static Vector imageList = new Vector();		// list of image windows
	private static Vector nonImageList = new Vector();	// list of non-image windows
	private static ImageWindow currentWindow;			// active image window
	private static Frame frontWindow;

	/** Makes the specified image active. */
	public synchronized static void setCurrentWindow(ImageWindow win) {
		setWindow(win);
		if (win==currentWindow || win==null || imageList.size()==0)
			return;
		//if (IJ.debugMode && win!=null)
		//	IJ.write(win.getImagePlus().getTitle()+": setCurrentWindow (previous="+(currentWindow!=null?currentWindow.getImagePlus().getTitle():"null") + ")");
		if (currentWindow!=null) {
			// free up pixel buffers used by current window
			ImagePlus imp = currentWindow.getImagePlus();
			if (imp!=null && imp.lockSilently()) {
				imp.trimProcessor();
				if (!Converter.newWindowCreated)
					imp.saveRoi();
				Converter.newWindowCreated = false;
				imp.unlock();
			}
		}
		Undo.reset();
		if (!win.isClosed() && win.getImagePlus()!=null)
			currentWindow = win;
		else
			currentWindow = null;
		Menus.updateMenus();
	}
	
	/** Returns the active ImageWindow. */
	public static ImageWindow getCurrentWindow() {
		//if (IJ.debugMode) IJ.write("ImageWindow.getCurrentWindow");
		return currentWindow;
	}

	static int getCurrentIndex() {
		return imageList.indexOf(currentWindow);
	}

	/** Returns the active ImagePlus. */
	public synchronized static ImagePlus getCurrentImage() {
		//if (IJ.debugMode) IJ.write("ImageWindow.getCurrentImage");
		if (currentWindow!=null)
			return currentWindow.getImagePlus();
		else
			return null;
	}

	/** Returns the number of open images. */
	public static int getWindowCount() {
		return imageList.size();
	}

	/** Returns the front most window. */
	//public static Frame getFrontWindow() {
	//	return frontWindow;
	//}

	/** Returns a list of the IDs of open images. Returns
		null if no windows are open. */
	public synchronized static int[] getIDList() {
		int nWindows = imageList.size();
		if (nWindows==0)
			return null;
		int[] list = new int[nWindows];
		for (int i=0; i<nWindows; i++) {
			ImageWindow win = (ImageWindow)imageList.elementAt(i);
			list[i] = win.getImagePlus().getID();
		}
		return list;
	}

	/** Returns the ImagePlus with the specified ID. Returns
		null if no open window has a matching ID. */
	public synchronized static ImagePlus getImage(int imageID) {
		//if (IJ.debugMode) IJ.write("ImageWindow.getImage");
		if (imageID>=0)
			return null;
		ImagePlus imp = null;
		for (int i=0; i<imageList.size(); i++) {
			ImageWindow win = (ImageWindow)imageList.elementAt(i);
			ImagePlus imp2 = win.getImagePlus();
			if (imageID==imp2.getID()) {
				imp = imp2;
				break;
			}
		}
		return imp;
	}

	/** Adds the specified window to the Window menu. */
	public synchronized static void addWindow(Frame win) {
		//IJ.write("addWindow: "+win.getTitle());
		if (win==null)
			return;
		else if (win instanceof ImageWindow)
			addImageWindow((ImageWindow)win);
		else {
			Menus.insertWindowMenuItem(win);
			nonImageList.addElement(win);
 		}
    }

	private static void addImageWindow(ImageWindow win) {
		imageList.addElement(win);
        Menus.addWindowMenuItem(win.getImagePlus());
        setCurrentWindow(win);
    }

	/** Removes the specified window from the Window menu. */
	public synchronized static void removeWindow(Frame win) {
		//IJ.write("removeWindow: "+win.getTitle());
		if (win==null)
			return;
		else if (win instanceof ImageWindow)
			removeImageWindow((ImageWindow)win);
		else {
				int index = nonImageList.indexOf(win);
				if (index>=0) {
					Menus.removeWindowMenuItem(index);
					nonImageList.removeElement(win);
				}
		}
	}

	private static void removeImageWindow(ImageWindow win) {
		int index = imageList.indexOf(win);
		if (index==-1)
			return;  // not on the window list
		if (imageList.size()>1) {
			int newIndex = index-1;
			if (newIndex<0)
				newIndex = imageList.size()-1;
			setCurrentWindow((ImageWindow)imageList.elementAt(newIndex));
		} else
			currentWindow = null;
		imageList.removeElementAt(index);
		int nonImageCount = nonImageList.size();
		if (nonImageCount>0)
			nonImageCount++;
		Menus.removeWindowMenuItem(nonImageCount+index);
		Menus.updateMenus();
		Undo.reset();
	}

	/** Puts a checkmark in the Windows menu next to the specified window. */
	public static void setWindow(Frame win) {
		if (win==null)
			return;
		frontWindow = win;
		//IJ.write("Front window: "+win.getTitle());
    }

	/** Closes all image windows. Stops and returns false if any "save changes" dialog is canceled. */
	public synchronized static boolean closeAllWindows() {
		while (imageList.size()>0) {
			ImagePlus imp = ((ImageWindow)imageList.elementAt(0)).getImagePlus();
			//IJ.write("Closing: " + imp.getTitle() + " " + imageList.size());
			if (!((ImageWindow)imageList.elementAt(0)).close())
				return false;
			IJ.wait(100);
		}
		return true;
    }
    
	/** Activates the next window on the window list. */
	public static void putBehind() {
		//if (IJ.debugMode) IJ.write("putBehind");
		if(imageList.size()<1 || currentWindow==null)
			return;
		int index = imageList.indexOf(currentWindow);
		index--;
		if (index<0)
			index = imageList.size()-1;
		ImageWindow win = (ImageWindow)imageList.elementAt(index);
		setCurrentWindow(win);
		win.toFront();
		Menus.updateMenus();
    }

	/** Activates a window selected from the Window menu. */
	synchronized static void activateWindow(String menuItemLabel, MenuItem item) {
		//IJ.write("activateWindow: "+menuItemLabel+" "+item);
		for (int i=0; i<nonImageList.size(); i++) {
			Frame win = (Frame)nonImageList.elementAt(i);
			String title = win.getTitle();
			if (menuItemLabel.equals(title)) {
				win.toFront();
				((CheckboxMenuItem)item).setState(false);
				return;
			}
		}
		for (int i=0; i<imageList.size(); i++) {
			ImageWindow win = (ImageWindow)imageList.elementAt(i);
			String title = win.getImagePlus().getTitle();
			if (menuItemLabel.startsWith(title)) {
				setCurrentWindow(win);
				win.toFront();
				int index = imageList.indexOf(win);
				int n = Menus.window.getItemCount();
				int start = Menus.WINDOW_MENU_ITEMS+Menus.windowMenuItems2;
				for (int j=start; j<n; j++) {
					MenuItem mi = Menus.window.getItem(j);
					((CheckboxMenuItem)mi).setState((j-start)==index);						
				}
				if (Recorder.record)
					Recorder.record("selectWindow", title);
				break;
			}
		}
    }
    
	static void showList() {
		if (IJ.debugMode) {
			for (int i=0; i<imageList.size(); i++) {
				ImageWindow win = (ImageWindow)imageList.elementAt(i);
				ImagePlus imp = win.getImagePlus();
				IJ.write(i + " " + imp.getTitle() + (win==currentWindow?"*":""));
			}
			IJ.write(" ");
		}
    }
    
}