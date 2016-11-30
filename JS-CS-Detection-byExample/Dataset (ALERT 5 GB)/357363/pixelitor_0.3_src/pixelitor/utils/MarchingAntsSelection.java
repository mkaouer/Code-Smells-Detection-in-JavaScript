/*
 * Copyright 2009 László Balázs-Csíki
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor.  If not, see <http://www.gnu.org/licenses/>.
 */

package pixelitor.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class MarchingAntsSelection {
	private float dashPhase;
	private Component c;
	private int x = 0;
	private int y = 0;
	private int width = 0;
	private int height = 0;
	private Timer tmr;

	public MarchingAntsSelection(Component c, Point start, Point end) {
		super();
		this.c = c;

		updateSelection(start, end);
	}

	public final void updateSelection(Point start, Point end) {
		this.x = Math.min(end.x, start.x);
		this.y = Math.min(end.y, start.y);
		int endX = Math.max(end.x, start.x);
		int endY = Math.max(end.y, start.y);

		this.width = endX - x;
		this.height = endY - y;
	}

	public void startMarching() {
		tmr = new Timer(100, null);
		tmr.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent evt) {
				dashPhase++;

// TODO some optimalizations are possible here, but the floowing is too simplistic
//				c.repaint(x, y, width + 1, height + 1);

				c.repaint();
			}
		});
		tmr.start();
	}

	public void stopMarching() {
		tmr.stop();
		c = null;
	}

	public void paintTheAnts(Graphics2D g2) {
		g2.setPaint(Color.WHITE);
		Stroke stroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 0.0f, new float[] { 4, 2, 6, 2 },
				dashPhase);
		g2.setStroke(stroke);
		g2.drawRect(x, y, width, height);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}


}
