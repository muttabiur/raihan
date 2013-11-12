import java.io.PrintWriter ;
import java.io.FileOutputStream ;
import java.io.FileNotFoundException ;

import java.util.Scanner ;
import java.io.FileInputStream ;

import java.io.BufferedReader ;
import java.io.FileReader;
import java.io.IOException ;

public class FileJava {
	
	public static void main(String []args) {
		
		PrintWriter outputStream = null ;
		
		try {
			
		     outputStream = new PrintWriter(new FileOutputStream("raihan.txt", true));
		    // outputStream.println("My Baby");
		
		}
		
		catch(FileNotFoundException e) {
		
			System.out.println("Error occured opening the file:");
			System.exit(0);
		}
		
		    outputStream.println("Hello There");
		    outputStream.println("My Love");
		    outputStream.println("My Baby");
		
		    //Flush the output Stream
		    outputStream.flush();
		    
	     	outputStream.close();
		
		    System.out.println("End of Program for writing data to file:");
		    
		    System.out.println("");
		    //Reading from a text file
		    System.out.println("We are going to read data from the text file: ");
		    
		    Scanner inputStream = null ;
		    
		    try {
		    	inputStream = new Scanner(new FileInputStream("raihan.txt"));
		    	
		    }
		    
		    catch(FileNotFoundException e){
		    	
		    	System.out.println("The Error with file input:" + e.getMessage());
		    	System.exit(0);
		    }
		    
		    while( inputStream.hasNextLine())
		    {
		    String line = inputStream.nextLine();
		    System.out.println("The Extracted element is :" + line);
		    
		   
		    }
		   
		   inputStream.close(); 
		   
		   System.out.println("");
		   System.err.println("The Error message could be displayed here:");
		   System.out.println("We are going to use BufferReader class for the reading purpose:");
		  
		   BufferedReader inputStream1 = null ;
		   try {
		         inputStream1 = new BufferedReader(new FileReader("raihan.txt"));
		    
		   String line1 = inputStream1.readLine();
		   
		   while(line1 != null) {
			   
			   System.out.println("The first line is:" + line1);
			   line1 = inputStream1.readLine();
		   }
		  
		   inputStream1.close();
	    }
		   catch(FileNotFoundException e) {
			   System.out.println("The Exception is:" + e.getMessage());
			   System.exit(0);
		   }
		   
		   catch(IOException e) {
			   System.out.println("The Exception is:"+ e.getMessage());
			   System.exit(0);   
		   }
		   
		    
	}

}
