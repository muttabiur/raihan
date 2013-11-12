package com.jhorotek.smpp;

//import java.io.IOException;

import org.apache.commons.configuration.XMLConfiguration;

import com.logica.smpp.Data;
import com.logica.smpp.ServerPDUEvent;
import com.logica.smpp.ServerPDUEventListener;
import com.logica.smpp.Session;
import com.logica.smpp.TCPIPConnection;
//import com.logica.smpp.WrongSessionStateException;
import com.logica.smpp.debug.Debug;
import com.logica.smpp.debug.Event;
import com.logica.smpp.debug.FileDebug;
import com.logica.smpp.debug.FileEvent;
import com.logica.smpp.pdu.*;

public class TransReceiver {
	/**
     * Directory for creating of debug and event files.
     */
    static final String dbgDir = "./debug/";

    /**
     * Number of times connections tried to establish
     */
    int TryConnection = 0;
    
    /**
     * Number of times connections try
     */
    int Trycount = 10;
    
    /**
     * The debug object.
     * @see FileDebug
     */
    Debug debug = null;

    /**
     * The event object.
     * @see FileEvent
     */
    Event event = null;

    /**
     * This is the SMPP session used for communication with SMSC.
     */
    Session session = null;
    
    /**
     * This is an instance of listener which obtains all PDUs received from the SMSC.
     * Application doesn't have explicitly call Session's receive() function,
     * all PDUs are passed to this application callback object.
     * See documentation in Session, Receiver and ServerPDUEventListener classes
     * form the SMPP library.
     */
    PDUEventListener pduListener = null;
    
    /**
     * The range of addresses the smpp session will serve.
     */
    public AddressRange addressRange = new AddressRange();
    
    /**
     * Destination address for sending msg to SMSC.
     */
    Address destAddress = null;
    
    /**
     * Source address for sending msg to SMSC.
     */
    Address sourceAddress = null;
    
    /**
     * If you attemt to receive message, how long will the application
     * wait for data.
     */
    long receiveTimeout = Data.RECEIVE_BLOCKING;
    
    /**
	 * If the Connection should be asynchronous with the SMSC.
	 */
	boolean isSynchronous = false;
	
    /**
     * If the application is bound to the SMSC.
     */
    boolean bound = false;
    
    /**
     * Configurations for SMSC.
     */
    private XMLConfiguration config = null;
    
    /**
     * Name for this SMSC.
     */
    String name = null;
    
    /**
     * sent msg id.
     */
    String messageId     = "";
    
    /**
	 * constructor
	 */
	public TransReceiver(XMLConfiguration conf, boolean debuglog) {
		config = conf;
		name = config.getString("NAME");
		debug = new FileDebug(dbgDir, name+"_transreceiver.dbg");
		event = new FileEvent(dbgDir, name+"_transreceiver.evt");
		
		if(!debuglog){
			debug.deactivate();
			event.deactivate();
		}
		
		/*sourceAddress = new Address();
		CommonUtils.setAddressParameter("Source-Address", sourceAddress, config.getByte("SOURCE_TON"), config.getByte("SOURCE_NPI"), config.getString("SOURCE_SHORTCODE"));
		
		destAddress = new Address();
		destAddress.setNpi(config.getByte("DESTINATION_NPI"));
		destAddress.setTon(config.getByte("DESTINATION_TON"));
		*/
		addressRange = new AddressRange();
		CommonUtils.setAddressRangeParameter("AddressRange", addressRange, (byte)1, (byte)1, config.getString("LOGIN_IP"));
		
		int rcvTimeout = config.getInt("RECEIVE_TIMEOUT");
        if (rcvTimeout == -1) {
            receiveTimeout = Data.RECEIVE_BLOCKING;
        } else {
            receiveTimeout = rcvTimeout * 1000;
        }
        
        isSynchronous = config.getString("SYNC_MODE").equalsIgnoreCase("sync");
	}

	/**
	 * connect as a TransReceiver
	 */
	public void Connect(){
		bind();
	}
	
	/**
	 * disconnects From SMSC
	 */
	public void Disconnect(){
		unbind();
	}
	
	
	/**
     * The first method called to start communication
     * betwen an ESME and a SMSC. A new instance of <code>TCPIPConnection</code>
     * is created and the IP address and port obtained from user are passed
     * to this instance. New <code>Session</code> is created which uses the created
     * <code>TCPIPConnection</code>.
     * All the parameters required for a bind are set to the <code>BindRequest</code>
     * and this request is passed to the <code>Session</code>'s <code>bind</code>
     * method. If the call is successful, the application should be bound to the SMSC.
     *
     * See "SMPP Protocol Specification 3.4, 4.1 BIND Operation."
     * @see BindRequest
     * @see BindResponse
     * @see TCPIPConnection
     * @see Session#bind(BindRequest)
     * @see Session#bind(BindRequest,ServerPDUEventListener)
     */
    private synchronized void bind()
    {
    	System.out.println("In TransReceiver bind....");
        debug.enter(this, "bind()");
        try {

            if (bound) {
                System.out.println("Already bound, unbind first.");
                return;
            }

            BindRequest request = null;
            BindResponse response = null;
            debug.write("b4 req");
            request = new BindTransciever();
            debug.write("after req");
            TCPIPConnection connection = new TCPIPConnection(config.getString("LOGIN_IP"), config.getInt("LOGIN_PORT"));
            debug.write("after tcpip");
            connection.setReceiveTimeout(20*1000);
            debug.write("after conn");
            session = new Session(connection);
            debug.write("after session");
            // set values
            request.setSystemId(config.getString("SMPP_USER"));
            request.setPassword(config.getString("SMPP_PASSWORD"));
            request.setSystemType(config.getString("SYSTEM_TYPE"));
            request.setInterfaceVersion((byte)0x34);
            request.setAddressRange(addressRange);
            
            // send the request
            System.out.println("Bind request " + request.debugString());
            debug.write("b4 bind req");
            if (isSynchronous){
                pduListener = new PDUEventListener(session);
                response = session.bind(request,pduListener);
            } else {
                response = session.bind(request);
            }
            debug.write("after bind req");
            System.out.println("Bind response " + response.debugString());
            //if (response.getCommandStatus() == Data.ESME_ROK) {
            if (response.isOk()) {
                bound = true;
                TryConnection = 0;
                debug.write("Connection ok. bound");
            }

        } catch (Exception e) {
            event.write(e,"");
            debug.write("Bind operation failed. " + e);
            System.out.println("Bind operation failed. " + e);
        } finally {
            debug.exit(this);
        }
    }
    
    /**
     * Ubinds (logs out) from the SMSC and closes the connection.
     *
     * See "SMPP Protocol Specification 3.4, 4.2 UNBIND Operation."
     * @see Session#unbind()
     * @see Unbind
     * @see UnbindResp
     */
    private synchronized void unbind()
    {
        debug.enter(this, "unbind()");
        try {

            if (!bound) {
                System.out.println("Not bound, cannot unbind.");
                return;
            }

            //send the request
            System.out.println("Going to unbind.");
            if (session.getReceiver().isReceiver()) {
                System.out.println("It can take a while to stop the receiver.");
            }
            UnbindResp response = session.unbind();
            System.out.println("Unbind response " + response.debugString());
            bound = false;

        } catch (Exception e) {
            event.write(e,"");
            debug.write("Unbind operation failed. " + e);
            System.out.println("Unbind operation failed. " + e);
        } finally {
            debug.exit(this);
        }
    }
    
    /**
     * Creates a new instance of <code>SubmitSM</code> class, lets you set
     * subset of fields of it. This PDU is used to send SMS message
     * to a device.
     *
     * See "SMPP Protocol Specification 3.4, 4.4 SUBMIT_SM Operation."
     * @see Session#submit(SubmitSM)
     * @see SubmitSM
     * @see SubmitSMResp
     */
    public synchronized boolean submit(SubmitSM request)
    {
        debug.enter(this, "submit()");
        boolean status = false;
        if (!bound) {
        	debug.write("Not bound, cannot send msg.");
            System.out.println("Not bound, connot send msg.");
            return status;
        }
        try {
            //SubmitSM request = new SubmitSM();
            SubmitSMResp response;
            
            //destAddress.setAddress(number);
            
            // set values
            //Set to NULL for default SMSC settings.
            request.setServiceType("");
            
            //the short code used 
            //request.setSourceAddr(sourceAddress);
            
            //request.setDestAddr(destAddress);
            
            //Flag indicating if submitted message should replace an existing message.
            request.setReplaceIfPresentFlag((byte)0);
            
            //request.setShortMessage(msg);
            
            //The short message is to be scheduled by the SMSC for delivery.
            //Set to NULL for immediate message delivery.
            request.setScheduleDeliveryTime("");
            
            //The validity period of this message. 
            //Set to NULL to request the SMSC default validity period.
            request.setValidityPeriod("");
            
            //Indicates Message Mode & Message Type.
            request.setEsmClass((byte)0);
            
            //Protocol Identifier. Network specific field.
            request.setProtocolId((byte)0);
            
            //Designates the priority level of the message.
            request.setPriorityFlag((byte)0);
            
            
            request.setRegisteredDelivery((byte)0);
            
            //Defines the encoding scheme of the short message user data.
            //request.setDataCoding((byte)0);
            
            //Indicates the short message to send from a list of pre-defined (‘canned’) 
            //short messages stored on the SMSC. 
            //If not using an SMSC canned message, set to NULL.
            request.setSmDefaultMsgId((byte)0);

            // send the request
            request.assignSequenceNumber(true);

            System.out.println("Submit request " + request.debugString());
            if (isSynchronous) {
                session.submit(request);
                System.out.println();
            } else {
                response = session.submit(request);
                System.out.println("Submit response " + response.debugString());
                messageId = response.getMessageId();
            }
            status = true;
        } catch (Exception e) {
        	event.write(e,"");
            debug.write("Name: "+ config.getString("NAME")+" IP: "+ config.getString("LOGIN_IP") +"\nException: Submit failed.\nTrying for new connection...");
            System.out.println("Name: "+ config.getString("NAME")+" IP: "+ config.getString("LOGIN_IP") +"\nException: Submit failed.\nTrying for new connection...");
            bound = false;
            TryConnection++;
            if(TryConnection<Trycount){
            	
            	try{
                	Thread.sleep(10*1000);
                	bind();
                }catch (Exception ex) {
					ex.printStackTrace();
				}
            	
            }else{
            	event.write("Submit failed. Try Count Exceeded");
            	debug.write("Name: "+ config.getString("NAME")+" IP: "+ config.getString("LOGIN_IP") +"\nException: Submit failed.\nTry Count Exceeded: "+TryConnection+ "\nGoing for sleep for about 30sec");
                System.out.println("Name: "+ config.getString("NAME")+" IP: "+ config.getString("LOGIN_IP") +"\nException: Submit failed.\ngoing for sleep Program\nTry Count Exceted: "+TryConnection);
                // System.exit(1);
                
                try{
                	Thread.sleep(30*1000);
                	TryConnection = 0;
                	bind();
                }catch (Exception ex) {
					
				}
                
            }
            
		} finally {
            debug.exit(this);
        }
        
        return status;
    }
    

    /**
     * Creates a new instance of <code>SubmitMultiSM</code> class, lets you set
     * subset of fields of it. This PDU is used to send SMS message
     * to multiple devices.
     *
     * See "SMPP Protocol Specification 3.4, 4.5 SUBMIT_MULTI Operation."
     * @see Session#submitMulti(SubmitMultiSM)
     * @see SubmitMultiSM
     * @see SubmitMultiSMResp
     */
    public synchronized boolean submitMulti(String []numbers, String msg)
    {
        debug.enter(this, "submitMulti()");
        boolean status = false;
        if (!bound) {
        	debug.write("Not bound, cannot send msg.");
            System.out.println("Not bound, connot send msg.");
            return status;
        }
        
        try {
            SubmitMultiSM request = new SubmitMultiSM();
            SubmitMultiSMResp response;

            // input values and set some :-)
            for (int i=0; i<numbers.length; i++) {
                request.addDestAddress(new DestinationAddress(destAddress.getTon(),destAddress.getNpi(),numbers[i]));
            }
      
            // set other values
            //Set to NULL for default SMSC settings.
            request.setServiceType("");
            
            //the short code used 
            request.setSourceAddr(sourceAddress);
            
            //Flag indicating if submitted message should replace an existing message.
            request.setReplaceIfPresentFlag((byte)0);
            
            request.setShortMessage(msg);
            
            //The short message is to be scheduled by the SMSC for delivery.
            //Set to NULL for immediate message delivery.
            request.setScheduleDeliveryTime("");
            
            //The validity period of this message. 
            //Set to NULL to request the SMSC default validity period.
            request.setValidityPeriod("");
            
            //Indicates Message Mode & Message Type.
            request.setEsmClass((byte)0);
            
            //Protocol Identifier. Network specific field.
            request.setProtocolId((byte)0);
            
            //Designates the priority level of the message.
            request.setPriorityFlag((byte)0);
            
            
            request.setRegisteredDelivery((byte)0);
            
            //Defines the encoding scheme of the short message user data.
            request.setDataCoding((byte)0);
            
            //Indicates the short message to send from a list of pre-defined (‘canned’) 
            //short messages stored on the SMSC. 
            //If not using an SMSC canned message, set to NULL.
            request.setSmDefaultMsgId((byte)0);
            
            // send the request
            System.out.println("Submit Multi request " + request.debugString());
            if (isSynchronous) {
                session.submitMulti(request);
            } else {
                response = session.submitMulti(request);
                System.out.println("Submit Multi response " + response.debugString());
                messageId = response.getMessageId();
            }

            status = true;
        } catch (Exception e) {
        	event.write(e,"");
            debug.write("Name: "+ config.getString("NAME")+" IP: "+ config.getString("LOGIN_IP") +"\nException: Submit Multi failed.\nTrying for new connection...");
            System.out.println("Name: "+ config.getString("NAME")+" IP: "+ config.getString("LOGIN_IP") +"\nException: Submit Multi failed.\nTrying for new connection...");
            bound = false;
            TryConnection++;
            if(TryConnection<Trycount){
            	try{
                	Thread.sleep(10*1000);
                	bind();
                }catch (Exception ex) {
					ex.printStackTrace();
				}       	
            }else{
            	event.write("Submit Multi failed. Try Count Exceeded");
            	debug.write("Name: "+ config.getString("NAME")+" IP: "+ config.getString("LOGIN_IP") +"\nException: Submit Multi failed.\nTry Count Exceeded: "+TryConnection+ "\nGoing for sleep for about 30sec");
                System.out.println("Name: "+ config.getString("NAME")+" IP: "+ config.getString("LOGIN_IP") +"\nException: Submit Multi failed.\ngoing for sleep Program\nTry Count Exceted: "+TryConnection);
                // System.exit(1);
                
                try{
                	Thread.sleep(30*1000);
                	TryConnection = 0;
                	bind();
                }catch (Exception ex) {
					
				}
                
            }
		} finally {
            debug.exit(this);
        }
        
        return status;
    }
    
    /**
	 * Receive messages in queue
	 * 
	 * @return 2 element array 0:number, 1:message
	 */
	public synchronized PDU receive()
    {

    	//String Dat[] = null;
    	PDU pdu = null;
    	debug.enter(this, "receive()");
        
        try {
        	
        	session.enquireLink();
        	
            System.out.print("Going to receive a PDU. ");
            if (receiveTimeout == Data.RECEIVE_BLOCKING) {
                System.out.print("The receive is blocking, i.e. the application "+
                                 "will stop until a PDU will be received.");
                debug.write("The receive is blocking, i.e. the application "+
                "will stop until a PDU will be received.");
            } else {
                System.out.print("The receive timeout is "+receiveTimeout/1000+" sec.");
                debug.write("The receive timeout is "+receiveTimeout/1000+" sec.");
            }
            System.out.println();
            if (isSynchronous) {
                ServerPDUEvent pduEvent =
                    pduListener.getRequestEvent(receiveTimeout);
                if (pduEvent != null) {
                    pdu = pduEvent.getPDU();
                }
            } else {
                pdu = session.receive(receiveTimeout);
            }
            if (pdu != null) {
                System.out.println("Received PDU "+pdu.debugString());
                if (pdu.isRequest()) {
                	
                	return pdu;
                	/*// System.out.println(pdu);
                    Response response = ((Request)pdu).getResponse();
                    // respond with default response
                    System.out.println("Going to send default response pdu to request "+response.debugString());
                    debug.write("Going to send default response pdu to request "+response.debugString());
                    session.respond(response);
                    
                    Dat = new String[2];
                    Dat[0] = new String(((DeliverSM)pdu).getSourceAddr().getAddress());
                    Dat[1] = new String(((DeliverSM)pdu).getShortMessage());
                    */
                    
                    //((DeliverSM)pdu).getReceiptedMessageId();
                    //((DeliverSM)pdu).getSequenceNumber();
                    //((DeliverSM)pdu).getShortMessage(encoding);
                    
                }
            } else {
                System.out.println("No PDU received this time.");
                debug.write("No PDU received this time.");
            }

        } catch (Exception e) {
        	event.write(e,"Exception: Receiving failed.");
            
            
            bound = false;
            TryConnection++;
            
            debug.write("Name: "+ config.getString("NAME")+" IP: "+ config.getString("LOGIN_IP") +"\nException: Receiving failed.\nTrying for new connection...\n Try Num: "+TryConnection);
            System.out.println("Name: "+ config.getString("NAME")+" IP: "+ config.getString("LOGIN_IP") +"\nException: Receiving failed.\nTrying for new connection...\n Try Num: "+TryConnection);
            if(TryConnection<Trycount){
            	event.write("Receiving failed. Trying for new Connection");
            	try{
                	Thread.sleep(10*1000);
                	//TryConnection = 0;
                	bind();
                }catch (Exception ex) {
					ex.printStackTrace();
				}
            }else{
            	event.write("Name: "+ config.getString("NAME")+" IP: "+ config.getString("LOGIN_IP") +"\nReceiving failed. Try Count Exceeded");
            	debug.write("Name: "+ config.getString("NAME")+" IP: "+ config.getString("LOGIN_IP") +"\nException: Receiving failed.\nTry Count Exceeded: "+TryConnection+ "\nGoing for sleep for about 30sec");
                System.out.println("Name: "+ config.getString("NAME")+" IP: "+ config.getString("LOGIN_IP") +"\nException: Receiving failed.\ngoing for sleep Program\nTry Count Exceted: "+TryConnection);
                // System.exit(1);
                
                try{
                	Thread.sleep(30*1000);
                	TryConnection = 0;
                	bind();
                }catch (Exception ex) {
					ex.printStackTrace();
				}
                
            }
		} finally {
            debug.exit(this);
        }
        return pdu;
    }
    
    public void ReceiveAck(PDU pdu){
    	
    	debug.enter(this, "ReceiveAck()");
    	
    	if (pdu.isRequest()) {
        	// System.out.println(pdu);
            Response response = ((Request)pdu).getResponse();
            // respond with default response
            System.out.println("Going to send default response pdu to request "+response.debugString());
            debug.write("Going to send default response pdu to request "+response.debugString());
            try {
				session.respond(response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				event.write(e,"Exception: ReceiveAck failed.");
	            
	            
	            debug.write("Name: "+ config.getString("NAME")+" IP: "+ config.getString("LOGIN_IP") +"\nException: ReceiveAck failed.");
	            System.out.println("Name: "+ config.getString("NAME")+" IP: "+ config.getString("LOGIN_IP") +"\nException: ReceiveAck failed.");
			} finally {
	            debug.exit(this);
	        }
            
            //((DeliverSM)pdu).getReceiptedMessageId();
            //((DeliverSM)pdu).getSequenceNumber();
            //((DeliverSM)pdu).getShortMessage(encoding);
            
        }
    }

}
