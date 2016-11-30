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

package pixelitor.levels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import pixelitor.filters.AdjustPanel;
import pixelitor.filters.lookup.DynamicLookupOp;
import pixelitor.utils.GUIUtils;

public class MultipleChannelsLevelsPanel extends AdjustPanel implements ItemListener, GrayScaleAdjustmentChangeListener {
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

    public MultipleChannelsLevelsPanel(DynamicLookupOp filter) {
		super(filter);

		rgbPanel = new LevelsAdjustments(LevelsAdjustments.Type.RGB, this);
		rPanel = new LevelsAdjustments(LevelsAdjustments.Type.R, this);
		gPanel = new LevelsAdjustments(LevelsAdjustments.Type.G, this);
		bPanel = new LevelsAdjustments(LevelsAdjustments.Type.B, this);
		rgPanel = new LevelsAdjustments(LevelsAdjustments.Type.RG, this);
		gbPanel = new LevelsAdjustments(LevelsAdjustments.Type.GB, this);
		rbPanel = new LevelsAdjustments(LevelsAdjustments.Type.RB, this);

		setLayout(new BorderLayout());

		selectorModel = new DefaultComboBoxModel();
        JComboBox selector = new JComboBox(selectorModel);
		selector.addItemListener(this);

		JPanel tmp = new JPanel();
		tmp.setLayout(new FlowLayout());
		tmp.add(selector);
        JButton resetAllButton = new JButton("Reset all");
		resetAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetToDefaultSettings();
			}
		});
		tmp.add(resetAllButton);
		add(tmp, BorderLayout.NORTH);

		cardPanel = new JPanel();
		cardPanel.setLayout(new CardLayout());

		addToCard(rgbPanel);
		addToCard(rPanel);
		addToCard(gPanel);
		addToCard(bPanel);
		addToCard(rgPanel);
		addToCard(gbPanel);
		addToCard(rbPanel);

		add(cardPanel, BorderLayout.CENTER);
	}


	private void addToCard(OneChannelLevelsPanel chPanel) {
		String channelName = chPanel.getChannelName();
		cardPanel.add(chPanel, channelName);
		selectorModel.addElement(channelName);
		levelsPanels.add(chPanel);
	}

	public static void main(String[] args) {
		GUIUtils.testJComponent(new MultipleChannelsLevelsPanel(null));
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
