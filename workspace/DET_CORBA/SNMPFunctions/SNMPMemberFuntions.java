package SNMPFunctions;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import DETApp.DET;



import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import snmpcorba.mib.* ;

//import DETApp.DET;
import DETApp.DETHelper;
//import DETClient.* ;
//import Interface.* ;
import DETServer.* ;

/*
 * Design By Contract
 * This Class Contains all the SNMP Operations
 * All the Mutator and Accessor  methods are implemented here.
 * This class works as a Template for our SNMP-Based Distributed Electronic Trader System.
 * Written-By "Raihan-Pawel-Wang"
 */
public class SNMPMemberFuntions {


    public String snmpversionNumber = "SNMPV1" ;
    public String communityName = "Public" ;
    public int requestID = 0 ;
    static DET detImpl;

    MIBTest newTest = new MIBTest();
    
   
    
    public void getRequest(String version, String commName, int reqID, String argument){

	/*Check all the Necessary Parameters, 
	 * just to authenticate at the Application Layer
	 */


	SearchObjectRegularExpression search = new SearchObjectRegularExpression();
	
	if((snmpversionNumber.equalsIgnoreCase(version)) && (communityName.equalsIgnoreCase(commName))){

	    SNMPMemberFuntions snmpcal = new SNMPMemberFuntions();
	   
	    String mValue = null ;
	    
	    /*
	    String index = null;
	    try {
		index = MIBTree.getInstance().getAttributeReference(argument);
	    } 
	    
	    catch (MIBException mibe) {
		System.out.println(mibe.getMessage());
	    }
	    */
	    File dataFile = newTest.writeTestData();
	    
	    String location = null ;
	    try {
	    MIBTree tree = new MIBTree(dataFile);
	    location = tree.getAttributeReference(argument);
	    }
	    
	    catch(Throwable t){
		System.out.println(t.getMessage());
	    }
	    mValue = search.searchObject( location);
	    
	    System.out.println("");
	    snmpcal.getResponse(version, commName, reqID, mValue) ;
	    System.out.println("");

	}

	else
	{
	    //This below message will be encapsulated in the Response message 

	    System.out.println("Access Denied!");
	}


    }
    
    public void getResponse(String version, String commName, int reqID, String argumenResponse) {

	System.out.println("###################### Begin of Response Message#####################");
	System.out.println("Get-Response Message from Agent:");

	System.out.println("The SNMP-Version is:" + version);
	System.out.println("The Community-Name is:" + commName);
	System.out.println("The Request-ID is:" + reqID);
	//In the Response object, i need to store the return value.
	System.out.println("The ResponseObjectFromMibTree is:" + argumenResponse) ;
	System.out.println("###################### End of Response Message#####################");
    }

    
    public void getNextRequest(String version, String commName, int reqID, String argument) {

	/*Check all the Necessary Parameters, 
	 * just to authenticate at the Application Layer
	 * This function's Implementation will be illustrated later, it is the hardest part.
	 */

	if((snmpversionNumber.equalsIgnoreCase(version)) && (communityName.equalsIgnoreCase(commName))){

	    //Then Process the Request.
	    //The output will be encapsulated in the Response message
	}

	else
	{
	    //This below message will be encapsulated in the Response message 

	    System.out.println("Access Denied!");
	}

    }



    public void setRequestForQuantity(String versionName, String commName, int reqID, String argument) {

	/*Check all the Necessary Parameters, 
	 * just to authenticate at the Application Layer
	 */

	String Naminghost="localhost";

	//Remote hosts are defined here
	//String tradeHost1="localhost";
	//String tradeHost2="localhost";

	try{
	    String name="DETCOMPUTER";

	    // create and initialize the ORB
	    String arg1[]={"-ORBInitialHost", Naminghost,"-ORBInitialPort", "1234"};

	    ORB orb = ORB.init(arg1, null);


	    // get the root naming context
	    org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");



	    NamingContextExt ncRef =  NamingContextExtHelper.narrow(objRef);

	    detImpl = DETHelper.narrow(ncRef.resolve_str(name));

	}//End of try block

	catch(Exception e){
	    System.out.println("Exception in testClient: " + e);
	} //End of catch block

	if((snmpversionNumber.equalsIgnoreCase(versionName)) && (communityName.equalsIgnoreCase(commName))){
	    SNMPMemberFuntions snmpcal = new SNMPMemberFuntions();
	    //Then Process the Request.
	    //The output will be encapsulated in the Response message
	    //The balance set up info is stored in main memory, Probably i need to store in flat file.
	   // double bal = 0.0 ;
	    //bal = detImpl.SnmpSetBalance();
	    int quantity = 0 ;
	    quantity = detImpl.SnmpSetQuantity();
	   // double qt = Double.parseDouble(quantity); 
	    //Call SNMP Response Message 
	    snmpcal.getResponseForSetRequestQuantity(versionName, commName, reqID, quantity);

	}

	else
	{
	    //This below message will be encapsulated in the Response message 

	    System.out.println("Access Denied!");
	}

    }
    
    public void getResponseForSetRequestQuantity(String version, String commName, int reqID, int argumenResponse) {

	System.out.println("###################### Begin of Response Message#####################");
	System.out.println("Get-Response Message from Agent Regarding Quantity Information Detail:");

	System.out.println("The SNMP-Version is:" + version);
	System.out.println("The Community-Name is:" + commName);
	System.out.println("The Request-ID is:" + reqID);
	//In the Response object, i need to store the return value.
	System.out.println("The New Quantity is:" + argumenResponse) ;
	System.out.println("###################### End of Response Message#####################");
    }
    
    
    public void setRequestForBalance(String versionName, String commName, int reqID, String argument) {

	/*Check all the Necessary Parameters, 
	 * just to authenticate at the Application Layer
	 */

	// Initialization of three different items

	//String url="";
	//String itm1="Computer";
	//String itm2="Shoe";

	//String SysDes ;


	//int qty=1;

	String Naminghost="localhost";

	//Remote hosts are defined here
	//String tradeHost1="localhost";
	//String tradeHost2="localhost";

	try{
	    String name="DETCOMPUTER";

	    // create and initialize the ORB
	    String arg1[]={"-ORBInitialHost", Naminghost,"-ORBInitialPort", "1234"};

	    ORB orb = ORB.init(arg1, null);


	    // get the root naming context
	    org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");



	    NamingContextExt ncRef =  NamingContextExtHelper.narrow(objRef);

	    detImpl = DETHelper.narrow(ncRef.resolve_str(name));

	}//End of try block

	catch(Exception e){
	    System.out.println("Exception in testClient: " + e);
	} //End of catch block

	if((snmpversionNumber.equalsIgnoreCase(versionName)) && (communityName.equalsIgnoreCase(commName))){
	    SNMPMemberFuntions snmpcal = new SNMPMemberFuntions();
	    //Then Process the Request.
	    //The output will be encapsulated in the Response message
	    //The balance set up info is stored in main memory, Probably i need to store in flat file.
	   double bal = 0.0 ;
	    bal = detImpl.SnmpSetBalance();
	    //int quantity = 0 ;
	    //quantity = detImpl.SnmpSetQuantity();
	   // double qt = Double.parseDouble(quantity); 
	    //Call SNMP Response Message 
	    snmpcal.getResponseForSetRequestBalance(versionName, commName, reqID, bal);

	}

	else
	{
	    //This below message will be encapsulated in the Response message 

	    System.out.println("Access Denied!");
	}

    }
    
    public void getResponseForSetRequestBalance(String version, String commName, int reqID, double argumenResponse) {

	System.out.println("###################### Begin of Response Message#####################");
	System.out.println("Get-Response Message from Agent Regarding Balance Information Detail:");

	System.out.println("The SNMP-Version is:" + version);
	System.out.println("The Community-Name is:" + commName);
	System.out.println("The Request-ID is:" + reqID);
	//In the Response object, i need to store the return value.
	System.out.println("The New Balance is:" + argumenResponse) ;
	System.out.println("###################### End of Response Message#####################");
    }
   

   
    public void trapSendForBalance(String version, String commName, String agentAddress) {


	//String itm1="Computer";
	//String itm2="Shoe";

	//String SysDes ;


	//int qty=1;

	String Naminghost="localhost";

	//Remote hosts are defined here
	//String tradeHost1="localhost";
	//String tradeHost2="localhost";

	try{
	    String name="DETCOMPUTER";

	    // create and initialize the ORB
	    String arg1[]={"-ORBInitialHost", Naminghost,"-ORBInitialPort", "1234"};

	    ORB orb = ORB.init(arg1, null);


	    // get the root naming context
	    org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");

	    // Use NamingContextExt instead of NamingContext, 
	    // part of the Interoperable naming Service.  

	    NamingContextExt ncRef =  NamingContextExtHelper.narrow(objRef);


	    detImpl = DETHelper.narrow(ncRef.resolve_str(name));

	}//End of try block

	catch(Exception e){
	    System.out.println("Exception in testClient: " + e);
	} //End of catch block

	detImpl.trapBalance() ;
	SNMPMemberFuntions snmpcal = new SNMPMemberFuntions();


	String trapGeneric = detImpl.getTrapNameGeneric();

	snmpcal.getResponseTrap(version, commName, agentAddress, trapGeneric);

	System.out.println("");
	String trapSpecific = detImpl.getTrapNameSpecific();


	snmpcal.getResponseTrap(version, commName, agentAddress, trapSpecific);



    }
    
    public void trapSendForQuantity(String version, String commName, String agentAddress) {


	//String itm1="Computer";
	//String itm2="Shoe";

	//String SysDes ;


	//int qty=1;

	String Naminghost="localhost";

	//Remote hosts are defined here
	//String tradeHost1="localhost";
	//String tradeHost2="localhost";

	try{
	    String name="DETCOMPUTER";

	    // create and initialize the ORB
	    String arg1[]={"-ORBInitialHost", Naminghost,"-ORBInitialPort", "1234"};

	    ORB orb = ORB.init(arg1, null);


	    // get the root naming context
	    org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");

	    // Use NamingContextExt instead of NamingContext, 
	    // part of the Interoperable naming Service.  

	    NamingContextExt ncRef =  NamingContextExtHelper.narrow(objRef);


	    detImpl = DETHelper.narrow(ncRef.resolve_str(name));

	}//End of try block

	catch(Exception e){
	    System.out.println("Exception in testClient: " + e);
	} //End of catch block

	detImpl.trapQuantity();
	SNMPMemberFuntions snmpcal = new SNMPMemberFuntions();


	//String trapGeneric = detImpl.getTrapNameGeneric();

	//snmpcal.getResponseTrap(version, commName, agentAddress, trapGeneric);

	System.out.println("");
	String trapSpecific = detImpl.getTrapNameSpecific();


	snmpcal.getResponseTrap(version, commName, agentAddress, trapSpecific);



    }

    public void getResponseTrap(String version, String commName, String host, String trapMessage) {

	System.out.println("###################### Begin of TrapResponse Message#####################");
	System.out.println("Get-Response Message from Agent Regarding Trap Information:");

	System.out.println("The SNMP-Version is:" + version);
	System.out.println("The Community-Name is:" + commName);
	System.out.println("The HostName is:" + host);
	//In the Response object, i need to store the return value.
	System.out.println("The TrapName is:" + trapMessage) ;
	System.out.println("###################### End of TrapResponse Message#####################");
    }




}