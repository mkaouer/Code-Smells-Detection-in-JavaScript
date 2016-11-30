package ij.plugin.frame;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import ij.*;
import ij.gui.*;
import ij.util.Tools;
import ij.text.TextWindow;
import ij.macro.*;
import ij.plugin.MacroInstaller;
import java.awt.datatransfer.*;																																																																																																																																																																																																																																																									 import java.util.*;
																																																																																																																																																					   

/** This is a simple TextArea based editor for editing and compiling plugins. */
public class Editor extends PlugInFrame implements ActionListener,
TextListener, ClipboardOwner, MacroConstants {

	public static final int MAX_SIZE = 28000, MAX_MACROS=50, XINC=10, YINC=18;
	private TextArea ta;
	private String path;
	private boolean changes;
	private static String searchString = "";
	private static int lineNumber = 1;
	private static int xoffset, yoffset;
	private static int nWindows;
	private Menu fileMenu;
	private Properties p = new Properties();
	private int[] macroStarts;
	private String[] macroNames;
	private MenuBar mb = new MenuBar();
	private Menu macrosMenu;
	private int nMacros;
	private Program pgm;
	private boolean firstEvent = true;
	private String shortcutsInUse;
	private int inUseCount;
	private int nShortcuts;
	private MacroInstaller installer;
	
	public Editor() {
		super("Editor");
		WindowManager.addWindow(this);

		Menu m = new Menu("File");
		m.add(new MenuItem("Save", new MenuShortcut(KeyEvent.VK_S)));
		m.add(new MenuItem("Save As..."));
		m.add(new MenuItem("Print...", new MenuShortcut(KeyEvent.VK_P)));
		m.addSeparator();
		m.add(new MenuItem("Compile and Run", new MenuShortcut(KeyEvent.VK_R)));
		m.addActionListener(this);
		fileMenu = m;
		mb.add(m);
		
		m = new Menu("Edit");
		String key = IJ.isMacintosh()?" = Cmd ":" = Ctrl+";
		MenuItem item = new MenuItem("Undo"+key+"Z");
		item.setEnabled(false);
		m.add(item);
		m.addSeparator();
		boolean shortcutsBroken = IJ.isWindows()
			&& System.getProperty("java.version").indexOf("1.1.8")>=0;
		if (shortcutsBroken)
			item = new MenuItem("Cut");
		else
			item = new MenuItem("Cut",new MenuShortcut(KeyEvent.VK_X));
		m.add(item);
		if (shortcutsBroken)
			item = new MenuItem("Copy");
		else
			item = new MenuItem("Copy", new MenuShortcut(KeyEvent.VK_C));
		m.add(item);
		if (shortcutsBroken)
			item = new MenuItem("Paste");
		else
			item = new MenuItem("Paste",new MenuShortcut(KeyEvent.VK_V));
		m.add(item);
		m.addSeparator();
		m.add(new MenuItem("Find...", new MenuShortcut(KeyEvent.VK_F)));
		m.add(new MenuItem("Find Next", new MenuShortcut(KeyEvent.VK_G)));
		m.add(new MenuItem("Go to Line...", new MenuShortcut(KeyEvent.VK_L)));
		m.addActionListener(this);
		mb.add(m);
		setMenuBar(mb);

		ta = new TextArea(16, 60);
		ta.addTextListener(this);
		if (IJ.isMacOSX())
			ta.setFont(new Font("SansSerif",Font.PLAIN,12));
 		addKeyListener(IJ.getInstance());  // ImageJ handles keyboard shortcuts
		add(ta);
		pack();
		positionWindow();
		display("Test.java", "");
		IJ.register(Editor.class);
	}
			
	public void positionWindow() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension window = getSize();
		if (window.width==0)
			return;
		int left = screen.width/2-window.width/2;
		int top = (screen.height-window.height)/4;
		if (top<0) top = 0;
		if (nWindows<=0 || xoffset>8*XINC)
			{xoffset=0; yoffset=0;}
		setLocation(left+xoffset, top+yoffset);
		xoffset+=XINC; yoffset+=YINC;
		nWindows++;
	}

	void setWindowTitle(String title) {
		Menus.updateWindowMenuItem(getTitle(), title);
		setTitle(title);
	}
	
	public void create(String name, String text) {
		ta.append(text);
		ta.setCaretPosition(0);
		setWindowTitle(name);
		if (!name.endsWith(".java")) {
			fileMenu.remove(4);
			fileMenu.insert(new MenuItem("Run Macro", new MenuShortcut(KeyEvent.VK_R)), 4);
			int itemCount = fileMenu.getItemCount();
			if (itemCount==5)
				fileMenu.insert(new MenuItem("Abort Macro"), 5);
			macrosMenu = new Menu("Macros");			
			macrosMenu.add(new MenuItem("Install Macros", new MenuShortcut(KeyEvent.VK_I)));
			macrosMenu.addSeparator();
			macrosMenu.addActionListener(this);
			mb.add(macrosMenu);
			if (text.indexOf("macro ")!=-1)
				installMacros(text, false);				
		}
		show();
		changes = false;
	}

	void installMacros(String text, boolean installInPluginsMenu) {
		installer = new MacroInstaller();
		installer.setFileName(getTitle());
		int nShortcuts = installer.install(text, macrosMenu);
		if (installInPluginsMenu || nShortcuts>0)
			installer.install(null);
	}
		
	public void open(String dir, String name) {
		path = dir+name;
		File file = new File(path);
		int size = (int)file.length();
		if (size>MAX_SIZE && !(IJ.isJava2()||IJ.isMacintosh())) {
			IJ.error("This file is too large for ImageJ to open.\n"
				+" \n"
				+"	  File size: "+size+" bytes\n"
				+"	  Max. size: "+MAX_SIZE+" bytes");
			dispose();
			return;
		}

		try {
			StringBuffer sb = new StringBuffer(5000);
			BufferedReader r = new BufferedReader(new FileReader(file));
			while (true) {
				String s=r.readLine();
				if (s==null)
					break;
				else
					sb.append(s+"\n");
			}
			r.close();
			create(name, new String(sb));
			changes = false;
		}
		catch (Exception e) {
			IJ.error(e.getMessage());
			return;
		}
	}

	public String getText() {
		if (ta==null)
			return "";
		else
			return ta.getText();
	}

	public void display(String title, String text) {
		ta.selectAll();
		ta.replaceRange(text,ta.getSelectionStart(),ta.getSelectionEnd());
		ta.setCaretPosition(0);
		setWindowTitle(title);
		changes = false;
		show();
	}

	void save() {
		if (path==null) {
			saveAs(); 
			return;
		}
		String text = ta.getText();
		char[] chars = new char[text.length()];
		text.getChars(0, text.length(), chars, 0);
		try {
			BufferedReader br = new BufferedReader(new CharArrayReader(chars));
			BufferedWriter bw = new BufferedWriter(new FileWriter(path));
			while (true) {
				String s = br.readLine();
				if (s==null) break;
				bw.write(s, 0, s.length());
				bw.newLine();
			}
			bw.close();
			IJ.showStatus(text.length()+" chars saved to " + path);
			changes = false;
		} catch
			(IOException e) {}
	}

	void compileAndRun() {
		if (path==null)
			saveAs();
		if (path!=null) {
			save();
			IJ.runPlugIn("ij.plugin.Compiler", path);
		}
	}
	
	void runMacro() {
		int start = ta.getSelectionStart();
		int end = ta.getSelectionEnd();
		String text;
		if (start==end)
			text = ta.getText();
		else
			text = ta.getSelectedText();
		new MacroRunner(text);
	}

	void print () {
		PrintJob pjob = Toolkit.getDefaultToolkit().getPrintJob(this, "Cool Stuff", p);
		if (pjob != null) {
			Graphics pg = pjob.getGraphics( );
			if (pg != null) {
				String s = ta.getText();
				printString(pjob, pg, s);
				pg.dispose( );	
			}
			pjob.end( );
		}
	}

	void printString (PrintJob pjob, Graphics pg, String s) {
		int pageNum = 1;
		int linesForThisPage = 0;
		int linesForThisJob = 0;
		int topMargin = 30;
		int leftMargin = 30;
		int bottomMargin = 30;
		
		if (!(pg instanceof PrintGraphics))
			throw new IllegalArgumentException ("Graphics contextt not PrintGraphics");
		if (IJ.isMacintosh()) {
			topMargin = 0;
			leftMargin = 0;
			bottomMargin = 0;
		}
		StringReader sr = new StringReader (s);
		LineNumberReader lnr = new LineNumberReader (sr);
		String nextLine;
		int pageHeight = pjob.getPageDimension().height - bottomMargin;
		Font helv = new Font("Helvetica", Font.PLAIN, 10);
		pg.setFont (helv);
		FontMetrics fm = pg.getFontMetrics(helv);
		int fontHeight = fm.getHeight();
		int fontDescent = fm.getDescent();
		int curHeight = topMargin;
		try {
			do {
				nextLine = lnr.readLine();
			   if (nextLine != null) {		   
					nextLine = detabLine(nextLine);
					if ((curHeight + fontHeight) > pageHeight) {
						// New Page
						pageNum++;
						linesForThisPage = 0;
						pg.dispose();
						pg = pjob.getGraphics();
						if (pg != null)
							pg.setFont (helv);
						curHeight = topMargin;
					}
					curHeight += fontHeight;
					if (pg != null) {
						pg.drawString (nextLine, leftMargin, curHeight - fontDescent);
						linesForThisPage++;
						linesForThisJob++;
					} 
				}
			} while (nextLine != null);
		} catch (EOFException eof) {
	   // Fine, ignore
		} catch (Throwable t) { // Anything else
			t.printStackTrace();
		}
	}
	
	String detabLine(String s) {
		if (s.indexOf('\t')<0)
			return s;
		int tabSize = 4;
		StringBuffer sb = new StringBuffer((int)(s.length()*1.25));
		char c;
		for (int i=0; i<s.length(); i++) {
			c = s.charAt(i);
			if (c=='\t') {
				  for (int j=0; j<tabSize; j++)
					  sb.append(' '); 
		} else
			sb.append(c);
		 }
		return sb.toString();
  }	   

	boolean copy() { 
		String s; 
		s = ta.getSelectedText();
		Clipboard clip = getToolkit().getSystemClipboard();
		if (clip!=null) {
			StringSelection cont = new StringSelection(s);
			clip.setContents(cont,this);
			return true;
		} else
			return false;
	}
 
	  
	void cut() {
		if (copy()) {
			int start = ta.getSelectionStart();
			int end = ta.getSelectionEnd();
			ta.replaceRange("", start, end);
			if (IJ.isMacOSX())
				ta.setCaretPosition(start);
		}	
	}

	void paste() {
		String s;
		s = ta.getSelectedText();
		Clipboard clipboard = getToolkit( ). getSystemClipboard(); 
		Transferable clipData = clipboard.getContents(s);
		try {
			s = (String)(clipData.getTransferData(DataFlavor.stringFlavor));
		}
		catch  (Exception e)  {
			s  = e.toString( );
		}
		int start = ta.getSelectionStart( );
		int end = ta.getSelectionEnd( );
		ta.replaceRange(s, start, end); 
		if (IJ.isMacOSX())
			ta.setCaretPosition(start+s.length());
	}

	public void actionPerformed(ActionEvent evt) {
		String what = evt.getActionCommand();
		if ("Save".equals(what))
			save();
		else if ("Compile and Run".equals(what))
				compileAndRun();
		else if ("Run Macro".equals(what))
				runMacro();
		else if ("Abort Macro".equals(what)) {
				Interpreter.abort();
				IJ.beep();		
		} else if ("Install Macros".equals(what))
				installMacros(ta.getText(), true);
		else if ("Print...".equals(what))
			print();
		else if("Paste".equals(what))
			paste();
		else if ("Copy".equals (what))
			copy();
		else if ("Cut".equals(what))
		   cut();
		else if ("Save As...".equals(what))
			saveAs();
		else if ("Find...".equals(what))
			find(null);
		else if ("Find Next".equals(what))
			find(searchString);
		else if ("Go to Line...".equals(what))
			gotoLine();
		else
			installer.runMacro(what);
	}

	public void textValueChanged(TextEvent evt) {
		if (firstEvent)  // first textValueChanged event may be bogus
			firstEvent = false;
		else
			changes = true;
	}

	/** Override windowActivated in PlugInFrame to
		prevent Mac meno bar from being installed. */
	public void windowActivated(WindowEvent e) {
		WindowManager.setWindow(this);
	}

	public void windowClosing(WindowEvent e) {
		close();
	}

	/** Overrides close() in PlugInFrame. */
	public void close() {
		boolean okayToClose = true;
		if (!getTitle().equals("Errors") && changes) {
			SaveChangesDialog d = new SaveChangesDialog(this, getTitle());
			if (d.cancelPressed())
				okayToClose = false;
			else if (d.savePressed())
				save();
		}
		if (okayToClose) {
			setVisible(false);
			dispose();
			WindowManager.removeWindow(this);
			nWindows--;
		}
	}

	void saveAs() {
		FileDialog fd = new FileDialog(this, "Save Plugin As...", FileDialog.SAVE);
		String name1 = getTitle();
		fd.setFile(name1);
		String pluginsDir = Menus.getPlugInsPath();
		if (path!=null)
			fd.setDirectory(pluginsDir);
		fd.show();
		String name2 = fd.getFile();
		String dir = fd.getDirectory();
		fd.dispose();
		if (name2!=null) {
			if (name2.endsWith(".java"))
				updateClassName(name1, name2);
			path = dir+name2;
			save();
			changes = false;
			setWindowTitle(name2);
		}
	}
	
	void updateClassName(String oldName, String newName) {
		if (newName.indexOf("_")<0)
			IJ.showMessage("Plugin Editor", "Plugins without an underscore in their name will not\n"
				+"be automatically installed when ImageJ is restarted.");
		if (oldName.equals(newName) || !oldName.endsWith(".java") || !newName.endsWith(".java"))
			return;
		oldName = oldName.substring(0,oldName.length()-5);
		newName = newName.substring(0,newName.length()-5);
		String text1 = ta.getText();
		int index = text1.indexOf("public class "+oldName);
		if (index<0)
			return;
		String text2 = text1.substring(0,index+13)+newName+text1.substring(index+13+oldName.length(),text1.length());
		ta.setText(text2);
	}
	
	void find(String s) {
		if (s==null) {
			GenericDialog gd = new GenericDialog("Find", this);
			gd.addStringField("Find: ", searchString, 20);
			gd.showDialog();
			if (gd.wasCanceled())
				return;
			s = gd.getNextString();
		}
		if (s.equals(""))
			return;
		String text = ta.getText();
		int index = text.indexOf(s, ta.getCaretPosition()+1);
		if (index<0)
			{IJ.beep(); return;}
		ta.setSelectionStart(index);
		ta.setSelectionEnd(index+s.length());
		searchString = s;
	}
	
	void gotoLine() {
		GenericDialog gd = new GenericDialog("Go to Line", this);
		gd.addNumericField("Go to line number: ", lineNumber, 0);
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		int n = (int)gd.getNextNumber();
		if (n<1) return;
		String text = ta.getText();
		char[] chars = new char[text.length()];
		chars = text.toCharArray();
		int count=1, loc=0;
		for (int i=0; i<chars.length; i++) {
			if (chars[i]=='\n') count++;
			if (count==n)
				{loc=i+1; break;}
		}
		ta.setCaretPosition(loc);
		lineNumber = n;
	}

	/*
	public void keyPressed(KeyEvent e) {
		if ((e.getModifiers()&Event.CTRL_MASK)==0)
			return;
		int keyCode = e.getKeyCode();
		//IJ.write("keyCode: "+keyCode); 
		switch(keyCode) {
			case KeyEvent.VK_S: save(); break;
			case KeyEvent.VK_P: print(); break;
			case KeyEvent.VK_R: compileAndRun(); break;
			case KeyEvent.VK_X: cut(); break;
			case KeyEvent.VK_C: copy(); break;
			case KeyEvent.VK_V: paste(); break;
			case KeyEvent.VK_F: find(null); break;
			case KeyEvent.VK_G: find(searchString); break;
			case KeyEvent.VK_L: gotoLine(); break;
			default:
		}
	}
	*/
	
	//public void keyReleased(KeyEvent e) {}
	//public void keyTyped(KeyEvent e) {}
	public void lostOwnership (Clipboard clip, Transferable cont) {}

}

