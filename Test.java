/*
 * Test.java
 * Miles Croxford 
 * Principles and applications of programming: Web Spider 2012
 * File description: Test file for the Web Spider, just simply declares a new spider passing the URL http://www.bbc.co.uk
 * 
 * */

package spider;

public class Test {
	public static void main(String[] args) {
		// Create new Web Spider, only passing URL
		Parker peter = new Parker("http://www.bbc.co.uk");
	}
}
