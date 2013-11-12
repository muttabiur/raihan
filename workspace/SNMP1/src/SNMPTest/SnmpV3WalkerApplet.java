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
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;
import java.security.*;
import java.io.UnsupportedEncodingException;

import com.outbackinc.services.protocol.snmp.*;
import com.outbackinc.services.protocol.snmp.USM.*;


/**
 * Sample which illustrates how to use the jSNMP within a browser.
 * Note: A fully JDK 1.1-compliant browser is required for this
 * example.  <p>
 *
 *
 * Here's an example HTML file with an applet tag: <p>
 * <p>
 * <pre>
 *   &lt;html>&lt;body>
 *   &lt;applet code="SnmpV2WalkerApplet" height=400 width=500>
 *   &lt;/applet>
 *   &lt;/body>&lt;/html>
 * </pre>
 * <p>
 *
 * Note: The CLASSPATH must be initialized correctly before
 * starting the browser.
 */
public class SnmpV3WalkerApplet
extends Applet
implements SnmpCustomer, SnmpTrapListener, Runnable, ActionListener, ItemListener
{
    // ------------------------------ Constants ---------------------------------

    // ------------------------------ Class variables ---------------------------

    // ------------------------------ Member (instance) variables ---------------

    private SnmpService                             m_service;
    private SnmpServiceConfiguration                m_serviceConfiguration;
    private String[]                                m_szLastOID;
    private int                                     m_iNumRetrieved;
    private SnmpAuthoritativeSession                m_cSnmpRemoteAuthoritativeSession;
    private SnmpOrderInfo                           m_orderinfo;
    private int                                     m_iOrderNumStart;
    private String                                  m_szManagedHost = null;
    private int                                     m_iSecurityLevel = SnmpSecurityLevels.noAuthNoPriv;
    private int                                     m_iAuthScheme = USMSecurityInfo.Auth_MD5;

    private TextArea                                m_textArea = null;
    private Thread                                  m_runThread = null;
    private TextArea                                m_trapOutput = null;

    private Button                                  m_start = null;
    private Button                                  m_stop = null;
    private TextField                               m_hostField = null;
    private TextField                               m_userNameField = null;
    private Checkbox                                m_noAuthNoPrivCheckBox = null;
    private Checkbox                                m_AuthNoPrivCheckBox = null;
    private Checkbox                                m_AuthPrivCheckBox = null;
    
    private Checkbox                                m_Auth_MD5CheckBox = null;
    private Checkbox                                m_Auth_SHACheckBox = null;
    
    private TextField                               m_authPasswordField = null;
    private TextField                               m_privPasswordField = null;
    private TextField                               m_contextEngineIDField = null;
    private TextField                               m_contextNameField = null;
    
    private boolean                                 m_bWalking = false;

    
    // ------------------------------ Methods -----------------------------------

    /**
     *
     */
    public SnmpV3WalkerApplet()
    {
        try
        {
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
        
        m_szLastOID = new String[1];
        m_szLastOID[0] = "1.3.6";
        m_iOrderNumStart = 1;
    }


    /**
     *
     */
    public void init()
    {
        // Create the ui...
        initUI();

        // Initialize the jSNMP core...
        try {
            initService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initUI()
    {
        setBackground(Color.lightGray);

        m_hostField = new TextField(5);
        m_userNameField = new TextField(5);
        CheckboxGroup authPrivCheckBoxGroup = new CheckboxGroup();
        m_noAuthNoPrivCheckBox = new Checkbox("noAuthNoPriv", authPrivCheckBoxGroup, true);
        m_AuthNoPrivCheckBox = new Checkbox("AuthNoPriv", authPrivCheckBoxGroup, false);
        m_AuthPrivCheckBox = new Checkbox("AuthPriv", authPrivCheckBoxGroup, false);
        
        CheckboxGroup authCheckBoxGroup = new CheckboxGroup();
        m_Auth_MD5CheckBox = new Checkbox("MD5", authCheckBoxGroup, true);
        m_Auth_SHACheckBox = new Checkbox("SHA", authCheckBoxGroup, false);
        
        m_authPasswordField = new TextField(10);
        m_privPasswordField = new TextField(10);
        m_contextEngineIDField = new TextField(10);
        m_contextNameField = new TextField(10);
        m_start = new Button("Start");
        m_stop = new Button("Stop");

        m_stop.setEnabled(false);
        m_hostField.addActionListener(this);
        m_userNameField.addActionListener(this);
        m_noAuthNoPrivCheckBox.addItemListener(this);
        m_AuthNoPrivCheckBox.addItemListener(this);
        m_AuthPrivCheckBox.addItemListener(this);
        m_Auth_MD5CheckBox.addItemListener(this);
        m_Auth_SHACheckBox.addItemListener(this);
        m_authPasswordField.addActionListener(this);
        m_privPasswordField.addActionListener(this);
        m_contextEngineIDField.addActionListener(this);
        m_contextNameField.addActionListener(this);
        m_start.addActionListener(this);
        m_stop.addActionListener(this);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        setLayout(gridbag);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        Label label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        // host and user editbox fields
        c.gridwidth = 1;
        label = new Label("SNMP Agent Host: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_hostField, c);
        add(m_hostField);

        label = new Label("User: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_userNameField, c);
        add(m_userNameField);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        // authentication/privacy and authentication/privacy checkbox fields
        c.gridwidth = 1;
        label = new Label("Authentication/Privacy: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_AuthPrivCheckBox, c);
        add(m_AuthPrivCheckBox);

        label = new Label("Authentication Scheme: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_Auth_MD5CheckBox, c);
        add(m_Auth_MD5CheckBox); 

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
        
        c.gridwidth = 1;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_AuthNoPrivCheckBox, c);
        add(m_AuthNoPrivCheckBox);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
  
        gridbag.setConstraints(m_Auth_SHACheckBox, c);
        add(m_Auth_SHACheckBox); 
    
        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
        
        c.gridwidth = 1;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_noAuthNoPrivCheckBox, c);
        add(m_noAuthNoPrivCheckBox);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);

        add(label);
        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
        
        // authentication/privacy password edit fields
        c.gridwidth = 1;
        label = new Label("Authentication Password: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_authPasswordField, c);
        add(m_authPasswordField);

        label = new Label("Privacy Password: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_privPasswordField, c);
        add(m_privPasswordField);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
        
        // context engine/name edit fields
        c.gridwidth = 1;
        label = new Label("Context Engine ID: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_contextEngineIDField, c);
        add(m_contextEngineIDField);

        label = new Label("Context Name: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_contextNameField, c);
        add(m_contextNameField);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        // buttons
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = 1;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_start, c);
        add(m_start);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_stop, c);
        add(m_stop);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
 
        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weighty = 3;
        m_textArea = new TextArea();
        gridbag.setConstraints(m_textArea, c);
        add(m_textArea);
       
        c.weightx = GridBagConstraints.REMAINDER;
        c.weighty = 1;
        m_trapOutput = new TextArea();
        gridbag.setConstraints(m_trapOutput, c);
        add(m_trapOutput);      
    }


    public synchronized void itemStateChanged(ItemEvent event) 
    {
        int iStateChange = event.getStateChange();
        
        if (event.getItemSelectable() == m_noAuthNoPrivCheckBox
            && iStateChange == event.SELECTED)
        {
            m_iSecurityLevel = SnmpSecurityLevels.noAuthNoPriv;
        }
        else if (event.getItemSelectable() == m_AuthNoPrivCheckBox
            && iStateChange == event.SELECTED)
        {
            m_iSecurityLevel = SnmpSecurityLevels.AuthNoPriv;
        }
        else if (event.getItemSelectable() == m_AuthPrivCheckBox
            && iStateChange == event.SELECTED)
        {
            m_iSecurityLevel = SnmpSecurityLevels.AuthPriv;
        }
        else if (event.getItemSelectable() == m_Auth_MD5CheckBox
            && iStateChange == event.SELECTED)
        {
            m_iAuthScheme = USMSecurityInfo.Auth_MD5;
        }
        else if (event.getItemSelectable() == m_Auth_SHACheckBox
            && iStateChange == event.SELECTED)
        {
            m_iAuthScheme = USMSecurityInfo.Auth_SHA;
        }
    }

    
    private void initService() 
        throws Exception
    {
        m_service = SnmpLocalInterfaces.getService();
        m_serviceConfiguration = SnmpLocalInterfaces.getServiceConfiguration(m_service);
        m_serviceConfiguration.setBufferDelay(0);

        SnmpTrapProfile stdProfile;
        stdProfile = SnmpLocalInterfaces.getTrapProfileFactory().createSnmpTrapProfile(162);
        m_service.addTrapListenerProfile(this, stdProfile);
    }

    /**
     * The walk begins here...
     */
    public void run()
    {
        System.out.println("Walking MIB Tree of " + m_szManagedHost);
        updateText("Walking MIB Tree of " + m_szManagedHost);
        getNext();
    }


    /* *
     * Update the UI with a new text string...
     */
    private synchronized void updateText(String s)
    {
        // Clear the text area periodically...
        
        if (m_textArea.getText().length() > 5000)
        {
            m_textArea.setText("");
        }

        // Append the new string to the existing text...

        m_textArea.append(s + "\n");
    }

    /* *
     * Update the UI with a new text string...
     */
    private synchronized void updateTrapText(String s)
    {
        // Clear the text area periodically...
        
        if (m_trapOutput.getText().length() > 500)
        {
            m_trapOutput.setText("");
        }

        // Append the new string to the existing text...

        m_trapOutput.append(s + "\n");
    }


    // -- ActionListener interface
    
    public synchronized void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == m_start)
        {
            // Upon a "start signal", clear the display, grab the
            // managed host from the UI, initialize the walk variables,
            // and start the run thread.
        
            try
            {
                m_szManagedHost = m_hostField.getText();
                String szUserName = m_userNameField.getText();
                int iPrivScheme = USMSecurityInfo.Priv_DES;

                String szContextEngineID = m_contextEngineIDField.getText();
                String szContextName = m_contextNameField.getText();
                String szAuthPassword = "";
                String szPrivPassword = "";
                if (m_iSecurityLevel == SnmpSecurityLevels.AuthNoPriv)
                {
                    szAuthPassword = m_authPasswordField.getText();
                }
                else if (m_iSecurityLevel == SnmpSecurityLevels.AuthPriv)
                {
                    szAuthPassword = m_authPasswordField.getText();
                    szPrivPassword = m_privPasswordField.getText();
                }
                
                if (m_szManagedHost.length() <= 0)
                {
                    System.out.println("Empty host");
                    updateText("Empty host");
                }
                else if (szUserName.length() <= 0)
                {
                    System.out.println("Empty user name");
                    updateText("Empty user name");
                }
                else
                {
                    updateText(" ");
                    m_orderinfo = new SnmpOrderInfo(2, 3, 0);
                    USMSecurityInfo secInfo;
                    try
                    {
                        secInfo = new USMSecurityInfo(szUserName.getBytes("ASCII"), 
                                                      szAuthPassword.getBytes("ASCII"), 
                                                      szPrivPassword.getBytes("ASCII"),
                                                      m_iAuthScheme, 
                                                      iPrivScheme, 
                                                      (byte)m_iSecurityLevel);
                    }
                    catch(UnsupportedEncodingException uee)
                    {
                        secInfo = new USMSecurityInfo(szUserName.getBytes(), 
                                                      szAuthPassword.getBytes(), 
                                                      szPrivPassword.getBytes(),
                                                      m_iAuthScheme, 
                                                      iPrivScheme, 
                                                      (byte)m_iSecurityLevel);
                    }
                    
                    SnmpAuthoritativeSessionFactory cSnmpAuthoritativeSessionFactory = SnmpLocalInterfaces.getAuthoritativeSessionFactory();
                    try
                    {
                        m_cSnmpRemoteAuthoritativeSession = cSnmpAuthoritativeSessionFactory.createRemoteAuthoritativeSession(m_szManagedHost,
                                                                                                              161,
                                                                                                             szContextEngineID.getBytes("ASCII"),
                                                                                                             szContextName.getBytes("ASCII"),
                                                                                                             secInfo);
                    }
                    catch(UnsupportedEncodingException uee)
                    {
                        m_cSnmpRemoteAuthoritativeSession = cSnmpAuthoritativeSessionFactory.createRemoteAuthoritativeSession(m_szManagedHost,
                                                                                                              161,
                                                                                                             szContextEngineID.getBytes(),
                                                                                                             szContextName.getBytes(),
                                                                                                             secInfo);
                    }
            
                    m_iNumRetrieved = 0;            
                    m_szLastOID[0] = "1.3.6";
                    m_bWalking = true;
                    m_start.setEnabled(false);
                    m_stop.setEnabled(true);
                    m_runThread = new Thread(this);
                    m_runThread.start();            
                }
            }
            catch(UnknownHostException uhe)
            {
                String szOutString = new String("Unknown host: " + m_szManagedHost);
                System.out.println(szOutString);
                updateText(szOutString);
            }
            catch(SnmpSecurityException sse)
            {
                System.out.println("Failed to create USMSecurityInfo: " + sse.getMessage());
                updateText("Failed to create USMSecurityInfo: " + sse.getMessage());
            }
            catch(Exception e)
            {
                System.out.println("Failed to create USMSecurityInfo: " + e.getMessage());
                updateText("Failed to create USMSecurityInfo: " + e.getMessage());
            }
        }
        else if (event.getSource() == m_stop)
        {
            // Stop the walk...
            
            m_bWalking = false;
            m_start.setEnabled(true);
            m_stop.setEnabled(false);
        }
            
    }
    
    // -- SnmpCustomer Implementation

    /**
     * callback for delivering successes.  In this case the implementation
     * is to print out the Object ID received and then use it for the basis
     * for the get-next request
     */
    public synchronized void deliverSuccessfulOrder(int iOrderNum,
                                                    SnmpVarBind vb)
    {
        String szOutString = new String(iOrderNum + ": " + vb.getName() + " (" + vb.getStringValue() + ")");
        System.out.println(szOutString);
        updateText(szOutString);
        
        if (m_szLastOID[0].compareTo(vb.getName()) != 0)
        {
            m_szLastOID[0] = vb.getName();

            if (m_bWalking) {
                getNext();
            }
        }
        else
        {
            System.out.println("Finished");
            updateText("Finished");

            m_start.setEnabled(true);
            m_stop.setEnabled(false);
        }
    }

    /**
     * callback for failures.  In this case if a failure is received,
     * stop walking the MIB
     */
    public void deliverFailedOrder(int iOrderNum, int iErrorStatus)
    {
        String szOutString = new String(iOrderNum + ": order failed (" + SnmpConstants.errorIDToString(iErrorStatus) + ")");
        System.out.println(szOutString);
        System.out.println("Ending MIB Walk");

        updateText(szOutString);
        updateText("Ending MIB Walk");

        m_start.setEnabled(true);
        m_stop.setEnabled(false);
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


    // -- SnmpTrapListener
    
    public void trapReceived(SnmpTrapEvent trap)
    {
        //Prints out all the information contained in the trap
        //Prints out all the information contained in the trap
        String szTrapType;
        if (trap.getType() == SnmpConstants.SNMP_TRAP)
        {
            szTrapType = new String("a SNMPv1 trap");
        }
        else if (trap.getType() == SnmpConstants.SNMP_TRAPV2)
        {
            szTrapType = new String("a SNMPv2 trap");
        }
        else if (trap.getType() == SnmpConstants.SNMP_INFORM)
        {
            szTrapType = new String("a SNMPv2 inform");
        }
        else
        {
            szTrapType = new String("an unexpected type (" + trap.getType() + " is not a SNMPv1 trap)");
        }
                 
        String szOutString = new String("\nReceived " + szTrapType + " ...\n"
                                        + "\tPort : " + trap.getPort()
                                        + "\n\tGenerating Agent : " + trap.getAgentIPAddress()
                                        + "\n\tSending Agent : " + trap.getSendersIPAddress()
                                        + "\n\tTime Stamp : " + trap.getTimeStamp()
                                        + "\n\tEnterprise OID : " + trap.getEnterpriseOID()
                                        + "\n\tTrap Type : " + trap.getTrapType());
        updateTrapText(szOutString);
                
        if (trap.getNumberOfVarBinds() > 0)
        {
            updateTrapText("\tVarBinds:");
        }
        
        for (int i = 0; i < trap.getNumberOfVarBinds(); i++)
        {
            SnmpVarBind vb = trap.getVarBind(i);
            szOutString = new String("\t\t" + vb.getName() + " (" + vb.getStringValue() + ")");
            updateTrapText(szOutString);
        }

    }
}


