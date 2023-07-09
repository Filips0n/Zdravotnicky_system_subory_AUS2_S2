package structure.memento;

import data.Hospitalization;
import data.Patient;
import structure.Hashing;

import java.time.LocalDate;

public class SetEndHospiToCommand extends HospiCommonCommand {

    private LocalDate date;
    public SetEndHospiToCommand(Hashing hashing, Patient data, Hospitalization hospi, LocalDate value) {
        super(hashing, data, hospi);
        this.date = value;
    }

    @Override
    public void execute() {
        this.receiver.delete(patientData);
        hospi.setEndOfHospitalization(date);
        this.receiver.insert(patientData);
    }

    public LocalDate getDate() {
        return date;
    }
}
