
public class IntTree {

    //Define a subtree
     private static class IntTreeNode{
	  
	 private int data ;
	 private IntTreeNode leftLink ;
	 private IntTreeNode rightLink ;
	 
	 public IntTreeNode(int newData, IntTreeNode newLeftLink, IntTreeNode newRightLink ) {
	     
	     this.data = newData ;
	     this.leftLink = newLeftLink ;
	     this.rightLink = newRightLink ;
	     
	     
	 }
     } // End of IntTreeNode inner class
     
     // Root reference to intTreeNode
     private IntTreeNode root ;
     
     //Define the constructor for the IntTree
     
     public IntTree() {
	 
	 root = null ;
	 
     }
     
     public void add(int item, IntTreeNode subTreeRoot) {
	 
	//Check is there any item already exist
	 if(root == null) {
	     
	     root = new IntTreeNode(item, null, null);
	     
	 }
	 else
	 {
	   //Insert in the left side
		 if(root.leftLink == null) {
		     subTreeRoot = new IntTreeNode(item, null, null);
			 root.leftLink = subTreeRoot ;
		 }
		 
		 else
		 {
		   //Now Right link points some node
		   //Right link is not null
		    if( item < root.leftLink.data) {
			//Insert in the left side
			// 2 cases, 
			
			if(subTreeRoot.leftLink == null) {
			    IntTreeNode newPointer = new IntTreeNode(item, null, null);
			    subTreeRoot = newPointer  ;
			}
			else
			{
			    // subTreeRoot.leftLink != null
			    // There is node exist
			    if(item < subTreeRoot.leftLink.data) {
				IntTreeNode newPointer = new IntTreeNode(item, null, null);
				subTreeRoot.leftLink = newPointer  ;
				
			    }
			    //Insert right side
			    else {
				IntTreeNode newPointer = new IntTreeNode(item, null, null);
				subTreeRoot.rightLink = newPointer  ;
			    }
			    
			}
		    }
		     
		 }
		 
		 
		 
		 
	     
	     
	     
	    
	     
	     
	     
	     // if ( item > root.data)
	     // Root's right side.
	     else {
	     
		 //Insert in the right side
		 if(root.rightLink == null) {
		     subTreeRoot = new IntTreeNode(item, null, null);
			 root.rightLink = subTreeRoot ;
		 }
		 
		 else
		 {
		   //Now Right link points some node
		   //Right link is not null
		    if( item < root.rightLink.data) {
			//Insert in the left side
			// 2 cases, 
			
			if(subTreeRoot.leftLink == null) {
			    IntTreeNode newPointer = new IntTreeNode(item, null, null);
			    subTreeRoot = newPointer  ;
			}
			else
			{
			    // subTreeRoot.leftLink != null
			    // There is node exist
			    if(item < subTreeRoot.leftLink.data) {
				IntTreeNode newPointer = new IntTreeNode(item, null, null);
				subTreeRoot.leftLink = newPointer  ;
				
			    }
			    
			
			    //Insert right side
			    else {
				IntTreeNode newPointer = new IntTreeNode(item, null, null);
				subTreeRoot.rightLink = newPointer  ;
			    }
			    
			}
		    }
		     
		 
		 
		 
	     
	 
	 

     
     public boolean contains(int item) {
	 
	 return true ;
     }
     
     public void showElements() {
	 
     }
     
     private static IntTreeNode insertInSubTree(int item) {
	 
	 
	 return reference of subtreenode
     }
}
