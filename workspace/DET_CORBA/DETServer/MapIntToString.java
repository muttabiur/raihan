package DETServer;

import java.util.HashMap ;

/**
 * @author m_sk
 */
public class MapIntToString {

    public String mapIntergerToString(Character  a) {



	String s = a.toString();
	int i = Integer.parseInt(s);

	HashMap<Integer, String> hmap = new HashMap<Integer,String>();

	hmap.put(5, "price:");
	hmap.put(6, "quantity:");
	hmap.put(7, "balance:");
	hmap.put(8, "itemName:");

	String mapValue = null ;

	mapValue = hmap.get(i);


	return mapValue ;

    }

}
