
package DETClient;
import DETApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;

/**
 * @author m_sk
 */


public class DETClient
{
  static DET detImpl;

  /**
	 * Design By Contract
	 * Precondition: This function expects no input from the user
	 * PostCondition: This function will create and initialize the OBR Object 
	 * 
	 */
  
  public static void main(String args[]){
	  
     try{
    	 
    	 //Initialization of one remote server, named "cukor.encs.concordia.ca"
    	 //Local host deals with only one item, that is Computer
    	 String Naminghost="localhost";
    	 String tradeHost="cukor.encs.concordia.ca";
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

 
        //System.out.println(detImpl.buyItem("Computer",5));
        //System.out.println(detImpl.buyItem("Shoe",2));
    
       
        //System.out.println(detImpl.buyItem("Computer",5));
        System.out.println(detImpl.buyItem("Shoe",2));
        
        System.out.println(detImpl.tradeItems("Computer",1,"Shoe",3,tradeHost));

        System.out.println(detImpl.printReport());

	  } //End of try block
     
     //Print an exception trace if any exception happens
     catch (Exception e) {
        System.out.println("ERROR : " + e) ;
	     e.printStackTrace(System.out);
	  } //End of Catch block
     
  } //end main

} // end class
