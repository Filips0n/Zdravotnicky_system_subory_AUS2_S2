package structure.memento;

public final class CommandReverser implements ICommandReverser {
    private static CommandReverser instance;
    private CommandReverser() {}

    @Override
    public ICommand reverseCommand(ICommand command) {
        if (command instanceof InsertCommand) {
            return new DeleteCommand(((InsertCommand) command).getReceiver(), ((InsertCommand) command).getPatientData());
        } else if (command instanceof DeleteCommand) {
            return new InsertCommand(((DeleteCommand) command).getReceiver(), ((DeleteCommand) command).getPatientData());
        } else if (command instanceof AddHospiCommand) {
            return new RemoveHospiCommand(((AddHospiCommand) command).getReceiver(), ((AddHospiCommand) command).getPatientData(), ((AddHospiCommand) command).getHospi());
        } else if (command instanceof RemoveHospiCommand) {
            return new AddHospiCommand(((RemoveHospiCommand) command).getReceiver(), ((RemoveHospiCommand) command).getPatientData(), ((RemoveHospiCommand) command).getHospi());
        } else if (command instanceof SetEndHospiToCommand) {
            return new SetEndHospiToCommand(((SetEndHospiToCommand) command).getReceiver(), ((SetEndHospiToCommand) command).getPatientData(), ((SetEndHospiToCommand) command).getHospi(),  ((SetEndHospiToCommand) command).getHospi().getEndOfHospitalization());
        }
        return null;
    }

    public static CommandReverser getInstance() {
        if (instance == null) {
            instance = new CommandReverser();
        }
        return instance;
    }
}
