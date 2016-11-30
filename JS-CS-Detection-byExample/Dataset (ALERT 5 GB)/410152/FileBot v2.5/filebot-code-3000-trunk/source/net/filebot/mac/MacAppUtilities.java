package net.filebot.mac;

import static ca.weblite.objc.util.CocoaUtils.*;

import java.awt.EventQueue;
import java.awt.SecondaryLoop;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JMenuBar;
import javax.swing.UIManager;

import ca.weblite.objc.Client;
import ca.weblite.objc.Proxy;

public class MacAppUtilities {

	private static Client _objc;

	public static Client objc() {
		if (_objc == null) {
			_objc = new Client();
		}
		return _objc;
	}

	public static Object NSData_initWithBase64Encoding(String text) {
		return objc().sendProxy("NSData", "data").send("initWithBase64Encoding:", text);
	}

	public static String NSURL_bookmarkDataWithOptions(String path) {
		return objc().sendProxy("NSURL", "fileURLWithPath:", path).sendProxy("bookmarkDataWithOptions:includingResourceValuesForKeys:relativeToURL:error:", 2048, null, null, null).sendString("base64Encoding");
	}

	public static Object NSURL_URLByResolvingBookmarkData_startAccessingSecurityScopedResource(String text) {
		return objc().sendProxy("NSURL", "URLByResolvingBookmarkData:options:relativeToURL:bookmarkDataIsStale:error:", NSData_initWithBase64Encoding(text), 1024, null, false, null).send("startAccessingSecurityScopedResource");
	}

	public static List<File> NSOpenPanel_openPanel_runModal(String title, boolean multipleMode, boolean canChooseDirectories, boolean canChooseFiles, String[] allowedFileTypes) {
		final List<File> result = new ArrayList<File>();

		final EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
		final SecondaryLoop secondaryLoop = eventQueue.createSecondaryLoop();

		// WARNING: dispatch_sync seems to work on most Mac always causes a deadlock and freezes the application on others (in particular MBP with 2 graphics chips)
		dispatch_async(new Runnable() {

			@Override
			public void run() {
				Proxy peer = objc().sendProxy("NSOpenPanel", "openPanel");
				peer.send("setTitle:", title);
				peer.send("setAllowsMultipleSelection:", multipleMode ? 1 : 0);
				peer.send("setCanChooseDirectories:", canChooseDirectories ? 1 : 0);
				peer.send("setCanChooseFiles:", canChooseFiles ? 1 : 0);

				if (allowedFileTypes != null) {
					Proxy mutableArray = objc().sendProxy("NSMutableArray", "arrayWithCapacity:", allowedFileTypes.length);
					for (String type : allowedFileTypes) {
						mutableArray.send("addObject:", type);
					}
					peer.send("setAllowedFileTypes:", mutableArray);
				}

				if (peer.sendInt("runModal") != 0) {
					Proxy nsArray = peer.getProxy("URLs");
					int size = nsArray.sendInt("count");
					for (int i = 0; i < size; i++) {
						Proxy url = nsArray.sendProxy("objectAtIndex:", i);
						String path = url.sendString("path");
						result.add(new File(path));
					}
				}

				secondaryLoop.exit();
			}
		});

		// Enter the loop to block the current event handler, but leave UI responsive
		if (!secondaryLoop.enter()) {
			throw new RuntimeException("SecondaryLoop.enter()");
		}

		return result;
	}

	public static void setWindowCanFullScreen(Window window) {
		try {
			Class<?> fullScreenUtilities = Class.forName("com.apple.eawt.FullScreenUtilities");
			Method setWindowCanFullScreen = fullScreenUtilities.getMethod("setWindowCanFullScreen", new Class<?>[] { Window.class, boolean.class });
			setWindowCanFullScreen.invoke(null, window, true);
		} catch (Throwable t) {
			Logger.getLogger(MacAppUtilities.class.getName()).log(Level.WARNING, "setWindowCanFullScreen not supported: " + t);
		}
	}

	public static void requestForeground() {
		try {
			Class<?> application = Class.forName("com.apple.eawt.Application");
			Object instance = application.getMethod("getApplication").invoke(null);
			Method requestForeground = application.getMethod("requestForeground", new Class<?>[] { boolean.class });
			requestForeground.invoke(instance, true);
		} catch (Throwable t) {
			Logger.getLogger(MacAppUtilities.class.getName()).log(Level.WARNING, "requestForeground not supported: " + t);
		}
	}

	public static void revealInFinder(File file) {
		try {
			Class<?> fileManager = Class.forName("com.apple.eio.FileManager");
			Method revealInFinder = fileManager.getMethod("revealInFinder", new Class<?>[] { File.class });
			revealInFinder.invoke(null, file);
		} catch (Throwable t) {
			Logger.getLogger(MacAppUtilities.class.getName()).log(Level.WARNING, "revealInFinder not supported: " + t);
		}
	}

	public static void setDefaultMenuBar(JMenuBar menu) {
		try {
			Class<?> application = Class.forName("com.apple.eawt.Application");
			Object instance = application.getMethod("getApplication").invoke(null);
			Method setDefaultMenuBar = application.getMethod("setDefaultMenuBar", new Class<?>[] { JMenuBar.class });
			setDefaultMenuBar.invoke(instance, menu);
		} catch (Throwable t) {
			Logger.getLogger(MacAppUtilities.class.getName()).log(Level.WARNING, "setDefaultMenuBar not supported: " + t);
		}
	}

	public static void setQuitStrategy(String field) {
		try {
			Class<?> application = Class.forName("com.apple.eawt.Application");
			Object instance = application.getMethod("getApplication").invoke(null);
			Class<?> quitStrategy = Class.forName("com.apple.eawt.QuitStrategy");
			Method setQuitStrategy = application.getMethod("setQuitStrategy", quitStrategy);
			Object closeAllWindows = quitStrategy.getField(field).get(null);
			setQuitStrategy.invoke(instance, closeAllWindows);
		} catch (Throwable t) {
			Logger.getLogger(MacAppUtilities.class.getName()).log(Level.WARNING, "setQuitStrategy not supported: " + t);
		}
	}

	public static void initializeApplication() {
		// improved UI defaults
		UIManager.put("TitledBorder.border", UIManager.getBorder("InsetBorder.aquaVariant"));

		// make sure Application Quit Events get forwarded to normal Window Listeners
		setQuitStrategy("CLOSE_ALL_WINDOWS");
	}

	public static boolean isLockedFolder(File folder) {
		// write permissions my not be available even after sandbox has granted permission (e.g. when accessing files of another user)
		return folder.isDirectory() && !folder.canRead();
	}

	public static boolean askUnlockFolders(final Window owner, final Collection<File> files) {
		return DropToUnlock.showUnlockFoldersDialog(owner, files);
	}

}
