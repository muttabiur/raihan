package OcsSNMPv1;


/**
* OcsSNMPv1/_SnmpServiceStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from OcsSNMPv1.idl
* Monday, January 26, 2009 4:24:09 o'clock PM EST
*/

public class _SnmpServiceStub extends org.omg.CORBA.portable.ObjectImpl implements OcsSNMPv1.SnmpService
{

  public void placeInspectionOrder (String host, int port, int retries, int timeout, int cacheThreshold, OcsSNMPv1.RequestType type, String readCommunity, String[] oids, OcsSNMPv1.SnmpCustomer customer, int orderNumStart)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("placeInspectionOrder", true);
                $out.write_string (host);
                $out.write_ulong (port);
                $out.write_ulong (retries);
                $out.write_ulong (timeout);
                $out.write_ulong (cacheThreshold);
                OcsSNMPv1.RequestTypeHelper.write ($out, type);
                $out.write_string (readCommunity);
                OcsSNMPv1.StringSeqHelper.write ($out, oids);
                OcsSNMPv1.SnmpCustomerHelper.write ($out, customer);
                $out.write_long (orderNumStart);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                placeInspectionOrder (host, port, retries, timeout, cacheThreshold, type, readCommunity, oids, customer, orderNumStart        );
            } finally {
                _releaseReply ($in);
            }
  } // placeInspectionOrder

  public void placeModificationOrder (String host, int port, int retries, int timeout, String writeCommunity, boolean atomic, OcsSNMPv1.SnmpVarBind[] varbinds, OcsSNMPv1.SnmpCustomer customer, int orderNumStart)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("placeModificationOrder", true);
                $out.write_string (host);
                $out.write_ulong (port);
                $out.write_ulong (retries);
                $out.write_ulong (timeout);
                $out.write_string (writeCommunity);
                $out.write_boolean (atomic);
                OcsSNMPv1.SnmpVarBindSeqHelper.write ($out, varbinds);
                OcsSNMPv1.SnmpCustomerHelper.write ($out, customer);
                $out.write_long (orderNumStart);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                placeModificationOrder (host, port, retries, timeout, writeCommunity, atomic, varbinds, customer, orderNumStart        );
            } finally {
                _releaseReply ($in);
            }
  } // placeModificationOrder

  public boolean addTrapListenerProfile (OcsSNMPv1.SnmpTrapListener listener, OcsSNMPv1.SnmpTrapProfile profile) throws OcsSNMPv1.SocketProblem
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("addTrapListenerProfile", true);
                OcsSNMPv1.SnmpTrapListenerHelper.write ($out, listener);
                OcsSNMPv1.SnmpTrapProfileHelper.write ($out, profile);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:OcsSNMPv1/SocketProblem:1.0"))
                    throw OcsSNMPv1.SocketProblemHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return addTrapListenerProfile (listener, profile        );
            } finally {
                _releaseReply ($in);
            }
  } // addTrapListenerProfile

  public boolean removeTrapListenerProfile (OcsSNMPv1.SnmpTrapListener listener, OcsSNMPv1.SnmpTrapProfile profile) throws OcsSNMPv1.NoSuchElement
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("removeTrapListenerProfile", true);
                OcsSNMPv1.SnmpTrapListenerHelper.write ($out, listener);
                OcsSNMPv1.SnmpTrapProfileHelper.write ($out, profile);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:OcsSNMPv1/NoSuchElement:1.0"))
                    throw OcsSNMPv1.NoSuchElementHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return removeTrapListenerProfile (listener, profile        );
            } finally {
                _releaseReply ($in);
            }
  } // removeTrapListenerProfile

  public OcsSNMPv1.SnmpTrapProfile[] listTrapListenerProfiles (OcsSNMPv1.SnmpTrapListener listener) throws OcsSNMPv1.NoSuchElement
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("listTrapListenerProfiles", true);
                OcsSNMPv1.SnmpTrapListenerHelper.write ($out, listener);
                $in = _invoke ($out);
                OcsSNMPv1.SnmpTrapProfile $result[] = OcsSNMPv1.SnmpTrapProfileSeqHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:OcsSNMPv1/NoSuchElement:1.0"))
                    throw OcsSNMPv1.NoSuchElementHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return listTrapListenerProfiles (listener        );
            } finally {
                _releaseReply ($in);
            }
  } // listTrapListenerProfiles

  public void removeTrapListener (OcsSNMPv1.SnmpTrapListener listener) throws OcsSNMPv1.NoSuchElement
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("removeTrapListener", true);
                OcsSNMPv1.SnmpTrapListenerHelper.write ($out, listener);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:OcsSNMPv1/NoSuchElement:1.0"))
                    throw OcsSNMPv1.NoSuchElementHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                removeTrapListener (listener        );
            } finally {
                _releaseReply ($in);
            }
  } // removeTrapListener

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:OcsSNMPv1/SnmpService:1.0"};

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
} // class _SnmpServiceStub