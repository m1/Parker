/*
 * Config.java
 * Miles Croxford 
 * Principles and applications of programming: Web Spider 2012
 * File description: The configuration file for the Web Spider, does the following functions and operations:
 * 						- Checks if the config exists, if it doesn't, make one using defaults
 * 						- If there is a config, read it and make methods to pass them to other classes
 * 
 * */
package spider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Config {
	String filePath = "sys/config.txt";
	private boolean verbose;
	private int threadNumLimit;
	private int requestsLimit;
	private int timeToRun;
	private int visitsLimit;
	private String url;

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public int getThreadNumLimit() {
		return threadNumLimit;
	}

	public void setThreadNumLimit(int threadNumLimit) {
		this.threadNumLimit = threadNumLimit;
	}

	public int getRequestsLimit() {
		return requestsLimit;
	}

	public void setRequestsLimit(int requestsLimit) {
		this.requestsLimit = requestsLimit;
	}

	public int getTimeToRun() {
		return timeToRun;
	}

	public void setTimeToRun(int timeToRun) {
		this.timeToRun = timeToRun;
	}

	public int getVisitsLimit() {
		return visitsLimit;
	}

	public void setVisitsLimit(int visitsLimit) {
		this.visitsLimit = visitsLimit;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public Config() {
		String next;
		String str;
		int parasPassed = 0;
		boolean validConfig = true;
		File file = new File(filePath);
		if (file.exists()) {
			try {
				Scanner s = new Scanner(file);

				while (s.hasNextLine() && validConfig) {
					next = s.nextLine().replace(" ", "");
					
					// ignore comments
					while (next.startsWith("#")) {
						next = s.nextLine().replace(" ", "");
					}

					if (next.contains("threadNumLimit:")) {
						str = next.replace("threadNumLimit:", "");
						if (isNum(str)) {
							setThreadNumLimit(Integer.parseInt(str));
							parasPassed++;
						} else {
							validConfig = false;
						}
					}
					if (next.contains("url:")) {
						str = next.replace("url:", "");
						// Check if valid url
						try {
							Document doc = Jsoup.connect(str).get();
							setUrl(str);
							parasPassed++;
						} catch (IOException e) {
							validConfig = false;
						}
					}
					if (next.contains("verbose:")) {
						str = next.replace("verbose:", "");
						if (str.equalsIgnoreCase("no")) {
							setVerbose(false);
							parasPassed++;
						} else if (str.equalsIgnoreCase("yes")) {
							setVerbose(true);
							parasPassed++;
						} else {
							validConfig = false;
						}
					}
					if (next.contains("vistsLimit:")) {
						str = next.replace("vistsLimit:", "");
						if (isNum(str)) {
							setVisitsLimit(Integer.parseInt(str));
							parasPassed++;
						} else {
							validConfig = false;
						}
					}
					if (next.contains("timeToRun:")) {
						str = next.replace("timeToRun:", "");
						if (isNum(str)) {
							setTimeToRun(Integer.parseInt(str));
							parasPassed++;
						} else {
							validConfig = false;
						}
					}
					if (next.contains("requestsLimit:")) {
						str = next.replace("requestsLimit:", "");
						if (isNum(str)) {
							setRequestsLimit(Integer.parseInt(str));
							parasPassed++;
						} else {
							validConfig = false;
						}
					}
				}
				if (!validConfig || parasPassed < 5) {
					//something went wrong with the original config (not enough parameters passed or unvalid values given
					makeDefault();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else if (!file.exists()) {
			makeDefault();
		}
	}

	public void makeDefault() {
		final boolean d_verbose = true;
		final int d_threadNumLimit = 15;
		final int d_requestsLimit = 1000;
		final int d_visitsLimit = 500;
		final int d_timeToRun = 50;
		final String d_url = "http://www.google.com";
		setVerbose(d_verbose);
		setThreadNumLimit(d_threadNumLimit);
		setRequestsLimit(d_requestsLimit);
		setVisitsLimit(d_visitsLimit);
		setTimeToRun(d_timeToRun);
		setUrl(d_url);

		String text = "# Must be in this format [option: parameter]"
					+ "\n# Parameter options and description:"
					+ "\n#"
					+ "\n#	verbose: [yes|no]"
					+ "\n#		verbose prints all output to the terminal"
					+ "\n#		Example:"
					+ "\n#"
					+ "\n#	threadNumLimit: [int > 0)]"
					+ "\n#		The limit to the number of threads used by the spider."
					+ "\n#"
					+ "\n#	requestsLimit: [int >= 0 (0 indicates no limit)]"
					+ "\n#		The limit to the number of pages requested, whether or not they were valid or not."
					+ "\n#"
					+ "\n#	visitsLimit: [int >= 0 (0 indicates no limit)]"
					+ "\n#		The limit to the number of pages visited."
					+ "\n#"
					+ "\n#	timeToRunLimit: [int (in Minutes) >= 0 (indicates no limit)]"
					+ "\n#		The limit to the time the spider runs for."
					+ "\nverbose: " + ((d_verbose) ? "yes" : "no")
					+ "\nthreadNumLimit: " + d_threadNumLimit 
					+ "\nrequestsLimit: "+ d_requestsLimit
					+ "\nvistsLimit: " + d_visitsLimit
					+ "\ntimeToRun: " + d_timeToRun;
		File fi;
		fi = new File(filePath);
		try {
			fi.createNewFile();
			FileWriter fstream = new FileWriter(fi);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(text);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isNum(String str) {
		try {
			Integer.parseInt(str);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
