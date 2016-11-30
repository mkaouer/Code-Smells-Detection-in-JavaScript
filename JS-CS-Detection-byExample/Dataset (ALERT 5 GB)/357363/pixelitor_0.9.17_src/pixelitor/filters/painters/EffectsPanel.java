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
package pixelitor.filters.painters;

import org.jdesktop.swingx.painter.effects.AreaEffect;
import org.jdesktop.swingx.painter.effects.GlowPathEffect;
import org.jdesktop.swingx.painter.effects.InnerGlowPathEffect;
import org.jdesktop.swingx.painter.effects.NeonBorderEffect;
import org.jdesktop.swingx.painter.effects.ShadowPathEffect;
import pixelitor.utils.GridBagHelper;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class EffectsPanel extends JPanel {
    private JCheckBox glowCB;
    private JCheckBox innerGlowCB;
    private JCheckBox neonBorderCB;
    private JCheckBox dropShadowCB;
    private JCheckBox watermarkCB;

    public EffectsPanel(ChangeListener changeListener) {
        setBorder(BorderFactory.createTitledBorder("Effects"));
        setLayout(new GridBagLayout());

        GridBagHelper.addLabel(this, "Glow:", 0, 0);
        glowCB = new JCheckBox();
        glowCB.addChangeListener(changeListener);
        GridBagHelper.addControl(this, glowCB);

        GridBagHelper.addLabel(this, "Inner Glow:", 0, 1);
        innerGlowCB = new JCheckBox();
        innerGlowCB.addChangeListener(changeListener);
        GridBagHelper.addControl(this, innerGlowCB);

        GridBagHelper.addLabel(this, "Neon Border:", 0, 2);
        neonBorderCB = new JCheckBox();
        neonBorderCB.addChangeListener(changeListener);
        GridBagHelper.addControl(this, neonBorderCB);

        GridBagHelper.addLabel(this, "Drop Shadow:", 0, 3);
        dropShadowCB = new JCheckBox();
        dropShadowCB.addChangeListener(changeListener);
        GridBagHelper.addControl(this, dropShadowCB);

        GridBagHelper.addLabel(this, "Use Text for Watermarking:", 0, 4);
        watermarkCB = new JCheckBox();
        watermarkCB.addChangeListener(changeListener);
        GridBagHelper.addControl(this, watermarkCB);

    }

    public boolean isWatermark() {
        return watermarkCB.isSelected();
    }

    public AreaEffect[] getEffects() {
        List<AreaEffect> effects = new ArrayList<AreaEffect>(2);
        if (glowCB.isSelected()) {
            effects.add(new GlowPathEffect());
        }
        if (innerGlowCB.isSelected()) {
            effects.add(new InnerGlowPathEffect());
        }
        if (neonBorderCB.isSelected()) {
            effects.add(new NeonBorderEffect());
        }
        if (dropShadowCB.isSelected()) {
            effects.add(new ShadowPathEffect());
        }
        AreaEffect[] retVal = effects.toArray(new AreaEffect[effects.size()]);
        return retVal;
    }
}
