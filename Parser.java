/*
 * Parser.java
 * Miles Croxford 
 * Principles and applications of programming: Web Spider 2012
 * File description: The Parser for the Web Spider, does the following: 
 * 						- Parses the links given by Worker, finds host() and file()
 * 						- Passes them back to Worker
 * 
 * */
package spider;

import java.net.URL;

public class Parser {
	private String finalURL;
	private String hostURL;

	public Parser(String URL) {
		URL aURL;
		try {
			aURL = new URL(URL);
			hostURL = aURL.getHost();
			// add the host + file 
			finalURL = aURL.getProtocol() + "://" + aURL.getHost()
					+ aURL.getFile();
			if (finalURL.endsWith("/")) {
				finalURL = finalURL.substring(0, finalURL.length() - 1);
			}
		} catch (Exception e) {
			System.out.println("Error: " + URL);
		}
	}

	public String getURL() {
		return finalURL;

	}

	public String getHost() {
		return hostURL;
	}

}
