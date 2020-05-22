package Model;

import Ontology.Node;

public class Visit {
	
	private Node node;
	private int loop; // rw loop where visit has made
	private int hop; // hop number when the node was visited
	
	public int getHop() {
		return hop;
	}
	public void setHop(int hop) {
		this.hop = hop;
	}
	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	public int getLoop() {
		return loop;
	}
	public void setLoop(int loop) {
		this.loop = loop;
	}

}
