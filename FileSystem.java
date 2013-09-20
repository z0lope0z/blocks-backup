import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

//Formating Partition D
//        Partition size :14
//        Available for Inodes and Datablocks :10
//        Inodes : 2 Datanodes : 8
//
//        6 - Bootblock
//        7 - Superblock
//        8 - Freespace offset : 12 (where it starts)
//        9 - Root Directory
//        10 - Inode
//        11 - Inode
//        12 - Data
//        13 - Data
//        <continued until 19>
class FileSystem {
    static int blocks = 21;
    Block[] HD = new Block[blocks];
    int hdCounter = 0; // number for current free index
    int maxPartitions = 3;
    
    public FileSystem(){
        MBRBlock mbr = new MBRBlock();
        HD[0] = mbr;
        hdCounter++;
    }
    
    public void addBlock(Block block){
        HD[hdCounter] = block;
        hdCounter++;
    }
    
    public void createPartition(int blockSize){
        System.out.println("Available blocks: " + getFreeSpace());
        System.out.println("Creating partitions with size " + blockSize);
        if (getFreeSpace() < blockSize) {
            System.out.println("No more free space..");
            return;
        }
        PartitionListBlock newPartitionListBlock;
        PartitionListBlock lastPartitionListBlock = getLastPartitionListBlock();
        if (lastPartitionListBlock == null){
            newPartitionListBlock = new PartitionListBlock(maxPartitions + 1, blockSize);
        } else {
            newPartitionListBlock = new PartitionListBlock(getLastPartitionListBlock(), blockSize); 
        }
        HD[hdCounter] = newPartitionListBlock;
        hdCounter++;
        setPartition(newPartitionListBlock);
    }
    
    private void setPartition(PartitionListBlock partitionListBlock){
        for (int i=partitionListBlock.startBlock; i<=partitionListBlock.endBlock; i++){
            HD[i] = partitionListBlock.getBlock(i);
        }
    } 
    
    public void showPartitions(){
        //System.out.println("Available blocks: " + getFreeSpace());
        //System.out.println("Partition size : " + getPartitionListBlockSize());
        System.out.println(Arrays.toString(HD));
        for (int i=1; i<hdCounter; i++){
            PartitionListBlock currentPartitionListBlock = (PartitionListBlock) HD[i];
            if (currentPartitionListBlock != null){
                System.out.println("Partition " + currentPartitionListBlock.partitionLetterCounter + " starts : " + currentPartitionListBlock.startBlock + " ends : " + (currentPartitionListBlock.endBlock - 1));
            }
        }
    }
    
    private PartitionListBlock getLastPartitionListBlock() {
        if (hdCounter <= 1)
            return null;
        return (PartitionListBlock) HD[hdCounter - 1];
    }
    
    private int getFreeSpace(){
        int freeSpace = 0;
        int totalConsumed = 0;
        for (int i=1; i<hdCounter; i++){
            PartitionListBlock currentPartitionListBlock = (PartitionListBlock) HD[i];
            if (currentPartitionListBlock != null)
                totalConsumed = totalConsumed + currentPartitionListBlock.getSize();
        }
        return getRemainingSpaceSize() - totalConsumed;
    }
    
    public int getRemainingSpaceSize(){
        // -1 for MBR
        // -1 for array index numbering
        return (blocks - maxPartitions) - 1 - 1;
    }
    
    private int getPartitionListBlockSize(){
        List<PartitionListBlock> returnPartitionListBlock = new ArrayList<PartitionListBlock>();
        int k=0;
        for (int i=1; i<hdCounter; i++){
            PartitionListBlock currentPartitionListBlock = (PartitionListBlock) HD[i];
            if (currentPartitionListBlock != null)
                k++;
        }
        return k;
    }

    public void formatPartition(char partitionLetter){
        for (int i=1; i<=maxPartitions;i++){
            PartitionListBlock currentPartitionListBlock = (PartitionListBlock) HD[i];
            if (currentPartitionListBlock.partitionLetterCounter == partitionLetter){
                currentPartitionListBlock.format();
                return;
            }
        }
    }
    
    public static void main(String[] args) {
        FileSystem fs = new FileSystem();
        fs.createPartition(15);
        fs.showPartitions();
//        fs.createPartition(2);
//        fs.showPartitions();
//        fs.createPartition(4);
//        fs.showPartitions();
        fs.formatPartition('C');
        System.out.println("Final available blocks " + fs.getFreeSpace());
    }
}
