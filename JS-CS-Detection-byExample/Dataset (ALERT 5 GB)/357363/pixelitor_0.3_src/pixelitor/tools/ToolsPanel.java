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
package pixelitor.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Insets;

import javax.swing.*;

import pixelitor.AppLogic;
import pixelitor.PixelitorWindow;
import pixelitor.utils.GUIUtils;

/**
 * A panel where the user can select the tools
 */
public class ToolsPanel extends JPanel implements ActionListener {
    private JToggleButton cropSelectionRB;
    private JToggleButton gradientRB;
    private JToggleButton drawRB;
    private ButtonGroup group;
    private Box verticalBox;

    public ToolsPanel() {
        verticalBox = Box.createVerticalBox();
        group = new ButtonGroup();

        cropSelectionRB = setupButton("crop_selection_tool_icon.gif", "selection for crop");
        drawRB = setupButton("draw_tool_icon.gif", "draw");
        gradientRB = setupButton("gradient_tool_icon.png", "gradient");

//        gradientRB.setSelectedIcon(drawRB.getIcon());

        add(verticalBox);

        // set draw as default selected
        cropSelectionRB.setSelected(true);
        Tool.setCurrentTool(Tool.CROP_SELECTION);
    }

    private JToggleButton setupButton(String iconName, String toolTipText) {
        String iconPath = "/images/" + iconName;
        java.net.URL imgURL = getClass().getResource(iconPath);
        JToggleButton retVal = null;
        if (imgURL != null) {
            retVal = new JToggleButton(new ImageIcon(imgURL));
            retVal.setToolTipText(toolTipText);
            retVal.setMargin(new Insets(0, 0, 0, 0));
            retVal.setBorderPainted(true);
            retVal.setRolloverEnabled(false);


            verticalBox.add(retVal);
            group.add(retVal);
            retVal.addActionListener(this);
        } else {
            JOptionPane.showMessageDialog(null, iconPath + " not found" , "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("ToolsPanel.setupButton iconPath = \"" + iconPath + "\"");
        }


        return retVal;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cropSelectionRB) {
            AppLogic.setCurrentTool(Tool.CROP_SELECTION);
        } else if (e.getSource() == gradientRB) {
            AppLogic.setCurrentTool(Tool.GRADIENT);
        } else if (e.getSource() == drawRB) {
            AppLogic.setCurrentTool(Tool.DRAW);
        }
    }
}
