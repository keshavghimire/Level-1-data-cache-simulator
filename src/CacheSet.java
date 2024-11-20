public class CacheSet {
    public CacheLine[] lines; // Array of cache lines

    public CacheSet(int associativity, int blockWords) {
        lines = new CacheLine[associativity];
        for (int i = 0; i < associativity; i++) {
            lines[i] = new CacheLine(blockWords);
        }
    }

    public CacheLine findLine(int tag) {
        for (CacheLine line : lines) {
            if (line.valid && line.tag == tag) {
                return line;
            }
        }
        return null;
    }

    public CacheLine replaceLine(int tag) {
        CacheLine lruLine = lines[0];
        for (CacheLine line : lines) {
            if (!line.valid) {
                lruLine = line;
                break;
            } else if (line.lruCounter > lruLine.lruCounter) {
                lruLine = line;
            }
        }

        // Handle eviction
        if (lruLine.valid && lruLine.dirty) {
            System.out.println("Evicting dirty line with tag: " + Integer.toHexString(lruLine.tag));
            CacheSim.dirtyEvictions++;
        }

        // Replace the line
        lruLine.valid = true;
        lruLine.dirty = false;
        lruLine.tag = tag;
        lruLine.lruCounter = 0;
        return lruLine;
    }

    public void updateLRUCounters(CacheLine accessedLine) {
        for (CacheLine line : lines) {
            if (line.valid && line != accessedLine) {
                line.lruCounter++;
            }
        }
        accessedLine.lruCounter = 0;
    }
}
