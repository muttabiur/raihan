package DETClient;


import java.rmi.Naming;
import java.util.Vector;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import DETApp.DET;
import DETApp.DETHelper;
import Interface.* ;
import DETServer.* ;


/**
 * @author m_sk
 */

public class testClient {

	static DET detImpl;
	
	
	/**
	 * Design By Contract
	 * Precondition: This function expects no input from the user
	 * PostCondition: This function will create new threads object.
	 * 
	 */
	
	public static void main(String args[]) {
	
		
	// Initialization of three different items
		
		String url="";
		String itm1="Computer";
		String itm2="Shoe";
		
		String SysDes ;
		
						
		int qty=1;
				
		String Naminghost="localhost";
   	 	
		//Remote hosts are defined here
		String tradeHost1="localhost";
   	 	String tradeHost2="localhost";
   	 	
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
		
		
		for (int x=0; x<5 ;x++)
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
	 	
	 	//cs.monitorDetComputerServerAfterTransaction() ;
	 	
		System.out.println(detImpl.printReport());
		
		System.out.println("");
		
		/*
		System.out.println("SNMP Server Call and Reply Message:");
		
		
	    System.out.println(detImpl.snmpSentRequestToServer1(itm1));
	    
	    System.out.println("");
	    
	    System.out.println("SNMP Server Call and Reply Message:");
			 
	    System.out.println("");
	    System.out.println(detImpl.snmpSentRequestToServer2(itm2));
	    
	    System.out.println("System Description will be Illustrated Here:");
	    
	    String SystemDes ;
	    SystemDes = detImpl.snmpGetRequestSysDescription(itm1);
	    
	    System.out.println("The System Description is Stated Below:" +  SystemDes);
	    
	    System.out.println("");
	    
	    System.out.println("The System Contact Information is Stated Below:");
	    
	    String SystemCont ;
	    
	    SystemCont = detImpl.snmpGetRequestSysContact(itm1);
	    
	    System.out.println("The Contact Information is Stated Below:" + SystemCont);
	    
	    
        System.out.println("");
	    
	    System.out.println("The System Name Information is Stated Below:");
	    
	    String SystemName ;
	    
	    SystemName = detImpl.snmpGetRequestSysName(itm1);
	    
	    System.out.println("The System Name Information is Stated Below:" + SystemName);
	    
	    System.out.println("");
	    
        System.out.println("The System Location Information is Stated Below:");
	    
	    String SystemLocation ;
	    
	    SystemLocation = detImpl.snmpGetRequestSysLocation(itm1);
	    
	    System.out.println("The System Location Information is Stated Below:" + SystemLocation);
	    
	    System.out.println("");
	    System.out.println("The System Service Information is stated below:");
	    
	    String SystemService ;
	    
	    SystemService = detImpl.snmpGetRequestSysServiceInfo(itm1);
	    
	    System.out.println("The System Service Information is Stated Below:" + SystemService);
	    
	    System.out.println("");
	    
	    */
		} //End of try block
       
       catch(Exception e){
			System.out.println("Exception is exist in the testClient: " + e);
		} //End of catch block
		
		
	}
} //End of Test Client class
