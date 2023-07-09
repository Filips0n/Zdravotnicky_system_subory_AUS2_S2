package structure.memento;

import data.Hospitalization;
import data.Patient;
import structure.Hashing;

public class AddHospiCommand extends HospiCommonCommand {
    public AddHospiCommand(Hashing hashing, Patient data, Hospitalization hospi) {
        super(hashing, data, hospi);
    }
    @Override
    public void execute() {
        this.receiver.delete(patientData);
        patientData.addHospitalization(hospi);
        this.receiver.insert(patientData);
    }
}
