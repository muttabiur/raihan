package OcsSNMPv1;


/**
* OcsSNMPv1/SnmpServiceHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from OcsSNMPv1.idl
* Monday, January 26, 2009 4:24:09 o'clock PM EST
*/

abstract public class SnmpServiceHelper
{
  private static String  _id = "IDL:OcsSNMPv1/SnmpService:1.0";

  public static void insert (org.omg.CORBA.Any a, OcsSNMPv1.SnmpService that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static OcsSNMPv1.SnmpService extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (OcsSNMPv1.SnmpServiceHelper.id (), "SnmpService");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static OcsSNMPv1.SnmpService read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_SnmpServiceStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, OcsSNMPv1.SnmpService value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static OcsSNMPv1.SnmpService narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof OcsSNMPv1.SnmpService)
      return (OcsSNMPv1.SnmpService)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      OcsSNMPv1._SnmpServiceStub stub = new OcsSNMPv1._SnmpServiceStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static OcsSNMPv1.SnmpService unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof OcsSNMPv1.SnmpService)
      return (OcsSNMPv1.SnmpService)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      OcsSNMPv1._SnmpServiceStub stub = new OcsSNMPv1._SnmpServiceStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
