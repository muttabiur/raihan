package OcsSNMPv1;


/**
* OcsSNMPv1/NoSuchElement.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from OcsSNMPv1.idl
* Monday, January 26, 2009 4:24:09 o'clock PM EST
*/

public final class NoSuchElement extends org.omg.CORBA.UserException
{

  public NoSuchElement ()
  {
    super(NoSuchElementHelper.id());
  } // ctor


  public NoSuchElement (String $reason)
  {
    super(NoSuchElementHelper.id() + "  " + $reason);
  } // ctor

} // class NoSuchElement