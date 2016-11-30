package ij.plugin.frame;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import ij.*;
import ij.gui.*;

/** This is a simple TextArea based editor for editing and compiling plugins. */
public class Editor extends PlugInFrame implements ActionListener, TextListener {

	public static final int MAX_SIZE = 28000;
	private TextArea ta;
	private String path;
	private boolean changes;
	private static String searchString = "";
	private static int lineNumber = 1;

	public Editor() {
		super("Editor");

		MenuBar mb = new MenuBar();
		Menu m = new Menu("File");
		m.add(new MenuItem("Save", new MenuShortcut(KeyEvent.VK_S)));
		m.add(new MenuItem("Save As..."));
		m.add(new MenuItem("Compile and Run", new MenuShortcut(KeyEvent.VK_R)));
		m.addActionListener(this);
		mb.add(m);
		
		m = new Menu("Edit");
		m.add(new MenuItem("Find...", new MenuShortcut(KeyEvent.VK_F)));
		m.add(new MenuItem("Find Next", new MenuShortcut(KeyEvent.VK_G)));
		m.add(new MenuItem("Go to Line...", new MenuShortcut(KeyEvent.VK_L)));
		m.addActionListener(this);
		mb.add(m);
		setMenuBar(mb);

		ta = new TextArea();
		ta.addTextListener(this);
		add(ta);
		pack();
	}
			
	public void open(String dir, String name) {
		path = dir+name;
		File file = new File(path);
		int size = (int)file.length();
		if (size>MAX_SIZE && !IJ.isMacintosh()) {
			IJ.error("This file is too large for ImageJ to open.\n"
				+" \n"
				+"    File size: "+size+" bytes\n"
				+"    Max. size: "+MAX_SIZE+" bytes");
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
			ta.append(new String(sb));
			ta.setCaretPosition(0);
			setTitle(name);
			changes = false;
			setVisible(true);
		}
		catch (Exception e) {
			IJ.error(e.getMessage());
			return;
		}
	}

	public void display(String title, String text) {
		ta.selectAll();
		ta.replaceRange(text,ta.getSelectionStart(),ta.getSelectionEnd());
		ta.setCaretPosition(0);
		setTitle(title);
		changes = false;
		setVisible(true);
	}

	void save() {
		if (path==null)
			return;
		//try   {
		//	FileWriter out = new FileWriter(path);
		//	String text = ta.getText();
		//	out.write(text);
		//	out.close();
		//	changes = false;
		//}   catch (IOException e) {
		//	IJ.error(e.getMessage());
		//	return;
		//}
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
			changes = false;
		} catch
			(IOException e) {}
	}

	void compileAndRun() {
		if (path!=null) {
			save();
			IJ.runPlugIn("ij.plugin.Compiler", path);
		}
	}


	public void actionPerformed(ActionEvent evt) {
		String what = evt.getActionCommand();
		if ("Save".equals(what))
			save();
		else if ("Compile and Run".equals(what))
			compileAndRun();
		else if ("Save As...".equals(what))
			saveAs();
		else if ("Find...".equals(what))
			find(null);
		else if ("Find Next".equals(what))
			find(searchString);
		else if ("Go to Line...".equals(what))
			gotoLine();
	}

	public void textValueChanged(TextEvent evt) {
		changes = true;
	}

	public void processWindowEvent(WindowEvent e) {
		boolean canceled = true;
		if (e.getID()==WindowEvent.WINDOW_CLOSING) {
			if (getTitle().equals("Errors") || close()) {	
				setVisible(false);
				dispose();
			}
		} else
			super.processWindowEvent(e);
	}

	boolean close() {
		boolean okay = true;
		if (changes) {
			SaveChangesDialog d = new SaveChangesDialog(this, getTitle());
			if (d.cancelPressed())
				okay = false;
			else if (d.savePressed())
				save();
		}
		return okay;
	}

	void saveAs() {
		FileDialog fd = new FileDialog(this, "Save As...", FileDialog.SAVE);
		fd.setFile(getTitle());
		fd.setDirectory(Menus.getPlugInsPath());
		fd.setVisible(true);
		String name = fd.getFile();
		String dir = fd.getDirectory();
		fd.dispose();
		if (name!=null) {
			path = dir+name;
			save();
			changes = false;
			setTitle(name);
		}
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
	
}



