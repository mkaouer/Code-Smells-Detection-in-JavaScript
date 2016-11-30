/*
 * Copyright 2009-2010 L�szl� Bal�zs-Cs�ki
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

import pixelitor.Composition;
import pixelitor.ImageComponent;
import pixelitor.history.DeselectEdit;
import pixelitor.history.History;
import pixelitor.history.NewSelectionEdit;
import pixelitor.history.PixelitorEdit;
import pixelitor.history.SelectionChangeEdit;
import pixelitor.menus.SelectionActions;
import pixelitor.selection.Selection;
import pixelitor.selection.SelectionInteraction;
import pixelitor.selection.SelectionType;

import javax.swing.*;
import java.awt.Cursor;
import java.awt.Shape;
import java.awt.event.MouseEvent;

/**
 *
 */
public class SelectionTool extends Tool {
    private JComboBox typeCombo;
    private JComboBox interactionCombo;

    private boolean altMeansSubtract = false;
    private SelectionInteraction originalSelectionInteraction;

    private Shape backupShape = null;


    SelectionTool() {
        super('m', "Selection", "selection_tool_icon.gif", "click and drag to select an area", Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR), false, true, false);
        spaceDragBehavior = true;
    }

    @Override
    void initSettingsPanel() {
        toolSettingsPanel.add(new JLabel("Type:"));
        typeCombo = new JComboBox(SelectionType.values());
        toolSettingsPanel.add(typeCombo);

        toolSettingsPanel.addSeparator();

        toolSettingsPanel.add(new JLabel("New Selection:"));
        interactionCombo = new JComboBox(SelectionInteraction.values());
        toolSettingsPanel.add(interactionCombo);

        toolSettingsPanel.addSeparator();

        JButton brushTraceButton = new JButton(SelectionActions.getTraceWithBrush());
        toolSettingsPanel.add(brushTraceButton);

        JButton eraserTraceButton = new JButton(SelectionActions.getTraceWithEraser());
        toolSettingsPanel.add(eraserTraceButton);

        JButton cropButton = new JButton(SelectionActions.getCropAction());
        toolSettingsPanel.add(cropButton);
    }

    @Override
    public void toolMousePressed(MouseEvent e, ImageComponent ic) {
        boolean shiftDown = e.isShiftDown();
        boolean altDown = e.isAltDown();

        altMeansSubtract = altDown;

        if (shiftDown || altDown) {
            originalSelectionInteraction = (SelectionInteraction) interactionCombo.getSelectedItem();
            if (shiftDown) {
                if (altDown) {
                    interactionCombo.setSelectedItem(SelectionInteraction.INTERSECT);
                } else {
                    interactionCombo.setSelectedItem(SelectionInteraction.ADD);
                }
            } else if (altDown) {
                interactionCombo.setSelectedItem(SelectionInteraction.SUBTRACT);
            }
        }


        SelectionType selectionType = (SelectionType) typeCombo.getSelectedItem();
        SelectionInteraction selectionInteraction = (SelectionInteraction) interactionCombo.getSelectedItem();

        Composition comp = ic.getComp();
        Selection selection = comp.getSelection();
        if (selection == null) {
            backupShape = null;
            comp.startSelection(selectionType, selectionInteraction);
        } else {
            backupShape = selection.getShape();
            selection.startNewShape(selectionType, selectionInteraction);
        }
    }

    @Override
    public void toolMouseDragged(MouseEvent e, ImageComponent ic) {
        Composition comp = ic.getComp();
        Selection selection = comp.getSelection();

        boolean altDown = e.isAltDown();
        boolean startFromCenter = (!altMeansSubtract) && altDown;
        if (!altDown) {
            altMeansSubtract = false;
        }

        userDrag.setStartFromCenter(startFromCenter);
        selection.updateSelection(userDrag);
    }

    @Override
    public void mouseMoved(MouseEvent e, ImageComponent ic) {
//        if(typeCombo.getSelectedItem() == SelectionType.POLYGONAL_LASSO) {
//            mouseDragged(e, ic);
//        }
    }

    @Override
    public void toolMouseReleased(MouseEvent e, ImageComponent ic) {
        if (userDrag.isClick()) { // will be handled by mouseClicked
            return;
        }

        Composition comp = ic.getComp();
        Selection selection = comp.getSelection();

        if (originalSelectionInteraction != null) {
            interactionCombo.setSelectedItem(originalSelectionInteraction);
            originalSelectionInteraction = null;
        }

        boolean startFromCenter = (!altMeansSubtract) && e.isAltDown();

        userDrag.setStartFromCenter(startFromCenter);
        selection.updateSelection(userDrag);

        boolean stillSomethingSelected = selection.combineShapes();

        PixelitorEdit edit = null;

        if (stillSomethingSelected) {
            SelectionInteraction selectionInteraction = selection.getSelectionInteraction();
            boolean somethingSelectedAfterClipping = selection.clipToCompSize(comp);
            if (somethingSelectedAfterClipping) {
                if (newSelectionStarted()) {
                    edit = new NewSelectionEdit(comp, selection.getShape());
                } else {
                    edit = new SelectionChangeEdit(comp, backupShape, selectionInteraction.getNameForUndo());
                }
            } else { // the selection is outside the composition bounds
                deselect(ic, false); // don't create a DeselectEdit because the backup shape could be null
                if (!newSelectionStarted()) { // backupShape != null
                    // create a special DeselectEdit with the backupShape
                    edit = new DeselectEdit(ic.getComp(), backupShape, "SelectionTool.toolMouseReleased 1");
                    assert !comp.hasSelection();
                }
            }
        } else {
            // special case: it started like a selection change but nothing is selected now
            // we also get here if the selection is a single line (area = 0), but then backupShape is null
            deselect(ic, false); // don't create a DeselectEdit because the backup shape could be null
            if (!newSelectionStarted()) { // backupShape != null
                // create a special DeselectEdit with the backupShape
                edit = new DeselectEdit(ic.getComp(), backupShape, "SelectionTool.toolMouseReleased 2");
                assert !comp.hasSelection();
            }
        }

        if (edit != null) {
            History.addEdit(edit);
        }

        altMeansSubtract = false;
    }

    @Override
    public boolean mouseClicked(MouseEvent e, ImageComponent ic) {
        super.mouseClicked(e, ic);

//        if(typeCombo.getSelectedItem() == SelectionType.POLYGONAL_LASSO) {
//            addPolygonalLassoPoint(ic);
//
//            return false;
//        }

        deselect(ic, true);

        altMeansSubtract = false;

        return false;
    }

    private void addPolygonalLassoPoint(ImageComponent ic) {
        Composition comp = ic.getComp();
        Selection selection = comp.getSelection();
        if (selection != null) {
            selection.addNewPolygonalLassoPoint(userDrag);
        }
    }

    private static void deselect(ImageComponent ic, boolean sendDeselectEdit) {
        Composition comp = ic.getComp();

        if (comp.hasSelection()) {
            comp.deselect(sendDeselectEdit);
        }
    }

    private boolean newSelectionStarted() {
        return backupShape == null;
    }

}
