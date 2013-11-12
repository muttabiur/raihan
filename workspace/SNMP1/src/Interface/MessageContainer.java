package Interface;

/**
 * @author m_sk
 */

 //Define a Message Container class that implements display me
public final class MessageContainer implements DisplayMessage
{
	public static String showMsg(int code)
	{
		  switch(code)
		  {
		  	case NOT_ENOUGH_BALANCE:	
		  		return "NOT ENOUGH BALANCE" ;
		  	
		  	case SUCCESS:	
		  		return "Successfull Transaction" ;
		  	
		  	case NOT_ENOUGH_QUANTITY:	
		  		return "NOT ENOUGH QUANTITY" ;
		  	
		  	case PRODUCT_NOT_FOUND:	
		  		return "PORDUCT NOT FOUND" ;
		  	
		  	case NOT_ENOUGH_FUNDS:	
		  		return "NOT ENOUGH FUNDS" ;
		  	
		  	case SERVER_NOT_FOUND:	
		  		return "SERVER NOT FOUND" ;
		  		
		  	
		  	default: return null;
		  }
	}
}
