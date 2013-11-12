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
 *   &lt;applet code="SnmpV2GetBulkApplet" height=400 width=500>
 *   &lt;/applet>
 *   &lt;/body>&lt;/html>
 * </pre>
 * <p>
 *
 * Note: The CLASSPATH must be initialized correctly before
 * starting the browser.
 */
public class RmiV2GetBulkApplet
extends Applet
implements SnmpCustomer, ActionListener
{
    // ------------------------------ Constants ---------------------------------

    // ------------------------------ Class variables ---------------------------

    // ------------------------------ Member (instance) variables ---------------

    private RMISnmpClient                           m_cRMISnmpClient = null;
    private SnmpAuthoritativeSessionFactory         m_cSnmpAuthoritativeSessionFactory = null;
    private SnmpAuthoritativeSession                m_cSnmpAuthoritativeSession;
    private SnmpService                             m_cSnmpService;
    //private SnmpServiceConfiguration                m_serviceConfiguration;
    private int                                     m_iNumRetrieved;
    private SnmpOrderInfo                           m_cSnmpOrderInfo;
    private int                                     m_iOrderNumStart = 1;
    private String                                  m_szRMIHost = null;
    private String                                  m_szManagedHost = null;


    private TextArea                                m_CommandOutput = null;
    private TextArea                                m_responseOutput = null;

    private Button                                  m_start = null;
    private TextField                               m_rmiHostField = null;
    private TextField                               m_hostField = null;
    private TextField                               m_communityField = null;
    private TextField                               m_nonRepeaterOIDField = null;
    private TextField                               m_repeaterOIDField = null;
    private TextField                               m_repetitions = null;
    
    
    // ------------------------------ Methods -----------------------------------

    /**
     *
     */
    public RmiV2GetBulkApplet()
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
        m_hostField = new TextField(10);
        m_communityField = new TextField(10);
        m_nonRepeaterOIDField = new TextField(10);
        m_repeaterOIDField = new TextField(10);
        m_repetitions = new TextField(5);
        
        m_start = new Button("GetBulk");

        m_rmiHostField.addActionListener(this);
        m_hostField.addActionListener(this);
        m_communityField.addActionListener(this);
        m_nonRepeaterOIDField.addActionListener(this);
        m_repeaterOIDField.addActionListener(this);
        m_repetitions.addActionListener(this);
        
        m_start.addActionListener(this);

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
        
        label = new Label("Snmp Agent Host: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_hostField, c);
        add(m_hostField);
        
        label = new Label("Read Community: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);
        
        gridbag.setConstraints(m_communityField, c);
        add(m_communityField); 

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);
    
        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = 1;
        label = new Label("Non-Repeater OID: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_nonRepeaterOIDField, c);
        add(m_nonRepeaterOIDField); 

        label = new Label("Repeater OID: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_repeaterOIDField, c); 
        add(m_repeaterOIDField); 
         
        label = new Label("Repetitions: ", Label.RIGHT);
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_repetitions, c);
        add(m_repetitions);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = 1;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        gridbag.setConstraints(m_start, c);
        add(m_start);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.gridwidth = GridBagConstraints.REMAINDER;
        label = new Label(" ");
        gridbag.setConstraints(label, c);
        add(label);

        c.weightx = GridBagConstraints.REMAINDER;
        c.weighty = 3;
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
                    
                    updateResponseOutput("Connected to jSNMP RMI Server " + rmiHost);
                    System.out.println("Connected to jSNMP RMI Server " + rmiHost);
                    
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

    // -- ActionListener interface
    
    public synchronized void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == m_start)
        {
            // Upon a "start signal", clear the display, grab the
            // managed host from the UI, initialize the walk variables,
            // and start the run thread.
            if (!initService())
            {
                return;
            }
            
            try
            {
                if (m_hostField.getText().length() <= 0)
                {
                    System.out.println("Empty host");
                    m_CommandOutput.setText("Empty host");
                }
                else if (m_communityField.getText().length() <= 0)
                {
                    System.out.println("Empty community");
                    m_CommandOutput.setText("Empty community");
                }
                else if (m_nonRepeaterOIDField.getText().length() <= 0)
                {
                    System.out.println("Empty non-repeater OID");
                    m_CommandOutput.setText("Empty non-repeater OID");
                }
                else if (m_repeaterOIDField.getText().length() <= 0)
                {
                    System.out.println("Empty repeater OID");
                    m_CommandOutput.setText("Empty repeater OID");
                }
                else if (m_repetitions.getText().length() <= 0)
                {
                    System.out.println("Empty repetitions");
                    m_CommandOutput.setText("Empty repetitions");
                }
                else
                {
                    m_szManagedHost = m_hostField.getText();
                    String szCommunity = m_communityField.getText();
                    m_cSnmpOrderInfo = new SnmpOrderInfo(2, 3, 0);
                    CSMSecurityInfo secInfo;
                    try
                    {
                        secInfo = new CSMSecurityInfo(szCommunity.getBytes("ASCII"), szCommunity.getBytes("ASCII"));
                    }
                    catch(UnsupportedEncodingException uee)
                    {
                        secInfo = new CSMSecurityInfo(szCommunity.getBytes(), szCommunity.getBytes());
                    }

                    m_cSnmpAuthoritativeSession = m_cSnmpAuthoritativeSessionFactory.createRemoteAuthoritativeSession(m_szManagedHost,
                                                                                  161,
                                                                                  SnmpConstants.SNMP_VERSION_2,
                                                                                  secInfo);
                    

                    String[] szNonRepeaterOIDs = new String[1];
                    szNonRepeaterOIDs[0] = new String(m_nonRepeaterOIDField.getText());
                    
                    String[] szRepeaterOIDs = new String[1];
                    szRepeaterOIDs[0] = new String(m_repeaterOIDField.getText());
                    
                    
                    int iMaxRepetitions = 0;
                    try
                    {
                        Integer igrVal = new Integer(m_repetitions.getText());
                        iMaxRepetitions = igrVal.intValue();
                    }
                    catch (Exception e)
                    {
                        System.out.println("Failed to convert repetitions (" + m_repetitions.getText() + ") to Integer");
                        m_CommandOutput.setText("Failed to convert repetitions (" + m_repetitions.getText() + ") to Integer");
                        return;
                    }
                    
                    m_CommandOutput.setText(m_iOrderNumStart + " get-bulk order placed: non-repeater (" + szNonRepeaterOIDs[0] + "), repeater (" + szRepeaterOIDs[0] + "), repetitions (" + iMaxRepetitions + ")");
                    
                    int iVal = m_cSnmpService.placeGetBulkOrder(m_cSnmpAuthoritativeSession,
                                                           m_cSnmpOrderInfo,
                                                           szNonRepeaterOIDs,
                                                           szRepeaterOIDs,
                                                           iMaxRepetitions,
                                                           this,
                                                           m_iOrderNumStart);
                
                    if (iVal != 0)
                    {
                        m_start.setEnabled(false);
                        m_iOrderNumStart = iVal;
                    }
                    else
                    {
                        System.out.println("placeGetBulkOrder() failed");
                        m_CommandOutput.setText("placeGetBulkOrder() failed");
                    }
                }
            }
            catch(UnknownHostException uhe)
            {
                String szOutString = new String("Unknown host: " + m_szManagedHost);
                System.out.println(szOutString);
                m_CommandOutput.setText(szOutString);
                m_start.setEnabled(true);
            }
            catch(SnmpSecurityException sse)
            {
                System.out.println("Failed to create CSMSecurityInfo");
                m_CommandOutput.setText("Failed to create CSMSecurityInfo");
                m_start.setEnabled(true);
            }
            catch (Exception e)
            {
                System.out.println(e.toString());
                m_CommandOutput.setText(e.toString());
                m_start.setEnabled(true);
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
        m_start.setEnabled(true);
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
        m_start.setEnabled(true);
    }
}

