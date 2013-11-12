package test.interview;

import java.util.*;
import java.io.*;

public class LeaseReCache {
	int num = 0;
	LinkedHashMap<String, String> map;

	public static void main(String[] args) throws IOException {
		LeaseReCache lruCache = new LeaseReCache();
	}

	public LeaseReCache() throws IOException {
		System.out.print("Please enter the size of the LRU Cache: ");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			num = Integer.parseInt(in.readLine());
		} catch (NumberFormatException ne) {
			System.out.println(ne.getMessage() + " is not a legal entry!");
			System.out.println("Please enter a numeric value.");
			System.exit(0);
		}
		map = new LinkedHashMap<String, String>() {
			public boolean removeEldestEntry(Map.Entry<String, String> eldest) {
				return size() > num;
			}
		};
		
		
		
		
		String ch = "N";
		while (!ch.equalsIgnoreCase("Y")) {
			System.out.print("Enter key: ");
			String str = in.readLine();
			System.out.print("Enter value: ");
			String str1 = in.readLine();
			map.put(str, str1);
			System.out.print("Do you want to stop the entry(Y/N)?");
			ch = in.readLine();
		}

		for (Map.Entry<String, String> e : getAll())
			System.out.println(e.getKey() + " : " + e.getValue());
	}

	public synchronized Collection<Map.Entry<String, String>> getAll() {
		return new ArrayList<Map.Entry<String, String>>(map.entrySet());
	}
}
