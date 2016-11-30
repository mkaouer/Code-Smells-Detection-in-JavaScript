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

/** 
 * Generate graphs from their sources depending on the graph name, 
 * Wether the source is an index and wether the data should be adjusted
 * for dividends or splits.
 * 
 * @author Mark Hummel
 */

package nz.org.venice.chart;

import nz.org.venice.chart.graph.*;
import nz.org.venice.chart.source.GraphSource;
import nz.org.venice.chart.source.OHLCVIndexQuoteGraphSource;
import nz.org.venice.chart.source.OHLCVQuoteGraphSource;
import nz.org.venice.chart.source.Adjustment;

import nz.org.venice.quote.Quote;
import nz.org.venice.quote.EODQuoteBundle;
import nz.org.venice.quote.Symbol;

import nz.org.venice.util.Locale;

public class GraphFactory {

    
    private static final int DAY_OPEN = 0;
    private static final int DAY_HIGH = 1;
    private static final int DAY_LOW = 2;
    private static final int DAY_CLOSE = 3;    
    private static final int VOLUME = 4;

    private static final int sourceIndeces[] = {DAY_OPEN, DAY_HIGH, DAY_LOW, DAY_CLOSE, VOLUME};

    private static final int SOURCE_LEN = 5; //OHLCV

    /**
     * Create a new graph according to the name and using the supplied symbol and quote bundle.
     * 
     * @param graphName The name of the graph type to create. (e.g. Moving Avg)
     * @param index If the graph is to be an index graph
     * @param quoteBundle the end of day bundle containg quote data
     * @param symbol The symbol of the graph.
     * @return a new Graph
     */

    public static Graph newGraph(String graphName, boolean index, EODQuoteBundle quoteBundle, Symbol symbol) {
	return newGraph(graphName, index, quoteBundle, symbol, null);
    }

    /**
     * Create a new graph according to the name and using the supplied symbol and quote bundle.
     * @param graphName The name of the graph type to create. (e.g. Moving Avg)
     * @param index If the graph is to be an index graph
     * @param quoteBundle the end of day bundle containg quote data
     * @param symbol The symbol of the graph
     * @param adjust An optional adjustment (for splits etc) applied to the data before graphing.
     * @return a new Graph
     */
    public static Graph newGraph(String graphName, 
				 boolean index,
				 EODQuoteBundle quoteBundle,
				 Symbol symbol,
				 Adjustment adjust) {

	Graph graph = null;

	GraphSource[] sources = getSources(graphName, quoteBundle, index, adjust);
	       
        if(graphName == Locale.getString("BAR_CHART")) {
	    graph = new BarChartGraph(sources[DAY_OPEN],
				      sources[DAY_HIGH],
				      sources[DAY_LOW],
				      sources[DAY_CLOSE]);	

	} else if(graphName == Locale.getString("BOLLINGER_BANDS")) {
            graph = new BollingerBandsGraph(sources[DAY_CLOSE]);
	    
        } else if(graphName == Locale.getString("CANDLE_STICK")) {
            graph = new CandleStickGraph(sources[DAY_OPEN], sources[DAY_LOW], sources[DAY_HIGH], sources[DAY_CLOSE]);
        
	} else if(graphName == Locale.getString("COUNTBACK_LINE")) {
	    graph = new CountbackLineGraph(sources[DAY_LOW], 
					   sources[DAY_HIGH],
					   sources[DAY_CLOSE]);

        } else if(graphName == Locale.getString("CUSTOM")) {
            graph = new CustomGraph(sources[DAY_CLOSE], symbol, quoteBundle);

        } else if(graphName == Locale.getString("DAY_HIGH")) {
            graph = new LineGraph(sources[DAY_HIGH], graphName, true);
	
        } else if(graphName == Locale.getString("DAY_LOW")) {
            graph = new LineGraph(sources[DAY_LOW], graphName, true);
	
        } else if(graphName == Locale.getString("DAY_OPEN")) {
            graph = new LineGraph(sources[DAY_OPEN], graphName, true);

        } else if(graphName == Locale.getString("VOLUME")) {
            graph = new BarGraph(sources[VOLUME], graphName, false);
	
        } else if(graphName == Locale.getString("EXP_MOVING_AVERAGE")) {
            graph = new ExpMovingAverageGraph(sources[DAY_CLOSE]);

        } else if(graphName == Locale.getString("HIGH_LOW_BAR")) {
            graph = new HighLowBarGraph(sources[DAY_LOW], sources[DAY_HIGH], sources[DAY_CLOSE]);

        } else if(graphName == Locale.getString("MACD")) {
        	graph = new MACDGraph(sources[DAY_CLOSE]);

        } else if(graphName == Locale.getString("KD")) {
            graph = new KDGraph(sources[DAY_LOW], sources[DAY_HIGH], sources[DAY_CLOSE]);

        } else if(graphName == Locale.getString("MOMENTUM")) {
            graph = new MomentumGraph(sources[DAY_CLOSE]);

	} else if(graphName == Locale.getString("MULT_MOVING_AVERAGE")) {
	    graph = new MultipleMovingAverageGraph(sources[DAY_CLOSE]);
	
        } else if(graphName == Locale.getString("OBV")) {
            graph = new OBVGraph(sources[DAY_OPEN], sources[DAY_CLOSE], sources[VOLUME]);

        } else if(graphName == Locale.getString("POINT_AND_FIGURE"))  {
            graph = new PointAndFigureGraph(sources[DAY_CLOSE]);
	    	    
	} else if (graphName == Locale.getString("SUPPORT_AND_RESISTENCE")) {
	    graph = new SupportAndResistenceGraph(sources[DAY_CLOSE]);

	} else if (graphName == Locale.getString("FIBO_CHART"))  {
	    graph = new FiboGraph(sources[DAY_CLOSE]);

        } else if(graphName == Locale.getString("RSI")) {
            graph = new RSIGraph(sources[DAY_CLOSE]);
	    
        } else if (graphName == Locale.getString("SIMPLE_MOVING_AVERAGE")) {
            graph = new MovingAverageGraph(sources[DAY_CLOSE]);

        } else if (graphName == Locale.getString("STANDARD_DEVIATION")) {
            graph = new StandardDeviationGraph(sources[DAY_CLOSE]);
	    
	} else {
            assert(graphName == Locale.getString("LINE_CHART"));
            graph = new LineGraph(sources[DAY_CLOSE], graphName, true);
        }

        // Make sure we did the right graphName -> graph mapping.
	if (graph != null) {
	    assert graphName == graph.getName();
	}

        return graph;
	
    }

    private static GraphSource[] getSources(String graphName, 
					    EODQuoteBundle quoteBundle,
					    boolean index, 
					    Adjustment adjust) {
	GraphSource[] sources = new GraphSource[SOURCE_LEN];
	
	sources[DAY_OPEN] = null;
	sources[DAY_HIGH] = null;
	sources[DAY_LOW] = null;
	sources[DAY_CLOSE] = null;
	sources[VOLUME] = null;

	if(graphName == Locale.getString("BAR_CHART")) {
	    sources[DAY_OPEN] = getDayOpen(quoteBundle, index);
	    sources[DAY_HIGH] = getDayHigh(quoteBundle, index);
	    sources[DAY_LOW] = getDayLow(quoteBundle, index);
	    sources[DAY_CLOSE] = getDayClose(quoteBundle, index);

	    	
        } else if(graphName == Locale.getString("BOLLINGER_BANDS")) {
	    sources[DAY_CLOSE] = getDayClose(quoteBundle, index);
	
        } else if(graphName == Locale.getString("CANDLE_STICK")) {
	    sources[DAY_OPEN] = getDayOpen(quoteBundle, index);
	    sources[DAY_HIGH] = getDayHigh(quoteBundle, index);
	    sources[DAY_LOW] = getDayLow(quoteBundle, index);
	    sources[DAY_CLOSE] = getDayClose(quoteBundle, index);
	} else if(graphName == Locale.getString("COUNTBACK_LINE")) {
	    sources[DAY_HIGH] = getDayHigh(quoteBundle, index);
	    sources[DAY_LOW] = getDayLow(quoteBundle, index);
	    sources[DAY_CLOSE] = getDayClose(quoteBundle, index);
	} else if(graphName == Locale.getString("CUSTOM")) {
	    sources[DAY_CLOSE] = getDayClose(quoteBundle, index);

        } else if(graphName == Locale.getString("DAY_HIGH")) {
	    sources[DAY_HIGH] = getDayHigh(quoteBundle, index);

	} else if(graphName == Locale.getString("DAY_LOW")) {
	    sources[DAY_LOW] = getDayLow(quoteBundle, index);

        } else if(graphName == Locale.getString("DAY_OPEN")) {
	    sources[DAY_OPEN] = getDayOpen(quoteBundle, index);

	} else if(graphName == Locale.getString("VOLUME")) {
	    sources[VOLUME] = getVolume(quoteBundle, index);

        } else if(graphName == Locale.getString("EXP_MOVING_AVERAGE")) {
	    sources[DAY_CLOSE] = getDayClose(quoteBundle, index);


        } else if(graphName == Locale.getString("HIGH_LOW_BAR")) {
	    sources[DAY_HIGH] = getDayHigh(quoteBundle, index);
	    sources[DAY_LOW] = getDayLow(quoteBundle, index);
	    sources[DAY_CLOSE] = getDayClose(quoteBundle, index);
	    
	} else if(graphName == Locale.getString("MACD")) {
	    sources[DAY_CLOSE] = getDayClose(quoteBundle, index);

        } else if(graphName == Locale.getString("KD")) {
	    sources[DAY_HIGH] = getDayHigh(quoteBundle, index);
	    sources[DAY_LOW] = getDayLow(quoteBundle, index);
	    sources[DAY_CLOSE] = getDayClose(quoteBundle, index);

        } else if(graphName == Locale.getString("MOMENTUM")) {
	    sources[DAY_CLOSE] = getDayClose(quoteBundle, index);

	} else if(graphName == Locale.getString("MULT_MOVING_AVERAGE")) {
	    sources[DAY_CLOSE] = getDayClose(quoteBundle, index);
	
        } else if(graphName == Locale.getString("OBV")) {
	    sources[DAY_OPEN] = getDayOpen(quoteBundle, index);
	    sources[DAY_CLOSE] = getDayClose(quoteBundle, index);
	    sources[VOLUME] = getVolume(quoteBundle, index);

        } else if(graphName == Locale.getString("POINT_AND_FIGURE"))  {
	    sources[DAY_CLOSE] = getDayClose(quoteBundle, index);
	    	    
	} else if (graphName == Locale.getString("SUPPORT_AND_RESISTENCE")) {
	    sources[DAY_CLOSE] = getDayClose(quoteBundle, index);

	} else if (graphName == Locale.getString("FIBO_CHART")) {	    
	    sources[DAY_CLOSE] = getDayClose(quoteBundle, index);

        } else if(graphName == Locale.getString("RSI")) {	    
	    sources[DAY_CLOSE] = getDayClose(quoteBundle, index);

        } else if(graphName == Locale.getString("SIMPLE_MOVING_AVERAGE")) {
	    sources[DAY_CLOSE] = getDayClose(quoteBundle, index);

        } else if(graphName == Locale.getString("STANDARD_DEVIATION")) {
	    sources[DAY_CLOSE] = getDayClose(quoteBundle, index);

	} else {
            assert(graphName == Locale.getString("LINE_CHART"));	    
	    sources[DAY_CLOSE] = getDayClose(quoteBundle, index);
        }

	if (adjust != null) {
	    for (int i = 0; i < SOURCE_LEN; i++) {

		//Volume can't be affected by splits/dividends
		if (i == VOLUME) {
		    continue;
		}
		
		if (sources[i] != null) {
		    sources[i].adjust(adjust.getType(),
				      adjust.getValue(), 
				      adjust.getStartPoint(),
				      adjust.getDirection());
		    
		}
	    }
	}

	return sources;
    }


    /* These could be replaced with a single getQuoteType up in getSources
       but was done like this for historical reasons and readability.
       Also, I expect the compiler to optimize away the redundant calls.
    */

    private static GraphSource getDayOpen(EODQuoteBundle quoteBundle,
					  boolean index) {
	return getQuoteType(quoteBundle, Quote.DAY_OPEN, index);
    }

    private static GraphSource getDayHigh(EODQuoteBundle quoteBundle, boolean index) {
	return getQuoteType(quoteBundle, Quote.DAY_HIGH, index);
    }

    private static GraphSource getDayLow(EODQuoteBundle quoteBundle, boolean index) {
	return getQuoteType(quoteBundle, Quote.DAY_LOW, index);
    }

    private static GraphSource getDayClose(EODQuoteBundle quoteBundle, boolean index) {
	return getQuoteType(quoteBundle, Quote.DAY_CLOSE, index);
    }

    private static GraphSource getVolume(EODQuoteBundle quoteBundle, boolean index) {
	return getQuoteType(quoteBundle, Quote.DAY_VOLUME, index);
    }
    
    private static GraphSource getQuoteType(EODQuoteBundle quoteBundle,
					    int quoteType,		  
					    boolean index) {
	
	if (index) {
	    return new OHLCVIndexQuoteGraphSource(quoteBundle, quoteType);
	} else {
	    return new OHLCVQuoteGraphSource(quoteBundle, quoteType);
	}					           
    }
}