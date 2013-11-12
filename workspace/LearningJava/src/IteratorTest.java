
import java.util.HashSet;
import java.util.Iterator;


public class IteratorTest {

	 public static void main(String[] args) {
		 
		 HashSet<String> s = new HashSet<String>();
		 
		 s.add("Hello");
		 s.add("Hey");
		 s.add("raihan");
		 
		 System.out.println("The set Contains the following Strings:");
		 
		 Iterator<String> it = s.iterator();
		 
		 while(it.hasNext()) {
			 
			 System.out.println(it.next());
		 }
		 
		 it.remove();
		 
		 System.out.println("");
		 System.out.println("The System contains:");
		 
		 it = s.iterator();
		 
		 while( it.hasNext()) {
			 System.out.println(it.next());
		 }
		 System.out.println("");
		 System.out.println("End of Program");
		 
		 System.exit(0);
		 
	 }
}
