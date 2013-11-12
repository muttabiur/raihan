package DETServer;

import java.util.ArrayList;
import java.util.Iterator;
//import java.util.Scanner;
import java.util.regex.* ;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap ;
/**
 * @author m_sk
 */

// public class DataFile {

// for SNMP
// public String searchObject(String indexNum) {
// public void setObject(String indexNum, String value) {

// for SERVER
// public String searchVariable(Vaiable varname) {
// return searchObject(varname.getId());
// }
// public 
//
//
//
//


enum Variable { 
    BALANCE("4"),
    PRICE("14");
    
    Variable (String itemId) {
	id = itemId;
    }
    private String id;
    public String getId() {
	return id;
    }
};



public class SearchObjectRegularExpression {

    public String searchObject(String indexNum) {
	
	//searchVariable(Variable.BALANCE);


	try {
	    Pattern linePattern = Pattern.compile("^(\\d+)\\s*\\:\\s*(\\w+)$");

	    BufferedReader inputS = new BufferedReader(new FileReader("system1.txt"));

	    String line1 = null;

	    // loop through the lines
	    while ((line1 = inputS.readLine()) != null) {

		// setup the regex matcher
		Matcher matcher = linePattern.matcher(line1);
		String index = null, value = null;

		// check if pattern matches and extract values
		if (matcher.matches()) {
		    index = matcher.group(1);
		    value = matcher.group(2);

		    // return value if found
		    if (indexNum.equals(index)) {
			return value;
		    }
		}
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
	
	return null;
    }

}
