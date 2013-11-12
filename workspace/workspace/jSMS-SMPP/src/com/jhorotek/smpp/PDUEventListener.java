package com.jhorotek.smpp;

import com.logica.smpp.ServerPDUEvent;
import com.logica.smpp.ServerPDUEventListener;
import com.logica.smpp.Session;
import com.logica.smpp.SmppObject;
import com.logica.smpp.pdu.PDU;
import com.logica.smpp.util.Queue;
//import com.logica.smpp.pdu.*;

/**
 * Implements simple PDU listener which handles PDUs received from SMSC.
 * It puts the received requests into a queue and discards all received
 * responses. Requests then can be fetched (should be) from the queue by
 * calling to the method <code>getRequestEvent</code>.
 * @see Queue
 * @see ServerPDUEvent
 * @see ServerPDUEventListener
 * @see SmppObject
 */
class PDUEventListener
extends SmppObject
implements ServerPDUEventListener
{
    Session session;
    Queue requestEvents = new Queue();

    public PDUEventListener(Session session)
    {
        this.session = session;
    }

    public void handleEvent(ServerPDUEvent event)
    {
        PDU pdu = event.getPDU();
        if (pdu.isRequest()) {
            //System.out.println("async request received, enqueuing "+ pdu.debugString());
            
            //System.out.println("msg: " + ((DeliverSM)pdu).getShortMessage());
            //System.out.println("num: " + ((DeliverSM)pdu).getSourceAddr().getAddress());
            
            
            synchronized (requestEvents) {
                requestEvents.enqueue(event);
                requestEvents.notify();
            }
        } else if (pdu.isResponse()) {
            System.out.println("async response received "+
                               pdu.debugString());
        } else {
            System.out.println("pdu of unknown class (not request nor "+
                               "response) received, discarding "+
                               pdu.debugString());
        }
    }

    /**
     * Returns received pdu from the queue. If the queue is empty,
     * the method blocks for the specified timeout.
     */
    public ServerPDUEvent getRequestEvent(long timeout)
    {
        ServerPDUEvent pduEvent = null;
        synchronized (requestEvents) {
            if (requestEvents.isEmpty()) {
                try {
                    requestEvents.wait(timeout);
                } catch (InterruptedException e) {
                    // ignoring, actually this is what we're waiting for
                }
            }
            if (!requestEvents.isEmpty()) {
                pduEvent = (ServerPDUEvent)requestEvents.dequeue();
            }
        }
        return pduEvent;
    }
}