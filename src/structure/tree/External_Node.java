package structure.tree;

import java.util.BitSet;

public class External_Node extends Node {
    private long blockAddress;
    private int numOfRecords;

    public External_Node(Internal_Node parent, long blockAddress, BitSet key) {
        super(key);
        this.blockAddress = blockAddress;
        this.parent = parent;
    }

    public External_Node(BitSet key, long blockAddress, int numOfRecords) {
        super(key);
        this.blockAddress = blockAddress;
        this.numOfRecords = numOfRecords;
    }

    public int getNumOfRecords() {
        return numOfRecords;
    }
    public long getBlockAddress() {return blockAddress;}
    public Internal_Node getParent() {
        return parent;
    }

    public void setBlockAddress(long blockAddress) {
        this.blockAddress = blockAddress;
    }

    public void incrementNumOfRecords(){
        numOfRecords++;
    }
    public void decrementNumOfRecords(){
        numOfRecords--;
    }
    public void setNumOfRecords(int numOfRecords) {
        this.numOfRecords = numOfRecords;
    }

    public External_Node getBrother(){
        if (this == this.getParent().getLeftSon()) {
            if (this.getParent().getRightSon() instanceof Internal_Node) {return null;}
            return (External_Node) this.getParent().getRightSon();
        } else {
            if (this.getParent().getLeftSon() instanceof Internal_Node) {return null;}
            return (External_Node) this.getParent().getLeftSon();
        }
    }

    @Override
    public Node getLeftSon() {
        return null;
    }
    @Override
    public Node getRightSon() {
        return null;
    }

    @Override
    public String print() {
        return "e;" + this.convert(key) + ";" + blockAddress + ";" + numOfRecords + ";";
    }

    @Override
    public boolean hasLeftSon() {
        return false;
    }
    @Override
    public boolean hasRightSon() {
        return false;
    }
}
