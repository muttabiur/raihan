
import java.io.*;
import java.util.Scanner;
import java.util.*;

public class Learning {
   
	//Declare and define the member variable of this class
	
	public static final double INTEREST_RATE = 2.0 ;
	
	
   public static void main(String [] args)
   {
	   int [] score = new int [5];
	   
	   Scanner key = new Scanner(System.in);
	   
	   System.out.println("Enter a New Number:");
	   

	   for(int i = 0; i < score.length; i++)
	   {
		   //System.out.println("Enter a New Number:");
		   score[i] = key.nextInt();
		   /*while((score == null || score.length == 0))
			   System.out.println("The arrat is empty:");
			   */   
	   }
		   
	   /*
	       Display the content of the array
	    */
	   
	   for(int j = 0; j < score.length; j++)
		   
	   {
		   System.out.println("The Content of the array is:" + score[j]);
		   
	   }
	   
	   /*
	    * Testing while expression, in the while expression takes boolean expression
	    */
	  /* 
	   while(true)
	   {
		   for(int k = 0; k < score.length; k++)
			   System.out.println("The New Array is:" + score[k]);
	   }
	   */
	   
	   
	   int [] Raihan = {12, 34, 67};
	   for(int i = 0 ; i < Raihan.length; i++)
	   {
		   System.out.println("");
		   System.out.println("The new staric array is:" + Raihan[i]);
	   }
	   /*
	    * Type casting
	    */
	   int r = (int)2.0 ;
	   System.out.println(r);
	   
	   // String object checking
	   String s = "Hello Raihan";
	   if(! s.isEmpty())
	   {
		   System.out.println("OK");
		   System.out.println(s.charAt(6));
		   //Compare checking between string
		 boolean b =  s.equalsIgnoreCase("Hello Raih");
		 System.out.println(b);
	   }
	   else
	   {
		   System.out.println("Not OK");
	   }
	   
	
	   System.out.println("");
	   System.out.println(s.toUpperCase());
	   System.out.println(s.trim());
	   
	   //check of print or println
	   System.out.print("Joy");
	   System.out.print("Bangla");
	   
	   System.out.println(" ");
	   Scanner p = new Scanner(System.in);
	   System.out.println("Enter a Word:");
	   
	   String words = p.next() ;
	   
	   System.out.println("" + words);
	   
	  // System.exit(0);
	   System.out.println("Enter the entire line:");
	   Scanner pi = new Scanner(System.in);
	   String bi = pi.nextLine();
	  // System.exit(3);
	   System.out.println("The Line is :" + bi);
	   
	   //Switch statement checks
	   
	   Scanner keyboard = new Scanner(System.in);
	   
	   System.out.println("Enter the number of ice cream flavors:");
	   int numberOfFlavors = keyboard.nextInt();
	   
	   switch(numberOfFlavors)
	   {
	   case 1: 
		   System.out.println("Vana");
		   break ;
		   
	   case 3:
		   System.out.println("Honey");
		   break ;
		   
	   default:
		   System.out.println("Do not Care");
	       break ;
	       
	       
	   }
	   
	   
	   
	   System.exit(0);
   } //End of main Method
	
	
} //End of Class Learning.java
