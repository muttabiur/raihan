
import java.io.* ;
import java.net.* ;



public class NetworkSocket {
	
	public static void main(String []args)throws Exception {
		
		String sentence = null;
		String modifiedSentence = null ;
		
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		
		InetAddress rHost = InetAddress.getByName("localhost");
		System.out.println("Hello");
		Socket clientSocket = new Socket( rHost, 6789);
		
		System.out.println("Hello");
		DataOutputStream outToServer =
			new DataOutputStream(clientSocket.getOutputStream());
		
		BufferedReader inFromServer = new BufferedReader(new
			                InputStreamReader(clientSocket.getInputStream()));
		
			sentence = inFromUser.readLine();
			
			outToServer.writeBytes(sentence + '\n');
			
			System.out.println("Hello");
			
			modifiedSentence = inFromServer.readLine();
			
			System.out.println("Hello");
			
			System.out.println("FROM SERVER: " + modifiedSentence);
			
			System.out.println("Hello");
			
			clientSocket.close();
			
	}
	
	

}
