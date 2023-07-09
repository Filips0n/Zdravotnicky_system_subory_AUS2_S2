package structure;

import data.Patient;

import java.io.IOException;

public class StaticHashing<T extends IData<T>> extends Hashing<T> {

    public StaticHashing(int blockFactor, String fileName, int quantityOfRecords, Class<T> classType) {
        super(blockFactor, fileName, quantityOfRecords/blockFactor, classType);
        init();
    }

    //Subor uz bol vytvoreny, tak sa zavola tento konstruktor
    public StaticHashing(String fileName, Class<T> classType) {
        super(fileName, classType);
    }

    private void init(){
        try {
            if (this.file.length() <= 0) {
                this.file.write(new byte[quantityOfBlocks*blockSize+headSize]);
            }
        } catch (IOException e) {e.printStackTrace();}
    }

    public T find(T data) {
        Block<T> block = retrieveBlock((convert(data.getHash()))%quantityOfBlocks*(blockSize)+headSize);
        if (block == null) {return null;}
        if (block.getValidCount() == 0) {return null;}
        return block.getData(data);
    }

    public boolean insert(T data){
        Block<T> block = new Block<>(blockFactor, classType);
        long blockAddress = convert(data.getHash())%quantityOfBlocks*block.getSize()+headSize;
        return insertRecordFile(data, block, blockAddress);
    }

    public boolean delete(T data) {
        Block<T> block = retrieveBlock((convert(data.getHash()))%quantityOfBlocks*(blockSize)+headSize);
        if (block.getValidCount() == 0) {return false;}
        if (block.existData(data)) {
            block.removeRecord(data);
        } else {return false;}
        try {
            file.seek(convert(data.getHash())%quantityOfBlocks*block.getSize()+headSize);
            file.write(block.toByteArray());
        } catch (IOException e) {e.printStackTrace();}
        return true;
    }

    @Override
    public void updateBlock(Block<T> block, T data) {
        try {
            file.seek((convert(data.getHash()))%quantityOfBlocks*(blockSize)+headSize);
            file.write(block.toByteArray());
        } catch (IOException e) {e.printStackTrace();}
    }

    @Override
    public Block<T> getBlock(T data) {
        return retrieveBlock((convert(data.getHash()))%quantityOfBlocks*(blockSize)+headSize);
    }
}
