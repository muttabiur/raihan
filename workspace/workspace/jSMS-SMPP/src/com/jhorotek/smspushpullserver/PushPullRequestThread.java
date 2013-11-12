/*
 * SMSServiceBroker.java
 *
 * Created on March 31, 2007, 5:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.jhorotek.smspushpullserver;

import java.net.URLEncoder;
import java.net.URL;
import java.util.LinkedHashMap;

import org.apache.commons.configuration.XMLConfiguration;

import com.jhorotek.smpp.SMPPClass;
import com.logica.smpp.pdu.DeliverSM;
import com.logica.smpp.pdu.PDU;
import com.logica.smpp.pdu.SubmitSM;



/**
 *
 * @author Administrator
 */
public class PushPullRequestThread extends Thread{
    /*
     *ToDo:
     * Should have a thread pool from which it picks threads and run them
     **/

	/**
     * Holds all the configuration for one operator 
     * pushpull url and server login details 
     */
    private XMLConfiguration config = null;
    private LinkedHashMap response = null;
    private SMPPClass writer = null;
    private PDU pdu = null;
    private String message;
    private String phNumber;
    private String port = "";
    private String theUrlStr = "";
    //private static Connector CONNECTOR;
   
    
    /** Creates a new instance of SMSServiceBroker */
    public PushPullRequestThread(XMLConfiguration SMPPConfig, SMPPClass writer, PDU pdu){
    	System.out.println("In PushPullRequest....");
        this.config = SMPPConfig;
    	this.writer=writer;
        this.pdu = pdu;
        
        this.phNumber= (String)(((DeliverSM)pdu).getSourceAddr().getAddress());
        this.message= (String)(((DeliverSM)pdu).getShortMessage());
        this.port = (String)(((DeliverSM)pdu).getDestAddr().getAddress());
    }
    
    /*
     * Upon getting a request, Service Listeners create a Thread with this Runnable SMSServiceBroker, 
     * which when runs, parses the message, and finds out the right plugin to instantiate,
     * gets the result from the plug-in for the given message and then writes the output
     * to the writer object.
     */
    
    public void run(){
        try {
        	System.out.println("In PushPullRequestThread class....");
        	System.out.println("msg: "+ message);
        	
        	
        	message = CommonUtils.trimResponse(message);
        	
        	//java.util.Date date = (new java.util.Date());
    		
    		//this.theUrlStr += "?IN_MSG_ID=" + date.getTime() + "&MOBILENO="+this.phNumber+"&BODY="+smsBody + "&SECURITY_CODE=98789";

        	//?IN_MSG_ID=654&MOBILENO=8801715817005&BODY=hi&PORT=98789&USERNAME=test&PASSWORD=847484
            
        	//(new java.util.Date()).getTime();
        	
        	theUrlStr = "?IN_MSG_ID=" + (new java.util.Date()).getTime();
        	theUrlStr += "&MOBILENO=" + URLEncoder.encode(phNumber,"UTF-8");
        	theUrlStr += "&BODY=" + URLEncoder.encode(message,"UTF-8");
        	
        	//theUrlStr += "&PORT=" + config.getString("SOURCE_SHORTCODE");
        	theUrlStr += "&PORT=" + port;
        	
        	theUrlStr += "&USERNAME=" + URLEncoder.encode(config.getString("CP_USERNAME"),"UTF-8");
        	theUrlStr += "&PASSWORD=" + URLEncoder.encode(config.getString("CP_PASSWORD"),"UTF-8");
        	
            //System.out.println("codes0: "+ shortCode[0]);
            //System.out.println("codes1: "+ shortCode[1]);
        	
        	pushpullCommmunicator commmunicator = new httpPushPull();
        	
        	response = commmunicator.getData(new URL(config.getString("REQUEST_RESPONSE_URL") + theUrlStr));
        	//response = new XMLConfiguration(new URL(config.getString("REQUEST_RESPONSE_URL") + theUrlStr));
        	
        	
        	/*response.getString("REPLY_TEXT");
        	response.getString("SEND_PORT");
        	response.getInt("MSG_TYPE");
        	phNumber;*/
        	
        	if(Integer.parseInt((String)response.get("MSG_TYPE")) == 3){
        		writer.RequestAck(pdu);
        	}else{
        		
        		SubmitSM request  = com.jhorotek.smpp.CommonUtils.buildSubmit(config, (String)response.get("SEND_PORT"), (String)response.get("MOBILENO"), (String)response.get("REPLY_TEXT"), false);
        		
        		if(response != null){
        			writer.RequestAck(pdu);
        			
        			while(!writer.ResponseWriter(request)) {
						writer.Connect();
					}
        		}
        		
        		//writer.ResponseWriter(phNumber, response.getString("REPLY_TEXT"));
        	}
        	
        	/*if(shortCode == null){
        		msgLevel1 = "";
        		msgLevel2 = "";
        	}else{
        		if(shortCode.length > 1){
        			msgLevel1 = shortCode[0];
        			msgLevel2 = shortCode[1];
                }else if(shortCode.length > 0){
                	msgLevel1 = shortCode[0];
                	msgLevel2 = "";
                }else{
                	msgLevel1 = "";
            		msgLevel2 = "";
                }
        	}

        	ServicePlugin service=getServicePlugin(msgLevel1); //<<-----md
        	
        	service.setPhNumber(phNumber);
        	service.setIn_msg_id(writer.getIn_msg_id());

            System.out.println("codes: "+ service);
            System.out.println("ph: "+ phNumber);
            
            System.out.println("tbl: "+ writer.gettblName());*/
            
            
            /* this writer may throw an exception if it
             * cannot write
             **/

            
            /*
            String query = "INSERT INTO "+ writer.gettblName() +"_inbox (mobileno, msg, short_code," +
			" answer, datetime" 
			+ ") VALUES('" 
			+ phNumber +"','"
			+ message +"','"
			+ msgLevel1 +"','"
			+ msgLevel2 +"',"
			+ "NOW()" +")";
            
            System.out.println("Sending msg....");
            writer.write(phNumber, CommonUtil.trimResponse(service.getResult(shortCode)));
            
            System.out.println("Executing Query....");
            Connector conn = new Connector();
            conn.executeQuery(query);
            conn.close();
            */
            System.out.println("end thread broker class....");
            
            
        } catch (Exception e) {
            e.printStackTrace();
            try
            {
            	//writer.write(phNumber,"wrong message format.");
            }
            catch(Exception ex)
            {
            	//do nothing
            }
        }
    }
    
}

