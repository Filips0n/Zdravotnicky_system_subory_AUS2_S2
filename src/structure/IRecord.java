package structure;

import java.io.IOException;

public interface IRecord {
    byte[] toByteArray();
    void fromByteArray(byte[] bytes) throws IOException;
    int getSize();
}
