package DETServer;

import DETApp.DET;



import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;


import DETApp.DETHelper;
import DETClient.* ;

import SNMPFunctions.* ;
//import DETServer.* ;

/**
 * @author m_sk
 */
public class Agent_Computer {

    static DET detImpl;
   
    public void monitorDetComputerServerBeforeTransaction() {

	System.out.println("The Current Information about ServerComputer befor any transaction Happens!");

	System.out.println("DETCOMPUTER Server's Initial: " + "BalanceInfo:" + "" + " QuantityInfo:" + ""+ " PriceInfo:"+"" + " ManagedItemInfo:");
	//use SNMP GET-Request function to achieve this, This Function will use Search Object Function as Well.
	//Server information will be stored in the flat file.
	//Agent Send message to the server
	System.out.println("");
	//Call SNMP get-Request and get the parameter that are looking for and pass to the search function

	MapIntToString map = new MapIntToString();

	//SearchObject so = new SearchObject();

	// I expect the get-Request function will give me a String parameters
	//Acquiring the last char from the String
	String value = "1.3.6.1.4.1.9.5" ;
	int length = value.length();

	Character c = new Character(value.charAt(length - 1));


	String mValue = null ;
	System.out.println("");
	System.out.println("Testing DETCOMPUTER Server's Price information:");

	mValue = map.mapIntergerToString(c);
	//so.searchObject(mValue);


	//System.out.println("");
	//System.out.println("Testing DETCOMPUTER Server's Quantity information:");
	String value1 = "1.3.6.1.4.1.9.6" ;
	int length1 = value1.length();

	Character ch = new Character(value1.charAt(length1 - 1));


	String mValue1  = null ;
	System.out.println("");
	System.out.println("Testing DETCOMPUTER Server's Quantity information:");

	mValue1 = map.mapIntergerToString(ch);
	//so.searchObject(mValue1);

	//SNMP Response will contain itemName, i need to construct the response message

	//Check another attribute of DETShoe server

	//System.out.println("");
	//System.out.println("Testing DETCOMPUTER Server's Balance information:");
	//System.out.println("");

	//Acquiring the last char from the String
	//I will get this following String from the get-request parameters
	String value2 = "1.3.6.1.4.1.9.7" ;
	int length2 = value2.length();

	Character ch2 = new Character(value2.charAt(length2 - 1));


	String mValue2 = null ;
	System.out.println("");
	System.out.println("Testing DETCOMPUTER Server's Balance information:");

	mValue2 = map.mapIntergerToString(ch2);
	//so.searchObject(mValue2);
    }



    public void monitorDetComputerServerAfterTransaction() {

	//System.out.println("The Current Information about ServerComputer After any transaction Happens!");
	//System.out.println("DETCOMPUTER Server's Initial: " + "BalanceInfo:" + "" + " QuantityInfo:" + ""+ " PriceInfo:"+"" + " ManagedItemInfo:");
	//May not use SNMP GET-Request use print function from the server implementation class
	//May Be I Can Write to file also.
	System.out.println("");

    }


    /**
     * Design By Contract
     * Precondition: This function expects no input from the user
     * PostCondition: This function will create new threads object.
     * 
     */

    public static void main(String args[]) {




	// Initialization of three different items

	//String url="";
	String itm1="Computer";
	String itm2="Shoe";

	//String SysDes ;


	//int qty=1;

	String Naminghost="localhost";

	//Remote hosts are defined here
	String tradeHost1="localhost";
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

	    // resolve the Object Reference in Naming
	    //String name = "DET";

	    detImpl = DETHelper.narrow(ncRef.resolve_str(name));
	    /*System.out.println("asfasfsaf");
       System.out.println(detImpl.buyItem("Shoe",2));
       System.out.println(detImpl.tradeItems("Computer",1,"Shoe",3,tradeHost1));
       System.out.println(detImpl.printReport()); */

	}//End of try block

	catch(Exception e){
	    System.out.println("Exception in testClient: " + e);
	} //End of catch block


	try{	

	    // Initialize new thread object r1 and have fixed number of thread
	    // We will have a thread pool that contains 100 concurrent requests
	    // All of the thread will be stored in the thread queue.

	    NewThread[] r1= new NewThread[100];
	    NewThread[] r2 = new NewThread[23];


	    for (int x=0; x<10 ;x++)
	    {		
		r2[x] = new NewThread(detImpl, itm2, 2) ;
		r2[x].start();

		r1[x]=new NewThread(detImpl,itm1,1,itm2,1,tradeHost1);
		r1[x].start();

		//Create concurrent buy operation 





		//r1[x]=new NewThread(detImpl,itm1,1,itm3,1,tradeHost2);
		//r1[x].start();
	    }	




	    Thread.sleep(5000); 

	    //Agent_Computer cs = new Agent_Computer();
	    SNMPMemberFuntions snmpcal = new SNMPMemberFuntions();

	    

	    //Constructing trap part i need to construct trap type first, get the trap type and pass it to the trapsend method.
	    snmpcal.trapSendForBalance("SNMPV1", "public", "localhost");
	    snmpcal.trapSendForQuantity("SNMPV1", "public", "localhost");

	    //For testing i am going to commented out..



	    //cs.monitorDetComputerServerBeforeTransaction();

	    //System.out.println("");
	    //cs.monitorDetComputerServerAfterTransaction() ;

	    //System.out.println(detImpl.printReportAfterTransaction());

	    //System.out.println("");

	    //System.out.println(detImpl.printReport());

	    //Call set balance from the detImpl. so that it is in the CORBA nameSpace.
	    //It is very tricy , so please follow up.

	    //System.out.println("The SetBalance Information will be Displayed:");

	    //System.out.println(detImpl.SnmpSetBalance());



	}


	catch(Exception e){
	    System.out.println("Exception is exist in the testClient: " + e);

	} //End of catch block



    }


    public void trapResponseforBalance(String trap) {
	String trapMessage = trap ;
	System.out.println("");

	System.out.println("################################################## START OF TRAP MESSAGE #################################################");
	System.out.println("The Received Message from the Manager:" + trapMessage);
	System.out.println("################################################## END OF TRAP MESSAGE ##################################################");
	System.out.println("");
    } 


    public void trapResponseforQuantity(String trap) {
	String trapMessage = trap ;
	System.out.println("");

	System.out.println("################################################## START OF TRAP MESSAGE #################################################");
	System.out.println("The Received Message from the Manager:" + trapMessage);
	System.out.println("################################################## END OF TRAP MESSAGE ##################################################");
	System.out.println("");
    } 

    public void ResponseSet(String tra) {
	String tp = null ;

	tp = tra ;

	System.out.println("");
	System.out.println("################################################## MESSAGE FROM MANAGER #################################################");
	System.out.println("The Received Message from the Manager:" + tp);
	System.out.println("################################################## MESSAGE FROM MANAGER ##################################################");
	System.out.println("");

    }

   
  

    /*   

   //This main function is used only for testing purpose.

    public static void main(String args[]) {

	Agent_Computer agent = new Agent_Computer();


	agent.monitorDetComputerServerBeforeTransaction();

	System.out.println("");
	agent.monitorDetComputerServerAfterTransaction() ;

	System.exit(0);


    }
     */
}
