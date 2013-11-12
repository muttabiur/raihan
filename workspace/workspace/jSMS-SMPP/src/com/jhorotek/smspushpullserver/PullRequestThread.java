package com.jhorotek.smspushpullserver;

import java.net.URLEncoder;

import org.apache.xerces.parsers.DOMParser;

import org.apache.commons.configuration.XMLConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import com.jhorotek.smpp.SMPPClass;
import com.logica.smpp.pdu.SubmitSM;


public class PullRequestThread extends Thread{

	private XMLConfiguration config = null;
    private SMPPClass writer = null;
    private String shortCode = null;
    private String url = null;
    private boolean firstCall = false;
    private int msgCount = 0;
    private String securityString;
	private String OUT_MSG_ID;
	private String MOBILENO;
	private String MSG_TYPE;
	private String REPLY_TEXT;
	private String SEND_PORT;
	private String COST;
	//private String OPERATOR_FLAG;
	//private String IN_MSG_ID;
    
    
	public PullRequestThread(XMLConfiguration SMPPConfig, SMPPClass writer, String shortCode){
		this(SMPPConfig, writer, shortCode, false);
	}
    
    public PullRequestThread(XMLConfiguration SMPPConfig, SMPPClass writer, String shortCode, boolean firstCall){
    	config = SMPPConfig;
    	this.writer = writer;
    	this.shortCode = shortCode;
    	this.firstCall = firstCall;
    	
    	//URLEncoder.encode(message,"UTF-8")
    	try {
    		securityString = "&USERNAME=" + URLEncoder.encode(config.getString("CP_USERNAME"),"UTF-8") + "&PASSWORD=" + URLEncoder.encode(config.getString("CP_PASSWORD"),"UTF-8");
			if(!firstCall)
				url = config.getString("PULL_URL") + "?PORT=" + shortCode + "&OPERATOR_FLAG=" + URLEncoder.encode(config.getString("PULL.OPERATOR_CODE"),"UTF-8") + "&STATUS=0" + securityString;
			else
				url = config.getString("PULL_URL") + "?PORT=" + shortCode + "&OPERATOR_FLAG=" + URLEncoder.encode(config.getString("PULL.OPERATOR_CODE"),"UTF-8") + "&STATUS=1" + securityString;
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void run(){
    	System.out.println("Started with pull thread:" + config.getString("NAME") + " shortcode: " + shortCode);
    	
    	System.out.println(url);
    	
    	DOMParser parser = new DOMParser();
    	
    	msgCount = 10;
    	
    	try {
    		
    		while(msgCount > 9){
				parser.parse(url + "&" + (new java.util.Date()).getTime());
				
				Document document = parser.getDocument();
				Element docEle = document.getDocumentElement();
	
				//NodeList nl = docEle.getElementsByTagName("sms");
				NodeList nl = (NodeList)docEle;
				msgCount = nl.getLength();
				if (nl != null &&  msgCount> 0) {
					
					//Element el = (Element) nl.item(0);
					for (int i = 0; i < msgCount; i++) {
	
						Element el = (Element) nl.item(i);
					
						OUT_MSG_ID =  getTextValue(el, "OUT_MSG_ID");
						//IN_MSG_ID = getTextValue(el, "IN_MSG_ID");
						MOBILENO = getTextValue(el, "MOBILENO");
						REPLY_TEXT = getTextValue(el, "REPLY_TEXT");
						MSG_TYPE = getTextValue(el, "MSG_TYPE");
						SEND_PORT = getTextValue(el, "SEND_PORT");
						COST = getTextValue(el, "COST");
						//OPERATOR_FLAG = getTextValue(el, "OPERATOR_FLAG");
						
						while(!CommonUtils.ackRequest(config.getString("ACK_URL") + "?OUT_MSG_ID=" + OUT_MSG_ID + "&STATUS=1" + securityString)) {
							
						}
						
						if(com.jhorotek.smpp.CommonUtils.isNumeric(COST) && Integer.parseInt(COST) > 0){
							String port = COST;
							
							if(config.getString("PULL.PRICES.P" + COST) == null){
								if(COST.length() < 2)
									port = "0" + port;
								
								if(config.getString("PULL.PRICE_TAG").equalsIgnoreCase("prefix")){
									SEND_PORT = port + SEND_PORT; 
								}else{
									SEND_PORT += port;
								}
							}else{
								SEND_PORT += config.getString("PULL.PRICES.P" + COST);
							}
						}
						
						SubmitSM request  = com.jhorotek.smpp.CommonUtils.buildSubmit(config, SEND_PORT, MOBILENO, REPLY_TEXT, false);
						
						while(!writer.ResponseWriter(request)) {
							writer.Connect();
						}
						
						while(!CommonUtils.ackRequest(config.getString("ACK_URL") + "?OUT_MSG_ID=" + OUT_MSG_ID + "&STATUS=2" + securityString)){
							
						}
						
					}
				}
    		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Ended with pull thread:" + config.getString("NAME") + " shortcode: " + shortCode);
    }
    
    private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			try{
				textVal = el.getFirstChild().getNodeValue();
			}catch (NullPointerException e) {
				return null;
			}
			
		}

		return textVal;
	} 
}
