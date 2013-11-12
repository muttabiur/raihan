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
import java.util.*;
import java.io.*;

import com.outbackinc.services.protocol.snmp.*;

/**
 * Sample which illustrates how to receive traps.  To run the sample
 * execute "java SnmpV1V2V3TrapListenerTest".  By default this example
 * registers a trap listener to receive ALL traps and informs coming in on
 * port 162.  When each trap is received, it prints out its information.
 */
public class SnmpV1V2V3TrapListenerTest
    implements SnmpTrapListener
{
    // ------------------------------ Constants ---------------------------------

    // ------------------------------ Class variables ---------------------------

    // ------------------------------ Member (instance) variables ---------------
    private Properties     m_props = null;

    // ------------------------------ Methods -----------------------------------

    public SnmpV1V2V3TrapListenerTest()
    {
        SnmpService cSnmpService;
        try 
        {
            cSnmpService = SnmpLocalInterfaces.getService();
        }
        catch (SocketException se)
        {
            System.out.println("Unable to initialize the Snmp service (" + se.getMessage() + ").");
            return;
        }
            
        SnmpServiceConfiguration cSnmpServiceConfiguration = SnmpLocalInterfaces.getServiceConfiguration(cSnmpService);
        cSnmpServiceConfiguration.setBufferDelay(0);
        SnmpTrapProfile cSnmpTrapProfile = SnmpLocalInterfaces.getTrapProfileFactory().createSnmpTrapProfile(162);
        try
        {
            cSnmpService.addTrapListenerProfile(this, cSnmpTrapProfile);
        }
        catch (SocketException se)
        {
            System.out.println("Unable to initialize the Snmp trap listener (" + se.getMessage() + ").");
            cSnmpService.stop();
            return;
        }
        
        //Listen for incoming traps
        System.out.println("Listening for traps and informs ...");
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
            szTrapType = new String("an unexpected type (" + trap.getType() + " is not a SNMPv1 trap)");
        }
                 
        String szOutString = new String("\nReceived " + szTrapType + " ...\n"
                                        + "\tPort : " + trap.getPort()
                                        + "\n\tGenerating Agent : " + trap.getAgentIPAddress()
                                        + "\n\tSending Agent : " + trap.getSendersIPAddress()
                                        + "\n\tTime Stamp : " + trap.getTimeStamp()
                                        + "\n\tEnterprise OID : " + trap.getEnterpriseOID()
                                        + "\n\tTrap Type : " + trap.getTrapType());
        System.out.println(szOutString);
        if (trap.getNumberOfVarBinds() > 0)
        {
            System.out.println("\tVarBinds:");
        }
        
        for (int i = 0; i < trap.getNumberOfVarBinds(); i++)
        {
            SnmpVarBind vb = trap.getVarBind(i);
            szOutString = new String("\t\t" + vb.getName() + " (" + vb.getStringValue() + ")");
            System.out.println(szOutString);
        }
    }


    /**
     * Main method to start execution
     *
     * @param args contains cmdline args, first of which must be the host
     *
     */
    public static void main(String[] args)
    {
        try
        {
            //Create a new instance of a SnmpTrapListenerTest
            SnmpV1V2V3TrapListenerTest test = new SnmpV1V2V3TrapListenerTest();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

