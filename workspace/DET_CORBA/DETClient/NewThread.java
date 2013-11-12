package DETClient;

import java.rmi.Naming;
import java.util.Vector;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import DETApp.DET;
import DETApp.DETHelper;
import Interface.* ;

/**
 * @author m_sk
 */

public class NewThread extends Thread{

  //NewThread class data members 
	
	DET det;
	String itm;
	int qty;
	
	String itm1;
	int qty1;
	String itm2;
	int qty2;
	String tradeHost;
	
	
	//Constructor function NewThread for buy / sell methods
	
	public NewThread(DET u,String i, int q){
		
		this.det=u;
		this.itm=i;
		this.qty=q;
		this.tradeHost="A";
				
	}
	
	//Constructor function NewThread for trade item function, trade item function will use
	//UDP protocol to communicate between two servers.
	
	public NewThread(DET u,String i1, int q1 ,String i2, int q2, String th ){
		
		det=u;
		this.itm1=i1;
		this.qty1=q1;
		this.itm2=i2;
		this.qty2=q2;
		this.tradeHost=th;
	}
	
	//Define new thread constructor for the buy method
	
	
	//The run method
	public  void run(){
		
		//Create object for MessageContainer class
		MessageContainer AllMessage = new MessageContainer();
		
		try {
	         
    	 	//Check if parameter "A" is equal to trade host, ignore case, if they have same length and character matches 
			//between two strings.
			
			if (tradeHost.equalsIgnoreCase("A"))
			{
			    int ret =det.buyItem(itm, qty);
			    
			    //I am commenting the following, just for testing
    	 	   System.out.println(AllMessage.showMsg(ret));
			}
	    	 else{  
	    		 
	    		System.out.println(itm1+","+ qty1+","+itm2+","+qty2+","+tradeHost);
	    	
	    		 int ret =det.tradeItems(itm1,qty1,itm2,qty2,tradeHost);
	    		 //I am commenting the following, just for testing
	    		 System.out.println(AllMessage.showMsg(ret));
	    	 }
	    	 		
	    
		} //End of Try block
		
		catch (Exception e) {
        System.out.println("Exception in testClient: " + e);
        } 
	} //End of Catch
	
		
	
}	//end class
