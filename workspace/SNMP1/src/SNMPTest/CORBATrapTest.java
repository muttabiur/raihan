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
import com.outbackinc.services.protocol.snmp.*;
import com.outbackinc.services.protocol.snmp.corba.*;
import java.io.*;
import java.net.*;

/**
 * Sample which illustrates using the remote capabilities of
 * jSNMP to construct a client which will listen to traps.
 */
public class CORBATrapTest
    implements SnmpTrapListener
{
    public static void main(String[] args)
    {
        try
        {
            if (args.length < 2)
            {
                usage();
                return;
            }

            //Read IORs
            RandomAccessFile factoryFile = new RandomAccessFile(args[0], "r");
            String szFactoryIOR = factoryFile.readLine();
            factoryFile.close();

            RandomAccessFile serviceFile = new RandomAccessFile(args[1], "r");
            String szServiceIOR = serviceFile.readLine();
            serviceFile.close();

            //Connect the CORBA communications
            CORBASnmpClient client = new CORBASnmpClient(szFactoryIOR,szServiceIOR);

            CORBATrapTest test = new CORBATrapTest();

            //Create a default trap profile which will receive all
            //traps on port 162
            SnmpTrapProfile stdProfile =
            client.getSnmpTrapProfileFactory().createSnmpTrapProfile(162);

            //Register the trap listener with the service
            client.getService().addTrapListenerProfile(test,stdProfile);

            //Listen for incoming traps
            System.out.println("Listening for traps ...");
            while (true)
            {
                Thread.currentThread().sleep(10000);
            }
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
        System.out.println("Usage : java CORBATrapTest <factoryIORfile> <serviceIORfile>");
        System.out.println("\tfactoryIORfile : file from which to read the IOR of the SnmpTrapProfileFactory");
        System.out.println("\tserviceIORfile : file from which to read the IOR of the SnmpService");
    }

    //SnmpTrapListener Implementation

    /**
     * callback for receiving traps.  This method must be implemented by a class
     * wishing for trap notification
     *
     * @param evt the received trap
     */
    public void trapReceived(SnmpTrapEvent evt)
    {
        //Prints out all the information contained in the trap
        System.out.println("Received a SNMPv1 trap ...");
        System.out.println("\tPort : " + evt.getPort());
        System.out.println("\tEnterprise OID : " + evt.getEnterpriseOID());
        System.out.println("\tGenerating Agent : " + evt.getAgentIPAddress());
        System.out.println("\tGeneric Trap Type : " + evt.getGenericTrapType());
        System.out.println("\tSpecific Trap Type : " + evt.getSpecificTrapType());
        System.out.println("\tTime Stamp : " + evt.getTimeStamp());

        //Print out the VarBind information
        System.out.println("\tVarBind information : ");
        int iNumVB = evt.getNumberOfVarBinds();
        for (int i=0; i < iNumVB; i++)
        {
            System.out.println("\t\tName : " + evt.getVarBind(i).getName());
            int iType = evt.getVarBind(i).getType();
            Object objValue = evt.getVarBind(i).getValue();

            //Demonstrates how to use the returned value
            switch (iType)
            {
            case SnmpConstants.ASN_INTEGER:
                System.out.print("\t\tType : ");
                System.out.println("Integer");
                System.out.print("\t\tValue : ");
                System.out.println(objValue);
                break;
            case SnmpConstants.ASN_OCTSTR:
                System.out.print("\t\tType : ");
                System.out.println("Octet String");
                System.out.print("\t\tValue : ");
                System.out.println(new String((byte[])objValue));
                break;
            case SnmpConstants.ASN_OPAQUE:
                System.out.print("\t\tType : ");
                System.out.println("Opaque");
                System.out.print("\t\tValue : ");
                byte[] opaque = (byte[])objValue;
                StringBuffer sbufOpaque = new StringBuffer();
                for (int j=0; j < opaque.length; j++)
                {
                    sbufOpaque.append(Integer.toHexString(opaque[j] & 0xFF));
                    sbufOpaque.append(' ');
                }
                System.out.println(sbufOpaque);
                break;
            case SnmpConstants.ASN_IPADDRESS:
                System.out.print("\t\tType : ");
                System.out.println("IP Address");
                System.out.print("\t\tValue : ");
                java.net.InetAddress address = (java.net.InetAddress)objValue;
                System.out.println(address);
                break;
            case SnmpConstants.ASN_OID:
                System.out.print("\t\tType : ");
                System.out.println("Object ID");
                System.out.print("\t\tValue : ");
                System.out.println(objValue);
                break;
            case SnmpConstants.ASN_COUNTER:
                System.out.print("\t\tType : ");
                System.out.println("Counter");
                System.out.print("\t\tValue : ");
                System.out.println(objValue);
                break;
            case SnmpConstants.ASN_GAUGE:
                System.out.print("\t\tType : ");
                System.out.println("Gauge");
                System.out.print("\t\tValue : ");
                System.out.println(objValue);
                break;
            case SnmpConstants.ASN_TIMETICKS:
                System.out.print("\t\tType : ");
                System.out.println("TimeTicks");
                System.out.print("\t\tValue : ");
                System.out.println(objValue);
                break;
            default:
                System.out.print("\t\tType : " + iType);
                System.out.print("\t\tValue : Unknown Type");
            }
            System.out.println();
        }
        System.out.println("\n");

    }
}
