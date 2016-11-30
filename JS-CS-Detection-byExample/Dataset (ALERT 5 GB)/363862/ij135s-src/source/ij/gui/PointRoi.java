package ij.gui;

import java.awt.*;
import java.awt.image.*;
import ij.*;
import ij.process.*;
import ij.measure.*;
import ij.plugin.filter.Analyzer;
import java.awt.event.KeyEvent;

/** This class represents selection consisting of one or more points. */
public class PointRoi extends PolygonRoi {
	
	/** Creates a new PointRoi using the specified offscreen coordinates. */
	public PointRoi(int[] ox, int[] oy, int points) {
		super(ox, oy, points, POINT);
		width+=1; height+=1;
	}

	/** Creates a new PointRoi using the specified offscreen coordinates. */
	public PointRoi(int sx, int sy, ImagePlus imp) {
		super(makeXArray(sx, imp), makeYArray(sy, imp), 1, POINT);
		setImage(imp);
		width=1; height=1;
		imp.draw(x-5, y-5, width+10, height+10);
	}

	static int[] makeXArray(int value, ImagePlus imp) {
		int[] array = new int[1];
		array[0] = imp!=null?imp.getWindow().getCanvas().offScreenX(value):value;
		return array;
	}
				
	static int[] makeYArray(int value, ImagePlus imp) {
		int[] array = new int[1];
		array[0] = imp!=null?imp.getWindow().getCanvas().offScreenY(value):value;
		return array;
	}
				
	void handleMouseMove(int ox, int oy) {
		//IJ.log("handleMouseMove");
	}
	
	protected void handleMouseUp(int sx, int sy) {
		super.handleMouseUp(sx, sy);
		modifyRoi(); //adds this point to previous points if shift key down
	}
	
	/** Draws the points on the image. */
	public void draw(Graphics g) {
		//IJ.log("draw: " + nPoints+"  "+width+"  "+height);
		updatePolygon();
		if (ic!=null) mag = ic.getMagnification();
		int size2 = HANDLE_SIZE/2;
		for (int i=0; i<nPoints; i++)
			drawPoint(g, xp2[i]-size2, yp2[i]-size2);
		//showStatus();
		if (updateFullWindow)
			{updateFullWindow = false; imp.draw();}
	}

	void drawPoint(Graphics g, int x, int y) {
		g.setColor(ROIColor);
		g.fillRect(x+1,y+1,3,3);
		//g.drawLine(x-3, y+2, x+7, y+2);
		//g.drawLine(x+2, y-3, x+2, y+7);
		g.setColor(Color.black);
		g.drawRect(x, y, 4, 4);
	}

	public void drawPixels(ImageProcessor ip) {
		ip.setLineWidth(Analyzer.markWidth);
		for (int i=0; i<nPoints; i++) {
			ip.moveTo(x+xp[i], y+yp[i]);
			ip.lineTo(x+xp[i], y+yp[i]);
		}
	}
	
	/** Adds a point to this selection and return the result. */
	public PointRoi addPoint(int x, int y) {
		Polygon poly = getPolygon();
		poly.addPoint(x, y);
		PointRoi p = new PointRoi(poly.xpoints, poly.ypoints, poly.npoints);
		//IJ.log("addPoint: "+((PointRoi)p).getNCoordinates());
		return p;
	}

	/** Subtract the points that intersect the specified ROI and return 
		the result. Returns null if there are no resulting points. */
	public PointRoi subtractPoints(Roi roi) {
		Polygon points = getPolygon();
		Polygon poly = roi.getPolygon();
		Polygon points2 = new Polygon();
		for (int i=0; i<points.npoints; i++) {
			if (!poly.contains(points.xpoints[i], points.ypoints[i]))
				points2.addPoint(points.xpoints[i], points.ypoints[i]);
		}
		if (points2.npoints==0)
			return null;
		else
			return new PointRoi(points2.xpoints, points2.ypoints, points2.npoints);
	}

	public ImageProcessor getMask() {
		if (cachedMask!=null && cachedMask.getPixels()!=null)
			return cachedMask;
		ImageProcessor mask = new ByteProcessor(width, height);
		for (int i=0; i<nPoints; i++) {
			mask.putPixel(xp[i], yp[i], 255);
		}
		cachedMask = mask;
		return mask;
	}

}
