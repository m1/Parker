/*
 * Parker.java
 * Miles Croxford 
 * Principles and applications of programming: Web Spider 2012
 * File description: The main file for the Web Spider, does the following operations:	
 * 						- Constructor for the web spider	
 * 						- Controls the configuration
 * 						- Controls the threaded crawlers.
 * 						- Contains the queue and visited data
 * 						- Contains and prints the stats data
 * 						- Executes the threads
 * 						- Passes URL to the crawler
 * 
 * */
package spider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Parker {
	private ExecutorService executor;
	private String url;
	private Set<String> queue = new HashSet<String>();
	private Set<String> visited = new HashSet<String>();
	private HashMap<String, Integer> stats = new HashMap<String, Integer>();
	private int threadNumLimit;
	private boolean verbose;
	private int requestsLimit;
	private int vistsLimit;
	private int timeToRun;
	private double timeRan;

	public Parker() {
		configController();
		setupCrawl();
	}

	public Parker(String url) {
		configController();
		this.url = url; // overwrite config url
		if (verbose) {
			System.out.println("Using configuration");
			System.out.println("URL: " + this.url);
			System.out.println("Verbose: " + this.verbose);
			System.out.println("Thread Limit: " + this.threadNumLimit);
			System.out.println("Requests Limit: " + this.requestsLimit);
			System.out.println("Visits Limit: " + this.vistsLimit);
			System.out.println("Time to run limit: " + this.timeToRun);
		}
		System.out.println("\nStarting crawler on " + url);
		System.out.println("\n-------------------------------------");

		setupCrawl();
	}

	// Incomplete *
	public Parker(String url, boolean verbose, int threadNumLimit,
			int requestsLimit, int vistsLimit, int timeToRun) {
		this.verbose = verbose;
		this.requestsLimit = requestsLimit;
		this.url = url;
		setupCrawl();
	}
	// Incomplete *
	
	public void setupCrawl() {
		Worker worker;
		// First worker to fill queue
		try {
			worker = new Worker(url, verbose);
			queue.addAll(worker.getLinks());
			stats.putAll(worker.getTempStats());
			visited.add(url);
			queue.remove(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// start threads
		timeRan = System.currentTimeMillis();
		executor = Executors.newFixedThreadPool(threadNumLimit);
		crawl(requestsLimit);
		executor.shutdown();
		
		// Wait for timeToRun before moving on
		try {
			executor.awaitTermination(timeToRun, TimeUnit.MINUTES);
		} catch (InterruptedException e) {

		}
		printStats();
	}

	public void crawl(int limit) {
		for (int i = 0; i < limit; i++) {
			executor.submit(new Crawler());

		}
	}
	public void setVerbose(boolean bool) {
		this.verbose = bool;
	}

	public void setThreadNumLimit(int num) {
		this.threadNumLimit = num;
	}

	public void setRequestsLimit(int num) {
		this.requestsLimit = num;
	}

	public void setVisitsLimit(int num) {
		this.vistsLimit = num;
	}

	public void setTimeToRun(int num) {
		this.timeToRun = num;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean getVerbose() {
		return this.verbose;
	}

	public synchronized void setVisitedSingle(String url) {
		visited.add(url);
	}

	public synchronized Set<String> getVisitedAll() {
		return visited;
	}

	public synchronized void removeSingleUrl(String url) {
		queue.remove(url);
	}

	public synchronized Set<String> getQueueAll() {
		return queue;
	}

	public synchronized HashMap<String, Integer> getStatsAll() {
		return stats;
	}
	
	public void configController() {
		Config config = new Config();
		setThreadNumLimit(config.getThreadNumLimit());
		setRequestsLimit(config.getRequestsLimit());
		setTimeToRun(config.getTimeToRun());
		setVerbose(config.isVerbose());
		setVisitsLimit(config.getVisitsLimit());
		setUrl(config.getUrl());
	}

	public void getStats() {
		// It through stats data
		System.out.println("\nPage Stats [Links to Host : Host]");
		Iterator<Entry<String, Integer>> it = stats.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Integer> pairs = it.next();
			if (!pairs.getKey().toString().isEmpty()) {
				System.out.println(pairs.getValue()
						+ ((pairs.getValue() > 1) ? " links"
								: " link") + " - http://" + pairs.getKey());
			}
		}
	}
	public void printStats() {
		if (verbose) {
			System.out.println("\n-------------------------------------");
			System.out.println("\nCrawling on " + url + " done.");
			System.out.println("Stats:");
			System.out.println("Requests made: " + requestsLimit);
			System.out.println("Pages visited: " + visited.size());
			System.out.println("Time taken: "
					+ (System.currentTimeMillis() - timeRan) / 1000
					+ " seconds");
			System.out.println("Threads used: " + threadNumLimit);
			System.out.println("Pages in the queue: " + queue.size());
			System.out.println("\n-------------------------------------");
			getStats();
		}
	}

	public class Crawler implements Runnable {
		private Worker worker;
		
		public synchronized void getStats() {
			// merge temp stats and this.stats
			Iterator<Entry<String, Integer>> it = getStatsAll().entrySet()
					.iterator();
			Iterator<Entry<String, Integer>> itWorker = worker.getTempStats()
					.entrySet().iterator();
			int visits;
			while (it.hasNext() || itWorker.hasNext()) {
				Entry<String, Integer> pairsWorker = itWorker.next();

				// If temp key already exists in this.stats
				if (stats.containsKey(pairsWorker.getKey())) {
					
					// Add this.stats value and temp stats value
					visits = stats.get(pairsWorker.getKey())
							+ pairsWorker.getValue();
					stats.put(pairsWorker.getKey(), visits);
				} 
				// If it doesn't, create it.
				else if (!stats.containsKey(pairsWorker.getKey())) {
					stats.put(pairsWorker.getKey(), 1);
				}
			}
		}
		public void run() {
			setVisitedSingle(url);
			Iterator<String> it = getQueueAll().iterator();
			String url = it.next();

			while (getVisitedAll().contains(url)) {
				removeSingleUrl(url);
				url = it.next();
				it = getQueueAll().iterator();
			}
			if (!getVisitedAll().contains(url)) {
				setVisitedSingle(url);

				try {
					worker = new Worker(url, verbose);
					queue.addAll(worker.getLinks());

				} catch (Exception e) {
					queue.remove(url);
				}
				queue.remove(url);
				getStats();
			} else {
				System.out.println("visited");
				queue.remove(url);
			}
		}
	}
}
