package test.interview;

import org.junit.Test;

import junit.framework.Assert;

public class ObjectCount {
	static int i;

	ObjectCount() {
		System.out.println(++i);
	}
	
	public boolean FindPair(){
		String arr[]= {"514 3455345", "514 45566234", "416873211", "514 3455345"} ;
		
		for(int i = 0 ; i < arr.length ; i++){
			for(int j = i + 1 ; j < arr.length ; j++){
				if(arr[i].equalsIgnoreCase(arr[j])){
					return true ;
				}
			}
		}
		return false ;
		
	}
	


	
	//Test the final result 
	@Test
	public void tesFindPair(){
		
		ObjectCount obj = new ObjectCount();
		boolean result = obj.FindPair();
		Assert.assertEquals(true, result);
	
	}
	//Test the final result is not null
	@Test
	public void testResultObject(){
		ObjectCount obj = new ObjectCount();
		Assert.assertTrue("The returnObject return true", obj.FindPair());
	}




	public static void main(String args[]) {
		ObjectCount oc = new ObjectCount();
		ObjectCount od = new ObjectCount();
		ObjectCount oe = new ObjectCount();
		ObjectCount of = new ObjectCount();
		ObjectCount og = new ObjectCount();
		
		boolean result = og.FindPair();
		System.out.println(result);
	}
}