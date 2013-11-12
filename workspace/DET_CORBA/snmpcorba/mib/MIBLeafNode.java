package snmpcorba.mib;

import java.util.regex.Matcher;

class MIBLeafNode extends MIBNode {

	private int target = 0;

	MIBLeafNode(String name, int target) throws MIBException {
		// call superclass constructor
		super(name);
		// set target if valid
		if (target > 0) {
			this.target = target;
		} else {
			throw new MIBException("Target parameter invalid for [" + name + "]: " + target);
		}
	}
	
	String findNode(String input) throws MIBException {
		// we reached the child node - we now return the target with the remainder as the index
		if (input == null) {
			return String.valueOf(target);
		} else {
			return target + ": " + input;
		}
	}
}
