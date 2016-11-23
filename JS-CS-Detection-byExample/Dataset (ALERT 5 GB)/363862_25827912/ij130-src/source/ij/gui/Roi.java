package ij.gui;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.KeyEvent;
import ij.*;
import ij.process.*;
import ij.measure.*;
import ij.plugin.frame.Recorder;

/** A rectangular region of interest and superclass for the other ROI classes. */
public class Roi extends Object implements Cloneable {

	public static final int CONSTRUCTING=0, MOVING=1, RESIZING=2, NORMAL=3, MOVING_HANDLE=4; // States
	public static final int RECTANGLE=0, OVAL=1, POLYGON=2, FREEROI=3, TRACED_ROI=4, LINE=5, POLYLINE=6, FREELINE=7, ANGLE=8; // Types
	public static final int HANDLE_SIZE = 5; 
	public static final int NOT_PASTING = -1; 
	
	int startX, startY, x, y, width, height;
	int activeHandle;
	int state;
	
	public static Roi previousRoi = null;
	protected static Color ROIColor = Prefs.getColor(Prefs.ROICOLOR,Color.yellow);
	protected static int pasteMode = Blitter.COPY;
	
	protected int type;
	protected int xMax, yMax;
	protected ImagePlus imp;
	protected ImageCanvas ic;
	protected int oldX, oldY, oldWidth, oldHeight;
	protected int clipX, clipY, clipWidth, clipHeight;
	protected ImagePlus clipboard = null;
	protected boolean constrain = false;
	protected boolean updateFullWindow;
	protected double mag = 1.0;

	/** Creates a new rectangular Roi. */
	public Roi(int x, int y, int width, int height) {
		setImage(null);
		if (width>xMax) width = xMax;
		if (height>yMax) height = yMax;
		//setLocation(x, y);
		this.x = x;
		this.y = y;
		startX = x; startY = y;
		oldX = x; oldY = y; oldWidth=0; oldHeight=0;
		this.width = width;
		this.height = height;
		oldWidth=width;
		oldHeight=height;
		clipX = x;
		clipY = y;
		clipWidth = width;
		clipHeight = height;
		state = NORMAL;
		type = RECTANGLE;
		if (ic!=null) {
			Graphics g = ic.getGraphics();
			draw(g);
			g.dispose();
		}
	}
	
	/** Starts the process of creating a user-defined rectangular Roi. */
	public Roi(int x, int y, ImagePlus imp) {
		setImage(imp);
		setLocation(x, y);
		width = 0;
		height = 0;
		state = CONSTRUCTING;
		type = RECTANGLE;
	}
	
	/** Obsolete */
	public Roi(int x, int y, int width, int height, ImagePlus imp) {
		this(x, y, width, height);
		setImage(imp);
	}

	public void setLocation(int x, int y) {
		if (x<0) x = 0;
		if (y<0) y = 0;
		if ((x+width)>xMax) x = xMax-width;
		if ((y+height)>yMax) y = yMax-height;
		//IJ.write(imp.getTitle() + ": Roi.setlocation(" + x + "," + y + ")");
		this.x = x;
		this.y = y;
		startX = x; startY = y;
		oldX = x; oldY = y; oldWidth=0; oldHeight=0;
	}
	
	public void setImage(ImagePlus imp) {
		this.imp = imp;
		if (imp==null) {
			ic = null;
			xMax = 99999;
			yMax = 99999;
		} else {
			ImageWindow win = imp.getWindow();
			if (win!=null)
				ic = win.getCanvas();
			xMax = imp.getWidth();
			yMax = imp.getHeight();
		}
	}
	
	public int getType() {
		return type;
	}
	
	public int getState() {
		return state;
	}
	
	/** Returns the perimeter length. */
	public double getLength() {
		double pw=1.0, ph=1.0;
		if (imp!=null) {
			Calibration cal = imp.getCalibration();
			pw = cal.pixelWidth;
			ph = cal.pixelHeight;
		}
		return 2.0*width*pw+2.0*height*ph;
	}
	
	/** Returns Feret's diameter, the greatest distance between 
		any two points along the ROI boundary. */
	public double getFeretsDiameter() {
		double pw=1.0, ph=1.0;
		if (imp!=null) {
			Calibration cal = imp.getCalibration();
			pw = cal.pixelWidth;
			ph = cal.pixelHeight;
		}
		return Math.sqrt(width*width*pw*pw+height*height*ph*ph);
	}

	public Rectangle getBoundingRect() {
		return new Rectangle(x, y, width, height);
	}
	
	/** Returns a copy of this roi. See Thinking is Java by Bruce Eckel
		(www.eckelobjects.com) for a good description of object cloning. */
	public synchronized Object clone() {
		try { 
			Roi r = (Roi)super.clone();
			r.previousRoi = null;
			r.imp = null;
			return r;
		}
		catch (CloneNotSupportedException e) {return null;}
	}
	
	protected void grow(int xNew, int yNew) {
		if (clipboard!=null)
			return;
		if (xNew < 0) xNew = 0;
		if (yNew < 0) yNew = 0;
		if (constrain) {
			// constrain selection to be square
			int dx, dy, d;
			dx = xNew - x;
			dy = yNew - y;
			if (dx<dy)
				d = dx;
			else
				d = dy;
			xNew = x + d;
			yNew = y + d;
		}
		width = Math.abs(xNew - startX);
		height = Math.abs(yNew - startY);
		x = (xNew>=startX)?startX:startX - width;
		y = (yNew>=startY)?startY:startY - height;
		if ((x+width) > xMax)
			width = xMax-x;
		if ((y+height) > yMax)
			height = yMax-y;
		updateClipRect();
		imp.draw(clipX, clipY, clipWidth, clipHeight);
		oldX = x;
		oldY = y;
		oldWidth = width;
		oldHeight = height;
	}

	protected void moveHandle(int ox, int oy) {
		if (clipboard!=null)
			return;
		if (ox<0) ox=0; if (oy<0) oy=0;
		if (ox>xMax) ox=xMax; if (oy>yMax) oy=yMax;
		//IJ.log("moveHandle: "+activeHandle+" "+ox+" "+oy);
		int x1=x, y1=y, x2=x1+width, y2=y+height;
		switch (activeHandle) {
			case 0: x=ox; y=oy; break;
			case 1: y=oy; break;
			case 2: x2=ox; y=oy; break;
			case 3: x2=ox; break;			
			case 4: x2=ox; y2=oy; break;
			case 5: y2=oy; break;
			case 6: x=ox; y2=oy; break;
			case 7: x=ox; break;
		}
		if (x<x2)
		   width=x2-x;
		else
		  {width=1; x=x2;}
		if (y<y2)
		   height = y2-y;
		else
		   {height=1; y=y2;}
		if (constrain)
			height = width;
		updateClipRect();
		imp.draw(clipX, clipY, clipWidth, clipHeight);
		oldX=x; oldY=y;
		oldWidth=width; oldHeight=height;
	}

	void move(int xNew, int yNew) {
		x += xNew - startX;
		y += yNew - startY;
		if (type!=RECTANGLE || clipboard==null) {
			if (x<0) x=0; if (y<0) y=0;
			if ((x+width)>xMax) x = xMax-width;
			if ((y+height)>yMax) y = yMax-height;
		}
		startX = xNew;
		startY = yNew;
		updateClipRect();
		imp.draw(clipX, clipY, clipWidth, clipHeight);
		oldX = x;
		oldY = y;
		oldWidth = width;
		oldHeight=height;
	}

	/** Nudge ROI one pixel on arrow key press. */
	public void nudge(int key) {
		switch(key) {
			case KeyEvent.VK_UP:
				y--;
				if (y<0 && (type!=RECTANGLE||clipboard==null))
					y = 0;
				break;
			case KeyEvent.VK_DOWN:
				y++;
				if ((y+height)>=yMax && (type!=RECTANGLE||clipboard==null))
					y = yMax-height;
				break;
			case KeyEvent.VK_LEFT:
				x--;
				if (x<0 && (type!=RECTANGLE||clipboard==null))
					x = 0;
				break;
			case KeyEvent.VK_RIGHT:
				x++;
				if ((x+width)>=xMax && (type!=RECTANGLE||clipboard==null))
					x = xMax-width;
				break;
		}
		updateClipRect();
		imp.draw(clipX, clipY, clipWidth, clipHeight);
		oldX = x; oldY = y;
	}
	
	/** Nudge lower right corner of rectangular and oval ROIs by
		one pixel based on arrow key press. */
	public void nudgeCorner(int key) {
		if (type>OVAL || clipboard!=null)
			return;
		switch(key) {
			case KeyEvent.VK_UP:
				height--;
				if (height<1) height = 1;
				break;
			case KeyEvent.VK_DOWN:
				height++;
				if ((y+height) > yMax) height = yMax-y;
				break;
			case KeyEvent.VK_LEFT:
				width--;
				if (width<1) width = 1;
				break;
			case KeyEvent.VK_RIGHT:
				width++;
				if ((x+width) > xMax) width = xMax-x;
				break;
		}
		updateClipRect();
		imp.draw(clipX, clipY, clipWidth, clipHeight);
		oldX = x; oldY = y;
	}
	
	protected void updateClipRect() {
	// Finds the union of current and previous roi
		clipX = (x<=oldX)?x:oldX;
		clipY = (y<=oldY)?y:oldY;
		clipWidth = ((x+width>=oldX+oldWidth)?x+width:oldX+oldWidth) - clipX + 1;
		clipHeight = ((y+height>=oldY+oldHeight)?y+height:oldY+oldHeight) - clipY + 1;
		int m = 3;
		if (ic!=null) {
			double mag = ic.getMagnification();
			if (mag<1.0)
				m = (int)(3/mag);
		}
		clipX-=m; clipY-=m;
		clipWidth+=m*2; clipHeight+=m*2;
	 }
		
	protected void handleMouseDrag(int sx, int sy, boolean constrain) {
		if (ic==null) return;
		this.constrain = constrain;
		int ox = ic.offScreenX(sx);
		int oy = ic.offScreenY(sy);
		switch(state) {
			case CONSTRUCTING:
				grow(ox, oy);
				break;
			case MOVING:
				move(ox, oy);
				break;
			case MOVING_HANDLE:
				moveHandle(ox, oy);
				break;
			default:
				break;
		}
	}

	int getHandleSize() {
		double mag = ic!=null?ic.getMagnification():1.0;
		double size = HANDLE_SIZE/mag;
		return (int)(size*mag);
	}
	
	public void draw(Graphics g) {
		if (ic==null) return;
		g.setColor(ROIColor);
		mag = ic.getMagnification();
		int sw = (int)(width*mag);
		int sh = (int)(height*mag);
		int sx1 = ic.screenX(x);
		int sy1 = ic.screenY(y);
		int sx2 = sx1+sw/2;
		int sy2 = sy1+sh/2;
		int sx3 = sx1+sw;
		int sy3 = sy1+sh;
		g.drawRect(sx1, sy1, sw, sh);
		if (state!=CONSTRUCTING && clipboard==null) {
			int size2 = HANDLE_SIZE/2;
			drawHandle(g, sx1-size2, sy1-size2);
			drawHandle(g, sx2-size2, sy1-size2);
			drawHandle(g, sx3-size2, sy1-size2);
			drawHandle(g, sx3-size2, sy2-size2);
			drawHandle(g, sx3-size2, sy3-size2);
			drawHandle(g, sx2-size2, sy3-size2);
			drawHandle(g, sx1-size2, sy3-size2);
			drawHandle(g, sx1-size2, sy2-size2);
		}
		showStatus();
		if (updateFullWindow)
			{updateFullWindow = false; imp.draw();}
	}

	void drawHandle(Graphics g, int x, int y) {
		double size = (width*height)*mag;
		if (type==LINE) {
			size = Math.sqrt(width*width+height*height);
			size *= size*mag;
		}
		if (size>6000.0) {
			g.setColor(Color.black);
			g.fillRect(x,y,5,5);
			g.setColor(Color.white);
			g.fillRect(x+1,y+1,3,3);
		} else if (size>1500.0) {
			g.setColor(Color.black);
			g.fillRect(x+1,y+1,4,4);
			g.setColor(Color.white);
			g.fillRect(x+2,y+2,2,2);
		} else {			
			g.setColor(Color.black);
			g.fillRect(x+1,y+1,3,3);
			g.setColor(Color.white);
			g.fillRect(x+2,y+2,1,1);
		}
	}

	public void drawPixels() {
		endPaste();
		imp.getProcessor().drawRect(x, y, width, height);
		if (Line.getWidth()>1)
			updateFullWindow = true;
	}

	public boolean contains(int x, int y) {
		Rectangle r = new Rectangle(this.x, this.y, width, height);
		return r.contains(x, y);
	}
		
	/** Returns a handle number if the specified screen coordinates are  
		inside or near a handle, otherwise returns -1. */
	int isHandle(int sx, int sy) {
		if (clipboard!=null) return -1;
		double mag = ic.getMagnification();
		int size = HANDLE_SIZE+3;
		int halfSize = size/2;
		int sx1 = ic.screenX(x) - halfSize;
		int sy1 = ic.screenY(y) - halfSize;
		int sx3 = ic.screenX(x+width) - halfSize;
		int sy3 = ic.screenY(y+height) - halfSize;
		int sx2 = sx1 + (sx3 - sx1)/2;
		int sy2 = sy1 + (sy3 - sy1)/2;
		if (sx>=sx1&&sx<=sx1+size&&sy>=sy1&&sy<=sy1+size) return 0;
		if (sx>=sx2&&sx<=sx2+size&&sy>=sy1&&sy<=sy1+size) return 1;
		if (sx>=sx3&&sx<=sx3+size&&sy>=sy1&&sy<=sy1+size) return 2;
		if (sx>=sx3&&sx<=sx3+size&&sy>=sy2&&sy<=sy2+size) return 3;
		if (sx>=sx3&&sx<=sx3+size&&sy>=sy3&&sy<=sy3+size) return 4;
		if (sx>=sx2&&sx<=sx2+size&&sy>=sy3&&sy<=sy3+size) return 5;
		if (sx>=sx1&&sx<=sx1+size&&sy>=sy3&&sy<=sy3+size) return 6;
		if (sx>=sx1&&sx<=sx1+size&&sy>=sy2&&sy<=sy2+size) return 7;
		return -1;
	}
	
	protected void mouseDownInHandle(int handle, int sx, int sy) {
		state = MOVING_HANDLE;
		activeHandle = handle;
	}

	protected void handleMouseDown(int sx, int sy) {
		if (state==NORMAL) {
			state = MOVING;
			startX = ic.offScreenX(sx);
			startY = ic.offScreenY(sy);
			showStatus();
		}
	}
		
	protected void handleMouseUp(int screenX, int screenY) {
		state = NORMAL;
		imp.draw(clipX-5, clipY-5, clipWidth+10, clipHeight+10);
		if (Recorder.record) {
			String method;
			if (type==LINE) {
				if (imp==null) return;
				Line line = (Line)imp.getRoi();
				Recorder.record("makeLine", line.x1, line.y1, line.x2, line.y2);
			} else if (type==OVAL)
				Recorder.record("makeOval", x, y, width, height);
			else
				Recorder.record("makeRectangle", x, y, width, height);
		}
	}

	protected void showStatus() {
		String value;
		if (state!=CONSTRUCTING && type==RECTANGLE && width<=25 && height<=25) {
			ImageProcessor ip = imp.getProcessor();
			double v = ip.getPixelValue(x,y);
			int digits = (imp.getType()==ImagePlus.GRAY8||imp.getType()==ImagePlus.GRAY16)?0:2;
			value = ", value="+IJ.d2s(v,digits);
		} else
			value = "";
		Calibration cal = imp.getCalibration();
		String size;
		if (cal.scaled())
			size = ", w="+IJ.d2s(width*cal.pixelWidth)+" ("+width+"), h="
			+IJ.d2s(height*cal.pixelHeight)+" ("+height+")";
		else
			size = ", width="+width+", height="+height;
		IJ.showStatus(imp.getLocationAsString(x,y)+size+value);
	}
	
	
	public int[] getMask() {
		return null;
	}
	
	void startPaste(ImagePlus clipboard) {
		IJ.showStatus("Pasting...");
		this.clipboard = clipboard;
		imp.getProcessor().snapshot();
		updateClipRect(); //v1.24e
		if (IJ.debugMode) IJ.log("startPaste: "+clipX+" "+clipY+" "+clipWidth+" "+clipHeight);
		imp.draw(clipX, clipY, clipWidth, clipHeight);
	}
	
	void updatePaste() {
		if (clipboard!=null) {
			ImageProcessor ip = imp.getProcessor();
			ip.reset();
			ip.copyBits(clipboard.getProcessor(), x, y, pasteMode);
			if (type!=RECTANGLE) //v1.24e
				ip.reset(imp.getMask());
			ic.setImageUpdated();
		}
	}

	public void endPaste() {
		if (clipboard!=null) {
			ImageProcessor ip = imp.getProcessor();
			if (pasteMode!=Blitter.COPY) ip.reset();
			ip.copyBits(clipboard.getProcessor(), x, y, pasteMode);
			if (type!=RECTANGLE) //v1.24e
				ip.reset(imp.getMask());
			ip.snapshot();
			clipboard = null;
			imp.updateAndDraw();
			Undo.setup(Undo.FILTER, imp);
		}
	}
	
	public void abortPaste() {
		clipboard = null;
		imp.getProcessor().reset();
		imp.updateAndDraw();
	}

	/** Returns the angle in degrees between the specified line and a horizontal line. */
	public double getAngle(int x1, int y1, int x2, int y2) {
		final int q1=0, q2orq3=2, q4=3; //quadrant
		int quadrant;
		double dx = x2-x1;
		double dy = y1-y2;
		double angle;

		if (imp!=null) {
			Calibration cal = imp.getCalibration();
			dx *= cal.pixelWidth;
			dy *= cal.pixelHeight;
		}
		
		if (dx!=0.0)
			angle = Math.atan(dy/dx);
		else {
			if (dy>=0.0)
				angle = Math.PI/2.0;
			else
				angle = -Math.PI/2.0;
		}
		angle = (180.0/Math.PI)*angle;
		if (dx>=0.0 && dy>=0.0)
			quadrant = q1;
		else if (dx<0.0)
			quadrant = q2orq3;
		else
			quadrant = q4;
		switch (quadrant) {
			case q1: 
				break;
			case q2orq3: 
				angle = angle+180.0;
				break;
			case q4: 
				angle = angle+360.0;
		}
		return angle;
	}
	
	/** Returns the color used for drawing ROI outlines. */
	public static Color getColor() {
		return ROIColor;
	}

	/** Sets the color used for ROI outline to the specified value. */
	public static void setColor(Color c) {
		ROIColor = c;
	}
	
	/** Sets the Paste transfer mode.
		@see ij.process.Blitter
	*/
	public static void setPasteMode(int transferMode) {
		int previousMode = pasteMode;
		pasteMode = transferMode;
		ImagePlus imp = WindowManager.getCurrentImage();
		if (imp==null)
			return;
		if (previousMode!=Blitter.COPY) {
			ImageProcessor ip = imp.getProcessor();
			ip.reset();
			if (pasteMode!=Blitter.COPY) {
				//ip.copyBits(clipboard.getProcessor(), x, y, pasteMode);
				//ic.setImageUpdated();
			}
		}
		imp.updateAndDraw();
	}
	
	/** Returns the current paste transfer mode, or NOT_PASTING (-1)
		if no paste operation is in progress.
		@see ij.process.Blitter
	*/
	public int getPasteMode() {
		if (clipboard==null)
			return NOT_PASTING;
		else
			return pasteMode;
	}

	public String toString() {
		return ("Roi: type="+type+", x="+x+", y="+y+", width="+width+", height="+height);
	}

}
