/* ----------------------------------------------------------------------------
 Copyright (c) 2004 jSNMP Enterprises, All Rights Reserved Worldwide
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

// ------------------------------ Imports -------------------------------------
package SNMPTest ;
import java.net.*;
import java.io.*;
import java.math.BigInteger;

import com.outbackinc.services.protocol.snmp.*;
import com.outbackinc.services.protocol.snmp.mib.*;
import com.outbackinc.services.protocol.snmp.CSM.*;

/**
 * Sample which illustrates how to use the SnmpService to place
 * get-next orders.  In this case, the walker walks the remote
 * agent's MIB until the end is reached.
 */
public class SnmpV1WalkerTest
    implements SnmpCustomer
{
    // ------------------------------ Constants ---------------------------------

    // ------------------------------ Class variables ---------------------------

    // ------------------------------ Member (instance) variables ---------------

    private SnmpService                             m_service;
    private SnmpServiceConfiguration                m_serviceConfiguration;
    private String[]                                m_szLastOID;
    private SnmpAuthoritativeSession                m_cSnmpRemoteAuthoritativeSession;
    private SnmpOrderInfo                           m_orderinfo;
    private int                                     m_iOrderNumStart;
    private SnmpMIBDictionary[]                     m_cSnmpMIBDictionaryList;
    private SnmpMIBService                          m_snmpMibService;
    
    // ------------------------------ Methods -----------------------------------

    /**
     * Constructor
     */
    public SnmpV1WalkerTest(String szHost, String szReadCommunity)
    {
        //Sets the initial OID to something which should be before the table
        m_szLastOID = new String[1];
        m_szLastOID[0] = "1.3.6";

        // load a mib dictionary so we can request by name
        m_snmpMibService = SnmpLocalInterfaces.getMIBService();
        try
        {
            InputStream istream = jMIBC.loadMib("HOST-RESOURCES-MIB.my");
            m_snmpMibService.loadMIB("HOST-RESOURCES-MIB", istream);
        }
        catch( Exception e )
        {
            System.out.println("Couldn't load the HOST-RESOURCES-MIB MIB into the SnmpMIBService ... " + e.getMessage() + ".\n");
        }

        // get of all the MIBs in the service for later use
        String[] szMibs = m_snmpMibService.listAvailableMIBs();
        m_cSnmpMIBDictionaryList = new SnmpMIBDictionary[szMibs.length];
        for (int i=0; i < szMibs.length; i++) 
        {
            m_cSnmpMIBDictionaryList[i] = m_snmpMibService.getMIBDictionary(szMibs[i]);
        }

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
									                                     SnmpConstants.SNMP_VERSION_1,
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
        System.out.println("Walking MIB Tree of " + szHost + " using SNMPv1");
        getNext();
    }

    /**
     * Main method to start execution
     *
     * @param szArgs contains cmdline args, first of which must be the szHost, second must be read community
     *
     */
    public static void main(String[] szArgs)
    {
		if (szArgs.length < 2)
		{
			usage();
			return;
  		}

        String szHost = szArgs[0];
        
        try
        {
            String szReadCommunity = szArgs[1];

            //Creates new walker
            SnmpV1WalkerTest test = new SnmpV1WalkerTest(szHost, szReadCommunity);
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
		System.out.println("Usage : java SnmpV1WalkerTest <hostname readcommunity>");
		System.out.println("\thostname : host or IP address on which agent resides to walk");
		System.out.println("\treadcommunity : read community of agent to walk");
	}


    //SnmpCustomer Implementation

    /**
     * callback for delivering successes.  In this case the implementation
     * is to print out the Object ID received and then use it for the basis
     * for the get-next request
     */
    public void deliverSuccessfulOrder(int iOrderNum,
                                       SnmpVarBind vb)
    {

        String szOID = vb.getName();
        boolean bFound = false;
        for (int i = 0; i < m_cSnmpMIBDictionaryList.length; i++)
        {
            String szName = m_cSnmpMIBDictionaryList[i].resolveOID(szOID);
            if (szName != null)
            {
                String szInfo = m_cSnmpMIBDictionaryList[i].resolveOIDType(szOID);
                if (szInfo != null)
                {
                    if (szInfo.compareTo("regPt") == 0)
                    {
                        continue;
                    }

                    System.out.println(szOID + " as " + szName + " found in MIB " + m_cSnmpMIBDictionaryList[i].getMIBDictionaryName()); 
                    if (szInfo.compareTo("Enum") == 0)
                    {
                        int intValue = ((BigInteger)vb.getValue()).intValue();
                        String szEnumValue = m_cSnmpMIBDictionaryList[i].resolveEnum(szName, intValue);
                        if (szEnumValue != null)
                            System.out.println(szName + " (" + szOID + ") value = " + szEnumValue + " (" + intValue + ")"); 
                        else
                            System.out.println(szName + " (" + szOID + ") value = " + vb.getStringValue() + " (couldn't resolve the string value of enum)"); 
                    }
                    else
                    {
                        System.out.println(szName + " (" + szOID + ") value = " + vb.getStringValue()); 
                    }
                        
                    System.out.println(szName + " (" + szOID + ") type = " + szInfo);
                    szInfo = m_cSnmpMIBDictionaryList[i].resolveOIDAbstractType(szOID);
                    System.out.println(szName + " (" + szOID + ") abstract type = " + szInfo);
                }
                else
                {
                    continue;
                }
                
                szInfo = m_cSnmpMIBDictionaryList[i].resolveOIDAccess(szOID);
                if (szInfo != null)
                    System.out.println(szName + " (" + szOID + ") access = " + szInfo);
                
                szInfo = m_cSnmpMIBDictionaryList[i].resolveOIDStatus(szOID);
                if (szInfo != null)
                    System.out.println(szName + " (" + szOID + ") status = " + szInfo);
                    
                szInfo = m_cSnmpMIBDictionaryList[i].resolveOIDDescription(szOID);
                if (szInfo != null)
                    System.out.println(szName + " (" + szOID + ") description = " + szInfo);
                bFound = true;
                break;
            }
        }
        
        if (!bFound)
        {
            System.out.println(szOID + " not found in any loaded MIB"); 
            System.out.println(szOID + " value = " + vb.getStringValue()); 
        }

        if (m_szLastOID[0].compareTo(szOID) != 0)
        {
            System.out.println(iOrderNum + ": " + szOID + " (" + vb.getStringValue() + ")");
            m_szLastOID[0] = szOID;
            getNext();
        }
        else
        {
            System.out.println("Finished");
            m_service.stop();
        }
    }

    /**
     * callback for failures.  In this case if a failure is received,
     * stop walking the MIB
     */
    public void deliverFailedOrder(int iOrderNum, int iErrorStatus)
    {
        System.out.println(iOrderNum + ": order failed (" + SnmpConstants.errorIDToString(iErrorStatus) + ")");
        System.out.println("Ending MIB Walk");
        m_service.stop();
    }

    /**
     * gets the next object from the remote agent.  Demonstrates
     * placing orders with the snmp service
     */
    public void getNext()
    {
        m_iOrderNumStart = m_service.placeGetNextOrder(m_cSnmpRemoteAuthoritativeSession,
                                                       m_orderinfo,
                                                       false,
                                                       m_szLastOID,
                                                       this,
                                                       m_iOrderNumStart);
    
    }
}

