package structure.memento;

public class Memento implements IMemento {
    private ICommand command;

    public Memento(ICommandReverser commandReverser, ICommand commandToReverse) {
        this.command = commandReverser.reverseCommand(commandToReverse);
    }
    public void executeCommand(){
        this.command.execute();
    }
}
