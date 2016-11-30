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



package nz.org.venice.prefs.settings;

import java.util.HashMap;
import java.util.List;

import javax.swing.JDesktopPane;

import nz.org.venice.chart.graph.BarChartGraph;
import nz.org.venice.chart.graph.BarGraph;
import nz.org.venice.chart.graph.BollingerBandsGraph;
import nz.org.venice.chart.graph.CandleStickGraph;
import nz.org.venice.chart.graph.CustomGraph;
import nz.org.venice.chart.graph.ExpMovingAverageGraph;
import nz.org.venice.chart.graph.FiboGraph;
import nz.org.venice.chart.graph.Graph;
import nz.org.venice.chart.graph.HighLowBarGraph;
import nz.org.venice.chart.graph.KDGraph;
import nz.org.venice.chart.graph.LineGraph;
import nz.org.venice.chart.graph.MACDGraph;
import nz.org.venice.chart.graph.MomentumGraph;
import nz.org.venice.chart.graph.MovingAverageGraph;
import nz.org.venice.chart.graph.MultipleMovingAverageGraph;
import nz.org.venice.chart.graph.OBVGraph;
import nz.org.venice.chart.graph.PointAndFigureGraph;
import nz.org.venice.chart.graph.RSIGraph;
import nz.org.venice.chart.graph.StandardDeviationGraph;
import nz.org.venice.chart.graph.SupportAndResistenceGraph;
import nz.org.venice.chart.source.GraphSource;
import nz.org.venice.chart.source.OHLCVIndexQuoteGraphSource;
import nz.org.venice.chart.source.OHLCVQuoteGraphSource;
import nz.org.venice.chart.source.PortfolioGraphSource;
import nz.org.venice.main.Module;
import nz.org.venice.portfolio.Portfolio;
import nz.org.venice.quote.EODQuoteBundle;
import nz.org.venice.quote.Quote;
import nz.org.venice.quote.Symbol;
import nz.org.venice.util.Locale;


/**
 * This class represents Graph data which can be saved for the purposes
 *  of restoring the modules upon restart.
 * 
 * @author Mark Hummel
 * @see nz.org.venice.prefs.PreferencesManager
*/

public class GraphSettings extends AbstractSettings {
    
    
    //Portfolio Graphs
                                               
    private static final String MARKET_VALUE = Locale.getString("MARKET_VALUE");
    private static final String CASH_VALUE = Locale.getString("CASH_VALUE");
    private static final String SHARE_VALUE = Locale.getString("SHARE_VALUE");
    private static final String RETURN_VALUE = Locale.getString("RETURN_VALUE");
    private static final String STOCKS_HELD = Locale.getString("STOCKS_HELD");

    private HashMap settings;
    private String title;
    private int sourceType;    
    private List settingsSymbolList;

    /**
     *
     * Create new GraphSettings. 
     * 
     * @param   key     The graph Settings Identifier
     * @param   parent  The chart settings identifier
     * @param   title   The title of the graph.
       
     */

    public GraphSettings(String key, String parent, String title) {
	super(Settings.CHART,Settings.GRAPHS, key);
	this.title = title;
    }

    /**
     *
     * Create new GraphSettings. 
     * 
     */

    public GraphSettings() {
	super(Settings.CHART, Settings.GRAPHS);
	
    }
    
    /**
     *
     * Return the internal graph settings 
     * 
     * @return A hashMap representing a set of key-value pairs
     */
    
    public HashMap getSettings() {
	return settings;
    }

    /**
     * 
     * Set the internal graph settings. 
     * 
     * @param settings A hashmap representing the settings data  

     */
    public void setSettings(HashMap settings) {
	this.settings = settings;
    }

   
    /**
     * Return the graph title
     * 
     * @return  The graph title
     */
    public String getTitle() {
	return title;
    }

    /**
     * 
     * Set the graph title
     * 
     * @param   title The graph title  
     */

    public void setTitle(String title) {
	this.title = title;
    }

    /**
     * 
     * Set the graph source type
     * 
     * @param   sourceType The graph source type  
     */

    public void setSourceType(int sourceType) {
	this.sourceType = sourceType;
    }

    /**
     * Return the graph source type
     * 
     * @return  The graph source type
     */
    public int getSourceType() {
	return sourceType;
    }

    /** 
     * Return the symbolList
     * 
     * @return  A list of symbols for this graph
     */    
    public List getSettingsSymbolList() {
	return settingsSymbolList;
    }

    /**
     * Set the symbollist
     *
     * @param settingsSymbolList  A list of symbols for this graph
     */
    public void setSettingsSymbolList(List settingsSymbolList) {
	this.settingsSymbolList = settingsSymbolList;
    }

    //Graph settings are data of the chart module, so nothing is returned here
    public Module getModule(JDesktopPane desktop) {
	return null;
    }

    /**
     * This is factory method which returns a graph represented by this 
     * object's settings.
     * 
     * @return  A graph with these settings.
     */

    public Graph getGraph(EODQuoteBundle bundle) {
	Graph newGraph = null;

	if (title.equals(Locale.getString("BAR_CHART"))) {

	    newGraph = new BarChartGraph(getSource(bundle, Quote.DAY_OPEN),
					 getSource(bundle, Quote.DAY_LOW),
					 getSource(bundle, Quote.DAY_HIGH),
					 getSource(bundle, Quote.DAY_CLOSE));
	    
	}
	
	if (title.equals(Locale.getString("CANDLE_STICK"))) {
	    newGraph = new CandleStickGraph(getSource(bundle, Quote.DAY_OPEN),
					    getSource(bundle, Quote.DAY_LOW),
					    getSource(bundle, Quote.DAY_HIGH),
					    getSource(bundle, Quote.DAY_CLOSE));
					    	    
	}

	if (title.equals(Locale.getString("POINT_AND_FIGURE"))) {
	    newGraph = new PointAndFigureGraph(getSource(bundle, Quote.DAY_CLOSE),
					  settings);
	}

       
	if (title.equals(Locale.getString("HIGH_LOW_BAR"))) {
		newGraph = 
		    new HighLowBarGraph(getSource(bundle, Quote.DAY_LOW),
					getSource(bundle, Quote.DAY_HIGH),
					getSource(bundle, Quote.DAY_CLOSE));
						
	    }

	if (title.equals(Locale.getString("LINE_CHART")) ||
	    title.equals(Locale.getString("DAY_CLOSE"))) {
	    newGraph = new LineGraph(getSource(bundle, Quote.DAY_CLOSE),
				     title,
				     true);
					       
	    
	}

	
	if (title.equals(Locale.getString("DAY_OPEN"))) {
	    newGraph = new LineGraph(getSource(bundle, Quote.DAY_OPEN),
				     title,
				     true);
					       	    
	}
	
	if (title.equals(Locale.getString("DAY_HIGH"))) {
	    newGraph = new LineGraph(getSource(bundle, Quote.DAY_HIGH),
				     title,
				     true);
					       	    
	}

	if (title.equals(Locale.getString("DAY_LOW"))) {
	    newGraph = new LineGraph(getSource(bundle, Quote.DAY_LOW),
				     title,
				     true);
					       	    
	}

	if (title.equals(Locale.getString("VOLUME"))) {
	    newGraph = new LineGraph(getSource(bundle, Quote.DAY_VOLUME),
				     title,
				     true);
					       	    
	}

		
	if (title.equals(Locale.getString("SIMPLE_MOVING_AVERAGE"))) {
	    newGraph = new 
		MovingAverageGraph(getSource(bundle, Quote.DAY_CLOSE), settings);
	}


	if (title.equals(Locale.getString("EXP_MOVING_AVERAGE"))) {
	    newGraph = new 
		ExpMovingAverageGraph(getSource(bundle, Quote.DAY_CLOSE), settings);	    
	}

	
	

	if (title.equals(Locale.getString("BOLLINGER_BANDS"))) {
	    newGraph = new 
		BollingerBandsGraph(getSource(bundle, Quote.DAY_CLOSE),
				    settings);
	}

	if (title.equals(Locale.getString("MOMENTUM"))) {	    	    

	    newGraph = new
		MomentumGraph(getSource(bundle, Quote.DAY_CLOSE),
			       settings);
	    
	}

	if (title.equals(Locale.getString("MULT_MOVING_AVERAGE"))) {
	    newGraph = new
		MultipleMovingAverageGraph(getSource(bundle, Quote.DAY_CLOSE));
	}

	
	if (title.equals((Locale.getString("OBV")))) {
	    
	    newGraph = new
		OBVGraph(getSource(bundle, Quote.DAY_OPEN),
			 getSource(bundle, Quote.DAY_CLOSE),
			 getSource(bundle, Quote.DAY_VOLUME));	    
	}

	if (title.equals(Locale.getString("STANDARD_DEVIATION"))) {
	    newGraph = new 
		StandardDeviationGraph(getSource(bundle, Quote.DAY_CLOSE),
				       settings);
		
	}
	
	if (title.equals(Locale.getString("MACD"))) {
	    newGraph = new
		MACDGraph(getSource(bundle, Quote.DAY_CLOSE),
			  settings);
	}

	if (title.equals(Locale.getString("RSI"))) {
	    newGraph = new
		RSIGraph(getSource(bundle, Quote.DAY_CLOSE),
			 settings);
	}

	if (title.equals(Locale.getString("SUPPORT_AND_RESISTENCE"))) {
	    newGraph = new 
		SupportAndResistenceGraph(getSource(bundle, Quote.DAY_CLOSE),
					  settings);
	}

	if (title.equals(Locale.getString("FIBO_CHART"))) {
	    newGraph = new 
		FiboGraph(getSource(bundle, Quote.DAY_CLOSE),
			  settings);
	}
	
	if (title.equals(Locale.getString("CUSTOM"))) {
	    //Eventually, custom graph will be able to graph
	    //groups of symbols, so the problem of just getting the first symbol
	    //is temporary.
	    Symbol symbol = (Symbol)settingsSymbolList.get(0);
	    newGraph = new
		CustomGraph(getSource(bundle, Quote.DAY_CLOSE),
			    symbol,
			    bundle,
			    settings);
	}
	if (title.equals(Locale.getString("KD"))) {
	    newGraph = new
		KDGraph(getSource(bundle, Quote.DAY_LOW),getSource(bundle, Quote.DAY_HIGH),getSource(bundle, Quote.DAY_CLOSE),
			    settings);
	}
	assert newGraph != null;

	return newGraph;

    }

    public Graph getGraph(EODQuoteBundle bundle, Portfolio portfolio) {
	
	Graph newGraph = null;	
	PortfolioGraphSource portfolioGraphSource;
	
	
	if (title.equals(MARKET_VALUE)) {
	    portfolioGraphSource = new PortfolioGraphSource(portfolio, 
							    bundle,
							    PortfolioGraphSource.MARKET_VALUE);
	    
	    newGraph = new LineGraph(portfolioGraphSource, 
				     Locale.getString("MARKET_VALUE"),
				     true);
	    
	} else if (title.equals(RETURN_VALUE)) {
	    portfolioGraphSource = new PortfolioGraphSource(portfolio, 
							    bundle,
							    PortfolioGraphSource.RETURN_VALUE);
	    
	    newGraph = new LineGraph(portfolioGraphSource, RETURN_VALUE, true);
	} else if (title.equals(CASH_VALUE)) {
	    
	    portfolioGraphSource = new PortfolioGraphSource(portfolio, 
							    bundle,
							    PortfolioGraphSource.CASH_VALUE);
	    newGraph = new LineGraph(portfolioGraphSource, CASH_VALUE, true);
	} else if (title.equals(SHARE_VALUE)) {
	    portfolioGraphSource = new PortfolioGraphSource(portfolio, 
							    bundle,
							    PortfolioGraphSource.SHARE_VALUE);
	    newGraph = new LineGraph(portfolioGraphSource, SHARE_VALUE, true);
	} else if (title.equals(STOCKS_HELD)) {
	    portfolioGraphSource = new PortfolioGraphSource(portfolio, 
							    bundle,
							    PortfolioGraphSource.STOCKS_HELD);
	    newGraph = new BarGraph(portfolioGraphSource, STOCKS_HELD, false);
	} else {
	    //Graph is an account.
	    portfolioGraphSource = new PortfolioGraphSource(portfolio, 
							    bundle,
							    title);
	    newGraph = new LineGraph(portfolioGraphSource, title, false);
	}
	
	return newGraph;
    }
    
    private GraphSource getSource(EODQuoteBundle bundle, int quoteType) {

	switch (sourceType) {
	case GraphSource.SYMBOL:
	case GraphSource.PORTFOLIO:
	    assert (quoteType >= Quote.DAY_CLOSE ||
		    quoteType <= Quote.DAY_VOLUME);
	    return new OHLCVQuoteGraphSource(bundle, quoteType);
	case GraphSource.INDEX:
	    return new OHLCVIndexQuoteGraphSource(bundle, quoteType);
	default:
	    return null;
	}
    }
}

