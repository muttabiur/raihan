package DETApp;


/**
* DETApp/DETOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DET.idl
* Sunday, March 8, 2009 10:30:35 o'clock PM EDT
*/

public interface DETOperations 
{
  int SnmpSetBalance ();
  int buyItem (String item, int qty);
  int printReport ();
  int printReportAfterTransaction ();
  void trapBalance ();

  //long sellItem(int String item, in long qty,in double bal);
  int sellItem (int qty, double bal);
  int tradeItems (String item1, int qty1, String item2, int qty2, String etrader);

  //string snmpSetRequest(in double bal) ;
  boolean snmpSentRequestToServer1 (String item1);
  boolean snmpSentRequestToServer2 (String item2);
  int getPrintReportFromServer1 ();
  int getPrintReportFromServer2 ();

  //string snmpGetRequestSysDescription(in string sysDescription, in string item1);
  String snmpGetRequestSysDescription (String item2);
  String snmpGetRequestSysContact (String item2);
  String snmpGetRequestSysName (String item2);
  String snmpGetRequestSysLocation (String item2);
  String snmpGetRequestSysServiceInfo (String item2);
} // interface DETOperations
