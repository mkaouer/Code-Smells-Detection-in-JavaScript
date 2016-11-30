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

import pixelitor.operations.painters.CenteredText;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * A panel where the user can select the tools
 */
public class ToolsPanel extends JPanel {

    /**
     *
     * @param mainComponent - something that is always visible - so that the leyboard shortcuts always work
     */
    public ToolsPanel(JComponent mainComponent) {
        Box verticalBox = Box.createVerticalBox();
        ButtonGroup group = new ButtonGroup();

        Tool[] tools = Tool.values();
        for (Tool tool : tools) {
            final ToolButton toolButton = new ToolButton(tool);
            verticalBox.add(toolButton);
            group.add(toolButton);
            setupKeyboardShortcut(mainComponent, tool, toolButton);
        }

        add(verticalBox);
        setDefaultTool();

        // temporarily the center text filter is used instead of a text tool - TODO
        // in the menu it was added using T, not t
        Action textToolAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CenteredText.INSTANCE.actionPerformed(e);
            }
        };
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('t'), "text");
        getActionMap().put("text", textToolAction);
    }

    private static void setupKeyboardShortcut(JComponent component, Tool tool, final ToolButton toolButton) {
        Action pressToolAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toolButton.doClick();
            }
        };

        String toolName = tool.getName();

        char activationChar = tool.getActivationKeyChar();
        char activationLC = Character.toLowerCase(activationChar);
        char activationUC = Character.toUpperCase(activationChar);

        KeyStroke keyStrokeLC = KeyStroke.getKeyStroke(activationLC);
        KeyStroke keyStrokeUC = KeyStroke.getKeyStroke(activationUC);

        component.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(keyStrokeLC, toolName);
        component.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(keyStrokeUC, toolName);
        component.getActionMap().put(toolName, pressToolAction);
    }

    private static void setDefaultTool() {
        Tool.setCurrentTool(Tool.BRUSH);
        Tool.currentTool.getButton().setSelected(true);
    }
}
