package com.jhorotek.smspushpullserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class CommonUtils {

	public static String[] parseMsg(String message){
		System.out.println("in msg parse....");
		
		String[] tmp2=null;
		String []tmp = null;
		try {
			
			tmp2 = new String[10];
			System.out.println("in msg parse try....");
			System.out.println("index +: "+message.indexOf("+"));
			System.out.println("index %20: "+message.indexOf("%20"));
			System.out.println("index ' ': "+message.indexOf(" "));
			
			if(message.indexOf("+") != -1)
				tmp = (message==null)?null:message.split("\\+");
			else if(message.indexOf("%20") != -1)
				tmp =  (message==null)?null:message.split("%20");
			else
				tmp =  (message==null)?null:message.split(" ");

			int i=0;
			int j=0;

			System.out.println("parse length: "+tmp.length);
			while(i<tmp.length){
				System.out.println("parse i:"+i);
				System.out.println("parse tmp[i].length():"+tmp[i].length());
				if(tmp[i].length() != 0){
					System.out.println("parse tmp[i]:"+tmp[i]);
					//tmp2[j] = new String();
					tmp2[j] = tmp[i];
					++j;
				}
				++i;
			}
			
			tmp  = new String[j];
			
			i=0;
			while(i<j){
				tmp[i] = tmp2[i];
				++i;
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("out msg parse....");
		return tmp;
    }
	
	public static String trimResponse(String returnMessage){
		
		if(returnMessage == null) return "blank";
		
		if(returnMessage.length() > 160){
			System.out.println("msg more than 160 character, triming....");
			return returnMessage.substring(0,160);
		}
		
		return returnMessage;
	}
	
	public static boolean ackRequest(String theUrlStr){
		
		String returnStr = "";
		try{
			
			URL url = new URL(theUrlStr); //URL connetion.
			System.out.println(theUrlStr);
			
			URLConnection urlCon = url.openConnection();
			urlCon.setReadTimeout(20000);
			urlCon.setConnectTimeout(20000);
			
			System.out.println(url.getAuthority().toString());
			/*get inputstream reader from url connection.
			 */			
			BufferedReader in = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
			
			String tempStr = "";
			
			/*Read each line from inputStream and construct returnStr.
			 */
			while((tempStr = in.readLine()) != null){
				returnStr += tempStr;
				//System.out.println(returnStr);
			}
			System.out.println(returnStr);
			return true;
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
			//System.out.println("This is socket error.");
			
			/*A problem with the connection occured. reconnect and read again. 
			 */
			//this.ackRequest();
		}
	}
}
