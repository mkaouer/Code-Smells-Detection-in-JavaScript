package com.crawljax.core;

import com.crawljax.browser.BrowserPool;
import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.condition.browserwaiter.WaitConditionChecker;
import com.crawljax.condition.crawlcondition.CrawlConditionChecker;
import com.crawljax.condition.eventablecondition.EventableConditionChecker;
import com.crawljax.condition.invariant.Invariant;
import com.crawljax.core.configuration.CrawlSpecificationReader;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.configuration.CrawljaxConfigurationReader;
import com.crawljax.core.plugin.CrawljaxPluginsUtil;
import com.crawljax.core.state.Eventable;
import com.crawljax.core.state.StateFlowGraph;
import com.crawljax.oraclecomparator.StateComparator;
import com.crawljax.plugins.aji.JSModifyProxyPlugin;

import net.jcip.annotations.GuardedBy;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The Crawljax Controller class is the core of Crawljax.
 *
 * @author mesbah
 * @author amin
 * @version $Id: CrawljaxController.java 446 2010-09-16 09:17:24Z slenselink@google.com $
 */
public class CrawljaxController implements CrawlQueueManager {

	/**
	 *  Added by Amin: 
	 *  diverseCrawling: set it true if you wish to do diverse crawling. The default value is false.
	 *  efficientCrawling: set it true if you wish to do diverse crawling. The default value is false.
	 *  JSCountList: keeping track of executed lines of all js
	 *  fstream: used to write stats about coverage and crawled states, etc.
	 */
	private ArrayList<Crawler> waitingCrawlerList = new ArrayList<Crawler>();
	private boolean diverseCrawling = false;
	private boolean efficientCrawling = false;
	private boolean randomEventExec = false;
	private Map<String,ArrayList<Integer>> JSCountList = new Hashtable<String,ArrayList<Integer>>(); 
	private FileWriter fstream;
	private BufferedWriter out;
	private double coverage = 0.0;

	
	// Amin: keeping track of executed lines of a js	
	public synchronized void setCountList(String modifiedJS, Object counter){
		ArrayList<Integer> countList = new ArrayList<Integer>();
		ArrayList c = (ArrayList) counter;
		countList.clear(); // used as a temp list to be added to JSCountList

		if (!JSCountList.containsKey(modifiedJS)){ // if not exist add new js to the JSCountList	
			for (int i=0;i<c.size();i++)
				countList.add(((Long)c.get(i)).intValue());
			JSCountList.put(modifiedJS, countList);
		}else{ // update JSCountList
			for (int i: JSCountList.get(modifiedJS))
				countList.add(i);
			for (int i=0;i<c.size();i++)
				countList.set(i,countList.get(i)+((Long)c.get(i)).intValue());
			JSCountList.put(modifiedJS, countList);
		}
	}

	// Amin: compute code coverage
	public double getCoverage(boolean printToFile){

		try {
			if (printToFile){
				this.fstream = new FileWriter("SmellReport.txt", true);
				this.out = new BufferedWriter(fstream);
			}

			//double coverage = 0.0;
			int totalExecutedLines = 0, totalLines = 0;

			for (String modifiedJS : JSModifyProxyPlugin.getModifiedJSList()){
				if (JSCountList.containsKey(modifiedJS)){
					totalLines += JSCountList.get(modifiedJS).size();
					int executedLines = 0;

					LOGGER.info(" List of " + modifiedJS + " is: " + JSCountList.get(modifiedJS));
					if (printToFile)
						out.write(" List of " + modifiedJS + " is: " + JSCountList.get(modifiedJS) + "\n");

					for (int i: JSCountList.get(modifiedJS))
						if (i>0){
							totalExecutedLines++;
							executedLines++;
						}

					LOGGER.info("List of " + modifiedJS + " # lines ececuted: " + executedLines + " # tolal lines: " + JSCountList.get(modifiedJS).size() + " - code coverage: " + (double)executedLines/(double)JSCountList.get(modifiedJS).size()*100+"%\n");
					if (printToFile)
						out.write("List of " + modifiedJS + " # lines ececuted: " + executedLines + " # tolal lines: " + JSCountList.get(modifiedJS).size() + " - code coverage: " + (double)executedLines/(double)JSCountList.get(modifiedJS).size()*100+"%\n");
				}
			}

			long timeCrawlCalc = System.currentTimeMillis() - startCrawl;

			coverage = (double)totalExecutedLines/(double)totalLines;

			LOGGER.info("Time: " + formatRunningTime(timeCrawlCalc) + " - code coverage: " + coverage*100+"%");
			if (printToFile){
				out.write("Time: " + formatRunningTime(timeCrawlCalc) + " - code coverage: " + coverage*100+"%" + "\n");
				out.close();
			}

		}catch(Exception e){
			LOGGER.info("IO exception!");
			e.printStackTrace();
		}
		return coverage;
	}	

	
	public boolean isDiverseCrawling() {
		return diverseCrawling;
	}
	public boolean isEfficientCrawling() {
		return efficientCrawling;
	}
	public boolean isRandomEventExec() {
		return randomEventExec;
	}

	
	
	
	private static final Logger LOGGER = Logger.getLogger(CrawljaxController.class.getName());

	private CrawlSession session;

	private long startCrawl;

	private final StateComparator stateComparator;
	private final CrawlConditionChecker crawlConditionChecker;
	private final EventableConditionChecker eventableConditionChecker;

	private final WaitConditionChecker waitConditionChecker = new WaitConditionChecker();

	// TODO Stefan, Can not be final because, must be created after the loading of the plugins
	private Crawler initialCrawler;

	private final CrawljaxConfigurationReader configurationReader;

	private final List<Invariant> invariantList;

	/**
	 * Central thread starting engine.
	 */
	private final CrawlerExecutor workQueue;

	private final CandidateElementManager elementChecker;

	private final BrowserPool browserPool;
	
	private boolean domMutationNotifierPluginCheck =false;
	public void setDomMutationNotifierPluginCheck(boolean value) {
		
		this.domMutationNotifierPluginCheck = value;
		
	}
	public boolean getDomMutationNotifierPluginCheck()
	{
		return this.domMutationNotifierPluginCheck;
	}
		
	/**
	 * @param config
	 *            the crawljax configuration.
	 * @throws ConfigurationException
	 *             if the configuration fails.
	 */
	public CrawljaxController(final CrawljaxConfiguration config) throws ConfigurationException {
		this.diverseCrawling = config.getCrawlSpecification().isDiverseCrawling();
		this.efficientCrawling = config.getCrawlSpecification().isEfficientCrawling();
		this.randomEventExec = config.getCrawlSpecification().isRandomEventExec();
		
		configurationReader = new CrawljaxConfigurationReader(config);
		CrawlSpecificationReader crawlerReader =
		        configurationReader.getCrawlSpecificationReader();
		
		this.domMutationNotifierPluginCheck =  crawlerReader.getDomMutationNotifierPluginCheck();


		stateComparator = new StateComparator(crawlerReader.getOracleComparators());
		invariantList = crawlerReader.getInvariants();
		crawlConditionChecker = new CrawlConditionChecker(crawlerReader.getCrawlConditions());
		waitConditionChecker.setWaitConditions(crawlerReader.getWaitConditions());
		eventableConditionChecker =
		        new EventableConditionChecker(configurationReader.getEventableConditions());

		elementChecker =
		        new CandidateElementManager(eventableConditionChecker, crawlConditionChecker);

		browserPool = new BrowserPool(configurationReader);
		

		workQueue = init();
	}

	/**
	 * @throws ConfigurationException
	 *             if the configuration fails.
	 * @NotThreadSafe
	 */
	private CrawlerExecutor init() throws ConfigurationException {
		LOGGER.info("Starting Crawljax...");

		LOGGER.info("Used plugins:");
		CrawljaxPluginsUtil.loadPlugins(configurationReader.getPlugins());

		if (configurationReader.getProxyConfiguration() != null) {
			CrawljaxPluginsUtil.runProxyServerPlugins(
			        configurationReader.getProxyConfiguration());
		}

		LOGGER.info("Embedded browser implementation: " + configurationReader.getBrowser());

		LOGGER.info("Number of threads: "
		        + configurationReader.getThreadConfigurationReader().getNumberThreads());

		LOGGER.info(
		        "Crawl depth: " + configurationReader.getCrawlSpecificationReader().getDepth());
		LOGGER.info("Crawljax initialized!");


		int numberOfThreads = configurationReader.getThreadConfigurationReader().getNumberThreads();
		
		// Amin: Check if is diverse crawling, it needs another thread (DiverseCrawlingManager)
		// NOTE: This is removed since we do not used multi-threading for diverse crawling at this version
		//if (diverseCrawling)
		//	numberOfThreads++; // need for another thread (DiverseCrawlingManager)*/
		
		return new CrawlerExecutor(numberOfThreads);
	}

	
	
	/**
	 * Added by Amin
	 * Add work (Crawler) to the Queue of work that need to be done.
	 *
	 * @param c
	 *            the c (Crawler) to add to the crawlerList
	 */
	public void addToWaitingCrawlerList(Crawler c){
		waitingCrawlerList.add(c);
	}
	
	/**
	 * Added by Amin
	 * Checks if all crawlers are waiting. 
	 */	
	public boolean allCrawlersWaiting(){
		if ( waitingCrawlerList.size() == configurationReader.getThreadConfigurationReader().getNumberThreads())
			return true;
		return false;
	}
	
	/**
	 * Added by Amin
	 * Checks if all browsers are opened. 
	 */	
	public boolean allBrowsersOpened(){
		System.out.println("activeBrowserCount: " + CrawljaxPluginsUtil.activeBrowserCount);
		if ( CrawljaxPluginsUtil.activeBrowserCount == configurationReader.getThreadConfigurationReader().getNumberBrowsers())
			return true;
		return false;
	}	
	
	
	
	
	/**
	 * Run Crawljax.
	 *
	 * @throws CrawljaxException
	 *             If the browser cannot be instantiated.
	 * @throws ConfigurationException
	 *             if crawljax configuration fails.
	 * @NotThreadSafe
	 */
	public final void run() throws CrawljaxException, ConfigurationException {
		//Amin: This is removed since we do not used multi-threading for diverse crawling at this version
		//DiverseCrawlingManager diverseCrawlingManager = new DiverseCrawlingManager(this);
		//if (diverseCrawling){
		//	workQueue.execute(diverseCrawlingManager);
		//}
		
		startCrawl = System.currentTimeMillis();

		LOGGER.info(
		        "Start crawling with " + configurationReader.getAllIncludedCrawlElements().size()
		                + " crawl elements");

		// Create the initailCrawler
		initialCrawler = new InitialCrawler(this);
	
		// Start the Crawling by adding the initialCrawler to the the workQueue.
		addWorkToQueue(initialCrawler);

		
		try {
			// Block until the all the jobs are done
			workQueue.waitForTermination();
			
			// Amin: Terminating the diverseCrawlingManager. This is removed since we do not used multi-threading for diverse crawling at this version
			//if (diverseCrawling)
			//	diverseCrawlingManager.finishedWorking();
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		if (workQueue.isAborted()) {
			LOGGER.warn("It apears to be that the workQueue was Aborted, "
			        + "not running postcrawling plugins and not closing the browsers");
			return;
		}
		
		long timeCrawlCalc = System.currentTimeMillis() - startCrawl;
		
		/**
		 * Close all the opened browsers, this is run in separate thread to have the post crawl
		 * plugins to execute in the meanwhile.
		 */
		Thread shutdownThread = browserPool.close();

		// TODO Stefan; Now we "re-request" a browser instance for the PostCrawlingPlugins Thread,
		// this is not ideal...
		EmbeddedBrowser b = null;
		try {
			b = this.getBrowserPool().requestBrowser();
		} catch (InterruptedException e1) {
			LOGGER.warn("Re-Request for a browser was interrupted", e1);
		}
		CrawljaxPluginsUtil.runPostCrawlingPlugins(session);
		this.getBrowserPool().freeBrowser(b);

		this.shutdown(timeCrawlCalc);

		try {
			shutdownThread.join();
		} catch (InterruptedException e) {
			LOGGER.error("could not wait for browsers to close.", e);
		}
		
	}

	/**
	 * Retrieve the current session, there is only one session active at a time. So this method by
	 * it self is Thread-Safe but actions on the session are NOT!
	 *
	 * @return the session
	 */
	public CrawlSession getSession() {
		return session;
	}

	/**
	 * Add work (Crawler) to the Queue of work that need to be done. The class is thread-safe.
	 *
	 * @param work
	 *            the work (Crawler) to add to the Queue
	 */
	public final void addWorkToQueue(Crawler work) {
		workQueue.execute(work);
	}

	/**
	 * Removes this Crawler from the workQueue if it is present, thus causing it not to be run if it
	 * has not already started.
	 *
	 * @param crawler
	 *            the Crawler to remove
	 * @return true if the crawler was removed
	 */
	public boolean removeWorkFromQueue(Crawler crawler) {
		return workQueue.remove(crawler);
	}

	/**
	 * Wait for a given condition. This call is thread safe as the underlying object is thread-safe.
	 *
	 * @param browser
	 *            the browser which requires a wait condition
	 */
	public final void doBrowserWait(EmbeddedBrowser browser) {
		this.waitConditionChecker.wait(browser);
	}

	/**
	 * TODO Stefan: Remove this synchronization; performance loss is huge! no synchrnization fails
	 * because ThreadLocal is not ThreadSafe??? get the stripped version of the dom currently in the
	 * browser. This call is thread safe, must be synchronised because there is thread-intefearing
	 * bug in the stateComparator.
	 *
	 * @param browser
	 *            the browser instance.
	 * @return a stripped string of the DOM tree taken from the browser.
	 */
	public synchronized String getStrippedDom(EmbeddedBrowser browser) {
		return this.stateComparator.getStrippedDom(browser);
	}

	/**
	 * @deprecated use the {@link #getInitialCrawler()} instead, does exactly the same.
	 * @return the crawler used to initiate the Crawling run.
	 */
	@Deprecated
	public final Crawler getCrawler() {
		return getInitialCrawler();
	}

	/**
	 * Retrieve the initial Crawler used.
	 *
	 * @return the initialCrawler used to initiate the Crawling run.
	 */
	public final Crawler getInitialCrawler() {
		return initialCrawler;
	}

	/**
	 * Format the time the current crawl run has taken into a more readable format. Taking now as
	 * the end time of the crawling.
	 *
	 * @return the formatted time in X min, X sec layout.
	 */
	private String formatRunningTime() {
		return formatRunningTime(System.currentTimeMillis() - startCrawl);
	}

	/**
	 * Format the time the current crawl run has taken into a more readable format.
	 *
	 * @param timeCrawlCalc
	 *            the time to display
	 * @return the formatted time in X min, X sec layout.
	 */
	private String formatRunningTime(long timeCrawlCalc) {
		return String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(timeCrawlCalc),
		        TimeUnit.MILLISECONDS.toSeconds(timeCrawlCalc) - TimeUnit.MINUTES.toSeconds(
		                TimeUnit.MILLISECONDS.toMinutes(timeCrawlCalc)));
	}

	/**
	 * Terminate the crawling, Stop all threads this will cause the controller which is sleeping to
	 * reactive and do the final work....
	 *
	 * @param isAbort
	 *            if set true the terminate must be as an abort not allowing running PostCrawling
	 *            plugins.
	 */
	@GuardedBy("this")
	public final synchronized void terminate(boolean isAbort) {
		LOGGER.warn("After " + this.formatRunningTime()
		        + " the crawling process was requested to terminate @ " + Thread.currentThread());
		browserPool.shutdown();
		workQueue.shutdownNow(isAbort);
		this.shutdown(System.currentTimeMillis() - startCrawl);
	}

	/**
	 * The general shutdown procedure without running plugins or using browsers.
	 */
	private void shutdown(long timeCrawlCalc) {
		StateFlowGraph stateFlowGraph = this.getSession().getStateFlowGraph();
		for (Eventable c : stateFlowGraph.getAllEdges()) {
			LOGGER.info("Interaction Element= " + c.toString());
		}
		LOGGER.info("Total Crawling time(" + timeCrawlCalc + "ms) ~= "
		        + formatRunningTime(timeCrawlCalc));
		LOGGER.info("EXAMINED ELEMENTS: " + elementChecker.numberOfExaminedElements());
		LOGGER.info("CLICKABLES: " + stateFlowGraph.getAllEdges().size());
		LOGGER.info("STATES: " + stateFlowGraph.getAllStates().size());
		LOGGER.info("Dom average size (byte): " + stateFlowGraph.getMeanStateStringSize());
		LOGGER.info("DONE!!!");
		
		// Amin: Calling getCoverage to write coverage report into the SmellReport.txt file 
		getCoverage(true);
		
	}

	/**
	 * The current element checker in use. This call is thread-safe because it returns a final
	 * field.
	 *
	 * @return the elementChecker used to register the checked elements.
	 */
	public final ExtractorManager getElementChecker() {
		return elementChecker;
	}

	/**
	 * @return the configurationReader
	 */
	public CrawljaxConfigurationReader getConfigurationReader() {
		return configurationReader;
	}

	/**
	 * @return the browser pool.
	 */
	public BrowserPool getBrowserPool() {
		return browserPool;
	}

	/**
	 * Return the used CrawlQueueManager, this method is designed for extension purposes. Being able
	 * to move the {@link #addWorkToQueue(Crawler)} and {@link #removeWorkFromQueue(Crawler)} out of
	 * this class using the interface.
	 *
	 * @return the crawlQueueManager that is used.
	 */
	public CrawlQueueManager getCrawlQueueManager() {
		return this;
	}

	/**
	 * @return the invariantList
	 */
	public final List<Invariant> getInvariantList() {
		return invariantList;
	}

	/**
	 * Install a new CrawlSession.
	 *
	 * @param session
	 *            set the new value for the session
	 */
	public void setSession(CrawlSession session) {
		this.session = session;
	}

	/**
	 * @return the startCrawl
	 */
	public final long getStartCrawl() {
		return startCrawl;
	}

	
	public void waitForTermination() throws InterruptedException {
		this.workQueue.waitForTermination();
	}

}
