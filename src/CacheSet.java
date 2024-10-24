// CacheSet.java - Represents a set in the cache
public class CacheSet {
    CacheLine[] lines;  // Array of cache lines

    // Constructor
    public CacheSet(int associativity, int blockSize) {
        lines = new CacheLine[associativity];
        for (int i = 0; i < associativity; i++) {
            lines[i] = new CacheLine(blockSize); // Initialize each cache line
        }
    }
}
