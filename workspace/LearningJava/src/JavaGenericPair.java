
public class JavaGenericPair<T1, T2> {
	
	private T1 first;
	private T2 second ;
	
	public JavaGenericPair() {
		first = null ;
		second = null ;
	}

	public JavaGenericPair(T1 firstItem, T2 secondItem) {
		
		this.first = firstItem ;
		this.second = secondItem ;
	}
	
	public void setFirst(T1 fItem) {
		this.first = fItem ;
	}
	
	public void setSecond(T2 sItem) {
		this.second = sItem ;
	}
	
	public T1 getFirst() {
		
		return first ;
	}
	
	public T2 getSecond() {
		
		return second ;
	}
	
	public String toString() {
		System.out.println("The firstItem is:" + this.getFirst());
		System.out.println("The secondItem is:" + this.getSecond());
		return "OK" ;
	}
	
	public static void main(String [] args) {
		
		JavaGenericPair<String, Integer> java = new JavaGenericPair<String, Integer>("Hello", 2000);
		
		java.toString();
		
		JavaGenericPair<Double, Integer> java1 = new JavaGenericPair<Double, Integer>(2000.00, 2000);
		
		java1.toString();
		java1.getClass();
		
		System.exit(0);
		
		
	}
	
	
}

