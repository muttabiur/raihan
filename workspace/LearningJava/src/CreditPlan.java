
import java.util.ArrayList;

import java.util.Iterator;


public class CreditPlan {
         
	  public static final double Interest_Rate = 1.5 ;
	
	 
	  public void calculate_Interest( double debt ) {
		  
		 ArrayList<Double> a = new ArrayList<Double>();
		  
		  double payedInterest = 0.0 ;
		  double realPay = 0.0 ;
		  double monthly_payment = 50.0 ;
		  double remaining_Debt = 0.0 ;
		  
		  remaining_Debt = debt ;
		  
		  double counter = 0.0 ;
		  

         for(int i = 0 ; i < remaining_Debt; i++) {
        	 
			  payedInterest = (remaining_Debt * Interest_Rate)/100 ;
			  realPay = monthly_payment - payedInterest ;
			  
			  remaining_Debt = remaining_Debt - realPay ; 
			  counter++ ;
			 
			  a.add(counter);
			  a.add(remaining_Debt);
			  a.add(payedInterest);
			  a.add(realPay);
			 
			         
		    } 
         System.out.println("The Detail Informaion for the interest is stated below:");
         for (Iterator<Double> iter = a.iterator(); iter.hasNext();  ) {
        	    
        	    System.out.println(iter.next());
        	    System.out.println("");
        	}
	  }
		 
	  
	  public static void main(String [] agrs) {
		  
		  CreditPlan p = new CreditPlan();
		  p.calculate_Interest(1000);
		  
		  System.exit(0);
	  }
	
	
	
}
