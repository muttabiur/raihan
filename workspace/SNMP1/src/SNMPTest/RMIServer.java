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
import com.outbackinc.services.protocol.snmp.rmi.*;
import java.security.*;

/**
 * Sample which illustrates a sample RMI server for jSNMP.
 */
public class RMIServer
{
    public static void main(String args[]) 
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
        
        RMISnmpServer server = new RMISnmpServer();
    } 
}
