package OcsSNMPv1;


/**
* OcsSNMPv1/SnmpVarBindSeqHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from OcsSNMPv1.idl
* Monday, January 26, 2009 4:24:09 o'clock PM EST
*/

abstract public class SnmpVarBindSeqHelper
{
  private static String  _id = "IDL:OcsSNMPv1/SnmpVarBindSeq:1.0";

  public static void insert (org.omg.CORBA.Any a, OcsSNMPv1.SnmpVarBind[] that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static OcsSNMPv1.SnmpVarBind[] extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = OcsSNMPv1.SnmpVarBindHelper.type ();
      __typeCode = org.omg.CORBA.ORB.init ().create_sequence_tc (0, __typeCode);
      __typeCode = org.omg.CORBA.ORB.init ().create_alias_tc (OcsSNMPv1.SnmpVarBindSeqHelper.id (), "SnmpVarBindSeq", __typeCode);
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static OcsSNMPv1.SnmpVarBind[] read (org.omg.CORBA.portable.InputStream istream)
  {
    OcsSNMPv1.SnmpVarBind value[] = null;
    int _len0 = istream.read_long ();
    value = new OcsSNMPv1.SnmpVarBind[_len0];
    for (int _o1 = 0;_o1 < value.length; ++_o1)
      value[_o1] = OcsSNMPv1.SnmpVarBindHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, OcsSNMPv1.SnmpVarBind[] value)
  {
    ostream.write_long (value.length);
    for (int _i0 = 0;_i0 < value.length; ++_i0)
      OcsSNMPv1.SnmpVarBindHelper.write (ostream, value[_i0]);
  }

}
