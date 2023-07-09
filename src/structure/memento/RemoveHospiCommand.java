package structure.memento;

import data.Hospitalization;
import data.Patient;
import structure.Hashing;

public class RemoveHospiCommand extends HospiCommonCommand {

    public RemoveHospiCommand(Hashing hashing, Patient data, Hospitalization hospi) {
        super(hashing, data, hospi);
    }

    @Override
    public void execute() {
        this.receiver.delete(patientData);
        patientData.deleteHospitalization(hospi);
        this.receiver.insert(patientData);
    }
}
