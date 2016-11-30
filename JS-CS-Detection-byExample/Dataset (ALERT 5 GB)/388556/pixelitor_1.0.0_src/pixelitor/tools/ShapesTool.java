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
import org.jdesktop.swingx.painter.effects.AreaEffect;
import pixelitor.AppLogic;
import pixelitor.Composition;
import pixelitor.ImageComponent;
import pixelitor.PixelitorWindow;
import pixelitor.filters.gui.RangeParam;
import pixelitor.filters.painters.EffectsPanel;
import pixelitor.history.History;
import pixelitor.history.NewSelectionEdit;
import pixelitor.history.PixelitorEdit;
import pixelitor.history.SelectionChangeEdit;
import pixelitor.layers.ImageLayer;
import pixelitor.selection.Selection;
import pixelitor.utils.GUIUtils;
import pixelitor.utils.OKCancelDialog;
import pixelitor.utils.SliderSpinner;

import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
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
public class ShapesTool extends Tool {
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
    private JButton strokeSettingsButton;
    private BasicStroke basicStroke;
    private JComboBox strokeFillCombo;
    private JComboBox fillCombo = new JComboBox(fillModel);
    private JButton effectsButton;
    private OKCancelDialog effectsDialog;
    private EffectsPanel effectsPanel;

    private Shape backupSelectionShape = null;
    private boolean drawing = false;


    public ShapesTool() {
        super('s', "Shapes", "shapes_tool_icon.gif", "click and drag to draw a shape", Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR), true, true, false);

        strokeFillModel.setSelectedItem(TwoPointsFillType.BACKGROUND);
        strokeFillCombo = new JComboBox(strokeFillModel);

        spaceDragBehavior = true;
    }

    @Override
    void initSettingsPanel() {
        toolSettingsPanel.add(new JLabel("Shape:"));
        final JComboBox shapeTypeCB = new JComboBox(typeModel);
        toolSettingsPanel.add(shapeTypeCB);

        toolSettingsPanel.add(new JLabel("Action:"));
        JComboBox actionCombo = new JComboBox(actionModel);
        toolSettingsPanel.add(actionCombo);

        actionCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableSettings();
            }
        });

        toolSettingsPanel.add(new JLabel("Fill:"));
        toolSettingsPanel.add(fillCombo);

        toolSettingsPanel.add(new JLabel("Stroke:"));
        toolSettingsPanel.add(strokeFillCombo);

        strokeSettingsButton = new JButton("Stroke Settings...");
        toolSettingsPanel.add(strokeSettingsButton);
        strokeSettingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initAndShowStrokeSettingsDialog();
            }
        });

        effectsButton = new JButton("Effects...");
        toolSettingsPanel.add(effectsButton);
        effectsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEffectsDialog();
            }
        });

        enableSettings();
    }

    private void showEffectsDialog() {
        if (effectsPanel == null) {
            effectsPanel = new EffectsPanel(null);
        }

        effectsDialog = new OKCancelDialog(effectsPanel, "Effects") {
            @Override
            protected void dialogAccepted() {
                super.dialogAccepted();
                closeDialog(effectsDialog);
                effectsPanel.updateEffectsFromGUI();
            }

            @Override
            protected void dialogCancelled() {
                super.dialogCancelled();
                closeDialog(effectsDialog);
            }
        };
        effectsDialog.setVisible(true);
    }

    @Override
    public void toolMousePressed(MouseEvent e, ImageComponent ic) {
        Composition comp = ic.getComp();
        Selection selection = comp.getSelection();
        if (selection != null) {
            backupSelectionShape = selection.getShape();
        } else {
            backupSelectionShape = null;
        }
    }

    @Override
    public void toolMouseDragged(MouseEvent e, ImageComponent ic) {
        drawing = true;
        userDrag.setStartFromCenter(e.isAltDown());

        Composition comp = ic.getComp();

        comp.imageChanged(true, false); // TODO optimize, the whole image should not be repainted
    }

    @Override
    public void toolMouseReleased(MouseEvent e, ImageComponent ic) {
        userDrag.setStartFromCenter(e.isAltDown());


        Composition comp = ic.getComp();

        ShapesAction action = actionModel.getSelectedItem();
        boolean selectionMode = action.createSelection();
        if (!selectionMode) {
//            saveImageForUndo(comp);

            int thickness = 0;
            int extraStrokeThickness = 0;
            if (action.enableStrokePaintSelection()) {
                thickness = strokeWidthParam.getValue();

                extraStrokeThickness = strokeTypeModel.getSelectedItem().getExtraWidth(thickness);
                thickness += extraStrokeThickness;
            }

            int effectThickness = 0;
            if (effectsPanel != null) {
                effectThickness = effectsPanel.getMaxEffectThickness();

                // the extra stroke thickness must be added to this because the effect can be on the stroke
                effectThickness += extraStrokeThickness;
            }

            if (effectThickness > thickness) {
                thickness = effectThickness;
            }

            ShapeType shapeType = typeModel.getSelectedItem();
            Shape currentShape = shapeType.getShape(userDrag);
            Rectangle shapeBounds = currentShape.getBounds();
            shapeBounds.grow(thickness, thickness);

            ToolAffectedArea affectedArea = new ToolAffectedArea(comp, shapeBounds, false);
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
    }


    private void enableSettings() {
        ShapesAction action = actionModel.getSelectedItem();
        enableEffectSettings(action.drawEffects());
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
                closeDialog(strokeDialog);
            }
        });
        JPanel southPanel = new JPanel();
        southPanel.add(okButton);
        strokeDialog.add(southPanel, BorderLayout.SOUTH);
        strokeDialog.pack();
        GUIUtils.centerOnScreen(strokeDialog);
        strokeDialog.setVisible(true);
    }

    private static void closeDialog(JDialog d) {
        if (d != null) {
            d.setVisible(false);
            d.dispose();
        }
    }

    @Override
    protected void toolEnded() {
        closeDialog(strokeDialog);
        closeDialog(effectsDialog);
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


        if (action.fill()) {
            TwoPointsFillType fillType = fillModel.getSelectedItem();
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

        Stroke stroke = null;
        if (action.stroke()) {
            TwoPointsFillType strokeFill = strokeFillModel.getSelectedItem();
            stroke = createStroke();
            g.setStroke(stroke);
            g.setPaint(strokeFill.getPaint(userDrag));
            g.draw(currentShape);
        } else {
            // no stroke
        }

        if (action.drawEffects()) {
            if (effectsPanel != null) {
                AreaEffect[] areaEffects = effectsPanel.getEffectsAsArray();
                for (AreaEffect effect : areaEffects) {
                    if(action.fill()) {
                        effect.apply(g, currentShape, 0, 0);
                    } else if(action.stroke()) { // special case if there is only stroke
                        if(stroke == null) {
                            stroke = createStroke();
                        }
                        effect.apply(g, stroke.createStrokedShape(currentShape), 0, 0);
                    } else { // "effects only"
                        effect.apply(g, currentShape, 0, 0);
                    }
                }
            }
        }


        if (action.createSelection()) {
            Shape selectionShape = null;
            if (action.enableStrokeSettings()) {
                if(stroke == null) {
                    stroke = createStroke();
                }
                selectionShape = stroke.createStrokedShape(currentShape);
            } else if (!shapeType.isClosed()) {
                if (basicStroke == null) {
                    throw new IllegalStateException();
                }
                selectionShape = basicStroke.createStrokedShape(currentShape);
            } else {
                selectionShape = currentShape;
            }

            Composition comp = AppLogic.getActiveComp(); // TODO there should be a more direct way to get the reference
            Selection selection = comp.getSelection();
            if (selection == null) {
                comp.createSelectionFromShape(selectionShape);
            } else {
                selection.setShape(selectionShape);
            }
        }
    }

    private Stroke createStroke() {
        int strokeWidth = strokeWidthParam.getValue();

        float[] dashFloats = null;
        if (dashedModel.isSelected()) {
            dashFloats = new float[]{2 * strokeWidth, 2 * strokeWidth};
        }

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
            closeDialog(strokeDialog);
        }
    }

    private void enableEffectSettings(boolean b) {
        effectsButton.setEnabled(b);

        if (!b) {
            closeDialog(effectsDialog);
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

