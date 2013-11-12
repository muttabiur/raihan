package OcsSNMPv1;


/**
* OcsSNMPv1/_SnmpCustomerStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from OcsSNMPv1.idl
* Monday, January 26, 2009 4:24:09 o'clock PM EST
*/

public class _SnmpCustomerStub extends org.omg.CORBA.portable.ObjectImpl implements OcsSNMPv1.SnmpCustomer
{

  public void deliverSuccessfulOrder (int orderNum, OcsSNMPv1.SnmpVarBind vb)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("deliverSuccessfulOrder", true);
                $out.write_long (orderNum);
                OcsSNMPv1.SnmpVarBindHelper.write ($out, vb);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                deliverSuccessfulOrder (orderNum, vb        );
            } finally {
                _releaseReply ($in);
            }
  } // deliverSuccessfulOrder

  public void deliverFailedOrder (int orderNum, int errorStatus)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("deliverFailedOrder", true);
                $out.write_long (orderNum);
                $out.write_ulong (errorStatus);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                deliverFailedOrder (orderNum, errorStatus        );
            } finally {
                _releaseReply ($in);
            }
  } // deliverFailedOrder

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:OcsSNMPv1/SnmpCustomer:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.Object obj = org.omg.CORBA.ORB.init (args, props).string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     String str = org.omg.CORBA.ORB.init (args, props).object_to_string (this);
     s.writeUTF (str);
  }
} // class _SnmpCustomerStub