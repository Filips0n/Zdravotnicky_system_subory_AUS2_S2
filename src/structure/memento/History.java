package structure.memento;

import java.util.Stack;

//Caretaker
public class History {
    private Stack<IMemento> history = new Stack<>();
    public void push(IMemento memento) {
        history.push(memento);
    }

    public IMemento pop() {
        return history.pop();
    }
    public boolean undo() {
        if (history.isEmpty()) {return false;}
        history.pop().executeCommand();
        return true;
    }
}
