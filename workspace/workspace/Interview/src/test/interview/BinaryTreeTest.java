package test.interview;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * 
 * @author Muttabiur
 * @Comment : Binary search tree to find sum of the most common integer
 */

public class BinaryTreeTest {

	public int returnValue = 0;

	// Tree Node definition
	static class Node {

		Node left = null;
		Node right = null;
		int value;

		public Node(int value) {
			this.value = value;
		}
	}



	/**
	 * Insert function for binary search tree
	 * 
	 * @param node
	 * @param value
	 */
	public void insert(Node node, int value) {

		if (value == node.value) {
			System.out.println("The most common value is: " + value + " "
					+ node.value);
			returnValue = returnValue + node.value;
			if (node.right != null) {
				insert(node.right, value);
			}

			System.out
					.println("The Sum of Most Common Integers:" + returnValue);
		}

		if (value < node.value) {
			if (node.left != null) {
				insert(node.left, value);
			} else {
				System.out.println("  Inserted " + value + " to left of "
						+ node.value);
				node.left = new Node(value);
			}
		} else if (value > node.value) {
			if (node.right != null) {
				insert(node.right, value);
			} else {
				System.out.println("  Inserted " + value + " to right of "
						+ node.value);
				node.right = new Node(value);
			}
		}

	}


	// Sample test case showing the expected value
	@Test
	public void testreturnValue() {

		Node root = new Node(1);

		System.out.println("Building tree with root value " + root.value);
		insert(root, 2);
		insert(root, 1);
		insert(root, 3);
		insert(root, 1);

		// Define Expect value
		int expected = 3;

		assertEquals(expected, returnValue);

	} // End of test

} // End of Class definition
