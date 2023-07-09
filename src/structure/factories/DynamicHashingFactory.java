package structure.factories;

import data.Patient;
import structure.DynamicHashing;
import structure.Hashing;

public class DynamicHashingFactory implements StructureFactory{
    @Override
    public Hashing createStructure(boolean fileExists, int blockFactor, String fileName, int quantityOfRecords) {
        if(fileExists) {
            return new DynamicHashing<>(fileName, Patient.class);
        } else {
            return new DynamicHashing<>(blockFactor, fileName, Patient.class);
        }
    }
}
