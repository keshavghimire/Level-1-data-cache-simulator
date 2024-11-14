public class CacheLine {
    int tag;             // Tag for the cache line
    boolean valid;       // Valid bit (true if the line is valid)
    boolean dirty;       // Dirty bit (true if the line is modified)
    int[] data;          // Data stored in the cache line

    // Constructor
    public CacheLine(int blockSize) {
        this.tag = 0;
        this.valid = false;   // Initially invalid
        this.dirty = false;   // Initially not dirty
        this.data = new int[blockSize / 4]; // 4 bytes per word (32 bits)
    }
}
