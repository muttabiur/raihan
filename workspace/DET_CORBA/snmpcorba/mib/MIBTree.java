package snmpcorba.mib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MIBTree {
    
    	private static MIBTree instance = null;
    	
    	public static void initMIB(String schemaFilePath) throws MIBException {
    	    instance = new MIBTree(schemaFilePath);
    	}
    	
    	public static MIBTree getInstance() {
    	    return instance;
    	}
	
	private MIBNode mibRootNode = new MIBBranchNode("_ROOT_");
	
	private MIBTree(String schemaFilePath) throws MIBException {
		// call the file tree constructor
		this(new File(schemaFilePath));		
	}

	public MIBTree(File schemaFile) throws MIBException {
		// check if the file is accessible and readible
		if (!schemaFile.exists() || !schemaFile.canRead()) {
			 throw new MIBException("The schema file " + schemaFile.getAbsolutePath() + " is not readible or does not exist.");
		}
		// now read the file line by line and create nodes
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(schemaFile));
		} catch (FileNotFoundException fnfe) {
			// log FNFExc
		}
		// go through the lines and create the nodes
		Pattern validLine = Pattern.compile("^[\\w]+(\\[\\d+\\])?(\\.[\\w]+(\\[\\d+\\])?)*\\s*=\\s\\d+$");
		Pattern commentLine = Pattern.compile("^#\\s*.*$");
		try {
			while (reader.ready()) {
				String input = reader.readLine();
				validLine.matcher(input);
				if (validLine.matcher(input).matches()) {
					// we have a valid line, add it to MIB
					mibRootNode.addNode(input);
				} else if (commentLine.matcher(input).matches()) {
					// comment line - do nothing
					continue;
				} else {
					// error - bad line
					throw new MIBException("File reading exception: Line [" + input + "] does not match a valid input.");
				}
			}
		} catch (Exception exc) {
			exc.printStackTrace();
			throw new MIBException("File reading exception: " + exc.getClass().getSimpleName() + ": " + exc.getMessage());
		}
	}
	
	private Pattern reference = Pattern.compile("^\\w+(\\.\\w+)*(\\..*)$");

	public String getAttributeReference(String input) throws MIBException {
		Matcher refMatcher = reference.matcher(input);
		if (refMatcher.matches()) {
			// if input is valid find the target starting at the root node
			return mibRootNode.findNode(input);
		} else {
			// input not valid
			throw new MIBException("Input [] does not have proper format for attribute reference");
		}	
	}
}
