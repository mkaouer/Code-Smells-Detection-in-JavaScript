package ij.plugin.frame;
import java.awt.*;
import java.awt.event.*;
import ij.*;
import ij.plugin.*;
import ij.gui.*;
import ij.process.*;

/** Implements ImageJ's Paste Control window. */
public class PasteController extends PlugInFrame implements PlugIn, ItemListener {

	private Panel panel;
	private Choice pasteMode;
	private static Frame instance;
	
	public PasteController() {
		super("Paste Control");
		if (instance!=null) {
			instance.toFront();
			return;
		}
		WindowManager.addWindow(this);
		instance = this;
		IJ.register(PasteController.class);
		setLayout(new FlowLayout(FlowLayout.CENTER, 2, 5));
		
		add(new Label(" Transfer Mode:"));
		pasteMode = new Choice();
		pasteMode.addItem("  Copy");
		pasteMode.addItem("  Blend");
		pasteMode.addItem("  Difference");
		pasteMode.addItem("  Transparent");
		pasteMode.addItem("  AND");
		pasteMode.addItem("  OR");
		pasteMode.addItem("  XOR");
		pasteMode.addItem("  Add");
		pasteMode.addItem("  Subtract");
		pasteMode.addItem("  Multiply");
		pasteMode.addItem("  Divide");
		pasteMode.select("  Copy");
		pasteMode.addItemListener(this);
		add(pasteMode);
		Roi.setPasteMode(Blitter.COPY);

		pack();
		GUI.center(this);
		setResizable(false);
		setVisible(true);
	}
	
	public void itemStateChanged(ItemEvent e) {
		int index = pasteMode.getSelectedIndex();
		int mode = Blitter.COPY;
		switch (index) {
			case 0: mode = Blitter.COPY; break;
			case 1: mode = Blitter.AVERAGE; break;
			case 2: mode = Blitter.DIFFERENCE; break;
			case 3: mode = Blitter.COPY_TRANSPARENT; break;
			case 4: mode = Blitter.AND; break;
			case 5: mode = Blitter.OR; break;
			case 6: mode = Blitter.XOR; break;
			case 7: mode = Blitter.ADD; break;
			case 8: mode = Blitter.SUBTRACT; break;
			case 9: mode = Blitter.MULTIPLY; break;
			case 10: mode = Blitter.DIVIDE; break;
		}
		Roi.setPasteMode(mode);
		ImagePlus imp = WindowManager.getCurrentImage();
	}
	
    public void windowClosing(WindowEvent e) {
		super.windowClosing(e);
		instance = null;
	}

}