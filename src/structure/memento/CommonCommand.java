package structure.memento;

import data.Patient;
import structure.Hashing;

public abstract class CommonCommand implements ICommand {
    protected Hashing receiver;
    protected Patient patientData;

    public CommonCommand(Hashing receiver, Patient patientData) {
        this.receiver = receiver;
        this.patientData = patientData;
    }

    public Hashing getReceiver() {
        return receiver;
    }

    public Patient getPatientData() {
        return patientData;
    }
}
