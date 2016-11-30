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
import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.ImageComponent;
import pixelitor.PixelitorWindow;
import pixelitor.filters.gui.RangeParam;
import pixelitor.history.History;
import pixelitor.history.NewSelectionEdit;
import pixelitor.history.PixelitorEdit;
import pixelitor.history.SelectionChangeEdit;
import pixelitor.layers.ImageLayer;
import pixelitor.selection.Selection;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.SliderSpinner;

import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * The Shapes Tool
 */
public class ShapesTool extends ForwardingTool {
    private EnumComboBoxModel<ShapesAction> actionModel = new EnumComboBoxModel<ShapesAction>(ShapesAction.class);
    private EnumComboBoxModel<ShapeType> typeModel = new EnumComboBoxModel<ShapeType>(ShapeType.class);
    private EnumComboBoxModel<TwoPointsFillType> fillModel = new EnumComboBoxModel<TwoPointsFillType>(TwoPointsFillType.class);
    private EnumComboBoxModel<TwoPointsFillType> strokeFillModel = new EnumComboBoxModel<TwoPointsFillType>(TwoPointsFillType.class);

    private RangeParam strokeWidthParam = new RangeParam("Stroke Width", 1, 100, 5);

    // controls in the Stroke Settings dialog
    private ButtonModel dashedModel = new JToggleButton.ToggleButtonModel();
    private EnumComboBoxModel<BasicStrokeCap> strokeCapModel = new EnumComboBoxModel<BasicStrokeCap>(BasicStrokeCap.class);
    private EnumComboBoxModel<BasicStrokeJoin> strokeJoinModel = new EnumComboBoxModel<BasicStrokeJoin>(BasicStrokeJoin.class);
    private EnumComboBoxModel<StrokeType> strokeTypeModel = new EnumComboBoxModel<StrokeType>(StrokeType.class);

    private JDialog strokeDialog;
    private boolean drawing = false;
    private JButton strokeSettingsButton;
    private BasicStroke basicStroke;

    private Shape backupSelectionShape = null;


    private JComboBox strokeFillCombo;
    private JComboBox fillCombo = new JComboBox(fillModel);

    public ShapesTool() {
        super('s', "Shapes", "shapes_tool_icon.gif", "click and drag to draw a shape", Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        strokeFillModel.setSelectedItem(TwoPointsFillType.BACKGROUND);
        strokeFillCombo = new JComboBox(strokeFillModel);
    }

    @Override
    void initSettingsPanel(ToolSettingsPanel p) {
        p.add(new JLabel("Shape:"));
        final JComboBox shapeTypeCB = new JComboBox(typeModel);
        p.add(shapeTypeCB);

        p.add(new JLabel("Action:"));
        JComboBox actionCombo = new JComboBox(actionModel);
        p.add(actionCombo);

        actionCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableSettings();
            }
        });

        p.add(new JLabel("Fill:"));
        p.add(fillCombo);

        p.add(new JLabel("Stroke:"));
        p.add(strokeFillCombo);

        strokeSettingsButton = new JButton("Stroke Settings...");
        p.add(strokeSettingsButton);
        strokeSettingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initAndShowStrokeSettingsDialog();
            }
        });

        enableSettings();
    }

    @Override
    public boolean mousePressed(MouseEvent e, ImageComponent ic) {
        if (super.mousePressed(e, ic)) {
            return true;
        }

        Composition comp = ic.getComp();
        Selection selection = comp.getSelection();
        if (selection != null) {
            backupSelectionShape = selection.getShape();
        } else {
            backupSelectionShape = null;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseEvent e, ImageComponent ic) {
        if (super.mouseDragged(e, ic)) {
            return true;
        }
        drawing = true;
        userDrag.setStartFromCenter(e.isAltDown());

        Composition comp = ic.getComp();

        comp.imageChanged(true, false); // TODO optimize, the whole image should not be repainted
        return false;
    }

    @Override
    public boolean mouseReleased(MouseEvent e, ImageComponent ic) {
        if (super.mouseReleased(e, ic)) {
            return true;
        }
        userDrag.setStartFromCenter(e.isAltDown());


        Composition comp = ic.getComp();

        ShapesAction action = actionModel.getSelectedItem();
        boolean selectionMode = action.createSelection();
        if (!selectionMode) {
//            saveImageForUndo(comp);

            int thickness = 0;
            if (action.enableStrokePaintSelection()) {
                thickness = strokeWidthParam.getValue();
            }

            AffectedArea affectedArea = new AffectedArea(comp, userDrag.getAffectedStrokedRectangle(thickness), false);
            saveSubImageForUndo(comp.getActiveImageLayer().getBufferedImage(), affectedArea);
        }

        paintShapeOnIC(comp, userDrag);

        if (selectionMode) {
            Selection selection = comp.getSelection();
            if (selection != null) {
                selection.clipToCompSize(comp); // the selection can be too big

                PixelitorEdit edit;
                if (backupSelectionShape != null) {
                    edit = new SelectionChangeEdit(comp, backupSelectionShape, "Selection Change");
                } else {
                    edit = new NewSelectionEdit(comp, selection.getShape());
                }
                History.addEdit(edit);
            }
        }

        drawing = false;
        comp.imageChanged(true, true);
        return false;
    }


    private void enableSettings() {
        ShapesAction action = actionModel.getSelectedItem();
        enableStrokeSettings(action.enableStrokeSettings());
        enableFillPaintSelection(action.enableFillPaintSelection());
        enableStrokePaintSelection(action.enableStrokePaintSelection());
    }

    private void initAndShowStrokeSettingsDialog() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        SliderSpinner strokeWidthSlider = new SliderSpinner(strokeWidthParam, false, SliderSpinner.TextPosition.BORDER);
        p.add(strokeWidthSlider);
        p.add(strokeWidthSlider);

        JPanel capJoinPanel = new JPanel();
        capJoinPanel.setBorder(BorderFactory.createTitledBorder("Line Endpoints"));
        capJoinPanel.setLayout(new GridLayout(2, 2, 5, 5));

        capJoinPanel.add(new JLabel("Endpoint Cap:", JLabel.RIGHT));
        JComboBox capCB = new JComboBox(strokeCapModel);
        capCB.setToolTipText("The shape of the endpoints of the lines");
        capJoinPanel.add(capCB);

        capJoinPanel.add(new JLabel("Corner Join:", JLabel.RIGHT));
        JComboBox joinCB = new JComboBox(strokeJoinModel);
        joinCB.setToolTipText("The way lines connect at the corners");
        capJoinPanel.add(joinCB);

        p.add(capJoinPanel);

        JPanel strokeTypePanel = new JPanel();
        strokeTypePanel.setBorder(BorderFactory.createTitledBorder("Stroke Type"));

        strokeTypePanel.setLayout(new GridLayout(2, 2, 5, 5));

        strokeTypePanel.add(new JLabel("Line Type:", JLabel.RIGHT));
        strokeTypePanel.add(new JComboBox(strokeTypeModel));

        strokeTypePanel.add(new JLabel("Dashed:", JLabel.RIGHT));


        JCheckBox dashedCB = new JCheckBox();
        dashedCB.setModel(dashedModel);
        strokeTypePanel.add(dashedCB);

        p.add(strokeTypePanel);


        strokeDialog = new JDialog(PixelitorWindow.getInstance(), "Stroke Settings");

        strokeDialog.setLayout(new BorderLayout());
        strokeDialog.add(p, BorderLayout.CENTER);
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeStrokeDialog();
            }
        });
        JPanel southPanel = new JPanel();
        southPanel.add(okButton);
        strokeDialog.add(southPanel, BorderLayout.SOUTH);
        strokeDialog.pack();
        GUIUtils.centerOnScreen(strokeDialog);
        strokeDialog.setVisible(true);
    }

    private void closeStrokeDialog() {
        if (strokeDialog != null) {
            strokeDialog.setVisible(false);
            strokeDialog.dispose();
            strokeDialog = null;
        }
    }

    @Override
    protected void toolEnded() {
        closeStrokeDialog();
    }

    @Override
    public void paintOverLayer(Graphics2D g) {
        if (drawing) {
            paintShape(g, userDrag);
        }
    }

    /**
     * Paint a shape on the given ImageComponent. Can be used programmatically.
     * Tha start and end point points are given relative to the Composition (not Layer)
     */
    public void paintShapeOnIC(Composition comp, UserDrag userDrag) {
        ImageLayer layer = (ImageLayer) comp.getActiveLayer();
        int translationX = -layer.getTranslationX();
        int translationY = -layer.getTranslationY();

        BufferedImage bi = layer.getBufferedImage();
        Graphics2D g2 = bi.createGraphics();
        g2.translate(translationX, translationY);
        comp.setSelectionClipping(g2, null);

        paintShape(g2, userDrag);
        g2.dispose();
    }

    /**
     * Called by paintOnImage while dragging, and by paintShapeOnIC on mouse release
     */
    private void paintShape(Graphics2D g, UserDrag userDrag) {
        if (userDrag.isClick()) {
            return;
        }

        if (basicStroke == null) {
            basicStroke = new BasicStroke(1);
        }

        ShapeType shapeType = typeModel.getSelectedItem();
        Shape currentShape = shapeType.getShape(userDrag);

        ShapesAction action = actionModel.getSelectedItem();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        TwoPointsFillType fillType = fillModel.getSelectedItem();
        if (action.fill()) {
            if (shapeType.isClosed()) {
                g.setPaint(fillType.getPaint(userDrag));
                g.fill(currentShape);
            } else if (!action.stroke()) {
                // special case: a shape that is not closed can be only stroked, even if stroke is disabled
                // stroke it with the basic stroke
                g.setStroke(basicStroke);
                g.setPaint(fillType.getPaint(userDrag));
                g.draw(currentShape);
            }
        } else {
            // no fill
        }

        TwoPointsFillType strokeFill = strokeFillModel.getSelectedItem();
        if (action.stroke()) {
            Stroke stroke = createStroke();
            g.setStroke(stroke);
            g.setPaint(strokeFill.getPaint(userDrag));
            g.draw(currentShape);
        } else {
            // no stroke
        }


        if (action.createSelection()) {
            if (action.enableStrokeSettings()) {
                Stroke stroke = createStroke();
                currentShape = stroke.createStrokedShape(currentShape);
            } else if (!shapeType.isClosed()) {
                if (basicStroke == null) {
                    throw new IllegalStateException();
                }
                currentShape = basicStroke.createStrokedShape(currentShape);
            }

            Composition comp = AppLogic.getActiveComp(); // TODO there should be a more direct way to get the reference
            Selection selection = comp.getSelection();
            if (selection == null) {
                comp.createSelectionFromShape(currentShape);
            } else {
                selection.setShape(currentShape);
            }
        }
    }

    private Stroke createStroke() {
        int strokeWidth = strokeWidthParam.getValue();

        float[] dashFloats = null;
        if (dashedModel.isSelected()) {
            dashFloats = new float[]{2 * strokeWidth, 2 * strokeWidth};
        }

//        if (strokeWidth < 0) { // ?? happened during robot test
//            throw new IllegalStateException("strokeWidth = " + strokeWidth);
//        }

        Stroke stroke = strokeTypeModel.getSelectedItem().getStroke(
                strokeWidth,
                strokeCapModel.getSelectedItem().getValue(),
                strokeJoinModel.getSelectedItem().getValue(),
                dashFloats
        );

        return stroke;
    }

    /**
     * Used for testing
     */
    public void setShapeType(ShapeType newType) {
        typeModel.setSelectedItem(newType);
    }

    public boolean isDrawing() {
        return drawing;
    }

    private void enableStrokeSettings(boolean b) {
        strokeSettingsButton.setEnabled(b);

        if (!b) {
            closeStrokeDialog();
        }
    }

    private void enableStrokePaintSelection(boolean b) {
        strokeFillCombo.setEnabled(b);
    }

    private void enableFillPaintSelection(boolean b) {
        fillCombo.setEnabled(b);
    }

    /**
     * Can be used for debugging
     */
    public void setAction(ShapesAction action) {
        actionModel.setSelectedItem(action);
    }

    /**
     * Can be used for debugging
     */
    public void setStrokeType(StrokeType newStrokeType) {
        strokeTypeModel.setSelectedItem(newStrokeType);
    }
}

