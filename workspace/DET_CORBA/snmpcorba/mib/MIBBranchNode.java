package snmpcorba.mib;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MIBBranchNode extends MIBNode {
	
	private ArrayList<MIBNode> children = new ArrayList<MIBNode>();
	
	private Pattern branch = Pattern.compile("^([\\w]+)(?:\\[(\\d+)\\])?\\.(.*)$");
	private Pattern leaf = Pattern.compile("^([\\w]+)(?:\\[(\\d+)\\])?\\s*=\\s*(\\d+)$");
	
	private Pattern findSeq = Pattern.compile("^(\\d+)(?:\\.(.*))?$");
	private Pattern findName = Pattern.compile("^(\\w+)(?:\\.(.*))?$");
	
	public MIBBranchNode(String name) throws MIBException {
		super(name);
	}
	
	boolean addNode(String input) throws MIBException {
		// initialize the matcher for the branch
		Matcher branchMatcher = branch.matcher(input);
		// initialize the matcher for the leaf for later
		Matcher leafMatcher = leaf.matcher(input);
		/// check if it there is more or this is the end (leaf)
		if (branchMatcher.matches()) {
			// it is the branch, save the leftover
			String leftover = branchMatcher.group(3);
			// save the name and sequence if any
			String name = branchMatcher.group(1);
			int sequence = 0;
			if (branchMatcher.group(2) != null) {
				System.out.println("Found sequence [" + branchMatcher.group(2) + "] for [" + name + "]");
				sequence = Integer.parseInt(branchMatcher.group(2));
			}
			// check if the branch node already exists
			MIBNode bNode = checkExistingMIB(name, sequence);
			// check if the node exists
			if (bNode != null) {
				if (bNode instanceof MIBBranchNode) {
					return bNode.addNode(leftover);
				} else {
					throw new MIBException("Cannot add node to a child node for [" + name + "] with data [" + leftover + "]");
				}
			}
			
			// Now create the child node
			MIBBranchNode newNode = new MIBBranchNode(name);
			// set the sequence
			if (sequence == 0) {
				children.add(newNode);
				newNode.setSequence(children.indexOf(newNode) + 1);
			} else {
				for (MIBNode child : children) {
					if (child.getSequence() == sequence) {
						return false;
					}
				}
				children.add(newNode);
				newNode.setSequence(sequence);
			}
			// success, now add the rest
			return newNode.addNode(leftover);
		} 
		// now check if the leaf matcher matches
		else if (leafMatcher.matches()) {
			// save the name and sequence if any
			String name = leafMatcher.group(1);
			int sequence = 0, target = 0;
			// extract the sequence
			if (leafMatcher.group(2) != null) {
				sequence = Integer.parseInt(leafMatcher.group(2));
			}
			target = Integer.parseInt(leafMatcher.group(3));
			
			// check if the branch node already exists
			MIBNode bNode = checkExistingMIB(name, sequence);
			// check if the node exists
			if (bNode != null) {
				if (bNode instanceof MIBLeafNode) {
					throw new MIBException("Duplicate leaf child node for [" + name + "] with target [" + target+ "]");
				} else {
					throw new MIBException("Cannot add leaf node to a child node for [" + name + "] with data [" + target + "]");
				}
			}
			// Now create the child leaf node
			MIBLeafNode newLeafNode = new MIBLeafNode(name, target);
			// set the sequence
			if (sequence == 0) {
				children.add(newLeafNode);
				newLeafNode.setSequence(children.indexOf(newLeafNode) + 1);
			} else {
				for (MIBNode child : children) {
					if (child.getSequence() == sequence) {
						return false;
					}
				}
				children.add(newLeafNode);
				newLeafNode.setSequence(sequence);
			}
			// success, now add the rest
			return true;
		} else {
			throw new MIBException("The expression [" + input + "] cannot be parsed in node");
		}
	}
	
	private MIBNode checkExistingMIB(String name, int sequence) throws MIBException {
		// check if the branch node already exists
		MIBNode bNode = null;
		if (sequence > 0) {
			System.out.println("name = [" + name + "], sequence = [" + sequence + "]");
			// see if a node exists at the right place with the right name
			for (MIBNode child : children) {
				if (child.getSequence() == sequence) {
					bNode = child;
					break;
				}
			}
			if (bNode != null) {
				if (!bNode.getName().equals(name)) {
					throw new MIBException("The node with the name [" + name + "] does not exist under the sequence + [" + sequence + "]");
				}
			}
		} else {
			// go through all the chidren and try to match the name
			for (MIBNode child : children) {
				System.out.println("name = [" + name + "], child name = [" + child.getName() + "]");
				if (child.getName().equals(name)) {
					bNode = child;
				}
			}
		}
		// check if the node exists
		return bNode;
	}

	String findNode(String input) throws MIBException {
		// setup the matchers
		Matcher seqMatcher = findSeq.matcher(input);
		Matcher nameMatcher = findName.matcher(input);
		// create a reference for the next item
		MIBNode nextItem = null;
		String leftover = null;
		// check if it is a sequence
		if (seqMatcher.matches()) {
			int seq = Integer.parseInt(seqMatcher.group(1));
			leftover = seqMatcher.group(2);
			System.out.println("Checking sequence " + seq + " with leftover " + leftover);
			for (MIBNode child : children) {
				System.out.println("Checking child: [" + child.getSequence() + "] named [" + child.getName() + "]");
				if (child.getSequence() == seq) {
					nextItem = child;
					leftover = seqMatcher.group(2);
				}
			}
		}
		// then check if it is a name
		else if (nameMatcher.matches()) {
			String name = nameMatcher.group(1);
			leftover = nameMatcher.group(2);
			for (MIBNode child : children) {
				if (child.getName().equals(name)) {
					nextItem = child;
					break;
				}
			}
		}
		// check if we managed to match the next item
		if (nextItem == null) {
			throw new MIBException("The input [" + input + "] cannot be matched to any item.");
		} else if (leftover == null && nextItem instanceof MIBBranchNode) {
			throw new MIBException("The input [" + input + "] is incomplete and cannot be matched (or target node is not a leaf node)");
		} else {
			return nextItem.findNode(leftover);
		}
	}
}
