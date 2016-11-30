/*
 * Copyright 2009-2010 László Balázs-Csíki
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
package pixelitor.tools;

import org.jdesktop.swingx.combobox.EnumComboBoxModel;
import pixelitor.FgBgColorSelector;
import pixelitor.ImageComponent;
import pixelitor.PixelitorWindow;
import pixelitor.operations.gui.RangeParam;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.SliderSpinner;

import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 *
 */
public class ShapesTool extends Tool {
    private EnumComboBoxModel<ShapeType> typeModel = new EnumComboBoxModel<ShapeType>(ShapeType.class);

    private JCheckBox unicodeCB = new JCheckBox();
    private JCheckBox fillCB = new JCheckBox();
    private JCheckBox bgStrokeCB = new JCheckBox();
    private JCheckBox dashedCB = new JCheckBox();
    private JCheckBox gradientFillCB = new JCheckBox();
    private RangeParam strokeWidthParam = new RangeParam("Stroke Width", 1, 100, 5);

    private EnumComboBoxModel<BasicStrokeCap> strokeCap = new EnumComboBoxModel<BasicStrokeCap>(BasicStrokeCap.class);
    private EnumComboBoxModel<BasicStrokeJoin> strokeJoin = new EnumComboBoxModel<BasicStrokeJoin>(BasicStrokeJoin.class);
    private boolean drawing = false;
    private String unicodeString = "\u263A";
    private JLabel unicodeLabel = new JLabel(unicodeString);

    enum BasicStrokeJoin {
        ROUND(BasicStroke.JOIN_ROUND) {
            @Override
            public String toString() {
                return "Round";
            }
        }, BEVEL(BasicStroke.JOIN_BEVEL) {
            @Override
            public String toString() {
                return "Bevel";
            }
        }, MITER(BasicStroke.JOIN_MITER) {
            @Override
            public String toString() {
                return "Miter";
            }
        };
        private int value;

        private BasicStrokeJoin(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    enum BasicStrokeCap {
        ROUND(BasicStroke.CAP_ROUND) {
            @Override
            public String toString() {
                return "Round";
            }
        }, BUTT(BasicStroke.CAP_BUTT) {
            @Override
            public String toString() {
                return "Butt";
            }
        }, SQUARE(BasicStroke.CAP_SQUARE) {
            @Override
            public String toString() {
                return "Square";
            }
        };
        private int value;

        private BasicStrokeCap(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public ShapesTool() {
        super('s', "Shapes", "shapes_tool_icon.gif");
    }

    @Override
    public void mouseDragged(MouseEvent e, ImageComponent ic) {
        super.mouseDragged(e, ic);
        drawing = true;
        ic.repaint(); // TODO optimize
    }

    @Override
    public void mouseReleased(MouseEvent e, ImageComponent ic) {
        super.mouseReleased(e, ic);
        saveImageForUndo(ic);

        paintShapeOnIC(ic, start, end);

        drawing = false;
        ic.imageChanged(true);
    }

    @Override
    void initSettingsPanel(JPanel p) {
        p.add(new JLabel("Shape:"));
        final JComboBox shapeTypeCB = new JComboBox(typeModel);
        p.add(shapeTypeCB);

        p.add(new JLabel("Fill:"));
        p.add(fillCB);
        fillCB.setSelected(true);

        p.add(new JLabel("Gradient Fill:"));
        p.add(gradientFillCB);
        gradientFillCB.setSelected(true);

        p.add(new JLabel("Stroke with BG:"));
        p.add(bgStrokeCB);

        SliderSpinner strokeWidthSlider = new SliderSpinner(strokeWidthParam, false, SliderSpinner.TextPosition.WEST);
        p.add(strokeWidthSlider);

        p.add(new JLabel("Dashed:"));
        p.add(dashedCB);


        // general setup
        unicodeLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 32));
        unicodeLabel.setToolTipText("The currently selected Unicode symbol");

        unicodeCB.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                shapeTypeCB.setEnabled(!unicodeCB.isSelected());
            }
        });
        unicodeCB.setToolTipText("Check this to use Unicode symbols instead of the shapes");

        JButton advancedButton = new JButton("Advanced...");
        p.add(advancedButton);
        advancedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel p = new JPanel();
                p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

                JPanel unicodePanel = new JPanel();
                unicodePanel.setBorder(BorderFactory.createTitledBorder("Unicode Symbols"));
                unicodePanel.setLayout(new GridLayout(2, 2, 5, 5));

                unicodePanel.add(new JLabel("Use Symbols:", JLabel.RIGHT));
                unicodePanel.add(unicodeCB);
                unicodePanel.add(new JLabel("Current Symbol:", JLabel.RIGHT));
                unicodePanel.add(unicodeLabel);

                p.add(unicodePanel);

                JPanel capJoinPanel = new JPanel();
                capJoinPanel.setBorder(BorderFactory.createTitledBorder("Line Endpoints"));
                capJoinPanel.setLayout(new GridLayout(2, 2, 5, 5));

                capJoinPanel.add(new JLabel("Endpoint Cap:", JLabel.RIGHT));
                JComboBox capCB = new JComboBox(strokeCap);
                capCB.setToolTipText("The shape of the endpoints of the lines");
                capJoinPanel.add(capCB);

                capJoinPanel.add(new JLabel("Corner Join:", JLabel.RIGHT));
                JComboBox joinCB = new JComboBox(strokeJoin);
                joinCB.setToolTipText("The way lines connect at the corners");
                capJoinPanel.add(joinCB);

                p.add(capJoinPanel);

                final JDialog d = new JDialog(PixelitorWindow.getInstance(), "Advanced Options");
                JMenuBar menuBar = new JMenuBar();
                d.setJMenuBar(menuBar);
                JMenu sb = new JMenu("Select Unicode Symbol");
                menuBar.add(sb);
                sb.add(new SymbolMenuItem("Smiling Face", "\u263A", ShapesTool.this));
                sb.add(new SymbolMenuItem("Flower", "\u2740", ShapesTool.this));
                sb.add(new SymbolMenuItem("Sun", "\u263C", ShapesTool.this));
                sb.add(new SymbolMenuItem("Pencil", "\u270E", ShapesTool.this));
                sb.add(new SymbolMenuItem("Envelope", "\u2709", ShapesTool.this));
                sb.add(new SymbolMenuItem("Scissors", "\u2702", ShapesTool.this));
                sb.add(new SymbolMenuItem("Airplane", "\u2708", ShapesTool.this));
                sb.add(new SymbolMenuItem("Check Mark", "\u2714", ShapesTool.this));
                sb.addSeparator();
                sb.add(new SymbolMenuItem("Eighth note", "\u266A", ShapesTool.this));
                sb.add(new SymbolMenuItem("Beamed Eighth Notes", "\u266B", ShapesTool.this));
                sb.addSeparator();
                sb.add(new SymbolMenuItem("Snowflake", "\u2744", ShapesTool.this));
                sb.add(new SymbolMenuItem("Snowflake 2", "\u2745", ShapesTool.this));
                sb.add(new SymbolMenuItem("Snowflake 3", "\u2746", ShapesTool.this));
                sb.addSeparator();
                sb.add(new SymbolMenuItem("Arrow", "\u27AB", ShapesTool.this));
                sb.add(new SymbolMenuItem("Arrow 2", "\u27B8", ShapesTool.this));
                sb.add(new SymbolMenuItem("Arrow 3", "\u279C", ShapesTool.this));


                d.setLayout(new BorderLayout());
                d.add(p, BorderLayout.CENTER);
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        d.dispose();
                    }
                });
                JPanel southPanel = new JPanel();
                southPanel.add(okButton);
                d.add(southPanel, BorderLayout.SOUTH);
                d.pack();
                GUIUtils.centerOnScreen(d);
                d.setVisible(true);
            }
        });
    }

    @Override
    public void paintOnImage(Graphics2D g) {
        paintShape(g, start, end);
    }

    /**
     * Paint a shape on the given ImageComponent. Can be used programmatically.
     */
    public void paintShapeOnIC(ImageComponent ic, Point startPoint, Point endPoint) {
        BufferedImage bi = ic.getActiveLayer().getBufferedImage();
        Graphics2D g2 = bi.createGraphics();
        drawing = true;
        paintShape(g2, startPoint, endPoint);
        g2.dispose();
    }

    private void paintShape(Graphics2D g, Point startPoint, Point endPoint) {
        Shape currentShape;
        AffineTransform saveXform = g.getTransform();
        if (drawing) {
            if (unicodeCB.isSelected()) {
                int x = startPoint.x;
                int y = startPoint.y;
//                int width = end.x - start.x;
                int height = endPoint.y - startPoint.y;

                Font f = new Font(Font.SANS_SERIF, Font.PLAIN, height);
                FontMetrics metrics = g.getFontMetrics(f);
//                int charWidth = metrics.stringWidth(unicodeString);
                int charHeight = metrics.getHeight();


                AffineTransform at = new AffineTransform();

//                at.scale(((double)width) /charWidth, ((double)height) /charHeight);
                at.translate(0, charHeight / 2);
                g.transform(at);


                GlyphVector glyphVector = f.createGlyphVector(g.getFontRenderContext(), unicodeString);

                currentShape = glyphVector.getOutline(x, y);

            } else {
                currentShape = typeModel.getSelectedItem().getShape(startPoint, endPoint);
            }

            Color fgColor = FgBgColorSelector.getFG();

            if (gradientFillCB.isSelected()) {
                Color bgColor = FgBgColorSelector.getBG();
                g.setPaint(new GradientPaint(startPoint.x, startPoint.y, fgColor, endPoint.x, endPoint.y, bgColor));
            } else {
                g.setColor(fgColor);
            }

            int strokeWidth = strokeWidthParam.getValue();

            float[] dashFloats = null;
            if (dashedCB.isSelected()) {
                dashFloats = new float[]{2 * strokeWidth, 2 * strokeWidth};
            }

            Stroke stroke = new BasicStroke(strokeWidth,
                    strokeCap.getSelectedItem().getValue(),
                    strokeJoin.getSelectedItem().getValue(),
                    1.5f,
                    dashFloats,
                    0.0f
            );
            g.setStroke(stroke);

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (fillCB.isSelected()) {
                g.fill(currentShape);
                if (bgStrokeCB.isSelected()) {
                    g.setColor(FgBgColorSelector.getBG());
                    g.draw(currentShape);
                }
            } else {
                if (bgStrokeCB.isSelected()) {
                    g.setColor(FgBgColorSelector.getBG());
                }
                g.draw(currentShape);
            }
            g.setTransform(saveXform);
        }
    }

    public void setUnicodeString(String unicodeString) {
        this.unicodeString = unicodeString;
        unicodeLabel.setText(unicodeString);
    }

    /**
     * Used for testing
     */
    public void setShapeType(ShapeType newType) {
        typeModel.setSelectedItem(newType);
    }

}

class SymbolMenuItem extends JMenuItem {
    private String descr;
    private String symbolString;
    private ShapesTool shapesTool;

    SymbolMenuItem(String descr, final String symbolString, final ShapesTool shapesTool) {
        super(descr + " " + symbolString);
        this.descr = descr;
        this.symbolString = symbolString;
        this.shapesTool = shapesTool;

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shapesTool.setUnicodeString(symbolString);
            }
        });
    }

}