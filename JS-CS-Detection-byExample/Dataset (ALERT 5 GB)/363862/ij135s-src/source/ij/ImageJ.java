package ij;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.awt.image.*;
import ij.gui.*;
import ij.process.*;
import ij.io.*;
import ij.plugin.*;
import ij.plugin.filter.*;
import ij.text.*;
import ij.macro.Interpreter;
import ij.io.Opener;
import ij.util.*;

/**
This frame is the main ImageJ class.
<p>
ImageJ is a work of the United States Government. It is in the public domain 
and open source. There is no copyright. You are free to do anything you want 
with this source but I like to get credit for my work and I would like you to 
offer your changes to me so I can possibly add them to the "official" version.

<pre>
The following command line options are recognized by ImageJ:

  "file-name"
     Opens a file
     Example 1: blobs.tif
     Example 2: /Users/wayne/images/blobs.tif
     Example3: e81*.tif

  -ijpath path
     Specified the path to the directory containing the plugins directory
     Example: -ijpath /Applications/ImageJ

  -port<n>
     Specifies the port ImageJ uses to determine if another instance is running
     Example 1: -port1
     Example 2: -port2

  -macro path [arg]
     Runs a macro, passing it an optional argument
     Example 1: -macro analyze.ijm
     Example 2: -macro analyze /Users/wayne/images/stack1

  -batch path [arg]
    Runs a macro in batch (no GUI) mode, passing it an optional argument.
    ImageJ exits when the macro finishes.

  -eval "macro code"
     Evaluates macro code
     Example 1: -eval "print('Hello, world');"
     Example 2: -eval "return getVersion();"

  -run command
     Runs an ImageJ menu command
     Example: -run "About ImageJ..."
</pre>
@author Wayne Rasband (wsr@nih.gov)
*/
public class ImageJ extends Frame implements ActionListener, 
	MouseListener, KeyListener, WindowListener, ItemListener, Runnable {

	public static final String VERSION = "1.35s";
	public static Color backgroundColor = new Color(220,220,220); //224,226,235
	/** SansSerif, 12-point, plain font. */
	public static final Font SansSerif12 = new Font("SansSerif", Font.PLAIN, 12);
	/** Address of socket where Image accepts commands */
	public static final int DEFAULT_PORT = 57294;

	private static final String IJ_X="ij.x",IJ_Y="ij.y";
	private static final String RESULTS_X="results.x",RESULTS_Y="results.y",
		RESULTS_WIDTH="results.width",RESULTS_HEIGHT="results.height";
	private static int port = DEFAULT_PORT;
	
	private Toolbar toolbar;
	private Panel statusBar;
	private ProgressBar progressBar;
	private Label statusLine;
	private boolean firstTime = true;
	private java.applet.Applet applet; // null if not running as an applet
	private Vector classes = new Vector();
	private boolean exitWhenQuiting;
	private boolean quitting;
	private long keyPressedTime, actionPerformedTime;
	
	boolean hotkey;
	
	/** Creates a new ImageJ frame. */
	public ImageJ() {
		this(null);
	}
	
	/** Creates a new ImageJ frame running as an applet
		if the 'applet' argument is not null. */
	public ImageJ(java.applet.Applet applet) {
		super("ImageJ");
		this.applet = applet;
		String err1 = Prefs.load(this, applet);
		Menus m = new Menus(this, applet);
		String err2 = m.addMenuBar();
		m.installPopupMenu(this);
		setLayout(new GridLayout(2, 1));
		
		// Tool bar
		toolbar = new Toolbar();
		toolbar.addKeyListener(this);
		add(toolbar);

		// Status bar
		statusBar = new Panel();
		statusBar.setLayout(new BorderLayout());
		statusBar.setForeground(Color.black);
		statusBar.setBackground(backgroundColor);
		statusLine = new Label();
		statusLine.setFont(SansSerif12);
		statusLine.addKeyListener(this);
		statusLine.addMouseListener(this);
		statusBar.add("Center", statusLine);
		progressBar = new ProgressBar(100, 18);
		progressBar.addKeyListener(this);
		progressBar.addMouseListener(this);
		statusBar.add("East", progressBar);
		statusBar.setSize(toolbar.getPreferredSize());
		add(statusBar);

		IJ.init(this, applet);
 		addKeyListener(this);
 		addWindowListener(this);
 		
		Point loc = getPreferredLocation();
		Dimension tbSize = toolbar.getPreferredSize();
		int ijWidth = tbSize.width+10;
		int ijHeight = 100;
		setCursor(Cursor.getDefaultCursor()); // work-around for JDK 1.1.8 bug
		setIcon();
		setBounds(loc.x, loc.y, ijWidth, ijHeight); // needed for pack to work
		setLocation(loc.x, loc.y);
		pack();
		setResizable(!(IJ.isMacintosh() || IJ.isWindows())); // make resizable on Linux
		show();
		if (err1!=null)
			IJ.error(err1);
		if (err2!=null)
			IJ.error(err2);
		if (IJ.isMacintosh()&&applet==null) { 
			Object qh = null; 
			if (IJ.isJava14()) 
				qh = IJ.runPlugIn("MacAdapter", ""); 
			if (qh==null) 
				IJ.runPlugIn("QuitHandler", ""); 
		} 
		if (IJ.isJava2() && applet==null) {
			IJ.runPlugIn("ij.plugin.DragAndDrop", "");
		}
		m.installStartupMacroSet();
		String str = m.nMacros==1?" macro)":" macros)";
		IJ.showStatus("Version "+VERSION + " ("+ m.nPlugins + " commands, " + m.nMacros + str);
		if (applet==null)
			new SocketListener();
	}
    	
	void setIcon() {
		URL url = this.getClass().getResource("/microscope.gif"); 
		if (url==null)
			return;
		Image img = null;
		try {img = createImage((ImageProducer)url.getContent());}
		catch(Exception e) {}
		if (img!=null)
			try {setIconImage(img);} catch (Exception e) {}
	}
	
	public Point getPreferredLocation() {
		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int ijX = Prefs.getInt(IJ_X,-99);
		int ijY = Prefs.getInt(IJ_Y,-99);
		if (ijX>=0 && ijY>0 && ijX<(screenWidth-75))
			return new Point(ijX, ijY);
			
		Dimension tbsize = toolbar.getPreferredSize();
		int windowWidth = tbsize.width+10;
		double percent;
		if (screenWidth > 832)
			percent = 0.8;
		else
			percent = 0.9;
		int windowX = (int)(percent * (screenWidth - windowWidth));
		if (windowX < 10)
			windowX = 10;
		int windowY = 32;
		return new Point(windowX, windowY);
	}
	
	void showStatus(String s) {
        statusLine.setText(s);
	}

	public ProgressBar getProgressBar() {
        return progressBar;
	}

    /** Starts executing a menu command in a separate thread. */
    void doCommand(String name) {
		new Executer(name, WindowManager.getCurrentImage());
    }
        
	public void runFilterPlugIn(Object theFilter, String cmd, String arg) {
		IJ.runFilterPlugIn(theFilter, cmd, arg);
	}
        
	public Object runUserPlugIn(String commandName, String className, String arg, boolean createNewLoader) {
		return IJ.runUserPlugIn(commandName, className, arg, createNewLoader);	
	} 
	
	/** Return the current list of modifier keys. */
	public static String modifiers(int flags) { //?? needs to be moved
		String s = " [ ";
		if (flags == 0) return "";
		if ((flags & Event.SHIFT_MASK) != 0) s += "Shift ";
		if ((flags & Event.CTRL_MASK) != 0) s += "Control ";
		if ((flags & Event.META_MASK) != 0) s += "Meta ";
		if ((flags & Event.ALT_MASK) != 0) s += "Alt ";
		s += "] ";
		return s;
	}

	/** Handle menu events. */
	public void actionPerformed(ActionEvent e) {
		if ((e.getSource() instanceof MenuItem)) {
			MenuItem item = (MenuItem)e.getSource();
			String cmd = e.getActionCommand();
			if (item.getParent()==Menus.openRecentMenu) {
				new RecentOpener(cmd); // open image in separate thread
				return;
			}
			hotkey = false;
			actionPerformedTime = System.currentTimeMillis();
			long ellapsedTime = actionPerformedTime-keyPressedTime;
			if (cmd!=null && ellapsedTime>=10L)
				doCommand(cmd);
			if (IJ.debugMode) IJ.log("actionPerformed: "+ellapsedTime+" "+e);
		}
	}

	/** Handles CheckboxMenuItem state changes. */
	public void itemStateChanged(ItemEvent e) {
		MenuItem item = (MenuItem)e.getSource();
		MenuComponent parent = (MenuComponent)item.getParent();
		String cmd = e.getItem().toString();
		if ((Menu)parent==Menus.window)
			WindowManager.activateWindow(cmd, item);
		else
			doCommand(cmd);
	}

	public void mousePressed(MouseEvent e) {
		Undo.reset();
		IJ.showStatus("Memory: "+IJ.freeMemory());
		if (IJ.debugMode)
			IJ.log("Windows: "+WindowManager.getWindowCount());
	}
	
	public void mouseReleased(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}

 	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		IJ.setKeyDown(keyCode);
		hotkey = false;
		if (keyCode==e.VK_CONTROL || keyCode==e.VK_SHIFT)
			return;
		char keyChar = e.getKeyChar();
		int flags = e.getModifiers();
		if (IJ.debugMode) IJ.log("keyCode=" + keyCode + " (" + KeyEvent.getKeyText(keyCode)
			+ ") keyChar=\"" + keyChar + "\" (" + (int)keyChar + ") "
			+ KeyEvent.getKeyModifiersText(flags));
		boolean shift = (flags & e.SHIFT_MASK) != 0;
		boolean control = (flags & e.CTRL_MASK) != 0;
		boolean alt = (flags & e.ALT_MASK) != 0;
		boolean meta = (flags & e.META_MASK) != 0;
		String c = "";
		ImagePlus imp = WindowManager.getCurrentImage();
		boolean isStack = (imp!=null) && (imp.getStackSize()>1);
		
		if (imp!=null && !control && ((keyChar>=32 && keyChar<=255) || keyChar=='\b' || keyChar=='\n')) {
			Roi roi = imp.getRoi();
			if (roi instanceof TextRoi) {
				if ((flags & e.META_MASK)!=0 && IJ.isMacOSX()) return;
				if (alt)
					switch (keyChar) {
						case 'u': case 'm': keyChar = IJ.micronSymbol; break;
						case 'A': keyChar = IJ.angstromSymbol; break;
						default:
					}
				((TextRoi)roi).addChar(keyChar);
				return;
			}
		}
        		
		// Handle one character macro shortcuts
		if (!control && !meta) {
			Hashtable macroShortcuts = Menus.getMacroShortcuts();
			if (macroShortcuts.size()>0) {
				if (shift)
					c = (String)macroShortcuts.get(new Integer(keyCode+200));
				else
					c = (String)macroShortcuts.get(new Integer(keyCode));
				if (c!=null) {
						MacroInstaller.doShortcut(c);
						return;
				}
			}
		}

		if (!Prefs.requireControlKey || control || meta) {
			Hashtable shortcuts = Menus.getShortcuts();
			if (shift)
				c = (String)shortcuts.get(new Integer(keyCode+200));
			else
				c = (String)shortcuts.get(new Integer(keyCode));
		}
		
		if (c==null) {
			switch (keyChar) {
				case '<': c="Previous Slice [<]"; break;
				case '>': c="Next Slice [>]"; break;
				case '+': case '=': c="In"; break;
				case '-': c="Out"; break;
				case '/': c="Reslice [/]..."; break;
				default:
			}
		}

		if (c==null) {
			switch(keyCode) {
				case KeyEvent.VK_TAB: WindowManager.putBehind(); return;
				case KeyEvent.VK_BACK_SPACE: c="Clear"; hotkey=true; break; // delete
				case KeyEvent.VK_BACK_SLASH: c="Start Animation"; break;
				case KeyEvent.VK_EQUALS: c="In"; break;
				case KeyEvent.VK_MINUS: c="Out"; break;
				case KeyEvent.VK_SLASH: case 0xbf: c="Reslice [/]..."; break;
				case KeyEvent.VK_COMMA: case 0xbc: c="Previous Slice [<]"; break;
				case KeyEvent.VK_PERIOD: case 0xbe: c="Next Slice [>]"; break;
				case KeyEvent.VK_LEFT: case KeyEvent.VK_RIGHT: case KeyEvent.VK_UP: case KeyEvent.VK_DOWN: // arrow keys
					Roi roi = null;
					if (imp!=null) roi = imp.getRoi();
					if (roi==null) return;
					if ((flags & KeyEvent.ALT_MASK) != 0)
						roi.nudgeCorner(keyCode);
					else
						roi.nudge(keyCode);
					return;
				case KeyEvent.VK_ESCAPE:
					if (imp!=null) {
						ImageWindow win = imp.getWindow();
						if (win!=null) {
							win.running = false;
							win.running2 = false;
						}
					}
					Macro.abort();
					Interpreter.abort();
					if (Interpreter.getInstance()!=null)
						IJ.beep();
					return;
				case KeyEvent.VK_ENTER: this.toFront(); return;
				default: break;
			}
		}
		
		if (c!=null && !c.equals("")) {
			if (c.equals("Fill"))
				hotkey = true;
			if (c.charAt(0)==MacroInstaller.commandPrefix)
				MacroInstaller.doShortcut(c);
			else {
				doCommand(c);
				keyPressedTime = System.currentTimeMillis();
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		IJ.setKeyUp(e.getKeyCode());
	}
		
	public void keyTyped(KeyEvent e) {}

	public void windowClosing(WindowEvent e) {
		doCommand("Quit");
	}

	public void windowActivated(WindowEvent e) {
		if (IJ.isMacintosh()) {
			IJ.wait(10); // may be needed for Java 1.4 on OS X
			setMenuBar(Menus.getMenuBar());
		}
	}
	
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	
	/** Adds the specified class to a Vector to keep it from being
		garbage collected, causing static fields to be reset. */
	public void register(Class c) {
		if (!classes.contains(c))
			classes.addElement(c);
	}

	/** Called by ImageJ when the user selects Quit. */
	public void quit() {
		Thread thread = new Thread(this, "Quit");
		thread.setPriority(Thread.NORM_PRIORITY);
		thread.start();
	}
	
	/** Returns true if ImageJ is exiting. */
	public boolean quitting() {
		return quitting;
	}
	
	/** Called once when ImageJ quits. */
	public void savePreferences(Properties prefs) {
		Point loc = getLocation();
		prefs.put(IJ_X, Integer.toString(loc.x));
		prefs.put(IJ_Y, Integer.toString(loc.y));
		//prefs.put(IJ_WIDTH, Integer.toString(size.width));
		//prefs.put(IJ_HEIGHT, Integer.toString(size.height));
	}

	public static void main(String args[]) {
		boolean noGUI = false;
		int nArgs = args!=null?args.length:0;
		for (int i=0; i<nArgs; i++) {
			String arg = args[i];
			if (arg==null) continue;
			//IJ.log(i+"  "+arg);
			if (args[i].startsWith("-")) {
				if (args[i].startsWith("-batch"))
					noGUI = true;
				else if (args[i].startsWith("-ijpath") && i+1<nArgs) {
					Prefs.setHomeDir(args[i+1]);
					args[i+1] = null;
				} else if (args[i].startsWith("-port")) {
					int delta = (int)Tools.parseDouble(args[i].substring(5, args[i].length()), 0.0);
					if (delta>0 && DEFAULT_PORT+delta<65536)
						port = DEFAULT_PORT+delta;
				}
			} 
		}
  		// If ImageJ is already running then isRunning()
  		// will pass the arguments to it using sockets.
		if (nArgs>0 && !noGUI && isRunning(args))
  				return;
 		ImageJ ij = IJ.getInstance();    	
		if (!noGUI && (ij==null || (ij!=null && !ij.isShowing()))) {
			ij = new ImageJ(null);
			ij.exitWhenQuiting = true;
		}
		int macros = 0;
		for (int i=0; i<nArgs; i++) {
			String arg = args[i];
			if (arg==null) continue;
			if (arg.startsWith("-")) {
				if ((arg.startsWith("-macro") || arg.startsWith("-batch")) && i+1<nArgs) {
					String arg2 = i+2<nArgs?args[i+2]:null;
					IJ.runMacroFile(args[i+1], arg2);
					break;
				} else if (arg.startsWith("-eval") && i+1<nArgs) {
					String rtn = IJ.runMacro(args[i+1]);
					if (rtn!=null)
						System.out.print(rtn);
					args[i+1] = null;
				} else if (arg.startsWith("-run") && i+1<nArgs) {
					IJ.run(args[i+1]);
					args[i+1] = null;
				}
			} else if (macros==0 && (arg.endsWith(".ijm") || arg.endsWith(".txt"))) {
				IJ.runMacroFile(arg);
				macros++;
			} else if (arg.indexOf("ij.ImageJ")==-1)
				IJ.open(arg);
		}
		if (noGUI) System.exit(0);
	}
	
	// Is there another instance of ImageJ? If so, send it the arguments and quit.
	static boolean isRunning(String args[]) {
		int macros = 0;
		int nArgs = args.length;
		if (nArgs==2 && args[0].startsWith("-ijpath"))
			return false;
		int nCommands = 0;
		try {
			sendArgument("user.dir "+System.getProperty("user.dir"));
			for (int i=0; i<nArgs; i++) {
				String arg = args[i];
				if (arg==null) continue;
				String cmd = null;
				if (macros==0 && arg.endsWith(".ijm")) {
					cmd = "macro " + arg;
					macros++;
				} else if (arg.startsWith("-macro") && i+1<nArgs) {
					String macroArg = i+2<nArgs?"("+args[i+2]+")":"";
					cmd = "macro " + args[i+1] + macroArg;
					sendArgument(cmd);
					nCommands++;
					break;
				} else if (arg.startsWith("-eval") && i+1<nArgs) {
					cmd = "eval " + args[i+1];
					args[i+1] = null;
				} else if (arg.startsWith("-run") && i+1<nArgs) {
					cmd = "run " + args[i+1];
					args[i+1] = null;
				} else if (arg.indexOf("ij.ImageJ")==-1 && !arg.startsWith("-"))
					cmd = "open " + arg;
				if (cmd!=null) {
					sendArgument(cmd);
					nCommands++;
				}
			} // for
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	static void sendArgument(String arg) throws IOException {
		Socket socket = new Socket("localhost", port);
		PrintWriter out = new PrintWriter (new OutputStreamWriter(socket.getOutputStream()));
		out.println(arg);
		out.close();
		socket.close();
	}
	
	/**
	Returns the port that ImageJ checks on startup to see if another instance is running.
	@see ij.SocketListener
	*/
	public static int getPort() {
		return port;
	}

	/** Quit using a separate thread, hopefully avoiding thread deadlocks. */
	public void run() {
		quitting = true;
		boolean changes = false;
		int[] wList = WindowManager.getIDList();
		if (wList!=null) {
			for (int i=0; i<wList.length; i++) {
				ImagePlus imp = WindowManager.getImage(wList[i]);
				if (imp!=null & imp.changes==true) {
					changes = true;
					break;
				}
			}
		}
		if (!changes && Menus.window.getItemCount()>Menus.WINDOW_MENU_ITEMS) {
			GenericDialog gd = new GenericDialog("ImageJ", this);
			gd.addMessage("Are you sure you want to quit ImageJ?");
			gd.showDialog();
			quitting = !gd.wasCanceled();
		}
		if (!quitting)
			return;
		if (!WindowManager.closeAllWindows()) {
			quitting = false;
			return;
		}
		//IJ.log("savePreferences");
		if (applet==null)
			Prefs.savePreferences();
		setVisible(false);
		//IJ.log("dispose");
		dispose();
		if (exitWhenQuiting)
			System.exit(0);
	}

}
