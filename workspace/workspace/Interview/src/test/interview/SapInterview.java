package test.interview;

public class SapInterview {

	// Given a string how to determine it is an integer
	public boolean checkInteger(String str) {

		int howManyInt = 0;
		boolean mark = false;
		char exract;

		if ((str == null) || (str.isEmpty()))
			return mark;

		for (int i = 0; i < str.length(); i++) {

			exract = str.charAt(i);

			// Positive and negative number
			// less than 0 is negative number. start with -
			if ((exract <= '0') || (exract <= '9')) {
				mark = true;
				howManyInt++;
				System.out.println("The Char is:" + exract);
				System.out.println("");
				System.out.println("How Many Integer:" + howManyInt);

			}// end of if

		} // end of for
		return mark;
	}

	public void reverseArray() {

		int arr[] = { 2, 4, 6, 7, 8, 0 };

		for (int i = 0; i < arr.length / 2; i++) {
			int temp = arr[arr.length - 1 - i];
			arr[arr.length - 1 - i] = arr[i];
			arr[i] = temp;
		}

		// display the array
		for (int i = 0; i < arr.length; i++) {
			System.out.println(arr[i]);
		}
		
		for(int i : arr){
			System.out.println(arr[i]);
		}
		
	}

	//Reverse the string using c++ pointer
	public void reverseString(){
		
	}
	public void sortArrayAsc() {

		int arr[] = { 23, 0, 76, 3, 76, 9 };
		
		for (int i = 0; i < arr.length; i++) {
			for (int j = i + 1; j < arr.length; j++) {
		         //> means ascending
				if (arr[i] > arr[j]) {
					int temp = arr[j];
					arr[j] = arr[i];
					arr[i] = temp;
				}
			}
		}

		// Display the ascending array
		for (int i = 0; i < arr.length; i++) {
			System.out.println("The Ascending Array is:" + arr[i]);
		}
	}

	public void sortArrayDesc() {

		int arr[] = { 23, 0, 76, 3, 76, 9 };
		
		for (int i = 0; i < arr.length; i++) {
			for (int j = i + 1; j < arr.length; j++) {
		         //> means ascending
				if (arr[i] < arr[j]) {
					int temp = arr[j];
					arr[j] = arr[i];
					arr[i] = temp;
				}
			}
		}

		// Display the ascending array
		for (int i = 0; i < arr.length; i++) {
			System.out.println("The Descending Array is:" + arr[i]);
		}
	}

	
	public void copyArray(){
		int arr[] = {2, 4, 6, 7, 8,9};
		int arr1[] = {} ;
		
		for(int i = 0 ; i <arr.length ; i++){
			arr1[i] = arr[i];
			
		}
		
		for(int j = 0 ; j < arr1.length ; j++){
			System.out.println("The copied array is:" + arr1[j]);
		}
		
	}
	
	//
	/**
	public void bfs()
	{
		//BFS uses Queue data structure
		Queue q=new LinkedList();
		q.add(this.rootNode);
		printNode(this.rootNode);
		rootNode.visited=true;
		while(!q.isEmpty())
		{
			Node n=(Node)q.remove();
			Node child=null;
			while((child=getUnvisitedChildNode(n))!=null)
			{
				child.visited=true;
				printNode(child);
				q.add(child);
			}
		}
		//Clear visited property of nodes
		clearNodes();
	}
	
	
	*/
	
	
	public static void main(String args[]) {

		SapInterview sap = new SapInterview();
		boolean returnVal = false;
		returnVal = sap.checkInteger("hello=9");
		System.out.println(returnVal);

		System.out.println("##############Reverse the arrayS###################");
		sap.reverseArray();

		System.out.println("#####################The Ascending Array is:########");
		sap.sortArrayAsc();
		
		System.out.println("#####################The Decending Array is:########");
		sap.sortArrayDesc();
		
		sap.copyArray();
	}
}
