package OcsSNMPv1;

/**
* OcsSNMPv1/DETHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from OcsSNMPv1.idl
* Monday, January 26, 2009 4:24:09 o'clock PM EST
*/

public final class DETHolder implements org.omg.CORBA.portable.Streamable
{
  public OcsSNMPv1.DET value = null;

  public DETHolder ()
  {
  }

  public DETHolder (OcsSNMPv1.DET initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = OcsSNMPv1.DETHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    OcsSNMPv1.DETHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return OcsSNMPv1.DETHelper.type ();
  }

}
