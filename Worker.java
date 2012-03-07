/*
 * Worker.java
 * Miles Croxford 
 * Principles and applications of programming: Web Spider 2012
 * File description: The worker file for the Web Spider, does the following:
 * 						- Make temp data structures for queues and stats, also make methods to pass them to other classes.
 * 						- Crawls the URL given by Crawler
 * 						- Finds links using JSoup and adds them to the temp queue.
 * 
 * */
package spider;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import java.net.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Worker {
	private Set<String> tempQueue = new HashSet<String>();
	private HashMap<String, Integer> tempStats = new HashMap<String, Integer>();

	public Worker(String url, boolean verbose) throws Exception {
		Document doc;
		doc = Jsoup.connect(url).get();
		// select anchors with href only
		Elements links = doc.select("a[href]");
		String l_Href;
		String host;
		int linksNum;
		Parser parser;
		for (Element link : links) {
			// absolute = http:// added
			l_Href = link.attr("abs:href");
			if (!l_Href.isEmpty()) {
				parser = new Parser(l_Href);
				host = parser.getHost();
				// if tempStats contains the url, add one to the value
				if (tempStats.containsKey(host)) {
					linksNum = tempStats.get(host);
					tempStats.put(host, linksNum += 1);
				} 
				// if it doesn't, add it

				else {
					tempStats.put(host, 1);
				}
				// parse the url
				tempQueue.add(parser.getURL());
			}
		}
		if (verbose) {
			System.out.println(Thread.currentThread().getName() + " : "
					+ tempQueue.size() + " links from " + url);
		}
	}

	public Set<String> getLinks() {
		return tempQueue;
	}

	public HashMap<String, Integer> getTempStats() {
		return tempStats;
	}

}
