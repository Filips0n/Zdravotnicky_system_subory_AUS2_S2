package structure.Iterator;

import structure.tree.Node;

import java.util.LinkedList;
import java.util.Queue;

public class LevelOrderIterator<E extends Node> implements IteratorTree<E> {
    Queue<E> queue;
    public LevelOrderIterator(E root) {
        queue = new LinkedList<>();
        queue.add(root);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public E next() {
        E node = queue.poll();
        if (node.getLeftSon() != null) {
            queue.add((E) node.getLeftSon());
        }
        if (node.getRightSon() != null) {
            queue.add((E)node.getRightSon());
        }
        return node;
    }
}
