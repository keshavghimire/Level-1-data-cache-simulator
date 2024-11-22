public class CacheLine {
    public boolean valid;
    public boolean dirty;
    public int tag;
    public int[] data;
    public int lruCounter; // LRU counter for replacement policy

    public CacheLine(int blockWords, int blockSize) {
        this.data = new int[blockWords]; // Assuming blockWords is the number of words in each block
        this.lruCounter = 0;  // Initialize LRU counter
    }
}
