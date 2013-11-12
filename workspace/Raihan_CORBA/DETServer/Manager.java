package DETServer;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import DETApp.DET;
import DETApp.DETHelper;



public class Manager {

    static DET detImpl;
    double balance ;
    ContainerManagedItem ct ;
  public void snmpTrapMessageReceived(double balInfo) {
	
	//When the balance will be zero, send trap message to manager.
		//System.out.println("Manager Received Trap Message from Server/Agent:");
		double balance = balInfo;
		//System.out.println("The Trap Message Information is Stated Below:" + balance);
	 
		//Call Trap Response message
		
		if (( balance == 0) || (balance < 0)) {
		    Agent_Shoe agent = new Agent_Shoe();
			agent.trapResponse("Manager will provide more money in that Server due to Balance became negative:");
			Manager m = new Manager();
			//m.snmpSetRequest(4000);
	    
	    }
  
      }

//Need to write set function, that will set the balance parameters
  
  
  /*
  public void snmpSetRequest(double bal) {
	    // synchronized(lockItm) {
		    ct.setBalance(bal);
		    Agent_Shoe agent = new Agent_Shoe();
		    agent.ResponseSet("The Balance is set to:" + bal);
		    //String ok = null;
		    //return ok ;
	     }
  
  */
  public void MessagePassed(String mess) {
	
	String mas = null ;
	mas = mess ;
	Agent_Shoe agent = new Agent_Shoe();
	agent.ResponseSet(mas);
	//return mas ;
  }
  
} 

