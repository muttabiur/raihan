package test.interview;

import java.util.*;
import java.io.*;
public class LeastRecentlyCache {

	public final int sizeOfCache = 5 ;
	public HashMap<String, String> map ;
	
	public int getHashSize(){
		
		return map.size() ;
	}
	
	public void testCache(){
		
		String stopCondition = "N" ;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String key = null ;
		String value = null ;
		
		while(!stopCondition.equalsIgnoreCase("N")){
			
			System.out.println("Eneter Key:");
		     try {
				key = in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Eneter the value:") ;
			
			try {
				value = in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			map.put(key, value) ;
		}
	}
	
}