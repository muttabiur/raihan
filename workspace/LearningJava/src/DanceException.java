
import java.util.Scanner ;

public class DanceException {
   
	public static void main(String[] args) {
		
		System.out.println("Demonstrate the Exception handling Mechnaism:");
		
		Scanner keyBoard = new Scanner(System.in);
		
		System.out.println("Enter the Male dancer number:");
		
		int mDancer = keyBoard.nextInt();
		
		System.out.println("Enter the female dancer number:");
		
		int fDancer = keyBoard.nextInt();
		
		//Put try block here, if exception is thrown then break the loop from the try blockand goes to 
		//catch block
		
		try {
			 if( mDancer == 0 && fDancer == 0)
				 throw (new Exception("Lecture is canceled, No Students"));
			 
			 else if( mDancer == 0)
				  throw (new Exception("Lecture is canceled, No Male Students"));
			 
			 else if (fDancer == 0)
				  throw (new Exception("Lecture is canceled, No Female Students"));
			 
			 
			 if( fDancer > mDancer)
				 System.out.println("Some female dancer can take rest:");
			 else
				 System.out.println("Some male dancer can take rest:");
			
		}
		
		catch(Exception e){
			
			String message = e.getMessage();
			System.out.println("The thrown exception message is:" + message);
			System.exit(0);
			
		}
		
		finally {
			
			System.out.println("Message from the final block, No Exception is not thrown:");
		}
		//If any exception is not thrown then execute the following code
		System.out.println("Begin the Dance party:");
		System.out.println("Exception is cool:?");
		System.exit(0);
	}
	
	
	
}
