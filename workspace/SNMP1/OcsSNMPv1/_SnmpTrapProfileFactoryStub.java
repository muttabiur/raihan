package OcsSNMPv1;


/**
* OcsSNMPv1/_SnmpTrapProfileFactoryStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from OcsSNMPv1.idl
* Monday, January 26, 2009 4:24:09 o'clock PM EST
*/

public class _SnmpTrapProfileFactoryStub extends org.omg.CORBA.portable.ObjectImpl implements OcsSNMPv1.SnmpTrapProfileFactory
{

  public OcsSNMPv1.SnmpTrapProfile createProfile (int port)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("createProfile", true);
                $out.write_ulong (port);
                $in = _invoke ($out);
                OcsSNMPv1.SnmpTrapProfile $result = OcsSNMPv1.SnmpTrapProfileHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return createProfile (port        );
            } finally {
                _releaseReply ($in);
            }
  } // createProfile

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:OcsSNMPv1/SnmpTrapProfileFactory:1.0"};

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
} // class _SnmpTrapProfileFactoryStub
