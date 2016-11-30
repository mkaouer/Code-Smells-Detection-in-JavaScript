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

import pixelitor.Composition;
import pixelitor.GlobalKeyboardWatch;
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
public class SelectionTool extends ForwardingTool {
    private JComboBox typeCombo;
    private JComboBox interactionCombo;

    private boolean altMeansSubtract = false;
    private SelectionInteraction originalSelectionInteraction;

    private boolean newSelectionStarted = false;
    private Shape backupShape = null;

    private boolean endPointInitialized = false;

    SelectionTool() {
        super('m', "Selection", "selection_tool_icon.gif", "click and drag to select an area", Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    void initSettingsPanel(ToolSettingsPanel p) {
        p.add(new JLabel("Type:"));
        typeCombo = new JComboBox(SelectionType.values());
        p.add(typeCombo);

        p.addSeparator();

        p.add(new JLabel("New Selection:"));
        interactionCombo = new JComboBox(SelectionInteraction.values());
        p.add(interactionCombo);

        p.addSeparator();

        JButton brushTraceButton = new JButton(SelectionActions.getTraceWithBrush());
        p.add(brushTraceButton);

        JButton eraserTraceButton = new JButton(SelectionActions.getTraceWithEraser());
        p.add(eraserTraceButton);

        JButton cropButton = new JButton(SelectionActions.getCropAction());
        p.add(cropButton);
    }

    @Override
    public boolean mousePressed(MouseEvent e, ImageComponent ic) {
        if (super.mousePressed(e, ic)) {
            return true;
        }

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
            comp.startSelection(selectionType, selectionInteraction);
            newSelectionStarted = true;
        } else {
            backupShape = selection.getShape();
            selection.startNewShape(selectionType, selectionInteraction);
            newSelectionStarted = false;
        }

        endPointInitialized = false;
        return false;
    }

    @Override
    public boolean mouseDragged(MouseEvent e, ImageComponent ic) {
        userDrag.saveEndValues();
        if (super.mouseDragged(e, ic)) { // this changes the end variable
            return true;
        }

        Composition comp = ic.getComp();
        Selection selection = comp.getSelection();
        if (selection == null) {
            // TODO it shouldn't happen but it happened during robot tests
            return true;
        }

        if (endPointInitialized && GlobalKeyboardWatch.isSpaceDown()) {
            userDrag.adjustStartForSpaceDownMove();
        }

        endPointInitialized = true; // can be set to true after the first super.mouseDragged(e, ic);

        boolean altDown = e.isAltDown();
        boolean startFromCenter = (!altMeansSubtract) && altDown;
        if (!altDown) {
            altMeansSubtract = false;
        }

        userDrag.setStartFromCenter(startFromCenter);
        selection.updateSelection(userDrag);
        return false;
    }

    @Override
    public void mouseMoved(MouseEvent e, ImageComponent ic) {
//        if(typeCombo.getSelectedItem() == SelectionType.POLYGONAL_LASSO) {
//            mouseDragged(e, ic);
//        }
    }

    @Override
    public boolean mouseReleased(MouseEvent e, ImageComponent ic) {
        if (super.mouseReleased(e, ic)) {
            return true;
        }

        if (userDrag.isClick()) { // will be handled by mouseClicked
            return true;
        }

//        if(typeCombo.getSelectedItem() == SelectionType.POLYGONAL_LASSO) {
//            addPolygonalLassoPoint(ic);
//
//            return false;
//        }


        Composition comp = ic.getComp();
        Selection selection = comp.getSelection();
        if (selection == null) {
            // TODO it shouldn't happen but it happened during robot tests
            return true;
        }

        if (originalSelectionInteraction != null) {
            interactionCombo.setSelectedItem(originalSelectionInteraction);
            originalSelectionInteraction = null;
        }

        boolean startFromCenter = (!altMeansSubtract) && e.isAltDown();

        userDrag.setStartFromCenter(startFromCenter);
        selection.updateSelection(userDrag);

        boolean stillSomethingSelected = selection.combineShapes();

        PixelitorEdit edit;

        if (stillSomethingSelected) {
            SelectionInteraction selectionInteraction = selection.getSelectionInteraction();
            boolean somethingSelectedAfterClipping = selection.clipToCompSize(comp);
            if (somethingSelectedAfterClipping) {
                if (newSelectionStarted) {
                    edit = new NewSelectionEdit(comp, selection.getShape());
                } else {
                    edit = new SelectionChangeEdit(comp, backupShape, selectionInteraction.getNameForUndo());
                }
            } else {
                edit = nothingSelectedAfterMouseRelease(ic);
            }
        } else {
            edit = nothingSelectedAfterMouseRelease(ic);
        }

        if (edit != null) {
            History.addEdit(edit);
        }

        endPointInitialized = false;
        altMeansSubtract = false;

        return false;
    }

    private PixelitorEdit nothingSelectedAfterMouseRelease(ImageComponent ic) {
        // special case: it started like a selection change but nothing is selected now
        // we also get here if the selection is a single line (area = 0), but then backupShape is null
        // and also f the selection is outside the composition bounds

        deselect(ic, false); // don't create a DeselectEdit because the current shape is null

        if (backupShape != null) {
            // create a special DeselectEdit with the backupShape
            return new DeselectEdit(ic.getComp(), backupShape);
        }
        return null;
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

}
