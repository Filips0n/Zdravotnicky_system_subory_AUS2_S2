package structure;

import java.util.BitSet;

public interface IData<T> extends IRecord {
    BitSet getHash();
    boolean myEquals(T Data);
    String print();
    String getId();
}