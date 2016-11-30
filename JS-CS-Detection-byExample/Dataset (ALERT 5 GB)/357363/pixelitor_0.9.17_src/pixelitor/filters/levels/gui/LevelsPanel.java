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

package pixelitor.filters.levels.gui;

import pixelitor.filters.gui.AdjustPanel;
import pixelitor.filters.levels.GrayScaleLookup;
import pixelitor.filters.levels.RGBLookup;
import pixelitor.filters.lookup.DynamicLookupOp;
import pixelitor.utils.GUIUtils;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;

public class LevelsPanel extends AdjustPanel implements ItemListener, GrayScaleAdjustmentChangeListener {
    private DefaultComboBoxModel selectorModel;

    private OneChannelLevelsPanel rgbPanel;
    private OneChannelLevelsPanel rPanel;
    private OneChannelLevelsPanel gPanel;
    private OneChannelLevelsPanel bPanel;
    private OneChannelLevelsPanel rgPanel;
    private OneChannelLevelsPanel gbPanel;
    private OneChannelLevelsPanel rbPanel;
    private JPanel cardPanel;
    private Collection<OneChannelLevelsPanel> levelsPanels = new ArrayList<OneChannelLevelsPanel>();

    public LevelsPanel(DynamicLookupOp filter) {
        super(filter);

        rgbPanel = new OneChannelLevelsPanel(OneChannelLevelsPanel.Type.RGB, this);
        rPanel = new OneChannelLevelsPanel(OneChannelLevelsPanel.Type.R, this);
        gPanel = new OneChannelLevelsPanel(OneChannelLevelsPanel.Type.G, this);
        bPanel = new OneChannelLevelsPanel(OneChannelLevelsPanel.Type.B, this);
        rgPanel = new OneChannelLevelsPanel(OneChannelLevelsPanel.Type.RG, this);
        gbPanel = new OneChannelLevelsPanel(OneChannelLevelsPanel.Type.GB, this);
        rbPanel = new OneChannelLevelsPanel(OneChannelLevelsPanel.Type.RB, this);

        setLayout(new BorderLayout());

        selectorModel = new DefaultComboBoxModel();
        JComboBox selector = new JComboBox(selectorModel);
        selector.addItemListener(this);

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout());
        northPanel.add(selector);
        JButton resetAllButton = new JButton("Reset all");
        resetAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetToDefaultSettings();
            }
        });
        northPanel.add(resetAllButton);
        add(northPanel, BorderLayout.NORTH);

        cardPanel = new JPanel();
        cardPanel.setLayout(new CardLayout());

        addNewCard(rgbPanel);
        addNewCard(rPanel);
        addNewCard(gPanel);
        addNewCard(bPanel);
        addNewCard(rgPanel);
        addNewCard(gbPanel);
        addNewCard(rbPanel);

        add(cardPanel, BorderLayout.CENTER);
    }


    private void addNewCard(OneChannelLevelsPanel chPanel) {
        String channelName = chPanel.getCardName();
        cardPanel.add(chPanel, channelName);
        selectorModel.addElement(channelName);
        levelsPanels.add(chPanel);
    }

    public static void main(String[] args) {
        GUIUtils.testJComponent(new LevelsPanel(null));
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        CardLayout cl = (CardLayout) (cardPanel.getLayout());
        cl.show(cardPanel, (String) e.getItem());
    }

    private void resetToDefaultSettings() {
        for (OneChannelLevelsPanel chPanel : levelsPanels) {
            chPanel.resetToDefaultSettings();
        }
        grayScaleAdjustmentHasChanged();
    }

    @Override
    public void grayScaleAdjustmentHasChanged() {
        GrayScaleLookup rgb = rgbPanel.getAdjustment();

        GrayScaleLookup r = rPanel.getAdjustment();
        GrayScaleLookup g = gPanel.getAdjustment();
        GrayScaleLookup b = bPanel.getAdjustment();

        GrayScaleLookup rg = rgPanel.getAdjustment();
        GrayScaleLookup gb = gbPanel.getAdjustment();
        GrayScaleLookup rb = rbPanel.getAdjustment();

        RGBLookup unifiedAdjustments = new RGBLookup(rgb, r, g, b, rg, rb, gb);
        DynamicLookupOp levelsFilter = (DynamicLookupOp) op;
        levelsFilter.setRGBLookup(unifiedAdjustments);
        super.executeFilterPreview();
    }
}
