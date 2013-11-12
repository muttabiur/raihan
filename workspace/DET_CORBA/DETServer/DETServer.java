// A server for the Hello object
package DETServer;

import DETApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;

import snmpcorba.mib.MIBTree;

import java.util.Properties;

/**
 * @author m_sk
 */

public class DETServer {

  public static void main(String args[]) {
    try{
      // create and initialize the ORB
 
      DETImpl detImpl = new DETImpl();
    	
      //String arg1[]={"-ORBInitialHost", "localhost","-ORBInitialPort", "1234"};
      
      
      String arg1[]={"-ORBInitialHost", detImpl.NamingServer,"-ORBInitialPort", "1234"};
 	 
  	 ORB orb = ORB.init(arg1, null);

      
      // get reference to root poa & activate the POAManager
  	 
      POA rootpoa =   (POA)orb.resolve_initial_references("RootPOA");
      
      rootpoa.the_POAManager().activate();

      // create servant and register it with the ORB
      
     
      detImpl.setORB(orb); 

      // get object reference from the servant
      org.omg.CORBA.Object  ref = rootpoa.servant_to_reference(detImpl);
      
      // and cast the reference to a CORBA reference
      DET href = DETHelper.narrow(ref);
	  
      // get the root naming context
      // NameService invokes the transient name service
      
      org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");
      
      // Use NamingContextExt, which is part of the
      // Interoperable Naming Service (INS) specification.
      
      NamingContextExt ncRef =  NamingContextExtHelper.narrow(objRef);

      // bind the Object Reference in Naming
      //String name = "DET";
      
      String name = detImpl.NamingObj;

      NameComponent path[] = ncRef.to_name( name );
      
      ncRef.rebind(path, href);
      
      // initialize the MIB
      // MIBTree.initMIB("PUT SCHEMA FILE PATH HERE");
      
      System.out.println("");

      System.out.println (name+" Server is ready and waiting for further requests !");

      System.out.println("");
      
      // wait for invocations from clients
      orb.run();
    } 
	
      catch (Exception e) {
        System.err.println("ERROR: " + e);
        e.printStackTrace(System.out);
      }
	  
      System.out.println("Requested Server is exist !");
	
  } //end of main
} // end of class
