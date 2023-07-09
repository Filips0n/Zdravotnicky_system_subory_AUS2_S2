package structure.tree;

import java.util.BitSet;

public abstract class Node {
    protected Internal_Node parent;
    protected BitSet key;

    public Node(BitSet key) {
        this.key = key;
    }

    public BitSet getKey() {
        return key;
    }

    public abstract boolean hasLeftSon();

    public abstract Node getLeftSon();

    public abstract boolean hasRightSon();

    public abstract Node getRightSon();

    public void setParent(Internal_Node parentNode) {
        parent = parentNode;
    }

    public abstract String print();

    protected long convert(BitSet hash) {
        long hashValue = 0L;
        for (int i = 0; i < hash.length(); i++) {
            hashValue += hash.get(i) ? (1L << i) : 0L;
        }
        return hashValue;
    }

    public Internal_Node getParent() {
        return parent;
    }
}
