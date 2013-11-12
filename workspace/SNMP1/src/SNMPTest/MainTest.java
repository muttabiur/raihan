package SNMPTest;

import java.io.InputStream;

import com.outbackinc.services.protocol.snmp.SnmpLocalInterfaces;
import com.outbackinc.services.protocol.snmp.mib.SnmpMIBDictionary;
import com.outbackinc.services.protocol.snmp.mib.SnmpMIBService;
import com.outbackinc.services.protocol.snmp.mib.jMIBC;

public class MainTest  {

	public static void main(String[] args) {
		
		//SnmpV1GetSysInfo(String szHost, String szReadCommunity)
		
		SnmpV1GetSysInfo info = new SnmpV1GetSysInfo("Localhost", "public");
		SnmpV1TrapSenderTest test = new SnmpV1TrapSenderTest();
	
		
		
		// load a MIB so we can request by name
		SnmpMIBService cMIBService = SnmpLocalInterfaces.getMIBService();
		//InputStream cInputStream = jMIBC.loadMib("HOST-RESOURCES-MIB.my");
		
		
		// convert enumerated value to a string
		//SnmpMIBDictionary cMIBDictionary = cMIBService.getMIBDictionary("HOST-RESOURCES-MIB.my");
		
	//	String szEnumValue = cMIBDictionary.resolveEnum("hrDeviceStatus", 2);
		
		// get access, status, description, type and ‘abstract’ type by
		// object name
		//SnmpMIBDictionary cMIBDictionary1 = cMIBService.getMIBDictionary("HOST-RESOURCES-MIB.my");
		//String szAccess = cMIBDictionary1. resolveNameAccess ("hrDeviceStatus");
		//String szStatus = cMIBDictionary1. resolveNameStatus ("hrDeviceStatus");
		//String szDescription = cMIBDictionary1. resolveNameDescription ("hrDeviceStatus");
		//String szType = cMIBDictionary1. resolveNameType ("hrDeviceStatus");
		//String szAbstractType = cMIBDictionary1. resolveNameAbstractType ("hrDeviceStatus");
		
		//System.out.println("He is :" + szType);

		//SnmpV1GetSysInfo info = new SnmpV1GetSysInfo("localhost", "public");
		//info.get();
		
		
		
		System.exit(0);
	}
}
