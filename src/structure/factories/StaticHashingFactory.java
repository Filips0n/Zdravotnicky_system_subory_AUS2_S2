package structure.factories;

import data.Patient;
import structure.Hashing;
import structure.StaticHashing;

public class StaticHashingFactory implements StructureFactory{
    @Override
    public Hashing createStructure(boolean fileExists, int blockFactor, String fileName, int quantityOfRecords) {
        if(fileExists) {
            return new StaticHashing<>(fileName, Patient.class);
        } else {
            return new StaticHashing<>(blockFactor, fileName, quantityOfRecords, Patient.class);
        }
    }
}
