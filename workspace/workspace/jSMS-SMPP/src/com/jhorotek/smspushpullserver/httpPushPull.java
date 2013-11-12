package com.jhorotek.smspushpullserver;

import java.net.URL;
import java.util.LinkedHashMap;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class httpPushPull implements pushpullCommmunicator {

	public LinkedHashMap getData(URL url) {
		// TODO Auto-generated method stub
		LinkedHashMap parsedData = null;
		
		try{
			parsedData = new LinkedHashMap();
			//URL url = new URL(this.theUrlStr); //URL connetion.
			System.out.println(url.toString());
			DOMParser parser = new DOMParser();
			parser.parse(url.toString());

			Document document = parser.getDocument();
			Element docEle = document.getDocumentElement();

			//NodeList nl = docEle.getElementsByTagName("sms");
			NodeList nl = (NodeList)docEle;
			
			if (nl != null && nl.getLength() > 0) {
				
				//Element el = (Element) nl.item(0);
				Element el = (Element) nl;
				
				parsedData.put("OUT_MSG_ID", getTextValue(el, "OUT_MSG_ID"));
				parsedData.put("MOBILENO", getTextValue(el, "MOBILENO"));
				parsedData.put("IN_MSG_ID", getTextValue(el, "IN_MSG_ID"));
				parsedData.put("REPLY_TEXT", getTextValue(el, "REPLY_TEXT"));
				parsedData.put("MSG_TYPE", getTextValue(el, "MSG_TYPE"));
				parsedData.put("SEND_PORT", getTextValue(el, "SEND_PORT"));
			}
			
			/*
			URLConnection urlCon = url.openConnection();
		//	urlCon.setReadTimeout(1000);
		//	urlCon.setConnectTimeout(2000);
			
			System.out.println(url.getAuthority().toString());
			//get inputstream reader from url connection.
			 			
			BufferedReader in = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
			
			String tempStr = "";
			
			//Read each line from inputStream and construct returnStr.
			
			while((tempStr = in.readLine()) != null){
				this.returnStr += tempStr;
				System.out.println(returnStr);
			}
			*/
		
		}
		catch(Exception e){
			e.printStackTrace();
			//return null;
		}
		
		return parsedData;
	}

	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			try{
				textVal = el.getFirstChild().getNodeValue();
			}catch (NullPointerException e) {
				// TODO: handle exception
				return null;
			}
			
		}

		return textVal;
	}
}
