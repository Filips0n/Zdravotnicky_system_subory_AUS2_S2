package structure.tree;

import structure.Iterator.InOrderIterator;
import structure.Iterator.IterableTree;
import structure.Iterator.IteratorTree;
import structure.Iterator.LevelOrderIterator;

import java.util.*;

public class Trie implements IterableTree<BitSet> {
    private Internal_Node root = null;
    private int size = 3;

    public ArrayList<Node> levelOrder() {
        if (root == null) {return null;}
        ArrayList<Node> arrayList = new ArrayList<>(size);
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);

        while(!queue.isEmpty()){
            Node head = queue.peek();
            queue.remove();
            arrayList.add(head);
            if(head.hasLeftSon()) { queue.add(head.getLeftSon());}
            if(head.hasRightSon()) { queue.add(head.getRightSon());}
        }
        return arrayList;
    }

    public boolean insert(Node node) {
        if (root == null){
            root = (Internal_Node) node;
        } else {
            Node parentNode = null;
            Node currentNode = root;

            int i = 0;
            while (currentNode != null){
                parentNode = currentNode;
                if ((node.getKey().get(i) ? 1 : 0) == 0){
                    currentNode = currentNode.getLeftSon();
                } else {
                    currentNode = currentNode.getRightSon();
                }
                i++;
            }

            node.setParent((Internal_Node) parentNode);
            if((node.getKey().get(i-1) ? 1 : 0) == 0){
                ((Internal_Node) parentNode).setLeftSon(node);
            } else {
                ((Internal_Node) parentNode).setRightSon(node);
            }
        }
        size++;
        return true;
    }

    public External_Node find(BitSet recordHash) {
        if (root == null) {return null;}
        Node currentNode = root;
        int i = 0;
        while (currentNode instanceof Internal_Node){
            if ((recordHash.get(i) ? 1 : 0) == 0){
                if (currentNode.hasLeftSon()){
                    currentNode = currentNode.getLeftSon();
                }
            } else if ((recordHash.get(i) ? 1 : 0) == 1){
                if (currentNode.hasRightSon()){
                    currentNode = currentNode.getRightSon();
                }
            }
            i++;
        }
        return (External_Node) currentNode;
    }

    public long getBlockAddress(BitSet recordHash) {
        return find(recordHash).getBlockAddress();
    }

    public Internal_Node getRoot() {
        return root;
    }

    public int getSize() {
        return size;
    }

    public void setRoot(Internal_Node root) {
        this.root =root;
    }

    public void mergeNode(External_Node node) {
        Internal_Node parent = node.getParent();
        node.setParent(parent.getParent());
        parent.setParent(null);
        if (node.getParent().getLeftSon() == parent) {
            node.getParent().setLeftSon(node);
        } else {
            node.getParent().setRightSon(node);
        }
        size -= 2;
    }
    public void splitNode(External_Node node, long newNodeAddress) {
        Internal_Node newParent = new Internal_Node(node.getKey());
        BitSet brotherKey = (BitSet) node.getKey().clone();
        brotherKey.set(getLevel(node)-1);
        External_Node newBrother = new External_Node(newParent, newNodeAddress, brotherKey);
        if (node == node.getParent().getLeftSon()) {
            node.getParent().setLeftSon(newParent);
        } else {
            node.getParent().setRightSon(newParent);
        }
        newParent.setParent(node.getParent());
        node.setParent(newParent);
        newParent.setLeftSon(node);
        newParent.setRightSon(newBrother);
        size += 2;
    }

    private int getLevel(Node node) {
        if (node == null) {return 0;}
        int level = 1;
        while(node.getParent() != null){
            node = node.getParent();
            level++;
        }
        return  level;
    }

    //------------------------NV-------------------------//
    @Override
    public IteratorTree<Node> createInOrderIterator() {
        return new InOrderIterator<>(root);
    }
    //------------------------NV-------------------------//
    @Override
    public IteratorTree<Node> createLevelOrderIterator() {
        return new LevelOrderIterator<>(root);
    }
}
