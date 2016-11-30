/*
 * Copyright 2010 László Balázs-Csíki
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
package pixelitor.operations.gui;

import pixelitor.utils.GUIUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

/**
 *
 */
public class AngleSelector extends JPanel  {
    boolean userChangedSpinner = true;

    public AngleSelector(final AngleParam angleParam) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        final AngleSelectorComponent asc = new AngleSelectorComponent(angleParam);
        add(asc);

        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 360, 1));
        add(spinner);
        angleParam.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                asc.repaint();
                userChangedSpinner = false;
                spinner.setValue(angleParam.getValueInDegrees());
                userChangedSpinner = true;
            }
        });
        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(userChangedSpinner) {
                    int value = (Integer) spinner.getValue();
                    angleParam.setValueInDegrees(value, true);
                }
            }
        });

        setBorder(BorderFactory.createTitledBorder(angleParam.getName()));
    }

    public static void main(String[] args) {
        GUIUtils.testJComponent(new AngleSelector(new AngleParam("Name", 0f)));
    }
}

class AngleSelectorComponent extends JComponent implements MouseListener, MouseMotionListener {
    private AngleParam model;

    public static final int SIZE = 50;

    private int cx = SIZE/2;
    private int cy = SIZE/2;

    AngleSelectorComponent(AngleParam angleParam) {
        this.model = angleParam;

        Dimension sizeDim = new Dimension(SIZE + 1, SIZE + 1);
        setSize(sizeDim);
        setMinimumSize(sizeDim);
        setPreferredSize(sizeDim);

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        Ellipse2D.Float ellipse = new Ellipse2D.Float(0, 0, SIZE, SIZE);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.draw(ellipse);

        double angle = model.getValueInRadians();

        int radius = SIZE/2;
        float endX = (float) (cx +  (radius * Math.cos(angle)));
        float endY = (float) (cy +  (radius * Math.sin(angle)));
        Line2D.Float line = new Line2D.Float(cx, cy, endX, endY);
        g2.draw(line);

        double backAngle1 = 2.8797926 + angle;
        double backAngle2 = 3.4033926 + angle;
        int arrowRadius = 10;

        float arrowEnd1X = (float) (endX + (arrowRadius * Math.cos(backAngle1)));
        float arrowEnd1Y = (float) (endY + (arrowRadius * Math.sin(backAngle1)));
        float arrowEnd2X = (float) (endX + (arrowRadius * Math.cos(backAngle2)));
        float arrowEnd2Y = (float) (endY + (arrowRadius * Math.sin(backAngle2)));

        Line2D.Float line1 = new Line2D.Float(endX, endY, arrowEnd1X, arrowEnd1Y);
        Line2D.Float line2 = new Line2D.Float(endX, endY, arrowEnd2X, arrowEnd2Y);

        g2.draw(line1);
        g2.draw(line2);
    }

    private void updateAngle(int x, int y, boolean trigger) {
        int mouseX = x;
        int mouseY = y;
        double angle = Math.atan2(y - cy, x - cx);
        repaint();
        model.setValueInRadians(angle, trigger);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        updateAngle(e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        updateAngle(e.getX(), e.getY(), false);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        updateAngle(e.getX(), e.getY(), true);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        updateAngle(e.getX(), e.getY(), false);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

}

