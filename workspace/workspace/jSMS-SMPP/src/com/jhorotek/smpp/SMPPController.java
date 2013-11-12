package com.jhorotek.smpp;

import java.io.*;
import java.util.Collection;

import org.apache.commons.configuration.XMLConfiguration;
//import com.jhorotek.multithreading.StaticPool;
//import com.jhorotek.smspushpullserver.*;

import com.jhorotek.smspushpullserver.PullRequestThread;
import com.jhorotek.smspushpullserver.PushPullRequestThread;
import com.logica.smpp.pdu.PDU;
import com.logica.smpp.pdu.DeliverSM;

/**
 * @author AdnaN
 *
 */
public class SMPPController extends Thread{
	
    /**
     * Holds all the configuration for one operator 
     * to transmit and receive 
     */
    private XMLConfiguration config = null;
    
    /**
     * For transmission obj 
     * to transmit msg
     */
    private SMPPClass smppClass = null;
    
    /**
     * Configuration for pull system 
     * to get delay, bulk and subscription sms 
     */
    public boolean isPull = false;
   
    /**
     * Time interval for pull system 
     * in milliseconds 
     */
    private long pullTime = 0;
    
    private PullRequestThread pullRequestThread = null;
    
    long firstTimeMillis = 0;
    
    boolean isPullFirstCall = true;
    
    /**
     * @param SMPPConfig
     * @param debug
     * @throws IOException
     */
    public SMPPController(XMLConfiguration SMPPConfig, boolean debug)
    throws IOException
    {
        //loadProperties(propsFilePath);
    	config = SMPPConfig;
    	System.out.println("name:"+SMPPConfig.getString("NAME"));
    	isPull = SMPPConfig.getString("PULL.ACTIVE").equalsIgnoreCase("true");
    	pullTime = config.getLong("PULL.DELAY_TIME") * 60 * 1000;
    	
    	smppClass = new SMPPClass(config, debug);
    }
    
    
    public void run(){
    	System.out.println("In thread smpp controller....");
    	smppClass.Connect();
    	
    	PDU pdu = null;
    	
    	
    	
		while(true){

    		pdu = smppClass.RequestListener();
        	
        	if(pdu != null  && (pdu instanceof DeliverSM)){
        		PushPullRequestThread broker = new PushPullRequestThread(config, smppClass, pdu);
        		//StaticPool.getTpool().addTask(broker);
        		broker.start();
        	}
        	
        	if(isPull){
        		if((System.currentTimeMillis() - firstTimeMillis) > pullTime){
        			
        			Object obj = config.getProperty("PULL.SHORTCODES.SHORTCODE");
        			
        			if (obj instanceof Collection) {
        				int pullReqSize = ((Collection) obj).size();
        	            
        	            //loading each and making separate config joining with the common config fields
        				if(isPullFirstCall){
        				
        					for (int i = 0; i < pullReqSize; i++) {
        	            	
        	            		pullRequestThread = new PullRequestThread(config, smppClass, config.getString("PULL.SHORTCODES.SHORTCODE(" + i + ")"), true);
        	            		pullRequestThread.start();
        	            	}
        					isPullFirstCall = false;
        				}else{
        					for (int i = 0; i < pullReqSize; i++) {
	        					pullRequestThread = new PullRequestThread(config, smppClass, config.getString("PULL.SHORTCODES.SHORTCODE(" + i + ")"));
	        	            	pullRequestThread.start();
        					}
        	            }
        			}else{
        				
        				if(isPullFirstCall){
        					pullRequestThread = new PullRequestThread(config, smppClass, config.getString("PULL.SHORTCODES.SHORTCODE"), true);
        					isPullFirstCall = false;
        				}else{
        					pullRequestThread = new PullRequestThread(config, smppClass, config.getString("PULL.SHORTCODES.SHORTCODE"));
        				}
        				
    	            	pullRequestThread.start();
        			}
        			
        			//pullRequestThread = new PullRequestThread(config, smppClass, "5676");
        			firstTimeMillis = System.currentTimeMillis();
        		}
        		
        	}
		}
    }
    
    
    /**
     * For transmitting msg
     * @param number
     * @param msg
     * @return msgID
     */
    /*
    public String send(String number, String msg){
    	return transmit.submit(number, msg);
    }
    */
    /**
     * For transmitting msg to multiple numbers
     * @param numbers
     * @param msg
     * @return msgID
     */
    /*
    public String sendMulti(String []numbers, String msg){
    	return transmit.submitMulti(numbers, msg);
    }
    
    public String[] receive(){
    	return receiver.receive();
    }
    */
    
}