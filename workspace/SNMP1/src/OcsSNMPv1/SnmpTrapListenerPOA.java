package OcsSNMPv1;


/**
* OcsSNMPv1/SnmpTrapListenerPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from OcsSNMPv1.idl
* Monday, January 26, 2009 4:24:09 o'clock PM EST
*/

public abstract class SnmpTrapListenerPOA extends org.omg.PortableServer.Servant
 implements OcsSNMPv1.SnmpTrapListenerOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("trapReceived", new java.lang.Integer (0));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // OcsSNMPv1/SnmpTrapListener/trapReceived
       {
         OcsSNMPv1.SnmpTrapEvent evt = OcsSNMPv1.SnmpTrapEventHelper.read (in);
         this.trapReceived (evt);
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:OcsSNMPv1/SnmpTrapListener:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public SnmpTrapListener _this() 
  {
    return SnmpTrapListenerHelper.narrow(
    super._this_object());
  }

  public SnmpTrapListener _this(org.omg.CORBA.ORB orb) 
  {
    return SnmpTrapListenerHelper.narrow(
    super._this_object(orb));
  }


} // class SnmpTrapListenerPOA
