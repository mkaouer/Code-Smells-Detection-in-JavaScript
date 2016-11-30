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
package pixelitor.animation;

import pixelitor.utils.GUIUtils;
import pixelitor.utils.GridBagHelper;
import pixelitor.utils.IntTextField;

import javax.swing.*;
import java.awt.GridBagLayout;

/**
 *
 */
public class AnimationSettingsPanel extends JPanel {
    private JTextField lengthTF;
    private JTextField frameRateTF;

    public AnimationSettingsPanel() {
        setLayout(new GridBagLayout());

        GridBagHelper.addLabel(this, "Length: ", 0, 0);
        lengthTF = new IntTextField(4);
        GridBagHelper.addControl(this, lengthTF);
        GridBagHelper.addNextControl(this, new JLabel("seconds"));

        GridBagHelper.addLabel(this, "Frame Rate: ", 0, 1);
        frameRateTF = new IntTextField(4);
        frameRateTF.setText("25");
        GridBagHelper.addControl(this, frameRateTF);
        GridBagHelper.addNextControl(this, new JLabel("fps"));

        GridBagHelper.addLabel(this, "Number of Frames: ", 0, 2);
        JLabel nrOfFramesLabel = new JLabel();
        GridBagHelper.addControl(this, nrOfFramesLabel);
    }

    public static void main(String[] args) {
        GUIUtils.testJComponent(new AnimationSettingsPanel());
    }

}
