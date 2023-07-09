package structure.tree;

import java.util.BitSet;

public class Internal_Node extends Node {
    Node leftSon;
    Node rightSon;

    public Internal_Node(BitSet key) {
        super(key);
    }

    public Internal_Node getParent() {
        return parent;
    }

    @Override
    public boolean hasLeftSon() {
        return leftSon != null;
    }
    @Override
    public Node getLeftSon() {
        return leftSon;
    }
    @Override
    public boolean hasRightSon() {
        return rightSon != null;
    }
    @Override
    public Node getRightSon() {
        return rightSon;
    }

    @Override
    public String print() {
        return "i;" + this.convert(key) + ";";
    }

    public void setLeftSon(Node node) {
        this.leftSon = node;
    }

    public void setRightSon(Node node) {
        this.rightSon = node;
    }
}
