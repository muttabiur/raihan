package snmpcorba.mib;

import java.util.regex.Pattern;

class MIBNode {
	
	protected String name = null;
	
	protected int sequence = -1;
	
	MIBNode(String name) throws MIBException {
		// check calling parameters
		if (name.equals("_ROOT_")) {
			System.out.println("Creating root MIB node");
			return;
		}
		if (name == null || name.length() == 0)  {
			throw new MIBException("Bad arguments to MIB Node: [name: " + name + "]");
		}
		this.name = name;
	}

	void setSequence(int indexOf) {
		sequence = indexOf;
	}

	int getSequence() {
		return sequence;
	}

	boolean addNode(String input) throws MIBException {
		// not implemented, should be called by superclass
		throw new MIBException("MIBNode.addNode not implemented! Make sure to use subclass methods.");
	}

	String findNode(String input) throws MIBException {
		// not implemented, should be called by superclass
		throw new MIBException("MIBNode.findNode not implemented! Make sure to use subclass methods.");
	}

	public String getName() {
		return name;
	}
}