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
import java.security.*;
import java.math.*;
import java.io.UnsupportedEncodingException;

import com.outbackinc.services.protocol.snmp.*;
import com.outbackinc.services.protocol.snmp.USM.*;

/**
 * Sample which illustrates how to use the SnmpService and USMUtilities to
 * change authentication and privacy passwords. It uses the following algorithm
 * from the SNMP-USER-BASED-SM-MIB (RFC 2574):
 *  The recommended way to do a key change is as follows:
 *  1) GET(usmUserSpinLock.0) and save in sValue.
 *  2) generate the keyChange value based on the old
 *     (existing) secret key and the new secret key,
 *     let us call this kcValue.
 *  3) SET(usmUserSpinLock.0=sValue,
 *         usmUserOwnAuthKeyChange=kcValue
 *         usmUserPublic=randomValue)
 *  If you get a response with error-status of noError,
 *  then the SET succeeded and the new key is active.
 *  If you do not get a response, then you can issue a
 *  GET(usmUserPublic) and check if the value is equal
 *  to the randomValue you did send in the SET. If so, then
 *  the key change succeeded and the new key is active
 *  (probably the response got lost). If not, then the SET
 *  request probably never reached the target and so you
 *  can start over with the procedure above.        
 * 
 * NOTE WELL
 * ---------
 * THIS IS A VERY DANGEROUS APPLICATION. IT HAS BEEN TESTED AGAINST SEVERAL
 * SNMPv3 AGENTS WITH SUCCESS, BUT IT IS NOT GUARANTEED TO WORK CORRECTLY WITH
 * ALL SNMPv3 AGENTS. USE AT YOUR OWN RISK. YOU HAVE BEEN WARNED!
 */
public class SnmpV3KeyChangeTest
    implements SnmpCustomer
{
    // ------------------------------ Constants ---------------------------------
    private static String m_szUsmUserOwnAuthKeyChangeOID = "1.3.6.1.6.3.15.1.2.2.1.7";
    private static String m_szUsmUserOwnPrivKeyChangeOID = "1.3.6.1.6.3.15.1.2.2.1.10";
    private static String m_szUsmUserPublicOID = "1.3.6.1.6.3.15.1.2.2.1.11";
    private static String m_szUsmUserSpinLockOID = new String("1.3.6.1.6.3.15.1.2.1.0");
    private static int m_iGetUserEngineIDOIDsOrderNum = 1;
    private static int m_iGetUserSpinLockOrderNum = 2;
    private static int m_iSetKeyStartOrderNum = 3;
    private static int m_iCheckKeyStartOrderNum = 100;

    // ------------------------------ Variables ---------------------------
    private SnmpService m_cSnmpService = null;
    private SnmpOrderInfo m_cSnmpOrderInfo = null;
    private SnmpAuthoritativeSession m_cSnmpAuthoritativeSession = null;
    private byte[] m_cbAuthKeyChangeValue = null;
    private byte[] m_cbPrivKeyChangeValue = null;
    private StringBuffer m_szbUsmUserOwnAuthKeyChangeOID = null;
    private StringBuffer m_szbUsmUserOwnPrivKeyChangeOID = null;
    private StringBuffer m_szbUsmUserPublicOID = null;
    private String[] m_szGetUserEngineIDOIDs = null;
    private String m_szUsmUserEngineIDOID = null;
    private StringBuffer m_szUserNameOID = null;
    private String m_szUserName = null;
    private String m_szAuthPassword = null;
    private String m_szPrivPassword = null;
    private int m_iAuthScheme;
    private int m_iPrivScheme; 
    private int m_iSecurityLevel;
    private String m_szHost;
    private String m_szContextEngineID;
    private String m_szContextName;
    private String m_szNewAuthPassword = "";
    private String m_szNewPrivPassword = "";
    private String m_szUsmUserPublicValue = null;
    private int m_iUserSpinLockValue;
    
    // ------------------------------ Methods -----------------------------------

    /**
     * Constructor.
     */
    public SnmpV3KeyChangeTest(String szHost, 
                               String szUserName, 
                               String szAuthPassword, 
                               String szPrivPassword,
                               int iAuthScheme, 
                               int iPrivScheme, 
                               int iSecurityLevel,
                               String szNewAuthPassword, 
                               String szNewPrivPassword,
                               String szContextEngineID,
                               String szContextName)
    {
        // initialize the Snmp Service
        try
        {
            m_cSnmpService = SnmpLocalInterfaces.getService();
        }
        catch (SocketException se)
        {
            System.out.println("Unable to initialize the Snmp service (" + se.getMessage() + ").");
            return;
        }
        
        SnmpServiceConfiguration cSnmpServiceConfiguration = SnmpLocalInterfaces.getServiceConfiguration(m_cSnmpService);
        cSnmpServiceConfiguration.setBufferDelay(0);
        m_cSnmpOrderInfo = new SnmpOrderInfo(2, 3, 0);
        USMSecurityInfo cUSMSecurityInfo;
        try
        {
            try
            {
                cUSMSecurityInfo = new USMSecurityInfo(szUserName.getBytes("ASCII"), 
                                                       szAuthPassword.getBytes("ASCII"), 
                                                       szPrivPassword.getBytes("ASCII"),
                                                       iAuthScheme, 
                                                       iPrivScheme, 
                                                       (byte)iSecurityLevel);
            }
            catch(UnsupportedEncodingException uee)
            {
                cUSMSecurityInfo = new USMSecurityInfo(szUserName.getBytes(), 
                                                       szAuthPassword.getBytes(), 
                                                       szPrivPassword.getBytes(),
                                                       iAuthScheme, 
                                                       iPrivScheme, 
                                                       (byte)iSecurityLevel);
            }
        }
        catch (SnmpSecurityException sse)
        {
            System.out.println("Unable to create secure session with remote agent " + szHost + " (" + sse.getMessage() + ").");
            m_cSnmpService.stop();
            return;
        }
        

        // create our session
        SnmpAuthoritativeSessionFactory cSnmpAuthoritativeSessionFactory = SnmpLocalInterfaces.getAuthoritativeSessionFactory();
        try
        {
            try
            {
                m_cSnmpAuthoritativeSession = cSnmpAuthoritativeSessionFactory.createRemoteAuthoritativeSession(szHost,
                                                                                                          161,
                                                                                                          szContextEngineID.getBytes("ASCII"),
                                                                                                          szContextName.getBytes("ASCII"),
                                                                                                          cUSMSecurityInfo);
            }
            catch(UnsupportedEncodingException uee)
            {
                m_cSnmpAuthoritativeSession = cSnmpAuthoritativeSessionFactory.createRemoteAuthoritativeSession(szHost,
                                                                                                          161,
                                                                                                          szContextEngineID.getBytes(),
                                                                                                          szContextName.getBytes(),
                                                                                                          cUSMSecurityInfo);
            }
        }
        catch (UnknownHostException uhe)
        {
            System.out.println("Unable to create secure session with remote agent as host (" + szHost + ") is unknown.");
            m_cSnmpService.stop();
            return;
        }
        catch (SnmpSecurityException sse)
        {
            System.out.println("Unable to create secure session with remote agent (" + szHost + ") due to discovery failure (" + sse.getMessage() + ").");
            m_cSnmpService.stop();
            return;
        }

        // save for later
        m_szUserName = new String(szUserName);;
        m_szAuthPassword = new String(szAuthPassword);
        m_szPrivPassword = new String(szPrivPassword);
        m_iAuthScheme = iAuthScheme;
        m_iPrivScheme = iPrivScheme; 
        m_iSecurityLevel = iSecurityLevel;
        m_szHost = new String(szHost);
        m_szContextEngineID = new String(szContextEngineID);
        m_szContextName = new String(szContextName);
               
        // set user name OID for index
        m_szUserNameOID = new StringBuffer("");
        byte[] cbUserName = szUserName.getBytes();
        for (int jCtr = 0; jCtr < cbUserName.length; jCtr++)
        {
            if (jCtr != 0)
            {
                m_szUserNameOID.append(".");
            }
            
            int iVal = (int)cbUserName[jCtr];
            if (iVal < 0)
            {
                iVal += 256;
            }

            m_szUserNameOID.append(iVal);
        }

        // generate key change
        if (szNewAuthPassword.compareTo("") != 0)
        {
            m_szNewAuthPassword = new String(szNewAuthPassword);
            try
            {
                m_cbAuthKeyChangeValue = USMUtilities.generateAuthKeyChangeValue(m_cSnmpAuthoritativeSession, 
                                                                                 szNewAuthPassword.getBytes("ASCII"));
            }
            catch(UnsupportedEncodingException uee)
            {
                m_cbAuthKeyChangeValue = USMUtilities.generateAuthKeyChangeValue(m_cSnmpAuthoritativeSession, 
                                                                                 szNewAuthPassword.getBytes());
            }
        }
        else
        {
            m_szNewPrivPassword = new String(szNewPrivPassword);
            try
            {
                m_cbPrivKeyChangeValue = USMUtilities.generatePrivKeyChangeValue(m_cSnmpAuthoritativeSession, 
                                                                                 szNewPrivPassword.getBytes("ASCII"));
            }
            catch(UnsupportedEncodingException uee)
            {
                m_cbPrivKeyChangeValue = USMUtilities.generatePrivKeyChangeValue(m_cSnmpAuthoritativeSession, 
                                                                                 szNewPrivPassword.getBytes());
            }
        }
              
        if (szNewAuthPassword.compareTo("") != 0
            && szNewPrivPassword.compareTo("") != 0)
        {
            System.out.println("Changing authentication and privacy passwords using SNMPv3");
        }
        else if (szNewAuthPassword.compareTo("") != 0)
        {
            System.out.println("Changing authentication password using SNMPv3");
        }
        else
        {
            System.out.println("Changing privacy password using SNMPv3");
        }
            
        System.out.println("\tHost: " + szHost);
        System.out.println("\tUser: " + szUserName);
        
        if (iSecurityLevel == SnmpSecurityLevels.noAuthNoPriv)
        {
            System.out.println("\tSecurity Level: noAuthNoPriv");
        }
        else if (iSecurityLevel == SnmpSecurityLevels.AuthNoPriv)
        {
            System.out.println("\tSecurity Level: AuthNoPriv");
            if (iAuthScheme == USMSecurityInfo.Auth_MD5)
            {
                System.out.println("\tAuthentication Scheme: MD5");
            }
            else if (iAuthScheme == USMSecurityInfo.Auth_SHA)
            {
                System.out.println("\tAuthentication Scheme: SHA");
            }
            else
            {
                System.out.println("\tAuthentication Scheme: unknown");
            }
            System.out.println("\tAuthentication Password: " + szAuthPassword);
        }
        else if (iSecurityLevel == SnmpSecurityLevels.AuthPriv)
        {
            System.out.println("\tSecurity Level: AuthPriv");
            if (iAuthScheme == USMSecurityInfo.Auth_MD5)
            {
                System.out.println("\tAuthentication Scheme: MD5");
            }
            else if (iAuthScheme == USMSecurityInfo.Auth_SHA)
            {
                System.out.println("\tAuthentication Scheme: SHA");
            }
            else
            {
                System.out.println("\tAuthentication Scheme: unknown");
            }
            if (iPrivScheme == USMSecurityInfo.Priv_DES)
            {
                System.out.println("\tPrivacy Scheme: DES");
            }
            else
            {
                System.out.println("\tPrivacy Scheme: unknown");
            }
            System.out.println("\tAuthentication Password: " + szAuthPassword);
            System.out.println("\tPrivacy Password: " + szPrivPassword);
        }
        else
        {
            System.out.println("\tSecurity Level: unknown");
        }

        System.out.println("\tNew Authentication Password: " + szNewAuthPassword);
        System.out.println("\tNew Privacy Password: " + szNewPrivPassword);
        System.out.println("\tContext Engine ID: " + szContextEngineID);
        System.out.println("\tContext Name: " + szContextName);
        getUserEngineID();
    }
    
    /**
     * Get usmUserSpinLock.0
     */
    public void getUserSpinLock()
    {
        String[] szOIDs = new String[1];
        szOIDs[0] = m_szUsmUserSpinLockOID;
        m_cSnmpService.placeGetOrder(m_cSnmpAuthoritativeSession,
                                     m_cSnmpOrderInfo,
                                     true,
                                     szOIDs,
                                     this,
                                     m_iGetUserSpinLockOrderNum);
    }
      
    /**
     * Get usmUserEngineID
     */
    public void getUserEngineID()
    {
        if (m_szNewAuthPassword.compareTo("") != 0)
        {
            if (m_szGetUserEngineIDOIDs == null)
            {
                m_szGetUserEngineIDOIDs = new String[1];
                m_szGetUserEngineIDOIDs[0] = new String(m_szUsmUserOwnAuthKeyChangeOID);
            }
        }
        else
        {
            if (m_szGetUserEngineIDOIDs == null)
            {
                m_szGetUserEngineIDOIDs = new String[1];
                m_szGetUserEngineIDOIDs[0] = new String(m_szUsmUserOwnPrivKeyChangeOID);
            }
        }
        
        m_cSnmpService.placeGetNextOrder(m_cSnmpAuthoritativeSession,
                                         m_cSnmpOrderInfo,
                                         true,
                                         m_szGetUserEngineIDOIDs,
                                         this,
                                         m_iGetUserEngineIDOIDsOrderNum);
    }
      
    /**
     * Check key
     */
    public void checkKey()
    {
        try
        {
            USMSecurityInfo cUSMSecurityInfo = null; 
            if (m_szNewAuthPassword.compareTo("") != 0)
            {
                try
                {
                    cUSMSecurityInfo = new USMSecurityInfo(m_szUserName.getBytes("ASCII"), 
                                                           m_szNewAuthPassword.getBytes("ASCII"), 
                                                           m_szPrivPassword.getBytes("ASCII"),
                                                           m_iAuthScheme, 
                                                           m_iPrivScheme, 
                                                           (byte)m_iSecurityLevel);
                }
                catch(UnsupportedEncodingException uee)
                {
                    cUSMSecurityInfo = new USMSecurityInfo(m_szUserName.getBytes(), 
                                                           m_szNewAuthPassword.getBytes(), 
                                                           m_szPrivPassword.getBytes(),
                                                           m_iAuthScheme, 
                                                           m_iPrivScheme, 
                                                           (byte)m_iSecurityLevel);
                }
            }
            else
            {
                try
                {
                    cUSMSecurityInfo = new USMSecurityInfo(m_szUserName.getBytes("ASCII"), 
                                                           m_szAuthPassword.getBytes("ASCII"), 
                                                           m_szNewPrivPassword.getBytes("ASCII"),
                                                           m_iAuthScheme, 
                                                           m_iPrivScheme, 
                                                           (byte)m_iSecurityLevel);
                }
                catch(UnsupportedEncodingException uee)
                {
                    cUSMSecurityInfo = new USMSecurityInfo(m_szUserName.getBytes(), 
                                                           m_szAuthPassword.getBytes(), 
                                                           m_szNewPrivPassword.getBytes(),
                                                           m_iAuthScheme, 
                                                           m_iPrivScheme, 
                                                           (byte)m_iSecurityLevel);
                }
            }

            // create our session
            SnmpAuthoritativeSessionFactory cSnmpAuthoritativeSessionFactory = SnmpLocalInterfaces.getAuthoritativeSessionFactory();
            try
            {
                m_cSnmpAuthoritativeSession = cSnmpAuthoritativeSessionFactory.createRemoteAuthoritativeSession(m_szHost,
                                                                                                            161,
                                                                                                            m_szContextEngineID.getBytes("ASCII"),
                                                                                                            m_szContextName.getBytes("ASCII"),
                                                                                                            cUSMSecurityInfo);
            }
            catch(UnsupportedEncodingException uee)
            {
                m_cSnmpAuthoritativeSession = cSnmpAuthoritativeSessionFactory.createRemoteAuthoritativeSession(m_szHost,
                                                                                                            161,
                                                                                                            m_szContextEngineID.getBytes(),
                                                                                                            m_szContextName.getBytes(),
                                                                                                            cUSMSecurityInfo);
            }
            
            String[] szOIDs = new String[1];
            szOIDs[0] = m_szbUsmUserPublicOID.toString();
            m_cSnmpService.placeGetOrder(m_cSnmpAuthoritativeSession,
                                         m_cSnmpOrderInfo,
                                         true,
                                         szOIDs,
                                         this,
                                         m_iCheckKeyStartOrderNum);
        }
        catch (Exception e)
        {
            deliverFailedOrder(m_iCheckKeyStartOrderNum, SnmpConstants.SNMP_ERR_PROVIDING_AUTHPRIV);
        }
    }
    
    /**
     * Set key
     */
    public void setKey()
    {
        String[] szOIDs = new String[3];
        Object[] objValues = new Object[3];
        byte[] bTypes = new byte[3];

        szOIDs[0] = m_szUsmUserSpinLockOID;
        objValues[0] = new BigInteger(new Integer(m_iUserSpinLockValue).toString());
        bTypes[0] = SnmpConstants.ASN_INTEGER;
        
        if (m_szNewAuthPassword.compareTo("") != 0)
        {
            m_szbUsmUserOwnAuthKeyChangeOID = new StringBuffer(m_szUsmUserOwnAuthKeyChangeOID);
            m_szbUsmUserOwnAuthKeyChangeOID.append(m_szUsmUserEngineIDOID);
            m_szbUsmUserOwnAuthKeyChangeOID.append(m_szUserNameOID);
            szOIDs[1] = m_szbUsmUserOwnAuthKeyChangeOID.toString();
            objValues[1] = m_cbAuthKeyChangeValue;
            bTypes[1] = SnmpConstants.ASN_OCTSTR;
        }
        else
        {
            m_szbUsmUserOwnPrivKeyChangeOID = new StringBuffer(m_szUsmUserOwnPrivKeyChangeOID);
            m_szbUsmUserOwnPrivKeyChangeOID.append(m_szUsmUserEngineIDOID);
            m_szbUsmUserOwnPrivKeyChangeOID.append(m_szUserNameOID);
            szOIDs[1] = m_szbUsmUserOwnPrivKeyChangeOID.toString();
            objValues[1] = m_cbPrivKeyChangeValue;
            bTypes[1] = SnmpConstants.ASN_OCTSTR;
        }
        
        m_szbUsmUserPublicOID = new StringBuffer(m_szUsmUserPublicOID);
        m_szbUsmUserPublicOID.append(m_szUsmUserEngineIDOID);
        m_szbUsmUserPublicOID.append(m_szUserNameOID);
        szOIDs[2] = m_szbUsmUserPublicOID.toString();
        m_szUsmUserPublicValue = Double.toString(Math.random());
        objValues[2] = m_szUsmUserPublicValue.getBytes();
        bTypes[2] = SnmpConstants.ASN_OCTSTR;
       
        m_cSnmpService.placeSetOrder(m_cSnmpAuthoritativeSession,
                                     m_cSnmpOrderInfo,
                                     true,
                                     szOIDs,
                                     bTypes,
                                     objValues,
                                     this,
                                     m_iSetKeyStartOrderNum);
    }

    /**
     * Main method to start execution
     *
     * @param szArgs contains cmdline args, first of which must be the szHost, second must be read community
     *
     */
    public static void main(String[] szArgs)
    {
        if (szArgs.length < 4)
        {
            usage();
            return;
          }
            
        String szHost = szArgs[0];
        
        try
        {
            try
            {
                // setup security provider
                Security.addProvider(new com.sun.crypto.provider.SunJCE());
            }
            catch (Error err)
            {
                // do nothing ... encyption will fail if provider not installed
            }
            catch (Exception e)
            {
                // do nothing ... encyption will fail if provider not installed
            }
        
            String szUserName = szArgs[1];
            String szAuthPassword = ""; 
            String szPrivPassword = ""; 
            String szNewAuthPassword = ""; 
            String szNewPrivPassword = ""; 
            String szContextEngineID = "";
            String szContextName = "";
            int iSecurityLevel = SnmpSecurityLevels.noAuthNoPriv;
            int iAuthScheme = USMSecurityInfo.Auth_MD5;
            int iPrivScheme = USMSecurityInfo.Priv_DES;
            
            for (int i = 2; i < szArgs.length; i += 2)
            {
                if (i == szArgs.length - 1)
                {
                    usage();
                    return;
                }
                else if (szArgs[i].compareTo("-auth") == 0)
                {
                    if (szArgs[i+1].compareTo("noAuthNoPriv") == 0)
                    {
                        iSecurityLevel = SnmpSecurityLevels.noAuthNoPriv;
                    }
                    else if (szArgs[i+1].compareTo("AuthNoPriv") == 0)
                    {
                        iSecurityLevel = SnmpSecurityLevels.AuthNoPriv;
                    }
                    else if (szArgs[i+1].compareTo("AuthPriv") == 0)
                    {
                        iSecurityLevel = SnmpSecurityLevels.AuthPriv;
                    }
                    else
                    {
                        usage();
                        return;
                    }
                }
                else if (szArgs[i].compareTo("-authscheme") == 0)
                {
                    if (szArgs[i+1].compareTo("MD5") == 0)
                    {
                        iAuthScheme = USMSecurityInfo.Auth_MD5;
                    }
                    else if (szArgs[i+1].compareTo("SHA") == 0)
                    {
                        iAuthScheme = USMSecurityInfo.Auth_SHA;
                    }
                    else
                    {
                        usage();
                        return;
                    }
                }
                else if (szArgs[i].compareTo("-authpwd") == 0)
                {
                    szAuthPassword = szArgs[i+1];
                }
                else if (szArgs[i].compareTo("-privpwd") == 0)
                {
                    szPrivPassword = szArgs[i+1];
                }
                else if (szArgs[i].compareTo("-newauthpwd") == 0)
                {
                    szNewAuthPassword = szArgs[i+1];
                }
                else if (szArgs[i].compareTo("-newprivpwd") == 0)
                {
                    szNewPrivPassword = szArgs[i+1];
                }
                else if (szArgs[i].compareTo("-ctxengid") == 0)
                {
                    szContextEngineID = szArgs[i+1];
                }
                else if (szArgs[i].compareTo("-ctxname") == 0)
                {
                    szContextName = szArgs[i+1];
                }
                else
                {
                    usage();
                    return;
                }
            }

            if (iSecurityLevel == SnmpSecurityLevels.AuthNoPriv)
            {
                if (szAuthPassword.compareTo("") == 0)
                {
                    usage();
                    return;
                }
            }
            else if (iSecurityLevel == SnmpSecurityLevels.AuthPriv)
            {
                if (szAuthPassword.compareTo("") == 0
                    || szPrivPassword.compareTo("") == 0)
                {
                    usage();
                    return;
                }
            }
            
            if ((szNewAuthPassword.compareTo("") == 0
                    && szNewPrivPassword.compareTo("") == 0)
                || (szNewAuthPassword.compareTo("") != 0
                    && szNewPrivPassword.compareTo("") != 0))
            {
                usage();
                return;
            }
            
            SnmpV3KeyChangeTest test = new SnmpV3KeyChangeTest(szHost, 
                                                               szUserName, 
                                                               szAuthPassword, 
                                                               szPrivPassword,
                                                               iAuthScheme, 
                                                               iPrivScheme, 
                                                               iSecurityLevel,
                                                               szNewAuthPassword, 
                                                               szNewPrivPassword,
                                                               szContextEngineID,
                                                               szContextName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Display correct usage.
     */
    public static void usage()
    {
        System.out.println("Usage : java SnmpV3KeyChangeTest hostname username [ -auth AuthNoPriv|AuthPriv -authscheme MD5|SHA -authpwd userspassword -privpwd userspassword -ctxengid contextengineid -ctxname contextname ] -newauthpwd userspassword | -newprivpwd userspassword");
        System.out.println("\thostname : Host on which agent resides to query (required as first argument)");
        System.out.println("\tusername : user with permission to query agent (required as second argument)");
        System.out.println("\t-auth noAuthNoPriv : do not use authentication or privacy to query agent (default)");
        System.out.println("\t-auth AuthNoPriv : use authentication without privacy to query agent");
        System.out.println("\t-auth AuthPriv : use authentication with privacy to query agent (requires -privpwd)");
        System.out.println("\t-authscheme MD5 : use MD5 authentication scheme to query agent (default for -auth AuthNoPriv and -auth AuthPriv)");
        System.out.println("\t-authscheme SHA : use SHA authentication scheme to query agent");
        System.out.println("\t-authpwd userspassword : use \"userspassword\" as authentication password to query agent (required for -auth AuthPriv and -auth AuthNoPriv)");
        System.out.println("\t-privpwd userspassword : use \"userspassword\" as privacy password to query agent (required for -auth AuthPriv)");
        System.out.println("\t-newauthpwd userspassword : change authentication password to \"userspassword\"");
        System.out.println("\t-newprivpwd userspassword : change privacy password to \"userspassword\"");
        System.out.println("\t-ctxengid contextengineid : use \"contextengineid\" as context engine identifier to query agent");
        System.out.println("\t-ctxname contextname : use \"contextname\" as context name to query agent");
    }


    //SnmpCustomer Implementation

    /**
     * Callback for delivering successes.
     */
    public void deliverSuccessfulOrder(int iOrderNum,
                                       SnmpVarBind vb)
    {
        if (iOrderNum == m_iGetUserEngineIDOIDsOrderNum)
        {
            // get engineID
            String szVbName = new String(vb.getName());
            String szUsmUserOwnKeyChangeOID = null;
            if (m_szNewAuthPassword.compareTo("") != 0)
            {
                szUsmUserOwnKeyChangeOID = m_szUsmUserOwnAuthKeyChangeOID;
            }
            else
            {
                szUsmUserOwnKeyChangeOID = m_szUsmUserOwnPrivKeyChangeOID;
            }
            
            int iUserNamePos = 0;
            if (szVbName.indexOf(szUsmUserOwnKeyChangeOID) == 0)
            {
                if (!((iUserNamePos = szVbName.indexOf(m_szUserNameOID.toString())) > 0
                    && iUserNamePos == (szVbName.length() - m_szUserNameOID.toString().length())))
                {
                    if (m_szNewAuthPassword.compareTo("") != 0)
                    {
                        System.out.println(iOrderNum + " success: " + vb.getName() + " (UsmUserOwnAuthKeyChange) getnext (" + vb.getStringValue() + ")");
                    }
                    else
                    {
                        System.out.println(iOrderNum + " success: " + vb.getName() + " (UsmUserOwnPrivKeyChange) getnext (" + vb.getStringValue() + ")");
                    }
                    m_szGetUserEngineIDOIDs[0] = vb.getName();
                    getUserEngineID();
                    return;
                }

                if (m_szNewAuthPassword.compareTo("") != 0)
                {
                    System.out.println(iOrderNum + " success: " + vb.getName() + " (UsmUserOwnAuthKeyChange) getnext (" + vb.getStringValue() + ")");
                }
                else
                {
                    System.out.println(iOrderNum + " success: " + vb.getName() + " (UsmUserOwnPrivKeyChange) getnext (" + vb.getStringValue() + ")");
                }
                
                m_szUsmUserEngineIDOID = new String(szVbName.substring(szUsmUserOwnKeyChangeOID.length(), iUserNamePos));
            }
            
            if (m_szUsmUserEngineIDOID == null)
            {
                if (m_szNewAuthPassword.compareTo("") != 0)
                {
                    System.out.println(iOrderNum + " failure: " + vb.getName() + " (UsmUserOwnAuthKeyChange) getnext (" + vb.getStringValue() + ")");
                }
                else
                {
                    System.out.println(iOrderNum + " failure: " + vb.getName() + " (UsmUserOwnPrivKeyChange) getnext (" + vb.getStringValue() + ")");
                }

                System.out.println("Unable to determine usmUserEngineID.");
                System.out.println("\nDone.");
                m_cSnmpService.stop();
            }
            else
            {
                getUserSpinLock();
            }
        }
        else if (iOrderNum == m_iGetUserSpinLockOrderNum)
        {
            // get spin lock
            if (vb.getType() != SnmpConstants.ASN_INTEGER)
            {
                deliverFailedOrder(iOrderNum, SnmpConstants.SNMP_ERR_WRONGTYPE);
            }
    
            m_iUserSpinLockValue = ((BigInteger)(vb.getValue())).intValue();
            System.out.println("\n" + iOrderNum + " success: " + vb.getName() + " (usmUserSpinLock) get (" + m_iUserSpinLockValue + ")");
            setKey();
        }
        else if (iOrderNum == m_iSetKeyStartOrderNum)
        {
            // set spin lock
            System.out.println("\n" + iOrderNum + " success: " + vb.getName() + " (usmUserSpinLock) set (" + vb.getStringValue() + ")");
        }
        else if (iOrderNum == m_iSetKeyStartOrderNum + 1)
        {
            // set key
            if (m_szNewAuthPassword.compareTo("") != 0)
            {
                System.out.println("\n" + iOrderNum + " success: " + vb.getName() + " (usmUserOwnAuthKeyChange) set (" + vb.getStringValue() + ")");
            }
            else
            {
                System.out.println("\n" + iOrderNum + " success: " + vb.getName() + " (usmUserOwnPrivKeyChange) set (" + vb.getStringValue() + ")");
            }
        }
        else if (iOrderNum == m_iSetKeyStartOrderNum + 2)
        {
            // set public value
            System.out.println("\n" + iOrderNum + " success: " + vb.getName() + " (usmUserPublic) set (" + vb.getStringValue() + ")");
            if (m_szNewAuthPassword.compareTo("") != 0)
            {
                System.out.println("\nDone. The authentication password for \"" + m_szUserName + "\" has been changed to \"" + m_szNewAuthPassword + "\".");
            }
            else
            {
                System.out.println("\nDone. The privacy password for \"" + m_szUserName + "\" has been changed to \"" + m_szNewPrivPassword + "\".");
            }
            
            m_cSnmpService.stop();
        }
        else if (iOrderNum == m_iCheckKeyStartOrderNum)
        {
            // get public value
            System.out.println("\n" + iOrderNum + " success: " + vb.getName() + " (usmUserPublic) get (" + vb.getStringValue() + ")");
            if (m_szUsmUserPublicValue.compareTo(vb.getStringValue()) == 0)
            {
                if (m_szNewAuthPassword.compareTo("") != 0)
                {
                    System.out.println("\nDone. The authentication password for \"" + m_szUserName + "\" has been changed to \"" + m_szNewAuthPassword + "\".");
                }
                else
                {
                    System.out.println("\nDone. The privacy password for \"" + m_szUserName + "\" has been changed to \"" + m_szNewPrivPassword + "\".");
                }
            }
            else
            {
                if (m_szNewAuthPassword.compareTo("") != 0)
                {
                    System.out.println("\nDone. The authentication password for \"" + m_szUserName + "\" has NOT have been changed to \"" + m_szNewAuthPassword + "\".");
                }
                else
                {
                    System.out.println("\nDone. The privacy password for \"" + m_szUserName + "\" has NOT been changed to \"" + m_szNewPrivPassword + "\".");
                }
            }

            m_cSnmpService.stop();
        }
        else
        {
            System.out.println("\n" + iOrderNum + " failed: unexpected order number");
            System.out.println("\nDone.");
            m_cSnmpService.stop();
        }
    }

    /**
     * Callback for failures.
     */
    public void deliverFailedOrder(int iOrderNum, int iErrorStatus)
    {
        if (iOrderNum == m_iGetUserEngineIDOIDsOrderNum)
        {
            if (m_szNewAuthPassword.compareTo("") != 0)
            {
                System.out.println(iOrderNum + " failure: " + m_szGetUserEngineIDOIDs[0] + " (UsmUserOwnAuthKeyChange) getnext (" + SnmpConstants.errorIDToString(iErrorStatus) + ")");
            }
            else
            {
                System.out.println(iOrderNum + " failure: " + m_szGetUserEngineIDOIDs[0] + " (UsmUserOwnPrivKeyChange) getnext (" + SnmpConstants.errorIDToString(iErrorStatus) + ")");
            }
            
            System.out.println("\nDone.");
            m_cSnmpService.stop();
        }
        else if (iOrderNum == m_iGetUserSpinLockOrderNum)
        {
            System.out.println("\n" + iOrderNum + " failure: " + m_szUsmUserSpinLockOID + " (usmUserSpinLock) get (" + SnmpConstants.errorIDToString(iErrorStatus) + ")");
            System.out.println("\nDone.");
            m_cSnmpService.stop();
        }
        else if (iOrderNum == m_iSetKeyStartOrderNum)
        {
            if (iErrorStatus == SnmpConstants.SNMP_ERR_TIMEOUT)
            {
                checkKey();
            }
            else
            {
                System.out.println("\n" + iOrderNum + " failure: " + m_szUsmUserSpinLockOID + " (usmUserSpinLock) set (" + SnmpConstants.errorIDToString(iErrorStatus) + ")");
                System.out.println("\nDone.");
                m_cSnmpService.stop();
            }
        }
        else if (iOrderNum == m_iSetKeyStartOrderNum + 1)
        {
            if (iErrorStatus == SnmpConstants.SNMP_ERR_TIMEOUT)
            {
                checkKey();
            }
            else
            {
                if (m_szNewAuthPassword.compareTo("") != 0)
                {
                    System.out.println("\n" + iOrderNum + " failure: " + m_szbUsmUserOwnAuthKeyChangeOID.toString() + " (usmUserOwnAuthKeyChange) set (" + SnmpConstants.errorIDToString(iErrorStatus) + ")");
                }
                else
                {
                    System.out.println("\n" + iOrderNum + " failure: " + m_szbUsmUserOwnPrivKeyChangeOID.toString() + " (usmUserOwnPrivKeyChange) set (" + SnmpConstants.errorIDToString(iErrorStatus) + ")");
                }
                
                System.out.println("\nDone.");
                m_cSnmpService.stop();
            }
        }
        else if (iOrderNum == m_iSetKeyStartOrderNum + 2)
        {
            if (iErrorStatus == SnmpConstants.SNMP_ERR_TIMEOUT)
            {
                checkKey();
            }
            else
            {
                System.out.println("\n" + iOrderNum + " failure: " + m_szbUsmUserPublicOID.toString() + " (usmUserPublic) set (" + SnmpConstants.errorIDToString(iErrorStatus) + ")");
                System.out.println("\nDone.");
                m_cSnmpService.stop();
            }
        }
        else if (iOrderNum == m_iCheckKeyStartOrderNum)
        {
            System.out.println("\n" + iOrderNum + " failure: " + m_szbUsmUserPublicOID.toString() + " (usmUserPublic) get (" + SnmpConstants.errorIDToString(iErrorStatus) + ")");
            System.out.println("\nDone.");
            m_cSnmpService.stop();
        }
        else
        {
            System.out.println("\n" + iOrderNum + " failed: unexpected order number (" + SnmpConstants.errorIDToString(iErrorStatus) + ")");
            System.out.println("\nDone.");
            m_cSnmpService.stop();
        }
    }
}

