package OcsSNMPv1;


/**
* OcsSNMPv1/ASNValueSeqHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from OcsSNMPv1.idl
* Monday, January 26, 2009 4:24:09 o'clock PM EST
*/

public final class ASNValueSeqHolder implements org.omg.CORBA.portable.Streamable
{
  public OcsSNMPv1.ASNValue value[] = null;

  public ASNValueSeqHolder ()
  {
  }

  public ASNValueSeqHolder (OcsSNMPv1.ASNValue[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = OcsSNMPv1.ASNValueSeqHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    OcsSNMPv1.ASNValueSeqHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return OcsSNMPv1.ASNValueSeqHelper.type ();
  }

}