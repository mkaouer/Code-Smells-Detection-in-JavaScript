package ij;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.awt.image.*;
import ij.gui.*;
import ij.process.*;
import ij.io.*;
import ij.plugin.*;
import ij.plugin.filter.*;
import ij.text.*;
import ij.macro.Interpreter;
import ij.plugin.frame.Editor;

/**
This frame is the main ImageJ class.
<p>
ImageJ is a work of the United States Government. It is in the public domain 
and open source. There is no copyright. You are free to do anything you want 
with this source but I like to get credit for my work and I would like you to 
offer your changes to me so I can possibly add them to the "official" version.

@author Wayne Rasband (wayne@codon.nih.gov)
*/
public class ImageJ extends Frame implements ActionListener, 
	MouseListener, KeyListener, WindowListener, ItemListener {

	public static final String VERSION = "1.30s";
	public static Color backgroundColor = new Color(220,220,220); //224,226,235

	private static final String IJ_X="ij.x",IJ_Y="ij.y";
	private static final String RESULTS_X="results.x",RESULTS_Y="results.y",
		RESULTS_WIDTH="results.width",RESULTS_HEIGHT="results.height";
	
	private Toolbar toolbar;
	private Panel statusBar;
	private ProgressBar progressBar;
	private Label statusLine;
	private boolean firstTime = true;
	private java.applet.Applet applet; // null if not running as an applet
	private Vector classes = new Vector();
	private boolean exitWhenQuiting;
	
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
		setResizable(false);
		show();
		if (IJ.isMacOSX()) { // hack needed for window to display correctly Mac OS X
			setLocation(loc.x+1, loc.y+1);
			setLocation(loc.x, loc.y);			
			pack(); 
		}
		if (err1!=null)
			IJ.error(err1);
		if (err2!=null)
			IJ.error(err2);
		if (IJ.isMacintosh())
			IJ.runPlugIn("QuitHandler", "");
		if (IJ.isJava2() && applet==null) {
			IJ.runPlugIn("ij.plugin.DragAndDrop", "");
		}
		int nMacros = m.installMacros();
		String str = nMacros==1?" macro)":" macros)";
		IJ.showStatus("Version "+VERSION + " ("+ Menus.nPlugins + " commands, " + nMacros + str);
		// Toolbar.getInstance().addTool("Spare tool [Cf0fG22ccCf00E22cc]"); 
	}
    
	void showResults() {
		TextWindow resultsWindow = new TextWindow("Results", "", 300, 200);
		TextPanel textPanel = resultsWindow.getTextPanel();
		textPanel.addKeyListener(this);
		IJ.setTextPanel(textPanel);
	}
	
	void setIcon() {
		URL url = this .getClass() .getResource("/microscope.gif"); 
		if (url==null)
			return;
		Image img = null;
		try {img = createImage((ImageProducer)url.getContent());}
		catch(Exception e) {}
		if (img!=null)
			setIconImage(img);
	}
	
	public Point getPreferredLocation() {
		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int ijX = Prefs.getInt(IJ_X,-99);
		int ijY = Prefs.getInt(IJ_Y,-99);
		if (ijX!=-99 && ijY!=-99 && ijX<(screenWidth-75))
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
			hotkey = false;
			if (cmd!=null)
				doCommand(cmd);
			if (IJ.debugMode) IJ.log("actionPerformed: "+e);
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
		IJ.showStatus(IJ.freeMemory());
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
		String c = "";
		ImagePlus imp = WindowManager.getCurrentImage();
		boolean isStack = (imp!=null) && (imp.getStackSize()>1);
		
		if (imp!=null && !control && ((keyChar>=32 && keyChar<=255) || keyChar=='\b' || keyChar=='\n')) {
			Roi roi = imp.getRoi();
			if (roi instanceof TextRoi) {
				if (alt)
					switch (keyChar) {
						case 'u': case 'm': keyChar = '�'; break;
						case 'A': keyChar = '�'; break;
						default:
					}				
				((TextRoi)roi).addChar(keyChar);
				return;
			}
		}
        		
		Hashtable shortcuts = Menus.getShortcuts();
		if (shift)
			c = (String)shortcuts.get(new Integer(keyCode+200));
		else
			c = (String)shortcuts.get(new Integer(keyCode));

		if (c==null)
			switch(keyCode) {
				case KeyEvent.VK_TAB: WindowManager.putBehind(); return;
				case KeyEvent.VK_BACK_SPACE: c="Clear"; hotkey=true; break; // delete
				case KeyEvent.VK_EQUALS: case 0xbb: c="Start Animation [=]"; break;
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
					if (imp!=null)
						imp.getWindow().running = false;
					Macro.abort();
					Interpreter.abort();
					if (Interpreter.getInstance()!=null)
						IJ.beep();
					return;
				case KeyEvent.VK_ENTER: this.toFront(); return;
				default: break;
			}
		if (c!=null && !c.equals("")) {
			if (c.equals("Fill"))
				hotkey = true;
			if (c.charAt(0)==MacroInstaller.commandPrefix)
				MacroInstaller.doShortcut(c);
			else
				doCommand(c);
		}
	}

	public void keyReleased(KeyEvent e) {
		IJ.setKeyUp(e.getKeyCode());
	}
		
	public void keyTyped(KeyEvent e) {}

	public void windowClosing(WindowEvent e) {
		boolean quit = true;
		if (Menus.window.getItemCount()>Menus.WINDOW_MENU_ITEMS)
			quit = IJ.showMessageWithCancel("ImageJ",
				"Are you sure you want to quit ImageJ?");
		if (quit)
			doCommand("Quit");
	}

	public void windowActivated(WindowEvent e) {
		if (IJ.isMacintosh())
			this.setMenuBar(Menus.getMenuBar());
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
		//IJ.log("quit: "+exitWhenQuiting); IJ.wait(5000);
		if (!WindowManager.closeAllWindows())
			return;
		//IJ.log("savePreferences");
		if (applet==null)
			Prefs.savePreferences();
		setVisible(false);
		//IJ.log("dispose");
		dispose();
		if (exitWhenQuiting)
			System.exit(0);
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
		ImageJ ij = IJ.getInstance();    	
		if (ij==null || (ij!=null && !ij.isShowing())) {
			ij = new ImageJ(null);
			ij.exitWhenQuiting = true;
		}
		boolean macroStarted = false;
		if (args!=null) {
			for (int i=0; i<args.length; i++) {
				//IJ.log(i+" "+args[i]);
				if (args[i].endsWith(".txt")) {
					if (macroStarted)
						new Opener().open(args[i]);
					else {
       					new ij.macro.MacroRunner(new File(args[i]));
       					macroStarted = true;
       				}
				} else {
					Opener opener = new Opener();
					ImagePlus imp = opener.openImage(args[i]);
					if (imp!=null)
					imp.show();
				}
			}
		}
	}


} //class ImageJ
