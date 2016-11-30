/* Merchant of Venice - technical analysis software for the stock market.
   Copyright (C) 2002 Andrew Leppard (aleppard@picknowl.com.au)

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.
   
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   
   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
*/

package nz.org.venice.chart;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nz.org.venice.util.Locale;
import nz.org.venice.util.TradingDate;

/**
 *  A class that contains the toolbar implemented to handle the timeline in a chart view
 *  This adds a toolbar with a JScrollbar component to the chart and handles the events
 *  To move the time being viewed when zoomed in
 *  
 * @author Guillermo Bonvehi - gbonvehi
 *
 */
class TimelineHandler implements ChangeListener, MouseWheelListener {
	/**
	 * 
	 */
	private final ChartModule chartModule;
	private JScrollBar bar;
	// Boolean to not run stateChanged while updating the value using Recalculate
	private boolean pause = false;
	// TradingDate holding the minimum value (cache it)
	private TradingDate minX = null;
	
	public TimelineHandler(ChartModule chartModule) {
		this.chartModule = chartModule;
		Chart chart = this.chartModule.getChart();
		
		JToolBar p = new JToolBar(SwingConstants.HORIZONTAL);

		JLabel tl = new JLabel(Locale.getString("TIMELINE"));

		bar = new JScrollBar(JScrollBar.HORIZONTAL);
		TradingDate minX = (TradingDate)chart.calculateStartX();
		TradingDate maxX = (TradingDate)chart.calculateEndX();
		BoundedRangeModel brm = new DefaultBoundedRangeModel();
		brm.setMaximum(maxX.getDifference(minX));    		
		brm.setMinimum(0);
		brm.setValue(0);
		brm.addChangeListener(this);
		brm.setExtent(maxX.getDifference(minX));
		bar.setModel(brm);
		bar.addMouseWheelListener(this);
		bar.setPreferredSize(new Dimension(200,20));
		
		p.add(tl);
		p.add(bar);
		this.chartModule.add(p, BorderLayout.NORTH);
		this.chartModule.updateUI();
		this.minX = minX;
	}
	
	// Call this method when the timeline was modified (zoomed in/out) to recalculate values
	public void recalculate() {
		pause = true;
		Chart chart = this.chartModule.getChart();
		BoundedRangeModel brm = bar.getModel(); 
		int day = this.minX.getDifference((TradingDate)chart.getStartX());
		brm.setExtent(0);
		brm.setValue(day);
		brm.setExtent(chart.getSpanDays());
		pause = false;
	}

  //@Override
  public void stateChanged(ChangeEvent arg0) {
    if (!pause)
      this.chartModule.getChart().moveTo(bar.getValue());
  }

  //@Override
  public void mouseWheelMoved(MouseWheelEvent event) {
	  if (event.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
		  BoundedRangeModel brm = bar.getModel();
          int totalScrollAmount = event.getUnitsToScroll();
          brm.setValue(brm.getValue() + totalScrollAmount);
      }
  }

    public int getBarValue() {
	return bar.getValue();
    }

    public void setBarValue(int position) {
	bar.setValue(position);
    }

}