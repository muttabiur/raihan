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
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.io.UnsupportedEncodingException;

import com.outbackinc.services.protocol.snmp.*;
import com.outbackinc.services.protocol.snmp.USM.*;
import com.outbackinc.services.protocol.snmp.rmi.*;

/**
 * Sample which illustrates how to use the SnmpService to place
 * get-next orders.  In this case, the walker walks the remote
 * agent's MIB until the end is reached.
 */
public class RmiV3WalkerTest
    implements SnmpCustomer
{
    // ------------------------------ Constants ---------------------------------

    // ------------------------------ Class variables ---------------------------

    // ------------------------------ Member (instance) variables ---------------

    private SnmpService                             m_cSnmpService;
    private String[]                                m_szLastOID;
    private SnmpAuthoritativeSession                m_cSnmpRemoteAuthoritativeSession;
    private SnmpOrderInfo                           m_cSnmpOrderInfo;
    private int                                     m_iOrderNumStart;
    
    // ------------------------------ Methods -----------------------------------

    /**
     * Constructor
     */
    public RmiV3WalkerTest(String szRmiHost, 
                           String szHost, 
                           String szUserName, 
                           String szAuthPassword, 
                           String szPrivPassword,
                           int iAuthScheme, 
                           int iPrivScheme, 
                           int iSecurityLevel,
                           String szContextEngineID,
                           String szContextName)
    {
        //Sets the initial OID to something which should be before the table
        m_szLastOID = new String[1];
        m_szLastOID[0] = "1.3.6";


        try
        {
            RMISnmpClient m_cRMISnmpClient = new RMISnmpClient(szRmiHost);
            m_cSnmpService = m_cRMISnmpClient.getService();
            m_cSnmpOrderInfo = new SnmpOrderInfo(2, 3, 0);
            USMSecurityInfo secInfo;
            try
            {
                secInfo = new USMSecurityInfo(szUserName.getBytes("ASCII"), 
                                              szAuthPassword.getBytes("ASCII"), 
                                              szPrivPassword.getBytes("ASCII"),
                                              iAuthScheme, 
                                              iPrivScheme, 
                                              (byte)iSecurityLevel);
            }
            catch(UnsupportedEncodingException uee)
            {
                secInfo = new USMSecurityInfo(szUserName.getBytes(), 
                                              szAuthPassword.getBytes(), 
                                              szPrivPassword.getBytes(),
                                              iAuthScheme, 
                                              iPrivScheme, 
                                              (byte)iSecurityLevel);
            }
        
            SnmpAuthoritativeSessionFactory cSnmpAuthoritativeSessionFactory = m_cRMISnmpClient.getAuthoritativeSessionFactory();
            try
            {
                m_cSnmpRemoteAuthoritativeSession = cSnmpAuthoritativeSessionFactory.createRemoteAuthoritativeSession(szHost,
                                                                                                     161,
                                                                                                     szContextEngineID.getBytes("ASCII"),
                                                                                                     szContextName.getBytes("ASCII"),
                                                                                                     secInfo);
            }
            catch(UnsupportedEncodingException uee)
            {
                m_cSnmpRemoteAuthoritativeSession = cSnmpAuthoritativeSessionFactory.createRemoteAuthoritativeSession(szHost,
                                                                                                     161,
                                                                                                     szContextEngineID.getBytes(),
                                                                                                     szContextName.getBytes(),
                                                                                                     secInfo);
            }
        }
        catch (RemoteException re)
        {
            System.out.println("RMI communication failure with " + szRmiHost + " : " + re.getMessage());
            return;
        }
        catch (NotBoundException nbe)
        {
            System.out.println("RMI communication failure with " + szRmiHost + " : " + nbe.getMessage());
            return;
        }
        catch (MalformedURLException mue)
        {
            System.out.println("RMI communication failure with " + szRmiHost + " : " + mue.getMessage());
            return;
        }
        catch (UnknownHostException uhe)
        {
            System.out.println("Unable to create session with remote agent as host (" + szHost + ") is unknown.");
            m_cSnmpService.stop();
            return;
        }
        catch (SnmpSecurityException sse)
        {
            System.out.println("Unable to create secure session with remote agent " + szHost + " (" + sse.getMessage() + ").");
            m_cSnmpService.stop();
            return;
        }

        m_iOrderNumStart = 1;
            
        //Starts the walker walking
        System.out.println("Walking MIB Tree of " + szHost + " using SNMPv3");
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
        if (szArgs.length < 3)
        {
            usage();
            return;
          }
            
        String szRmiHost = szArgs[0];
        String szHost = szArgs[1];
        
        try
        {
            String szUserName = szArgs[2];
            String szAuthPassword = ""; 
            String szPrivPassword = ""; 
            int iSecurityLevel = SnmpSecurityLevels.noAuthNoPriv;
            int iAuthScheme = USMSecurityInfo.Auth_MD5;
            int iPrivScheme = USMSecurityInfo.Priv_DES;
            if (szArgs.length >= 4)
            {
                if (szArgs[3].compareTo("-auth") != 0
                    || szArgs.length < 5)
                {
                    usage();
                    return;
                }

                if (szArgs[4].compareTo("AuthNoPriv") == 0)
                {
                    iSecurityLevel = SnmpSecurityLevels.AuthNoPriv;
                }
                else if (szArgs[4].compareTo("AuthPriv") == 0)
                {
                    iSecurityLevel = SnmpSecurityLevels.AuthPriv;
                }
                else
                {
                    usage();
                    return;
                }
                        
                if (szArgs.length < 9
                    || szArgs[5].compareTo("-authscheme") != 0)
                {
                    usage();
                    return;
                }
                    
                if (szArgs[6].compareTo("MD5") == 0)
                {
                    iAuthScheme = USMSecurityInfo.Auth_MD5;
                }
                else if (szArgs[6].compareTo("SHA") == 0)
                {
                    iAuthScheme = USMSecurityInfo.Auth_SHA;
                }
                else
                {
                    usage();
                    return;
                }
                    
                if (szArgs[7].compareTo("-authpwd") != 0)
                {
                    usage();
                    return;
                }
                    
                szAuthPassword = szArgs[8];

                if (iSecurityLevel == SnmpSecurityLevels.AuthPriv)
                {
                    if (szArgs.length < 11
                        || szArgs[9].compareTo("-privpwd") != 0)
                    {
                         usage();
                      return;
                    }

                    szPrivPassword = szArgs[10];
               }
            }
                
            String szContextEngineID = "";
            String szContextName = "";

            //Creates new walker
            RmiV3WalkerTest test = new RmiV3WalkerTest(szRmiHost,
                                                       szHost, 
                                                       szUserName, 
                                                       szAuthPassword, 
                                                       szPrivPassword,
                                                       iAuthScheme, 
                                                       iPrivScheme, 
                                                       iSecurityLevel,
                                                       szContextEngineID,
                                                       szContextName);

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
        System.out.println("Usage : java RmiV3WalkerTest rmihostname hostname username [ -auth AuthNoPriv|AuthPriv -authscheme MD5|SHA -authpwd userspassword -privpwd userspassword ]");
        System.out.println("\trmihostname : host on which jSNMP RMIServer resides");
        System.out.println("\thostname : Host on which agent resides to walk");
        System.out.println("\tusername : user with permission to query agent");
        System.out.println("\t-auth AuthNoPriv : use authentication without privacy to query agent");
        System.out.println("\t-auth AuthPriv : use authentication with privacy to query agent (requires -privpwd)");
        System.out.println("\t-authscheme MD5 : use MD5 authentication scheme to query agent");
        System.out.println("\t-authscheme SHA : use SHA authentication scheme to query agent");
        System.out.println("\t-authpwd userspassword : use \"userspassword\" as authentication password to query agent");
        System.out.println("\t-privpwd userspassword : use \"userspassword\" as privacy password to query agent (required for -auth AuthPriv)");
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
        if (m_szLastOID[0].compareTo(vb.getName()) != 0)
        {
            System.out.println(iOrderNum + ": " + vb.getName() + " (" + vb.getStringValue() + ")");
            m_szLastOID[0] = vb.getName();
            getNext();
        }
        else
        {
            System.out.println("Finished");
            m_cSnmpService.stop();
            m_cSnmpService = null;
            m_cSnmpRemoteAuthoritativeSession = null;
            m_cSnmpOrderInfo = null;
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
        System.out.println("Ending MIB Walk");
        m_cSnmpService.stop();
        m_cSnmpService = null;
        m_cSnmpRemoteAuthoritativeSession = null;
        m_cSnmpOrderInfo = null;
        System.exit(0);
    }

    /**
     * gets the next object from the remote agent.  Demonstrates
     * placing orders with the snmp service
     */
    public void getNext()
    {
        m_iOrderNumStart = m_cSnmpService.placeGetNextOrder(m_cSnmpRemoteAuthoritativeSession,
                                                            m_cSnmpOrderInfo,
                                                            false,
                                                            m_szLastOID,
                                                            this,
                                                            m_iOrderNumStart);
    
    }
}

