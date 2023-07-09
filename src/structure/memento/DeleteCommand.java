package structure.memento;

import data.Patient;
import structure.Hashing;

public class DeleteCommand extends CommonCommand {
    public DeleteCommand(Hashing hashing, Patient data) {
        super(hashing, data);
    }
    @Override
    public void execute() {
        receiver.delete(patientData);
    }
}
