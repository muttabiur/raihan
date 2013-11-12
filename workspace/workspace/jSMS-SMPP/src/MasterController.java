import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.configuration.XMLConfiguration;

import com.jhorotek.smpp.SMPPController;
import com.jhorotek.smspushpullserver.CronRequestThread;


public class MasterController{
	public static void main(String [] args){
		
		XMLConfiguration cronConfiguration = null;
		LoadConfiguration loadConfiguration = new LoadConfiguration();
		LinkedHashMap loadedConfiguration = loadConfiguration.getConfiguration();
		LinkedHashMap loadedSMPPControllerThread = new LinkedHashMap();
		boolean isCron = false;
		long cronTime = 0;
		CronRequestThread cronRequestThread = null;
		SMPPController smppController = null;
		Iterator it = null;
		
		if(loadedConfiguration.containsKey("_CRON")){
			cronConfiguration = (XMLConfiguration) loadedConfiguration.get("_CRON");
			loadedConfiguration.remove("_CRON");
			isCron = true;
			cronTime = cronConfiguration.getLong("DELAY_TIME") * 60 * 1000;
		}
		
		it = loadedConfiguration.entrySet().iterator();
	    
		while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        System.out.println(pairs.getKey() + " = " + ((XMLConfiguration)pairs.getValue()).getString("OPERATOR_CODE"));
	        
	        try {
				smppController = new SMPPController((XMLConfiguration)pairs.getValue(), true);
				smppController.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        loadedSMPPControllerThread.put(pairs.getKey(), smppController);

	        try {
				Thread.sleep(10 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

		long first = 0;
		
	    while(true){
	    	
	    	System.out.println((new java.util.Date()));
	    	it = loadedSMPPControllerThread.entrySet().iterator();
	    	
	    	while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        System.out.println(pairs.getKey() + " = Alive: " + ((SMPPController)pairs.getValue()).isAlive() + " PullThread: " + ((SMPPController)pairs.getValue()).isPull);
		        
				if(!((SMPPController)pairs.getValue()).isAlive()){
					System.out.println(pairs.getKey() + " = Starting again....");
					((SMPPController)pairs.getValue()).start();
				}
		    }
	        
	    	if(isCron){
	    		if((System.currentTimeMillis() - first) > cronTime){
	    			
	    			Object obj = cronConfiguration.getProperty("URLS.URL");
        			
        			if (obj instanceof Collection) {
        				int cronReqSize = ((Collection) obj).size();
        	            
        	            //loading each and making separate config joining with the common config fields
        	            for (int i = 0; i < cronReqSize; i++) {
        	                
        	            	cronRequestThread = new CronRequestThread(cronConfiguration.getString("URLS.URL(" + i + ")"));
        	            	cronRequestThread.start();
        	            }
        			}else{
        				
        				cronRequestThread = new CronRequestThread(cronConfiguration.getString("URLS.URL"));
    	            	cronRequestThread.start();
        			}
        			
	    			first = System.currentTimeMillis();
	    		}
	    	}
	    	
	    	try {
				Thread.sleep(30 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
}