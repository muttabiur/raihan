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
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;
import java.math.*;

import com.outbackinc.services.protocol.snmp.*;
import com.outbackinc.services.protocol.snmp.CSM.*;
import com.outbackinc.services.protocol.snmp.rmi.*;
import java.io.UnsupportedEncodingException;

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
 *   &lt;applet code="SnmpV1GetSetGetNextApplet">
 *   &lt;/applet>
 *   &lt;/body>&lt;/html>
 * </pre>
 * <p>
 *
 * Note: The CLASSPATH must be initialized correctly before
 * starting the browser.
 */
public class RmiV1GetSetGetNextApplet
extends Applet
implements SnmpCustomer, ActionListener, ItemListener
{
    // ------------------------------ Constants ---------------------------------

    // ------------------------------ Class variables ---------------------------

    // ------------------------------ Member (instance) variables ---------------

    private RMISnmpClient                           m_cRMISnmpClient = null;
    private SnmpAuthoritativeSessionFactory         m_cSnmpAuthoritativeSessionFactory = null;
    private SnmpService                             m_cSnmpService;
    //private SnmpServiceConfiguration                m_serviceConfiguration;
    private int                                     m_iNumRetrieved;
    private SnmpAuthoritativeSession                m_cSnmpAuthoritativeSession;
    private SnmpOrderInfo                           m_cSnmpOrderInfo;
    private int                                     m_iOrderNumStart = 1;
    private String                                  m_szRMIHost = null;
    private String                                  m_szManagedHost = null;

    private TextArea                                m_CommandOutput = null;
    private TextArea                                m_responseOutput = null;

    private Button                                  m_startSet = null;
    private Button                                  m_startGet = null;
    private Button                                  m_startGetNext = null;
    private TextField                               m_rmiHostField = null;
    private TextField                               m_hostField = null;
    private TextField                               m_readCommunityField = null;
    private TextField                               m_writeCommunityField = null;
    private TextField                               m_OIDField = null;
    private TextField                               m_valueField = null;
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
    public RmiV1GetSetGetNextApplet()
    {
        m_bType = SnmpConstants.ASN_INTEGER;
    }


    /**
     *
     */
    public void init()
    {
        // Create the ui...
        initUI();
    }

    /**
     *
     */
    private void initUI()
    {
        setBackground(Color.lightGray);

        m_rmiHostField = new TextField(10);
        m_hostField = new TextField(10);
        m_readCommunityField = new TextField(10);
        m_writeCommunityField = new TextField(10);
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

        m_rmiHostField.addActionListener(this);
        m_hostField.addActionListener(this);
        m_readCommunityField.addActionListener(this);
        m_writeCommunityField.addActionListener(this);
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

        m_startSet.addActionListener(this);
        m_startGet.addActionListener(this);
        m_startGetNext.addActionListener(this);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        setLayout(gridbag);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;

        
        c.gridwidth = GridBagConstraints.REMAINDER;
        Label label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = 1;
        label = new Label("jSNMP RMI Server Host:", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_rmiHostField, c);
        add(m_rmiHostField);

        label = new Label("SNMP Agent Host: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_hostField, c);
        add(m_hostField);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = 1;
        label = new Label("Read Community:", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);
                
        gridbag.setConstraints(m_readCommunityField, c);
        add(m_readCommunityField);

        label = new Label("Write Community:", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);
                
        gridbag.setConstraints(m_writeCommunityField, c);
        add(m_writeCommunityField);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
        
        c.gridwidth = 1;
        label = new Label("OID:", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_OIDField, c);
        add(m_OIDField);
        
        label = new Label("Value:", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_valueField, c);
        add(m_valueField);

        label = new Label("Type:", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        c.anchor = GridBagConstraints.CENTER;
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

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_OCTSTRCheckBox, c);
        add(m_ASN_OCTSTRCheckBox);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

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

        label = new Label(" ");
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

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_OIDCheckBox, c);
        add(m_ASN_OIDCheckBox);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

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

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_COUNTERCheckBox, c);
        add(m_ASN_COUNTERCheckBox);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

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

        gridbag.setConstraints(m_startGetNext, c);
        add(m_startGetNext);
        
        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_GAUGECheckBox, c);
        add(m_ASN_GAUGECheckBox);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

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

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_TIMETICKSCheckBox, c);
        add(m_ASN_TIMETICKSCheckBox);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

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

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_OPAQUECheckBox, c);
        add(m_ASN_OPAQUECheckBox);
        
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

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

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ", Label.LEFT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_COUNTER64CheckBox, c);
        add(m_ASN_COUNTER64CheckBox);
        
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

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


    private boolean initService() 
    {
        String rmiHost = null;
        try
        {
            rmiHost = m_rmiHostField.getText();
            if (rmiHost.length() > 0)
            {
                updateResponseOutput(" ");
                if (m_szRMIHost == null
                    || m_szRMIHost.compareTo(rmiHost) != 0)
                {
                    m_cRMISnmpClient = new RMISnmpClient(rmiHost);
                    m_cSnmpService = m_cRMISnmpClient.getService();
                    m_cSnmpAuthoritativeSessionFactory = m_cRMISnmpClient.getAuthoritativeSessionFactory();
                    
                    if (m_szRMIHost != null)
                    {
                        System.out.println("Disconnected from jSNMP RMI Server " + m_szRMIHost + "; connected to jSNMP RMI Server " + rmiHost);
                        updateResponseOutput("Disconnected from jSNMP RMI Server " + m_szRMIHost + "; connected to jSNMP RMI Server " + rmiHost);
                    }
                    else
                    {
                        m_CommandOutput.setText("Connected to jSNMP RMI Server " + rmiHost);
                        updateResponseOutput("Connected to jSNMP RMI Server " + rmiHost);
                    }
                    
                    m_szRMIHost = new String(rmiHost);
                    m_iOrderNumStart = 1;
                }
            }
            else
            {
                if (m_szRMIHost != null)
                {
                    m_CommandOutput.setText("Empty RMI Server Host; disconnected from jSNMP RMI Server " + m_szRMIHost);
                    System.out.println("Empty RMI Server Host; disconnected from jSNMP RMI Server " + m_szRMIHost);
                }
                else
                {
                    m_CommandOutput.setText("Empty RMI Server Host");
                    System.out.println("Empty RMI Server Host");
                }
                    
                m_szRMIHost = null;
                m_cRMISnmpClient = null;
                m_cSnmpService = null;
                m_cSnmpAuthoritativeSessionFactory = null;
                return false;
            }

            return true;
        }
        catch (Exception e)
        {
            m_szRMIHost = null;
            m_cRMISnmpClient = null;
            m_cSnmpService = null;
            m_cSnmpAuthoritativeSessionFactory = null;
            System.out.println("Couldn't connect to jSNMP RMI Server " + rmiHost + " (" + e.toString() + ")");
            m_CommandOutput.setText("Couldn't connect to jSNMP RMI Server " + rmiHost + " (" + e.toString() + ")");
            e.printStackTrace();
            return false;
        }
        catch (Error er)
        {
            m_szRMIHost = null;
            m_cRMISnmpClient = null;
            m_cSnmpService = null;
            m_cSnmpAuthoritativeSessionFactory = null;
            System.out.println("Couldn't connect to jSNMP RMI Server " + rmiHost + " (" + er.toString() + ")");
            m_CommandOutput.setText("Couldn't connect to jSNMP RMI Server " + rmiHost + " (" + er.toString() + ")");
            er.printStackTrace();
            return false;
        }
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
    }
    
    
    // -- ActionListener interface
    
    public synchronized void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == m_startGet
            || event.getSource() == m_startGetNext
            || event.getSource() == m_startSet)
        {
            if (!initService())
            {
                return;
            }
            
            // Upon a "start signal", clear the display, grab the
            // managed host from the UI, initialize the walk variables,
            // and start the run thread.
            try
            {
                m_CommandOutput.setText("");
                if (m_hostField.getText().length() <= 0)
                {
                    System.out.println("Empty host");
                    m_CommandOutput.setText("Empty host");
                }
                else if (m_readCommunityField.getText().length() <= 0)
                {
                    System.out.println("Empty read community");
                    m_CommandOutput.setText("Empty read community");
                }
                else if (m_writeCommunityField.getText().length() <= 0)
                {
                    System.out.println("Empty write community");
                    m_CommandOutput.setText("Empty write community");
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
                    m_szManagedHost = m_hostField.getText();
                    String szReadCommunity = m_readCommunityField.getText();
                    String szWriteCommunity = m_writeCommunityField.getText();
                    m_cSnmpOrderInfo = new SnmpOrderInfo(2, 3, 0);
                    CSMSecurityInfo secInfo;
                    try
                    {
                        secInfo = new CSMSecurityInfo(szReadCommunity.getBytes("ASCII"), szWriteCommunity.getBytes("ASCII"));
                    }
                    catch(UnsupportedEncodingException uee)
                    {
                        secInfo = new CSMSecurityInfo(szReadCommunity.getBytes(), szWriteCommunity.getBytes());
                    }
                    
                    System.out.println("Creating session using factory...");
                    m_cSnmpAuthoritativeSession = m_cSnmpAuthoritativeSessionFactory.createRemoteAuthoritativeSession(m_szManagedHost,
                                                                                  161,
                                                                                  SnmpConstants.SNMP_VERSION_1,
                                                                                  secInfo);
                    
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
                    
                        int iVal = m_cSnmpService.placeSetOrder(m_cSnmpAuthoritativeSession,
                                                           m_cSnmpOrderInfo,
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
                        int iVal = m_cSnmpService.placeGetOrder(m_cSnmpAuthoritativeSession,
                                                           m_cSnmpOrderInfo,
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
                        int iVal = m_cSnmpService.placeGetNextOrder(m_cSnmpAuthoritativeSession,
                                                               m_cSnmpOrderInfo,
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


