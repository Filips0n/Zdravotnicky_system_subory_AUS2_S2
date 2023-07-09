package structure;

import data.Patient;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;

public abstract class Hashing<T extends IData<T>> {

    protected int blockFactor;
    protected RandomAccessFile file;
    protected File originalFile;
    protected int quantityOfBlocks;
    protected final Class<T> classType;
    protected final int headSize = Integer.BYTES;
    protected final String defaultFileName = "patients.dat";
    protected int blockSize;

    public Hashing(int blockFactor, String fileName, int quantityOfBlocks, Class<T> classType) {
        if (fileName.isEmpty()) {fileName = defaultFileName;}
        this.blockFactor = blockFactor;
        this.quantityOfBlocks = quantityOfBlocks;
        this.classType = classType;
        this.blockSize = new Block<T>(blockFactor, classType).getSize();
        try {
            this.originalFile = new File(fileName);
            this.file = new RandomAccessFile(originalFile, "rw");
        } catch (FileNotFoundException e) {e.printStackTrace();}
    }

    public Hashing(int blockFactor, String fileName, Class<T> classType) {
        if (fileName.isEmpty()) {fileName = defaultFileName;}
        this.blockFactor = blockFactor;
        this.quantityOfBlocks = 2;
        this.blockSize = new Block<T>(blockFactor, classType).getSize();
        this.classType = classType;
        try {
            this.originalFile = new File(fileName);
            this.file = new RandomAccessFile(originalFile, "rw");
        } catch (FileNotFoundException e) {e.printStackTrace();}
    }

    //Subor uz bol vytvoreny, tak sa zavola tento konstruktor
    public Hashing(String fileName, Class<T> classType) {
        if (fileName.isEmpty()) {fileName = defaultFileName;}
        this.originalFile = new File(fileName);
        this.classType = classType;//Ukladat si ClassType???
        try {
            if(this.originalFile.exists() && !this.originalFile.isDirectory()) {
                this.file = new RandomAccessFile(originalFile, "rw");
                //Nacitanie BlockFactoru
                byte[] bytes = new byte[headSize];
                try {
                    file.seek(0);
                    file.read(bytes);
                    DataInputStream inp = new DataInputStream(new ByteArrayInputStream(bytes));
                    blockFactor = inp.readInt();
                    this.blockSize = new Block<T>(blockFactor, classType).getSize();
                    this.quantityOfBlocks = (int)((fileSize()-headSize)/blockSize);
                } catch (IOException e) {e.printStackTrace();}
            }
        } catch (FileNotFoundException e) {e.printStackTrace();}
    }

    public void endHashing(){
        try {
            file.seek(0);
            file.writeInt(blockFactor);
            file.close();
        } catch (IOException e) {e.printStackTrace();}
    }

    protected long convert(BitSet hash) {
        return convertDynamic(hash, hash.length());
    }

    protected long convertDynamic(BitSet hash, int length) {
        long hashValue = 0L;
        for (int i = 0; i < length; i++) {
            hashValue += hash.get(i) ? (1L << i) : 0L;
        }
        return hashValue;
    }

    protected boolean insertRecordFile(T data, Block<T> block, long blockAddress) {
        byte[] bytes = new byte[block.getSize()];
        try {
            file.seek(blockAddress);
            file.read(bytes);
            block.fromByteArray(bytes);
            if(!block.insertRecord(data)){return false;}
            file.seek(blockAddress);
            file.write(block.toByteArray());
        } catch (IOException e) {e.printStackTrace();}
        return true;
    }

    public String printAllBlocks(){
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < quantityOfBlocks; i++) {
            Block<T> block = new Block<T>(blockFactor, classType);
            byte[] bytes = new byte[block.getSize()];
            long blockAddress = (long) i *block.getSize()+headSize;
            try {
                file.seek(blockAddress);
                file.read(bytes);
                block.fromByteArray(bytes);
                out.append("************************").append("\n");
                out.append("Adresa bloku: "+ blockAddress+ " ");
                out.append("Index bloku: "+ i+ " ");
                out.append(block.printBlock()).append("\n");
            } catch (IOException e) {e.printStackTrace();}
        }
        return out.toString();
    }

    public ArrayList<String> listAllIds(){
        ArrayList<String> ids = new ArrayList<>();

        for (int i = 0; i < quantityOfBlocks; i++) {
            Block<T> block = new Block<T>(blockFactor, classType);
            byte[] bytes = new byte[block.getSize()];
            long hashAddress = (long) i *block.getSize()+headSize;
            try {
                file.seek(hashAddress);
                file.read(bytes);
                block.fromByteArray(bytes);
                ids.addAll(block.listAllBlockIds());
            } catch (IOException e) {e.printStackTrace();}
        }
        return ids;
    }

    public void fileDelete(){
        try {
            file.close();
        } catch (IOException e) {e.printStackTrace();}
        originalFile.delete();
    }

    protected Block<T> retrieveBlock(long address) {
        Block<T> block = new Block<T>(blockFactor, classType);

        //Load DATA to block
        byte[] bytes = new byte[block.getSize()];
        try {
            file.seek(address);
            file.read(bytes);
            block.fromByteArray(bytes);
        } catch (IOException e) {e.printStackTrace();}
        return block;
    }

    public long fileSize(){
        try {
            return file.length();
        } catch (IOException e) {e.printStackTrace();}
        return -1;
    }

    public abstract boolean insert(T data);
    public abstract T find(T data);
    public abstract boolean delete(T data);

    public abstract void updateBlock(Block<T> block, T pat);

    public abstract Block<T> getBlock(T data);
}
