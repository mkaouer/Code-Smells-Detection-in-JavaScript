package ij.plugin.filter;
import ij.*;
import ij.gui.*;
import ij.process.*;
import ij.measure.*;
import java.awt.*;
import java.awt.event.*;

/** Implements the Analyze/Set Scale command. */
public class ScaleDialog implements PlugInFilter {

    private ImagePlus imp;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		IJ.register(ScaleDialog.class);
		return DOES_ALL+NO_CHANGES;
	}

	public void run(ImageProcessor ip) {
		double measured = 0.0;
		double known = 1.0;
		double aspectRatio = 1.0;
		String unit = "cm";
		boolean oldGlobal = Calibrator.global;
		Calibration cal = imp.getCalibration();
		boolean isCalibrated = cal.scaled();
		
		String scale = "<no scale>";
		int digits = 2;
		if (isCalibrated) {
			measured = 1.0/cal.pixelWidth;
			if ((int)measured==measured)
				digits = 0;
			if (measured<0.01)
				digits = 3;
			else if (measured<0.001)
				digits = 4;
			known = 1.0;
			aspectRatio = cal.pixelHeight/cal.pixelWidth;
			unit = cal.getUnit();
			scale = IJ.d2s(measured,digits)+" pixels/"+unit;
		}
		Roi roi = imp.getRoi();
		if (roi!=null && (roi instanceof Line)) {
			measured = ((Line)roi).getRawLength();
			known = 0.0;
		}
		
		SetScaleDialog gd = new SetScaleDialog("Set Scale", scale);
		gd.addNumericField("Distance in Pixels:", measured, digits);
		gd.addNumericField("Known Distance:", known, 2);
		gd.addNumericField("Pixel Aspect Ratio:", aspectRatio, 1);
		gd.addStringField("Unit of Length:", unit);
		gd.addMessage("Scale: "+"12345.789 pixels per centimeter");
		gd.addCheckbox("Global", Calibrator.global);
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		measured = gd.getNextNumber();
		known = gd.getNextNumber();
		aspectRatio = gd.getNextNumber();
		unit = gd.getNextString();
        if (unit.equals("um"))
            unit = "�m";
        else if (unit.equals("A"))
        	unit = "�";
 		Calibrator.global = gd.getNextBoolean();
		if (measured!=0.0 && known==0.0) {
			imp.setGlobalCalibration(Calibrator.global?cal:null);
			return;
		}
		if (measured<=0.0 || unit.startsWith("pixel") || unit.startsWith("Pixel") || unit.equals("")) {
			cal.pixelWidth = 1.0;
			cal.pixelHeight = 1.0;
			cal.setUnit("pixel");
		} else {
			cal.pixelWidth = known/measured;
			if (aspectRatio!=0.0)
				cal.pixelHeight = cal.pixelWidth*aspectRatio;
			else
				cal.pixelHeight = cal.pixelWidth;
			cal.setUnit(unit);
		}
		if (oldGlobal&&!Calibrator.global)
			imp.setGlobalCalibration(null);
		else {
			imp.setCalibration(cal);
			imp.setGlobalCalibration(Calibrator.global?cal:null);
		}
		if (Calibrator.global || Calibrator.global!=oldGlobal) {
			int[] list = WindowManager.getIDList();
			if (list==null)
				return;
			for (int i=0; i<list.length; i++) {
				ImagePlus imp2 = WindowManager.getImage(list[i]);
				if (imp2!=null)
					imp2.getWindow().repaint();
			}
		} else
			imp.getWindow().repaint();
	}

}

class SetScaleDialog extends GenericDialog {
	static final String NO_SCALE = "<no scale>";
	String initialScale;

	public SetScaleDialog(String title, String scale) {
		super(title);
		initialScale = scale;
	}

    protected void setup() {
    	if (IJ.isJava2())
    		initialScale += "          ";
   		setScale(initialScale);
    }
 	
 	public void textValueChanged(TextEvent e) {
 		Double d = getValue(((TextField)numberField.elementAt(0)).getText());
 		if (d==null)
 			{setScale(NO_SCALE); return;}
 		double measured = d.doubleValue();
 		d = getValue(((TextField)numberField.elementAt(1)).getText());
 		if (d==null)
 			{setScale(NO_SCALE); return;}
 		double known = d.doubleValue();
 		String theScale;
 		String unit = ((TextField)stringField.elementAt(0)).getText();
 		boolean noScale = measured<=0||known<=0||unit.startsWith("pixel")||unit.startsWith("Pixel")||unit.equals("");
 		if (noScale)
 			theScale = NO_SCALE;
 		else {
 			double scale = measured/known;
			int digits = 2;
			if ((int)scale==scale)
				digits = 0;
			else if (scale<0.01)
				digits = 3;
			else if (scale<0.001)
				digits = 4;
 			theScale = IJ.d2s(measured/known,digits)+" pixels/"+unit;
 		}
 		setScale(theScale);
	}
	
	void setScale(String theScale) {
 		((Label)theLabel).setText("Scale: "+theScale);
	}

}