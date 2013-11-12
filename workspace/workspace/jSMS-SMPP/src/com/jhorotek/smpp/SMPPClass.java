package com.jhorotek.smpp;

import java.io.IOException;

import org.apache.commons.configuration.XMLConfiguration;

import com.logica.smpp.pdu.PDU;
import com.logica.smpp.pdu.SubmitSM;

public class SMPPClass {
    /**
     * Holds all the configuration for one operator 
     * to transmit and receive 
     */
    private XMLConfiguration config = null;
    
    /**
     * For transmission obj  
     * to transmit msg
     */
    private Transmitter transmit= null;
    
    /**
     * For receiving obj
     * to receive msg
     */
    private Receiver receiver= null;
    
    /**
     * For receiving obj
     * to receive msg
     */
    private TransReceiver transreveiver= null;
    
    /**
     * For receiving obj
     * to receive msg
     */
    private boolean isTransreceiver= false;
    
    /**
     * @param SMPPConfig
     * @param debug
     * @throws IOException
     */
    public SMPPClass(XMLConfiguration SMPPConfig, boolean debug)
    throws IOException
    {
        //loadProperties(propsFilePath);
    	config = SMPPConfig;
    	
    	isTransreceiver = config.getString("BIND_MODE").equalsIgnoreCase("tr");
    	
    	System.out.println("name:"+SMPPConfig.getString("NAME")+" transreceiver: " + isTransreceiver);
    	    	
    	if(isTransreceiver){
    		transreveiver = new TransReceiver(SMPPConfig, debug);
    	}else{
    		transmit = new Transmitter(SMPPConfig, debug);
        	receiver = new Receiver(SMPPConfig, debug);
    	}
    	
    }
    
    public PDU RequestListener(){
    	
    	if(isTransreceiver){
        	return transreveiver.receive();
            	
    	}else{
        	return receiver.receive();
    	}
    }
    
    public void RequestAck(PDU pdu){
    	if(isTransreceiver){
    		transreveiver.ReceiveAck(pdu);
        	
		}else{
	    	receiver.ReceiveAck(pdu);
		}
    }
    
    public boolean ResponseWriter(SubmitSM request){
    	if(isTransreceiver){
    		return transreveiver.submit(request);
        	
		}else{
	    	return transmit.submit(request);
		}
    }
    
    public synchronized void Connect(){
		System.out.println("In thread smpp Connect....");
		if(isTransreceiver){
			System.out.println("In thread smpp transreceiver....");
			transreveiver.Connect();
    	}else{
    		System.out.println("In thread smpp t/r....");
    		transmit.Connect();
    		receiver.Connect();
    	}
	}
	
	/**
	 * disconnects From SMSC
	 */
	public synchronized void Disconnect(){
		
		if(isTransreceiver){
			transreveiver.Disconnect();
    	}else{
    		transmit.Disconnect();
    		receiver.Disconnect();
    	}
		
	}
}
