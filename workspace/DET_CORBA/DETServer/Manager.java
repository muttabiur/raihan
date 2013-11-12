package DETServer;

//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.util.ArrayList;
//import java.util.List;
import snmpcorba.mib.*;

//import org.omg.CORBA.ORB;
//import org.omg.CosNaming.NamingContextExt;
//import org.omg.CosNaming.NamingContextExtHelper;

import DETApp.DET;
//import DETApp.DETHelper;
import SNMPFunctions.* ;

/**
 * @author m_sk
 */

public class Manager {

    double balance ;
    static DET detImpl;
    ContainerManagedItem ct ;
    String value = null ;

    public void snmpTrapMessageReceived(double balInfo) {

	//When the balance will be zero, send trap message to manager.
	//System.out.println("Manager Received Trap Message from Server/Agent:");
	double balance = balInfo;
	//System.out.println("The Trap Message Information is Stated Below:" + balance);

	//Call Trap Response message

	if (( balance == 0) || (balance < 0)) {
	    Agent_Computer agent = new Agent_Computer();
	    agent.trapResponseforBalance("Manager will provide more money in that Server due to Balance became negative:");
	    //Manager m = new Manager();



	    // System.out.println("The new balance is:" + raihan);
	    // m.snmpSetRequest(4000);
	}

    }

    public void snmpTrapMessageReceivedForQuantity(int qtityInfo) {

	//When the balance will be zero, send trap message to manager.
	//System.out.println("Manager Received Trap Message from Server/Agent:");
	int quantity = qtityInfo;
	//System.out.println("The Trap Message Information is Stated Below:" + balance);

	//Call Trap Response message

	if (( quantity == 0) || (quantity < 0)) {
	    Agent_Computer agent = new Agent_Computer();
	    agent.trapResponseforQuantity("Manager will provide Enough Quantity in that Server due to Quantity became negative:");
	    //Manager m = new Manager();



	    // System.out.println("The new balance is:" + raihan);
	    // m.snmpSetRequest(4000);
	}

    }
    /*
    public void snmpSetRequest(double bal) {
	     //synchronized(lockItm) {
		    ct.setBalance(bal);
		    Agent_Computer agent = new Agent_Computer();
		    agent.ResponseSet("The Balance is set to:" + bal);
		    //String ok = null;
		    //return ok ;
	     }
     */

    public void MessagePassed(String mess) {

	String mas = null ;
	mas = mess ;
	Agent_Computer agent = new Agent_Computer();
	agent.ResponseSet(mas);

    }


   public void setLocation(String location) {
    
       this.value = location ;
   }

   public String getLocation() {
       return this.value ;
   }

    //Mib module will be here

    //Manager will call get-request
    //Manager will call set-request
    //Manager will call get-next-request

    public static void main(String args[]) {

	SNMPMemberFuntions snmpCall = new SNMPMemberFuntions();


	snmpCall.getRequest("SNMPV1" , "Public" , 1, "1.3.6.1.5.1" );
	//Set-Request message to agent
	//String value1 = "1.3.6.1.4.1.9.7" ;
	//snmpCall.setRequestForBalance("SNMPV1", "Public", 3,  "6");
	
	//Set up for Quantity
	//snmpCall.setRequestForQuantity("SNMPV1", "Public", 5, "5");



    }
    }




