
package DETServer;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.Locale;
import java.util.*;


/**
 * @author m_sk
 */

public class ContainerManagedItem implements Serializable{

    Format df = new DecimalFormat("####.00");

  
    
    //private DataFile dataFile = new DataFile("where my data is"); 
     public String name;
     private int qty;
     private double price;
     private double balance;

    private transient Timer tm; 

    static public Object lockPrice=new Object();
    static public Object lockQty=new Object();
    static public Object lockBalance=new Object();

    public ContainerManagedItem(String i, double p, int q, double b) {

	this.name=i;
	this.qty=q;
	this.price=p;
	this.balance=b;

	int interval=1;

	tm = new Timer(true);

	/**
	 *  change price peroidically
	 *  Trigger changePrice method
	 * 
	 */

	tm.schedule( new TimerTask() {
	    public void run() {
		//	changePrice();
	    }
	}, interval*100, interval*100); //repeat every [interval]m seconds


    }


    /**
     * Design By Contract
     * Precondition: This function expects no input from the user.
     * PostCondition: This function will set price periodically.
     * 
     */

    private void changePrice(){

	Random r=new Random();
	double p=price;


	synchronized(lockPrice){
	    // price = get value from falat file
	    price=p*(0.9+r.nextDouble()*0.2); //increase or decrease within 10%

	    price = Double.parseDouble(df.format(Double.valueOf(price)));
	    
	    // set price in flat file (price)

	}

    }

    /**
     * Design By Contract
     * Precondition: This function expects no input from the user.
     * PostCondition: This function will return current price of that item.
     * 
     */

    public double getPrice(){

	synchronized(lockPrice){
	   // return 
	    return price;
	}

    }


    /**
     * Design By Contract
     * Precondition: This function expects no input from the user.
     * PostCondition: This function will return available quantity from the e-trader managed item. 
     * 
     */

    public int getQty(){

	synchronized(lockQty){
	    return qty;
	}

    }

    /**
     * Design By Contract
     * Precondition: This function expects an input as a integer quantity from the user.
     * PostCondition: This function will set user passed quantity.  
     * 
     */

    public void setQty(int q){

	synchronized(lockQty){
	    this.qty=q;
	}

    }


    /**
     * Design By Contract
     * Precondition: This function expects no input from the user
     * PostCondition: This function will return current balance for e-trader.
     * 
     */


    public double getBalance(){

	synchronized(lockBalance){
	    return balance;
	}
    }


    /**
     * Design By Contract
     * Precondition: This function expects an input as a balance from the user.
     * PostCondition: This function will set new balance for that particular e-trader.
     * 
     */

    public void setBalance(double bal){

	synchronized(lockBalance){

	    this.balance=Double.parseDouble(df.format(Double.valueOf(bal)));

	}

    }



} //end of ContainerManaged Class
