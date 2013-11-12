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

// ------------------------------ Imports -------------------------------------

package SNMPTest ;
import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.io.UnsupportedEncodingException;

import com.outbackinc.services.protocol.snmp.*;
import com.outbackinc.services.protocol.snmp.mib.*;
import com.outbackinc.services.protocol.snmp.CSM.*;

/**
 * Sample which illustrates how to use the SnmpService to place
 * a get order using the MIB dictionary service.
 */
public class SnmpV1GetSysInfo
    implements SnmpCustomer
{
    // ------------------------------ Constants ---------------------------------

    // ------------------------------ Class variables ---------------------------
    private static final int    SYSNAME                       = 1;
    private static final int    SYSCONTACT                    = 2;
    private static final int    SYSLOCATION                   = 4;
    private static final int    IFDESCR                       = 8;
    private static final int    IFADMINSTATUS                 = 16;
    private static final int    HRSYSTEMUPTIME                = 32;
    private static final int    HRSYSTEMDATE                  = 64;
    private static final int    HRSYSTEMINITIALLOADDEVICE     = 128;
    private static final int    HRSYSTEMINITIALLOADPARAMETERS = 256;
    private static final int    HRSYSTEMNUMUSERS              = 512;
    private static final int    HRSYSTEMPROCESSES             = 1024;
    private static final int    HRSYSTEMMAXPROCESSES          = 2048;
    private static final int    HRMEMORYSIZE                  = 4096;
    private static final int    ALL             = SYSNAME + 
                                                  SYSCONTACT + 
                                                  SYSLOCATION + 
                                                  IFDESCR + 
                                                  IFADMINSTATUS +
                                                  HRSYSTEMUPTIME +
                                                  HRSYSTEMDATE +
                                                  HRSYSTEMINITIALLOADDEVICE +
                                                  HRSYSTEMINITIALLOADPARAMETERS +
                                                  HRSYSTEMNUMUSERS +
                                                  HRSYSTEMPROCESSES +
                                                  HRSYSTEMMAXPROCESSES +
                                                  HRMEMORYSIZE;

    // ------------------------------ Member (instance) variables ---------------

    private SnmpService                             m_service;
    private SnmpServiceConfiguration                m_serviceConfiguration;
    private SnmpMIBService                          m_snmpMibService;
    private SnmpMIBDictionary[]                     m_cSnmpMIBDictionaryList;
    private String[]                                m_szLastOID;
    private SnmpAuthoritativeSession                m_cSnmpRemoteAuthoritativeSession;
    private SnmpOrderInfo                           m_orderinfo;
    private int                                     m_iOrderNumStart;
    private int                                     m_recd;

    protected boolean                               m_recdFailedOrder = false;
    
    // ------------------------------ Methods -----------------------------------

    /**
     * Constructor
     */
    public SnmpV1GetSysInfo(String szHost, String szReadCommunity)
    {
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

        //Set the OID we want to retrieve
        m_szLastOID = new String[13];
        m_szLastOID[0] = "sysName.0";
        m_szLastOID[1] = "sysContact.0";
        m_szLastOID[2] = "sysLocation.0";
        m_szLastOID[3] = "ifDescr.1";
        m_szLastOID[4] = "ifAdminStatus.1";
        m_szLastOID[5] = "hrSystemUptime.0";
        m_szLastOID[6] = "hrSystemDate.0";
        m_szLastOID[7] = "hrSystemInitialLoadDevice.0";
        m_szLastOID[8] = "hrSystemInitialLoadParameters.0";
        m_szLastOID[9] = "hrSystemNumUsers.0";
        m_szLastOID[10] = "hrSystemProcesses.0";
        m_szLastOID[11] = "hrSystemMaxProcesses.0";
        m_szLastOID[12] = "hrMemorySize.0";
            
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

        // set a buffer delay to enhance the network efficiency in requesting these three OIDs
        SnmpServiceConfiguration serviceConfig = SnmpLocalInterfaces.getServiceConfiguration(m_service);
        serviceConfig.setBufferDelay(100);

        //Specify order details..
        // timeouts, retries, cache threshold
        m_orderinfo = new SnmpOrderInfo(2, 3, 0);
        // authentication (v1 read community from command-line)
        CSMSecurityInfo secInfo;
        try
        {
            secInfo = new CSMSecurityInfo(szReadCommunity.getBytes("ASCII"), szReadCommunity.getBytes("ASCII"));
        }
        catch(UnsupportedEncodingException uee)
        {
            secInfo = new CSMSecurityInfo(szReadCommunity.getBytes(), szReadCommunity.getBytes());
        }
        
        // then create a session that can be used for multiple requests
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
        m_recd = 0;
    }

    /**
     * gets the next object from the remote agent.  Demonstrates
     * placing orders with the snmp service.
     */
    public void get()
    {
        m_iOrderNumStart = m_service.placeGetOrder(m_cSnmpRemoteAuthoritativeSession,
                                                   m_orderinfo,
                                                   false,
                                                   m_szLastOID,
                                                   this,
                                                   m_iOrderNumStart);
    
        // now wait for callback from SnmpService via deliverSuccessfulOrder or deliverFailedOrder
    }

    //SnmpCustomer Implementation

    /**
     * handle successful deliveries
     * delivered.
     */
    synchronized private void handleSuccessfulOrder(int iOrderNum,
                                                    SnmpVarBind vb)
    {
        String szObjectName = null;
        String szObjectIndexedName = null;
        // the orders come back in the order they are given
        switch (iOrderNum)
        {
            case 1:
                szObjectIndexedName = "sysName.0";
                szObjectName = "sysName";
                m_recd |= SYSNAME;
                break;
            case 2:
                szObjectIndexedName = "sysContact.0";
                szObjectName = "sysContact";
                m_recd |= SYSCONTACT;
                break;
            case 3:
                szObjectIndexedName = "sysLocation.0";
                szObjectName = "sysLocation";
                m_recd |= SYSLOCATION;
                break;
            case 4:
                szObjectIndexedName = "ifDescr.1";
                szObjectName = "ifDescr";
                m_recd |= IFDESCR;
                break;
            case 5:
                szObjectIndexedName = "ifAdminStatus.1";
                szObjectName = "ifAdminStatus";
                m_recd |= IFADMINSTATUS;
                break;
            case 6:
                szObjectIndexedName = "hrSystemUptime.0";
                szObjectName = "hrSystemUptime";
                m_recd |= HRSYSTEMUPTIME;
                break;
            case 7:
                szObjectIndexedName = "hrSystemDate.0";
                szObjectName = "hrSystemDate";
                m_recd |= HRSYSTEMDATE;
                break;
            case 8:
                szObjectIndexedName = "hrSystemInitialLoadDevice.0";
                szObjectName = "hrSystemInitialLoadDevice";
                m_recd |= HRSYSTEMINITIALLOADDEVICE;
                break;
            case 9:
                szObjectIndexedName = "hrSystemInitialLoadParameters.0";
                szObjectName = "hrSystemInitialLoadParameters";
                m_recd |= HRSYSTEMINITIALLOADPARAMETERS;
                break;
            case 10:
                szObjectIndexedName = "hrSystemNumUsers.0";
                szObjectName = "hrSystemNumUsers";
                m_recd |= HRSYSTEMNUMUSERS;
                break;
            case 11:
                szObjectIndexedName = "hrSystemProcesses.0";
                szObjectName = "hrSystemProcesses";
                m_recd |= HRSYSTEMPROCESSES;
                break;
            case 12:
                szObjectIndexedName = "hrSystemMaxProcesses.0";
                szObjectName = "hrSystemMaxProcesses";
                m_recd |= HRSYSTEMMAXPROCESSES;
                break;
            case 13:
                szObjectIndexedName = "hrMemorySize.0";
                szObjectName = "hrMemorySize";
                m_recd |= HRMEMORYSIZE;
                break;
        }
        
        boolean bFound = false;
        for (int i = 0; i < m_cSnmpMIBDictionaryList.length; i++)
        {
            if (m_cSnmpMIBDictionaryList[i].resolveName(szObjectName) != null)
            {
                System.out.println(szObjectIndexedName + " found in MIB " + m_cSnmpMIBDictionaryList[i].getMIBDictionaryName()); 
                String szInfo = m_cSnmpMIBDictionaryList[i].resolveNameType(szObjectName);
                if (szInfo != null)
                {
                    if (szInfo.compareTo("Enum") == 0)
                    {
                        int intValue = ((BigInteger)vb.getValue()).intValue();
                        String szEnumValue = m_cSnmpMIBDictionaryList[i].resolveEnum(szObjectName, intValue);
                        if (szEnumValue != null)
                            System.out.println(szObjectIndexedName + " (" + vb.getName() + ") value = " + szEnumValue + " (" + intValue + ")"); 
                        else
                            System.out.println(szObjectIndexedName + " (" + vb.getName() + ") value = " + vb.getStringValue() + " (couldn't resolve the string value of enum)"); 
                    }
                    else
                    {
                        System.out.println(szObjectIndexedName + " (" + vb.getName() + ") value = " + vb.getStringValue()); 
                    }
                        
                    System.out.println(szObjectIndexedName + " (" + vb.getName() + ") type = " + szInfo);
                    szInfo = m_cSnmpMIBDictionaryList[i].resolveNameAbstractType(szObjectName);
                    System.out.println(szObjectIndexedName + " (" + vb.getName() + ") abstract type = " + szInfo);
                }
                else
                {
                    System.out.println(szObjectIndexedName + " (" + vb.getName() + ") value = " + vb.getStringValue()); 
                }
                
                szInfo = m_cSnmpMIBDictionaryList[i].resolveNameAccess(szObjectName);
                if (szInfo != null)
                    System.out.println(szObjectIndexedName + " (" + vb.getName() + ") access = " + szInfo);
                
                szInfo = m_cSnmpMIBDictionaryList[i].resolveNameStatus(szObjectName);
                if (szInfo != null)
                    System.out.println(szObjectIndexedName + " (" + vb.getName() + ") status = " + szInfo);
                    
                szInfo = m_cSnmpMIBDictionaryList[i].resolveNameDescription(szObjectName);
                if (szInfo != null)
                    System.out.println(szObjectIndexedName + " (" + vb.getName() + ") description = " + szInfo);
                bFound = true;
                break;
            }
        }
        
        if (!bFound)
        {
            System.out.println(szObjectIndexedName + " not found in any loaded MIB"); 
            System.out.println(vb.getName() + " value = " + vb.getStringValue()); 
        }
                
        System.out.println(); 
    }
    
    /**
     * callback for delivering successes.  In this case the implementation
     * is to print out the returned values and exit after all orders are
     * delivered.
     */
    public void deliverSuccessfulOrder(int iOrderNum,
                                       SnmpVarBind vb)
    {
        handleSuccessfulOrder(iOrderNum, vb);
        if ((m_recd ^ ALL) == 0)
        {
            this.done();
        }
    }

    /**
     * handle successful deliveries
     * delivered.
     */
    synchronized private void handleFailedOrder(int iOrderNum, int iErrorStatus)
    {
        m_recdFailedOrder = true;

        String szObjectName = null;
        String szObjectIndexedName = null;
        // the orders come back in the order they are given
        switch (iOrderNum)
        {
              case 1:
                szObjectIndexedName = "sysName.0";
                szObjectName = "sysName";
                m_recd |= SYSNAME;
                break;
            case 2:
                szObjectIndexedName = "sysContact.0";
                szObjectName = "sysContact";
                m_recd |= SYSCONTACT;
                break;
            case 3:
                szObjectIndexedName = "sysLocation.0";
                szObjectName = "sysLocation";
                m_recd |= SYSLOCATION;
                break;
            case 4:
                szObjectIndexedName = "ifDescr.1";
                szObjectName = "ifDescr";
                m_recd |= IFDESCR;
                break;
            case 5:
                szObjectIndexedName = "ifAdminStatus.1";
                szObjectName = "ifAdminStatus";
                m_recd |= IFADMINSTATUS;
                break;
            case 6:
                szObjectIndexedName = "hrSystemUptime.0";
                szObjectName = "hrSystemUptime";
                m_recd |= HRSYSTEMUPTIME;
                break;
            case 7:
                szObjectIndexedName = "hrSystemDate.0";
                szObjectName = "hrSystemDate";
                m_recd |= HRSYSTEMDATE;
                break;
            case 8:
                szObjectIndexedName = "hrSystemInitialLoadDevice.0";
                szObjectName = "hrSystemInitialLoadDevice";
                m_recd |= HRSYSTEMINITIALLOADDEVICE;
                break;
            case 9:
                szObjectIndexedName = "hrSystemInitialLoadParameters.0";
                szObjectName = "hrSystemInitialLoadParameters";
                m_recd |= HRSYSTEMINITIALLOADPARAMETERS;
                break;
            case 10:
                szObjectIndexedName = "hrSystemNumUsers.0";
                szObjectName = "hrSystemNumUsers";
                m_recd |= HRSYSTEMNUMUSERS;
                break;
            case 11:
                szObjectIndexedName = "hrSystemProcesses.0";
                szObjectName = "hrSystemProcesses";
                m_recd |= HRSYSTEMPROCESSES;
                break;
            case 12:
                szObjectIndexedName = "hrSystemMaxProcesses.0";
                szObjectName = "hrSystemMaxProcesses";
                m_recd |= HRSYSTEMMAXPROCESSES;
                break;
            case 13:
                szObjectIndexedName = "hrMemorySize.0";
                szObjectName = "hrMemorySize";
                m_recd |= HRMEMORYSIZE;
                break;
        }
        
        boolean bFound = false;
        for (int i = 0; i < m_cSnmpMIBDictionaryList.length; i++)
        {
            if (m_cSnmpMIBDictionaryList[i].resolveName(szObjectName) != null)
            {
                System.out.println(szObjectIndexedName + " found in MIB " + m_cSnmpMIBDictionaryList[i].getMIBDictionaryName()); 
                String szInfo = m_cSnmpMIBDictionaryList[i].resolveNameType(szObjectName);
                if (szInfo != null)
                {
                    System.out.println(szObjectIndexedName + " failed with " + SnmpConstants.errorIDToString(iErrorStatus));
                    System.out.println(szObjectIndexedName + " type = " + szInfo);
                    szInfo = m_cSnmpMIBDictionaryList[i].resolveNameAbstractType(szObjectName);
                    System.out.println(szObjectIndexedName + " abstract type = " + szInfo);
                }
                else
                {
                    System.out.println(szObjectIndexedName + " failed with " + SnmpConstants.errorIDToString(iErrorStatus));
                }
                
                szInfo = m_cSnmpMIBDictionaryList[i].resolveNameAccess(szObjectName);
                if (szInfo != null)
                    System.out.println(szObjectIndexedName + " access = " + szInfo);
                
                szInfo = m_cSnmpMIBDictionaryList[i].resolveNameStatus(szObjectName);
                if (szInfo != null)
                    System.out.println(szObjectIndexedName + "  status = " + szInfo);
                    
                szInfo = m_cSnmpMIBDictionaryList[i].resolveNameDescription(szObjectName);
                if (szInfo != null)
                    System.out.println(szObjectIndexedName + "  description = " + szInfo);
                bFound = true;
                break;
            }
        }
        
        if (!bFound)
        {
            System.out.println(szObjectIndexedName + " not found in any loaded MIB"); 
            System.out.println(szObjectIndexedName + " failed with " + SnmpConstants.errorIDToString(iErrorStatus));
        }
                
        System.out.println(); 
    }
    
    
    /**
     * callback for failures.  In this case if a failure is received,
     * print the error(s) and exit after all orders are delivered.
     */
    public void deliverFailedOrder(int iOrderNum, int iErrorStatus)
    {
        handleFailedOrder(iOrderNum, iErrorStatus);
        if ((m_recd ^ ALL) == 0)
        {
            this.done();
        }
    }


    protected void begin()
    {
        this.get();
    }

    protected void done()
    {
        if ( !m_recdFailedOrder )
            System.out.println("Done - all orders delivered successfully.");
        else
            System.out.println("Done - some orders failed.");
        m_service.stop();
    }


    // static methods for app entry point

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

            //Creates new SnmpCustomer
            SnmpV1GetSysInfo test = new SnmpV1GetSysInfo(szHost, szReadCommunity);
            test.begin();
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
        System.out.println("Usage : java SnmpV1GetSysInfo <hostname readcommunity>");
        System.out.println("\thostname : host name or IP address of agent to query");
        System.out.println("\treadcommunity : read community of agent to query");
    }


}

