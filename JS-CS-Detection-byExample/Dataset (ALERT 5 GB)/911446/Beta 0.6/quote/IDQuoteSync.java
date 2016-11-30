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

package org.mov.quote;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.mov.ui.DesktopManager;
import org.mov.util.TradingDate;
import org.mov.util.TradingTime;

/**
 * This class controls the periodic downloading, or synchronisation, of new
 * intra-day quotes. This class will only sync quotes if all the following
 * conditions are met:
 * <ul><li>Quote sync is enabled</li>
 * <li>There are symbols to sync</li>
 * <li>We are currently between the start and stop times</li>
 * <li>We are not on a weekend</li>
 * </ul>
 *
 * @author Andrew Leppard
 */
public class IDQuoteSync {
    
    /**
     * This class contains the function that is periodically called to download
     * new intra-day quotes.
     */
    private class Sync extends TimerTask {

        // List of symbols to download
        private List symbols;

        // Quote cache to store quotes
        private IDQuoteCache quoteCache;

        /**
         * Create a new object to periodically download intra-day quotes.
         */
        public Sync(List symbols) {
            assert symbols.size() > 0;
            this.symbols = symbols;
            quoteCache = IDQuoteCache.getInstance();
        }

        /**
         * Download the current intra-day quotes.
         */
        public void run() {
            try {
                List quotes = YahooIDQuoteImport.importSymbols(symbols);

                quoteCache.load(quotes);
                
                //System.out.println("---------");
                //for(Iterator iterator = quotes.iterator(); iterator.hasNext();) 
                //    System.out.println(iterator.next());
            }
            catch(ImportExportException e) {
                // If an error message is already up, then don't display the error.
                // This prevents us spamming the user with error messages every
                // sync period.
                if (!DesktopManager.isDisplayingMessage())
                    DesktopManager.showErrorMessage(e.getMessage());
            }
        }
    }

    /**
     * This class contains the function that starts the automatic quote sync.
     */
    private class StartSync extends TimerTask {
        
        // Quote sync to start
        private IDQuoteSync idQuoteSync;

        /**
         * Create a new object to start the automatic quote sync.
         *
         * @param idQuoteSync the quote sync module.
         */
        public StartSync(IDQuoteSync idQuoteSync) {
            this.idQuoteSync = idQuoteSync;
        }

        /**
         * Start automatic quote sync.
         */
        public void run() {
            idQuoteSync.startSyncTimer();
        }        
    }
 
    /**
     * This class contains the function that starts the automatic quote sync.
     */
    private class StopSync extends TimerTask {
        
        // Quote sync to stop
        private IDQuoteSync idQuoteSync;
        
        /**
         * Createa  new object to stop the automatic quote sync.
         *
         * @param idQuoteSync the quote sync module.
         */
        public StopSync(IDQuoteSync idQuoteSync) {
            this.idQuoteSync = idQuoteSync;
        }

        /**
         * Stop automatic quote sync.
         */
        public void run() {
            idQuoteSync.stopSyncTimer();
        }
    }
 
    /** The default time period inbetween quote downloads. */
    public final static int DEFAULT_PERIOD = 60;
    
    /** The default start time. */
    public final static TradingTime DEFAULT_START_TIME = new TradingTime(9, 0, 0); // 9am

    /** The default stop time. */
    public final static TradingTime DEFAULT_STOP_TIME = new TradingTime(16, 0, 0); // 4pm

    // Number of milliseconds in one day
    private final static int MILLISECONDS_IN_DAY = (TradingTime.HOURS_IN_DAY *
                                                    TradingTime.MINUTES_IN_HOUR *
                                                    TradingTime.SECONDS_IN_MINUTE *
                                                    TradingTime.MILLISECONDS_IN_SECOND);

    // Singleton instance of this class
    private static IDQuoteSync instance = null;

    // List of symbols to import
    private List symbols;

    // Status of whether the quote sync is enabled
    private boolean isEnabled;

    // Period, in seconds, between quote sync
    private int period;

    // Timer which schedules quote syncs
    private Timer syncTimer;

    // Timer which starts the quote sync
    private Timer startTimer;

    // Timer which stops the quote sync
    private Timer stopTimer;

    // Time to start sync, time to stop sync
    private TradingTime startTime;
    private TradingTime stopTime;

    /**
     * Create a new intra-day quote synchoronisation object.
     */
    private IDQuoteSync() {
        symbols = new ArrayList();
        isEnabled = false;
        period = DEFAULT_PERIOD;
        syncTimer = null;
        startTime = DEFAULT_START_TIME;
        stopTime = DEFAULT_STOP_TIME;
        startTimer = null;
        stopTimer = null;
    }

    /**
     * Create or return the singleton instance of the intra-day quote synchronisation object.
     *
     * @return  singleton instance of this class
     */
    public static synchronized IDQuoteSync getInstance() {
	if(instance == null)
	    instance = new IDQuoteSync();

        return instance;
    }

    /**
     * Set whether the automatic downloading of intra-day quotes is enabled.
     *
     * @param isEnabled enabled status
     */
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;

        if(isEnabled) {
            startStartStopTimers();
            startSyncTimer();
        }
        else {
            stopStartStopTimers();
            stopSyncTimer();
        }
    }
    
    /**
     * Return whether the automatic downloading of intra-day quotes is enabled.
     *
     * @return <code>true</code> if enabled.
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Return whether the automatic downloading of intra-day quotes is running.
     * This function does not return whether we are currently in the processing
     * of downloading new quotes. Instead it returns whether the timer is currently
     * active and we are periodically downloading new intra-day quotes.
     *
     * @return <code>true</code> if running.
     */
    public boolean isRunning() {
        return(syncTimer != null);
    }

    /**
     * Return the list of symbols of the intra-day quotes to download.
     *
     * @return symbols list
     */
    public List getSymbols(List symbols) {
        return symbols;
    }

    /**
     * Add the a list of symbols to the list of symbols for the intra-day quotes
     * to download.
     *
     * @param symbols new symbols to download
     */
    public void addSymbols(List symbols) {
        boolean isNewSymbol = false;

        // Add the new unique symbols
        for(Iterator iterator = symbols.iterator(); iterator.hasNext();) {
            Symbol symbol = (Symbol)iterator.next();
            if(!this.symbols.contains(symbol)) {
                this.symbols.add(symbol);
                isNewSymbol = true;
            }
        }

        // Restart sync task so that it has the updated symbol list
        if(isNewSymbol)
            restartSyncTimer();
    }

    /**
     * Set the time period inbetween quote downloads
     *
     * @param period the period in seconds.
     */
    public void setPeriod(int period) {
        assert period > 0;

        if(period != this.period) {
            assert period != 0;
            this.period = period;
            restartSyncTimer();
        }
    }

    /**
     * Set the time range to sync quotes.
     *
     * @param startTime start quote sync
     * @param stopTime stop quote sync
     */
    public void setTimeRange(TradingTime startTime, TradingTime stopTime) {
        assert startTime != null && stopTime != null;

        this.startTime = startTime;
        this.stopTime = stopTime;
        restartStartStopTimers();

        // This will cancel the sync timer if we are not between the
        // correct time range.
        restartSyncTimer();
    }

    /**
     * Start the sync timer that triggers the quote download.
     */
    private synchronized void startSyncTimer() {
        TradingDate today = new TradingDate();
        TradingTime now = new TradingTime();

        // Don't start up timer if:
        // * Syncing is disabled OR
        // * Syncing is not already running OR
        // * There are no symbols to download OR
        // * We are not between the start/end times OR
        // * Today is on a weekend.
        if(isEnabled &&
           syncTimer == null &&
           symbols.size() > 0 &&
           (!now.before(startTime) && !now.after(stopTime)) &&
           !today.isWeekend()) {

            syncTimer = new Timer();
            syncTimer.scheduleAtFixedRate(new Sync(symbols),
                                          0,
                                          period * TradingTime.MILLISECONDS_IN_SECOND);
        }
    }

    /**
     * Stop the sync timer that triggers the quote download.
     */
    private synchronized void stopSyncTimer() {
        if(syncTimer != null) {
            syncTimer.cancel();
            syncTimer = null;
        }
    }

    /**
     * Restart the sync timer that triggers the quote download.
     */
    private synchronized void restartSyncTimer() {
        stopSyncTimer();
        startSyncTimer();
    }

    /**
     * Start the timers that start and stop the quote download.
     */
    private synchronized void startStartStopTimers() {
        TradingDate today = new TradingDate();
        
        // Start timers to occur once per day each
        if(startTimer == null) {
            startTimer = new Timer();
            startTimer.scheduleAtFixedRate(new StartSync(this),
                                           today.toDate(startTime),
                                           MILLISECONDS_IN_DAY);
        }

        if(stopTimer == null) {
            stopTimer = new Timer();
            stopTimer.scheduleAtFixedRate(new StopSync(this),
                                          today.toDate(stopTime),
                                          MILLISECONDS_IN_DAY);
        }
    }

    /**
     * Stop the timers that start and stop the quote download.
     */
    private synchronized void stopStartStopTimers() {
        // Stop timers
        if(startTimer != null) {
            startTimer.cancel();
            startTimer = null;
        }
        if(stopTimer != null) {
            stopTimer.cancel();
            stopTimer = null;
        }
    }

    /**
     * Restart the timers that start and stop the quote download.
     */
    private synchronized void restartStartStopTimers() {
        stopStartStopTimers();
        startStartStopTimers();
    }
}

