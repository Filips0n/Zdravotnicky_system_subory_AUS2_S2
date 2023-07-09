package structure.factories;

import structure.Hashing;

public interface StructureFactory {
    Hashing createStructure(boolean fileExists, int blockFactor, String fileName, int quantityOfRecords);
}
