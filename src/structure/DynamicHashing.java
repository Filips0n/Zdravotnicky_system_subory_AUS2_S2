package structure;

import data.Patient;
import structure.Iterator.IteratorTree;
import structure.Iterator.LevelOrderIterator;
import structure.tree.External_Node;
import structure.tree.Internal_Node;
import structure.tree.Node;
import structure.tree.Trie;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Scanner;

public class DynamicHashing<T extends IData<T>> extends Hashing<T> {
    private Trie trie = new Trie();
    private ArrayList<Long> freeBlocks = new ArrayList<>();
    private final String trieFileName;
    private final String freeBlocksFileName;

    public DynamicHashing(int blockFactor, String fileName, Class<T> classType) {
        super(blockFactor, fileName, classType);
        trieFileName = fileName+"trie.csv";
        freeBlocksFileName = fileName+"freeBlocks.txt";
        if (this.fileSize() == 0) {
            Internal_Node root = new Internal_Node(BitSet.valueOf(new long[]{0}));
            trie.setRoot(root);
            root.setLeftSon(new External_Node(root, headSize, BitSet.valueOf(new long[]{0})));
            root.setRightSon(new External_Node(root,blockSize+headSize, BitSet.valueOf(new long[]{1})));
            try {
                this.file.write(new byte[2*(blockSize)+headSize]);
            } catch (IOException e) {e.printStackTrace();}
        }
    }

    public DynamicHashing(String fileName, Class<T> classType) {
        super(fileName, classType);
        trieFileName = fileName+"trie.csv";
        freeBlocksFileName = fileName+"freeBlocks.txt";
        loadAddressTree();
        loadFreeBlocks();
    }

    @Override
    public boolean insert(T data){
        External_Node node = trie.find(data.getHash());
        if(node.getBlockAddress() == -1L) {node.setBlockAddress(getAvailableAddress());}

        Block<T> block = retrieveBlock(node.getBlockAddress());
        if(block.existData(data)){return false;}

        if (node.getNumOfRecords() >= blockFactor) {
            boolean ok = false;
            ArrayList<T> list = block.getRecords();
            list.add(data);

            while (!ok) {
                //Transformuj vrchol
                trie.splitNode(node, - 1L);

                Block<T> block1 = new Block<>(blockFactor, classType);
                Block<T> block2 = new Block<>(blockFactor, classType);
                //Preusporiadaj zaznamy v blokoch
                ok = this.rearrangeRecords(node, node.getBrother(), block1, block2 ,list);
                if (!ok) {
                    //Vsetky zaznamy sa presunu do brata
                    updateBlock(block1, node.getBlockAddress());
                    if (node.getNumOfRecords() == 0) {
                        node = node.getBrother();
                        node.setBlockAddress(getAvailableAddress());
                        updateBlock(block2, node.getBlockAddress());
                    }
                } else {
                    //Ziskaj adresu volneho bloku
                    node.getBrother().setBlockAddress(getAvailableAddress());
                    updateBlock(block1, node.getBlockAddress());
                    updateBlock(block2, node.getBrother().getBlockAddress());
                }
            }
            return true;
        } else {
            boolean inserted = block.insertRecord(data);
            boolean updated = updateBlock(block, node.getBlockAddress());
            if (updated && inserted) {node.incrementNumOfRecords();}
            return updated;
            //return writeRecordToBlock(data, node);
        }
    }

    private long getAvailableAddress() {
        long newNodeAddress;
        if (freeBlocks.size() == 0) {
            newNodeAddress = fileSize();
            try {
                this.file.setLength(fileSize()+blockSize);
                this.quantityOfBlocks++;
            } catch (IOException e) {e.printStackTrace();}
        } else {
            Collections.sort(freeBlocks);
            newNodeAddress = freeBlocks.get(0);
            freeBlocks.remove(0);
        }
        return newNodeAddress;
    }

    private boolean rearrangeRecords(External_Node node, External_Node brother, Block<T> nodeBlock, Block<T> brotherBlock, ArrayList<T> list) {
        boolean ok = false;
        if (nodeBlock != null && brotherBlock != null) {
            nodeBlock.setValidCount(0);
            brotherBlock.setValidCount(0);
            node.setNumOfRecords(0);
            brother.setNumOfRecords(0);
            for (T rec : list) {
                if (trie.getBlockAddress(rec.getHash()) == node.getBlockAddress()) {
                    nodeBlock.insertRecord(rec);
                    node.incrementNumOfRecords();
                } else {
                    brotherBlock.insertRecord(rec);
                    brother.incrementNumOfRecords();
                }
            }
            if (node.getNumOfRecords() > 0 && brother.getNumOfRecords() > 0) {
                if (node.getNumOfRecords() + brother.getNumOfRecords() == blockFactor+1) {
                    ok = true;
                }
            }
        }
        return ok;
    }

    private boolean updateBlock(Block<T> block, long address){
        try {
            file.seek(address);
            file.write(block.toByteArray());
            return true;
        } catch (IOException e) {e.printStackTrace();return false;}
    }
    @Override
    public T find(T data) {
        long blockAddress = trie.getBlockAddress(data.getHash());
        if(blockAddress == -1L) {return null;}
        Block<T> block = retrieveBlock(blockAddress);
        if (block == null) {return null;}
        if (block.getValidCount() == 0) {return null;}
        return block.getData(data);
    }

    @Override
    public boolean delete(T data) {
        External_Node node = trie.find(data.getHash());
        if (!removeRecordFromBlock(data, node)) {return false;}
        node.decrementNumOfRecords();
        if (node.getBrother() == null) {
            if (node.getNumOfRecords() == 0) {
                freeBlock(node);
            }
            return true;
        }
        if ((node.getNumOfRecords() + node.getBrother().getNumOfRecords() <= blockFactor)) {
            boolean cannotMerge = false;
            while (!cannotMerge) {
                if (node.getParent() == trie.getRoot()) {return true;}//Nemerguj posledne 2 externe nody
                if ((node.getBlockAddress() > node.getBrother().getBlockAddress())
                        && (node.getBrother().getBlockAddress() != -1L)) {node = node.getBrother();}
                if (node.getBrother().getBlockAddress() != -1L) {
                    mergeRecords(node);
                }
                if ((node.getBrother().getNumOfRecords() == 0) &&
                        (node.getBrother().getBlockAddress() != -1L)) {
                    freeBlock(node.getBrother());
                }
                trie.mergeNode(node);
                if (node.getBrother() == null ||
                        (node.getNumOfRecords() + node.getBrother().getNumOfRecords() > blockFactor)) {
                    cannotMerge = true;
                }
            }
        }
        if (node.getNumOfRecords() == 0) {
            freeBlock(node);
        }
        return true;
    }

    @Override
    public void updateBlock(Block<T> block, T pat) {
        updateBlock(block, trie.getBlockAddress(pat.getHash()));
    }

    @Override
    public Block<T> getBlock(T data) {
        long blockAddress = trie.getBlockAddress(data.getHash());
        if(blockAddress == -1L) {return null;}
        Block<T> block = retrieveBlock(blockAddress);
        if (block == null || block.getValidCount() == 0) {return null;}
        return block;
    }

    private void freeBlock(External_Node node) {
        if (fileSize() - blockSize <= node.getBlockAddress()) {
            try {
                file.setLength(fileSize() - blockSize);
                this.quantityOfBlocks--;
                deleteFreeBlocks();
            } catch (IOException e) {e.printStackTrace();}
        } else {
            freeBlocks.add(node.getBlockAddress());
        }
        node.setBlockAddress(-1L);
        node.setNumOfRecords(0);
    }

    private void deleteFreeBlocks() {
        if (freeBlocks.size() == 0) {return;}
        Collections.sort(freeBlocks);
        while (freeBlocks.get(freeBlocks.size()-1) >= (fileSize() - blockSize)){
            try {
                file.setLength(fileSize() - blockSize);
                this.quantityOfBlocks--;
                freeBlocks.remove(freeBlocks.size()-1);
                if (freeBlocks.size() == 0) {return;}
            } catch (IOException e) {e.printStackTrace();}
        }
    }

    private boolean removeRecordFromBlock(T data, External_Node node) {
        Block<T> block = retrieveBlock(node.getBlockAddress());
        return removeRecordFile(data, node, block);
    }

    private void mergeRecords(External_Node node) {
        Block<T> block1 = retrieveBlock(node.getBlockAddress());
        Block<T> block2 = retrieveBlock(node.getBrother().getBlockAddress());
        if (block1 != null && block2 != null) {
            ArrayList<T> list = block2.getRecords();
            block2.setValidCount(0);
            node.getBrother().setNumOfRecords(0);
            for (T rec : list) {
                block1.insertRecord(rec);
                node.incrementNumOfRecords();
            }
            updateBlock(block1, node.getBlockAddress());
            updateBlock(block2, node.getBrother().getBlockAddress());
        }
    }

    private boolean removeRecordFile(T data, External_Node node, Block<T> block) {
        if (block.getValidCount() == 0) {return false;}
        if (block.existData(data)) {
            block.removeRecord(data);
        } else {return false;}
        try {
            file.seek(node.getBlockAddress());
            file.write(block.toByteArray());
        } catch (IOException e) {e.printStackTrace();}
        return true;
    }

    private void loadAddressTree() {
        File trieFile = new File(trieFileName);
        try {
            Scanner sc = new Scanner(trieFile);
            while(sc.hasNext()){
                String readLine = sc.nextLine();
                Scanner rowSc = new Scanner(readLine);
                rowSc.useDelimiter(";");
                ArrayList<String> dataList = new ArrayList<>();
                while (rowSc.hasNext()) {
                    dataList.add(rowSc.next());
                }
                Node node;
                if (dataList.get(0).equals("i")) {
                    node = new Internal_Node(BitSet.valueOf(new long[]{Long.parseLong(dataList.get(1))}));
                } else {
                    node = new External_Node(BitSet.valueOf(new long[]{Long.parseLong(dataList.get(1))}), Long.parseLong(dataList.get(2)), Integer.parseInt(dataList.get(3)));
                }
                trie.insert(node);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        ArrayList<Node> allNodes = trie.levelOrder();
//        for (Node node : allNodes) {
//            System.out.println((node.print()));
//        }
    }
    private void loadFreeBlocks() {
        File freeBlocksFile = new File(freeBlocksFileName);
        try {
            for(Scanner sc = new Scanner(freeBlocksFile); sc.hasNext(); ) {
                String readLine = sc.nextLine();
                freeBlocks.add(Long.parseLong(readLine));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveAddressTree() throws FileNotFoundException, UnsupportedEncodingException {
//        ArrayList<Node> allNodes = trie.levelOrder();
//        PrintWriter writer = new PrintWriter(trieFileName, "UTF-8");
//        for (Node node : allNodes) {
//            writer.println(node.print());
//        }
//        writer.close();
        PrintWriter writer = new PrintWriter(trieFileName, "UTF-8");
        IteratorTree<Node> iterator = trie.createLevelOrderIterator();
        while (iterator.hasNext()) {
            writer.println(iterator.next().print());
        }
        writer.close();
    }
    private void saveFreeBlocks() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(freeBlocksFileName, "UTF-8");
        for (Long freeBlock : freeBlocks) {
            writer.println(freeBlock);
        }
        writer.close();
    }

    public void endDynamicHashing() {
        endHashing();
        try {
            saveAddressTree();
            saveFreeBlocks();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {e.printStackTrace();}
    }

    public void printAllBrothers(){
        for (Node node :
                trie.levelOrder()) {
            if (node instanceof External_Node) {
                System.out.print(((External_Node) node).getBlockAddress());
                if (((External_Node) node).getBrother() == null) {
                    System.out.println(" - No Brother");
                } else {
                    System.out.println(" - " + ((External_Node) node).getBrother().getBlockAddress()); ;
                }
            }
        }
    }
}
