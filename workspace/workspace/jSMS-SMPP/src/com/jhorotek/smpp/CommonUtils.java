package com.jhorotek.smpp;

import org.apache.commons.configuration.XMLConfiguration;

import com.logica.smpp.pdu.Address;
import com.logica.smpp.pdu.AddressRange;
import com.logica.smpp.pdu.SubmitSM;
import com.logica.smpp.pdu.WrongLengthOfStringException;

public class CommonUtils {

	public static void setAddressParameter(String descr, Address address, byte ton, byte npi, String addr)
    {
        address.setTon(ton);
        address.setNpi(npi);
        try {
            address.setAddress(addr);
        } catch (WrongLengthOfStringException e) {
            System.out.println("The length of "+descr+" parameter is wrong.");
        }
    }
	
	static void setAddressRangeParameter(String descr, AddressRange address, byte ton, byte npi, String addr)
    {
        address.setTon(ton);
        address.setNpi(npi);
        try {
            address.setAddressRange(addr);
        } catch (WrongLengthOfStringException e) {
            System.out.println("The length of "+descr+" parameter is wrong.");
        }
    }
	
	public static SubmitSM buildSubmit(XMLConfiguration config, String SEND_PORT, String MOBILENO, String REPLY_TEXT, boolean isUnicode){
        
        System.out.println("In buildSubmit() ....");
        SubmitSM request = null;
        try {
        	request = new SubmitSM();
    		
    		Address sourceAddress = new Address();
    		if(isNumeric(SEND_PORT)){
    			setAddressParameter("Source-Address", sourceAddress, config.getByte("SOURCE_TON"), config.getByte("SOURCE_NPI"), SEND_PORT);
    		}else{
    			setAddressParameter("Source-Address", sourceAddress, (byte)5, config.getByte("SOURCE_NPI"), SEND_PORT);
    		}
    		
    		Address destAddress = new Address();
    		setAddressParameter("Destination-Address", destAddress, config.getByte("DESTINATION_TON"), config.getByte("DESTINATION_NPI"), MOBILENO);
    		
    		//the short code used 
            request.setSourceAddr(sourceAddress);
            
            request.setDestAddr(destAddress);
            
            //Defines the encoding scheme of the short message user data.
            if(isUnicode){
            	request.setDataCoding((byte)8);
            }else{
            	request.setDataCoding((byte)0);
            }
            
            request.setShortMessage(REPLY_TEXT);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Out buildSubmit() ....");
        return request;
    }
	
	public static boolean isNumeric(String str){
	    
		try{
	      Integer.parseInt(str);
	    
		}catch(NumberFormatException nfe){
	      
			return false;
	    }
	    
		return true;
	  }
}
