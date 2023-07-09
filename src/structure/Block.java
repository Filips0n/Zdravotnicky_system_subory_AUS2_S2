package structure;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Block<T extends IData<T>> implements IRecord{

    private final int blockFactor;
    private int validCount;
    private final int headSize;
    private ArrayList<T> records;
    private final Class<T> classType;

    public Block(int blockFactor, Class<T> classType) {
        this.blockFactor = blockFactor;
        this.classType = classType;

        this.records = new ArrayList<>(this.blockFactor);
        for (int i = 0; i < this.blockFactor; i++) {
            try {
                this.records.add(classType.newInstance());//asi netreba createInstance
            } catch (InstantiationException | IllegalAccessException e) {e.printStackTrace();}
        }
        headSize = Integer.BYTES;
    }

    public boolean insertRecord(T record){
        if (isFree() && !existData(record)) {
            records.set(validCount, record);
            validCount++;
            return true;
        }
        return false;
    }

    public void removeRecord(T data) {
        for (int i = 0; i < blockFactor; i++) {
            if (records.get(i).myEquals(data)) {
                records.set(i, records.get(validCount-1));
                validCount--;
                return;
            }
        }
    }

    public String printBlock() {
        StringBuilder out = new StringBuilder();
        out.append("Valid count: ").append(validCount).append("\n");
        for (int i = 0; i < records.size(); i++) {
            if (i >= validCount) {
                out.append("Neplatny zaznam\n");
            } else {
                out.append(records.get(i).print());
            }
        }
        return out.toString();
    }

    public ArrayList<String> listAllBlockIds() {
        ArrayList<String> ids = new ArrayList<>();
        for (int i = 0; i < validCount; i++) {
            ids.add(records.get(i).getId());
        }
        return ids;
    }

    public void setValidCount(int validCount) {
        this.validCount = validCount;
    }

    public boolean isFree() {
        return validCount < blockFactor;
    }

    public boolean existData(T data) {
        for (int i = 0; i < validCount; i++) {
            if (data.myEquals(records.get(i))) {
                return true;
            }
        }
        return false;
    }

    public int getValidCount() {
        return validCount;
    }

    public ArrayList<T> getRecords() {
        ArrayList<T> list = new ArrayList<>(validCount);
        for (int i = 0; i < validCount; i++) {
            list.add(records.get(i));
        }
        return list;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(hlpByteArrayOutputStream);

        try {
            out.writeInt(validCount);
            for (int i = 0; i < blockFactor; i++) {
                out.write(records.get(i).toByteArray());
            }
            return hlpByteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion to byte array.");
        }
    }

    @Override
    public void fromByteArray(byte[] bytes) throws IOException {
        DataInputStream inp = new DataInputStream(new ByteArrayInputStream(bytes));
        validCount = inp.readInt();

        for (int i = 0; i < blockFactor; i++) {
            int sizeOfRecord = records.get(i).getSize();
            byte[] n = Arrays.copyOfRange(bytes, headSize + (i * sizeOfRecord), headSize + ((i+1)*sizeOfRecord));
            records.get(i).fromByteArray(n);
        }
    }

    @Override
    public int getSize() {
        try {
            return classType.newInstance().getSize() * blockFactor + headSize;
        } catch (InstantiationException | IllegalAccessException e) {e.printStackTrace();}
        return 0;
    }

    public T getData(T data) {
        for (int i = 0; i < validCount; i++) {
            if (data.myEquals(records.get(i))) {
                return records.get(i);
            }
        }
        return null;
    }
}
