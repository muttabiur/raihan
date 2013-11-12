/* ----------------------------------------------------------------------------
 Copyright (c) 2003 jSNMP Enterprises, All Rights Reserved Worldwide
 ------------------------------------------------------------------------------

   Disclaimer: This software is provided AS IS, and without any warranty
               other than those which may be provided in a separate
               writing specifically referencing the software contained
               herein.  All other warranties, except those which may be
               provided separately in writing, are expressly disclaimed.
               WITHOUT LIMITING THE GENERALITY OF THE FOREGOING, THERE IS
               NO WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR
               PURPOSE which is given with respect to this software or the
               operability thereof.

   ------------------------------------------------------------------------
 */


// ------------------------------ Package -------------------------------------
package SNMPTest ;
// ------------------------------ Imports -------------------------------------
import java.net.*;
import java.io.UnsupportedEncodingException;

import com.outbackinc.services.protocol.snmp.*;
import com.outbackinc.services.protocol.snmp.CSM.*;

/**
 * Sample which illustrates how to use the SnmpService to place
 * get-next orders.  In this case, the walker walks a table on
 * the remote until the end of the table is reached.  If the table OID
 * aregument is NOT a table OID, results are unpredictable.
 * 
 *  <pre>
 *  For example:
 *      >java SnmpV2GetBulkTableWalkerTest snmphost readcommunity 1.3.6.1.2.1.2.2
 * 
 *  You should get something like ...
 *      Walking MIB Tree of snmphost starting at OID 1.3.6.1.2.1.2.2 using SNMPv2
 *      ******************************************************
 *      ************** jSNMP EVALUATION VERSION **************
 *      ******** EXPIRES Thu Dec 31 00:00:00 PST 2099 ********
 *      ******************************************************
 *      1.3.6.1.2.1.2.2.1.1.1 (1)
 *      1.3.6.1.2.1.2.2.1.1.2 (2)
 *      1.3.6.1.2.1.2.2.1.2.1 (lo0)
 *      1.3.6.1.2.1.2.2.1.2.2 (eth0)
 *      1.3.6.1.2.1.2.2.1.3.1 (24)
 *      1.3.6.1.2.1.2.2.1.3.2 (6)
 *      1.3.6.1.2.1.2.2.1.4.1 (3924)
 *      1.3.6.1.2.1.2.2.1.4.2 (1500)
 *      1.3.6.1.2.1.2.2.1.5.1 (10000000)
 *      1.3.6.1.2.1.2.2.1.5.2 (10000000)
 *      1.3.6.1.2.1.2.2.1.6.1 ()
 *      1.3.6.1.2.1.2.2.1.6.2 (0x00,0xa0,0xc9,0x49,0x31,0xb0)
 *      1.3.6.1.2.1.2.2.1.7.1 (1)
 *      1.3.6.1.2.1.2.2.1.7.2 (1)
 *      1.3.6.1.2.1.2.2.1.8.1 (1)
 *      1.3.6.1.2.1.2.2.1.8.2 (1)
 *      1.3.6.1.2.1.2.2.1.10.1 (793171)
 *      1.3.6.1.2.1.2.2.1.10.2 (507992524)
 *      1.3.6.1.2.1.2.2.1.11.1 (5694)
 *      1.3.6.1.2.1.2.2.1.11.2 (2822767)
 *      1.3.6.1.2.1.2.2.1.14.1 (0)
 *      1.3.6.1.2.1.2.2.1.14.2 (0)
 *      1.3.6.1.2.1.2.2.1.16.1 (793171)
 *      1.3.6.1.2.1.2.2.1.16.2 (40074440)
 *      1.3.6.1.2.1.2.2.1.17.1 (5694)
 *      1.3.6.1.2.1.2.2.1.17.2 (245058)
 *      1.3.6.1.2.1.2.2.1.19.1 (0)
 *      1.3.6.1.2.1.2.2.1.19.2 (0)
 *      1.3.6.1.2.1.2.2.1.20.1 (0)
 *      1.3.6.1.2.1.2.2.1.20.2 (0)
 *      1.3.6.1.2.1.2.2.1.21.1 (0)
 *      1.3.6.1.2.1.2.2.1.21.2 (0)
 *      1.3.6.1.2.1.2.2.1.22.1 (0.0.0)
 *      1.3.6.1.2.1.2.2.1.22.2 (0.0.0)
 *  </pre>
 */
public class SnmpV2GetBulkTableWalkerTest
    implements SnmpCustomer
{
    // ------------------------------ Constants ---------------------------------
    private final static int            MAX_REPETITIONS      = 20;

    // ------------------------------ Class variables ---------------------------

    // ------------------------------ Member (instance) variables ---------------

    private SnmpService                             m_service;
    private SnmpServiceConfiguration                m_serviceConfiguration;
    private String                                  m_szStartOID;
    private String                                  m_szLastOID;
    private SnmpAuthoritativeSession                m_cSnmpRemoteAuthoritativeSession;
    private SnmpOrderInfo                           m_orderinfo;
    private int                                     m_iOrderNumStart;
    private SnmpVarBind                             m_cSnmpVarBinds[];
    private int                                     m_iSnmpVarBindsCapacity;
    private int                                     m_iSnmpVarBindsCount;
    
    // ------------------------------ Methods -----------------------------------

    /**
     * Constructor
     */
    public SnmpV2GetBulkTableWalkerTest(String szHost, String szReadCommunity, String szLastOID)
    {
        //Sets the initial OID to something which should be before the table
        m_szLastOID = szLastOID;
        m_szStartOID = szLastOID;
        m_iSnmpVarBindsCapacity = MAX_REPETITIONS;
        m_iSnmpVarBindsCount = 0;
        m_cSnmpVarBinds = new SnmpVarBind[m_iSnmpVarBindsCapacity];

        //Initialize the Snmp Service
        try
        {
            m_service = SnmpLocalInterfaces.getService();
        }
        catch (SocketException se)
        {
            System.out.println("Unable to initialize the Snmp service (" + se.getMessage() + ").");
            return;
        }
        //This sample app runs synchronously--each request completes before the next 
        //starts--so there is no possibility of packing PDU's and therefore no benefit 
        //for having BufferDelay > 0. We change it through the SnmpServiceConfiguration API.
        m_serviceConfiguration = SnmpLocalInterfaces.getServiceConfiguration(m_service);
        m_serviceConfiguration.setBufferDelay(0);
        m_orderinfo = new SnmpOrderInfo(2, 3, 0);
        CSMSecurityInfo secInfo;
        try
        {
            secInfo = new CSMSecurityInfo(szReadCommunity.getBytes("ASCII"), szReadCommunity.getBytes("ASCII"));
        }
        catch(UnsupportedEncodingException uee)
        {
            secInfo = new CSMSecurityInfo(szReadCommunity.getBytes(), szReadCommunity.getBytes());
        }
                    
        SnmpAuthoritativeSessionFactory cSnmpAuthoritativeSessionFactory = SnmpLocalInterfaces.getAuthoritativeSessionFactory();
        try
        {
            m_cSnmpRemoteAuthoritativeSession = cSnmpAuthoritativeSessionFactory.createRemoteAuthoritativeSession(szHost,
                                                                                                     161,
                                                                                                     SnmpConstants.SNMP_VERSION_2,
                                                                                                     secInfo);
        }
        catch (SnmpSecurityException sse)
        {
            System.out.println("Unable to create secure session with remote agent " + szHost + " (" + sse.getMessage() + ").");
            m_service.stop();
            return;
        }
        catch (UnknownHostException uhe)
        {
            System.out.println("Unable to create session with remote agent as host (" + szHost + ") is unknown.");
            m_service.stop();
            return;
        }

        m_iOrderNumStart = 1;

        //Starts the walker walking
        System.out.println("Walking MIB Tree of " + szHost + " starting at OID " + szLastOID + " using SNMPv2");
        getBulk();
    }

    /**
     * Main method to start execution
     *
     * @param szArgs contains cmdline args, first of which must be the szHost, second must be read community
     *
     */
    public static void main(String[] szArgs)
    {
        if (szArgs.length < 3)
        {
            usage();
            return;
          }

        String szHost = szArgs[0];
        
        try
        {
            String szReadCommunity = szArgs[1];
            String szTableOID = szArgs[2];

            //Creates new walker
            SnmpV2GetBulkTableWalkerTest test = new SnmpV2GetBulkTableWalkerTest(szHost, szReadCommunity, szTableOID);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *
     * Displays correct usage
     */
    public static void usage()
    {
        System.out.println("Usage : java SnmpV2GetBulkTableWalkerTest <hostname readcommunity tableOID>");
        System.out.println("\thostname : host on which agent resides to walk");
        System.out.println("\treadcommunity : read community of agent to walk");
        System.out.println("\ttableOID : table OID to walk");
    }


    //SnmpCustomer Implementation

    /**
     * add varbind to table
     */
    private synchronized void addVarBind(SnmpVarBind vb)
    {
        if (m_iSnmpVarBindsCount == m_iSnmpVarBindsCapacity)
        {
            int iSnmpVarBindsCapacity = m_iSnmpVarBindsCapacity * 2;
            SnmpVarBind cSnmpVarBinds[] = new SnmpVarBind[iSnmpVarBindsCapacity];
            for (int i = 0; i < m_iSnmpVarBindsCapacity; i++)
            {
                cSnmpVarBinds[i] = m_cSnmpVarBinds[i];
            }
            
            m_cSnmpVarBinds = cSnmpVarBinds;
            m_iSnmpVarBindsCapacity = iSnmpVarBindsCapacity;
        }
        
        m_cSnmpVarBinds[m_iSnmpVarBindsCount] = vb;
        m_iSnmpVarBindsCount++;
    }
    
    /**
     * print table
     */
    private synchronized void printVarBinds()
    {
        for (int i = 0; i < m_iSnmpVarBindsCount; i++)
        {
            System.out.println(m_cSnmpVarBinds[i].getName() + " (" + m_cSnmpVarBinds[i].getStringValue() + ")");
        }
        
        System.out.println(m_iSnmpVarBindsCount + " objects in table " + m_szStartOID);
    }
    
    /**
     * callback for delivering successes.  In this case the implementation
     * is to print out the Object ID received and then use it for the basis
     * for the get-next request
     */
    public void deliverSuccessfulOrder(int iOrderNum,
                                       SnmpVarBind vb)
    {
        if (m_szLastOID.compareTo(vb.getName()) != 0
            && vb.getName().startsWith(m_szStartOID))
        {
            addVarBind(vb);
            m_szLastOID = vb.getName();
            if (m_iOrderNumStart == (iOrderNum + 1))
            {
                getBulk();
            }
        }
        else
        {
            printVarBinds();
            m_service.stop();
            System.exit(0);
        }
    }

    /**
     * callback for failures.  In this case if a failure is received,
     * stop walking the MIB
     */
    public void deliverFailedOrder(int iOrderNum, int iErrorStatus)
    {
        System.out.println(iOrderNum + ": order failed (" + SnmpConstants.errorIDToString(iErrorStatus) + ")");
        if (iErrorStatus != SnmpConstants.SNMP_ERR_RESPONSENOTGENERATED)
        {
            printVarBinds();
            m_service.stop();
            System.exit(0);
        }

        if (m_iOrderNumStart == (iOrderNum + 1))
        {
            getBulk();
        }
    }

    /**
     * gets the next object from the remote agent.  Demonstrates
     * placing orders with the snmp service
     */
    public void getBulk()
    {
        String[] szNonRepeaterOIDs = new String[0];
        String[] szRepeaterOIDs = new String[1];
        szRepeaterOIDs[0] = m_szLastOID;
                    
        m_iOrderNumStart = m_service.placeGetBulkOrder(m_cSnmpRemoteAuthoritativeSession,
                                                       m_orderinfo,
                                                       szNonRepeaterOIDs,
                                                       szRepeaterOIDs,
                                                       MAX_REPETITIONS,
                                                       this,
                                                       m_iOrderNumStart);
    }
}


