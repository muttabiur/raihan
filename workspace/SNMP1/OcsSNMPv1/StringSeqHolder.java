package OcsSNMPv1;


/**
* OcsSNMPv1/StringSeqHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from OcsSNMPv1.idl
* Monday, January 26, 2009 4:24:09 o'clock PM EST
*/

public final class StringSeqHolder implements org.omg.CORBA.portable.Streamable
{
  public String value[] = null;

  public StringSeqHolder ()
  {
  }

  public StringSeqHolder (String[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = OcsSNMPv1.StringSeqHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    OcsSNMPv1.StringSeqHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return OcsSNMPv1.StringSeqHelper.type ();
  }

}
