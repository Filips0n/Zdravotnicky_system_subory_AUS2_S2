package structure.Iterator;

import structure.tree.Node;

public interface IterableTree<E> {
    public abstract IteratorTree<Node> createInOrderIterator();
    public abstract IteratorTree<Node> createLevelOrderIterator();
}
