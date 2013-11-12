

public class JavaGeneric<T> {

	private T data ;
	
	public void setData(T newData) {
	    
		this.data = newData ;
		
	}
	
	public T getData() {
		
		return data ;
	}
	
	
  public static void main(String[]args) {
	  
	  //Create the object of generic class
	  JavaGeneric<String> java = new JavaGeneric<String>();
	  
	  java.setData("Hello");
	  
	  System.out.println("The data information is stated here:" + java.getData());
	
	  JavaGeneric<Integer> java1 = new JavaGeneric<Integer>();
		
	  java1.setData(1000);
	  
	  System.out.println("The Integer number is stated:" + java1.getData());
	  
	  
	  System.exit(0);
	  
  }
}

