package structure.memento;

import data.Hospitalization;
import data.Patient;
import structure.Hashing;

public abstract class HospiCommonCommand extends CommonCommand {
    protected Hospitalization hospi;

    public HospiCommonCommand(Hashing receiver, Patient patientData, Hospitalization hospi) {
        super(receiver, patientData);
        this.hospi = hospi;
    }

    public Hospitalization getHospi() {
        return hospi;
    }
}
