package DETServer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.FileOutputStream;


public class SearchObject {

    /*This Function is called when we use SNMP-Request method, It will extract the requested parameter info, 
     * they are looking for,  
        And Pass it to the Search Object Function, to do search in the flat file
      */
    public void searchObject(String s) {

	ArrayList<String> extractToken = new ArrayList<String>();
	
	// look at the flat file and open file
	
	try {
	    BufferedReader inputS = new BufferedReader(new FileReader("system.txt"));

	    String line1 = inputS.readLine();

	    while (line1 != null) {

		// System.out.println("The line is:" + line1);

		StringTokenizer tokenizer = new StringTokenizer(line1);

		while (tokenizer.hasMoreTokens()) {
		    
		    String token = tokenizer.nextToken();

		    // add each token to the Arraylist data structure
		    extractToken.add(token);

		    // maybe search function will be here

		    // System.out.println("The Extracted token is :" + token);

		}

		line1 = inputS.readLine();

	    }

	    inputS.close();

	}

	catch (FileNotFoundException e) {
	    System.out.println("The Error is:" + e.getMessage());
	    System.exit(0);
	}

	catch (IOException e) {
	    System.out.println("The Input read error is :" + e.getMessage());
	    System.exit(0);
	}

	// System.out.println("Hi");

	Iterator<String> it = extractToken.iterator();

	while (it.hasNext()) {

	    System.out.println("The New Extracted Token is :" + it.next());

	}

	// Write to file

	PrintWriter outputStream = null;

	try {
	    outputStream = new PrintWriter(new FileOutputStream("Raihan.txt"));

	}

	catch (FileNotFoundException e) {
	    System.out.println("The file is not found:" + e.getMessage());
	    System.exit(0);

    }

	System.out.println("Writing to file......");
	System.out.println("");

	Iterator<String> itt = extractToken.iterator();
	
	while (itt.hasNext()) {
	    // outputStream.write(itt.next());
	    outputStream.println(itt.next());
	}

	outputStream.close();
	
	// do search in the Arraylist for the particular object and replace

	Iterator<String> itFind = extractToken.iterator();
	
	ArrayList<String> matchString = new ArrayList<String>();
	
	//String ra = null ;
	while (itFind.hasNext()) {
	    
	   String ra = itFind.next();
	    
	    if (ra.contentEquals(s)) {
		//System.out.println("All are Equal:" + ra);
		System.out.println(ra);
		//System.out.println("");
	 
		/*String nextString = itFind.next();
		System.out.println("The Next String is :" + nextString);
		*/
		//Write a code that looks for . sign And Extract all the string until encounter .
		
		String nextNextString = itFind.next();
		
		while(!nextNextString.contentEquals(".")) {
		    
		    matchString.add(nextNextString);
		    nextNextString = itFind.next();
		    
		}
		
	    }
	    
	}
    
	Iterator<String> displayString = matchString.iterator();  
	
	//Problem Exists with ra value.
	//System.out.println(ra);
	
	while(displayString.hasNext()) {
	    System.out.println( displayString.next());
	} 
}  
    
    
 /*
    public static void main(String args[]) {

	SearchObject s = new SearchObject();

	s.searchObject("SystemGroup:");
	
	System.out.println("");
	
	s.searchObject("SysDes:");
	System.out.println("");
	
	s.searchObject("sysName:");
	
	
	
	System.exit(0);
    } */
}
