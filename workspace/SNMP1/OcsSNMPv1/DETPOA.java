package OcsSNMPv1;


/**
* OcsSNMPv1/DETPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from OcsSNMPv1.idl
* Monday, January 26, 2009 4:24:09 o'clock PM EST
*/

public abstract class DETPOA extends org.omg.PortableServer.Servant
 implements OcsSNMPv1.DETOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("buyItem", new java.lang.Integer (0));
    _methods.put ("printReport", new java.lang.Integer (1));
    _methods.put ("sellItem", new java.lang.Integer (2));
    _methods.put ("tradeItems", new java.lang.Integer (3));
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
       case 0:  // OcsSNMPv1/DET/buyItem
       {
         String item = in.read_string ();
         int qty = in.read_long ();
         int $result = (int)0;
         $result = this.buyItem (item, qty);
         out = $rh.createReply();
         out.write_long ($result);
         break;
       }

       case 1:  // OcsSNMPv1/DET/printReport
       {
         int $result = (int)0;
         $result = this.printReport ();
         out = $rh.createReply();
         out.write_long ($result);
         break;
       }


  //long sellItem(int String item, in long qty,in double bal);
       case 2:  // OcsSNMPv1/DET/sellItem
       {
         int qty = in.read_long ();
         double bal = in.read_double ();
         int $result = (int)0;
         $result = this.sellItem (qty, bal);
         out = $rh.createReply();
         out.write_long ($result);
         break;
       }

       case 3:  // OcsSNMPv1/DET/tradeItems
       {
         String item1 = in.read_string ();
         int qty1 = in.read_long ();
         String item2 = in.read_string ();
         int qty2 = in.read_long ();
         String etrader = in.read_string ();
         int $result = (int)0;
         $result = this.tradeItems (item1, qty1, item2, qty2, etrader);
         out = $rh.createReply();
         out.write_long ($result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:OcsSNMPv1/DET:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public DET _this() 
  {
    return DETHelper.narrow(
    super._this_object());
  }

  public DET _this(org.omg.CORBA.ORB orb) 
  {
    return DETHelper.narrow(
    super._this_object(orb));
  }


} // class DETPOA
