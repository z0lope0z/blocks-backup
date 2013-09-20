import java.util.Arrays;

public class PartitionListBlock extends Block {
    public char partitionLetterCounter = 'C';
    public int startBlock;
    public int endBlock;
    public Block[] blocks;
    private int partitionSize;

    public PartitionListBlock(PartitionListBlock partitionListBlock, int size){
        this.partitionSize = size;
        this.startBlock = partitionListBlock.endBlock;
        this.endBlock = partitionListBlock.endBlock + size;
        this.buildBlock(size);
        this.partitionLetterCounter = partitionListBlock.partitionLetterCounter;
        partitionLetterCounter++;
    }
    
    public PartitionListBlock(int startAddress, int size){
        this.partitionSize = size;
        this.startBlock = startAddress;
        this.endBlock = startAddress + size - 1;
        this.buildBlock(size);
    }

    public void buildBlock(int size){
        this.blocks = new Block[size];
        this.blocks[0] = new BootBlock();
        this.blocks[1] = new SuperBlock();
        this.blocks[2] = new FreeSpaceBlock();
        this.blocks[3] = new RootDirectoryBlock();
        int numInodes = computeNumInodes(size - 4);
        int currentBlockCount = 4;
        for (int i=0; i<numInodes; i++){
            this.blocks[currentBlockCount] = new INodeBlock();
            currentBlockCount++;
        }
        //set free space offset
        ((FreeSpaceBlock) this.blocks[2]).offset = currentBlockCount;
        // turn the rest into data blocks
        int freeSpaceCount = 0;
        int numRemaining = size-currentBlockCount;
        for (int i=0; i<numRemaining; i++){
            this.blocks[currentBlockCount] = new DataBlock();
            currentBlockCount++;
            freeSpaceCount++;
        }
    }

    public void format(){
//        Formating Partition D
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
//                <continued until 19>
        System.out.println("Formatting Partition " + partitionLetterCounter);
        System.out.println("Partition size: " + partitionSize);
        System.out.println("Available for Inodes and Datablocks " + getAvailableForInodesAndDatablocks());
        System.out.println("Inodes " + getNumInodes());
        System.out.println("Datanodes " + getNumData());
        System.out.println("" + Arrays.toString(blocks));
        for (int i=0;i<partitionSize;i++){
            System.out.println((i + startBlock) + " - " + blocks[i].toString());
        }
    }

    /**
     * returns the block from a given block counter(from entire disk)
     * @param diskBlockIndex
     * @return
     */
    public Block getBlock(int diskBlockIndex){
        return blocks[diskBlockIndex-startBlock];
    }

    private int computeNumInodes(int remainingSize){
        return (remainingSize / 10) + 1;
    }
    
    public int getSize(){
        return endBlock - startBlock;
    }    
    
    private int getAvailableForInodesAndDatablocks(){
        int counter = 0;
        for (int i=0;i < blocks.length;i++){
            if ((blocks[i] instanceof INodeBlock) || (blocks[i] instanceof DataBlock))
                counter++;
        }
        return counter;
    }

    private int getNumInodes(){
        int counter = 0;
        for (int i=0;i < blocks.length;i++){
            if (blocks[i] instanceof INodeBlock)
                counter++;
        }
        return counter;
    }

    private int getNumData(){
        int counter = 0;
        for (int i=0;i < blocks.length;i++){
            if (blocks[i] instanceof DataBlock)
                counter++;
        }
        return counter;
    }
    
    @Override
    public String toString(){
        return "Partition " + partitionLetterCounter;
    }
}
