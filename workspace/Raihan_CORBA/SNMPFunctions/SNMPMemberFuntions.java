package SNMPFunctions;


/*
 * This Class Contains all the SNMP Operations
 * All the Mutator and Accessor  methods are implemented here.
 * This class works as a Template for our SNMP-Based Distributed Electronic Trader System.
 * Written-By "Raihan-Pawel-Wang"
 */
public class SNMPMemberFuntions {

    
     public String snmpversionNumber = "SNMPV1" ;
     public String communityName = "Public" ;
     public int requestID = 0 ;
     
     
     
     
     public void getRequest(String version, String commName, int reqID, String argument){
	 
	 /*Check all the Necessary Parameters, 
	  * just to authenticate at the Application Layer
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
     
     public void getNextRequest(String version, String commName, int reqID, String argument) {
	 
	 /*Check all the Necessary Parameters, 
	  * just to authenticate at the Application Layer
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
     
     public void getResponse(String version, String commName, int reqID, String argumenResponse) {
	 
	 System.out.println("###################### Begin of Response Message#####################");
	 System.out.println("Get-Response Message from Agent:");
	
	 System.out.println("The SNMP-Version is:" + this.snmpversionNumber);
	 System.out.println("The Community-Name is:" + this.communityName);
	 System.out.println("The Request-ID is:" + this.requestID);
	 //In the Response object, i need to store the return value.
	 System.out.println("The Response-Object:" + " ") ;
	 System.out.println("###################### End of Response Message#####################");
     }
     
     public void setRequest(String version, String commName, int reqID, String argument) {
	
	 /*Check all the Necessary Parameters, 
	  * just to authenticate at the Application Layer
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
     
     public void trapSend(String version, String commName, String objectIDENTIFIER, String agentAddress, int generalTrap, int specificTrap, double timeStamp, String trapMessage) {
	 
	 
     }
    
}
