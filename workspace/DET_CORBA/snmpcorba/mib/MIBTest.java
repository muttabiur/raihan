package snmpcorba.mib;

import DETServer.* ;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class MIBTest {

    /**
     * @param args
     */
    public static void main(String[] args) {

	List<String> requests = new ArrayList<String>();
	requests.add("1.3.6.1.5.1");
	requests.add("1.3.6.1.5.1.INDEX_OF_OBJ");

	// setup test database
	File dataFile = writeTestData();

	try {
	    System.out.println("-------------------------- TREE CREATED --------------------------");
	    MIBTree tree = new MIBTree(dataFile);
	    for (String request : requests) {
		System.out.println("-------------------------- TESTING GET " + request + " --------------------------");
		String location = tree.getAttributeReference(request);
		
		//Raihan was checking the location value.
		System.out.println("The localtion is:" + location);
		
		//End of checkLocation
		System.out.println("Requesting " + request + ": " + location);
	    }
	    System.out.println("-------------------------- TESTING DONE --------------------------");

	} catch (Throwable t) {
	    System.out.print("Exception [" + t.getClass().getSimpleName() + "] caught with message: " + t.getMessage());
	    t.printStackTrace();
	}

	// cleanTestData();
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// helper methods ///////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    public static File writeTestData() {
	// declare and check the file
	File outFile = new File("testMIB");
	if (outFile.exists()) {
	    outFile.delete();
	}
	try {
	    System.out.println("Writing test data to: " + outFile.getAbsolutePath());

	    BufferedWriter writer = new BufferedWriter(new FileWriter(new File("testMIB")));

	    writer.write("internet[1].org[3].dod[6].application.test1 = 1\n");
	    writer.write("internet[1].org[3].dod[6].application.test2 = 2\n");
	    writer.write("internet[1].org[3].dod[6].application.test3 = 3\n");
	    writer.write("internet[1].org[3].dod[6].application.test4 = 4\n");
	    writer.write("internet[1].org[3].dod[6].application.data.balance = 5\n");
	    writer.write("internet[1].org[3].dod[6].application.data.def = 6\n");

	    writer.close();

	} catch (Throwable t) {
	    System.out.print("Exception [" + t.getClass().getSimpleName() + "] caught with message: " + t.getMessage());
	    t.printStackTrace();
	}
	return outFile;
    }

    private static void cleanTestData() {
	// declare and check the file
	File outFile = new File("testMIB");
	if (outFile.exists()) {
	    outFile.delete();
	}
	try {
	    if (outFile.exists()) {
		outFile.delete();
	    }
	} catch (Throwable t) {
	    System.out.print("Exception [" + t.getClass().getSimpleName() + "] caught with message: " + t.getMessage());
	    t.printStackTrace();
	}
    }

}
