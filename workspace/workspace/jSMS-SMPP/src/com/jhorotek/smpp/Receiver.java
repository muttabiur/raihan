package com.jhorotek.smpp;

import org.apache.commons.configuration.XMLConfiguration;

import com.logica.smpp.Data;
import com.logica.smpp.ServerPDUEvent;
import com.logica.smpp.ServerPDUEventListener;
import com.logica.smpp.Session;
import com.logica.smpp.TCPIPConnection;
import com.logica.smpp.debug.Debug;
import com.logica.smpp.debug.Event;
import com.logica.smpp.debug.FileDebug;
import com.logica.smpp.debug.FileEvent;
import com.logica.smpp.pdu.*;

/**
 * @author AdnaN
 * 
 */
class Receiver {

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
	 * 
	 * @see FileDebug
	 */
	Debug debug = null;

	/**
	 * The event object.
	 * 
	 * @see FileEvent
	 */
	Event event = null;

	/**
	 * This is the SMPP session used for communication with SMSC.
	 */
	Session session = null;

	/**
	 * This is an instance of listener which obtains all PDUs received from the
	 * SMSC. Application doesn't have explicitly call Session's receive()
	 * function, all PDUs are passed to this application callback object. See
	 * documentation in Session, Receiver and ServerPDUEventListener classes
	 * form the SMPP library.
	 */
	PDUEventListener pduListener = null;

	/**
     * The range of addresses the smpp session will serve.
     */
    public AddressRange addressRange = new AddressRange();
    
	/**
     * If you attempt to receive message, how long will the application
     * wait for data.
     */
    long receiveTimeout = Data.RECEIVE_BLOCKING;
    
    /**
	 * If the Connection should be asynchronous to the SMSC.
	 */
	boolean isSynchronous = false;
	
	/**
	 * If the application is bound to the SMSC.
	 */
	boolean bound = false;

	/**
	 * Configurations for SMSC.
	 */
	XMLConfiguration config = null;

	/**
	 * Name for this SMSC.
	 */
	String name = null;

	/**
	 * sent msg id.
	 */
	String messageId = "";

	/**
	 * BindRequest to SMSC
	 */
	private BindRequest request = null;

	/**
	 * BindResponse to SMSC
	 */
	private BindResponse response = null;

	/**
	 * constructor
	 */
	public Receiver(XMLConfiguration conf, boolean debuglog) {
		config = conf;
		name = config.getString("NAME");
		debug = new FileDebug(dbgDir, name + "_receive.dbg");
		event = new FileEvent(dbgDir, name + "_receive.evt");

		if (!debuglog) {
			debug.deactivate();
			event.deactivate();
		}
		// System.out.print(config.destAddress.getNpi());

	
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
	 * connect as a Transmitter
	 */
	public void Connect() {
		bind();
	}

	/**
	 * disconnects From SMSC
	 */
	public void Disconnect() {
		unbind();
	}

	/**
	 * The first method called to start communication betwen an ESME and a SMSC.
	 * A new instance of <code>TCPIPConnection</code> is created and the IP
	 * address and port obtained from user are passed to this instance. New
	 * <code>Session</code> is created which uses the created
	 * <code>TCPIPConnection</code>. All the parameters required for a bind
	 * are set to the <code>BindRequest</code> and this request is passed to
	 * the <code>Session</code>'s <code>bind</code> method. If the call is
	 * successful, the application should be bound to the SMSC.
	 * 
	 * See "SMPP Protocol Specification 3.4, 4.1 BIND Operation."
	 * 
	 * @see BindRequest
	 * @see BindResponse
	 * @see TCPIPConnection
	 * @see Session#bind(BindRequest)
	 * @see Session#bind(BindRequest,ServerPDUEventListener)
	 */
	private synchronized void bind() {
		debug.enter(this, "Receiver.bind()");
		try {

			if (bound) {
				System.out.println("Already bound, unbind first.");
				debug.write("Already bound, unbind first.");
				return;
			}

			request = new BindReceiver();
			debug.write("new BindReceiver()");

			TCPIPConnection connection = new TCPIPConnection(config.getString("LOGIN_IP"), config.getInt("LOGIN_PORT"));
			connection.setReceiveTimeout(20 * 1000);
			debug.write("TCPIPConnection");
			session = new Session(connection);
			debug.write("new Session(connection)");

			// set values
			request.setSystemId(config.getString("SMPP_USER"));
            request.setPassword(config.getString("SMPP_PASSWORD"));
            request.setSystemType(config.getString("SYSTEM_TYPE"));
            request.setInterfaceVersion((byte)0x34);
            request.setAddressRange(addressRange);
			debug.write("set all values done");

			// send the request
			System.out.println("Bind request " + request.debugString());
			debug.write("Bind request " + request.debugString());
			if (isSynchronous) {
				pduListener = new PDUEventListener(session);
				response = session.bind(request, pduListener);
				debug.write("Made synchronous Connection");
			} else {
				response = session.bind(request);
				debug.write("Made Asynchronous Connection");
			}

			System.out.println("Bind response " + response.debugString());
			debug.write("Bind response " + response.debugString());
			// if (response.getCommandStatus() == Data.ESME_ROK) {
			if (response.isOk()) {
				bound = true;
				TryConnection = 0;
				debug.write("Connection ok. bound");
			}

		} catch (Exception e) {
			event.write(e, "");
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
	 * 
	 * @see Session#unbind()
	 * @see Unbind
	 * @see UnbindResp
	 */
	private void unbind() {
		debug.enter(this, "Receiver.unbind()");
		try {

			if (!bound) {
				System.out.println("Not bound, cannot unbind.");
				return;
			}

			// send the request
			System.out.println("Going to unbind.");
			if (session.getReceiver().isReceiver()) {
				System.out.println("It can take a while to stop the receiver.");
			}
			UnbindResp response = session.unbind();
			System.out.println("Unbind response " + response.debugString());
			bound = false;

		} catch (Exception e) {
			event.write(e, "");
			debug.write("Unbind operation failed. " + e);
			System.out.println("Unbind operation failed. " + e);
		} finally {
			debug.exit(this);
		}
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
    	debug.enter(this, "Receiver.receive()");
        
        try {
        	
        	session.enquireLink();
        	
            
            System.out.print("Going to receive a PDU. ");
            if(receiveTimeout == Data.RECEIVE_BLOCKING) {
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
                    System.out.println("Going to send default response to request "+response.debugString());
                    debug.write("Going to send default response to request "+response.debugString());
                    session.respond(response);
                    
                    Dat = new String[2];
                    Dat[0] = new String(((DeliverSM)pdu).getSourceAddr().getAddress());
                    Dat[1] = new String(((DeliverSM)pdu).getShortMessage());*/
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
            	event.write("Receiving failed. Try Count Exceeded");
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
				//e.printStackTrace();
				event.write(e,"Exception: ReceiveAck failed.");
	            
	            
	            debug.write("Exception: ReceiveAck failed.");
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