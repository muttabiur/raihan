import java.util.Collection;
import java.util.LinkedHashMap;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;



public class LoadConfiguration{
	
	LinkedHashMap loadedConfiguration = new LinkedHashMap();
	
	public LinkedHashMap getConfiguration(){
		
		XMLConfiguration mainConfig = null;
		XMLConfiguration SMPPConfig = null;
		XMLConfiguration []tempConfig = null;
		int SMPPLoadSize = 0;
		try {
			mainConfig = new XMLConfiguration("config/MainConfig.xml");
			SMPPConfig = new XMLConfiguration("config/SMPPConfig.xml");
			
			Object obj = SMPPConfig.getProperty("SMPP.ACTIVE");
			
			//checking if multiple list of smpp config
			if (obj instanceof Collection) {
				int SMPPSize = ((Collection) obj).size();
	            tempConfig = new XMLConfiguration[SMPPSize];
	            
	            //loading each and making separate config joining with the common config fields
	            for (int i = 0; i < SMPPSize; i++) {
	                
	            	if(SMPPConfig.getString("SMPP(" + i + ").ACTIVE").equalsIgnoreCase("true")){
		            	tempConfig[SMPPLoadSize] = new XMLConfiguration(SMPPConfig.configurationAt("SMPP(" + i + ")"));
		                tempConfig[SMPPLoadSize].append(mainConfig.configurationAt("COMMON"));
		                ++SMPPLoadSize;
	            	}
	            }
			}else{
				
				if(SMPPConfig.getString("SMPP.ACTIVE").equalsIgnoreCase("true")){
					SMPPLoadSize = 1;
					tempConfig = new XMLConfiguration[SMPPLoadSize];
					tempConfig[0] = new XMLConfiguration(SMPPConfig.configurationAt("SMPP"));
					tempConfig[0].append(mainConfig.configurationAt("COMMON"));
				}
			}
			
			
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(mainConfig.getString("CRON.ACTIVE").toLowerCase().equals("true")){
			loadedConfiguration.put("_CRON", new XMLConfiguration(mainConfig.configurationAt("CRON")) );
		}
		
		for (int i = 0; i < SMPPLoadSize; i++) {
			
			loadedConfiguration.put(tempConfig[i].getString("NAME"), tempConfig[i]);
			
			/*System.out.println(tempConfig[i].getProperty("NAME"));
			System.out.println(tempConfig[i].getProperty("CP_USERNAME"));
			System.out.println(tempConfig[i].getProperty("CP_PASSWORD"));
			System.out.println(tempConfig[i].getProperty("REQUEST_RESPONSE_URL"));
			System.out.println(tempConfig[i].getProperty("PULL_URL"));*/
		}
		return loadedConfiguration;
		
		
	}
}