package structure.memento;

public interface ICommandReverser {
    public abstract ICommand reverseCommand(ICommand command);
}
