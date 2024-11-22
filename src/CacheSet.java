public class CacheSet {
    public CacheLine[] lines;

    public CacheSet(int associativity, int blockWords) {
        lines = new CacheLine[associativity];
        for (int i = 0; i < associativity; i++) {
            lines[i] = new CacheLine(blockWords, blockWords);  // Pass correct arguments to constructor
        }
    }

    // Find a line based on the tag
    public CacheLine findLine(int tag) {
        for (CacheLine line : lines) {
            if (line.tag == tag && line.valid) {
                return line;
            }
        }
        return null;
    }

    // Replace a cache line using LRU
    public CacheLine replaceLine(int tag) {
        CacheLine lruLine = lines[0];
        for (CacheLine line : lines) {
            if (line.lruCounter > lruLine.lruCounter) {
                lruLine = line;
            }
        }
        lruLine.tag = tag;
        lruLine.valid = true;
        lruLine.lruCounter = 0;  // Reset LRU counter
        return lruLine;
    }
}
