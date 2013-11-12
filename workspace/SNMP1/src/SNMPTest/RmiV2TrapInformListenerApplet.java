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
import java.util.*;
import java.io.UnsupportedEncodingException;

import com.outbackinc.services.protocol.snmp.*;
import com.outbackinc.services.protocol.snmp.CSM.*;
import com.outbackinc.services.protocol.snmp.rmi.*;

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
 *   &lt;applet code="RmiSnmpV2TrapInformListenerApplet" height=400 width=500>
 *   &lt;/applet>
 *   &lt;/body>&lt;/html>
 * </pre>
 * <p>
 *
 * Note: The CLASSPATH must be initialized correctly before
 * starting the browser.
 */
public class RmiV2TrapInformListenerApplet
extends Applet
implements SnmpTrapListener, ActionListener
{
    // ------------------------------ Constants ---------------------------------

    // ------------------------------ Class variables ---------------------------

    // ------------------------------ Member (instance) variables ---------------

    private RMISnmpClient                           m_rmiClient = null;
    private SnmpAuthoritativeSessionFactory         m_sessionFactory = null;
    private SnmpService                             m_service = null;
    //private SnmpServiceConfiguration                m_serviceConfiguration;
    private SnmpTrapProfile                         m_trapInformProfile = null;

    private TextArea                                m_textArea = null;
    private TextArea                                m_trapOutput = null;

    private SnmpAuthoritativeSession                m_trapSession;
    private String                                  m_szRMIHost = null;
    private String                                  m_szTrapHost = null;
    private Button                                  m_addTrapHost = null;
    private Button                                  m_removeTrapHost = null;
    private Button                                  m_listTrapHosts = null;
    private TextField                               m_trapHostField = null;

    private SnmpAuthoritativeSession                m_informSession;
    private String                                  m_szInformHost = null;
    private Button                                  m_rmiHostBtn = null;
    private Button                                  m_addInformHost = null;
    private Button                                  m_removeInformHost = null;
    private Button                                  m_listInformHosts = null;
    private TextField                               m_informHostField = null;

    private TextField                               m_rmiHostField = null;
    private TextField                               m_OIDField = null;
    private TextField                               m_trapNumField = null;
    private Button                                  m_addOID = null;
    private Button                                  m_removeOID = null;
    private Button                                  m_removeAllOIDs = null;
    private Button                                  m_listOIDs = null;
    
    // ------------------------------ Methods -----------------------------------

    /**
     *
     */
    public RmiV2TrapInformListenerApplet()
    {
    }


    /**
     *
     */
    public void init()
    {
        // Create the ui...
        initUI();
    }

    private void initUI()
    {
        setBackground(Color.lightGray);

        m_rmiHostField = new TextField(10);
        m_trapHostField = new TextField(10);
        m_addTrapHost = new Button("Add");
        m_removeTrapHost = new Button("Remove");
        m_listTrapHosts = new Button("List");

        m_informHostField = new TextField(10);
        m_addInformHost = new Button("Add");
        m_removeInformHost = new Button("Remove");
        m_listInformHosts = new Button("List");

        m_OIDField = new TextField(10);
        m_trapNumField = new TextField(5);
        m_rmiHostBtn = new Button("Connect");
        m_addOID = new Button("Add");
        m_removeOID = new Button("Remove");
        m_removeAllOIDs = new Button("Remove All");
        m_listOIDs = new Button("List");
    
        m_rmiHostBtn.addActionListener(this);
        m_trapHostField.addActionListener(this);
        m_addTrapHost.addActionListener(this);
        m_removeTrapHost.addActionListener(this);
        m_listTrapHosts.addActionListener(this);
        
        m_informHostField.addActionListener(this);
        m_addInformHost.addActionListener(this);
        m_removeInformHost.addActionListener(this);
        m_listInformHosts.addActionListener(this);

        m_OIDField.addActionListener(this);
        m_trapNumField.addActionListener(this);
        m_addOID.addActionListener(this);
        m_removeOID.addActionListener(this);
        m_removeAllOIDs.addActionListener(this);
        m_listOIDs.addActionListener(this);

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

        label = new Label("jSNMP RMI Server Host: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);
       
        gridbag.setConstraints(m_rmiHostField, c);
        add(m_rmiHostField);
        
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_rmiHostBtn, c);
        add(m_rmiHostBtn);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = 1;

        label = new Label("SNMP Trap Host: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_trapHostField, c);
        add(m_trapHostField);
        
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_addTrapHost, c);
        add(m_addTrapHost);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_removeTrapHost, c);
        add(m_removeTrapHost);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_listTrapHosts, c);
        add(m_listTrapHosts);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = 1;

        label = new Label("SNMP Inform Host: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_informHostField, c);
        add(m_informHostField);
        
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_addInformHost, c);
        add(m_addInformHost);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_removeInformHost, c);
        add(m_removeInformHost);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_listInformHosts, c);
        add(m_listInformHosts);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = 1;
        label = new Label("Enterprise OID: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_OIDField, c);
        add(m_OIDField);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = 1;
        label = new Label("Trap Number: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_trapNumField, c);
        add(m_trapNumField);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_addOID, c);
        add(m_addOID);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_removeOID, c);
        add(m_removeOID);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_removeAllOIDs, c);
        add(m_removeAllOIDs);

        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_listOIDs, c);
        add(m_listOIDs);

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
        m_trapOutput = new TextArea();
        gridbag.setConstraints(m_trapOutput, c);
        add(m_trapOutput);

        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weighty = 1;
        m_textArea = new TextArea();
        gridbag.setConstraints(m_textArea, c);
        add(m_textArea);
        
        m_addTrapHost.setEnabled(true);
        m_removeTrapHost.setEnabled(false);
        m_listTrapHosts.setEnabled(false);
        m_addOID.setEnabled(true);
        m_removeOID.setEnabled(false);
        m_removeAllOIDs.setEnabled(false);
        m_listOIDs.setEnabled(false);
        m_addInformHost.setEnabled(true);
        m_removeInformHost.setEnabled(false);
        m_listInformHosts.setEnabled(false);
    }


    private void initService() 
    {
        String rmiHost = null;
        try
        {
            rmiHost = m_rmiHostField.getText();
            if (rmiHost.length() > 0)
            {
                m_rmiClient = new RMISnmpClient(rmiHost);
                m_service = m_rmiClient.getService();
                m_sessionFactory = m_rmiClient.getAuthoritativeSessionFactory();
                
                m_trapInformProfile = m_rmiClient.getTrapProfileFactory().createSnmpTrapProfile(162);
                m_service.addTrapListenerProfile(this, m_trapInformProfile);
                    
                m_trapOutput.setText("");
                if (m_szRMIHost != null)
                {
                    System.out.println("Disconnected from jSNMP RMI Server " + m_szRMIHost + "; connected to jSNMP RMI Server " + rmiHost);
                    updateText("Disconnected from jSNMP RMI Server " + m_szRMIHost + "; connected to jSNMP RMI Server " + rmiHost);
                }
                else
                {
                    updateText("Connected to jSNMP RMI Server " + rmiHost);
                    System.out.println("Connected to jSNMP RMI Server " + rmiHost);
                }
                    
                m_szRMIHost = new String(rmiHost);
            }
            else
            {
                m_trapOutput.setText("");
                if (m_szRMIHost != null)
                {
                    updateText("Empty RMI Server Host; disconnected from jSNMP RMI Server " + m_szRMIHost);
                    System.out.println("Empty RMI Server Host; disconnected from jSNMP RMI Server " + m_szRMIHost);
                }
                else
                {
                    updateText("Empty RMI Server Host");
                    System.out.println("Empty RMI Server Host");
                }
                    
                m_szRMIHost = null;
                m_rmiClient = null;
                m_service = null;
                m_sessionFactory = null;
                m_trapInformProfile = null;
            }
        }
        catch (Exception e)
        {
            m_szRMIHost = null;
            m_rmiClient = null;
            m_service = null;
            m_sessionFactory = null;
            m_trapInformProfile = null;
            System.out.println("Couldn't connect to jSNMP RMI Server " + rmiHost + " (" + e.toString() + ")");
            updateText("Couldn't connect to jSNMP RMI Server " + rmiHost + " (" + e.toString() + ")");
            e.printStackTrace();
        }
        catch (Error er)
        {
            m_szRMIHost = null;
            m_rmiClient = null;
            m_service = null;
            m_sessionFactory = null;
            m_trapInformProfile = null;
            System.out.println("Couldn't connect to jSNMP RMI Server " + rmiHost + " (" + er.toString() + ")");
            updateText("Couldn't connect to jSNMP RMI Server " + rmiHost + " (" + er.toString() + ")");
            er.printStackTrace();
        }
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
        
        if (m_trapOutput.getText().length() > 5000)
        {
            m_trapOutput.setText("");
        }

        // Append the new string to the existing text...

        m_trapOutput.append(s + "\n");
    }

    // -- ActionListener interface
    
    public synchronized void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == m_rmiHostBtn)
        {
            initService();
        }
        else if (event.getSource() == m_addTrapHost)
        {
            // Upon a "start signal", clear the display, grab the
            // managed host from the UI, initialize the walk variables,
            // and start the run thread.
        
            try
            {
                m_szTrapHost = m_trapHostField.getText();
                if (m_szTrapHost.length() <= 0)
                {
                    System.out.println("Empty host");
                    updateText("Empty host");
                }
                else
                {
                    String szCommunity = new String("public");
                    CSMSecurityInfo secInfo;
                    try
                    {
                        secInfo = new CSMSecurityInfo(szCommunity.getBytes("ASCII"), szCommunity.getBytes("ASCII"));
                    }
                    catch(UnsupportedEncodingException uee)
                    {
                        secInfo = new CSMSecurityInfo(szCommunity.getBytes(), szCommunity.getBytes());
                    }

                    m_trapSession = m_sessionFactory.createRemoteAuthoritativeSession(m_szTrapHost,
                                                                                      162,
                                                                                      SnmpConstants.SNMP_VERSION_2,
                                                                                      secInfo);
            
                    if (!m_trapInformProfile.addTrapSession(m_trapSession))
                    {
                        String szOutString = new String("Already listening for traps from host: " + m_szTrapHost);
                        System.out.println(szOutString);
                        updateText(szOutString);
                    }
                    else
                    {
                        String szOutString = new String("Added host:" + m_szTrapHost);
                        System.out.println(szOutString);
                        updateText(szOutString);
                        m_removeTrapHost.setEnabled(true);
                        m_listTrapHosts.setEnabled(true);
                    }
                }
            }
            catch(UnknownHostException uhe)
            {
                String szOutString = new String("Unknown host: " + m_szTrapHost);
                System.out.println(szOutString);
                updateText(szOutString);
            }
            catch(SnmpSecurityException sse)
            {
                System.out.println("Failed to create CSMSecurityInfo");
                updateText("Failed to create CSMSecurityInfo");
            }
        }
        else if (event.getSource() == m_removeTrapHost)
        {
            try
            {
                m_szTrapHost = m_trapHostField.getText();
                if (m_szTrapHost.length() <= 0)
                {
                    System.out.println("Empty host");
                    updateText("Empty host");
                }
                else
                {
                    String szCommunity = new String("public");
                    CSMSecurityInfo secInfo;
                    try
                    {
                        secInfo = new CSMSecurityInfo(szCommunity.getBytes("ASCII"), szCommunity.getBytes("ASCII"));
                    }
                    catch(UnsupportedEncodingException uee)
                    {
                        secInfo = new CSMSecurityInfo(szCommunity.getBytes(), szCommunity.getBytes());
                    }

                    m_trapSession = m_sessionFactory.createRemoteAuthoritativeSession(m_szTrapHost,
                                                                                      162,
                                                                                      SnmpConstants.SNMP_VERSION_2,
                                                                                      secInfo);
            
                    if (m_trapInformProfile.removeTrapSession(m_trapSession))
                    {
                        m_removeTrapHost.setEnabled(false);
                        m_listTrapHosts.setEnabled(false);
                    }

                    String szOutString = new String("Removed host: " + m_szTrapHost);
                    System.out.println(szOutString);
                    updateText(szOutString);
                }
            }
            catch(UnknownHostException uhe)
            {
                String szOutString = new String("Unknown host: " + m_szTrapHost);
                System.out.println(szOutString);
                updateText(szOutString);
            }
            catch(SnmpSecurityException sse)
            {
                System.out.println("Failed to create CSMSecurityInfo");
                updateText("Failed to create CSMSecurityInfo");
            }
            catch (NoSuchElementException nsee)
            {
                String szOutString = new String("Were not listening for traps from host: " + m_szTrapHost);
                System.out.println(szOutString);
                updateText(szOutString);
            }
        }
        else if (event.getSource() == m_listTrapHosts)
        {
            SnmpAuthoritativeSession[] sessions = m_trapInformProfile.listTrapSessions();
            if (sessions != null)
            {
                System.out.println("Waiting for traps from the following hosts:");
                updateText("Waiting for traps from the following hosts:");
                
                for (int i = 0; i < sessions.length; i++)
                {
                    String szOutString = new String("\t" + sessions[i].getAgentHostIPAddress());
                    System.out.println(szOutString);
                    updateText(szOutString);
                }
            }
            else
            {
                System.out.println("Not waiting for traps from any specific hosts");
                updateText("Not waiting for traps from any specific hosts");
            }
        }
        else if (event.getSource() == m_addInformHost)
        {
            // Upon a "start signal", clear the display, grab the
            // managed host from the UI, initialize the walk variables,
            // and start the run thread.
        
            try
            {
                m_szInformHost = m_informHostField.getText();
                if (m_szInformHost.length() <= 0)
                {
                    System.out.println("Empty host");
                    updateText("Empty host");
                }
                else
                {
                    String szCommunity = new String("public");
                    CSMSecurityInfo secInfo;
                    try
                    {
                        secInfo = new CSMSecurityInfo(szCommunity.getBytes("ASCII"), szCommunity.getBytes("ASCII"));
                    }
                    catch(UnsupportedEncodingException uee)
                    {
                        secInfo = new CSMSecurityInfo(szCommunity.getBytes(), szCommunity.getBytes());
                    }

                    m_informSession = m_sessionFactory.createLocalAuthoritativeSession(m_szInformHost,
                                                                                       162,
                                                                                       SnmpConstants.SNMP_VERSION_2,
                                                                                       secInfo);
            
                    if (!m_trapInformProfile.addInformSession(m_informSession))
                    {
                        String szOutString = new String("Already listening for informs from host: " + m_szInformHost);
                        System.out.println(szOutString);
                        updateText(szOutString);
                    }
                    else
                    {
                        String szOutString = new String("Added host:" + m_szInformHost);
                        System.out.println(szOutString);
                        updateText(szOutString);
                        m_removeInformHost.setEnabled(true);
                        m_listInformHosts.setEnabled(true);
                    }
                }
            }
            catch(UnknownHostException uhe)
            {
                String szOutString = new String("Unknown host: " + m_szInformHost);
                System.out.println(szOutString);
                updateText(szOutString);
            }
            catch(SnmpSecurityException sse)
            {
                System.out.println("Failed to create CSMSecurityInfo");
                updateText("Failed to create CSMSecurityInfo");
            }
        }
        else if (event.getSource() == m_removeInformHost)
        {
            try
            {
                m_szInformHost = m_informHostField.getText();
                if (m_szInformHost.length() <= 0)
                {
                    System.out.println("Empty host");
                    updateText("Empty host");
                }
                else
                {
                    String szCommunity = new String("public");
                    CSMSecurityInfo secInfo;
                    try
                    {
                        secInfo = new CSMSecurityInfo(szCommunity.getBytes("ASCII"), szCommunity.getBytes("ASCII"));
                    }
                    catch(UnsupportedEncodingException uee)
                    {
                        secInfo = new CSMSecurityInfo(szCommunity.getBytes(), szCommunity.getBytes());
                    }

                    m_informSession = m_sessionFactory.createLocalAuthoritativeSession(m_szInformHost,
                                                                                       162,
                                                                                       SnmpConstants.SNMP_VERSION_2,
                                                                                       secInfo);
            
                    if (m_trapInformProfile.removeInformSession(m_informSession))
                    {
                        m_removeInformHost.setEnabled(false);
                        m_listInformHosts.setEnabled(false);
                    }

                    String szOutString = new String("Removed host: " + m_szInformHost);
                    System.out.println(szOutString);
                    updateText(szOutString);
                }
            }
            catch(UnknownHostException uhe)
            {
                String szOutString = new String("Unknown host: " + m_szInformHost);
                System.out.println(szOutString);
                updateText(szOutString);
            }
            catch(SnmpSecurityException sse)
            {
                System.out.println("Failed to create CSMSecurityInfo");
                updateText("Failed to create CSMSecurityInfo");
            }
            catch (NoSuchElementException nsee)
            {
                String szOutString = new String("Were not listening for informs from host: " + m_szInformHost);
                System.out.println(szOutString);
                updateText(szOutString);
            }
        }
        else if (event.getSource() == m_listInformHosts)
        {
            SnmpAuthoritativeSession[] sessions = m_trapInformProfile.listInformSessions();
            if (sessions != null)
            {
                System.out.println("Waiting for informs from the following hosts:");
                updateText("Waiting for informs from the following hosts:");
                
                for (int i = 0; i < sessions.length; i++)
                {
                    String szOutString = new String("\t" + sessions[i].getAgentHostIPAddress());
                    System.out.println(szOutString);
                    updateText(szOutString);
                }
            }
            else
            {
                System.out.println("Not waiting for informs from any specific hosts");
                updateText("Not waiting for informs from any specific hosts");
            }
        }
        else if (event.getSource() == m_addOID)
        {
            if (m_OIDField.getText().length() <= 0)
            {
                System.out.println("Empty enterprise OID");
                updateText("Empty enterprise OID");
            }
            else if (m_trapNumField.getText().length() <= 0)
            {
                System.out.println("Empty trap number");
                updateText("Empty trap number");
            }
            else
            {
                try
                {
                    if (!m_trapInformProfile.addTrapInform(m_OIDField.getText(), new Integer(m_trapNumField.getText())))
                    {
                        String szOutString = new String("Already listening for trap:" + m_OIDField.getText() + "(" + m_trapNumField.getText() + ")");
                        System.out.println(szOutString);
                        updateText(szOutString);
                    }
                    else
                    {
                        String szOutString = new String("Added trap: " + m_OIDField.getText() + "(" + m_trapNumField.getText() + ")");
                        System.out.println(szOutString);
                        updateText(szOutString);
                        m_removeOID.setEnabled(true);
                        m_removeAllOIDs.setEnabled(true);
                        m_listOIDs.setEnabled(true);
                    }
                }
                catch (IllegalArgumentException iae)
                {
                    String szOutString = new String("Unable to add trap: " + m_OIDField.getText() + "(" + m_trapNumField.getText() + ") ... illegal OID?");
                    System.out.println(szOutString);
                    updateText(szOutString);
                }
            }
        }
        else if (event.getSource() == m_listOIDs)
        {
            String[] szEnterpriseOIDs = m_trapInformProfile.listTrapInformOIDs();
            if (szEnterpriseOIDs != null)
            {
                System.out.println("Waiting for traps with the following enterprise OIDs:");
                updateText("Waiting for traps with the following enterprise OIDs:");
                for (int i = 0; i < szEnterpriseOIDs.length; i++)
                {
                    StringBuffer szbOutString = new StringBuffer("\t" + szEnterpriseOIDs[i] + " (");
                    Integer[] igrTrapInformTypes = m_trapInformProfile.listTrapInformTypes(szEnterpriseOIDs[i]);
                    if (igrTrapInformTypes != null)
                    {
                        for (int j = 0; j < igrTrapInformTypes.length; j++)
                        {
                            if (j != 0)
                            {
                                szbOutString.append(", ");
                            }
                            
                            szbOutString.append(igrTrapInformTypes[j]);
                        }
                    }
                    
                    szbOutString.append(")");
                    System.out.println(szbOutString.toString());
                    updateText(szbOutString.toString());
                }
            }
            else
            {
                System.out.println("Not waiting for traps with specific OIDs.");
                updateText("Not waiting for traps with specific OIDs.");
            }
        }
        else if (event.getSource() == m_removeAllOIDs)
        {
            m_removeAllOIDs.setEnabled(false);
            m_removeOID.setEnabled(false);
            m_listOIDs.setEnabled(false);
            m_trapInformProfile.removeTrapsInforms();
        }
        else if (event.getSource() == m_removeOID)
        {
            try
            {
                if (m_trapInformProfile.removeTrapInform(m_OIDField.getText(), new Integer(m_trapNumField.getText())))
                {
                    m_listOIDs.setEnabled(false);
                    m_removeAllOIDs.setEnabled(false);
                    m_removeOID.setEnabled(false);
                }
                
                String szOutString = new String("Removed trap:" + m_OIDField.getText() + "(" + m_trapNumField.getText() + ")");
                System.out.println(szOutString);
                updateText(szOutString);
            }
            catch (NoSuchElementException nsee)
            {
                String szOutString = new String("Were not listening for trap: " + m_OIDField.getText() + "(" + m_trapNumField.getText() + ")");
                System.out.println(szOutString);
                updateText(szOutString);
            }
        }
    }
    
    //SnmpTrapListener Implementation

    /**
     * callback for receiving traps.  This method must be implemented by a class
     * wishing for trap notification
     *
     * @param trap the received trap
     */
    public void trapReceived(SnmpTrapEvent trap)
    {
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
            szTrapType = new String("an unexpected type (" + trap.getType() + " is not a SNMPv2 trap or inform)");
        }
                 
        String szOutString = new String("\nReceived " + szTrapType + " ...\n"
                                        + "\tPort : " + trap.getPort()
                                        + "\n\tGenerating Agent : " + trap.getAgentIPAddress()
                                        + "\n\tSending Agent : " + trap.getSendersIPAddress()
                                        + "\n\tTime Stamp : " + trap.getTimeStamp()
                                        + "\n\tEnterprise OID : " + trap.getEnterpriseOID()
                                        + "\n\tTrap Type : " + trap.getTrapType());
        updateTrapText(szOutString);
        System.out.println(szOutString);
        if (trap.getNumberOfVarBinds() > 0)
        {
            updateTrapText("\tVarBinds:");
            System.out.println("\tVarBinds:");
        }
        
        for (int i = 0; i < trap.getNumberOfVarBinds(); i++)
        {
            SnmpVarBind vb = trap.getVarBind(i);
            szOutString = new String("\t\t" + vb.getName() + " (" + vb.getStringValue() + ")");
            updateTrapText(szOutString);
            System.out.println(szOutString);
        }
    }
}

