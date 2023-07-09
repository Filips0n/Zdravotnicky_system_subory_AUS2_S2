package structure.Iterator;

import structure.tree.Node;

public class InOrderIterator<E extends Node> implements IteratorTree<E> {
    private E nextNode;

    public InOrderIterator(E root) {
        this.nextNode = root;
        while (nextNode.getLeftSon() != null) {
            nextNode = (E) nextNode.getLeftSon();
        }
    }

    @Override
    public boolean hasNext() {
        return nextNode != null;
    }

    @Override
    public E next() {
        if (nextNode == null) return null;

        if (nextNode.getRightSon() != null) {
            nextNode = (E) nextNode.getRightSon();
            while (nextNode.getLeftSon() != null) {
                nextNode = (E) nextNode.getLeftSon();
            }
            return nextNode;

        }

        Node parent = nextNode.getParent();
        while(parent != null && nextNode == parent.getRightSon()){
            nextNode = (E) parent;
            parent = parent.getParent();
        }
        this.nextNode = (E) parent;
        return this.nextNode;
    }
}
