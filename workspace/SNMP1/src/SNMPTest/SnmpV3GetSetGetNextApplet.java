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
import java.math.*;
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
 *   &lt;applet code="SnmpV3GetSetGetNextApplet" height=400 width=500>
 *   &lt;/applet>
 *   &lt;/body>&lt;/html>
 * </pre>
 * <p>
 *
 * Note: The CLASSPATH must be initialized correctly before
 * starting the browser.
 */
public class SnmpV3GetSetGetNextApplet
extends Applet
implements SnmpCustomer, ActionListener, ItemListener
{
    // ------------------------------ Constants ---------------------------------

    // ------------------------------ Class variables ---------------------------

    // ------------------------------ Member (instance) variables ---------------

    private SnmpService                             m_service;
    private SnmpServiceConfiguration                m_serviceConfiguration;
    private int                                     m_iNumRetrieved;
    private SnmpAuthoritativeSession                m_cSnmpRemoteAuthoritativeSession;
    private SnmpOrderInfo                           m_orderinfo;
    private int                                     m_iOrderNumStart;
    private String                                  m_szManagedHost = null;
    private int                                     m_iSecurityLevel = SnmpSecurityLevels.noAuthNoPriv;
    private int                                     m_iAuthScheme = USMSecurityInfo.Auth_MD5;
    private TextArea                                m_CommandOutput = null;
    private TextArea                                m_responseOutput = null;

    private Button                                  m_startSet = null;
    private Button                                  m_startGet = null;
    private Button                                  m_startGetNext = null;
    
    private TextField                               m_hostField = null;
    private TextField                               m_userNameField = null;
    private TextField                               m_authPasswordField = null;
    private TextField                               m_privPasswordField = null;
    private TextField                               m_contextEngineIDField = null;
    private TextField                               m_contextNameField = null;
    private TextField                               m_OIDField = null;
    private TextField                               m_valueField = null;

    private Checkbox                                m_noAuthNoPrivCheckBox = null;
    private Checkbox                                m_AuthNoPrivCheckBox = null;
    private Checkbox                                m_AuthPrivCheckBox = null;
    private Checkbox                                m_Auth_MD5CheckBox = null;
    private Checkbox                                m_Auth_SHACheckBox = null;
    
    private Checkbox                                m_ASN_INTEGERCheckBox = null;
    private Checkbox                                m_ASN_OCTSTRCheckBox = null;
    private Checkbox                                m_ASN_NULLCheckBox = null;
    private Checkbox                                m_ASN_OIDCheckBox = null;
    private Checkbox                                m_ASN_IPADDRESSCheckBox = null;
    private Checkbox                                m_ASN_COUNTERCheckBox = null;
    private Checkbox                                m_ASN_GAUGECheckBox = null;
    private Checkbox                                m_ASN_TIMETICKSCheckBox = null;
    private Checkbox                                m_ASN_OPAQUECheckBox = null;
    private Checkbox                                m_ASN_COUNTER64CheckBox = null;
    private Checkbox                                m_ASN_UINTEGER32CheckBox = null;
    
    private byte                                    m_bType;
    
    
    // ------------------------------ Methods -----------------------------------

    /**
     *
     */
    public SnmpV3GetSetGetNextApplet()
    {
//        try
//        {
//            Security.addProvider(new com.sun.crypto.provider.SunJCE());
//        }
//        catch (Error err)
//        {
//             // do nothing ... encyption will fail if provider not installed
//        }
//        catch (Exception e)
//        {
//            // do nothing ... encyption will fail if provider not installed
//        }
        
        m_iOrderNumStart = 1;
        m_bType = SnmpConstants.ASN_INTEGER;
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

        m_hostField = new TextField(10);
        m_userNameField = new TextField(10);
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
        
        m_OIDField = new TextField(10);
        m_valueField = new TextField(10);
        CheckboxGroup checkBoxGroup = new CheckboxGroup();

        m_ASN_INTEGERCheckBox = new Checkbox("INTEGER", checkBoxGroup, true);
        m_ASN_OCTSTRCheckBox = new Checkbox("OCTET STRING", checkBoxGroup, false);
        m_ASN_NULLCheckBox = new Checkbox("Null", checkBoxGroup, false);
        m_ASN_OIDCheckBox = new Checkbox("OBJECT IDENTIFIER", checkBoxGroup, false);
        m_ASN_IPADDRESSCheckBox = new Checkbox("IpAddress", checkBoxGroup, false);
        m_ASN_COUNTERCheckBox = new Checkbox("Counter", checkBoxGroup, false);
        m_ASN_GAUGECheckBox = new Checkbox("Gauge", checkBoxGroup, false);
        m_ASN_TIMETICKSCheckBox = new Checkbox("TimeTicks", checkBoxGroup, false);
        m_ASN_OPAQUECheckBox = new Checkbox("Opaque", checkBoxGroup, false);
        m_ASN_COUNTER64CheckBox = new Checkbox("Counter64", checkBoxGroup, false);
        m_ASN_UINTEGER32CheckBox = new Checkbox("Unsigned32", checkBoxGroup, false);
        
        m_startSet = new Button("Set");
        m_startGet = new Button("Get");
        m_startGetNext = new Button("GetNext");

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
        m_startSet.addActionListener(this);
        m_startGet.addActionListener(this);
        m_startGetNext.addActionListener(this);
        m_OIDField.addActionListener(this);
        m_valueField.addActionListener(this);
        
        m_ASN_INTEGERCheckBox.addItemListener(this);
        m_ASN_OCTSTRCheckBox.addItemListener(this);
        m_ASN_NULLCheckBox.addItemListener(this);
        m_ASN_OIDCheckBox.addItemListener(this);
        m_ASN_IPADDRESSCheckBox.addItemListener(this);
        m_ASN_COUNTERCheckBox.addItemListener(this);
        m_ASN_GAUGECheckBox.addItemListener(this);
        m_ASN_TIMETICKSCheckBox.addItemListener(this);
        m_ASN_OPAQUECheckBox.addItemListener(this);
        m_ASN_COUNTER64CheckBox.addItemListener(this);
        m_ASN_UINTEGER32CheckBox.addItemListener(this);

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

        // OID/Value edit and Type checkbox fields plus buttons
        c.gridwidth = 1;
        label = new Label("OID: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_OIDField, c);
        add(m_OIDField);

        label = new Label("Value: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_valueField, c);
        add(m_valueField);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        // Type checkbox fields plus buttons
        c.gridwidth = 1;
        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label("Type: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_INTEGERCheckBox, c);
        add(m_ASN_INTEGERCheckBox);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = 1;
        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_ASN_OCTSTRCheckBox, c);
        add(m_ASN_OCTSTRCheckBox);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);        

        c.gridwidth = 1;
        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_startGet, c);
        add(m_startGet);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_NULLCheckBox, c);
        add(m_ASN_NULLCheckBox);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);
        
        c.gridwidth = 1;
        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_OIDCheckBox, c);
        add(m_ASN_OIDCheckBox);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);        
        
        c.gridwidth = 1;
        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_startSet, c);
        add(m_startSet);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_IPADDRESSCheckBox, c);
        add(m_ASN_IPADDRESSCheckBox);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = 1;
        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_COUNTERCheckBox, c);
        add(m_ASN_COUNTERCheckBox);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);        
        
        c.gridwidth = 1;
        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_startGetNext, c);
        add(m_startGetNext);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_GAUGECheckBox, c);
        add(m_ASN_GAUGECheckBox);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);        
        
        c.gridwidth = 1;
        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_TIMETICKSCheckBox, c);
        add(m_ASN_TIMETICKSCheckBox);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);        
        
        c.gridwidth = 1;
        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_OPAQUECheckBox, c);
        add(m_ASN_OPAQUECheckBox);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);        

        c.gridwidth = 1;
        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_COUNTER64CheckBox, c);
        add(m_ASN_COUNTER64CheckBox);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);        
        
        c.gridwidth = 1;
        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_UINTEGER32CheckBox, c);
        add(m_ASN_UINTEGER32CheckBox);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);        

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);        
        
        c.weightx = GridBagConstraints.REMAINDER;
        c.weighty = 4;
        m_responseOutput = new TextArea();
        gridbag.setConstraints(m_responseOutput, c);
        add(m_responseOutput);      

        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weighty = 1;
        m_CommandOutput = new TextArea();
        gridbag.setConstraints(m_CommandOutput, c);
        add(m_CommandOutput);
    }


    private void initService() 
        throws Exception
    {
        m_service = SnmpLocalInterfaces.getService();
        m_serviceConfiguration = SnmpLocalInterfaces.getServiceConfiguration(m_service);
        m_serviceConfiguration.setBufferDelay(0);
    }


    /* *
     * Update the UI with a new text string...
     */
    private synchronized void updateResponseOutput(String s)
    {
        // Clear the text area periodically...
        
        if (m_responseOutput.getText().length() > 5000)
        {
            m_responseOutput.setText("");
        }

        // Append the new string to the existing text...

        m_responseOutput.append(s + "\n");
    }

    // -- ItemListener interface
    public synchronized void itemStateChanged(ItemEvent event) 
    {
        int iStateChange = event.getStateChange();
        
        if (event.getItemSelectable() == m_ASN_INTEGERCheckBox
            && iStateChange == event.SELECTED)
        {
            m_bType = SnmpConstants.ASN_INTEGER;
        }
        else if (event.getItemSelectable() == m_ASN_OCTSTRCheckBox
            && iStateChange == event.SELECTED)
        {
            m_bType = SnmpConstants.ASN_OCTSTR;
        }
        else if (event.getItemSelectable() == m_ASN_NULLCheckBox
            && iStateChange == event.SELECTED)
        {
            m_bType = SnmpConstants.ASN_NULL;
        }
        else if (event.getItemSelectable() == m_ASN_OIDCheckBox
            && iStateChange == event.SELECTED)
        {
            m_bType = SnmpConstants.ASN_OID;
        }
        else if (event.getItemSelectable() == m_ASN_IPADDRESSCheckBox
            && iStateChange == event.SELECTED)
        {
            m_bType = SnmpConstants.ASN_IPADDRESS;
        }
        else if (event.getItemSelectable() == m_ASN_COUNTERCheckBox
            && iStateChange == event.SELECTED)
        {
            m_bType = SnmpConstants.ASN_COUNTER;
        }
        else if (event.getItemSelectable() == m_ASN_GAUGECheckBox
            && iStateChange == event.SELECTED)
        {
            m_bType = SnmpConstants.ASN_GAUGE;
        }
        else if (event.getItemSelectable() == m_ASN_TIMETICKSCheckBox
            && iStateChange == event.SELECTED)
        {
            m_bType = SnmpConstants.ASN_TIMETICKS;
        }
        else if (event.getItemSelectable() == m_ASN_OPAQUECheckBox
            && iStateChange == event.SELECTED)
        {
            m_bType = SnmpConstants.ASN_OPAQUE;
        }
        else if (event.getItemSelectable() == m_ASN_COUNTER64CheckBox
            && iStateChange == event.SELECTED)
        {
            m_bType = SnmpConstants.ASN_COUNTER64;
        }
        else if (event.getItemSelectable() == m_ASN_UINTEGER32CheckBox
            && iStateChange == event.SELECTED)
        {
            m_bType = SnmpConstants.ASN_GAUGE; // see RFC1905 (http://www.ietf.org/rfc/rfc1905.txt?number=1905)
        }
        else if (event.getItemSelectable() == m_noAuthNoPrivCheckBox
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
    
    // -- ActionListener interface
    
    public synchronized void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == m_startGet
            || event.getSource() == m_startGetNext
            || event.getSource() == m_startSet)
        {
            // Upon a "start signal", clear the display, grab the
            // managed host from the UI, initialize the walk variables,
            // and start the run thread.
            try
            {
                m_CommandOutput.setText("");
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
                    m_CommandOutput.setText("Empty host");
                }
                else if (szUserName.length() <= 0)
                {
                    System.out.println("Empty user name");
                    m_CommandOutput.setText("Empty user name");
                }
                else if (m_OIDField.getText().length() <= 0)
                {
                    System.out.println("Empty OID");
                    m_CommandOutput.setText("Empty OID");
                }
                else if (m_valueField.getText().length() <= 0
                    && event.getSource() == m_startSet)
                {
                    System.out.println("Empty value");
                    m_CommandOutput.setText("Empty value");
                }
                else
                {
                    updateResponseOutput(" ");
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
                                                                                                             szContextEngineID.getBytes("ASCII"),
                                                                                                             secInfo);
                    }
                    catch(UnsupportedEncodingException uee)
                    {
                        m_cSnmpRemoteAuthoritativeSession = cSnmpAuthoritativeSessionFactory.createRemoteAuthoritativeSession(m_szManagedHost,
                                                                                                              161,
                                                                                                             szContextEngineID.getBytes(),
                                                                                                             szContextEngineID.getBytes(),
                                                                                                             secInfo);
                    }
            
                    Object obj = null;

                    if (event.getSource() == m_startSet)
                    {
                        switch (m_bType)
                        {
                            case SnmpConstants.ASN_NULL:
                                break;
                       
                            case SnmpConstants.ASN_IPADDRESS:
                            case SnmpConstants.ASN_OID:
                                try
                                {
                                    obj = new String(m_valueField.getText());
                                }
                                catch (Exception e)
                                {
                                    System.out.println("Failed to convert \"" + m_valueField.getText() + "\" to ASN_OID or ASN_IPADDRESS object value (String)");
                                    m_CommandOutput.setText("Failed to convert \"" + m_valueField.getText() + "\" to ASN_OID or ASN_IPADDRESS object value (String)");
                                    return;
                                }
                                
                                break;
                        
                            case SnmpConstants.ASN_OPAQUE:
                            case SnmpConstants.ASN_OCTSTR:
                                try
                                {
                                    byte[] bArray = new byte[m_valueField.getText().getBytes().length];
                                    System.arraycopy(m_valueField.getText().getBytes(), 0, bArray, 0, m_valueField.getText().getBytes().length);
                                    obj = bArray;
                                }
                                catch (Exception e)
                                {
                                    System.out.println("Failed to convert \"" + m_valueField.getText() + "\" to ASN_OPAQUE or ASN_OCTSTR object value (byte)");
                                    m_CommandOutput.setText("Failed to convert \"" + m_valueField.getText() + "\" to ASN_OPAQUE or ASN_OCTSTR object value (byte)");
                                    return;
                                }
                                
                                break;
                        
                            case SnmpConstants.ASN_COUNTER:
                            case SnmpConstants.ASN_GAUGE:
                            case SnmpConstants.ASN_TIMETICKS:
                            case SnmpConstants.ASN_UINTEGER32:
                                try
                                {
                                    obj = new Integer(m_valueField.getText());
                                }
                                catch (Exception e)
                                {
                                    System.out.println("Failed to convert \"" + m_valueField.getText() + "\" to ASN_COUNTER, ASN_GAUGE, ASN_TIMETICKS, or ASN_UINTEGER32 object value (Integer)");
                                    m_CommandOutput.setText("Failed to convert \"" + m_valueField.getText() + "\" to ASN_COUNTER, ASN_GAUGE, ASN_TIMETICKS, or ASN_UINTEGER32 object value (Integer)");
                                    return;
                                }
                                
                                break;

                            case SnmpConstants.ASN_INTEGER:
                            case SnmpConstants.ASN_COUNTER64:
                                try
                                {
                                    obj = new BigInteger(m_valueField.getText());
                                }
                                catch (Exception e)
                                {
                                    System.out.println("Failed to convert \"" + m_valueField.getText() + "\" to ASN_INTEGER or ASN_COUNTER64 object value (BigInteger)");
                                    m_CommandOutput.setText("Failed to convert \"" + m_valueField.getText() + "\" to ASN_INTEGER or ASN_COUNTER64 object value (BigInteger)");
                                    return;
                                }
                                
                                break;
                        }

                        String[] szOIDs = new String[1];
                        szOIDs[0] = new String(m_OIDField.getText());
                    
                        byte[] bTypes = new byte[1];
                        bTypes[0] = m_bType;
                        Object[] objValues = new Object[1];
                        objValues[0] = obj;

                        switch (m_bType)
                        {
                            case SnmpConstants.ASN_NULL:
                                m_CommandOutput.setText(m_iOrderNumStart + " set order placed: " + szOIDs[0].toString() + "(null)");
                                break;
                       
                            case SnmpConstants.ASN_OPAQUE:
                            case SnmpConstants.ASN_OCTSTR:
                                m_CommandOutput.setText(m_iOrderNumStart + " set order placed: " + szOIDs[0].toString() + "(" + new String((byte[])objValues[0]) + ")");
                                break;
                        
                            default:
                                m_CommandOutput.setText(m_iOrderNumStart + " set order placed: " + szOIDs[0].toString() + "(" + objValues[0].toString() + ")");
                                break;
                        }
                    
                        int iVal = m_service.placeSetOrder(m_cSnmpRemoteAuthoritativeSession,
                                                           m_orderinfo,
                                                           true,
                                                           szOIDs,
                                                           bTypes,
                                                           objValues,
                                                           this,
                                                           m_iOrderNumStart);
                
                        if (iVal != 0)
                        {
                            m_startSet.setEnabled(false);
                            m_startGet.setEnabled(false);
                            m_startGetNext.setEnabled(false);
                            m_iOrderNumStart = iVal;
                        }
                        else
                        {
                            System.out.println("placeSetOrder() failed");
                            m_CommandOutput.setText("placeSetOrder() failed");
                        }
                    }
                    else if (event.getSource() == m_startGet)
                    {
                        String[] szOIDs = new String[1];
                        szOIDs[0] = new String(m_OIDField.getText());
                        m_CommandOutput.setText(m_iOrderNumStart + " Get order placed: " + szOIDs[0].toString());
                        int iVal = m_service.placeGetOrder(m_cSnmpRemoteAuthoritativeSession,
                                                           m_orderinfo,
                                                           false,
                                                           szOIDs,
                                                           this,
                                                           m_iOrderNumStart);
                
                        if (iVal != 0)
                        {
                            m_startSet.setEnabled(false);
                            m_startGet.setEnabled(false);
                            m_startGetNext.setEnabled(false);
                            m_iOrderNumStart = iVal;
                        }
                        else
                        {
                            System.out.println("placeGetOrder() failed");
                            m_CommandOutput.setText("placeGetOrder() failed");
                        }
                    }
                    else if (event.getSource() == m_startGetNext)
                    {
                        String[] szOIDs = new String[1];
                        szOIDs[0] = new String(m_OIDField.getText());
                        m_CommandOutput.setText(m_iOrderNumStart + " GetNext order placed: " + szOIDs[0].toString());
                        int iVal = m_service.placeGetNextOrder(m_cSnmpRemoteAuthoritativeSession,
                                                                m_orderinfo,
                                                                false,
                                                                szOIDs,
                                                                this,
                                                                m_iOrderNumStart);
                
                        if (iVal != 0)
                        {
                            m_startSet.setEnabled(false);
                            m_startGet.setEnabled(false);
                            m_startGetNext.setEnabled(false);
                            m_iOrderNumStart = iVal;
                        }
                        else
                        {
                            System.out.println("placeGetNextOrder() failed");
                            m_CommandOutput.setText("placeGetNextOrder() failed");
                        }
                    }
                }
            }
            catch(UnknownHostException uhe)
            {
                String szOutString = new String("Unknown host: " + m_szManagedHost);
                System.out.println(szOutString);
                m_CommandOutput.setText(szOutString);
                m_startSet.setEnabled(true);
                m_startGet.setEnabled(true);
                m_startGetNext.setEnabled(true);
            }
            catch(SnmpSecurityException sse)
            {
                System.out.println("Failed to create CSMSecurityInfo");
                m_CommandOutput.setText("Failed to create CSMSecurityInfo");
                m_startSet.setEnabled(true);
                m_startGet.setEnabled(true);
                m_startGetNext.setEnabled(true);
            }
            catch (Exception e)
            {
                System.out.println(e.toString());
                m_CommandOutput.setText(e.toString());
                m_startSet.setEnabled(true);
                m_startGet.setEnabled(true);
                m_startGetNext.setEnabled(true);
            }
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
        String szOutString = new String(iOrderNum + " order succeeded: " + vb.getName() + " (" + vb.getStringValue() + ")");
        System.out.println(szOutString);
        updateResponseOutput(szOutString);
        m_startSet.setEnabled(true);
        m_startGet.setEnabled(true);
        m_startGetNext.setEnabled(true);
    }

    /**
     * callback for failures.  In this case if a failure is received,
     * stop walking the MIB
     */
    public void deliverFailedOrder(int iOrderNum, int iErrorStatus)
    {
        String szOutString = new String(iOrderNum + " order failed :" + SnmpConstants.errorIDToString(iErrorStatus));
        System.out.println(szOutString);
        updateResponseOutput(szOutString);
        m_startSet.setEnabled(true);
        m_startGet.setEnabled(true);
        m_startGetNext.setEnabled(true);
    }
}

