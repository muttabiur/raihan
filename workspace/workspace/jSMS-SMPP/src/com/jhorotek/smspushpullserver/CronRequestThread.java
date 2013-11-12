package com.jhorotek.smspushpullserver;

import org.apache.xerces.parsers.DOMParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



public class CronRequestThread extends Thread{

    private String url = null;
    private int msgCount = 0;
    
    
    public CronRequestThread(String url){
    	this.url = url;
    }
    
    public void run(){
    	System.out.println("Started with cron thread.... ");
    	
    	System.out.println(url);
    	
    	DOMParser parser = new DOMParser();
    	
    	msgCount = 10;
    	
    	try {
    		
    		while(msgCount > 9){
    			//adding random number with url to avoid cache hit.
				parser.parse(url + (new java.util.Date()).getTime());
				
				Document document = parser.getDocument();
				Element docEle = document.getDocumentElement();
	
				//NodeList nl = docEle.getElementsByTagName("sms");
				NodeList nl = (NodeList)docEle;
				msgCount = nl.getLength();
				if (nl != null &&  msgCount> 0) {
					
					Element el = (Element) nl;
					
					
					msgCount =  Integer.parseInt(getTextValue(el, "MSG_COUNT"));
					
				}
    		}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Ended with cron thread.... ");
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
