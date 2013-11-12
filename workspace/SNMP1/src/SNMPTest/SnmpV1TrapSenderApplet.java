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
import java.io.UnsupportedEncodingException;

import com.outbackinc.services.protocol.snmp.*;
import com.outbackinc.services.protocol.snmp.CSM.*;

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
 *   &lt;applet code="SnmpV1TrapSenderApplet" height=400 width=500>
 *   &lt;/applet>
 *   &lt;/body>&lt;/html>
 * </pre>
 * <p>
 *
 * Note: The CLASSPATH must be initialized correctly before
 * starting the browser.
 */
public class SnmpV1TrapSenderApplet
extends Applet
implements ActionListener, ItemListener
{
    // ------------------------------ Constants ---------------------------------

    // ------------------------------ Class variables ---------------------------

    // ------------------------------ Member (instance) variables ---------------

    private SnmpService                             m_service;
    private SnmpServiceConfiguration                m_serviceConfiguration;
    private int                                     m_iNumRetrieved;
    private SnmpAuthoritativeSession                m_cSnmpLocalAuthoritativeSession;
    private SnmpOrderInfo                           m_orderinfo;
    private int                                     m_iOrderNumStart;
    private String                                  m_szManagedHost = null;


    private TextArea                                m_CommandOutput = null;
    private TextArea                                m_responseOutput = null;

    private Button                                  m_startSendTrap = null;
    private TextField                               m_destHostField = null;
    private TextField                               m_srcHostField = null;
    private TextField                               m_timeticksField = null;
    private TextField                               m_communityField = null;
    
    
    private TextField                               m_trapOIDField = null;
    private TextField                               m_trapNumField = null;
    
    
    
    private TextField                               m_varbindOIDField = null;
    private TextField                               m_varbindValueField = null;
    private Checkbox                                m_ASN_INTEGERCheckBox = null;
    private Checkbox                                m_ASN_OCTSTRCheckBox = null;
    private Checkbox                                m_ASN_NULLCheckBox = null;
    private Checkbox                                m_ASN_OIDCheckBox = null;
    private Checkbox                                m_ASN_IPADDRESSCheckBox = null;
    private Checkbox                                m_ASN_COUNTERCheckBox = null;
    private Checkbox                                m_ASN_GAUGECheckBox = null;
    private Checkbox                                m_ASN_TIMETICKSCheckBox = null;
    private Checkbox                                m_ASN_OPAQUECheckBox = null;
    
    private byte                                    m_bVarbindType;
    
    
    // ------------------------------ Methods -----------------------------------

    /**
     *
     */
    public SnmpV1TrapSenderApplet()
    {
        m_iOrderNumStart = 1;
        m_bVarbindType = SnmpConstants.ASN_INTEGER;
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
        int i;
        int iwidth = 9;
        setBackground(Color.lightGray);

        m_destHostField = new TextField(10);
        m_srcHostField = new TextField(10);
        m_timeticksField = new TextField(5);
        m_communityField = new TextField(10);
        m_trapOIDField = new TextField(10);
        m_trapNumField = new TextField(10);
        m_varbindOIDField = new TextField(10);
        m_varbindValueField = new TextField(10);
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
        
        m_startSendTrap = new Button("Send Trap");

        m_destHostField.addActionListener(this);
        m_srcHostField.addActionListener(this);
        m_timeticksField.addActionListener(this);
        m_communityField.addActionListener(this);
        m_trapOIDField.addActionListener(this);
        m_trapNumField.addActionListener(this);
        m_varbindOIDField.addActionListener(this);
        m_varbindValueField.addActionListener(this);
        
        m_ASN_INTEGERCheckBox.addItemListener(this);
        m_ASN_OCTSTRCheckBox.addItemListener(this);
        m_ASN_NULLCheckBox.addItemListener(this);
        m_ASN_OIDCheckBox.addItemListener(this);
        m_ASN_IPADDRESSCheckBox.addItemListener(this);
        m_ASN_COUNTERCheckBox.addItemListener(this);
        m_ASN_GAUGECheckBox.addItemListener(this);
        m_ASN_TIMETICKSCheckBox.addItemListener(this);
        m_ASN_OPAQUECheckBox.addItemListener(this);

        m_startSendTrap.addActionListener(this);
        
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        setLayout(gridbag);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        Label label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        // hosts and community  edit fields
        c.gridwidth = 1;
        label = new Label("SNMP Target Host: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_destHostField, c);
        add(m_destHostField);

        label = new Label("SNMP Source Host: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_srcHostField, c);
        add(m_srcHostField);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        // Community and TimeTicks edit fields
        c.gridwidth = 1;
        label = new Label("Community:", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);
                
        gridbag.setConstraints(m_communityField, c);
        add(m_communityField);

        label = new Label("Time Ticks:", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);
                
        gridbag.setConstraints(m_timeticksField, c);
        add(m_timeticksField);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        // Trap enterprise OID and Number edit fields
        c.gridwidth = 1;
        label = new Label("Trap Enterprise OID: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_trapOIDField, c);
        add(m_trapOIDField);

        label = new Label("Trap Number: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_trapNumField, c);
        add(m_trapNumField);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
 
        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        // VarBind OID and Value edit fields and Type check boxes plus buttons
        c.gridwidth = 1;
        label = new Label("VarBind OID: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_varbindOIDField, c);
        add(m_varbindOIDField);

        label = new Label("VarBind Value: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_varbindValueField, c);
        add(m_varbindValueField);

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
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_OCTSTRCheckBox, c);
        add(m_ASN_OCTSTRCheckBox);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
       
        c.gridwidth = 1;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_startSendTrap, c);
        add(m_startSendTrap);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_NULLCheckBox, c);
        add(m_ASN_NULLCheckBox);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = 1;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_OIDCheckBox, c);
        add(m_ASN_OIDCheckBox);
      
        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = 1;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_IPADDRESSCheckBox, c);
        add(m_ASN_IPADDRESSCheckBox);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);        
        
        c.gridwidth = 1;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_COUNTERCheckBox, c);
        add(m_ASN_COUNTERCheckBox);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);        
        
        c.gridwidth = 1;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_GAUGECheckBox, c);
        add(m_ASN_GAUGECheckBox);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);        
        
        c.gridwidth = 1;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_TIMETICKSCheckBox, c);
        add(m_ASN_TIMETICKSCheckBox);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);        
        
        c.gridwidth = 1;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_ASN_OPAQUECheckBox, c);
        add(m_ASN_OPAQUECheckBox);

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
        c.weighty = 0.5;
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


    // -- ItemListener interface
    public synchronized void itemStateChanged(ItemEvent event) 
    {
        int iStateChange = event.getStateChange();
        
        if (event.getItemSelectable() == m_ASN_INTEGERCheckBox
            && iStateChange == event.SELECTED)
        {
            m_bVarbindType = SnmpConstants.ASN_INTEGER;
        }
        else if (event.getItemSelectable() == m_ASN_OCTSTRCheckBox
            && iStateChange == event.SELECTED)
        {
            m_bVarbindType = SnmpConstants.ASN_OCTSTR;
        }
        else if (event.getItemSelectable() == m_ASN_NULLCheckBox
            && iStateChange == event.SELECTED)
        {
            m_bVarbindType = SnmpConstants.ASN_NULL;
        }
        else if (event.getItemSelectable() == m_ASN_OIDCheckBox
            && iStateChange == event.SELECTED)
        {
            m_bVarbindType = SnmpConstants.ASN_OID;
        }
        else if (event.getItemSelectable() == m_ASN_IPADDRESSCheckBox
            && iStateChange == event.SELECTED)
        {
            m_bVarbindType = SnmpConstants.ASN_IPADDRESS;
        }
        else if (event.getItemSelectable() == m_ASN_COUNTERCheckBox
            && iStateChange == event.SELECTED)
        {
            m_bVarbindType = SnmpConstants.ASN_COUNTER;
        }
        else if (event.getItemSelectable() == m_ASN_GAUGECheckBox
            && iStateChange == event.SELECTED)
        {
            m_bVarbindType = SnmpConstants.ASN_GAUGE;
        }
        else if (event.getItemSelectable() == m_ASN_TIMETICKSCheckBox
            && iStateChange == event.SELECTED)
        {
            m_bVarbindType = SnmpConstants.ASN_TIMETICKS;
        }
        else if (event.getItemSelectable() == m_ASN_OPAQUECheckBox
            && iStateChange == event.SELECTED)
        {
            m_bVarbindType = SnmpConstants.ASN_OPAQUE;
        }
    }
    
    
    // -- ActionListener interface
    
    public synchronized void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == m_startSendTrap)
        {
            // Upon a "start signal", clear the display, grab the
            // managed host from the UI, initialize the walk variables,
            // and start the run thread.
            try
            {
                m_CommandOutput.setText("");
                if (m_destHostField.getText().length() <= 0)
                {
                    System.out.println("Empty host");
                    m_CommandOutput.setText("Empty host");
                }
                else if (m_communityField.getText().length() <= 0)
                {
                    System.out.println("Empty community");
                    m_CommandOutput.setText("Empty community");
                }
                else if (m_trapOIDField.getText().length() <= 0)
                {
                    System.out.println("Empty enterprise OID");
                    m_CommandOutput.setText("Empty enterprise OID");
                }
                else if (m_trapNumField.getText().length() <= 0
                    && event.getSource() == m_startSendTrap)
                {
                    System.out.println("Empty trap number");
                    m_CommandOutput.setText("Empty trap number");
                }
                else
                {
                    m_szManagedHost = m_destHostField.getText();
                    InetAddress cSrcHostInetAddress = null;
                    if (m_destHostField.getText().length() > 0)
                    {
                        try
                        {
                            cSrcHostInetAddress = java.net.InetAddress.getByName(m_srcHostField.getText());
                        }
                        catch (UnknownHostException uhe)
                        {
                        }
                    }
                    
                    Long lTimeTicks = null;
                    if (m_timeticksField.getText().length() > 0)
                    {
                        lTimeTicks = new Long(m_timeticksField.getText());
                    }
                    
                    String szCommunity = m_communityField.getText();
                    m_orderinfo = new SnmpOrderInfo(2, 3, 0);
                    CSMSecurityInfo secInfo;
                    try
                    {
                        secInfo = new CSMSecurityInfo(szCommunity.getBytes("ASCII"), szCommunity.getBytes("ASCII"));
                    }
                    catch(UnsupportedEncodingException uee)
                    {
                        secInfo = new CSMSecurityInfo(szCommunity.getBytes(), szCommunity.getBytes());
                    }

                    SnmpAuthoritativeSessionFactory cSnmpAuthoritativeSessionFactory = SnmpLocalInterfaces.getAuthoritativeSessionFactory();
                    m_cSnmpLocalAuthoritativeSession = cSnmpAuthoritativeSessionFactory.createLocalAuthoritativeSession(m_szManagedHost,
                                                                                    162,
                                                                                    SnmpConstants.SNMP_VERSION_1,
                                                                                    secInfo);
                    
                    String[] szVarbindOIDs = null;
                    byte[] bVarbindTypes = null;
                    Object[] varBindObjValues = null;

                    if (m_varbindOIDField.getText().length() > 0
                        && m_varbindValueField.getText().length() > 0)
                    {
                        Object obj = null;
                        switch (m_bVarbindType)
                        {
                            case SnmpConstants.ASN_NULL:
                                break;
                       
                            case SnmpConstants.ASN_IPADDRESS:
                            case SnmpConstants.ASN_OID:
                                try
                                {
                                    obj = new String(m_varbindValueField.getText());
                                }
                                catch (Exception e)
                                {
                                    System.out.println("Failed to convert \"" + m_varbindValueField.getText() + "\" to ASN_OID or ASN_IPADDRESS object value (String)");
                                    m_CommandOutput.setText("Failed to convert \"" + m_varbindValueField.getText() + "\" to ASN_OID or ASN_IPADDRESS object value (String)");
                                    return;
                                }
                                
                                break;
                        
                            case SnmpConstants.ASN_OPAQUE:
                            case SnmpConstants.ASN_OCTSTR:
                                try
                                {
                                    byte[] bArray = new byte[m_varbindValueField.getText().getBytes().length];
                                    System.arraycopy(m_varbindValueField.getText().getBytes(), 0, bArray, 0, m_varbindValueField.getText().getBytes().length);
                                    obj = bArray;
                                }
                                catch (Exception e)
                                {
                                    System.out.println("Failed to convert \"" + m_varbindValueField.getText() + "\" to ASN_OPAQUE or ASN_OCTSTR object value (byte)");
                                    m_CommandOutput.setText("Failed to convert \"" + m_varbindValueField.getText() + "\" to ASN_OPAQUE or ASN_OCTSTR object value (byte)");
                                    return;
                                }
                                
                                break;
                        
                            case SnmpConstants.ASN_COUNTER:
                            case SnmpConstants.ASN_GAUGE:
                            case SnmpConstants.ASN_TIMETICKS:
                            case SnmpConstants.ASN_UINTEGER32:
                                try
                                {
                                    obj = new Integer(m_varbindValueField.getText());
                                }
                                catch (Exception e)
                                {
                                    System.out.println("Failed to convert \"" + m_varbindValueField.getText() + "\" to ASN_COUNTER, ASN_GAUGE, ASN_TIMETICKS, or ASN_UINTEGER32 object value (Integer)");
                                    m_CommandOutput.setText("Failed to convert \"" + m_varbindValueField.getText() + "\" to ASN_COUNTER, ASN_GAUGE, ASN_TIMETICKS, or ASN_UINTEGER32 object value (Integer)");
                                    return;
                                }
                                
                                break;

                            case SnmpConstants.ASN_INTEGER:
                            case SnmpConstants.ASN_COUNTER64:
                                try
                                {
                                    obj = new BigInteger(m_varbindValueField.getText());
                                }
                                catch (Exception e)
                                {
                                    System.out.println("Failed to convert \"" + m_varbindValueField.getText() + "\" to ASN_INTEGER or ASN_COUNTER64 object value (BigInteger)");
                                    m_CommandOutput.setText("Failed to convert \"" + m_varbindValueField.getText() + "\" to ASN_INTEGER or ASN_COUNTER64 object value (BigInteger)");
                                    return;
                                }
                                
                                break;
                        }

                        szVarbindOIDs = new String[1];
                        szVarbindOIDs[0] = new String(m_varbindOIDField.getText());
                    
                        bVarbindTypes = new byte[1];
                        bVarbindTypes[0] = m_bVarbindType;
                        varBindObjValues = new Object[1];
                        varBindObjValues[0] = obj;
                    }
                    
                    boolean bVal = m_service.placeV1TrapOrder(m_cSnmpLocalAuthoritativeSession,
                                                              cSrcHostInetAddress,
                                                              lTimeTicks,
                                                              m_trapOIDField.getText(),
                                                              new Long(m_trapNumField.getText()).intValue(),
                                                              szVarbindOIDs,
                                                              bVarbindTypes,
                                                              varBindObjValues);
                
                    if (!bVal)
                    {
                        System.out.println("placeTrapOrder() failed");
                        m_CommandOutput.setText("placeTrapOrder() failed");
                    }
                    else
                    {
                        System.out.println("placeTrapOrder() succeeded");
                        m_CommandOutput.setText("placeTrapOrder() succeeded");
                    }
                }
            }
            catch(UnknownHostException uhe)
            {
                String szOutString = new String("Unknown host: " + m_szManagedHost);
                System.out.println(szOutString);
                m_CommandOutput.setText(szOutString);
                m_startSendTrap.setEnabled(true);
            }
            catch(SnmpSecurityException sse)
            {
                System.out.println("Failed to create CSMSecurityInfo");
                m_CommandOutput.setText("Failed to create CSMSecurityInfo");
                m_startSendTrap.setEnabled(true);
            }
            catch (Exception e)
            {
                System.out.println(e.toString());
                m_CommandOutput.setText(e.toString());
                m_startSendTrap.setEnabled(true);
            }
        }
    }
}

