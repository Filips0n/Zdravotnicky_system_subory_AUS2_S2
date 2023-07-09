package structure.memento;

import data.Patient;
import structure.Hashing;

public class InsertCommand extends CommonCommand {
    public InsertCommand(Hashing hashing, Patient data) {
        super(hashing, data);
    }
    @Override
    public void execute() {
        receiver.insert(patientData);
    }
}
