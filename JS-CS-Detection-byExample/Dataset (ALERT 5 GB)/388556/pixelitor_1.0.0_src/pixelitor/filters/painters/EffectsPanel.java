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
import pixelitor.filters.gui.ParamAdjustmentListener;
import pixelitor.utils.GUIUtils;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration panel for SwingX effects
 */
public class EffectsPanel extends JPanel {
    private EffectConfigurator glowConfigurator;
    private EffectConfigurator innerGlowConfigurator;
    private NeonBorderEffectConfigurator neonBorderConfigurator;
    private DropShadowEffectConfigurator dropShadowConfigurator;

    private GlowPathEffect glowEffect;
    private InnerGlowPathEffect innerGlowEffect;
    private NeonBorderEffect neonBorderEffect;
    private ShadowPathEffect dropShadowEffect;

    private JTabbedPane tabs;

    public EffectsPanel(final ParamAdjustmentListener listener) {
        setLayout(new BorderLayout());

        glowConfigurator = new EffectConfigurator("Glow", false, Color.WHITE, 10);
        innerGlowConfigurator = new EffectConfigurator("Inner Glow", false, Color.WHITE, 10);
        neonBorderConfigurator = new NeonBorderEffectConfigurator(false, Color.GREEN, Color.WHITE, 10);
        dropShadowConfigurator = new DropShadowEffectConfigurator(false, Color.BLACK, 10);

        if (listener != null) {
            glowConfigurator.setAdjustmentListener(listener);
            innerGlowConfigurator.setAdjustmentListener(listener);
            neonBorderConfigurator.setAdjustmentListener(listener);
            dropShadowConfigurator.setAdjustmentListener(listener);
        }

        tabs = new JTabbedPane();
        tabs.setTabPlacement(JTabbedPane.LEFT);
        tabs.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);

        addTab("Glow               ", glowConfigurator);
        addTab("Inner Glow     ", innerGlowConfigurator);
        addTab("Neon Border ", neonBorderConfigurator);
        addTab("Drop Shadow", dropShadowConfigurator);

        tabs.setPreferredSize(new Dimension(530, 250)); // A width if 520 is enough on windows. TODO: calculate

        add(tabs, BorderLayout.CENTER);
    }

    public void updateEffectsFromGUI() {
        if (glowConfigurator.isSelected()) {
            glowEffect = new GlowPathEffect();
            glowConfigurator.updateEffectColorAndBrush(glowEffect);
        } else {
            glowEffect = null;
        }

        if (innerGlowConfigurator.isSelected()) {
            innerGlowEffect = new InnerGlowPathEffect();
            innerGlowConfigurator.updateEffectColorAndBrush(innerGlowEffect);
        } else {
            innerGlowEffect = null;
        }

        if (neonBorderConfigurator.isSelected()) {
            Color edgeColor = neonBorderConfigurator.getColor();
            Color centerColor = neonBorderConfigurator.getInnerColor();
            int effectWidth = neonBorderConfigurator.getBrushWidth();

            neonBorderEffect = new NeonBorderEffect(edgeColor, centerColor, effectWidth);
        } else {
            neonBorderEffect = null;
        }

        if (dropShadowConfigurator.isSelected()) {
            dropShadowEffect = new ShadowPathEffect();
            dropShadowConfigurator.updateEffectColorAndBrush(dropShadowEffect);
            dropShadowEffect.setOffset(dropShadowConfigurator.getOffset());
        } else {
            dropShadowEffect = null;
        }
    }


    private void addTab(String name, EffectConfigurator configurator) {
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox tabSelectionCB = new JCheckBox();
        tabSelectionCB.setModel(configurator.getEnabledModel());
        tabPanel.add(tabSelectionCB);
        tabPanel.add(new JLabel(name));

//        tabPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
        tabPanel.setOpaque(false);

        tabs.addTab(name, configurator);
        tabs.setTabComponentAt(tabs.getTabCount() - 1, tabPanel);
    }

    public AreaEffect[] getEffectsAsArray() {
        List<AreaEffect> effects = new ArrayList<AreaEffect>(2);
        // draw the drop shadow first so that it doesn't get painted over other effects
        if (dropShadowEffect != null) {
            effects.add(dropShadowEffect);
        }
        if (glowEffect != null) {
            effects.add(glowEffect);
        }
        if (innerGlowEffect != null) {
            effects.add(innerGlowEffect);
        }
        if (neonBorderEffect != null) {
            effects.add(neonBorderEffect);
        }
        AreaEffect[] retVal = effects.toArray(new AreaEffect[effects.size()]);
        return retVal;
    }

    public int getMaxEffectThickness() {
        int max = 0;
        if (glowEffect != null) {
            int effectWidth = glowEffect.getEffectWidth();
            if (effectWidth > max) {
                max = effectWidth;
            }
        }
        if (neonBorderEffect != null) {
            int effectWidth = neonBorderEffect.getEffectWidth();
            if (effectWidth > max) {
                max = effectWidth;
            }
        }
        if (dropShadowEffect != null) {
            double safetyFactor = 2.0;
            int effectWidth = 3 + (int) (dropShadowEffect.getEffectWidth() * safetyFactor);

            Point2D offset = dropShadowEffect.getOffset();

            int xGap = effectWidth + (int) Math.abs(offset.getX() * safetyFactor);
            if (xGap > max) {
                max = xGap;
            }
            int yGap = effectWidth + (int) Math.abs(offset.getY() * safetyFactor);
            if (yGap > max) {
                max = yGap;
            }
        }

        return max;
    }


    public static void main(String[] args) {
        GUIUtils.testJComponent(new EffectsPanel(null));
    }


}

